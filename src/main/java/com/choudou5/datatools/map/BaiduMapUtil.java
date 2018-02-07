package com.choudou5.datatools.map;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.choudou5.datatools.log.LogHelper;
import com.choudou5.datatools.map.bean.BaiduArea;
import com.choudou5.datatools.map.bean.GeoBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Name: 百度地图 工具类
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class BaiduMapUtil {

    /** 百度地理编码 地址*/
    private static final String GEOCODE_URL = "http://api.map.baidu.com/geocoder/v2/";

    /** 百度地图 经纬度-->获取省市区信息 接口 */
    private static final String MAP_API_GEO_BY_GEO = "http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&location=%s&batch=%s&output=json&pois=0&extensions_poi=null";

    //http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding

    public static void main(String[] args) throws Exception {
        String appKey = "A1LU7iHS0avqQwPLAxbhKn0UYSQCuRVH";

        GeoBean geo = getLngLat(appKey, "上海市");
        LogHelper.log(geo);

        List<BaiduArea> areas = getAreas(appKey, geo);
        LogHelper.log(areas);
    }

    /**
     * 获得 经纬度
     * @param appKey 授权key
     * @return
     */
    public static GeoBean getLngLat(String appKey, String address){
        //json&&json({"status":0,"result":{"location":{"lng":116.52169489108084,"lat":39.95895316640668},"precise":0,"confidence":16,"level":"区县"}})
        String jsonStr = HttpUtil.get(GEOCODE_URL + "?address=" + address + "&output=json&ak=" + appKey);
        if(StrUtil.isNotBlank(jsonStr)){
            JSONObject obj = JSONUtil.parseObj(jsonStr);
            int status = obj.getInt("status");
            if(status != 0){
                throw new IllegalArgumentException("result status:"+status);
            }
            return obj.getJSONObject("result").getJSONObject("location").toBean(GeoBean.class);
        }
        return null;
    }


    /**
     * 批量获取 百度地区
     * @param appKey
     * @param geos
     * @return
     */
    public static List<BaiduArea> getAreas(String appKey, GeoBean... geos){
        int len = geos.length;
        if(len == 0){
            throw new IllegalArgumentException("经纬度地址 不能为空.");
        }
        if(len > 20){
            throw new IllegalArgumentException("批量最多解析20个经纬度地址.");
        }
        String batch = "false";
        String geoStr = null;
        if(len >= 1){
            batch = "true";
            try {
                geoStr = URLEncoder.encode(ArrayUtil.join(geos, "|"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                LogHelper.error("URLEncoder geos 失败.", e);
            }
        }else{
            geoStr = geos[0].toString();
        }
        String url = String.format(MAP_API_GEO_BY_GEO, appKey, geoStr, batch);
        String resp = HttpUtil.get(url);
        if(StrUtil.isNotBlank(resp)){
            String prefix = "renderReverse&&renderReverse(";
            if(resp.startsWith(prefix)){
                try {
                    String jsonStr = StrUtil.sub(resp, prefix.length(), resp.length() - 1);
                    JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                    if(jsonObject.getInt("status") == 0){
                        JSONArray areas = jsonObject.getJSONArray("areas");
                        List<BaiduArea> list = areas.toList(BaiduArea.class);
                        return list;
                    }
                } catch (Exception e) {
                    LogHelper.error(geoStr+" 解析成经纬度失败.", e);
                }
            }
            LogHelper.log(geoStr + " 解析经纬度失败，resp:" + resp);
        }
        return null;
    }

}
