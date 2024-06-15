package com.example.esp32camipfind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.esp32camipfind.databinding.ActivityMainBinding;
import com.example.esp32camipfind.ui.SharedViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public SharedViewModel viewModel;
    SharedPreferences sharedPreferences;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedPreferences = this.getSharedPreferences("com.example.esp32camipfind", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("storedIPs", null);
        Type type = new TypeToken<ArrayList<IpData>>() {
        }.getType();
        ArrayList<IpData> ipDataList = gson.fromJson(json, type);
        viewModel.getIpList().setValue(ipDataList);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_stream, R.id.nav_auto_detect, R.id.nav_manual_set)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public void onBackPressed() {
        // Get the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Get the current destination
        NavDestination currentDestination = navController.getCurrentDestination();

        // Check if the current destination is the home destination
        if (currentDestination != null && currentDestination.getId() == R.id.nav_stream) {
            // Terminate the activity when the home fragment is active and back button is pressed
            finish();
        } else {
            // If the current fragment is not the home fragment, proceed with default back button behavior
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}