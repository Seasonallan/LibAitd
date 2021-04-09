package com.library.aitd;

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

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

    }

}