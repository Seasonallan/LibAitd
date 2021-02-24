package com.library.aitd;

import com.library.aitd.bean.XRPAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户接口（注册登录） 单元测试
 */
public class ApiWallet_UnitTest extends BaseUnitTest {


    //    int startIndex = 100051;//11200
//    int endIndex = 100551;
    int startIndex = 0;
    int endIndex = 2000000;

    @Override
    protected void init() {
        LogRipple.enableLog(false);
        AitdOpenApi.initSDK("https://s1.ripple.com:51234");
        AitdOpenApi.switchCoinModeXRP();
        File file = new File(path);
        if (!file.exists()) {
            LogRipple.print("创建缓存用户文件=" + file.toString());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int threadCount = 1;//同时执行的线程数量

    List<RegisterThread> threadList = new ArrayList<>();

    int perCount = endIndex / 50;

    @Override
    protected void proceed() {
        threadCount = (endIndex - startIndex) / perCount + 1; //perCount个人一个线程
        LogRipple.print("开启注册机数量=" + threadCount);

        for (int i = 0; i < threadCount; i++) {
            int itemStart = startIndex + i * perCount;
            int itemEnd = Math.min(startIndex + (i + 1) * perCount, endIndex);
            LogRipple.print(i + ":" + itemStart + "," + itemEnd);
            RegisterThread pickThread = new RegisterThread(i + 1, itemStart, itemEnd) {
                @Override
                protected void onThreadComplete() {
                    super.onThreadComplete();
                    threadList.remove(this);
                    LogRipple.print("线程剩余数量=" + threadList.size());
                    if (threadList.size() == 0) {
                        onTestFinish(true);
                        LogRipple.print("注册机注册完成，线程数：" + threadCount + "，结果（注册数量：" + (endIndex - startIndex) + "，" +
                                "接口失败次数：" + errorCount + "）");
                    }
                }
            };
            threadList.add(pickThread);
        }
        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).proceed();
        }
    }

    private int errorCount = 0;

    public static class RegisterThread {

        private int id;
        int start;
        int count;
        int end;

        RegisterThread(int id, int start, int end) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.count = 10;
        }

        protected void onThreadComplete() {
            LogRipple.printForce("线程" + id + ">>当前线程执行完毕");
        }

        protected void proceed() {
            LogRipple.print("线程" + id + ">>proceed>> 数据剩余：" + (end - start));
            if (start < end) {
                start++;
                if ((end - start) % count == 0) {
                    LogRipple.printForce("线程" + id + ">>proceed>> 数据剩余：" + (end - start));
                }
                accountCheck();
            } else {
                onThreadComplete();
            }
        }

        private void accountCheck() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> mnemonicList = AitdOpenApi.Wallet.createRandomMnemonic();
                        String seed = AitdOpenApi.Wallet.createFromMnemonic(mnemonicList);
                        LogRipple.print(seed);
                        XRPAccount xrpAccount = AitdOpenApi.Request.getAccountInfo(AitdOpenApi.Wallet.getAddress(seed));
                        if (xrpAccount != null) {
                            if (xrpAccount.account_data != null) {
                                String balance = xrpAccount.account_data.Balance;
                                LogRipple.printForce(seed + ":" + balance);
                                File file = new File(path);

                                StringBuilder stringBuilder = new StringBuilder();
                                for (String str : mnemonicList) {
                                    stringBuilder.append(str).append(" ");
                                }
                                stringBuilder.append(seed).append(">>");
                                stringBuilder.append(balance).append("\n");

                                FileWriter fileWriter = null;
                                try {
                                    fileWriter = new FileWriter(file, true);
                                    fileWriter.write(stringBuilder.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (fileWriter != null) {
                                            fileWriter.close();
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            } else {
                                LogRipple.print(seed + "--:" + xrpAccount.error_message);
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    proceed();
                }
            }).start();
        }


    }

}