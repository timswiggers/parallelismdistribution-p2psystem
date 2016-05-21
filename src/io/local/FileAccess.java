package io.local;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by timsw on 07/05/2016.
 */
public interface FileAccess {
    boolean exists(String fileName);

    String getPath(String fileName);

    byte[] getFileBytes(String fileName) throws IOException;
    void saveFileBytes(String fileName, byte[] bytes) throws IOException;
}
