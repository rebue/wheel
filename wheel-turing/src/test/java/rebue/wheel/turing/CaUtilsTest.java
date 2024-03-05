package rebue.wheel.turing;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;


@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CaUtilsTest {

    /**
     * 密钥库路径
     */
    private final static String           STORE_PATH     = "target/test.pfx";
    /**
     * 密钥库密码
     */
    private final static char[]           STORE_PASSWORD = "12345678".toCharArray();
    // 固定值，SM2签名的标识
    private final static byte[]           USER_ID        = "1234567812345678".getBytes(StandardCharsets.UTF_8);
    private final static SM2ParameterSpec sm2Params      = new SM2ParameterSpec(USER_ID);


    /**
     * 测试用RSA算法生成keypair并存储到密钥库中
     */
    @Test
    public void test01_genkeypair_rsa() {
        log.info("创建密钥对");
        String  encryptAlgorithm = "RSA";               // 加密算法
        String  signAlgorithm    = "SHA256withRSA";     // 签名算法
        KeyPair keyPair          = KeyUtils.generateKeyPair(encryptAlgorithm, 2048);

        log.info("创建私钥的证书链");
        X500Name privateSubject = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
        Date     now            = new Date();
        Calendar calendar       = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 365);
        Date endDate = calendar.getTime();
        KeyStoreUtils.putCert(keyPair, signAlgorithm, "test-rsa", privateSubject, now, endDate, new File(STORE_PATH), STORE_PASSWORD);
    }

    /**
     * 测试用ECDSA算法生成keypair并存储到密钥库中
     */
    @Test
    public void test02_genkeypair_ecdsa() {
        log.info("创建密钥对");
        String  encryptAlgorithm = "secp256k1";                 // 加密算法
        String  signAlgorithm    = "SHA256withECDSA";           // 签名算法
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair(encryptAlgorithm);

        log.info("创建私钥的证书链");
        X500Name privateSubject = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
        Date     now            = new Date();
        Calendar calendar       = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 365);
        Date endDate = calendar.getTime();
        KeyStoreUtils.putCert(keyPair, signAlgorithm, "test-ecdsa", privateSubject, now, endDate, new File(STORE_PATH), STORE_PASSWORD);
    }

    /**
     * 测试用SM2算法生成keypair并存储到密钥库中
     */
    @Test
    public void test03_genkeypair_sm2() {
        log.info("创建密钥对");
        String  encryptAlgorithm = "sm2p256v1";             // 加密算法
        String  signAlgorithm    = "SM3withSM2";            // 签名算法
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair(encryptAlgorithm);

        log.info("创建私钥的证书链");
        X500Name privateSubject = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
        Date     now            = new Date();
        Calendar calendar       = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 365);
        Date endDate = calendar.getTime();
        KeyStoreUtils.putCert(keyPair, signAlgorithm, "test-sm2", privateSubject, now, endDate, new File(STORE_PATH), STORE_PASSWORD);
    }

    /**
     * 测试打印密钥库里的key和entry
     */
    @Test
    public void test04_print_store_entries() {
        KeyStoreUtils.printEntries(new File(STORE_PATH), STORE_PASSWORD);
    }

    @Test
    @SneakyThrows
    public void test05_generate_csr_rsa() {
//        String                     signAlgorithm              = "SHA256withRSA";     // 签名算法
//        X500Name                   subject                    = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
//        KeyPair                    keyPair                    = KeyStoreUtils.getKeyPair("test-rsa", new File(STORE_PATH), STORE_PASSWORD);
//        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr(subject, signAlgorithm, keyPair, null);
        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr("test-rsa", new File(STORE_PATH), STORE_PASSWORD, null);
        log.info("生成CSR to BASE64: {}", Base64.getEncoder().encodeToString(pkcs10CertificationRequest.getEncoded()));
        log.info("生成CSR to PEM: {}", getPemStr(pkcs10CertificationRequest));
    }

    @Test
    @SneakyThrows
    public void test06_generate_csr_ecdsa() {
//        String                     signAlgorithm              = "SHA256withECDSA";     // 签名算法
//        X500Name                   subject                    = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
//        KeyPair                    keyPair                    = KeyStoreUtils.getKeyPair("test-ecdsa", new File(STORE_PATH), STORE_PASSWORD);
//        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr(subject, signAlgorithm, keyPair, null);
        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr("test-ecdsa", new File(STORE_PATH), STORE_PASSWORD, null);
        log.info("生成CSR to BASE64: {}", Base64.getEncoder().encodeToString(pkcs10CertificationRequest.getEncoded()));
        log.info("生成CSR to PEM: {}", getPemStr(pkcs10CertificationRequest));
    }

    @Test
    @SneakyThrows
    public void test07_generate_csr_sm2() {
//        String                     signAlgorithm              = "SM3withSM2";     // 签名算法
//        X500Name                   subject                    = new X500Name("CN=公用名称,OU=组织部门,O=组织名称,L=城市,ST=省份,C=CN");
//        KeyPair                    keyPair                    = KeyStoreUtils.getKeyPair("test-sm2", new File(STORE_PATH), STORE_PASSWORD);
//        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr(subject, signAlgorithm, keyPair, sm2Params);
        PKCS10CertificationRequest pkcs10CertificationRequest = CaUtils.generateCsr("test-sm2", new File(STORE_PATH), STORE_PASSWORD, sm2Params);
        log.info("生成CSR to BASE64: {}", Base64.getEncoder().encodeToString(pkcs10CertificationRequest.getEncoded()));
        log.info("生成CSR to PEM: {}", getPemStr(pkcs10CertificationRequest));
    }

    @SneakyThrows
    public static String getPemStr(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
             JcaPEMWriter pemWriter = new JcaPEMWriter(outputStreamWriter)) {
            pemWriter.writeObject(obj);
            pemWriter.flush();
            return byteArrayOutputStream.toString();
        }
    }


}
