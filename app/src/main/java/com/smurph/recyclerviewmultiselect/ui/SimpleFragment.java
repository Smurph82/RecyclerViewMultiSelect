package com.smurph.recyclerviewmultiselect.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.smurph.multiselectlib.MultiSelectAdapter;
import com.smurph.recyclerviewmultiselect.R;
import com.smurph.recyclerviewmultiselect.ui.adapter.SimpleStringRecyclerViewAdapter;
import com.smurph.recyclerviewmultiselect.ui.base.BaseFragment;
import com.smurph.recyclerviewmultiselect.ui.widget.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Ben on 10/19/2015.
 *
 */
public class SimpleFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private SimpleStringRecyclerViewAdapter mAdapter;
    private View mLayout;
    private ArrayList<String> mItems;

    public static SimpleFragment createFragment(int position, @NonNull String title,
                                                int itemCount) {
        SimpleFragment frag = new SimpleFragment();
        frag.init(position, title, itemCount);
        return frag;
    }

    public void init(int position, @NonNull String title, int itemCount) {
        changePosition(position);
        changeTitle(title);
        mItems = new ArrayList<>(itemCount);
        for (int i=0;i<itemCount;i++) { mItems.add(Integer.toString(i+1)); }
    }

    @Override
    protected int getLayoutResource() { return R.layout.fragment_list; }

    @NonNull
    @Override
    public String getTitle() {
        if (mAdapter==null) { return mTitle + " (0)"; }
        return mTitle + " (" + mAdapter.getItemCount() + ")";
    }

    @Override
    protected void setUpViews(@NonNull View v, @Nullable Bundle savedInstanceState) {
        mLayout = v.findViewById(R.id.main_content_fragment);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SimpleStringRecyclerViewAdapter(getActivity(), mItems,
                SimpleStringRecyclerViewAdapter.HELPER_VIEWPAGER_CAB);
//        mAdapter.setOnInnerItemClickedListener(mInnerClickListener);
        mAdapter.setAdapterListener(new SimpleStringRecyclerViewAdapter.AdapterListener() {
            @Override
            public void onCABMenuStart() {
                if (getActivity() instanceof ViewPagerActivity) {
                    ((ViewPagerActivity)getActivity()).animateAppBarBackgroundColor(
                            R.color.theme_primary, R.color.theme_accent);
                }
            }
            @Override
            public void onCABMenuFinish() {
                if (getActivity() instanceof ViewPagerActivity) {
                    ((ViewPagerActivity)getActivity()).animateAppBarBackgroundColor(
                            R.color.theme_accent, R.color.theme_primary);
                }
            }
            @Override
            public void updateTitleCount(int count) {

            }
        });
        mAdapter.setOnItemClickedListener(new MultiSelectAdapter.SimpleOnItemClickedListener() {
            @Override
            public void onItemClicked(@NonNull View v, int position, boolean isSelectionMode,
                                      boolean isExitingActionMode) {
                if (isSelectionMode || isExitingActionMode) {
                    return;
                }

                Toast.makeText(v.getContext(), "You clicked: " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        mAdapter.restoreSelectedItems(getContext(), savedInstanceState);
        ((ViewPagerActivity)getActivity()).updateTabTitle(getPosition(), getTitle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter!=null) { mAdapter.saveSelectedItems(outState); }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onSwipeLayoutRefresh() {
        mAdapter.setIsClickingEnabled(false);
        Toast.makeText(getContext(),
                "Clicking disabled while something processes.", Toast.LENGTH_SHORT).show();
        setRefreshingEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setIsClickingEnabled(true);
                setRefreshingEnabled(true);
                setRefreshing(false);
            }
        }, 3000L);
    }
}
