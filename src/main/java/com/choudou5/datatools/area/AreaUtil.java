package com.choudou5.datatools.area;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.choudou5.datatools.area.bean.AreaBean;
import com.choudou5.datatools.log.LogHelper;
import com.choudou5.datatools.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * @Name: 地区工具类
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class AreaUtil {

    private static int stat = 0;

    //国家统计局接口
    private static final String API = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/";

    public static void main(String[] args) throws Exception {
        //统计年
        int year = 2016;
    }


    /**
     * 深度 爬取地区列表
     * @param year 统计年
     * @param  provincePrefix 省份编码前缀
     * @return
     */
    public static void depthCrawlAreaBeanList(int year, String provincePrefix, InsertCall call) {
        AreaBean root = new AreaBean("000000", "中国", 0);
        crawlProvinceAreaBeanList(year, root, call, provincePrefix);
    }


    /**
     * 爬取省份 地区列表
     * @param year
     * @param parent
     * @param provincePrefix
     */
    private static void crawlProvinceAreaBeanList(int year, AreaBean parent, InsertCall call, String provincePrefix) {
        int level = 1;
        String url = getApiUrl(level, year, parent.getPath());
        //定位内容规则
        String indexRule = getIndexRule(level);
        Document doc = JsoupUtil.getDocument(url, "gbk");
        Elements eles = doc.select(indexRule);
        if (eles != null && !eles.isEmpty()) {
            Iterator<Element> iterator = eles.iterator();
            AreaBean area = null;
            for (Element ele : eles) {
                area = parseArea(level, ele, parent);
                if(area == null || !area.getCode().startsWith(provincePrefix))
                    continue;
                call.insertDB(area);
                LogHelper.log((++stat) + area.toString());
                //深度递归
                depthCrawlAreaBeanList(year, area, level + 1, call);
                area = null;
            }
        } else {
            LogHelper.log(url + " 内容解析失败, 规则：" + indexRule);
            LogHelper.log("内容定位规则不正确，请从下面复制正确的规则解析.");
            //测试 打印规则
            JsoupUtil.testRuleByHtml(doc.html(), "<a href=");
        }
    }

    public static void repairCityArea(int year, AreaBean parent, int level, InsertCall call) {
        String url = getApiUrl(level, year, parent.getPath());
        //定位内容规则
        String indexRule = getIndexRule(level);
        Document doc = JsoupUtil.getDocument(url, "gbk");
        Elements eles = doc.select(indexRule);
        if (eles != null && !eles.isEmpty()) {
            Iterator<Element> iterator = eles.iterator();
            AreaBean area = null;
            for (Element ele : eles) {
                area = parseArea(level, ele, parent);
                if(area == null)
                    continue;
                call.insertDB(area);
                LogHelper.log((++stat) + area.toString());
                if(StrUtil.isBlank(area.getPath())) {
                    area = null;
                    continue;
                }
                //深度递归
                repairCityArea(year, area, level + 1, call);
                area = null;
            }
        } else {
            LogHelper.log(url + " 内容解析失败, 规则：" + indexRule);
            LogHelper.log("内容定位规则不正确，请从下面复制正确的规则解析.");
            //测试 打印规则
            JsoupUtil.testRuleByHtml(doc.html(), "<a href=");
        }
    }

    /**
     * 深度 爬取地区列表
     * @param year
     * @param parent
     * @param level
     */
    private static void depthCrawlAreaBeanList(int year, AreaBean parent, final int level, InsertCall call) {
        String url = getApiUrl(level, year, parent.getPath());
        //定位内容规则
        String indexRule = getIndexRule(level);
        Document doc = JsoupUtil.getDocument(url, "gbk");
        Elements eles = doc.select(indexRule);
        if (eles != null && !eles.isEmpty()) {
            Iterator<Element> iterator = eles.iterator();
            AreaBean area = null;
            for (Element ele : eles) {
                area = parseArea(level, ele, parent);
                if(area == null)
                    continue;
                call.insertDB(area);
                LogHelper.log((++stat) + area.toString());
                if(StrUtil.isBlank(area.getPath())) {
                    area = null;
                    continue;
                }
                //深度递归
                depthCrawlAreaBeanList(year, area, level + 1, call);
                area = null;
            }
        } else {
            LogHelper.log(url + " 内容解析失败, 规则：" + indexRule);
            LogHelper.log("内容定位规则不正确，请从下面复制正确的规则解析.");
            //测试 打印规则
            JsoupUtil.testRuleByHtml(doc.html(), "<a href=");
        }
    }


    /**
     * 获取 请求api
     * @param level
     * @param year
     * @param pPath
     * @return
     */
    private static String  getApiUrl(int level, int year, String pPath) {
        if(level == 1){
            //省份
            return API + year+"/index.html";
        }else{
            //省份以下
            return API + year+"/" + pPath+ ".html";
        }
    }

    /**
     * 获取 定位内容规则
     * @param level
     * @return
     */
    private static String  getIndexRule(int level) {
        if(level == 1){
            //省份
            return "body > map > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > a";
        }else{
            //省份以下
            return "body > map > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr";
        }
    }

    /**
     * 解析地区
     * @param level
     * @param ele
     * @param parent
     * @return
     */
    private static AreaBean parseArea(int level, Element ele, AreaBean parent){
        AreaBean area = null;
        if(level == 1){//省份
            String href = ele.attr("href");
            String path = StrUtil.subBefore(href, ".html", true);
            String name = ele.text();
            area = new AreaBean(path, name, level, parent.getCode(), parent.getName(), parent.getFullname(), path);
        }else if(level != 5){
            Elements codeEle = ele.select("td:eq(0) a");//编码
            String href = codeEle.attr("href");
            if(StrUtil.isNotBlank(href)){
                String code = codeEle.text();
                String path = StrUtil.subBefore(href, ".html", true);
                String name = ele.select("td:eq(1) a").text(); //名称
                area = new AreaBean(code, name, level, parent.getCode(), parent.getName(), parent.getFullname(), getUrl(parent, path));
            }
            codeEle = null;
        }else{
            String code = ele.select("td:eq(0)").text();//编码
            if(NumberUtil.isNumber(code)){
                String name = ele.select("td:eq(2)").text(); //名称
                //兼容 geo查询5级地区
                String pFullName = StrUtil.subBefore(parent.getFullname(), parent.getPname(), true);
                if(StrUtil.isBlank(pFullName))
                    pFullName = parent.getFullname();
                area = new AreaBean(code, name, level, parent.getCode(), parent.getName(), pFullName, null);
            }
            code = null;
        }
        return area;
    }

    private static String getUrl(AreaBean parent, String currPath){
        StringBuilder url = new StringBuilder();
        int level = parent.getLevel();
        if(level == 1){
            return currPath;
        }else{
            for (int i = 0; i < level; i++) {
                if(i != level){
                    int start = i*2;
                    String space = parent.getCode().substring(start, start+2);
                    if(!"00".equals(space))
                        url.append(space+"/");
                }
            }
            url.append(StrUtil.subAfter(currPath, "/", true));
        }
        return url.toString();
    }

}
