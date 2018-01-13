package rebue.wheel.turing;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;

public class DigestUtils {
    // 各种摘要的算法名称
    public static final String ALGORITHM_MD5    = "MD5";
    public static final String ALGORITHM_SHA1   = "SHA-1";
    public static final String ALGORITHM_SHA224 = "SHA-224";
    public static final String ALGORITHM_SHA256 = "SHA-256";
    public static final String ALGORITHM_SHA384 = "SHA-384";
    public static final String ALGORITHM_SHA512 = "SHA-512";

    /**
     * 摘要的核心算法
     */
    public static byte[] digest(String algorithm, byte[] data) {
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
     * 将摘要的结果(byte[])转成16进制的字符串
     */
    public static String digestAsHexStr(String algorithm, byte[] data) {
        return BytesUtils.toHexString(digest(algorithm, data));
    }

    /**
     * MD4
     */
    public static byte[] md4(byte[] data) {
        return Md4Utils.digest(data);
    }

    public static String md4AsHexStr(byte[] data) {
        return Md4Utils.digestAsHexStr(data);
    }

    /**
     * MD5
     *
     * @return MD5的结果是128位bit，也就是16个字节，如果转换成16进制的字符串为32个char长度的字符串
     */
    public static byte[] md5(byte[] data) {
        return digest(ALGORITHM_MD5, data);
    }

    /**
     * MD5 输出32个char长度的字符串
     *
     * @return 输出32个char长度的16进制字符串
     */
    public static String md5AsHexStr(byte[] data) {
        return md5AsHexStrX32(data);
    }

    /**
     * MD5 输出32个char长度的字符串
     *
     * @return String 输出32个char长度的16进制字符串
     */
    public static String md5AsHexStrX32(byte[] data) {
        return digestAsHexStr(ALGORITHM_MD5, data);
    }

    /**
     * MD5 输出16个char长度的字符串
     *
     * @return String 输出16个char长度的字符串
     */
    public static String md5AsHexStrX16(byte[] data) {
        return md5AsHexStrX32(data).substring(8, 24);
    }

    public static String shaAsHexStr(byte[] data) {
        return sha1AsHexStr(data);
    }

    public static String sha1AsHexStr(byte[] data) {
        return digestAsHexStr(ALGORITHM_SHA1, data);
    }

    public static String sha224AsHexStr(byte[] data) {
        return digestAsHexStr(ALGORITHM_SHA224, data);
    }

    public static String sha256AsHexStr(byte[] data) {
        return digestAsHexStr(ALGORITHM_SHA256, data);
    }

    public static String sha384AsHexStr(byte[] data) {
        return digestAsHexStr(ALGORITHM_SHA384, data);
    }

    public static String sha512AsHexStr(byte[] data) {
        return digestAsHexStr(ALGORITHM_SHA512, data);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(Security.getProviders()));
        System.out.println("111111 MD4   :" + DigestUtils.md4AsHexStr("111111".getBytes()));
        System.out.println("111111 MD5 16:" + DigestUtils.md5AsHexStrX16("111111".getBytes()));
        System.out.println("111111 MD5 32:" + DigestUtils.md5AsHexStrX32("111111".getBytes()));
        System.out.println("111111 SHA1  :" + DigestUtils.sha1AsHexStr("111111".getBytes()));
        System.out.println("111111 SHA224:" + DigestUtils.sha224AsHexStr("111111".getBytes()));
        System.out.println("111111 SHA256:" + DigestUtils.sha256AsHexStr("111111".getBytes()));
        System.out.println("111111 SHA384:" + DigestUtils.sha384AsHexStr("111111".getBytes()));
        System.out.println("111111 SHA512:" + DigestUtils.sha512AsHexStr("111111".getBytes()));
    }

}
