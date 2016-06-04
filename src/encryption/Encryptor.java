package encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Credits: http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String Encoding = "UTF-8";
    private static final String CipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String CipherKeySpec = "AES";

    public static byte[] encrypt(String key, String initVector, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(Encoding));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(Encoding), CipherKeySpec);

            Cipher cipher = Cipher.getInstance(CipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(String key, String initVector, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(Encoding));
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
