package com.library.aitd;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * 单元测试框架
 */
public abstract class BaseUnitTest {
    @Rule
    public Timeout timeout = new Timeout(getTimeoutMillis());

    volatile boolean isEnd = false;

    //public static String path = "D:/userList.txt";
    //public static String path = "D:/userListEmpty.txt";

    public static String path = "D:/FindXrp.txt";
    public static String inviteCode = "ED5E8P";//"E0HDYP";
    public static String token = "df57174a8c89471d9819f240ec2c1a07";

    /**
     * 程序执行最大时间
     *
     * @return
     */
    protected int getTimeoutMillis() {
        return 24 * 60 * 60 * 1000;
    }

    @Test
    public void apiTest() {
        LogRipple.print("start");

        init();

        proceed();

        while (!isEnd) {
        }
        LogRipple.print("end");
    }

    /**
     * 初始化操作
     */
    protected abstract void init();

    /**
     * 逻辑代码
     */
    protected abstract void proceed();


    /**
     * 结束或进入下一个
     *
     * @param finish
     */
    protected void onTestFinish(boolean finish) {
        if (finish) {
            isEnd = true;
        } else {
            proceed();
        }
    }

}
