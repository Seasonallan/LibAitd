package com.library.aitd;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.utils.Base58;
import com.ripple.core.binary.STReader;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Blob;
import com.ripple.core.coretypes.Currency;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.fields.Field;
import com.ripple.core.serialized.BinaryParser;
import com.ripple.core.types.known.tx.Transaction;
import com.ripple.core.types.known.tx.signed.SignedTransaction;
import com.ripple.core.types.known.tx.txns.Payment;
import com.ripple.core.types.ledger.LedgerHeader;
import com.ripple.crypto.ecdsa.ECDSASignature;
import com.ripple.crypto.ecdsa.SECP256K1;
import com.ripple.crypto.ecdsa.Seed;
import com.ripple.utils.HashUtils;
import com.ripple.utils.Sha512;
import com.ripple.utils.Utils;
import com.ripple.word.MnemonicGenerator;
import com.ripple.word.RandomSeed;
import com.ripple.word.SeedCalculator;
import com.ripple.word.WordCount;
import com.ripple.word.wordlists.English;
import com.tqxd.btc.BtcOpenApi;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECPoint;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用户接口（注册登录） 单元测试
 */
public class Crypto_UnitTest extends BaseUnitTest {


    @Override
    protected void init() {
        AitdOpenApi.initSDK("https://s1.ripple.com:51234");
        AitdOpenApi.switchCoinModeXRP();
    }

    private void decode(){

        //助记词生成种子。

        byte[] random = RandomSeed.random(WordCount.TWELVE);
        List<String> cc = new MnemonicGenerator(English.INSTANCE).createMnemonic(random);
        cc.clear();
        String name = "property, energy, ladder, imitate, report, ripple, wish, dove, someone, loud, tongue, old";
        String[] ss = name.split(", ");
        cc = Arrays.asList(ss);

        LogRipple.printForce("助记词:" + cc);

        byte[] seed = new SeedCalculator().calculateSeed(cc, "");

        ECKeyPair ecKeyPair = BtcOpenApi.Wallet.createFromMnemonic(cc);
        BigInteger privateGen = generateKey(seed, null);
        byte[] publicKey = SECP256K1.basePointMultipliedBy(privateGen);
        BigInteger publicGen = uBigInt(publicKey);


        LogRipple.printForce("私钥:" + ecKeyPair.getPrivateKey());
        LogRipple.printForce("公钥:" + ecKeyPair.getPublicKey());


        //加密
        String content = "hello world!";

        byte[] hash = content.getBytes();
        //ECDSASignature sig = createECDSASignature(hash, ecKeyPair.priv);
        byte[] sigBytes = new byte[0];//sig.encodeToDER();
        try {
            sigBytes = ecKeyPair.sign(hash);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        LogRipple.printForce("加密长度:" + sigBytes.length);

        //验证加密
        ECDSASignature signature = ECDSASignature.decodeFromDER(sigBytes);
        ECDSASigner signer = new ECDSASigner();
        ECPoint pubPoint = SECP256K1.curve().decodePoint(ecKeyPair.getRawPublicKey());
        ECPublicKeyParameters params = new ECPublicKeyParameters(pubPoint, SECP256K1.params());
        signer.init(false, params);
        boolean result = signer.verifySignature(hash, signature.r, signature.s);

        LogRipple.printForce("加密验证:" + (result ? "成功" : "失败"));
    }



    private static ECDSASignature createECDSASignature(byte[] hash, BigInteger secret) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(secret, SECP256K1.params());
        signer.init(true, privKey);
        BigInteger[] sigs = signer.generateSignature(hash);
        BigInteger r = sigs[0], s = sigs[1];

        BigInteger otherS = SECP256K1.order().subtract(s);
        if (s.compareTo(otherS) == 1) {
            s = otherS;
        }

        return new ECDSASignature(r, s);
    }


    public static BigInteger uBigInt(byte[] bytes) {
        return new BigInteger(1, bytes);
    }

    /**
     * @param seedBytes     - a bytes sequence of arbitrary length which will be hashed
     * @param discriminator - nullable optional uint32 to hash
     * @return a number between [1, order -1] suitable as a private key
     */
    public static BigInteger generateKey(byte[] seedBytes, Integer discriminator) {
        BigInteger key = null;
        for (long i = 0; i <= 0xFFFFFFFFL; i++) {
            Sha512 sha512 = new Sha512().add(seedBytes);
            if (discriminator != null) {
                sha512.addU32(discriminator);
            }
            sha512.addU32((int) i);
            byte[] keyBytes = sha512.finish256();
            key = Utils.uBigInt(keyBytes);
            if (key.compareTo(BigInteger.ZERO) == 1 &&
                    key.compareTo(SECP256K1.order()) == -1) {
                break;
            }
        }
        return key;
    }

    @Override
    protected void proceed() {
        if (true){
            decode();
            return;
        }

        List<String> mnemonicList2 = AitdOpenApi.Wallet.createRandomMnemonic();
        Seed seed2 = AitdOpenApi.Wallet.createSeedFromMnemonic(mnemonicList2);

        List<String> mnemonicList = AitdOpenApi.Wallet.createRandomMnemonic();
        Seed seed = AitdOpenApi.Wallet.createSeedFromMnemonic(mnemonicList);
        LogRipple.print(seed.toString());

        byte[] bytes = seed.keyPair().signMessage("你好".getBytes());
        boolean res = seed.keyPair().verifySignature("你好".getBytes(), bytes);

        LogRipple.print("verifySignature: " + res);


        Payment payment = new Payment();
        payment.as(AccountID.Account, AitdOpenApi.Wallet.getAddress(seed.toString()));
        payment.as(AccountID.Destination, AitdOpenApi.Wallet.getAddress(seed2.toString()));
        payment.as(UInt32.DestinationTag, "10086");
        payment.as(Amount.Amount, "411000000");
        payment.as(UInt32.Sequence, "1");
        payment.as(UInt32.LastLedgerSequence, "1");
        payment.as(Amount.Fee, "12222");
        SignedTransaction signed = payment.sign(seed.toString());
        String blob = signed.tx_blob;

        STReader reader = new STReader(blob);
        Payment paymentDecode = new Payment();

        //TODO:此处顺序需要调整
        paymentDecode.put(Field.TransactionType.TransactionIndex, reader.uInt16());
        paymentDecode.put(UInt32.Flags, reader.uInt32());
        paymentDecode.put(UInt32.Sequence, reader.uInt32());
        paymentDecode.put(UInt32.DestinationTag, reader.uInt32());
        paymentDecode.put(UInt32.LedgerSequence, reader.uInt32());
        paymentDecode.put(Amount.Amount, reader.amount());
        paymentDecode.put(Amount.Fee, reader.amount());


        LogRipple.print("verifySignature: " + paymentDecode.toJSON());

        onTestFinish(true);
    }


}