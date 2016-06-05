package common;

import java.io.DataOutputStream;
import java.io.IOException;

public class Response {
    private final DataOutputStream out;

    public Response(DataOutputStream out){
        this.out = out;
    }

    public void success() throws IOException {
        out.writeInt(ResponseCode.Success.ordinal());
        out.flush();
    }

    public void waiting() throws IOException {
        out.writeInt(ResponseCode.Waiting.ordinal());
        out.flush();
    }

    public void error(String message) throws IOException {
        out.writeInt(ResponseCode.Error.ordinal());
        out.writeInt(message.length());
        out.writeBytes(message);
        out.flush();
    }

    public void file(byte[] bytes) throws IOException {
        out.writeInt(ResponseCode.File.ordinal());
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }
}
