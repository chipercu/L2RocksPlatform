package com.fuzzy.subsystem.extensions.network;

import l2open.config.ConfigValue;
import l2open.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MMOConnection<T extends MMOClient> implements Lockable
{
	private final SelectorThread<T> _selectorThread;

	private final SelectionKey _selectionKey;
	private final MMOSocket _socket;
	private final WritableByteChannel _writableByteChannel;
	private final ReadableByteChannel _readableByteChannel;

	private final Queue<SendablePacket<T>> _sendQueue;
	private final Queue<ReceivablePacket<T>> _recvQueue;

	private T _client;
	private ByteBuffer _readBuffer, _primaryWriteBuffer, _secondaryWriteBuffer;

	private long _connectionOpenTime;

	private boolean _pendingClose;
	private long _pendingCloseTime;
	private boolean _closed;

	private long _pendingWriteTime;
	private final AtomicBoolean _isPengingWrite = new AtomicBoolean();

	private final Lock _lock = new ReentrantLock();

	public MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey key, byte[] proxy_ip)
	{
		_selectorThread = selectorThread;
		_selectionKey = key;
		_socket = new MMOSocket(socket, proxy_ip);
		_writableByteChannel = socket.getChannel();
		_readableByteChannel = socket.getChannel();
		_sendQueue = new ArrayDeque<SendablePacket<T>>();
		_recvQueue = new MMOExecutableQueue<T>(selectorThread.getExecutor());
		_connectionOpenTime = System.currentTimeMillis();
	}

	public MMOConnection(SelectorThread<T> selectorThread)
	{
		_selectorThread = selectorThread;
		_selectionKey = null;
		_socket = null;
		_writableByteChannel = null;
		_readableByteChannel = null;
		_sendQueue = new ArrayDeque<SendablePacket<T>>();
		_recvQueue = new MMOExecutableQueue<T>(null);
		_connectionOpenTime = System.currentTimeMillis();
	}

	@Override
	public void lock()
	{
		_lock.lock();
	}

	@Override
	public void unlock()
	{
		_lock.unlock();
	}

	protected long getConnectionOpenTime()
	{
		return _connectionOpenTime;
	}

	protected void setClient(T client)
	{
		_client = client;
	}

	public T getClient()
	{
		return _client;
	}

	public void recvPacket(ReceivablePacket<T> rp)
	{
		if(rp == null)
			return;

		if(isClosed())
			return;

		_recvQueue.add(rp);
	}

	public void sendPacket(SendablePacket<T> sp)
	{
		if(sp == null)
			return;

		lock();
		try
		{
			if(isClosed())
				return;

			_sendQueue.add(sp);
		}
		finally
		{
			unlock();
		}

		scheduleWriteInterest();
	}

	public void sendPacket(SendablePacket<T>[] args)
	{
		if(args == null || args.length == 0)
			return;

		lock();
		try
		{
			if(isClosed())
				return;

			for(SendablePacket<T> sp : args)
				if(sp != null)
					_sendQueue.add(sp);
		}
		finally
		{
			unlock();
		}

		scheduleWriteInterest();
	}

	public void sendPackets(Collection<? extends SendablePacket<T>> args)
	{
		if(args == null || args.isEmpty())
			return;

		//SendablePacket<T> sp;

		lock();
		try
		{
			if(isClosed())
				return;

			for(SendablePacket<T> sp : args)
				_sendQueue.add(sp);
			//for(int i = 0; i < args.size(); i++)
			//	if((sp = args.get(i)) != null)
			//		_sendQueue.add(sp);
		}
		finally
		{
			unlock();
		}

		scheduleWriteInterest();
	}

	protected SelectionKey getSelectionKey()
	{
		return _selectionKey;
	}

	/**
	 * Немедленно выключает интересуемое действие OP_READ
	 */
	protected void disableReadInterest()
	{
		if(_selectionKey != null)
			try
			{
				_selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_READ);
			}
			catch(CancelledKeyException e)
			{
				// ignore
			}
	}

	/**
	 * Планирует интересуемое действие OP_WRITE
	 */
	protected void scheduleWriteInterest()
	{
		if (_isPengingWrite.compareAndSet(false, true))
			_pendingWriteTime = System.currentTimeMillis();
	}

	/**
	 * Включает интересуемое действие OP_WRITE, если запланировано
	 */
	protected void enableWriteInterest()
	{
		try
		{
			if(_isPengingWrite.compareAndSet(true, false))
				_selectionKey.interestOps(_selectionKey.interestOps() | SelectionKey.OP_WRITE);
		}
		catch(CancelledKeyException e)
		{
			// ignore
		}
	}

	/**
	 * Немедленно выключает интересуемое действие OP_WRITE
	 */
	protected void disableWriteInterest()
	{
		if(_selectionKey != null)
			try
			{
				_selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
			}
			catch(CancelledKeyException e)
			{
				// ignore
			}
	}

	protected boolean isPendingWrite()
	{
		return _isPengingWrite.get();
	}

	protected long getPendingWriteTime()
	{
		return _pendingWriteTime;
	}

	public MMOSocket getSocket()
	{
		return _socket;
	}

	protected WritableByteChannel getWritableChannel()
	{
		return _writableByteChannel;
	}

	protected ReadableByteChannel getReadableByteChannel()
	{
		return _readableByteChannel;
	}

	protected Queue<SendablePacket<T>> getSendQueue()
	{
		return _sendQueue;
	}

	protected Queue<ReceivablePacket<T>> getRecvQueue()
	{
		return _recvQueue;
	}

	protected void createWriteBuffer(ByteBuffer buf)
	{
		if(_primaryWriteBuffer == null)
		{
			_primaryWriteBuffer = _selectorThread.getPooledBuffer();
			_primaryWriteBuffer.put(buf);
		}
		else
		{
			ByteBuffer temp = _selectorThread.getPooledBuffer();
			temp.put(buf);

			int remaining = temp.remaining();
			_primaryWriteBuffer.flip();
			int limit = _primaryWriteBuffer.limit();

			if(remaining >= _primaryWriteBuffer.remaining())
			{
				temp.put(_primaryWriteBuffer);
				_selectorThread.recycleBuffer(_primaryWriteBuffer);
				_primaryWriteBuffer = temp;
			}
			else
			{
				_primaryWriteBuffer.limit(remaining);
				temp.put(_primaryWriteBuffer);
				_primaryWriteBuffer.limit(limit);
				_primaryWriteBuffer.compact();
				_secondaryWriteBuffer = _primaryWriteBuffer;
				_primaryWriteBuffer = temp;
			}
		}
	}

	protected boolean hasPendingWriteBuffer()
	{
		return _primaryWriteBuffer != null;
	}

	protected void movePendingWriteBufferTo(ByteBuffer dest)
	{
		_primaryWriteBuffer.flip();
		dest.put(_primaryWriteBuffer);
		_selectorThread.recycleBuffer(_primaryWriteBuffer);
		_primaryWriteBuffer = _secondaryWriteBuffer;
		_secondaryWriteBuffer = null;
	}

	protected void setReadBuffer(ByteBuffer buf)
	{
		_readBuffer = buf;
	}

	protected ByteBuffer getReadBuffer()
	{
		return _readBuffer;
	}

	public boolean isClosed()
	{
		return _pendingClose || _closed;
	}

	protected boolean isPengingClose()
	{
		return _pendingClose;
	}

	protected long getPendingCloseTime()
	{
		return _pendingCloseTime;
	}

	protected void close() throws IOException
	{
		_closed = true;
		_socket.close();
	}

	protected void closeNow()
	{
		lock();
		try
		{
			if(isClosed())
				return;

			_sendQueue.clear();

			if(ConfigValue.DubugTraceCloseConnection)
			{
				String ip = "err";
				try
				{
					ip = getSocket().getInetAddress().getHostAddress();
				}
				catch(Exception e)
				{}
				Log.logTrace("["+ip+"]", "selector_thread", "close_now");
			}
			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}
		finally
		{
			unlock();
		}

		disableReadInterest();
		disableWriteInterest();
	}

	public void close(SendablePacket<T> sp)
	{
		lock();
		try
		{
			if(isClosed())
				return;

			_sendQueue.clear();

			sendPacket(sp);

			if(ConfigValue.DubugTraceCloseConnection)
			{
				String ip = "err";
				try
				{
					ip = getSocket().getInetAddress().getHostAddress();
				}
				catch(Exception e)
				{}
				Log.logTrace("["+ip+"]", "selector_thread", "close_send");
			}
			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}
		finally
		{
			unlock();
		}

		disableReadInterest();
	}

	protected void closeLater()
	{
		lock();
		try
		{
			if(isClosed())
				return;

			if(ConfigValue.DubugTraceCloseConnection)
			{
				String ip = "err";
				try
				{
					ip = getSocket().getInetAddress().getHostAddress();
				}
				catch(Exception e)
				{}
				Log.logTrace("["+ip+"]", "selector_thread", "close_later");
			}
			_pendingClose = true;
			_pendingCloseTime = System.currentTimeMillis();
		}
		finally
		{
			unlock();
		}
	}

	protected void releaseBuffers()
	{
		if(_primaryWriteBuffer != null)
		{
			_selectorThread.recycleBuffer(_primaryWriteBuffer);
			_primaryWriteBuffer = null;
			if(_secondaryWriteBuffer != null)
			{
				_selectorThread.recycleBuffer(_secondaryWriteBuffer);
				_secondaryWriteBuffer = null;
			}
		}
		if(_readBuffer != null)
		{
			_selectorThread.recycleBuffer(_readBuffer);
			_readBuffer = null;
		}
	}

	protected void clearQueues()
	{
		lock();
		try
		{
			_sendQueue.clear();
			_recvQueue.clear();
		}
		finally
		{
			unlock();
		}
	}

	protected void onDisconnection()
	{
		//l2open.util.Util.test();
		getClient().onDisconnection();
	}

	protected void onForcedDisconnection()
	{
		//l2open.util.Util.test();
		getClient().onForcedDisconnection();
	}

	@Override
	public String toString()
	{
		return "MMOConnection: selector=" + _selectorThread + "; client=" + getClient();
	}
}