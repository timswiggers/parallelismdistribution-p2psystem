package userclient.commands;

import userclient.UserInteraction;
import hashing.BytesAsHexPrinter;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by timsw on 07/05/2016.
 */
public class HashCommand implements Command {
    private final FileAccess files;

    public HashCommand(FileAccess files) {
        this.files = files;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executeHash(user);
        } catch (IOException e) {
            user.sayError(e);
        } catch (NoSuchAlgorithmException e) {
            user.sayError(e);
        }
    }

    private void executeHash(UserInteraction user) throws IOException, NoSuchAlgorithmException {
        String fileName = user.askForValue("filename", "383MB.exe");
        if(fileName == null) {
            return;
        }

        byte[] bytes = files.getFileBytes(fileName);
        if(bytes == null || bytes.length < 0) {
            user.say(String.format("File '%s' was not found.", fileName));
            return;
        }

        int granularity = Integer.parseInt(user.askForValue("granularity", "100"));
        boolean parallelize = user.askYesNoQuestion("parallelize the algorithm");

        BytesHasher hasher = new SHA256MerkleBytesHasher(granularity, parallelize);

        Instant startTime = Instant.now();
        byte[] hash = hasher.hash(bytes);
        Instant endTime = Instant.now();

        Duration totalTime = Duration.between(startTime, endTime);

        user.say(String.format("%s (%s)", BytesAsHexPrinter.toString(hash), toPrettyString(totalTime)));
    }

    private String toPrettyString(Duration duration){
        return String.format("%s.%s seconds", duration.getSeconds(), duration.getNano());
    }
}
