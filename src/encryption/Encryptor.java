package encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

// Credits: http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private final static String InitVector = "";

    private static final String Encoding = "UTF-8";
    private static final String CipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String CipherKeySpec = "AES";

    private static final SecureRandom random = new SecureRandom();

    public static String generateKey() throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(CipherKeySpec);
        keyGenerator.init(128);
        return keyGenerator.generateKey().toString();
    }

    public static String generateInitVector(){
        return new BigInteger(130, random).toString(32);
    }

    public static byte[] encrypt(String key, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(InitVector.getBytes(Encoding));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(Encoding), CipherKeySpec);

            Cipher cipher = Cipher.getInstance(CipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(String key, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(InitVector.getBytes(Encoding));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(Encoding), CipherKeySpec);

            Cipher cipher = Cipher.getInstance(CipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);

            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
