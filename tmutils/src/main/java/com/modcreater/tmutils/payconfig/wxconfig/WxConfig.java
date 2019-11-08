package com.modcreater.tmutils.payconfig.wxconfig;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class WxConfig implements WXPayConfig {
    private byte[] certData;
    public WxConfig() throws Exception {
        //证书只是撤销订单时会使用
        String certPath = "C:\\mdxc_wpayx.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }
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

