package hashing;


import java.security.NoSuchAlgorithmException;

public interface BytesHasher {
    byte[] hash(byte[] bytes) throws NoSuchAlgorithmException;
}
