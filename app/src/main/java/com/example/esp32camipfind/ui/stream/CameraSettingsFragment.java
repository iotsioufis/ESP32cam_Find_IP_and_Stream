package com.example.esp32camipfind.ui.stream;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.esp32camipfind.R;
import com.example.esp32camipfind.ui.SharedViewModel;


public class CameraSettingsFragment extends Fragment {

    SharedViewModel viewModel;
    WebView CameraSettingsWebview;
    WebView CameraSettingStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_camera_settings, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        CameraSettingsWebview = root.findViewById(R.id.camera_settings_webview);
        CameraSettingStream =root.findViewById(R.id.camera_settings_stream);

        WebSettings webSettings = CameraSettingsWebview.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        CameraSettingsWebview.setWebViewClient(new WebViewClient());

        CameraSettingsWebview.loadUrl("http://" +viewModel.getFullscreenIP().getValue());
        CameraSettingsWebview.setRotation(0);

        WebSettings webSettingsStream = CameraSettingStream.getSettings();
        webSettingsStream.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettingsStream.setUseWideViewPort(true);
        webSettingsStream.setLoadWithOverviewMode(true);
        webSettingsStream.setJavaScriptEnabled(true);
        CameraSettingStream.setWebViewClient(new WebViewClient());
        CameraSettingStream.loadUrl("http://" +viewModel.getFullscreenIP().getValue()+":81/stream");

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

            CameraSettingsWebview.stopLoading(); // Stop loading the webpage
            CameraSettingsWebview.destroy();
            CameraSettingStream.stopLoading();
            CameraSettingStream.destroy();



    }

}
