package com.example.esp32camipfind.ui.stream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.esp32camipfind.CustomWebView;
import com.example.esp32camipfind.IpData;
import com.example.esp32camipfind.R;
import com.example.esp32camipfind.ui.SharedViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class StreamFragment extends Fragment {

    SharedPreferences sharedPreferences;
    Gson gson = new Gson();
    String json = "";
    private SharedViewModel viewModel;
    private GridLayout gridLayout;
    ArrayList<IpData> ipList;
    private ConstraintLayout constraintLayout;
    private final List<WebView> webViews = new ArrayList<>();
    ImageView no_cameras_added_image ;
    TextView no_cameras_added_text;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stream, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        gridLayout = root.findViewById(R.id.gridLayout);
        sharedPreferences = getActivity().getSharedPreferences("com.example.esp32camipfind", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        no_cameras_added_image= root.findViewById(R.id.no_cameras_added_imageview);
        no_cameras_added_text=root.findViewById(R.id.no_cameras_added_textView);
        ipList = viewModel.getIpList().getValue();
        addCamerasToGridLayout();
        viewModel.getIpList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IpData>>() {
            @Override
            public void onChanged(ArrayList<IpData> items) {
                json = gson.toJson(items);
                editor.putString("storedIPs", json);
                editor.apply();
            }
        });
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the MenuProvider to the Fragment's view lifecycle owner after the view has been created
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main, menu);
                MenuItem item = menu.findItem(R.id.icon_refresh_previews);
                if (ipList == null || ipList.isEmpty()) {
                    no_cameras_added_image.setVisibility(View.VISIBLE);
                    no_cameras_added_text.setVisibility(View.VISIBLE);
                    item.setVisible(false);
                }
                else {
                item.setVisible(true);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.icon_refresh_previews) {
                    for (WebView webView : webViews) {
                        webView.stopLoading();
                        webView.destroy();
                    }
                    addCamerasToGridLayout();
                    return true;
                }
                return false;
            }


        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    private void addCamerasToGridLayout() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        if (ipList == null || ipList.isEmpty()) {
            no_cameras_added_image.setVisibility(View.VISIBLE);
            no_cameras_added_text.setVisibility(View.VISIBLE);
            return;
        }
        no_cameras_added_image.setVisibility(View.GONE);
        no_cameras_added_text.setVisibility(View.GONE);
        // Get screen width
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Define minimum tile width
        int minTileWidth = (int) (300 * displayMetrics.density);
        if (ipList.size() > 2) {
            minTileWidth = (int) (180 * displayMetrics.density);
        }
        // Calculate number of columns based on screen width and minimum tile width
        int columnCount = Math.max(1, screenWidth / minTileWidth);

        // Calculate number of rows based on the number of WebViews and columns
        int rowCount = (int) Math.ceil(ipList.size() / (float) columnCount);

        // Set the column count dynamically
        gridLayout.setColumnCount(columnCount);
        gridLayout.setRowCount(rowCount);
        gridLayout.removeAllViews(); // Clear previous views

        for (int i = 0; i < ipList.size(); i++) {
            String url = ipList.get(i).getIp();
            View cameraView = inflater.inflate(R.layout.camera_item_layout, gridLayout, false);
            CustomWebView webView = cameraView.findViewById(R.id.webview);
            ImageView errorImageView = cameraView.findViewById(R.id.errorImageView);
            ImageView rotate_icon = cameraView.findViewById(R.id.rotate_icon);
            ImageView settings_icon = cameraView.findViewById(R.id.settings_icon);
            ImageView fullscreen_icon = cameraView.findViewById(R.id.fullscreen_icon);
            TextView ipText = cameraView.findViewById(R.id.ip_address_text);
            constraintLayout = cameraView.findViewById(R.id.camera_item_layout);
            ipText.setText(url);

            rotate_icon.setOnClickListener(new View.OnClickListener() {
                private int rotationAngle = viewModel.getRotationValueForIp(url);

                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    // Update rotation angle
                    rotationAngle += 90;
                    rotationAngle = rotationAngle % 360; // Ensure rotation angle stays within 0-360 range
                    // Apply rotation transformation to the WebView
                    viewModel.setRotationValueForIp(url, rotationAngle);
                    webView.setRotation(rotationAngle);
                }
            });

            fullscreen_icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    viewModel.setFullscreenIP(url);
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_stream_to_fullscreen_fragment);

                }
            });

            settings_icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    viewModel.setFullscreenIP(url);
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_stream_to_nav_camera_settings_fragment);

                }
            });
            // Generate a unique ID for the WebView
            int uniqueId = View.generateViewId();
            webView.setId(uniqueId);
            webViews.add(webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setJavaScriptEnabled(true);

            // Set WebViewClient to handle errors
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    settings_icon.setVisibility(View.VISIBLE);
                    fullscreen_icon.setVisibility(View.VISIBLE);
                    rotate_icon.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    settings_icon.setVisibility(View.INVISIBLE);
                    fullscreen_icon.setVisibility(View.INVISIBLE);
                    rotate_icon.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                    webView.setRotation(0);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(ipText.getId(), ConstraintSet.TOP, webView.getId(), ConstraintSet.BOTTOM);
                    constraintSet.connect(webView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                    constraintSet.applyTo(constraintLayout);


                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    errorImageView.setVisibility(View.VISIBLE);
                    // webView.setVisibility(View.GONE);
                }
            });

            // Reset visibility before loading the URL
            errorImageView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("http://" +url + ":81/stream");
            webView.setRotation(viewModel.getRotationValueForIp(url));
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = 0;
            layoutParams.columnSpec = GridLayout.spec(i % columnCount, 1f); // Set column weight
            layoutParams.rowSpec = GridLayout.spec(i / columnCount); // Set row index
            layoutParams.setMargins(8, 8, 8, 8); // Add margins
            cameraView.setLayoutParams(layoutParams);

            gridLayout.addView(cameraView);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        for (WebView webView : webViews) {
            //webView.stopLoading(); // Stop loading the webpage
            webView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        for (WebView webView : webViews) {
            webView.onResume();

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (WebView webView : webViews) {
            webView.stopLoading(); // Stop loading the webpage
            webView.destroy();
        }


    }
}