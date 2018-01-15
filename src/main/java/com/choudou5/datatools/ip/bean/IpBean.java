package com.choudou5.datatools.ip.bean;

import java.io.Serializable;

/**
 * @Name: Ip Bean
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class IpBean implements Serializable{
    private String ip;
    private int port;

    public IpBean() {
    }

    public IpBean(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
            this.port = port;
        }
}