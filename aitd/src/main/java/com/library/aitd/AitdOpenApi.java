package com.library.aitd;

import android.util.Log;

import com.library.aitd.bean.XRPAccount;
import com.library.aitd.bean.XRPFee;
import com.library.aitd.bean.XRPLedger;
import com.library.aitd.bean.XRPTransaction;
import com.library.aitd.bean.transaction.XRPAccount_TransactionList;
import com.library.aitd.http.SimpleRequest;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.signed.SignedTransaction;
import com.ripple.core.types.known.tx.txns.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AitdOpenApi {

    public static String DOMAIN;

    public static void initSDK(String domain) {
        DOMAIN = domain;
    }

    /**
     * 通过私钥获取地址
     *
     * @param seed
     * @return
     */
    public static String getAddress(String seed) {
        return AccountID.fromSeedString(seed).address;
    }


    private static String buildRequestBody(String method, JSONArray params) {
        JSONObject js_request = new JSONObject();
        try {
            js_request.put("method", method);
            js_request.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js_request.toString();
    }


    /**
     * 获取手续费
     *
     * @return
     */
    public static XRPFee getFee() {
        JSONArray emptyParams = new JSONArray();
        emptyParams.put(new JSONObject());
        String res = SimpleRequest.postRequest(DOMAIN, buildRequestBody("fee", emptyParams));
        XRPFee xrpFee = new XRPFee();
        try {
            JSONObject responseObject = new JSONObject(res);
            xrpFee.fromJsonObject(responseObject.getJSONObject("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xrpFee;
    }


    /**
     * 获取账户余额
     *
     * @return
     */
    public static XRPAccount getAccountInfo(String address) {
        JSONArray transactionParams = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        transactionParams.put(jsonObject);
        String res = SimpleRequest.postRequest(DOMAIN, buildRequestBody("account_info", transactionParams));
        XRPAccount xrpAccount = new XRPAccount();
        try {
            JSONObject responseObject = new JSONObject(res);
            xrpAccount.fromJsonObject(responseObject.getJSONObject("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xrpAccount;
    }


    /**
     * 获取交易列表
     *
     * @return
     */
    public static XRPAccount_TransactionList getTransactionList(String address, int limit, int ledger, int seq) {
        JSONArray transactionParams = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", address);
            jsonObject.put("binary", false); //返回json格式
            jsonObject.put("limit", limit);

            if (ledger > 0) { //分页
                JSONObject marker = new JSONObject();
                marker.put("ledger", ledger);
                marker.put("seq", seq);
                jsonObject.put("marker", marker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        transactionParams.put(jsonObject);
        String res = SimpleRequest.postRequest(DOMAIN, buildRequestBody("account_tx", transactionParams));
        XRPAccount_TransactionList xrpAccountTransactionList = new XRPAccount_TransactionList();
        try {
            JSONObject responseObject = new JSONObject(res);
            xrpAccountTransactionList.fromJsonObject(responseObject.getJSONObject("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xrpAccountTransactionList;
    }


    /**
     * 转账
     *
     * @return
     */
    public static XRPTransaction transaction(String seed, String destination, String fee, String amount) {
        String address = AitdOpenApi.getAddress(seed);
        XRPAccount xrpAccount = AitdOpenApi.getAccountInfo(address);
        Payment payment = new Payment();
        payment.as(AccountID.Account, address);
        payment.as(AccountID.Destination, destination);
        payment.as(UInt32.DestinationTag, "1");
        payment.as(Amount.Amount, amount);
        payment.as(UInt32.Sequence, xrpAccount.account_data.Sequence);
        payment.as(UInt32.LastLedgerSequence, xrpAccount.ledger_current_index + 1);
        payment.as(Amount.Fee, fee);
        SignedTransaction signed = payment.sign(seed);
        String tx_blob = signed.tx_blob;
        return transaction(tx_blob);
    }

    /**
     * 转账
     *
     * @return
     */
    public static XRPTransaction transaction(String tx_blob) {
        JSONArray transactionParams = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tx_blob", tx_blob);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        transactionParams.put(jsonObject);

        String res = SimpleRequest.postRequest(DOMAIN, buildRequestBody("submit", transactionParams));
        XRPTransaction xrpTransaction = new XRPTransaction();
        try {
            JSONObject responseObject = new JSONObject(res);
            xrpTransaction.fromJsonObject(responseObject.getJSONObject("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xrpTransaction;
    }



    /**
     * 关闭账单
     *
     * @return
     */
    public static XRPLedger close() {
        JSONArray emptyParams = new JSONArray();
        emptyParams.put(new JSONObject());

        String res = SimpleRequest.postRequest(DOMAIN, buildRequestBody("ledger_accept", emptyParams));
        XRPLedger xrpLedger = new XRPLedger();
        try {
            JSONObject responseObject = new JSONObject(res);
            xrpLedger.fromJsonObject(responseObject.getJSONObject("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xrpLedger;
    }


}
