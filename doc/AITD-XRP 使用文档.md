# 第一步
在根目录的 build.gradle中 allprojects的repositories里添加jitpack依赖
maven { url 'https://jitpack.io' }
# 第二步
在app项目的build.gradle下的dependencies中添加AITD库依赖
    implementation 'com.github.Seasonallan:LibAitd:1.2'
# 第三步
        
# 第四步
在AndroidManifest.xml中添加网络权限
<uses-permission android:name="android.permission.INTERNET" />
# 第五步
使用AitdOpenApi调用库API
示例1 创建钱包：
 AitdOpenApi.Wallet.createRandomWallet()
 AitdOpenApi.Wallet.getAddress(walletSecret);
示例2 获取余额：
 AitdOpenApi.Request.getAccountInfo(address)
示例3 获取交易记录：
 AitdOpenApi.Request.getTransactionList(address, 10, page, pageCount);
示例4 转账：
 AitdOpenApi.Request.transaction(fromPrivateKey, toPublicKey, "10", "11")
 
切换链
       if (aitd) {
            setTitle("测试AITD链功能");
            AitdOpenApi.initSDK(Key.DOMAIN_AITD);
            AitdOpenApi.switchCoinModeAITD();
        } else {
            setTitle("测试xrp链功能");
            AitdOpenApi.initSDK(Key.DOMAIN_XRP);
            AitdOpenApi.switchCoinModeXRP();
        }

# 注意事项
1、使用demo时，gradle版本不一致方案：
中断更新gradle
修改根目录下的build.gradle中的classpath "com.android.tools.build:gradle:4.0.1" 为你的版本
修改根目录下的gradle/wrapper/gradle-wrapper.properties中的distributionUrl为你的版本
关闭重启项目
2、混淆配置，保持序列号模型
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
 