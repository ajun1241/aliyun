package com.modcreater.tmutils;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/9 9:39
 */
public class PushUtil {
    /**
     * 证书路径
     */
    private static final String CERTIFICATE_PATH = "C:\\DevelopentPushManager.p12";
    /**
     * 证书密码
     */
    private static final String CERTIFICATE_PASSWORD = "7897899";
    /**
     * true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
     */
    private static final boolean IS_PRODUCTION =false;
    /**
     * 铃声
     */
    private static final String SOUND = "default";

    /**
     * iOS官方推送
     * @param deviceToken
     * @param alert 推送内容
     * @param badge 图标小红圈的数值
     */
    public static void APNSPush(String deviceToken,String alert,int badge){
        List<String> tokens = new ArrayList<>();
        tokens.add(deviceToken);
        boolean sendCount = true;
        try {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            // 消息内容
            payLoad.addAlert(alert);
            // iphone应用图标上小红圈上的数值
            payLoad.addBadge(badge);
            if (!StringUtils.isBlank(SOUND)) {
                //铃音
                payLoad.addSound(SOUND);
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(CERTIFICATE_PATH, CERTIFICATE_PASSWORD, IS_PRODUCTION));
            List<PushedNotification> notifications = new ArrayList<>();
            // 发送push消息
            if (sendCount) {
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            }else {
                List<Device> device = new ArrayList<>();
                for (String token : tokens) {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            System.out.println("推送");
            pushManager.stopConnection();
        } catch (Exception e) {
            System.out.println("sad");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PushUtil.APNSPush("0b7a7fd7a60b7ed945862b58a5b4c00eff1e9fd2c18c0d4d1bb7042a8a8a1c96","sad",1);
    }
}
