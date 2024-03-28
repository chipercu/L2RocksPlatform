package com.fuzzy.main.rdao.rocksdb;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.utils.PathUtils;
import com.fuzzy.main.rdao.database.utils.TempLibraryCleaner;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfig;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfigService;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RocksDataBaseBuilder {

    private Path path;
    private ColumnFamilyConfigService columnFamilyConfigService;


    public RocksDataBaseBuilder withPath(Path path) {
        this.path = path.toAbsolutePath();
        return this;
    }

    public RocksDataBaseBuilder withConfigColumnFamilies(Map<String, ColumnFamilyConfig> configuredColumnFamilies) {
        columnFamilyConfigService = new ColumnFamilyConfigService(configuredColumnFamilies);
        return this;
    }

    public RocksDBProvider build() throws DatabaseException {
        TempLibraryCleaner.clear();
        PathUtils.checkPath(path);
        try (DBOptions options = buildOptions()) {
            List<ColumnFamilyDescriptor> columnFamilyDescriptors = getColumnFamilyDescriptors();
            if (Objects.nonNull(columnFamilyConfigService)) {
                columnFamilyConfigService.applySettings(columnFamilyDescriptors);
            }
            List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
            OptimisticTransactionDB rocksDB = OptimisticTransactionDB.open(options, path.toString(), columnFamilyDescriptors, columnFamilyHandles);

            ConcurrentMap<String, ColumnFamilyHandle> columnFamilies = new ConcurrentHashMap<>();
            for (int i = 0; i < columnFamilyDescriptors.size(); i++) {
                String columnFamilyName = TypeConvert.unpackString(columnFamilyDescriptors.get(i).getName());
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandles.get(i);
                columnFamilies.put(columnFamilyName, columnFamilyHandle);
            }

            return new RocksDBProvider(rocksDB, columnFamilies);
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    private DBOptions buildOptions() throws RocksDBException {
        final String optionsFilePath = path.toString() + ".ini";

        DBOptions options = new DBOptions();
        if (Files.exists(Paths.get(optionsFilePath))) {
            final List<ColumnFamilyDescriptor> ignoreDescs = new ArrayList<>();
            OptionsUtil.loadOptionsFromFile(optionsFilePath, Env.getDefault(), options, ignoreDescs, false);
        } else {
            options
                    .setInfoLogLevel(InfoLogLevel.WARN_LEVEL)
                    .setMaxTotalWalSize(100L * SizeUnit.MB);
        }

        return options.setCreateIfMissing(true);
    }

    private List<ColumnFamilyDescriptor> getColumnFamilyDescriptors() throws RocksDBException {
        List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();

        try (Options options = new Options()) {
            for (byte[] columnFamilyName : RocksDB.listColumnFamilies(options, path.toString())) {
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(columnFamilyName));
            }
        }

        if (columnFamilyDescriptors.isEmpty()) {
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(TypeConvert.pack(RocksDBProvider.DEFAULT_COLUMN_FAMILY)));
        }

        return columnFamilyDescriptors;
    }
}