package com.library.aitd;

import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.signed.SignedTransaction;
import com.ripple.core.types.known.tx.txns.Payment;
import com.ripple.crypto.ecdsa.Seed;

import java.util.List;

/**
 * 用户接口（注册登录） 单元测试
 */
public class Crypto_UnitTest extends BaseUnitTest {


    @Override
    protected void init() {
        AitdOpenApi.initSDK("https://s1.ripple.com:51234");
        AitdOpenApi.switchCoinModeXRP();
    }


    @Override
    protected void proceed() {
        List<String> mnemonicList2 = AitdOpenApi.Wallet.createRandomMnemonic();
        Seed seed2 = AitdOpenApi.Wallet.createSeedFromMnemonic(mnemonicList2);

        List<String> mnemonicList = AitdOpenApi.Wallet.createRandomMnemonic();
        Seed seed = AitdOpenApi.Wallet.createSeedFromMnemonic(mnemonicList);
        LogRipple.print(seed.toString());

        byte[] bytes = seed.keyPair().signMessage("你好".getBytes());
        boolean res = seed.keyPair().verifySignature("你好".getBytes(), bytes);

        LogRipple.print("verifySignature: "+ res);

        if (true){
            return;
        }
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


       // onTestFinish(true);
    }


}