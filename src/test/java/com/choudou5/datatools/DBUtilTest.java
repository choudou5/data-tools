package com.choudou5.datatools;


import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.choudou5.datatools.area.bean.AreaBean;
import com.choudou5.datatools.db.DBUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.choudou5.datatools.db.DBUtil.*;
import static org.junit.Assert.fail;

public class DBUtilTest {

    private Connection con = null;

    private String TABLE = "DIC_AREA";

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

    @Test
    public void insert() {
        AreaBean bean = new AreaBean();
        bean.setCode("0");
        bean.setName("中国");
        bean.setLevel(0);
        String sql = "insert INTO "+TABLE+" values(?, ?, ?, ?, ?, ?, ?);";
        try {
            con.setAutoCommit(false);
            int count = DBUtil.execute(con, sql, 1, bean.getCode(), bean.getName(), bean.getPcode(), bean.getPname(), bean.getLevel(), bean.getGeo());
            con.setAutoCommit(true);
            Assert.isTrue(count == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void update() {
        String sql = "UPDATE "+TABLE+" t SET t.salary =? WHERE t.employee_id =?";
        try {
            con.setAutoCommit(false);
            int count = DBUtil.execute(con, sql, 20000, 120);
            Assert.isTrue(count == 1);
            sql = "SELECT t.salary FROM employees t WHERE t.employee_id =?";
            Double d = DBUtil.queryObject(con, sql, Double.class, 120);
            Assert.isTrue(d - 20000 == 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void selectAll() {
        String sql = "SELECT * FROM "+TABLE;
        try {
            List<AreaBean> emList = DBUtil.queryBeanList(con, sql, AreaBean.class);
            print(emList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void selectByConds() {
        String sql = "SELECT * FROM "+TABLE+" t WHERE t.salary > ? and T.JOB_ID = ?";
        try {
            List<AreaBean> emList = DBUtil.queryBeanList(con, sql, AreaBean.class, 5000, "ST_MAN");
            print(emList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testQueryBeanListConnectionStringIResultSetCallOfTObjectArray() {
        String sql = "SELECT first_name, last_name, salary FROM "+TABLE+" t WHERE t.salary > ? and T.JOB_ID = ?";
//        try {
//            List<AreaBean> emList = DBUtil.queryBeanList(con, sql, new IResultSetCall<AreaBean>() {
//                public AreaBean invoke(ResultSet rs) throws SQLException {
//                    AreaBean e = new AreaBean();
//                    e.setFirst_name(rs.getString("first_name"));
//                    e.setLast_name(rs.getString("last_name"));
//                    e.setSalary(rs.getDouble("salary"));
//                    return e;
//                }
//            }, 5000, "ST_MAN");
//            print(emList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
    }

    @Test
    public void selectList() {
        String sql = "SELECT email FROM "+TABLE+" t";
        try {
            List<String> lists = DBUtil.queryObjectList(con, sql, String.class);
            print(lists);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void selectByCond() {
        String sql = "SELECT salary FROM "+TABLE+" t WHERE t.salary > ? and T.JOB_ID = ?";
        try {
            List<Double> lists = DBUtil.queryObjectList(con, sql, Double.class, 2000, "ST_MAN");
            print(lists);
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void selectByIds() {
        String sql = "SELECT * FROM "+TABLE+" t WHERE t.employee_id in (120)";
        try {
            AreaBean emp = DBUtil.queryBean(con, sql, AreaBean.class);
            print(emp);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void selectById() {
        String sql = "SELECT * FROM "+TABLE+" t WHERE t.employee_id = ?";
        try {
            AreaBean emp = DBUtil.queryBean(con, sql, AreaBean.class, 120);
            print(emp);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void selectByIn() {
        String sql = "SELECT email FROM "+TABLE+" t WHERE t.employee_id in (120)";
        try {
            String s = DBUtil.queryObject(con, sql, String.class);
            print(s);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void selectByInParam() {
        String sql = "SELECT salary FROM "+TABLE+" t WHERE t.employee_id in (?)";
        try {
            Double d = DBUtil.queryObject(con, sql, Double.class, 12);
            print(d);
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void selectMapList() {
        String sql = "SELECT first_name, last_name, salary FROM "+TABLE+" t WHERE t.salary > ? and T.JOB_ID = ?";
        try {
            List<Map<String, Object>> lists = DBUtil.queryMapList(con, sql, 3000, "ST_MAN");
            print(lists);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void callProcedure() {
        try {
            executeProcedure(openConnection(), "{CALL prc_updatedata_for_daochong(?,?,?,?)}", "3000000993447731",
                    "060000019213", "50", "0010701848");
            System.out.println("执行存储过程更新采购订单表上的数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void batchUpdate() {
        try {
            List<String> sqlList = new ArrayList<String>();
            sqlList.add("UPDATE sm_user t SET t.password = 'ok' WHERE t.row_id = '232s43' ");
            sqlList.add("UPDATE sm_user t SET t.password = 'ok' WHERE t.row_id = '232f42' ");
            sqlList.add("UPDATE sm_user t SET t.password = 'ok' WHERE t.row_id = '23g2423' ");
            sqlList.add("UPDATE sm_user t SET t.password = 'ok' WHERE t.row_id = '232434s' ");
            executeAsBatch(openConnection(), sqlList);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void batchForPre() {
        try {
            executeAsBatch(con, "UPDATE "+TABLE+" t SET t.first_name = ? WHERE t.last_name = ? ", new Object[][] {
                    { "ok", "235jklsd" }, { "no", "jg4ti324" }, { "no1", "111" }, { "no2", "32423" } });
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void print(Object obj) {
        if (obj instanceof List) {
            List list = (List) obj;
            for (Object o : list) {
                System.out.println(JSONUtil.toJsonStr(o));
            }
            System.out.println("总共查询出数据数量是：" + list.size());
        } else {
            System.out.println(obj);
        }
    }


}
