package com.fuzzy.main.entityprovidersdk.serialization.deserializer;


import com.fuzzy.main.entityprovidersdk.exception.runtime.DeserializerException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;

public class CompactStreamDecoder {

    static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8L;
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private InputStreamByteSource source = null;
    private byte[] buf = null;
    private int minPos = 0;
    private int pos = 0;
    private int limit = 0;


    public CompactStreamDecoder(InputStream stream) {
        configureSource(new InputStreamByteSource(stream));
    }

    private void configureSource(InputStreamByteSource source) {
        if (null != this.source) {
            this.source.detach();
        }
        source.attach(DEFAULT_BUFFER_SIZE, this);
        this.source = source;
    }


    public ArrayList<Integer> readIntegerCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<Integer> integers = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    integers.add(readNullableInt());
                }
            } while ((leght = arrayNext()) > 0);
            return integers;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Long> readLongCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }

        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<Long> result = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    result.add(readNullableLong());
                }
            } while ((leght = arrayNext()) > 0);
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Double> readDoubleCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<Double> result = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    result.add(readNullableDouble());
                }
            } while ((leght = arrayNext()) > 0);
            return result;
        } else {
            return new ArrayList<>();
        }
    }


    public byte[] readByteArray() throws IOException {
        int leght = readInt();
        if (leght > 0) {
            final byte[] bytes = new byte[leght];
            readFixed(bytes, 0, leght);
            return bytes;
        } else {
            return new byte[0];
        }
    }


    public ArrayList<Boolean> readBooleanCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<Boolean> result = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    result.add(readNullableBoolean());
                }
            } while ((leght = arrayNext()) > 0);
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Instant> readInstantCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<Instant> result = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    result.add(readNullableInstant());
                }
            } while ((leght = arrayNext()) > 0);
            return result;
        } else {
            return new ArrayList<>();
        }
    }


    public ArrayList<String> readStringCollection() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        long leght = readArrayStart();
        if (leght > 0) {
            final ArrayList<String> result = new ArrayList<>();
            do {
                for (long i = 0; i < leght; i++) {
                    result.add(readNullableString());
                }
            } while ((leght = arrayNext()) > 0);
            return result;
        } else {
            return new ArrayList<>();
        }
    }


    public void readFixed(byte[] bytes) throws IOException {
        readFixed(bytes, 0, bytes.length);
    }


    public String readString() throws IOException {
        int length = (int) readLong();
        if (length > MAX_ARRAY_SIZE) {
            throw new UnsupportedOperationException("Cannot read strings longer than " + MAX_ARRAY_SIZE + " bytes");
        }
        if (length < 0L) {
            throw new DeserializerException("Malformed data. Length is negative: " + length);
        }

        final ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        if (0L != length) {
            doReadBytes(byteBuffer.array(), 0, length);
        }
        return new String(byteBuffer.array(), 0, length, StandardCharsets.UTF_8);
    }


    static class BufferAccessor {
        private final CompactStreamDecoder decoder;
        private byte[] buf;
        private int pos;
        private int limit;
        boolean detached = false;

        private BufferAccessor(CompactStreamDecoder decoder) {
            this.decoder = decoder;
        }

        void detach() {
            this.buf = decoder.buf;
            this.pos = decoder.pos;
            this.limit = decoder.limit;
            detached = true;
        }

        int getPos() {
            if (detached)
                return this.pos;
            else
                return decoder.pos;
        }

        int getLim() {
            if (detached)
                return this.limit;
            else
                return decoder.limit;
        }

        byte[] getBuf() {
            if (detached)
                return this.buf;
            else
                return decoder.buf;
        }

        void setPos(int pos) {
            if (detached)
                this.pos = pos;
            else
                decoder.pos = pos;
        }

        void setLimit(int limit) {
            if (detached)
                this.limit = limit;
            else
                decoder.limit = limit;
        }
    }


    static class InputStreamByteSource extends InputStream {

        protected BufferAccessor bufferAccessor;

        private final InputStream in;
        protected boolean isEof = false;

        protected InputStreamByteSource(InputStream stream) {
            this.in = stream;
        }

        public boolean isEof() {
            return isEof;
        }

        protected void attach(int bufferSize, CompactStreamDecoder decoder) {
            decoder.buf = new byte[bufferSize];
            decoder.pos = 0;
            decoder.minPos = 0;
            decoder.limit = 0;
            this.bufferAccessor = new BufferAccessor(decoder);
        }

        protected void detach() {
            bufferAccessor.detach();
        }


        protected void readRaw(byte[] data, int off, int len) throws IOException {
            while (len > 0) {
                int read = in.read(data, off, len);
                if (read < 0) {
                    isEof = true;
                    throw new EOFException();
                }
                len -= read;
                off += read;
            }
        }

        protected int tryReadRaw(byte[] data, int off, int len) throws IOException {
            int leftToCopy = len;
            try {
                while (leftToCopy > 0) {
                    int read = in.read(data, off, leftToCopy);
                    if (read < 0) {
                        isEof = true;
                        break;
                    }
                    leftToCopy -= read;
                    off += read;
                }
            } catch (EOFException eof) {
                isEof = true;
            }
            return len - leftToCopy;
        }

        protected void compactAndFill(byte[] buf, int pos, int minPos, int remaining) throws IOException {
            System.arraycopy(buf, pos, buf, minPos, remaining);
            bufferAccessor.setPos(minPos);
            int newLimit = remaining + tryReadRaw(buf, minPos + remaining, buf.length - remaining);
            bufferAccessor.setLimit(newLimit);
        }

        @Override
        public int read() throws IOException {
            if (bufferAccessor.getLim() - bufferAccessor.getPos() == 0) {
                return in.read();
            } else {
                int position = bufferAccessor.getPos();
                int result = bufferAccessor.getBuf()[position] & 0xff;
                bufferAccessor.setPos(position + 1);
                return result;
            }
        }

        @Override
        public int available() {
            return (bufferAccessor.getLim() - bufferAccessor.getPos());
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    public Boolean readNullableBoolean() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return readBoolean();
    }


    public Instant readNullableInstant() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return Instant.ofEpochMilli(readLong());
    }


    public boolean readBoolean() throws IOException {
        if (limit == pos) {
            limit = source.tryReadRaw(buf, 0, buf.length);
            pos = 0;
            if (limit == 0) {
                throw new EOFException();
            }
        }
        int n = buf[pos++] & 0xff;
        return n == 1;
    }


    public int readInt() throws IOException {
        ensureBounds(5);
        int len = 1;
        int b = buf[pos] & 0xff;
        int n = b & 0x7f;
        if (b > 0x7f) {
            b = buf[pos + len++] & 0xff;
            n ^= (b & 0x7f) << 7;
            if (b > 0x7f) {
                b = buf[pos + len++] & 0xff;
                n ^= (b & 0x7f) << 14;
                if (b > 0x7f) {
                    b = buf[pos + len++] & 0xff;
                    n ^= (b & 0x7f) << 21;
                    if (b > 0x7f) {
                        b = buf[pos + len++] & 0xff;
                        n ^= (b & 0x7f) << 28;
                        if (b > 0x7f) {
                            throw new DeserializerException("Invalid int encoding");
                        }
                    }
                }
            }
        }
        pos += len;
        if (pos > limit) {
            throw new EOFException();
        }
        return (n >>> 1) ^ -(n & 1);
    }

    public Integer readNullableInt() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return readInt();
    }


    public String readNullableString() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return readString();
    }


    public long readLong() throws IOException {
        ensureBounds(10);
        int b = buf[pos++] & 0xff;
        int n = b & 0x7f;
        long l;
        if (b > 0x7f) {
            b = buf[pos++] & 0xff;
            n ^= (b & 0x7f) << 7;
            if (b > 0x7f) {
                b = buf[pos++] & 0xff;
                n ^= (b & 0x7f) << 14;
                if (b > 0x7f) {
                    b = buf[pos++] & 0xff;
                    n ^= (b & 0x7f) << 21;
                    if (b > 0x7f) {
                        l = innerLongDecode(n);
                    } else {
                        l = n;
                    }
                } else {
                    l = n;
                }
            } else {
                l = n;
            }
        } else {
            l = n;
        }
        if (pos > limit) {
            throw new EOFException();
        }
        return (l >>> 1) ^ -(l & 1);
    }

    public Long readNullableLong() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return readLong();
    }


    private long innerLongDecode(long l) {
        int len = 1;
        int b = buf[pos] & 0xff;
        l ^= (b & 0x7fL) << 28;
        if (b > 0x7f) {
            b = buf[pos + len++] & 0xff;
            l ^= (b & 0x7fL) << 35;
            if (b > 0x7f) {
                b = buf[pos + len++] & 0xff;
                l ^= (b & 0x7fL) << 42;
                if (b > 0x7f) {
                    b = buf[pos + len++] & 0xff;
                    l ^= (b & 0x7fL) << 49;
                    if (b > 0x7f) {
                        b = buf[pos + len++] & 0xff;
                        l ^= (b & 0x7fL) << 56;
                        if (b > 0x7f) {
                            b = buf[pos + len++] & 0xff;
                            l ^= (b & 0x7fL) << 63;
                            if (b > 0x7f) {
                                throw new DeserializerException("Invalid long encoding");
                            }
                        }
                    }
                }
            }
        }
        pos += len;
        return l;
    }


    public double readDouble() throws IOException {
        ensureBounds(8);
        int len = 1;
        int n1 = (buf[pos] & 0xff) | ((buf[pos + len++] & 0xff) << 8) | ((buf[pos + len++] & 0xff) << 16)
                | ((buf[pos + len++] & 0xff) << 24);
        int n2 = (buf[pos + len++] & 0xff) | ((buf[pos + len++] & 0xff) << 8) | ((buf[pos + len++] & 0xff) << 16)
                | ((buf[pos + len++] & 0xff) << 24);
        if ((pos + 8) > limit) {
            throw new EOFException();
        }
        pos += 8;
        return Double.longBitsToDouble((((long) n1) & 0xffffffffL) | (((long) n2) << 32));
    }

    public Double readNullableDouble() throws IOException {
        final boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        return readDouble();
    }

    public void readFixed(byte[] bytes, int start, int length) throws IOException {
        doReadBytes(bytes, start, length);
    }

    protected void doReadBytes(byte[] bytes, int start, int length) throws IOException {
        if (length < 0)
            throw new DeserializerException("Malformed data. Length is negative: " + length);
        int remaining = limit - pos;
        if (length <= remaining) {
            System.arraycopy(buf, pos, bytes, start, length);
            pos += length;
        } else {
            System.arraycopy(buf, pos, bytes, start, remaining);
            start += remaining;
            length -= remaining;
            pos = limit;
            source.readRaw(bytes, start, length);
        }
    }


    protected long doReadItemCount() throws IOException {
        long result = readLong();
        if (result < 0L) {
            readLong();
            result = -result;
        }
        return result;
    }


    public long readArrayStart() throws IOException {
        return doReadItemCount();
    }


    public long arrayNext() throws IOException {
        return doReadItemCount();
    }


    private void ensureBounds(int num) throws IOException {
        int remaining = limit - pos;
        if (remaining < num) {
            source.compactAndFill(buf, pos, minPos, remaining);
            if (pos >= limit)
                throw new EOFException();
        }
    }
}