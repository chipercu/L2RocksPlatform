package com.fuzzy.main.entityprovidersdk.serialization.serializer;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class CompactStreamEncoder {

    private final byte[] buf = new byte[12];
    private final OutputStream out;

    public CompactStreamEncoder(OutputStream out) {
        this.out = out;
    }


    public void writeByteArray(byte[] bytes) throws IOException {
        writeBytes(bytes);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        writeBytes(bytes, 0, bytes.length);
    }


    public void writeFixed(byte[] bytes) throws IOException {
        writeFixed(bytes, 0, bytes.length);
    }


    public void writeFixed(ByteBuffer bytes) throws IOException {
        int pos = bytes.position();
        int len = bytes.limit() - pos;
        if (bytes.hasArray()) {
            writeFixed(bytes.array(), bytes.arrayOffset() + pos, len);
        } else {
            byte[] b = new byte[len];
            bytes.duplicate().get(b, 0, len);
            writeFixed(b, 0, len);
        }
    }


    public void writeNullableString(String value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeString(value);
        }
    }

    public void writeString(String string) throws IOException {
        if (0 == string.length()) {
            writeZero();
            return;
        }
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeFixed(bytes, 0, bytes.length);
    }


    public void writeBytes(ByteBuffer bytes) throws IOException {
        int len = bytes.limit() - bytes.position();
        if (0 == len) {
            writeZero();
        } else {
            writeInt(len);
            writeFixed(bytes);
        }
    }


    public void writeStringArray(Collection<String> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (String sourceString : source) {
            writeNullableString(sourceString);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeStringCollection(Collection<String> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeStringArray(source);
        }
    }

    public void writeIntArray(Collection<Integer> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (int integer : source) {
            writeNullableInt(integer);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeIntegerCollection(Collection<Integer> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeIntArray(source);
        }
    }

    public void writeDoubleArray(Collection<Double> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (Double sourceDouble : source) {
            writeNullableDouble(sourceDouble);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeDoubleCollection(Collection<Double> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeDoubleArray(source);
        }
    }


    public void writeLongArray(Collection<Long> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (Long sourceLong : source) {
            writeNullableLong(sourceLong);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeLongCollection(Collection<Long> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeLongArray(source);
        }
    }


    public void writeBooleanArray(Collection<Boolean> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (Boolean sourceBoolean : source) {
            writeNullableBoolean(sourceBoolean);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeBooleanCollection(Collection<Boolean> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeBooleanArray(source);
        }
    }


    public void writeInstantArray(Collection<Instant> source) throws IOException {
        long size = source.size();
        long actualSize = 0;
        setItemCount(size);
        for (Instant sourceBoolean : source) {
            writeNullableInstant(sourceBoolean);
            actualSize++;
        }
        writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException(
                    "Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    public void writeInstantCollection(Collection<Instant> source) throws IOException {
        if (Objects.isNull(source)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeInstantArray(source);
        }
    }


    public void writeBytes(byte[] bytes, int start, int len) throws IOException {
        if (0 == len) {
            writeZero();
            return;
        }
        this.writeInt(len);
        this.writeFixed(bytes, start, len);
    }

    public void setItemCount(long itemCount) throws IOException {
        if (itemCount > 0) {
            this.writeLong(itemCount);
        }
    }


    public void writeArrayEnd() throws IOException {
        writeZero();
    }


    public void writeBoolean(boolean b) throws IOException {
        out.write(b ? 1 : 0);
    }


    public void writeNullableBoolean(Boolean value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeBoolean(value);
        }
    }


    /*
     * buffering is slower for ints that encode to just 1 or two bytes, and and
     * faster for large ones. (Sun JRE 1.6u22, x64 -server)
     */

    public void writeInt(int n) throws IOException {
        int val = (n << 1) ^ (n >> 31);
        if ((val & ~0x7F) == 0) {
            out.write(val);
            return;
        } else if ((val & ~0x3FFF) == 0) {
            out.write(0x80 | val);
            out.write(val >>> 7);
            return;
        }
        int len = encodeInt(n, buf, 0);
        out.write(buf, 0, len);
    }


    public void writeNullableInt(Integer value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeInt(value);
        }
    }
    /*
     * buffering is slower for writeLong when the number is small enough to fit in
     * an int. (Sun JRE 1.6u22, x64 -server)
     */

    public void writeLong(long n) throws IOException {
        long val = (n << 1) ^ (n >> 63); // move sign to low-order bit
        if ((val & ~0x7FFFFFFFL) == 0) {
            int i = (int) val;
            while ((i & ~0x7F) != 0) {
                out.write((byte) ((0x80 | i) & 0xFF));
                i >>>= 7;
            }
            out.write((byte) i);
            return;
        }
        int len = encodeLong(n, buf, 0);
        out.write(buf, 0, len);
    }

    public void writeNullableInstant(Instant value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeLong(value.toEpochMilli());
        }
    }

    public void writeNullableLong(Long value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeLong(value);
        }
    }

    public void writeFloat(float f) throws IOException {
        int len = encodeFloat(f, buf, 0);
        out.write(buf, 0, len);
    }


    public void writeDouble(double d) throws IOException {
        int len = encodeDouble(d, buf, 0);
        out.write(buf, 0, len);
    }

    public void writeNullableDouble(Double value) throws IOException {
        if (Objects.isNull(value)) {
            writeBoolean(true);
        } else {
            writeBoolean(false);
            writeDouble(value);
        }
    }


    public void writeFixed(byte[] bytes, int start, int len) throws IOException {
        out.write(bytes, start, len);
    }

    protected void writeZero() throws IOException {
        out.write(0);
    }


    private int encodeInt(int n, byte[] buf, int pos) {
        // move sign to low-order bit, and flip others if negative
        n = (n << 1) ^ (n >> 31);
        int start = pos;
        if ((n & ~0x7F) != 0) {
            buf[pos++] = (byte) ((n | 0x80) & 0xFF);
            n >>>= 7;
            if (n > 0x7F) {
                buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                n >>>= 7;
                if (n > 0x7F) {
                    buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                    n >>>= 7;
                    if (n > 0x7F) {
                        buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                        n >>>= 7;
                    }
                }
            }
        }
        buf[pos++] = (byte) n;
        return pos - start;
    }

    /**
     * Encode a long to the byte array at the given position. Will throw
     * IndexOutOfBounds if it overflows. Users should ensure that there are at least
     * 10 bytes left in the buffer before calling this method.
     *
     * @return The number of bytes written to the buffer, between 1 and 10.
     */
    private int encodeLong(long n, byte[] buf, int pos) {
        // move sign to low-order bit, and flip others if negative
        n = (n << 1) ^ (n >> 63);
        int start = pos;
        if ((n & ~0x7FL) != 0) {
            buf[pos++] = (byte) ((n | 0x80) & 0xFF);
            n >>>= 7;
            if (n > 0x7F) {
                buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                n >>>= 7;
                if (n > 0x7F) {
                    buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                    n >>>= 7;
                    if (n > 0x7F) {
                        buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                        n >>>= 7;
                        if (n > 0x7F) {
                            buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                            n >>>= 7;
                            if (n > 0x7F) {
                                buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                                n >>>= 7;
                                if (n > 0x7F) {
                                    buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                                    n >>>= 7;
                                    if (n > 0x7F) {
                                        buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                                        n >>>= 7;
                                        if (n > 0x7F) {
                                            buf[pos++] = (byte) ((n | 0x80) & 0xFF);
                                            n >>>= 7;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        buf[pos++] = (byte) n;
        return pos - start;
    }

    /**
     * Encode a float to the byte array at the given position. Will throw
     * IndexOutOfBounds if it overflows. Users should ensure that there are at least
     * 4 bytes left in the buffer before calling this method.
     *
     * @return Returns the number of bytes written to the buffer, 4.
     */
    private int encodeFloat(float f, byte[] buf, int pos) {
        final int bits = Float.floatToRawIntBits(f);
        buf[pos + 3] = (byte) (bits >>> 24);
        buf[pos + 2] = (byte) (bits >>> 16);
        buf[pos + 1] = (byte) (bits >>> 8);
        buf[pos] = (byte) (bits);
        return 4;
    }

    /**
     * Encode a double to the byte array at the given position. Will throw
     * IndexOutOfBounds if it overflows. Users should ensure that there are at least
     * 8 bytes left in the buffer before calling this method.
     *
     * @return Returns the number of bytes written to the buffer, 8.
     */
    private int encodeDouble(double d, byte[] buf, int pos) {
        final long bits = Double.doubleToRawLongBits(d);
        int first = (int) (bits & 0xFFFFFFFF);
        int second = (int) ((bits >>> 32) & 0xFFFFFFFF);
        // the compiler seems to execute this order the best, likely due to
        // register allocation -- the lifetime of constants is minimized.
        buf[pos] = (byte) (first);
        buf[pos + 4] = (byte) (second);
        buf[pos + 5] = (byte) (second >>> 8);
        buf[pos + 1] = (byte) (first >>> 8);
        buf[pos + 2] = (byte) (first >>> 16);
        buf[pos + 6] = (byte) (second >>> 16);
        buf[pos + 7] = (byte) (second >>> 24);
        buf[pos + 3] = (byte) (first >>> 24);
        return 8;
    }

}
