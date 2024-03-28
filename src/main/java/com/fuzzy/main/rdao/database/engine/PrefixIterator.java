package com.fuzzy.main.rdao.database.engine;

import com.fuzzy.main.rdao.database.Record;
import com.fuzzy.main.rdao.database.domainobject.filter.PrefixFilter;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBDataReader;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBPrefixIndex;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;
import com.fuzzy.main.rdao.database.utils.PrefixIndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import com.fuzzy.main.rdao.database.utils.key.PrefixIndexKey;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrefixIterator extends BaseIndexRecordIterator {

    private final DBPrefixIndex index;

    private List<String> searchingWords;
    private ByteBuffer loadingIds = null;
    private final RangeSet<Long> prevLoadedIds = TreeRangeSet.create();

    private List<String> tempList;

    public PrefixIterator(DBTable table, PrefixFilter filter, DBDataReader dataReader) {
        super(table, dataReader);

        this.index = table.getIndex(filter);
        this.searchingWords = PrefixIndexUtils.splitSearchingTextIntoWords(filter.getFieldValue());
        if (this.searchingWords.isEmpty()) {
            return;
        }

        KeyPattern indexKeyPattern = PrefixIndexKey.buildKeyPatternForFind(searchingWords.get(searchingWords.size() - 1), index);
        if (this.searchingWords.size() <= 1) {
            this.searchingWords = Collections.emptyList();
        }

        this.tempList = new ArrayList<>();

        KeyValue keyValue = indexIterator.seek(indexKeyPattern);
        this.loadingIds = keyValue != null ? TypeConvert.wrapBuffer(keyValue.getValue()) : null;

        nextImpl();
    }

    @Override
    protected void nextImpl() throws DatabaseException {
        while (loadingIds != null) {
            if (!loadingIds.hasRemaining()) {
                KeyValue keyValue = indexIterator.next();
                loadingIds = keyValue != null ? TypeConvert.wrapBuffer(keyValue.getValue()) : null;
                continue;
            }

            final long id = loadingIds.getLong();
            if (prevLoadedIds.contains(id)) {
                continue;
            }

            nextRecord = findRecord(id);
            if (nextRecord != null) {
                prevLoadedIds.add(Range.closedOpen(id, id + 1));
                return;
            }
        }

        nextRecord = null;
        close();
    }

    @Override
    boolean checkFilter(Record record) throws DatabaseException {
        String[] values = new String[index.getFieldIds().length];
        for (int i = 0; i < index.getFieldIds().length; ++i) {
            values[i] = record.getValues()[index.getFieldIds()[i]].toString();
        }
        return PrefixIndexUtils.contains(searchingWords, values, tempList);
    }
}
