package com.example.esp32camipfind.ui.manual_set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esp32camipfind.IpData;
import com.example.esp32camipfind.R;
import com.example.esp32camipfind.databinding.FragmentManualSetBinding;
import com.example.esp32camipfind.ui.IPRecyclerAdapter;
import com.example.esp32camipfind.ui.SharedViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;


public class ManualSetFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();
    String json = "";
    private FragmentManualSetBinding binding;
    private SharedViewModel viewModel;
    private MaterialButton saveButton;
    private EditText editTextIP;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentManualSetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("com.example.esp32camipfind", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        editTextIP = root.findViewById(R.id.editTextIPs);
        saveButton = root.findViewById(R.id.button_save_ip);

        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_manual);
        IPRecyclerAdapter adapter = new IPRecyclerAdapter(viewModel,navController);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newItem = editTextIP.getText().toString();
                if (isValidIpAddress(newItem)) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    viewModel.addIP(newItem, 0);
                    editTextIP.getText().clear();
                } else if (!isValidIpAddress(newItem)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    if(newItem.equals("")){Toast.makeText(getContext(), "Enter an IP address ", Toast.LENGTH_LONG).show();}
                    else{
                    Toast.makeText(getContext(), "Invalid IP address ", Toast.LENGTH_LONG).show();}
                }


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

    boolean isValidIpAddress(String ipAddress) {
        return Patterns.IP_ADDRESS.matcher(ipAddress).matches();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}