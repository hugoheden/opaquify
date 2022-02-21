import whatever.heden.hugo.opaquify.Opaquifyer;

import java.math.BigInteger;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Example1 {
    public static void main(String[] args) {
        example1();
        example2();
        comparisonWithBase64_adoc();
    }

    static void example1() {
        Opaquifyer opaquifyer = new Opaquifyer(); // <1>
        String opaquified = opaquifyer.opaquify("Opaquify me!"); // <2>
        System.out.println(opaquified); // mek7mop9k49kv9njd5manbsugw <3>
    }

    static void example2() {
        Opaquifyer opaquifyer = new Opaquifyer("My project rocks!".getBytes(UTF_8)); // <1>
        String opaquified = opaquifyer.opaquify("Opaquify me!"); // <2>
        System.out.println(opaquified); // mek7mop9k49kv9njd5manbsugw <3>
    }

    static void comparisonWithBase64_adoc() {
        Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
        Opaquifyer opaquifyer = new Opaquifyer();
        System.out.println("|===");
        System.out.println("| Original String | Base64 encoded (URL-style) | Opaquified ");

        for (int i = 0; i < 20; i++) {
            String orig = String.valueOf(i);
            String base64encoded = base64Encoder.encodeToString(orig.getBytes());
            String opaquified = opaquifyer.opaquify(orig);
            System.out.printf("| `%s` | `%s` | `%s` %n", orig, base64encoded, opaquified);
        }
        for (int i = 1; i < 10; i++) {
            String orig = BigInteger.valueOf(10).pow(i).toString();
            String base64encoded = base64Encoder.encodeToString(orig.getBytes());
            String opaquified = opaquifyer.opaquify(orig);
            System.out.printf("| `%s` | `%s` | `%s` %n", orig, base64encoded, opaquified);
        }
        System.out.println("|===");
    }
}