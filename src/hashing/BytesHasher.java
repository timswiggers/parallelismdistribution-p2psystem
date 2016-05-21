package hashing;


import java.security.NoSuchAlgorithmException;

/**
 * Created by timsw on 30/04/2016.
 */
public interface BytesHasher {
    byte[] hash(byte[] bytes) throws NoSuchAlgorithmException;
}
