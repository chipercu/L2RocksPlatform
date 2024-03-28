package com.fuzzy.main.rdao.rocksdb.options.columnfamily;

import java.io.Serializable;
import java.util.Objects;

public class ColumnFamilyConfig implements Serializable {

    final Long writeBufferSize;
    final Integer maxWriteBufferNumber;
    final Integer minWriteBufferNumberToMerge;
    final Integer numLevels;
    final Long targetFileSizeBase;
    final Long maxBytesForLevelBase;
    final Boolean enableLevelCompactionDynamicLevelBytes;
    final Long maxCompactionBytes;
    final Long arenaBlockSize;
    final Boolean disableAutoCompactions;
    final Long maxSequentialSkipInIterations;
    final Double memtablePrefixBloomSizeRatio;
    final Long maxSuccessiveMerges;
    final Long softPendingCompactionBytesLimit;
    final Integer level0FileNumCompactionTrigger;
    final Integer level0StopWritesTrigger;
    final Integer maxWriteBufferNumberToMaintain;

    private ColumnFamilyConfig(Builder builder) {
        writeBufferSize = builder.writeBufferSize;
        maxWriteBufferNumber = builder.maxWriteBufferNumber;
        minWriteBufferNumberToMerge = builder.minWriteBufferNumberToMerge;
        numLevels = builder.numLevels;
        targetFileSizeBase = builder.targetFileSizeBase;
        maxBytesForLevelBase = builder.maxBytesForLevelBase;
        enableLevelCompactionDynamicLevelBytes = builder.enableLevelCompactionDynamicLevelBytes;
        maxCompactionBytes = builder.maxCompactionBytes;
        arenaBlockSize = builder.arenaBlockSize;
        disableAutoCompactions = builder.disableAutoCompactions;
        maxSequentialSkipInIterations = builder.maxSequentialSkipInIterations;
        memtablePrefixBloomSizeRatio = builder.memtablePrefixBloomSizeRatio;
        maxSuccessiveMerges = builder.maxSuccessiveMerges;
        softPendingCompactionBytesLimit = builder.softPendingCompactionBytesLimit;
        level0FileNumCompactionTrigger = builder.level0FileNumCompactionTrigger;
        level0StopWritesTrigger = builder.level0StopWritesTrigger;
        maxWriteBufferNumberToMaintain = builder.maxWriteBufferNumberToMaintain;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public Long getWriteBufferSize() {
        return writeBufferSize;
    }

    public Integer getMaxWriteBufferNumber() {
        return maxWriteBufferNumber;
    }

    public Integer getMinWriteBufferNumberToMerge() {
        return minWriteBufferNumberToMerge;
    }

    public Integer getNumLevels() {
        return numLevels;
    }

    public Long getTargetFileSizeBase() {
        return targetFileSizeBase;
    }

    public Long getMaxBytesForLevelBase() {
        return maxBytesForLevelBase;
    }

    public Boolean getEnableLevelCompactionDynamicLevelBytes() {
        return enableLevelCompactionDynamicLevelBytes;
    }

    public Long getMaxCompactionBytes() {
        return maxCompactionBytes;
    }

    public Long getArenaBlockSize() {
        return arenaBlockSize;
    }

    public Boolean getDisableAutoCompactions() {
        return disableAutoCompactions;
    }

    public Long getMaxSequentialSkipInIterations() {
        return maxSequentialSkipInIterations;
    }

    public Double getMemtablePrefixBloomSizeRatio() {
        return memtablePrefixBloomSizeRatio;
    }

    public Long getMaxSuccessiveMerges() {
        return maxSuccessiveMerges;
    }

    public Long getSoftPendingCompactionBytesLimit() {
        return softPendingCompactionBytesLimit;
    }

    public Integer getLevel0FileNumCompactionTrigger() {
        return level0FileNumCompactionTrigger;
    }

    public Integer getLevel0StopWritesTrigger() {
        return level0StopWritesTrigger;
    }

    public Integer getMaxWriteBufferNumberToMaintain() {
        return maxWriteBufferNumberToMaintain;
    }

    public Boolean isContainWriteBufferSize() {
        return Objects.nonNull(writeBufferSize);
    }

    public Boolean isContainMaxWriteBufferNumber() {
        return Objects.nonNull(maxWriteBufferNumber);
    }

    public Boolean isContainMinWriteBufferNumberToMerge() {
        return Objects.nonNull(minWriteBufferNumberToMerge);
    }

    public Boolean isContainNumLevels() {
        return Objects.nonNull(numLevels);
    }

    public Boolean isContainTargetFileSizeBase() {
        return Objects.nonNull(targetFileSizeBase);
    }

    public Boolean isContainMaxBytesForLevelBase() {
        return Objects.nonNull(maxBytesForLevelBase);
    }

    public Boolean isContainEnableLevelCompactionDynamicLevelBytes() {
        return Objects.nonNull(enableLevelCompactionDynamicLevelBytes);
    }

    public Boolean isContainMaxCompactionBytes() {
        return Objects.nonNull(maxCompactionBytes);
    }

    public Boolean isContainArenaBlockSize() {
        return Objects.nonNull(arenaBlockSize);
    }

    public Boolean isContainDisableAutoCompactions() {
        return Objects.nonNull(disableAutoCompactions);
    }

    public Boolean isContainMaxSequentialSkipInIterations() {
        return Objects.nonNull(maxSequentialSkipInIterations);
    }

    public Boolean isContainMemtablePrefixBloomSizeRatio() {
        return Objects.nonNull(memtablePrefixBloomSizeRatio);
    }

    public Boolean isContainMaxSuccessiveMerges() {
        return Objects.nonNull(maxSuccessiveMerges);
    }

    public Boolean isContainSoftPendingCompactionBytesLimit() {
        return Objects.nonNull(softPendingCompactionBytesLimit);
    }

    public Boolean isContainLevel0FileNumCompactionTrigger() {
        return Objects.nonNull(level0FileNumCompactionTrigger);
    }

    public Boolean isContainLevel0StopWritesTrigger() {
        return Objects.nonNull(level0StopWritesTrigger);
    }

    public Boolean isContainMaxWriteBufferNumberToMaintain() {
        return Objects.nonNull(maxWriteBufferNumberToMaintain);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnFamilyConfig that = (ColumnFamilyConfig) o;
        return Objects.equals(writeBufferSize, that.writeBufferSize)
                && Objects.equals(maxWriteBufferNumber, that.maxWriteBufferNumber)
                && Objects.equals(minWriteBufferNumberToMerge, that.minWriteBufferNumberToMerge)
                && Objects.equals(numLevels, that.numLevels)
                && Objects.equals(targetFileSizeBase, that.targetFileSizeBase)
                && Objects.equals(maxBytesForLevelBase, that.maxBytesForLevelBase)
                && Objects.equals(enableLevelCompactionDynamicLevelBytes, that.enableLevelCompactionDynamicLevelBytes)
                && Objects.equals(maxCompactionBytes, that.maxCompactionBytes)
                && Objects.equals(arenaBlockSize, that.arenaBlockSize)
                && Objects.equals(disableAutoCompactions, that.disableAutoCompactions)
                && Objects.equals(maxSequentialSkipInIterations, that.maxSequentialSkipInIterations)
                && Objects.equals(memtablePrefixBloomSizeRatio, that.memtablePrefixBloomSizeRatio)
                && Objects.equals(maxSuccessiveMerges, that.maxSuccessiveMerges)
                && Objects.equals(softPendingCompactionBytesLimit, that.softPendingCompactionBytesLimit)
                && Objects.equals(level0FileNumCompactionTrigger, that.level0FileNumCompactionTrigger)
                && Objects.equals(level0StopWritesTrigger, that.level0StopWritesTrigger)
                && Objects.equals(maxWriteBufferNumberToMaintain, that.maxWriteBufferNumberToMaintain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(writeBufferSize,
                maxWriteBufferNumber,
                minWriteBufferNumberToMerge,
                numLevels,
                targetFileSizeBase,
                maxBytesForLevelBase,
                enableLevelCompactionDynamicLevelBytes,
                maxCompactionBytes,
                arenaBlockSize,
                disableAutoCompactions,
                maxSequentialSkipInIterations,
                memtablePrefixBloomSizeRatio,
                maxSuccessiveMerges,
                softPendingCompactionBytesLimit,
                level0FileNumCompactionTrigger,
                level0StopWritesTrigger,
                maxWriteBufferNumberToMaintain);
    }

    public static final class Builder {
        private Long writeBufferSize;
        private Integer maxWriteBufferNumber;
        private Integer minWriteBufferNumberToMerge;
        private Integer numLevels;
        private Long targetFileSizeBase;
        private Long maxBytesForLevelBase;
        private Boolean enableLevelCompactionDynamicLevelBytes;
        private Long maxCompactionBytes;
        private Long arenaBlockSize;
        private Boolean disableAutoCompactions;
        private Long maxSequentialSkipInIterations;
        private Double memtablePrefixBloomSizeRatio;
        private Long maxSuccessiveMerges;
        private Long softPendingCompactionBytesLimit;
        private Integer level0FileNumCompactionTrigger;
        private Integer level0StopWritesTrigger;
        private Integer maxWriteBufferNumberToMaintain;

        private Builder() {
        }

        public Builder withWriteBufferSize(Long writeBufferSize) {
            this.writeBufferSize = writeBufferSize;
            return this;
        }

        public Builder withMaxWriteBufferNumber(Integer maxWriteBufferNumber) {
            this.maxWriteBufferNumber = maxWriteBufferNumber;
            return this;
        }

        public Builder withMinWriteBufferNumberToMerge(Integer minWriteBufferNumberToMerge) {
            this.minWriteBufferNumberToMerge = minWriteBufferNumberToMerge;
            return this;
        }

        public Builder withNumLevels(Integer numLevels) {
            this.numLevels = numLevels;
            return this;
        }

        public Builder withTargetFileSizeBase(Long targetFileSizeBase) {
            this.targetFileSizeBase = targetFileSizeBase;
            return this;
        }

        public Builder withMaxBytesForLevelBase(Long maxBytesForLevelBase) {
            this.maxBytesForLevelBase = maxBytesForLevelBase;
            return this;
        }

        public Builder withEnableLevelCompactionDynamicLevelBytes(Boolean enableLevelCompactionDynamicLevelBytes) {
            this.enableLevelCompactionDynamicLevelBytes = enableLevelCompactionDynamicLevelBytes;
            return this;
        }

        public Builder withMaxCompactionBytes(Long maxCompactionBytes) {
            this.maxCompactionBytes = maxCompactionBytes;
            return this;
        }

        public Builder withArenaBlockSize(Long arenaBlockSize) {
            this.arenaBlockSize = arenaBlockSize;
            return this;
        }

        public Builder withDisableAutoCompactions(Boolean disableAutoCompactions) {
            this.disableAutoCompactions = disableAutoCompactions;
            return this;
        }

        public Builder withMaxSequentialSkipInIterations(Long maxSequentialSkipInIterations) {
            this.maxSequentialSkipInIterations = maxSequentialSkipInIterations;
            return this;
        }

        public Builder withMemtablePrefixBloomSizeRatio(Double memtablePrefixBloomSizeRatio) {
            this.memtablePrefixBloomSizeRatio = memtablePrefixBloomSizeRatio;
            return this;
        }

        public Builder withMaxSuccessiveMerges(Long maxSuccessiveMerges) {
            this.maxSuccessiveMerges = maxSuccessiveMerges;
            return this;
        }

        public Builder withSoftPendingCompactionBytesLimit(Long softPendingCompactionBytesLimit) {
            this.softPendingCompactionBytesLimit = softPendingCompactionBytesLimit;
            return this;
        }

        public Builder withLevel0FileNumCompactionTrigger(Integer level0FileNumCompactionTrigger) {
            this.level0FileNumCompactionTrigger = level0FileNumCompactionTrigger;
            return this;
        }

        public Builder withLevel0StopWritesTrigger(Integer level0StopWritesTrigger) {
            this.level0StopWritesTrigger = level0StopWritesTrigger;
            return this;
        }

        public Builder withMaxWriteBufferNumberToMaintain(Integer maxWriteBufferNumberToMaintain) {
            this.maxWriteBufferNumberToMaintain = maxWriteBufferNumberToMaintain;
            return this;
        }

        public ColumnFamilyConfig build() {
            return new ColumnFamilyConfig(this);
        }
    }
}