package userclient.commands;

import userclient.UserInteraction;
import hashing.BytesAsHex;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class HashCommand implements Command {
    private final FileAccess files;

    public HashCommand(FileAccess files) {
        this.files = files;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executeHash(user);
        } catch (IOException | NoSuchAlgorithmException e) {
            user.sayError(e);
        }
    }

    private void executeHash(UserInteraction user) throws IOException, NoSuchAlgorithmException {
        String fileName = user.askForValue("filename", "..\\136MB.pdf");
        if(fileName == null) {
            return;
        }

        byte[] bytes = files.getFileBytes(fileName);
        if(bytes == null || bytes.length < 0) {
            user.say(String.format("File '%s' was not found.", fileName));
            return;
        }

        int granularity = Integer.parseInt(user.askForValue("granularity", "100000"));
        int parallelism = Integer.parseInt(user.askForValue("parallelism", "4"));
        boolean parallelize = user.askYesNoQuestion("Parallelize the algorithm");
        int times = Integer.parseInt(user.askForValue("how many times", "10"));

        BytesHasher hasher = new SHA256MerkleBytesHasher(granularity, parallelize, parallelism);

        Instant startTime = Instant.now();
        byte[] hash = null;

        for(int i = 0; i < times; i++) {
            hash = hasher.hash(bytes);
        }

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        user.say(String.format("%s (%s, avg of %d run(s))", BytesAsHex.toString(hash), toPrettyString(totalTime.dividedBy(times)), times));
    }

    private String toPrettyString(Duration duration){
        return String.format("%s.%s seconds", duration.getSeconds(), duration.getNano());
    }
}
