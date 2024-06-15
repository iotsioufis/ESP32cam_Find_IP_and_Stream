package com.example.esp32camipfind.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.esp32camipfind.R;


public class FullScreenFragment extends Fragment {
    SharedViewModel viewModel;
    WebView fullscreenwebview;
    SeekBar zoomLevelSeekBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_full_screen, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        fullscreenwebview = root.findViewById(R.id.fullscreenwebview);
        zoomLevelSeekBar = root.findViewById(R.id.zoom_level);

        WebSettings webSettings = fullscreenwebview.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);

        fullscreenwebview.setWebViewClient(new WebViewClient());

        fullscreenwebview.loadUrl("http://" +viewModel.getFullscreenIP().getValue() + ":81/stream");


        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullscreenwebview.setRotation(0);
            // Landscape

        } else {
            fullscreenwebview.setRotation(viewModel.getRotationValueForIp(viewModel.getFullscreenIP().getValue()));
            // Portrait

        }
        zoomLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<20){
                    progress=20;
                }
                int zoom = -100 + progress;
                float zoomFactor = 1 + ((zoom + 50) / 50.0f); // Adjust zoom factor as needed
                fullscreenwebview.setScaleX(zoomFactor);
                fullscreenwebview.setScaleY(zoomFactor);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        return root;
    }




    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);



        int orientation = newConfig.orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                fullscreenwebview.setRotation(viewModel.getRotationValueForIp(viewModel.getFullscreenIP().getValue()));
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                fullscreenwebview.setRotation(0);
                break;
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        // Stop loading the webpage
        fullscreenwebview.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        fullscreenwebview.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        fullscreenwebview.stopLoading(); // Stop loading the webpage
        fullscreenwebview.destroy();

    }


}

