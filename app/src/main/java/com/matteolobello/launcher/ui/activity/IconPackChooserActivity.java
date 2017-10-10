package com.matteolobello.launcher.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.data.loader.IconPackLoader;
import com.matteolobello.launcher.data.model.IconPack;
import com.matteolobello.launcher.ui.adapter.recyclerview.IconPackChooserRecyclerViewAdapter;
import com.matteolobello.launcher.util.SystemBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IconPackChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_pack_chooser);

        SystemBarUtil.enableLightStatusBar(this);

        RecyclerView recyclerView = findViewById(R.id.icon_pack_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> iconPackPackageNames = new ArrayList<>();

        // Default Icon Pack
        iconPackPackageNames.add(null);
        HashMap<String, IconPack> availableIconPacks = IconPackLoader.getAvailableIconPacks(this, false);
        for (Map.Entry<String, IconPack> entry : availableIconPacks.entrySet()) {
            iconPackPackageNames.add(entry.getKey());
        }

        recyclerView.setAdapter(new IconPackChooserRecyclerViewAdapter(iconPackPackageNames));
    }
}
