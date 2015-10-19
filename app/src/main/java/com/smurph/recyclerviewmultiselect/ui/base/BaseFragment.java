package com.smurph.recyclerviewmultiselect.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smurph.recyclerviewmultiselect.R;

/**
 * Created by Ben on 10/19/2015.
 *
 */
public abstract class BaseFragment extends Fragment {

    private SwipeRefreshLayout mSwipeLayout;
    protected int mPosition;
    @NonNull
    protected String mTitle="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(hasOptionMenu());
        View v = inflater.inflate(getLayoutResource(), container, false);
        setUpViews(v, savedInstanceState);
        if (getActivity()!=null && getActivity() instanceof BaseActivity) {
            boolean isFragmentRefresh = ((BaseActivity) getActivity()).isFragmentRefresh();
            if (isFragmentRefresh) { setupSwipeRefreshLayout(v); }
        }
        return v;
    }

    protected boolean hasOptionMenu() { return false; }

    @LayoutRes
    protected abstract int getLayoutResource();

    public int getPosition() { return mPosition; }

    public void changePosition(int position) { mPosition = position; }

    @NonNull
    public String getTitle() { return mTitle; }

    public void changeTitle(@NonNull String title) { mTitle = title; }

    protected void setRefreshing(boolean refreshing) {
        if (mSwipeLayout!=null) {  mSwipeLayout.setRefreshing(refreshing); }
    }

    protected void setRefreshingEnabled(boolean enabled) {
        if (mSwipeLayout!=null) { mSwipeLayout.setEnabled(enabled); }
    }

    protected boolean isRefreshing() { return mSwipeLayout!=null && mSwipeLayout.isRefreshing(); }

    protected @NonNull int[] setSwipeRefreshColorSchemeResources() {
        return new int[] { R.color.theme_accent_dark, R.color.theme_primary,
                R.color.theme_primary_dark, R.color.theme_primary_light };
    }

    protected void setSwipeRefreshOffset(int pixels) {
        if (mSwipeLayout==null) { return; }
        mSwipeLayout.setProgressViewOffset(false, 0, pixels);
    }

    protected int getDistanceToTriggerSync() { return -1; }

    private void setupSwipeRefreshLayout(@NonNull View v) {
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        if (mSwipeLayout!=null) {
            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onSwipeLayoutRefresh();
                }
            });
//            setSwipeRefreshOffset(dipsToPix(SWIPE_PROGRESS_VIEW_OFFSET_DEFAULT));
            mSwipeLayout.setColorSchemeResources(setSwipeRefreshColorSchemeResources());
            mSwipeLayout.setDistanceToTriggerSync(getDistanceToTriggerSync());
        }
    }

    protected void onSwipeLayoutRefresh() { }

    protected abstract void setUpViews(@NonNull View v, @Nullable Bundle savedInstanceState);
}
