package com.fuzzy.main.rdao.rocksdb.options.columnfamily;

import org.rocksdb.ColumnFamilyOptions;

import java.util.Objects;

public class ColumnFamilyConfigMapper {

    private ColumnFamilyConfigMapper() {
    }

    public static ColumnFamilyOptions toRocksDbOpt(ColumnFamilyConfig from) {
        Objects.requireNonNull(from);
        ColumnFamilyOptions destination = new ColumnFamilyOptions();
        setRocksDbOpt(from, destination);
        return destination;
    }

    public static ColumnFamilyConfig fromRocksDbOpt(ColumnFamilyOptions from) {
        Objects.requireNonNull(from);
        return ColumnFamilyConfig.newBuilder()
                .withArenaBlockSize(from.arenaBlockSize())
                .withWriteBufferSize(from.writeBufferSize())
                .withMaxWriteBufferNumber(from.maxWriteBufferNumber())
                .withMinWriteBufferNumberToMerge(from.minWriteBufferNumberToMerge())
                .withNumLevels(from.numLevels())
                .withTargetFileSizeBase(from.targetFileSizeBase())
                .withMaxBytesForLevelBase(from.maxBytesForLevelBase())
                .withEnableLevelCompactionDynamicLevelBytes(from.levelCompactionDynamicLevelBytes())
                .withMaxCompactionBytes(from.maxCompactionBytes())
                .withArenaBlockSize(from.arenaBlockSize())
                .withDisableAutoCompactions(from.disableAutoCompactions())
                .withMaxSequentialSkipInIterations(from.maxSequentialSkipInIterations())
                .withMemtablePrefixBloomSizeRatio(from.memtablePrefixBloomSizeRatio())
                .withMaxSuccessiveMerges(from.maxSuccessiveMerges())
                .withSoftPendingCompactionBytesLimit(from.softPendingCompactionBytesLimit())
                .withLevel0FileNumCompactionTrigger(from.level0FileNumCompactionTrigger())
                .withLevel0StopWritesTrigger(from.level0StopWritesTrigger())
                .withMaxWriteBufferNumberToMaintain(from.maxWriteBufferNumberToMaintain())
                .build();
    }

    public static void setRocksDbOpt(ColumnFamilyConfig from, ColumnFamilyOptions to) {
        Objects.requireNonNull(from, "column family config cannot be null");
        Objects.requireNonNull(to);
        if (from.isContainArenaBlockSize()) {
            to.setArenaBlockSize(from.getArenaBlockSize());
        }
        if (from.isContainWriteBufferSize()) {
            to.setWriteBufferSize(from.getWriteBufferSize());
        }
        if (from.isContainMaxWriteBufferNumber()) {
            to.setMaxWriteBufferNumber(from.getMaxWriteBufferNumber());
        }
        if (from.isContainMinWriteBufferNumberToMerge()) {
            to.setMinWriteBufferNumberToMerge(from.getMinWriteBufferNumberToMerge());
        }
        if (from.isContainNumLevels()) {
            to.setNumLevels(from.getNumLevels());
        }
        if (from.isContainTargetFileSizeBase()) {
            to.setTargetFileSizeBase(from.getTargetFileSizeBase());
        }
        if (from.isContainMaxBytesForLevelBase()) {
            to.setMaxBytesForLevelBase(from.getMaxBytesForLevelBase());
        }
        if (from.isContainEnableLevelCompactionDynamicLevelBytes()) {
            to.setLevelCompactionDynamicLevelBytes(from.getEnableLevelCompactionDynamicLevelBytes());
        }
        if (from.isContainMaxCompactionBytes()) {
            to.setMaxCompactionBytes(from.getMaxCompactionBytes());
        }
        if (from.isContainArenaBlockSize()) {
            to.setArenaBlockSize(from.getArenaBlockSize());
        }
        if (from.isContainDisableAutoCompactions()) {
            to.setDisableAutoCompactions(from.getDisableAutoCompactions());
        }
        if (from.isContainMaxSequentialSkipInIterations()) {
            to.setMaxSequentialSkipInIterations(from.getMaxSequentialSkipInIterations());
        }
        if (from.isContainMemtablePrefixBloomSizeRatio()) {
            to.setMemtablePrefixBloomSizeRatio(from.getMemtablePrefixBloomSizeRatio());
        }
        if (from.isContainMaxSuccessiveMerges()) {
            to.setMaxSuccessiveMerges(from.getMaxSuccessiveMerges());
        }
        if (from.isContainSoftPendingCompactionBytesLimit()) {
            to.setSoftPendingCompactionBytesLimit(from.getSoftPendingCompactionBytesLimit());
        }
        if (from.isContainLevel0FileNumCompactionTrigger()) {
            to.setLevel0FileNumCompactionTrigger(from.getLevel0FileNumCompactionTrigger());
        }
        if (from.isContainLevel0StopWritesTrigger()) {
            to.setLevel0StopWritesTrigger(from.getLevel0StopWritesTrigger());
        }
        if (from.isContainMaxWriteBufferNumberToMaintain()) {
            to.setMaxWriteBufferNumberToMaintain(from.getMaxWriteBufferNumberToMaintain());
        }
    }
}