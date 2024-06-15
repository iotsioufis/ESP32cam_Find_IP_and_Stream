package com.example.esp32camipfind;

public class IpData {
    private String ip;
    private int rotation;

    public IpData(String ip, int rotation) {
        this.ip = ip;
        this.rotation = rotation;
    }

    public String getIp() {
        return ip;
    }


    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
