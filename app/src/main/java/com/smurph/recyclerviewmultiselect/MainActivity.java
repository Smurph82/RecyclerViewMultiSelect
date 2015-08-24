package com.smurph.recyclerviewmultiselect;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smurph.multiselectlib.MultiSelectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Murphy on 8/7/15.
 *
 */
public class MainActivity extends AppCompatActivity {

    private List<String> mList;
    {
        mList = new ArrayList<>(25);
        for (int i=0;i<25;i++) { mList.add(Integer.toString(i + 1)); }
    }

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;

    // Default ActionBar height 52
    protected static final int SWIPE_PROGRESS_VIEW_OFFSET_DEFAULT = 62;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView(R.id.recyclerview, savedInstanceState);
        setupSwipeRefreshLayout();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar==null) { return; }
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setTitle("MultiSelect Helper");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        mRecyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(this, mList));
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter()).restoreSelectedItems(bundle);
    }


    protected void onSwipeLayoutRefresh() {
        ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter()).setIsClickingEnabled(false);
        Toast.makeText(this,
                "Clicking disabled while something processes.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((SimpleStringRecyclerViewAdapter)mRecyclerView.getAdapter())
                        .setIsClickingEnabled(true);
                setRefreshing(false);
            }
        }, 5000L);
    }

    protected void setRefreshing(boolean refreshing) {
        if (mSwipeLayout!=null) {  mSwipeLayout.setRefreshing(refreshing); }
    }

    protected @NonNull int[] setSwipeRefreshColorSchemeResources() {
        return new int[] { R.color.theme_primary_light, R.color.theme_primary,
                R.color.theme_primary_dark, R.color.theme_accent_dark };
    }

    protected void setSwipeRefreshOffset(int pixels) {
        if (mSwipeLayout==null) { return;
        }

        mSwipeLayout.setProgressViewOffset(false, 0, pixels);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        if (mSwipeLayout != null) {
            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onSwipeLayoutRefresh();
                }
            });
            mSwipeLayout.setColorSchemeResources(setSwipeRefreshColorSchemeResources());
        }
    }

    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        public MultiSelectHelper mHelper;

        public SimpleStringRecyclerViewAdapter(@NonNull Context context,
                                               @Nullable List<String> items) {
            //NOTE  Helper with Contextual Action Mode (CAB)
//            mHelper = new MultiSelectHelper(context, R.id.tag_position);

            // NOTE Helper with CAB but only selects one item at a time
//            mHelper = new MultiSelectHelper(context, R.id.tag_position)
//                    .setSingleSelectMode(true);

            // NOTE If you setActionModeEnabled(false) this does not have to be set.
//            mHelper.setActionModeCallback(mActionModeCallback);

            //NOTE Helper that has no CAB and will only select one item at a time
            mHelper = new MultiSelectHelper(context, R.id.tag_position)
                    .setActionModeEnabled(false)
                    .setSingleSelectMode(true);

            // NOTE Helper that has no CAB but still can select multiple positions
//            mHelper = new MultiSelectHelper(context, R.id.tag_position)
//                    .setActionModeEnabled(false);

            //NOTE Examples to show setting selected color at runtime.
//            mHelper.setSelectedColor(Color.CYAN);
//            mHelper.setSelectedColor(MainActivity.this, R.color.theme_cyan);
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground,
                    mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        public String getSelectedPositions() {
            return mHelper==null ? "-1" : mHelper.getSelectedPositions().toString();
        }

        public void saveSelectedItems(@NonNull Bundle bundle) {
            mHelper.saveSelectedPositions(bundle);
        }

        public void restoreSelectedItems(@Nullable Bundle bundle) {
            if (bundle==null) { return; }

            mHelper.restoreSelectedPositions(MainActivity.this, bundle);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            v.setBackgroundResource(mBackground);
//            v.setOnClickListener(mHelper);
//            v.setOnLongClickListener(mHelper);
            mHelper.setView(v);
            mHelper.setOnMultiSelectListener(mListener);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setTag(R.id.tag_position, position);
            if (mHelper.getIsSelected(position)) {
                mHelper.setRippleColor(holder.itemView);
                holder.mTxtView.setTextColor(Color.WHITE);
            } else {
                holder.itemView.setBackgroundResource(mBackground);
                holder.mTxtView.setTextColor(Color.BLACK);
            }
            holder.mString = mValues.get(position);
            holder.mTxtView.setText(mValues.get(position));
        }

        @Override
        public int getItemCount() { return mValues.size(); }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mString;
            public TextView mTxtView;

            public ViewHolder(View v) {
                super(v);
                mTxtView = (TextView) v.findViewById(android.R.id.text1);
            }
        }

        public void setIsClickingEnabled(boolean isEnabled) {
            mHelper.setIsClickingEnabled(isEnabled);
        }

        private MultiSelectHelper.OnMultiSelectListener mListener =
                new MultiSelectHelper.OnMultiSelectListener() {

                    @Override
                    public void onClick(View v, boolean isSelectionMode) {
                        if (isSelectionMode) { return; }

                        Toast.makeText(MainActivity.this, "Position " +
                                v.getTag(R.id.tag_position) +
                                " clicked.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }

                    @Override
                    public void itemChangedAt(int position) {
                        notifyItemChanged(position);
                    }
                };

        private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.cab_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        Toast.makeText(MainActivity.this, "Delete these.", Toast.LENGTH_SHORT).show();
                        List<Integer> list = mHelper.getSelectedPositions();
                        for (Integer i : list) { notifyItemChanged(i); }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) { mHelper.destroyActionMode(); }
        };
    }
}
