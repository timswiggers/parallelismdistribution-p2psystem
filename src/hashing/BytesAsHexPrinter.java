package hashing;

import java.io.PrintStream;

public final class BytesAsHexPrinter {

    public static String toString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static void print(byte[] bytes, PrintStream out){
        out.print(toString(bytes) + "\n");
    }
}
