package hashing;

import java.io.PrintStream;

public final class BytesAsHex {

    public static byte[] toBytes(String string){
        int len = string.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i+1), 16));
        }
        return data;
    }

    public static String toString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static void println(byte[] bytes, PrintStream out){
        out.print(toString(bytes) + "\n");
    }
}
