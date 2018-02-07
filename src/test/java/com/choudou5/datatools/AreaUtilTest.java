package com.choudou5.datatools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.choudou5.datatools.area.AreaUtil;
import com.choudou5.datatools.area.InsertCall;
import com.choudou5.datatools.area.bean.AreaBean;
import com.choudou5.datatools.db.DBUtil;
import com.choudou5.datatools.map.BaiduMapUtil;
import com.choudou5.datatools.map.bean.GeoBean;
import com.choudou5.datatools.util.PinyinUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class AreaUtilTest {

    private Connection con = null;

    @Before
    public void setUp() throws Exception {
        try {
            con = DBUtil.openConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            DBUtil.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    private void commInsertDB(AreaBean bean){
        String sql = "insert INTO DIC_AREA(`code`, `name`, `pcode`, `pname`, `level`, `geo`, `fullname`, pinyin, pinyin_short, pinyin_first_letter) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String py = null;
        String spy = null;
        String pyf = null;
        //拼音
        if (bean.getLevel() < 4) {
            py = PinyinUtil.getPinYin(bean.getName());
            spy = PinyinUtil.getShortPinYin(bean.getName());
            if (bean.getLevel() < 3)
                pyf = PinyinUtil.getHeadFirstLetter(bean.getName());
        }
        //经纬度
        String geo = getGeo(bean.getFullname());
        if (geo != null) {
            bean.setGeo(geo);
        }

        try {
            con.setAutoCommit(false);
            int count = DBUtil.execute(con, sql, AreaBean.formatAreaCode(bean.getCode(), bean.getLevel()), bean.getName(), AreaBean.formatAreaCode(bean.getPcode(), bean.getLevel() - 1), bean.getPname(), bean.getLevel(), bean.getGeo(), bean.getFullname(), py, spy, pyf);
            con.setAutoCommit(true);
            Assert.isTrue(count == 1);
            sql = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 执行 拉取
     * @throws Exception
     */
    @Test
    public void executeCrawl() throws Exception {
        try {
            int year = 2016;
            String provincePrefix = "65";
            AreaUtil.depthCrawlAreaBeanList(year, provincePrefix, new InsertCall() {
                public void insertDB(AreaBean bean) {
                    commInsertDB(bean);
                }
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    /**
     * 修复 特殊 市辖区
     * @throws Exception
     */
    @Test
    public void repairArea() throws Exception {
        try {
            int year = 2016;
//            AreaBean parent = new AreaBean("441900", "市辖区", 3, "440000", "东莞市", "广东省东莞市", "44/4419");
//            AreaBean parent = new AreaBean("442000", "市辖区", 3, "440000", "中山市", "广东省中山市", "44/4420");
            AreaBean parent = new AreaBean("460400", "市辖区", 3, "440000", "儋州市", "海南省儋州市市", "46/4604");
            commInsertDB(parent);
            AreaUtil.repairCityArea(year, parent, 4, new InsertCall() {
                public void insertDB(AreaBean bean) {
                    commInsertDB(bean);
                }
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private List<String> backList = new ArrayList<>();
    /**
     * 修复 地区经纬度 错误
     */
    @Test
    public void repairAreaGeoError() {
        try {
            int loop = 500;
            for (int i = 0; i < loop; i++) {
//            List<AreaBean> list = DBUtil.queryBeanList(con, "select id, fullname from DIC_AREA WHERE geo is null ", AreaBean.class);
//                List<AreaBean> list = DBUtil.queryBeanList(con, "SELECT id, fullname FROM dic_area WHERE pcode REGEXP '^110100|^620201|^500100|^460201|^310100|^120100' ", AreaBean.class);
                List<AreaBean> list = DBUtil.queryBeanList(con, "SELECT id, fullname FROM dic_area WHERE `level` > 3 AND status IS NULL " +
                        "GROUP BY `geo` HAVING COUNT(1) > 20 LIMIT 0, 500", AreaBean.class);
                if(CollectionUtil.isEmpty(list))
                    break;
                System.out.println("待修复："+list.size());
                List<String> sqlList = new ArrayList<String>();

                String geo = null;
                for (AreaBean area : list) {
                    if(backList.contains(area.getId()+""))
                        continue;
                    StringBuilder sql = new StringBuilder(128);
                    System.out.println(area.getId()+"="+area.getFullname());
                    sql.append("UPDATE DIC_AREA SET");
                    geo = getGeo(area.getFullname());
                    if(geo == null){
                        backList.add(area.getId() + "");
                        continue;
                    }
                    sql.append(" geo = '" + geo + "'");
                    sql.append(", status = 1");
                    sql.append(" WHERE id = '" + area.getId() + "';");
                    sqlList.add(sql.toString());
                    sql = null;
                }
                if(CollectionUtil.isNotEmpty(sqlList)){
                    System.out.println(i+" ——> 插入成功："+sqlList.size()+", 黑名单个数："+backList.size());
                    DBUtil.executeAsBatch(con, sqlList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }



    private String getGeo(String address){
        return getGeo(address, 2);
    }

    private String getGeo(String address, int maxLoop){
        String ak = akList.get(akIndex);
        GeoBean geo = new GeoBean();
        try {
            geo = BaiduMapUtil.getLngLat(ak, address);
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException){
                e.printStackTrace();
                maxLoop--;
                akIndex++;
                if(akIndex == akList.size())
                    akIndex = 0;
                if(maxLoop == 0)
                    return null;
                return getGeo(address, maxLoop);
            }
        }
        return geo.toString();
    }

    private int akIndex = 0;
    private List<String> akList = Arrays.asList(
            "0D518594d504c02b9271272ddca83c41"
            , "KmqcYEwkUyar9wtcykadxq4xva5bty6P"
            , "vK9dQIay1e6UwEkhb02BNDGpmCbSt2Pe"
            , "1b216a2956131233230294d4c8328d6c"
            , "4c77ac8f8318a8c1b226cd83b434cf32"
            , "A1LU7iHS0avqQwPLAxbhKn0UYSQCuRVH"
            , "18NwnTeKzmkoMSpsNCTkUudt"
            , "hVOmt4zo8hAZO7mhHNxj9GEf");

}
