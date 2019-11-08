package com.modcreater.tmutils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.utils.GetBarcode;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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
     * @param lon1
     * @param lat2
     * @param lon2
     * @return 距离
     */
    public static double getDistance(double lon1, double lat1, double lon2,double lat2 ) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
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
        String key="408f5521009651ca143e465947d4d2dc";
        String origins=lng1+","+lat1;
        String destination=lng2+","+lat2;
        /**
         * 高德地图api请求路径
         */
        String url = "https://restapi.amap.com/v3/distance?key="+key+"&origins="+origins+"&destination="+destination+"&type="+type;
        System.out.println(url);
        String result=HttpRequestUtil.sendGet(url);
        String distance="";
        Map<String,Object> map=JSONObject.parseObject(result,Map.class);
        if ("0".equals(map.get("status"))){
            logger.error("amap request failed code ["+map.get("infocode")+"] details："+map.get("info"));
            return "0";
        }else {
            List<Map> list=JSONObject.parseObject(map.get("results").toString(), ArrayList.class);
            for (Map resultMap:list) {
                distance=resultMap.get("distance").toString();
                break;
            }
        }
        return distance;
    }

    /**
     * 根据高德地图api查询逆地理位置
     * @return
     */
    public static Map<String,String> getAddressByamap(double lng1, double lat1){
        String key="52ddc032c0d3078860d4767c6b0742b2";
        String location=lng1+","+lat1;
        /**
         * 高德地图api请求路径
         */
        String url = "https://restapi.amap.com/v3/geocode/regeo?key="+key+"&location="+location;
        String result=HttpRequestUtil.sendGet(url);
        System.out.println(result);
        Map<String,String> resultMap=new HashMap<>();
        Map map1=JSONObject.parseObject(result,Map.class);
        if ("0".equals(map1.get("status"))){
            logger.error("amap request failed code ["+map1.get("infocode")+"] details："+map1.get("info"));
            return null;
        }else {
            Map map2=JSONObject.parseObject(JSON.toJSONString(map1.get("regeocode")), Map.class);
            //获取详细地址
            resultMap.put("detailAddress",map2.get("formatted_address").toString());
            Map map3=JSONObject.parseObject(JSON.toJSONString(map2.get("addressComponent")),Map.class);
            resultMap.put("country", ObjectUtils.isEmpty(map3.get("country")) ? "" : map3.get("country").toString());
            resultMap.put("province",ObjectUtils.isEmpty(map3.get("province")) ? "" : map3.get("province").toString());
            resultMap.put("city",ObjectUtils.isEmpty(map3.get("city")) ? "" :map3.get("city").toString());
            resultMap.put("district",ObjectUtils.isEmpty(map3.get("district")) ? "" :map3.get("district").toString());
            resultMap.put("township",ObjectUtils.isEmpty(map3.get("township")) ? "" :map3.get("township").toString());
        }
        return resultMap;
    }

    public static void main(String[] args) {
        List<String> collection=new ArrayList();
        collection.add("1");
        collection.add("2");
        collection.add("3");
        collection.add("4");
        /*for (String e:collection) {
            if (e.equals("2")){
                collection.remove(e);
            }
        }
*/
        Iterator<String> iterator=collection.iterator();
        while (iterator.hasNext()){
            String e=iterator.next();
            if (e.equals("2")){
                iterator.remove();
            }
        }
        /*for (int i = 0; i < collection.size(); i++) {
            if ("2".equals(collection.get(i))){
                collection.remove(i);
            }
        }*/
        System.out.println(collection.toString());
    }
}