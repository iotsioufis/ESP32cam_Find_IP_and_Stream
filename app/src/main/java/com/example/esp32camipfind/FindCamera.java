package com.example.esp32camipfind;

import android.util.Log;

import com.example.esp32camipfind.ui.SharedViewModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class FindCamera {


    String ip_str = "";
    String camera_ip = "";

    public static boolean isIPv4(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    public void getClientList(SharedViewModel viewModel) {        // This works both in tethering and when connected to an Access Point

        Enumeration<NetworkInterface> interfaces = null;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (interfaces.hasMoreElements()) {

            NetworkInterface networkInterface = interfaces.nextElement();

            try {
                if (networkInterface.isLoopback())
                    continue; // Don't want to broadcast to the loopback interface
            } catch (SocketException e) {
                e.printStackTrace();
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                ip_str = "";
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast != null) {
                    if (isIPv4(broadcast.getHostAddress())) {
                        String[] arrOfStr = broadcast.getHostAddress().split("\\.", 4);
                        ip_str = ip_str + arrOfStr[0] + "." + arrOfStr[1] + "." + arrOfStr[2] + ".";
                        Log.e("ip_str: ", ip_str);
                        for (int i = 0; i <= 246; i = i + 6) {
                            search_ranged_ip_thread(ip_str, i, i + 5, viewModel);
                        }
                        search_ranged_ip_thread(ip_str, 252, 255, viewModel);


                    }
                }

            }

        }
    }

    void search_ranged_ip_thread(String ip, int from, int to, SharedViewModel viewModel) {


        Runnable runnable_search_ranged_ip = new Runnable() {
            @Override
            public void run() {

                boolean reachable = false;
                final String[] reachable_ip = {""};
                String ip_to_check = ip;
                {

                    int i = 0;
                    Log.e("possible_ips", ip + from + " to " + to);
                    if (ip != "") {
                        try {
                            for (i = from; i <= to; i++) {

                                InetAddress address = InetAddress.getByName(ip_to_check + i);
                                reachable = address.isReachable(250);
                                if (reachable) {
                                    int finalI = i;

                                    reachable_ip[0] = ip_to_check + finalI;
                                    try_to_connect(reachable_ip[0], viewModel);
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                }


            }

        };


        Thread newThread = new Thread(runnable_search_ranged_ip);
        newThread.start();

    }

    public boolean try_to_connect(String ip, SharedViewModel viewModel) {
        boolean success = false;
        URL url = null;
        try {
            url = new URL("http://" + ip + "/capture?_cb");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection huc = null;
        try {
            huc = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = 0;
        try {
            responseCode = huc.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (responseCode == 200) {


            viewModel.addIP(ip, 0);
            success = true;
            camera_ip = ip;
        } else {
            success = false;


        }
        return success;
    }
}
