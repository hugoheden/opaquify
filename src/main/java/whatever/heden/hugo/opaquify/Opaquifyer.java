package whatever.heden.hugo.opaquify;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static whatever.heden.hugo.opaquify.Util.prependPositiveByte;
import static whatever.heden.hugo.opaquify.Util.removeFirstByte;

/**
 * Note: Do not try to opaquify anything that is secret. The scrambling done here is easy to crack.
 */
public class Opaquifyer {

    private final Cipher forward;
    private final Cipher backward;

    public Opaquifyer(byte[] uniquifier) {
        uniquifier = Util.expandToLength32(uniquifier);
        // Note: Even though we are using JDK:s encryption libraries, we are using in it in a very unsafe way.
        // So it is important that nobody tries to "encrypt" anything that is secret. For example, we are
        // reusing the same IV over and over again. This totally breaks any secrecy. (Here, we do this
        // intentionally, because we want a repeated scrambling with the same input to yield the same result).
        SecretKey keySpec = new SecretKeySpec(uniquifier, 0, 16, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(uniquifier, 16, 16);
        try {
            forward = Cipher.getInstance("AES/CBC/PKCS5Padding");
            backward = Cipher.getInstance("AES/CBC/PKCS5Padding");
            forward.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            backward.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String opaquify(String input) {
        return opaquifyBytes(input.getBytes(StandardCharsets.UTF_8));
    }

    public String deopaquify(String token) {
        return new String(deopaquifyBytes(token), StandardCharsets.UTF_8);
    }

    public String opaquifyBytes(byte[] bytes) {
        byte[] scramble = scramble(bytes);
        return encode(scramble);
    }

    public byte[] deopaquifyBytes(String token) {
        byte[] decoded = decode(token);
        return unscramble(decoded);
    }

    byte[] scramble(byte[] plainText) {
        try {
            return forward.doFinal(plainText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    byte[] unscramble(byte[] cipherText) {
        try {
            return backward.doFinal(cipherText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String encode(byte[] b) {
        // Our encoding scheme (using BigInteger functionality) breaks if there are leading zeros in the input.
        // Leading zeros will be "lost": new BigInteger(new byte[]{0, 0, 119}).toByteArray() -> { 119 }.
        // So we use a hack here, and prepend a non-zero byte. Furthermore, we want to avoid a negative number, so
        // we make sure to prepend a _positive_ byte.
        b = prependPositiveByte(b);
        BigInteger bigInteger = new BigInteger(b);
        return bigInteger.toString(36);
    }

    static byte[] decode(String token) {
        BigInteger bigInteger = new BigInteger(token, 36);
        byte[] bytes = bigInteger.toByteArray();
        return removeFirstByte(bytes);
    }
}
