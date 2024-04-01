package com.fuzzy.subsystem.extensions.network;

import com.fuzzy.subsystem.gameserver.SelectorHelperGS;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection<?>> {
    public boolean can_runImpl = true; // TODO

    private T _connection;
    private boolean isAuthed;
    public SelectorHelperGS.ClientInfo _c_info;

    public MMOClient(T con) {
        _connection = con;
    }

    protected void setConnection(T con) {
        _connection = con;
    }

    public T getConnection() {
        return _connection;
    }

    public boolean isAuthed() {
        return isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public void closeNow(boolean error) {
        final T conn = _connection;
        if (conn != null && !conn.isClosed())
            conn.closeNow();
    }

    public void closeLater() {
        final T conn = _connection;
        if (conn != null && !conn.isClosed())
            conn.closeLater();
    }

    public boolean isConnected() {
        final T conn = _connection;
        return conn != null && !conn.isClosed();
    }

    public abstract boolean decrypt(ByteBuffer buf, int size);

    public abstract boolean encrypt(ByteBuffer buf, int size);

    protected void onDisconnection() {
    }

    protected void onForcedDisconnection() {
    }
}