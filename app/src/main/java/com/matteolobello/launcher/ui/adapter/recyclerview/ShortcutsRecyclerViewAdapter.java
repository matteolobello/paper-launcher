package com.matteolobello.launcher.ui.adapter.recyclerview;

import android.content.pm.ShortcutInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.util.SDKUtil;
import com.matteolobello.launcher.util.ShortcutUtil;

import java.util.List;

public class ShortcutsRecyclerViewAdapter extends RecyclerView.Adapter<ShortcutsRecyclerViewAdapter.ViewHolder> {

    private final List<ShortcutInfo> mShortcuts;

    public ShortcutsRecyclerViewAdapter(List<ShortcutInfo> shortcuts) {
        mShortcuts = shortcuts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shortcut, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!SDKUtil.AT_LEAST_N_MR1) {
            return;
        }

        final ShortcutInfo shortcutInfo = mShortcuts.get(position);

        holder.titleTextView.setText(shortcutInfo.getShortLabel());
        holder.iconImageView.setImageDrawable(ShortcutUtil.loadDrawable(holder.itemView.getContext(), shortcutInfo));
        holder.itemView.setOnClickListener(view -> ShortcutUtil.launchShortcut(view, shortcutInfo));
    }

    @Override
    public int getItemCount() {
        return mShortcuts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView titleTextView;

        ViewHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.shortcut_image);
            titleTextView = itemView.findViewById(R.id.shortcut_text);
        }
    }
}
