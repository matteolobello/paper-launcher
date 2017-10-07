package com.matteolobello.launcher.ui.adapter.recyclerview;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.data.preference.IconPackSelectHelper;

import java.util.ArrayList;

public class IconPackChooserRecyclerViewAdapter extends RecyclerView.Adapter<IconPackChooserRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mIconPackPackageNamesArrayList;

    public IconPackChooserRecyclerViewAdapter(ArrayList<String> iconPackPackageNamesArrayList) {
        mIconPackPackageNamesArrayList = iconPackPackageNamesArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon_pack, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String packageName = mIconPackPackageNamesArrayList.get(position);

        if (packageName == null) {
            holder.iconPackIconImageView.setImageResource(R.drawable.defaul_icon_pack_app_icon);
            holder.iconPackNameTextView.setText(R.string.default_icon_pack);
        } else {

            ApplicationInfo applicationInfo;
            try {
                applicationInfo = holder.itemView.getContext().getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                return;
            }

            holder.iconPackIconImageView.setImageDrawable(applicationInfo.loadIcon(
                    holder.itemView.getContext().getPackageManager()));
            holder.iconPackNameTextView.setText(applicationInfo.loadLabel(
                    holder.itemView.getContext().getPackageManager()));
        }

        holder.itemView.setOnClickListener((view -> {
            IconPackSelectHelper.get().setIconPack(view.getContext(), packageName);

            Intent launcherSelectorIntent = new Intent();
            launcherSelectorIntent.setAction(Intent.ACTION_MAIN);
            launcherSelectorIntent.addCategory(Intent.CATEGORY_HOME);
            launcherSelectorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            view.getContext().startActivity(Intent.createChooser(launcherSelectorIntent,
                    view.getContext().getString(R.string.default_icon_pack)));
        }));
    }

    @Override
    public int getItemCount() {
        return mIconPackPackageNamesArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconPackIconImageView;
        TextView iconPackNameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            iconPackIconImageView = itemView.findViewById(R.id.icon_pack_icon_image_view);
            iconPackNameTextView = itemView.findViewById(R.id.icon_pack_name_text_view);
        }
    }
}
