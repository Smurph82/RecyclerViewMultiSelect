package com.smurph.recyclerviewmultiselect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.smurph.multiselectlib.MultiSelectAdapter;
import com.smurph.recyclerviewmultiselect.R;
import com.smurph.recyclerviewmultiselect.ui.adapter.SimpleStringRecyclerViewAdapter;
import com.smurph.recyclerviewmultiselect.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Murphy on 8/7/15.
 *
 */
public class MultiWithCABActivity extends BaseActivity {

    private List<String> mList;
    {
        mList = new ArrayList<>(25);
        for (int i=0;i<25;i++) { mList.add(Integer.toString(i + 1)); }
    }

    private RecyclerView mRecyclerView;

    @IdRes
    private static final int MENU_ITEM = R.id.nav_multi_w_cab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupRecyclerView(R.id.recyclerview, savedInstanceState);
    }

    @Override
    protected int getLayoutResource() { return R.layout.activity_multi_with_cab; }

    @Override
    protected void setupHomeUp() {
        ActionBar ab = getSupportActionBar();
        if (ab==null) { return; }

        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("Multi w/ CAB");
    }

    @Override
    protected int getNavigationSelectedPosition() { return 0; }

    @Override
    protected int getNavigationMenuItemId() { return R.id.nav_multi_w_cab; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_show:
                Snackbar.make(findViewById(R.id.main_content),
                        "Select position(s): " +
                                ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter())
                                        .getSelectedPositions(),
                        Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter()).saveSelectedItems(outState);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView(@IdRes int id, @Nullable Bundle bundle) {
        mRecyclerView = (RecyclerView) findViewById(id);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.hasFixedSize();
        mRecyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(this, mList,
                SimpleStringRecyclerViewAdapter.HELPER_MULTI_WITH_CAB));
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter())
                .setOnItemClickedListener(new MultiSelectAdapter.SimpleOnItemClickedListener() {
                    @Override
                    public void onItemClicked(@NonNull View v, int position,
                                              boolean isSelectionMode) {
                        if (isSelectionMode) { return; }
                        Toast.makeText(MultiWithCABActivity.this, "You clicked: " + position,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter())
                .restoreSelectedItems(this, bundle);
    }

    @Override
    protected void onSwipeLayoutRefresh() {
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter()).setIsClickingEnabled(false);
        Toast.makeText(this,
                "Clicking disabled while something processes.", Toast.LENGTH_SHORT).show();
        setRefreshingEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter())
                        .setIsClickingEnabled(true);
                setRefreshingEnabled(false);
                setRefreshing(false);
            }
        }, 5000L);
    }
}
