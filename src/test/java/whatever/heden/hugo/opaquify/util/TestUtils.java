package whatever.heden.hugo.opaquify.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestUtils {

    public static double estimateRandomness(byte[] arr) {
        if (arr == null || arr.length == 0) {
            throw new RuntimeException("bad input");
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipEntry e = new ZipEntry("some-file.txt");
            ZipOutputStream zos = new ZipOutputStream(out);
            zos.putNextEntry(e);
            zos.write(arr);
            zos.close();
            long compressedSize = e.getCompressedSize();
            return compressedSize / (1.0 * arr.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
