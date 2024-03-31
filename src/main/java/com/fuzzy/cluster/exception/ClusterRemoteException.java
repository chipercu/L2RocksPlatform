package com.fuzzy.cluster.exception;

import com.fuzzy.cluster.exception.ClusterException;

/**
 * Created by user on 06.09.2017.
 */
public class ClusterRemoteException extends ClusterException {

    public ClusterRemoteException() {
    }

    public ClusterRemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterRemoteException(Throwable cause) {
        super(cause);
    }

}
