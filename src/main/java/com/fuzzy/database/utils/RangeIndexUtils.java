package com.fuzzy.database.utils;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.*;
import com.fuzzy.database.schema.RangeIndex;
import com.fuzzy.database.schema.dbstruct.DBRangeIndex;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.utils.IntervalIndexUtils;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.database.utils.key.KeyUtils;
import com.fuzzy.database.utils.key.RangeIndexKey;

import java.util.ArrayList;

public class RangeIndexUtils {

    public static void insertIndexedRange(RangeIndex index, RangeIndexKey key, Object beginValue, Object endValue, DBTransaction transaction) throws DatabaseException {
        if (isIndexedRange(beginValue, endValue)) {
            return;
        }

        final long totalBegin = IntervalIndexUtils.castToLong(beginValue);
        final long totalEnd = IntervalIndexUtils.castToLong(endValue);
        IntervalIndexUtils.checkInterval(totalBegin, totalEnd);

        key.setBeginRangeValue(totalBegin);

        try (DBIterator iterator = transaction.createIterator(index.columnFamily)) {
            // разобьем уже существующие интервалы по началу вставляемого
            final KeyPattern pattern = RangeIndexKey.buildLeftBorder(key.getHashedValues(), totalBegin, index);
            KeyValue keyValue = seek(iterator, pattern, totalBegin);
            for (; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (begin < totalBegin) {
                    if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                        RangeIndexKey.setIndexedValue(totalBegin, keyValue.getKey());
                        transaction.put(index.columnFamily, keyValue.getKey(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
                    }
                } else if (begin > totalBegin) {
                    break;
                }
            }

            // вставим начало вставляемого интервала
            key.setIndexedValue(totalBegin);
            key.setType(totalBegin != totalEnd ? RangeIndexKey.Type.BEGIN : RangeIndexKey.Type.DOT);
            transaction.put(index.columnFamily, key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

            ArrayList<byte[]> prevKeys = new ArrayList<>();
            // разобьем вставляемый интервал по уже существующим
            for (long prevBegin = totalBegin; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                if (key.getId() == RangeIndexKey.unpackId(keyValue.getKey())) {
                    continue;
                }

                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (prevBegin != begin) {
                    if (begin == totalEnd) {
                        prevKeys.clear();
                        break;
                    } else if (begin > totalEnd) {
                        for (; isEnd(keyValue); keyValue = stepForward(iterator, pattern)) {
                            prevKeys.add(keyValue.getKey());
                        }
                        break;
                    }

                    key.setIndexedValue(begin);
                    key.setType(RangeIndexKey.Type.BEGIN);
                    transaction.put(index.columnFamily, key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

                    prevBegin = begin;
                    prevKeys.clear();
                }

                if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                    prevKeys.add(keyValue.getKey());
                }
            }

            // разобьем уже существующие интервалы по концу вставляемого
            for (byte[] prevKey : prevKeys) {
                if (RangeIndexKey.unpackType(prevKey) == RangeIndexKey.Type.END) {
                    RangeIndexKey.setType(RangeIndexKey.Type.BEGIN, prevKey);
                }
                RangeIndexKey.setIndexedValue(totalEnd, prevKey);
                transaction.put(index.columnFamily, prevKey, com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
            }

            // добавим конец вставляемого интервала
            if (totalBegin != totalEnd) {
                key.setIndexedValue(totalEnd);
                key.setType(RangeIndexKey.Type.END);
                transaction.put(index.columnFamily, key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
            }
        }
    }

    public static void insertIndexedRange(DBRangeIndex index, RangeIndexKey key, Object beginValue, Object endValue, DBTable table, DBDataCommand dataCommand) throws DatabaseException {
        if (isIndexedRange(beginValue, endValue)) {
            return;
        }

        final long totalBegin = IntervalIndexUtils.castToLong(beginValue);
        final long totalEnd = IntervalIndexUtils.castToLong(endValue);
        IntervalIndexUtils.checkInterval(totalBegin, totalEnd);

        key.setBeginRangeValue(totalBegin);

        try (DBIterator iterator = dataCommand.createIterator(table.getIndexColumnFamily())) {
            // разобьем уже существующие интервалы по началу вставляемого
            final KeyPattern pattern = RangeIndexKey.buildLeftBorder(key.getHashedValues(), totalBegin, index);
            KeyValue keyValue = seek(iterator, pattern, totalBegin);
            for (; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (begin < totalBegin) {
                    if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                        RangeIndexKey.setIndexedValue(totalBegin, keyValue.getKey());
                        dataCommand.put(table.getIndexColumnFamily(), keyValue.getKey(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
                    }
                } else if (begin > totalBegin) {
                    break;
                }
            }

            // вставим начало вставляемого интервала
            key.setIndexedValue(totalBegin);
            key.setType(totalBegin != totalEnd ? RangeIndexKey.Type.BEGIN : RangeIndexKey.Type.DOT);
            dataCommand.put(table.getIndexColumnFamily(), key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

            ArrayList<byte[]> prevKeys = new ArrayList<>();
            // разобьем вставляемый интервал по уже существующим
            for (long prevBegin = totalBegin; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                if (key.getId() == RangeIndexKey.unpackId(keyValue.getKey())) {
                    continue;
                }

                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (prevBegin != begin) {
                    if (begin == totalEnd) {
                        prevKeys.clear();
                        break;
                    } else if (begin > totalEnd) {
                        for (; isEnd(keyValue); keyValue = stepForward(iterator, pattern)) {
                            prevKeys.add(keyValue.getKey());
                        }
                        break;
                    }

                    key.setIndexedValue(begin);
                    key.setType(RangeIndexKey.Type.BEGIN);
                    dataCommand.put(table.getIndexColumnFamily(), key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

                    prevBegin = begin;
                    prevKeys.clear();
                }

                if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                    prevKeys.add(keyValue.getKey());
                }
            }

            // разобьем уже существующие интервалы по концу вставляемого
            for (byte[] prevKey : prevKeys) {
                if (RangeIndexKey.unpackType(prevKey) == RangeIndexKey.Type.END) {
                    RangeIndexKey.setType(RangeIndexKey.Type.BEGIN, prevKey);
                }
                RangeIndexKey.setIndexedValue(totalEnd, prevKey);
                dataCommand.put(table.getIndexColumnFamily(), prevKey, com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
            }

            // добавим конец вставляемого интервала
            if (totalBegin != totalEnd) {
                key.setIndexedValue(totalEnd);
                key.setType(RangeIndexKey.Type.END);
                dataCommand.put(table.getIndexColumnFamily(), key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
            }
        }
    }

    public static void insertIndexedRange(DBRangeIndex index, RangeIndexKey key, Object beginValue, Object endValue, String indexColumnFamily, DBTransaction transaction) throws DatabaseException {
        if (isIndexedRange(beginValue, endValue)) {
            return;
        }

        final long totalBegin = IntervalIndexUtils.castToLong(beginValue);
        final long totalEnd = IntervalIndexUtils.castToLong(endValue);
        IntervalIndexUtils.checkInterval(totalBegin, totalEnd);

        key.setBeginRangeValue(totalBegin);

        try (DBIterator iterator = transaction.createIterator(indexColumnFamily)) {
            // разобьем уже существующие интервалы по началу вставляемого
            final KeyPattern pattern = RangeIndexKey.buildLeftBorder(key.getHashedValues(), totalBegin, index);
            KeyValue keyValue = seek(iterator, pattern, totalBegin);
            for (; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (begin < totalBegin) {
                    if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                        RangeIndexKey.setIndexedValue(totalBegin, keyValue.getKey());
                        transaction.put(indexColumnFamily, keyValue.getKey(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
                    }
                } else if (begin > totalBegin) {
                    break;
                }
            }

            // вставим начало вставляемого интервала
            key.setIndexedValue(totalBegin);
            key.setType(totalBegin != totalEnd ? RangeIndexKey.Type.BEGIN : RangeIndexKey.Type.DOT);
            transaction.put(indexColumnFamily, key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

            ArrayList<byte[]> prevKeys = new ArrayList<>();
            // разобьем вставляемый интервал по уже существующим
            for (long prevBegin = totalBegin; keyValue != null; keyValue = stepForward(iterator, pattern)) {
                if (key.getId() == RangeIndexKey.unpackId(keyValue.getKey())) {
                    continue;
                }

                long begin = RangeIndexKey.unpackIndexedValue(keyValue.getKey());
                if (prevBegin != begin) {
                    if (begin == totalEnd) {
                        prevKeys.clear();
                        break;
                    } else if (begin > totalEnd) {
                        for (; isEnd(keyValue); keyValue = stepForward(iterator, pattern)) {
                            prevKeys.add(keyValue.getKey());
                        }
                        break;
                    }

                    key.setIndexedValue(begin);
                    key.setType(RangeIndexKey.Type.BEGIN);
                    transaction.put(indexColumnFamily, key.pack(), com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);

                    prevBegin = begin;
                    prevKeys.clear();
                }

                if (RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.BEGIN) {
                    prevKeys.add(keyValue.getKey());
                }
            }

            // разобьем уже существующие интервалы по концу вставляемого
            for (byte[] prevKey : prevKeys) {
                if (RangeIndexKey.unpackType(prevKey) == RangeIndexKey.Type.END) {
                    RangeIndexKey.setType(RangeIndexKey.Type.BEGIN, prevKey);
                }
                RangeIndexKey.setIndexedValue(totalEnd, prevKey);
                transaction.put(indexColumnFamily, prevKey, com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY);
            }

            // добавим конец вставляемого интервала
            if (totalBegin != totalEnd) {
                key.setIndexedValue(totalEnd);
                key.setType(RangeIndexKey.Type.END);
                transaction.put(indexColumnFamily, key.pack(), TypeConvert.EMPTY_BYTE_ARRAY);
            }
        }
    }

    private static boolean isEnd(KeyValue keyValue) {
        return keyValue != null && RangeIndexKey.unpackType(keyValue.getKey()) == RangeIndexKey.Type.END;
    }

    public static void removeIndexedRange(RangeIndex index, RangeIndexKey key, Object beginValue, Object endValue,
                                          DBTransaction transaction, BiConsumer<String, byte[]> deleteFunc) throws DatabaseException {
        if (isIndexedRange(beginValue, endValue)) {
            return;
        }

        final long begin = IntervalIndexUtils.castToLong(beginValue);
        final long end = IntervalIndexUtils.castToLong(endValue);
        IntervalIndexUtils.checkInterval(begin, end);

        try (DBIterator iterator = transaction.createIterator(index.columnFamily)) {
            KeyValue keyValue = iterator.seek(RangeIndexKey.buildBeginPattern(key.getHashedValues(), begin, index));
            while (keyValue != null) {
                if (RangeIndexKey.unpackId(keyValue.getKey()) == key.getId()) {
                    deleteFunc.accept(index.columnFamily, keyValue.getKey());

                    if (RangeIndexKey.unpackType(keyValue.getKey()) != RangeIndexKey.Type.BEGIN) {
                        break;
                    }
                } else if (RangeIndexKey.unpackIndexedValue(keyValue.getKey()) > end) {
                    break;
                }

                keyValue = iterator.next();
            }
        }
    }

    public static void removeIndexedRange(DBRangeIndex index,
                                          RangeIndexKey key,
                                          Object beginValue,
                                          Object endValue,
                                          DBTable table,
                                          DBDataCommand dataCommand) throws DatabaseException {
        if (isIndexedRange(beginValue, endValue)) {
            return;
        }

        final long begin = IntervalIndexUtils.castToLong(beginValue);
        final long end = IntervalIndexUtils.castToLong(endValue);
        IntervalIndexUtils.checkInterval(begin, end);

        try (DBIterator iterator = dataCommand.createIterator(table.getIndexColumnFamily())) {
            KeyValue keyValue = iterator.seek(RangeIndexKey.buildBeginPattern(key.getHashedValues(), begin, index));
            while (keyValue != null) {
                if (RangeIndexKey.unpackId(keyValue.getKey()) == key.getId()) {
                    dataCommand.singleDelete(table.getIndexColumnFamily(), keyValue.getKey());

                    if (RangeIndexKey.unpackType(keyValue.getKey()) != RangeIndexKey.Type.BEGIN) {
                        break;
                    }
                } else if (RangeIndexKey.unpackIndexedValue(keyValue.getKey()) > end) {
                    break;
                }

                keyValue = iterator.next();
            }
        }
    }

    private static boolean isIndexedRange(Object beginValue, Object endValue) {
        return beginValue == null || endValue == null;
    }

    private static KeyValue stepForward(DBIterator iterator, KeyPattern pattern) throws DatabaseException {
        KeyValue keyValue = iterator.step(DBIterator.StepDirection.FORWARD);
        if (keyValue == null || pattern.match(keyValue.getKey()) != KeyPattern.MATCH_RESULT_SUCCESS) {
            return null;
        }

        return keyValue;
    }

    public static KeyValue seek(DBIterator indexIterator, KeyPattern pattern, long filterBeginValue) throws DatabaseException {
        KeyValue res = indexIterator.seek(pattern);
        if  (res == null) {
            return null;
        }

        long begin = RangeIndexKey.unpackIndexedValue(res.getKey());
        if (begin != filterBeginValue) {
            res = indexIterator.step(DBIterator.StepDirection.BACKWARD);
            if (res == null) {
                byte[] attendant = KeyUtils.getIndexAttendant(pattern.getPrefix());
                return indexIterator.seek(new KeyPattern(attendant));
            } else if (pattern.match(res.getKey()) != KeyPattern.MATCH_RESULT_UNSUCCESS) {
                begin = RangeIndexKey.unpackIndexedValue(res.getKey());
            }
        }

        do {
            if (pattern.match(res.getKey()) != KeyPattern.MATCH_RESULT_SUCCESS ||
                    RangeIndexKey.unpackType(res.getKey()) == RangeIndexKey.Type.END ||
                    begin != RangeIndexKey.unpackIndexedValue(res.getKey())) {
                res = indexIterator.step(DBIterator.StepDirection.FORWARD);
                return res != null && pattern.match(res.getKey()) != KeyPattern.MATCH_RESULT_UNSUCCESS ? res  : null;
            }

            res = indexIterator.step(DBIterator.StepDirection.BACKWARD);
        } while (res != null);

        return indexIterator.seek(null);
    }

    @FunctionalInterface
    public interface BiConsumer<T, U> {

        void accept(T t, U u) throws DatabaseException;
    }
}
