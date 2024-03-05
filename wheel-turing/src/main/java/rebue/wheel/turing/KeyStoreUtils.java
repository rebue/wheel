package rebue.wheel.turing;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
public class KeyStoreUtils {
    static {
        // 添加BouncyCastle实现
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 获取密钥库
     *
     * @param storeFile     密钥库文件
     * @param storePassword 密钥库密码
     * @return 密钥库
     */
    public static KeyStore getKeyStore(File storeFile, char[] storePassword) {
        return getKeyStore(storeFile, storePassword, false);
    }

    /**
     * 获取密钥库
     *
     * @param storeFile      密钥库文件
     * @param storePassword  密钥库密码
     * @param initIfNotExist 如果文件不存在则直接初始化
     * @return 密钥库
     */
    public static KeyStore getKeyStore(File storeFile, char[] storePassword, boolean initIfNotExist) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            if (initIfNotExist && !storeFile.exists()) {
                keyStore.load(null, storePassword);
            } else {
                try (FileInputStream fileInputStream = new FileInputStream(storeFile)) {
                    keyStore.load(fileInputStream, storePassword);
                }
            }
            return keyStore;
        } catch (IOException e) {
            throw new RuntimeException("读取密钥库文件异常", e);
        } catch (CertificateException e) {
            throw new RuntimeException("加载证书异常", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持的算法", e);
        } catch (KeyStoreException | NoSuchProviderException e) {
            throw new RuntimeException("未加载BC库");
        }
    }

    @SneakyThrows
    public static void printEntries(File storeFile, char[] storePassword) {
        KeyStore            keyStore    = getKeyStore(storeFile, storePassword);
        Enumeration<String> enumeration = keyStore.aliases();
        log.info("开始打印KeyEntries");
        log.info("========================================");
        while (enumeration.hasMoreElements()) {
            String                       alias               = enumeration.nextElement();
            PrivateKey                   privateKey          = (PrivateKey) keyStore.getKey(alias, null);
            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(null);
            KeyStore.Entry               entry               = keyStore.getEntry(alias, protectionParameter);
            log.info("{}: \nkey: {}\nentry: {}", alias, privateKey, entry);
        }
        log.info("========================================");
    }

    /**
     * 存储证书到密钥库中
     *
     * @param keyPair       密钥对
     * @param signAlgorithm 签名算法(所谓签名其实就是先进行摘要，再对摘要进行加密，所以签名算法的名称一般为摘要算法名称+"with"+加密算法名称)
     * @param certAlias     存储证书的别名
     * @param subject       使用者()
     * @param notBefore     存储的有效期开始时间
     * @param notAfter      存储的有效期结束时间
     * @param storeFile     存储的密钥库文件
     * @param storePassword 密钥库的密码
     */
    @SneakyThrows
    public static void putCert(KeyPair keyPair, String signAlgorithm,
                               String certAlias, X500Name subject, Date notBefore, Date notAfter,
                               File storeFile, char[] storePassword) {
        log.debug("存储keypair到密钥库中");
        log.debug("生成存储key需要的证书链");
        Certificate[] certificates = new Certificate[]{
                CaUtils.generateCertificate(signAlgorithm, subject, subject, keyPair, notBefore, notAfter)
        };

        log.debug("存储私钥及其证书链到密钥库中");
        KeyStore keyStore = getKeyStore(storeFile, storePassword, true);
        keyStore.setKeyEntry(certAlias, keyPair.getPrivate(), null, certificates);
        try (FileOutputStream fileOutputStream = new FileOutputStream(storeFile)) {
            keyStore.store(fileOutputStream, storePassword);
        }
    }

    @SneakyThrows
    public static KeyPair getKeyPair(String certAlias, File storeFile, char[] storePassword) {
        KeyStore keyStore = getKeyStore(storeFile, storePassword);
        return getKeyPair(certAlias, keyStore);
    }

    @SneakyThrows
    public static KeyPair getKeyPair(String certAlias, KeyStore keyStore) {
        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(certAlias, keyStore);
        PublicKey                publicKey       = privateKeyEntry.getCertificate().getPublicKey();
        PrivateKey               privateKey      = privateKeyEntry.getPrivateKey();
        return new KeyPair(publicKey, privateKey);
    }

    @SneakyThrows
    public static KeyStore.PrivateKeyEntry getPrivateKeyEntry(String certAlias, KeyStore keyStore) {
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(null);
        KeyStore.Entry               entry               = keyStore.getEntry(certAlias, protectionParameter);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new KeyStoreException("That's not a private key!");
        }
        return (KeyStore.PrivateKeyEntry) entry;
    }

    public static void importCert(String certAlias, InputStream certInputStream, File storeFile, char[] storePassword) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
        log.debug("存储证书到密钥库中");
        KeyStore keyStore = getKeyStore(storeFile, storePassword, true);
//        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(certAlias, keyStore);
//        PrivateKey               privateKey      = privateKeyEntry.getPrivateKey();
////        Certificate              innerCertificate   = privateKeyEntry.getCertificate();
        KeyPair            keyPair            = KeyUtils.generateKeyPair("RSA", 2048);
        PrivateKey         privateKey         = keyPair.getPrivate();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
        String             text;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(certInputStream))) {
            StringBuilder sb = new StringBuilder();
            String        line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            text = sb.toString();
        }
        if (!text.startsWith("-----")) {
//            text = "-----BEGIN CERTIFICATE-----\n" + text + "\n-----END CERTIFICATE-----";
            certInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(text));
        }
        Collection<? extends Certificate> outerCertificates = certificateFactory.generateCertificates(certInputStream);
//        Certificate[]                     certificates      = new Certificate[]{innerCertificate};
        Certificate[] certificates = new Certificate[outerCertificates.size()];
        outerCertificates.toArray(certificates);
        keyStore.setKeyEntry(certAlias, privateKey, storePassword, certificates);
        try (FileOutputStream fileOutputStream = new FileOutputStream(storeFile)) {
            keyStore.store(fileOutputStream, storePassword);
        }
    }


}
