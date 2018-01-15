package com.choudou5.datatools.area.bean;

import java.io.Serializable;

/**
 * @Name: 地区Bean
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class AreaBean implements Serializable {

    private String code;
    private String name;
    private int level;
    private String geo; //'地理经纬度'
    private String pcode;
    private String path;
    private String pname;

    public AreaBean() {
    }

    public AreaBean(String code, String name, int level) {
        this.code = code;
        this.name = name;
        this.level = level;
    }

    public AreaBean(String code, String name, int level, String pcode, String pname, String path) {
        this.code = code;
        this.name = name;
        this.level = level;
        this.pcode = pcode;
        this.pname = pname;
        this.path = path;
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
                '}';
    }


}
