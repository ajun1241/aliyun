package com.modcreater.tmutils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.utils.GetBarcode;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/14 16:13
 */
public class DistanceUtil {


    private static Logger logger= LoggerFactory.getLogger(RongCloudMethodUtil.class);

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    /**
     * 根据高德地图api查询两地距离
     * @return
     */
    public static String getDistanceByamap(double lng1, double lat1, double lng2, double lat2,String type){
        String key="52ddc032c0d3078860d4767c6b0742b2";
        String origins=lng1+","+lat1;
        String destination=lng2+","+lat2;
        /**
         * 高德地图api请求路径
         */
        String url = "https://restapi.amap.com/v3/distance?key="+key+"&origins="+origins+"&destination="+destination+"&type="+type;
        logger.info("url："+url);
        RestTemplate template = new RestTemplate();
        ResponseEntity responseEntity = template.getForEntity(url,String.class);
        Map resultMap=JSONObject.parseObject(responseEntity.getBody().toString(),Map.class);
        if (Integer.parseInt(resultMap.get("infocode").toString()) != 10000){
            logger.error("请求错误，错误码："+resultMap.get("infocode").toString());
            return "-1";
        }
        List<Map> mapList=JSONObject.parseObject(resultMap.get("results").toString(),List.class);
        String distance="";
        for (Map map:mapList) {
            distance=map.get("distance").toString();
            break;
        }
        return distance;
    }


}
