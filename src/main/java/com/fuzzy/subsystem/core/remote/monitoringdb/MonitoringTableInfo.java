package com.fuzzy.subsystem.core.remote.monitoringdb;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

import java.util.Objects;

public class MonitoringTableInfo implements RemoteObject {

    private final String storageGuid;
    private final String clusterName;
    private final String databaseName;
    private final String tableName;


    private MonitoringTableInfo(Builder builder) {
        storageGuid = builder.storageGuid;
        clusterName = builder.clusterName;
        databaseName = builder.databaseName;
        tableName = builder.tableName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getStorageGuid() {
        return storageGuid;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonitoringTableInfo that = (MonitoringTableInfo) o;

        if (!Objects.equals(storageGuid, that.storageGuid)) return false;
        if (!Objects.equals(clusterName, that.clusterName)) return false;
        if (!Objects.equals(databaseName, that.databaseName)) return false;
        return Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        int result = storageGuid != null ? storageGuid.hashCode() : 0;
        result = 31 * result + (clusterName != null ? clusterName.hashCode() : 0);
        result = 31 * result + (databaseName != null ? databaseName.hashCode() : 0);
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private String storageGuid;
        private String clusterName;
        private String databaseName;
        private String tableName;

        private Builder() {
        }

        public Builder withStorageGuid(String storageGuid) {
            this.storageGuid = storageGuid;
            return this;
        }

        public Builder withClusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder withDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public MonitoringTableInfo build() {
            return new MonitoringTableInfo(this);
        }
    }
}
