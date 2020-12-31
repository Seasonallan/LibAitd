package com.example.aitd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.key.Key;
import com.library.aitd.AitdOpenApi;
import com.library.aitd.LogRipple;
import com.library.aitd.bean.XRPAccount;
import com.library.aitd.bean.XRPFee;
import com.library.aitd.bean.XRPLedger;
import com.library.aitd.bean.XRPTransaction;
import com.library.aitd.bean.transaction.XRPAccount_Transaction;
import com.library.aitd.bean.transaction.XRPAccount_TransactionList;

public class MainActivity extends AppCompatActivity {

    TextView log;

    boolean aitd = false;
    private void changeMode(){
        if (aitd) {
            setTitle("测试AITD链功能");
            //设置为AITD的base58规则
            AitdOpenApi.initSDK(Key.DOMAIN_AITD);
            AitdOpenApi.switchCoinModeAITD();
        } else {
            setTitle("测试xrp链功能");
            //设置为XRP的base58规则
            AitdOpenApi.initSDK(Key.DOMAIN_XRP);
            AitdOpenApi.switchCoinModeXRP();
        }

        Key.checkTestAccount();

        fillContent("测试公钥（转账到该账户）：" + Key.testPublicKey);
        fillContent("测试私钥（拥有者）：" + Key.testPrivateKey);
        fillContent("测试公钥（拥有者）：" + AitdOpenApi.Wallet.getAddress(Key.testPrivateKey));
        fillContent("当前使用网络：" + AitdOpenApi.DOMAIN);
        fillContent("当前配置：");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = findViewById(R.id.log);

        changeMode();

        findViewById(R.id.mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aitd = !aitd;
                changeMode();
            }
        });
        findViewById(R.id.fee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XRPFee xrpFee = AitdOpenApi.Request.getFee();
                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ledger_current_index：" + xrpFee.ledger_current_index);
                        stringBuffer.append("\nbase_fee：" + xrpFee.drops.base_fee);
                        stringBuffer.append("\nopen_ledger_fee：" + xrpFee.drops.open_ledger_fee);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillContent(stringBuffer.toString());
                            }
                        });
                    }
                }).start();
            }
        });
        findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XRPAccount xrpAccount = AitdOpenApi.Request.getAccountInfo(AitdOpenApi.Wallet.getAddress(Key.testPrivateKey));
                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("当前余额：" + xrpAccount.account_data.Balance);
                        stringBuffer.append("\n当前序列：" + xrpAccount.account_data.Sequence);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillContent(stringBuffer.toString());
                            }
                        });
                    }
                }).start();
            }
        });

        findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XRPAccount_TransactionList xrpAccountTransactionList = AitdOpenApi.Request.getTransactionList(AitdOpenApi.Wallet.getAddress(Key.testPrivateKey),
                                2, -1, -1);
                        final StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < xrpAccountTransactionList.transactions.size(); i++) {
                            XRPAccount_Transaction item = xrpAccountTransactionList.transactions.get(i);
                            stringBuffer.append("转账 " + item.tx.Amount + " 到地址：" + item.tx.Destination);
                            stringBuffer.append("\n时间：" + item.tx.getTime());
                            stringBuffer.append("\n序列：" + item.tx.Sequence);
                            stringBuffer.append("\n-----------------\n");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillContent(stringBuffer.toString());
                            }
                        });
                    }
                }).start();
            }
        });

        findViewById(R.id.transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XRPTransaction xrpTransaction = AitdOpenApi.Request.transaction(Key.testPrivateKey,
                                Key.testPublicKey, "100", "250");

                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("交易结果：" + xrpTransaction.engine_result_message);
                        if (xrpTransaction.isSuccess()) {
                            stringBuffer.append("\n成功，信息： ");
                            stringBuffer.append("\nAccount：" + xrpTransaction.tx_json.Account);
                            stringBuffer.append("\nDestination：" + xrpTransaction.tx_json.Destination);
                            stringBuffer.append("\nSigningPubKey：" + xrpTransaction.tx_json.SigningPubKey);
                            stringBuffer.append("\nFee：" + xrpTransaction.tx_json.Fee);
                            stringBuffer.append("\nLastLedgerSequence：" + xrpTransaction.tx_json.LastLedgerSequence);
                            stringBuffer.append("\nSequence：" + xrpTransaction.tx_json.Sequence);
                            stringBuffer.append("\nTxnSignature：" + xrpTransaction.tx_json.TxnSignature);
                            stringBuffer.append("\nhash：" + xrpTransaction.tx_json.hash);
                        } else {
                            stringBuffer.append("\n交易失败：错误码 = " + xrpTransaction.engine_result_code);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillContent(stringBuffer.toString());
                            }
                        });
                    }
                }).start();
            }
        });


        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XRPLedger xrpLedger = AitdOpenApi.Request.close();

                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("当前账本位置：" + xrpLedger.ledger_current_index);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillContent(stringBuffer.toString());
                            }
                        });
                    }
                }).start();
            }
        });

    }

    /**
     * 进入文字
     *
     * @param response
     */
    public void fillContent(String response) {
        LogRipple.e("TTT", response);
        log.setText(response + "\n" + log.getText().toString());
    }
}