package rebue.wheel.turing;

import java.security.MessageDigest;

public class HashUtils {
    private static final String ALGORITHM_MD5  = "MD5";
    private static final String ALGORITHM_SHA1 = "SHA1";

    /**
     * getHashByAlgorithm byte[]
     *
     * @param data
     * @param algorithm
     * @return byte[]
     */
    public static byte[] getHashByAlgorithm(byte[] data, String algorithm) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * getHexStrByAlgorithm string
     *
     * @param data
     * @param algorithm
     * @return String
     */
    public static String getHexStrByAlgorithm(byte[] data, String algorithm) {
        return BytesUtils.toHexString(getHashByAlgorithm(data, algorithm));
    }

    /**
     * getHashByMd5 By MD5 输出32个char长度的字符串
     *
     * @param data
     * @return String 输出32个char长度的字符串
     */
    public static byte[] getHashByMd5(byte[] data) {
        return getHashByAlgorithm(data, ALGORITHM_MD5);
    }

    /**
     * getHexStrByAlgorithm By MD5 输出32个char长度的字符串
     *
     * @param data
     * @return String 输出32个char长度的字符串
     */
    public static String getHashHexStrByMd5x32(byte[] data) {
        return getHexStrByAlgorithm(data, ALGORITHM_MD5);
    }

    public static String getHashHexStrByMd5x32(String data) {
        return getHashHexStrByMd5x32(data.getBytes());
    }

    /**
     * getHexStrByAlgorithm By MD5 输出16个char长度的字符串
     *
     * @param data
     * @return String 输出16个char长度的字符串
     */
    public static String getHexStrByMd5x16(byte[] data) {
        return getHashHexStrByMd5x32(data).substring(8, 24);
    }

    public static String getHexStrByMd5x16(String data) {
        return getHexStrByMd5x16(data.getBytes());
    }

    /**
     * getHexStrByAlgorithm By MD5
     *
     * @param data
     * @return String
     */
    public static String getHashBySha1(byte[] data) {
        return getHexStrByAlgorithm(data, ALGORITHM_SHA1);
    }

    public static String getHashBySha1(String data) {
        return getHashBySha1(data.getBytes());
    }

    public static void main(String[] args) {
        System.out.println("111111 MD5 32:" + HashUtils.getHashHexStrByMd5x32("111111"));
        System.out.println("111111 MD5 16:" + HashUtils.getHexStrByMd5x16("111111"));
        System.out.println("111111 SHA1  :" + HashUtils.getHashBySha1("111111"));
    }

}
