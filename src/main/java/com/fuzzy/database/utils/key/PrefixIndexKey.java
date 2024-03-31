package com.fuzzy.database.utils.key;

import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.schema.PrefixIndex;
import com.fuzzy.database.schema.dbstruct.DBPrefixIndex;
import com.fuzzy.database.utils.ByteUtils;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.database.utils.key.KeyUtils;

import java.nio.ByteBuffer;

public class PrefixIndexKey {

    private static final int BLOCK_NUMBER_BYTE_SIZE = Integer.BYTES;
    private static final byte LEXEME_TERMINATOR = 0;

    private final String lexeme;
    private final int blockNumber;
    private final byte[] attendant;

    private PrefixIndexKey(String lexeme, int blockNumber, final byte[] attendant) {
        this.lexeme = lexeme;
        this.blockNumber = blockNumber;
        this.attendant = attendant;
    }

    public PrefixIndexKey(String lexeme, final PrefixIndex index) {
        this(lexeme, 0, index.attendant);
    }

    public PrefixIndexKey(String lexeme, final DBPrefixIndex index) {
        this(lexeme, 0, index.getAttendant());
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public static void incrementBlockNumber(byte[] key) {
        ByteBuffer buffer = TypeConvert.wrapBuffer(key);
        int number = buffer.getInt(key.length - BLOCK_NUMBER_BYTE_SIZE);
        if (number == Integer.MAX_VALUE) {
            throw new RuntimeException("Unexpected block number " + number);
        }
        buffer.putInt(key.length - BLOCK_NUMBER_BYTE_SIZE, number + 1);
    }

    public byte[] pack() {
        byte[] value = TypeConvert.pack(lexeme);

        return TypeConvert.allocateBuffer(attendant.length + value.length + 1 + BLOCK_NUMBER_BYTE_SIZE)
                .put(attendant)
                .put(value)
                .put(LEXEME_TERMINATOR)
                .putInt(blockNumber)
                .array();
    }

    public static PrefixIndexKey unpack(byte[] key) {
        byte[] attendant = com.fuzzy.database.utils.key.KeyUtils.getIndexAttendant(key);
        byte[] payload = new byte[key.length - attendant.length];
        System.arraycopy(key, attendant.length, payload, 0, payload.length);
        int endIndex = ByteUtils.indexOf(LEXEME_TERMINATOR, payload);
        return new PrefixIndexKey(
                TypeConvert.unpackString(payload, 0, endIndex),
                TypeConvert.wrapBuffer(payload).getInt(endIndex + 1),
                attendant
        );
    }

    public static KeyPattern buildKeyPatternForFind(final String word, final PrefixIndex index) {
        byte[] payload = TypeConvert.pack(word);
        byte[] key = com.fuzzy.database.utils.key.KeyUtils.allocateAndPutIndexAttendant(index.attendant.length + payload.length, index.attendant);
        System.arraycopy(payload, 0, key, index.attendant.length, payload.length);
        return new KeyPattern(key);
    }

    public static KeyPattern buildKeyPatternForFind(final String word, final DBPrefixIndex index) {
        byte[] payload = TypeConvert.pack(word);
        byte[] key = com.fuzzy.database.utils.key.KeyUtils.allocateAndPutIndexAttendant(index.getAttendant().length + payload.length, index.getAttendant());
        System.arraycopy(payload, 0, key, index.getAttendant().length, payload.length);
        return new KeyPattern(key);
    }

    public static KeyPattern buildKeyPatternForEdit(final String lexeme, final PrefixIndex index) {
        byte[] payload = TypeConvert.pack(lexeme);
        byte[] key = com.fuzzy.database.utils.key.KeyUtils.allocateAndPutIndexAttendant(index.attendant.length + payload.length + 1, index.attendant);
        System.arraycopy(payload, 0, key, index.attendant.length, payload.length);
        key[key.length - 1] = LEXEME_TERMINATOR;
        return new KeyPattern(key);
    }

    public static KeyPattern buildKeyPatternForEdit(final String lexeme, final DBPrefixIndex index) {
        byte[] payload = TypeConvert.pack(lexeme);
        byte[] key = KeyUtils.allocateAndPutIndexAttendant(index.getAttendant().length + payload.length + 1, index.getAttendant());
        System.arraycopy(payload, 0, key, index.getAttendant().length, payload.length);
        key[key.length - 1] = LEXEME_TERMINATOR;
        return new KeyPattern(key);
    }
}
