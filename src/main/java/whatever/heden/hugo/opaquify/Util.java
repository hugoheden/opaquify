package whatever.heden.hugo.opaquify;

import java.util.Arrays;

public class Util {

    static byte[] prependPositiveByte(byte[] b) {
        // prepend a byte in span [1->127]
        byte[] result = new byte[b.length + 1];
        // Could prepend whatever here, say 1 (just not zero). But we want it
        // to look somewhat random, i.e. a different byte for different input. But we
        // don't want to use randomness here, because we want the result to be
        // the same for multiple runs with the same input.
        long pad = 0L;
        for (byte value : b) {
            pad += Byte.toUnsignedInt(value);
        }
        pad %= 127; // [0->126]
        pad += 1; // [1->127]
        result[0] = (byte) pad;
        System.arraycopy(b, 0, result, 1, b.length);
        return result;
    }

    static byte[] removeFirstByte(byte[] bytes) {
        byte[] b = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, b, 0, b.length);
        return b;
    }

    static byte[] expandToLength32(byte[] input) {
        if (input.length > 32) {
            // Refuse to handle. (Don't mislead the caller to think that their long input makes them more
            // unique than their first 32 bytes).
            throw new RuntimeException("input too long");
        }
        if (input.length == 32) {
            return input;
        }
        return Arrays.copyOf(input, 32);
    }

}
