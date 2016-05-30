package io.local;

import java.io.IOException;

public interface FileAccess {
    boolean exists(String fileName);

    String getName(String fileName);
    String getPath(String fileName);

    byte[] getFileBytes(String fileName) throws IOException;
    void saveFileBytes(String fileName, byte[] bytes) throws IOException;
}
