package whatever.heden.hugo.opaquify;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UtilTest {

    @Test
    void testExpandToLength32_prolongs_short_array() {
        byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byte[] result = Util.expandToLength32(bytes);
        assertThat(result).isEqualTo(new byte[]{
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0
        });
    }

    @Test
    void testExpandLength32_unchanged_if_already_32() {
        byte[] bytes = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                31, 32
        };
        byte[] result = Util.expandToLength32(bytes);
        assertThat(result).isEqualTo(bytes);
    }

    @Test
    void testExpandLength32_throws_if_input_longer_than_32() {
        byte[] bytes = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                31, 32, 33
        };
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> Util.expandToLength32(bytes));
    }

    @Test
    void testPrependPositiveByte() {
        byte[] bytes = Util.prependPositiveByte(new byte[]{0, 1, 2});
        assertThat(bytes.length).isEqualTo(4);
        assertThat(bytes[0]).isGreaterThan((byte) 0);
    }

    @Test
    void testRemoveFirstByte() {
        byte[] bytes = Util.removeFirstByte(new byte[]{77, 0, 1, 2});
        assertThat(bytes).isEqualTo(new byte[]{0, 1, 2});
    }

}
