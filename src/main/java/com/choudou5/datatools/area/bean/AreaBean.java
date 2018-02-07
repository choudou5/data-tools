package com.choudou5.datatools.area.bean;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * @Name: 地区Bean
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class AreaBean implements Serializable {

    private int id;
    private String code;
    private String name;
    private int level;
    private String geo; //'地理经纬度'
    private String pcode;
    private String path;
    private String pname;
    private String fullname;

    public AreaBean() {
    }

    public AreaBean(String code, String name, int level) {
        this.code = code;
        this.name = name;
        this.level = level;
    }

    public AreaBean(String code, String name, int level, String pcode, String pname, String pfullname, String path) {
        //处理特殊 市辖区
        if(level > 3 && StrUtil.endWith(code, "000")){
            code = StrUtil.sub(code, 0, code.length()-3);
        }
        this.code = code;
        boolean bk = false;
        if("市辖区".equals(name)) {
            name = "";
            bk = true;
        }
        this.name = name;
        this.level = level;
        this.pcode = pcode;
        if(StrUtil.isBlank(pname))
            pname = pfullname;
        this.pname = pname;
        this.path = path;
        if(StrUtil.isBlank(pfullname)){
            if(bk)
                this.fullname = pname;
            else
                this.fullname = name;
        }else{
            this.fullname = pfullname+name;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return "{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", geo='" + geo + '\'' +
                ", pcode='" + pcode + '\'' +
                ", path='" + path + '\'' +
                ", pname='" + pname + '\'' +
                ", fullname='" + fullname + '\'' +
                '}';
    }

    /**
     * 格式化 地区编码
     * @param code
     * @param level
     * @return
     */
    public static String formatAreaCode(String code, int level){
        if(level == 1){ //省   第1、2位
            return code+"0000";
        }else if(level == 2 || level == 3){  //市 第3、4位， 区、县 第5、6位
            return StrUtil.sub(code, 0, 6);
        }else if(level == 4){ //乡、镇  第7、9位
            return StrUtil.sub(code, 0, 9);
        }
        return code;
    }

}
