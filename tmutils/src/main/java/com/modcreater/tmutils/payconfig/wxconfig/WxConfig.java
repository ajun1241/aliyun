package com.modcreater.tmutils.payconfig.wxconfig;

import com.github.wxpay.sdk.WXPayConfig;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class WxConfig implements WXPayConfig {
    private byte[] certData;
    public static final String APP_ID = "wx32c50fdca073afb0";
    public static final String KEY = "MIIC4TCCAckCAQAwgZsxCzAJBgNVBAYT";
    public static final String MCH_ID = "1537117381";

    @Override
    public String getAppID() {
        return APP_ID;
    }

    //parnerid，商户号
    @Override
    public String getMchID() {
        return MCH_ID;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}

