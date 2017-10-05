package com.matteolobello.launcher.ui.adapter.recyclerview;

import android.content.pm.ApplicationInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.ui.dialog.SelectDockIconDialog;
import com.matteolobello.launcher.util.IconUtil;

import java.util.List;

public class SelectDockIconRecyclerViewAdapter extends RecyclerView.Adapter<SelectDockIconRecyclerViewAdapter.ViewHolder> {

    private final SelectDockIconDialog mSelectDockIconDialog;
    private final List<ApplicationInfo> mApplicationInfoArrayList;

    public SelectDockIconRecyclerViewAdapter(SelectDockIconDialog selectDockIconDialog,
                                             List<ApplicationInfo> applicationInfoArrayList) {
        mSelectDockIconDialog = selectDockIconDialog;
        mApplicationInfoArrayList = applicationInfoArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_dock_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApplicationInfo applicationInfo = mApplicationInfoArrayList.get(position);

        holder.appTitleTextView.setText(applicationInfo.loadLabel(holder.itemView.getContext().getPackageManager()));
        IconUtil.setIconOnImageView(mSelectDockIconDialog.getLauncherActivity(),
                holder.appIconImageView, applicationInfo);

        holder.itemView.setOnClickListener(view -> mSelectDockIconDialog.onAppSelected(applicationInfo));
    }

    @Override
    public int getItemCount() {
        return mApplicationInfoArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView appTitleTextView;
        private ImageView appIconImageView;

        ViewHolder(View itemView) {
            super(itemView);

            appTitleTextView = itemView.findViewById(R.id.select_dock_icon_app_name);
            appIconImageView = itemView.findViewById(R.id.select_dock_icon_app_icon);
        }
    }
}
