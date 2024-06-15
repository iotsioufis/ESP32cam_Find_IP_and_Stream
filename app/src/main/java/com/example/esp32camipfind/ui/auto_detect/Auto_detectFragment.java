package com.example.esp32camipfind.ui.auto_detect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.esp32camipfind.FindCamera;
import com.example.esp32camipfind.IpData;
import com.example.esp32camipfind.R;
import com.example.esp32camipfind.databinding.FragmentAutoDetectBinding;
import com.example.esp32camipfind.ui.IPRecyclerAdapter;
import com.example.esp32camipfind.ui.SharedViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Auto_detectFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();
    String json = "";
    FindCamera findCamera = new FindCamera();
    SharedViewModel viewModel;
    private MaterialButton scanButton;
    private FragmentAutoDetectBinding binding;
    ProgressBar progressBar ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAutoDetectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("com.example.esp32camipfind", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        scanButton = root.findViewById(R.id.button_scan_cameras);
        progressBar = root.findViewById(R.id.progressBar);
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_manual);
        IPRecyclerAdapter adapter = new IPRecyclerAdapter(viewModel,navController);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (viewModel.getIpList().getValue() == null) {
            viewModel.getIpList().setValue(new ArrayList<>());

        }
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                findCamera.getClientList(viewModel);
                progressBar.setVisibility(View.VISIBLE);
                AlphaAnimation fadeOut;
                fadeOut = new AlphaAnimation(1,0); //fade out animation from 1 (fully visible) to 0 (transparent)
                fadeOut.setFillBefore(true);
                fadeOut.setDuration(5000); //set duration in mill seconds
                fadeOut.setFillAfter(true);
                progressBar.startAnimation(fadeOut);


            }
        });

        viewModel.getIpList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IpData>>() {
            @Override
            public void onChanged(ArrayList<IpData> items) {
                json = gson.toJson(items);
                editor.putString("storedIPs", json);
                editor.apply();
                adapter.notifyDataSetChanged();
            }
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}