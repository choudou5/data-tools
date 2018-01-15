package com.choudou5.datatools.area;

import cn.hutool.core.util.StrUtil;
import com.choudou5.datatools.area.bean.AreaBean;
import com.choudou5.datatools.util.JsoupUtil;
import com.choudou5.datatools.log.LogHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        List<AreaBean> areaList = depthCrawlAreaBeanList(year);
    }

    /**
     * 深度 爬取地区列表
     * @param year 统计年
     * @return
     */
    public static List<AreaBean> depthCrawlAreaBeanList(int year) {
        List<AreaBean> areaList = new ArrayList<AreaBean>(2048);
        AreaBean root = new AreaBean("0", "中国", 0);
        depthCrawlAreaBeanList(year, root, areaList, 1);
        return areaList;
    }

    /**
     * 深度 爬取地区列表
     * @param year
     * @param parent
     * @param retAreaBeanList 返回地区列表
     * @param level
     */
    private static void depthCrawlAreaBeanList(int year, AreaBean parent, List<AreaBean> retAreaBeanList, int level) {
        String url = getApiUrl(level, year, parent.getPath());
        //定位内容规则
        String indexRule = getIndexRule(level);
        Document doc = JsoupUtil.getDocument(url);
        Elements eles = doc.select(indexRule);
        if (!eles.isEmpty()) {
            Iterator<Element> iterator = eles.iterator();
            AreaBean area = null;
            for (Element ele : eles) {
                area = parseArea(level, ele, parent);
                if(area == null)
                    continue;
                retAreaBeanList.add(area);
                LogHelper.log((++stat) + area.toString());
                //深度递归
                depthCrawlAreaBeanList(year, area, retAreaBeanList, level + 1);
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
            area = new AreaBean(path, name, level, null, null, path);
            LogHelper.log((++stat) + area.toString());
        }else{
            Elements codeEle = ele.select("td:eq(0) a");//编码
            String href = codeEle.attr("href");
            if(StrUtil.isNotBlank(href)){
                String code = codeEle.text();
                String path = StrUtil.subBefore(href, ".html", true);
                String name = ele.select("td:eq(1) a").text(); //名称
                area = new AreaBean(code, name, level, parent.getCode(), parent.getName(), getUrl(parent, path));
            }
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
                    url.append(parent.getCode().substring(start, start+2)+"/");
                }
            }
            url.append(StrUtil.subAfter(currPath, "/", true));
        }
        return url.toString();
    }

}
