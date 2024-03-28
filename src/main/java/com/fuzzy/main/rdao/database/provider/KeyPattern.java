package com.fuzzy.main.rdao.database.provider;

import com.fuzzy.main.rdao.database.utils.ByteUtils;

import java.io.Serializable;

public class KeyPattern implements Serializable {

    public static final int MATCH_RESULT_SUCCESS = 1;
    public static final int MATCH_RESULT_CONTINUE = 0;
    public static final int MATCH_RESULT_UNSUCCESS = -1;

    public static class Postfix implements Serializable {
        private final int startPos;
        private final byte[] value;

        public Postfix(int startPos, byte[] value) {
            this.startPos = startPos;
            this.value = value;
        }

        public boolean match(byte[] key) {
            if ((key.length - startPos) != value.length) {
                return false;
            }
            return ByteUtils.endsWith(value, key);
        }
    }

    private byte[] prefix;
    private int strictMatchingLen;
    private final Postfix[] orPatterns;
    private boolean forBackward = false;

    public KeyPattern(byte[] prefix, int strictMatchingLen, Postfix[] orPatterns) {
        this.prefix = prefix;
        this.strictMatchingLen = strictMatchingLen;
        this.orPatterns = orPatterns;
    }

    public KeyPattern(byte[] prefix, Postfix[] orPatterns) {
        this(prefix, prefix != null ? prefix.length : 0, orPatterns);
    }

    public KeyPattern(byte[] prefix, int strictMatchingLen) {
        this(prefix, strictMatchingLen, null);
    }

    public KeyPattern(byte[] prefix) {
        this(prefix, null);
    }

    public KeyPattern(Postfix[] orPatterns) {
        this(null, orPatterns);
    }

    public void setPrefix(byte[] prefix) {
        this.prefix = prefix;
        if (strictMatchingLen != -1) {
            strictMatchingLen = prefix != null ? prefix.length : 0;
        }
    }

    public byte[] getPrefix() {
        return prefix;
    }

    public boolean isForBackward() {
        return forBackward;
    }

    public void setForBackward(boolean forBackward) {
        this.forBackward = forBackward;
    }

    public int match(final byte[] key) {
        if (prefix != null) {
            if (strictMatchingLen != -1 && !ByteUtils.startsWith(prefix, 0, strictMatchingLen, key)) {
                return MATCH_RESULT_UNSUCCESS;
            }
        }

        if (orPatterns == null) {
            return MATCH_RESULT_SUCCESS;
        }

        for (Postfix orPattern : orPatterns) {
            if (orPattern.match(key)) {
                return MATCH_RESULT_SUCCESS;
            }
        }

        return MATCH_RESULT_CONTINUE;
    }
}
