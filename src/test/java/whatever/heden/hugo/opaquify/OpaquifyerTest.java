package whatever.heden.hugo.opaquify;

import org.junit.jupiter.api.Test;
import whatever.heden.hugo.opaquify.util.TestUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class OpaquifyerTest {

    @Test
    void opaquifySimple() {
        checkOpaquification("hello", "there");
    }

    @Test
    void opaquifyDeopaquifySmallNumbers() {
        String uniquifier = "";
        for (int i = 0; i < 500_000; i++) {
            checkOpaquification(Integer.toString(i), uniquifier);
        }
    }

    @Test
    void opaquifyLongerString() {
        String str = "dsadsadsad21jdkoejci3o2jr302ufijewifojeifojfio3j2ijf302fewjfidojfidsofjie3j2ifjeofjdjsfxzjiroeji023jfiaojfidaofjdisofj3209e0vkl980jdlkjcxzu8eoqcxlackxzcsoacs";
        checkOpaquification(str, "");
    }

    @Test
    void opaquifyDeopaquifyPowersOfTen() {
        String uniquifier = "the powers that be";
        for (int i = 0; i < 1000; i++) {
            BigInteger pow = BigInteger.TEN.pow(i);
            checkOpaquification(pow.toString(), uniquifier);
        }
    }

    @Test
    void opaquifyDeopaquifyOnes() {
        String uniquifier = "";
        String ones = "";
        for (int i = 0; i < 1000; i++) {
            ones += "1";
            checkOpaquification(ones, uniquifier);
        }
    }

    @Test
    void opaquifyDeopaquifyZeros() {
        // We seem to get by with an empty uniquifier!?
        String uniquifier = "";
        String zeros = "";
        for (int i = 0; i < 1000; i++) {
            zeros += "0";
            checkOpaquification(zeros, uniquifier);
        }
    }

    @Test
    void opaquifyDeopaquifyTheUniquifierItself() {
        String uniquifier = "the powers that be";
        String input = "the powers that be";
        checkOpaquification(input, uniquifier);
    }

    @Test
    void opaquifyDeopaquifySomeChinese() {
        String str1 = "Hello, lorem ipsum etc.";
        String str2 = "份非常间单的说明书";
        checkOpaquification(str2, str1);
        checkOpaquification(str1, str2);
    }

    void checkOpaquification(String input, String uniquifier) {

        Opaquifyer scrambler = new Opaquifyer(uniquifier.getBytes(StandardCharsets.UTF_8));
        String tokified = scrambler.opaquify(input);
        String detokified = scrambler.deopaquify(tokified);

        double randomness = TestUtils.estimateRandomness(scrambler.scramble(input.getBytes(StandardCharsets.UTF_8)));
        Supplier<String> messageSupplier = () ->
                String.format("Input: %s (len: %d). Uniquifier: %s. Tokified: %s. Detokified (should be equal to input): %s. Randomness: %f",
                        input, input.length(), uniquifier, tokified, detokified, randomness);
        // System.out.printf("%d -> %s (%f)%n", input.length(), tokified, randomness);
        assertThat(detokified).withFailMessage(messageSupplier).isEqualTo(input);

        assertThat(randomness)
                .withFailMessage(messageSupplier)
                .isGreaterThanOrEqualTo(minimumAcceptableRandomnessScore(input.getBytes(StandardCharsets.UTF_8)));

    }

    private double minimumAcceptableRandomnessScore(byte[] input) {
        return 1.0;
    }

    @Test
    void testEncode_initialZerosAreRetained() {
        // Some naive encoders fail a test like this - information about leading zeros is lost
        byte[] bytes = {0, 119};
        String encoded = Opaquifyer.encode(bytes);
        byte[] bytes2 = {119};
        String encoded2 = Opaquifyer.encode(bytes2);
        assertThat(encoded2).isNotEqualTo(encoded);
    }


}
