package io.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FileUtils.*;

public class LocalFileSystem implements FileAccess {
    private Path appRoot;

    public LocalFileSystem(Path appRoot) {
        this.appRoot = appRoot;
    }

    public byte[] getFileBytes(String fileName) throws IOException {
        String filePath = resolvePath(fileName);
        File file = new File(filePath);

        if(!file.exists()){
            throw new RuntimeException("The file does not exist");
        }

        return readFileToByteArray(file); //  Never return null
    }

    public void saveFileBytes(String fileName, byte[] bytes) throws IOException {
        String filePath = resolvePath(fileName);
        File file = new File(filePath);

        if(!file.exists()){
            File parent = file.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Could not create directory: " + parent);
            }

            boolean creationSuccess = file.createNewFile();
            if(!creationSuccess) {
                throw new IOException("Could not create " + fileName + " file");
            }
        }

        writeByteArrayToFile(file, bytes);
    }

    public boolean exists(String fileName) {
        String filePath = resolvePath(fileName);
        return new File(filePath).exists();
    }

    @Override
    public String getName(String fileName) {
        String path = getPath(fileName);
        return new File(path).getName();
    }

    @Override
    public String getPath(String fileName) {
        return resolvePath(fileName);
    }

    private String resolvePath(String fileName) {

        // TODO: Handle relative or absolute path -- To test
        String localRoot = appRoot.toString();

        return Paths.get(localRoot, fileName).toAbsolutePath().toString();
    }
}
