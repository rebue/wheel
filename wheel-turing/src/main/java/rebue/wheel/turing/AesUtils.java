package rebue.wheel.turing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class AesUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String KEY_ALGORITHM    = "AES";
    // public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    public static final String CIPHER_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";

    /**
     * 根据密码和盐值获得Key
     */
    public static byte[] genKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // get raw key from password and salt
        PBEKeySpec       pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec    secretKey  = new SecretKeySpec(keyFactory.generateSecret(pbeKeySpec).getEncoded(), KEY_ALGORITHM);
        return secretKey.getEncoded();

    }

    /**
     * 获得随机生成的Key
     */
    public static byte[] genKey(int keySize) throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AesUtils.KEY_ALGORITHM, "BC");
        keyGenerator.init(keySize);
        return keyGenerator.generateKey().getEncoded();
    }

    public static byte[] encrypt(byte[] key, byte[] toEncrypt)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
        return cipher.doFinal(toEncrypt);
    }

    public static byte[] decrypt(byte[] key, byte[] encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
        return cipher.doFinal(encrypted);
    }

}
