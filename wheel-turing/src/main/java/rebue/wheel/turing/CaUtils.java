package rebue.wheel.turing;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.UUID;


@Slf4j
public class CaUtils {
    static {
        // 添加BouncyCastle实现
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 根据密钥的算法标识返回对应签名算法标识
     *
     * @param algorithmIdentifier 密钥的算法标识
     * @return 签名算法标识
     */
    public static AlgorithmIdentifier getSignAlgorithmIdentifier(AlgorithmIdentifier algorithmIdentifier) {
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE);
        } else if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey) && algorithmIdentifier.getParameters().equals(GMObjectIdentifiers.sm2p256v1)) {
            return new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sm3, DERNull.INSTANCE);
        } else if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey) && algorithmIdentifier.getParameters().equals(SECObjectIdentifiers.secp256k1)) {
            return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256);
        } else {
            throw new IllegalArgumentException("不支持的密钥算法: " + algorithmIdentifier.getAlgorithm());
        }
    }

    /**
     * 生成证书
     *
     * @param signAlgorithm 签名算法(所谓签名其实就是先进行摘要，再对摘要进行加密，所以签名算法的名称一般为摘要算法名称+"with"+加密算法名称)
     * @param issuer        颁发者
     * @param subject       使用者
     * @param keyPair       密钥对
     * @param notBefore     存储的有效期开始时间
     * @param notAfter      存储的有效期结束时间
     * @return 证书
     */
    @SneakyThrows
    public static Certificate generateCertificate(String signAlgorithm,
                                                  X500Name issuer, X500Name subject,
                                                  KeyPair keyPair, Date notBefore, Date notAfter) {
        PrivateKey           privateKey           = keyPair.getPrivate();
        PublicKey            publicKey            = keyPair.getPublic();
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                issuer,
                BigInteger.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE),
                notBefore,
                notAfter,
                subject,
                subjectPublicKeyInfo
        );
        BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
        certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));    // ca cert 如果false为实体证书
        certificateBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        certificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(subjectPublicKeyInfo)); //授权密钥标识
        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));     //使用者密钥标识

        // 构建签名器
        ContentSigner signer = new JcaContentSignerBuilder(signAlgorithm)
                .setProvider("BC")
                .build(privateKey);

        // 使用CA私钥签署证书
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certificateHolder);
    }

    /**
     * 生成PKCS10格式的证书签名请求
     *
     * @param certAlias     存储证书的别名
     * @param storeFile     存储的密钥库文件
     * @param storePassword 密钥库的密码
     * @param initParams    签名算法的初始化参数
     * @return PKCS10格式的证书签名请求
     */
    @SneakyThrows
    public static PKCS10CertificationRequest generateCsr(String certAlias, File storeFile, char[] storePassword, AlgorithmParameterSpec initParams) {
        KeyStore                 keyStore                = KeyStoreUtils.getKeyStore(storeFile, storePassword);
        KeyStore.PrivateKeyEntry privateKeyEntry         = KeyStoreUtils.getPrivateKeyEntry(certAlias, keyStore);
        PrivateKey               privateKey              = privateKeyEntry.getPrivateKey();
        X509Certificate          certificate             = (X509Certificate) privateKeyEntry.getCertificate();
        PublicKey                publicKey               = certificate.getPublicKey();
        X500Principal            subjectX500Principal    = certificate.getSubjectX500Principal();
        X500Name                 subject                 = X500Name.getInstance(subjectX500Principal.getEncoded());
        SubjectPublicKeyInfo     subjectPublicKeyInfo    = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        CertificationRequestInfo csrInfo                 = new CertificationRequestInfo(subject, subjectPublicKeyInfo, new DERSet());
        byte[]                   data                    = csrInfo.getEncoded(ASN1Encoding.DER);
        AlgorithmIdentifier      signAlgorithmIdentifier = getSignAlgorithmIdentifier(subjectPublicKeyInfo.getAlgorithm());
        byte[]                   sign                    = SignUtils.sign(certificate.getSigAlgName(), privateKey, data, initParams);
        return new PKCS10CertificationRequest(new CertificationRequest(csrInfo, signAlgorithmIdentifier, new DERBitString(sign)));
    }

//    /**
//     * 生成PKCS10格式的证书签名请求
//     *
//     * @param subject       使用者
//     * @param signAlgorithm 签名算法(所谓签名其实就是先进行摘要，再对摘要进行加密，所以签名算法的名称一般为摘要算法名称+"with"+加密算法名称)
//     * @param keyPair       密钥对
//     * @param initParams    签名算法的初始化参数
//     * @return PKCS10格式的证书签名请求
//     */
//    @SneakyThrows
//    public static PKCS10CertificationRequest generateCsr(X500Name subject, String signAlgorithm, KeyPair keyPair, AlgorithmParameterSpec initParams) {
//        PrivateKey privateKey = keyPair.getPrivate();
//        PublicKey  publicKey  = keyPair.getPublic();
//
//        SubjectPublicKeyInfo     subjectPublicKeyInfo    = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
//        CertificationRequestInfo info                    = new CertificationRequestInfo(subject, subjectPublicKeyInfo, new DERSet());
//        byte[]                   data                    = info.getEncoded(ASN1Encoding.DER);
//        AlgorithmIdentifier      signAlgorithmIdentifier = getSignAlgorithmIdentifier(subjectPublicKeyInfo.getAlgorithm());
//        byte[]                   sign                    = SignUtils.sign(signAlgorithm, privateKey, data, initParams);
//        return new PKCS10CertificationRequest(new CertificationRequest(info, signAlgorithmIdentifier, new DERBitString(sign)));
//    }
}
