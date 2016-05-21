package io.local;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by timsw on 01/05/2016.
 */
public class LocalFileSystem implements FileAccess {
    private String baseDir;

    public LocalFileSystem(String baseDir) {
        this.baseDir = baseDir;
    }

    public byte[] getFileBytes(String fileName) throws IOException {
        String filePath = revolvePath(fileName);
        File file = new File(filePath);

        // TODO: Handle file not existing => return new byte[0]

        byte[] bytes = FileUtils.readFileToByteArray(file);

        return bytes; //  Never return null
    }

    public void saveFileBytes(String fileName, byte[] bytes) throws IOException {
        String filePath = revolvePath(fileName);
        File file = new File(filePath);

        // TODO: Handle file already existing. Ask for cancel/overwrite.

        FileUtils.writeByteArrayToFile(file, bytes);
    }

    private String revolvePath(String fileName) {

        // TODO: Handle relative or absolute path

        return Paths.get(baseDir, fileName).toAbsolutePath().toString();
    }
}
