package hashing;

import org.apache.commons.lang3.ArrayUtils;

import java.security.*;
import java.util.Arrays;
import java.util.concurrent.*;

public class SHA256MerkleBytesHasher implements BytesHasher {
    public static final String HashAlgorithm = "SHA-256";

    private final int granularity;
    private final boolean parallelize;

    private final ForkJoinPool pool;

    public SHA256MerkleBytesHasher(int granularity, boolean parallelize, int parallelism) {
        this.granularity = granularity;
        this.parallelize = parallelize;
        pool = new ForkJoinPool(parallelism);
    }
    public SHA256MerkleBytesHasher() {
        this.granularity = 100 * 1000;
        this.parallelize = true;
        pool = ForkJoinPool.commonPool();
    }

    @Override
    public byte[] hash(byte[] bytes) throws NoSuchAlgorithmException {
        return pool.invoke(new HashTask(bytes, 0, bytes.length-1, getDigestInstance(), Math.max(1, granularity), parallelize));
    }

    class HashTask extends RecursiveTask<byte[]> {

        private final byte[] bytes;
        private final int low;
        private final int high;
        private final MessageDigest digest;
        private final int cutOff;
        private final boolean parallelize;

        public HashTask(byte[] bytes, int low, int high, MessageDigest digest, int cutOff, boolean parallelize) {
            this.bytes = bytes;
            this.low = low;
            this.high = high;
            this.digest = digest;
            this.cutOff = cutOff;
            this.parallelize = parallelize;
        }

        protected byte[] compute() {
            try {
                return computeHash();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new byte[0];
            }
        }

        private byte[] computeHash() throws NoSuchAlgorithmException {
            if(high - low < cutOff) {
                byte[] slice = Arrays.copyOfRange(bytes, low, high);
                return digest.digest(slice);
            }

            // Use the MessageDigest instance from this node already in the left node, we can safe the construction cost
            // this way. We have to reset it before we use it for ourselves though.
            HashTask leftTask = new HashTask(bytes, low, (high+low)/2, digest, cutOff, parallelize);
            HashTask rightTask = new HashTask(bytes, (high+low)/2, high, SHA256MerkleBytesHasher.getDigestInstance(), cutOff, parallelize);

            byte[] rightHash;
            byte[] leftHash;

            if(parallelize) {
                leftTask.fork();
                rightHash = rightTask.compute();
                leftHash = leftTask.join();
            } else {
                leftHash = leftTask.compute();
                rightHash = rightTask.compute();
            }

            byte[] combinedHash = ArrayUtils.addAll(rightHash, leftHash);

            digest.reset(); // We reset the digest because it was used in the left branch already

            return digest.digest(combinedHash);
        }
    }

    private static MessageDigest getDigestInstance() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(HashAlgorithm);
    }
}
