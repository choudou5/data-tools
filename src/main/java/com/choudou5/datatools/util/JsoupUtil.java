package com.choudou5.datatools.util;

import com.choudou5.datatools.log.LogHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * @Name: Jsoup工具类
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class JsoupUtil {

    /**
     * 获取 网页文档对象
     * @param url
     * @return
     */
    public static Document getDocument(String url) {
        return getDocument(url, 1, 3);
    }

    private static Document getDocument(String url, int loop, int max) {
        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url), 1000 * 5);
        } catch (Exception e) {
            if(!(e instanceof SocketTimeoutException))
                e.printStackTrace();
            if(loop == max)
                return null;
            try {
                long sleep = 3000+(loop*1000);
                LogHelper.log("休眠" + (sleep / 1000) + "秒，准备开始第" + loop + "次重试.");
                Thread.currentThread().sleep(sleep);
                return getDocument(url, ++loop, max);
            } catch (InterruptedException e1) {e1.printStackTrace();}
        }
        return doc;
    }

    /**
     * 测试内容规则
     * @param url
     * @param printStartKeyword 打印开始关键字
     * @throws IOException
     */
    public static void testRuleByUrl(String url, String printStartKeyword) throws IOException {
        testDeepRule(getDocument(url), printStartKeyword);
    }

    /**
     * 测试内容规则
     * @param html
     * @param printStartKeyword
     */
    public static void testRuleByHtml(String html, String printStartKeyword) {
        Document doc = Jsoup.parse(html);
        testDeepRule(doc, printStartKeyword);
    }

    /**
     * 测试 Html深层次规则
     * @param doc
     * @return
     */
    public static void testDeepRule(Document doc, String printStartKeyword) {
        Element ele = doc.body();
        List<Node> nodes = ele.childNodes();
        StringBuffer str = new StringBuffer(1024 * 15);
        recursion(nodes, ele.tagName(), str, printStartKeyword);
        LogHelper.log(str.toString());
    }

    /**
     * 递归打印
     * @param nodes
     * @param pTag
     * @param str
     * @param printStartKeyword
     */
    private static void recursion(List<Node> nodes, String pTag, StringBuffer str, String printStartKeyword) {
        for (Node node : nodes) {
            if (node.toString().startsWith(printStartKeyword)) {
                str.append("规则：" + pTag + " > " + node.nodeName() + "\r\n");
                str.append(node.toString() + "\r\n\n");
            }
            recursion(node.childNodes(), pTag + " > " + node.nodeName(), str, printStartKeyword);
        }
    }

}
