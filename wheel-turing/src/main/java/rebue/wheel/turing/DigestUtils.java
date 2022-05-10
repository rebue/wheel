package rebue.wheel.turing;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * 摘要算法(使用BouncyCastle实现算法)
 *
 * @author zbz
 *
 */
public class DigestUtils {
    static {
        // 使用BouncyCastle实现
        Security.addProvider(new BouncyCastleProvider());
    }

    // 各种摘要的算法名称
    public static final String ALGORITHM_SM3    = "SM3";
    public static final String ALGORITHM_MD4    = "MD4";
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
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将摘要的结果(byte[])转成16进制的字符串
     */
    public static String digestToHexStr(String algorithm, byte[] data) {
        return Hex.toHexString(digest(algorithm, data));
    }

    /**
     * SM3
     */
    public static byte[] sm3(byte[] data) {
        return digest(ALGORITHM_SM3, data);
    }

    /**
     * SM3
     */
    public static String sm3ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SM3, data);
    }

    /**
     * MD4
     */
    public static byte[] md4(byte[] data) {
        return digest(ALGORITHM_MD4, data);
    }

    /**
     * MD4
     */
    public static String md4ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_MD4, data);
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
    public static String md5ToHexStr(byte[] data) {
        return md5ToHexStrX32(data);
    }

    /**
     * MD5 输出32个char长度的字符串
     *
     * @return String 输出32个char长度的16进制字符串
     */
    public static String md5ToHexStrX32(byte[] data) {
        return digestToHexStr(ALGORITHM_MD5, data);
    }

    /**
     * MD5 输出16个char长度的字符串
     *
     * @return String 输出16个char长度的字符串
     */
    public static String md5ToHexStrX16(byte[] data) {
        return md5ToHexStrX32(data).substring(8, 24);
    }

    public static String shaToHexStr(byte[] data) {
        return sha1ToHexStr(data);
    }

    public static String sha1ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SHA1, data);
    }

    public static String sha224ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SHA224, data);
    }

    public static String sha256ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SHA256, data);
    }

    public static String sha384ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SHA384, data);
    }

    public static String sha512ToHexStr(byte[] data) {
        return digestToHexStr(ALGORITHM_SHA512, data);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(Security.getProviders()));
        System.out.println("111111 MD4   :" + DigestUtils.md4ToHexStr("111111".getBytes()));
        System.out.println("111111 MD5 16:" + DigestUtils.md5ToHexStrX16("111111".getBytes()));
        System.out.println("111111 MD5 32:" + DigestUtils.md5ToHexStrX32("111111".getBytes()));
        System.out.println("111111 SHA1  :" + DigestUtils.sha1ToHexStr("111111".getBytes()));
        System.out.println("111111 SHA224:" + DigestUtils.sha224ToHexStr("111111".getBytes()));
        System.out.println("111111 SHA256:" + DigestUtils.sha256ToHexStr("111111".getBytes()));
        System.out.println("111111 SHA384:" + DigestUtils.sha384ToHexStr("111111".getBytes()));
        System.out.println("111111 SHA512:" + DigestUtils.sha512ToHexStr("111111".getBytes()));
    }

}
