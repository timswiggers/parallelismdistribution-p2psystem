package io.local;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileSystem implements FileAccess {
    private Path appRoot;

    public LocalFileSystem(Path appRoot) {
        this.appRoot = appRoot;
    }

    public byte[] getFileBytes(String fileName) throws IOException {
        String filePath = resolvePath(fileName);
        File file = new File(filePath);

        // TODO: Handle file not existing => return new byte[0]

        byte[] bytes = FileUtils.readFileToByteArray(file);

        return bytes; //  Never return null
    }

    public void saveFileBytes(String fileName, byte[] bytes) throws IOException {
        String filePath = resolvePath(fileName);
        File file = new File(filePath);

        if(!file.exists()){
            file.createNewFile();
        }

        FileUtils.writeByteArrayToFile(file, bytes);
    }

    public boolean exists(String fileName) {
        String filePath = resolvePath(fileName);
        return new File(filePath).exists();
    }

    private String resolvePath(String fileName) {

        // TODO: Handle relative or absolute path
        String localRoot = appRoot.toString();

        return Paths.get(localRoot, fileName).toAbsolutePath().toString();
    }
}
