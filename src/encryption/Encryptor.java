package encryption;

import org.apache.commons.lang3.RandomUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

// Credits: http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String Encoding = "UTF-8";
    private static final String CipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String CipherKeySpec = "AES";

    private static final SecureRandom random = new SecureRandom();

    private Encryptor(){ }

    public static byte[] generateKey() throws NoSuchProviderException, NoSuchAlgorithmException {
        return RandomUtils.nextBytes(16); // Must be 16!
    }

    public static byte[] generateInitVector(){
        return RandomUtils.nextBytes(16); // Must be 16!
    }

    public static byte[] encrypt(byte[] key, byte[] initVector, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec sKeySpec = new SecretKeySpec(key, CipherKeySpec);

            Cipher cipher = Cipher.getInstance(CipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] key, byte[]initVector, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec sKeySpec = new SecretKeySpec(key, CipherKeySpec);

            Cipher cipher = Cipher.getInstance(CipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);

            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
