package common;

import peers.PeerInfo;
import peers.PeerMapper;

import java.io.DataInputStream;
import java.io.IOException;

public class Request {
    private final DataInputStream in;

    public Request(DataInputStream in) {
        this.in = in;
    }

    public int getIntParameter() throws IOException {
        return in.readInt();
    }

    public String getStringParameter() throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return new String(bytes);
    }

    public byte[] getBytesParameter() throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return bytes;
    }

    public PeerInfo getPeerParameter() throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return PeerMapper.fromBytes(bytes);
    }
}
