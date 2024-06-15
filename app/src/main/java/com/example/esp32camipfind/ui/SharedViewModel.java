package com.example.esp32camipfind.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.esp32camipfind.IpData;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<IpData>> IpList = new MutableLiveData<>();
    private final MutableLiveData<String> fullscreenIP = new MutableLiveData<>();

    public MutableLiveData<String> getFullscreenIP() {
        return fullscreenIP;
    }

    public void setFullscreenIP(String ip) {
        fullscreenIP.setValue(ip);
    }

    public MutableLiveData<ArrayList<IpData>> getIpList() {
        return IpList;
    }

    public void addIP(String ip, int rotation) {
        if (IpList.getValue() == null) {
            IpList.postValue(new ArrayList<>());
        }
        boolean ipExists = false;

        for (IpData ipData : IpList.getValue()) {
            if (ipData.getIp().equals(ip)) {
                ipExists = true;
                break;
            }
        }
        if (!ipExists) {
            IpList.getValue().add(new IpData(ip, rotation));
            IpList.postValue(IpList.getValue());
        }
    }


    public void setRotationValueForIp(String ip, int rotation) {
        if (IpList.getValue() != null) {
            for (IpData ipData : IpList.getValue()) {
                if (ipData.getIp().equals(ip)) {
                    ipData.setRotation(rotation);
                    IpList.postValue(IpList.getValue());
                    break;
                }
            }
        }
    }

    public int getRotationValueForIp(String ip) {
        if (IpList.getValue() != null) {
            for (IpData ipData : IpList.getValue()) {
                if (ipData.getIp().equals(ip)) {
                    return ipData.getRotation();
                }
            }
        }
        return -1; // Return -1 or any invalid rotation value if IP not found
    }

    public void removeIP(int item) {
        if (IpList.getValue() != null) {
            IpList.getValue().remove(item);
            IpList.postValue(IpList.getValue());
        }
    }
}
