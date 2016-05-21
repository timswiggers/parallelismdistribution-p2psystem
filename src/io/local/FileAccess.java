package io.local;

import java.io.IOException;

/**
 * Created by timsw on 07/05/2016.
 */
public interface FileAccess {
    byte[] getFileBytes(String fileName) throws IOException;
    void saveFileBytes(String fileName, byte[] bytes) throws IOException;
}
