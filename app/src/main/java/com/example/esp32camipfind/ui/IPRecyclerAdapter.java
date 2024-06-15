package com.example.esp32camipfind.ui;


import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.example.esp32camipfind.R;


public class IPRecyclerAdapter extends RecyclerView.Adapter<IPRecyclerAdapter.ViewHolder> {

    SharedViewModel viewModel;
    NavController navController;


    public IPRecyclerAdapter(SharedViewModel viewModel, NavController navController) {
        this.navController = navController;
        this.viewModel = viewModel;
    }

    @Override
    public IPRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ip_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(viewModel.getIpList().getValue().get(position).getIp());
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) { // Check if item still exists
                    viewModel.removeIP(currentPosition);
                    notifyDataSetChanged();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                navController.navigate(R.id.nav_stream);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (viewModel.getIpList().getValue() != null) {
            return viewModel.getIpList().getValue().size();
        } else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView deleteIcon;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text);
            deleteIcon = v.findViewById(R.id.delete_icon);
        }
    }
}