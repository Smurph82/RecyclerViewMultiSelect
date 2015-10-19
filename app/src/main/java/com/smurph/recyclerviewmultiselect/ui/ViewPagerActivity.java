package com.smurph.recyclerviewmultiselect.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.smurph.recyclerviewmultiselect.Config;
import com.smurph.recyclerviewmultiselect.R;
import com.smurph.recyclerviewmultiselect.ui.base.BaseActivity;
import com.smurph.recyclerviewmultiselect.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Ben on 10/19/2015.
 *
 */
public class ViewPagerActivity extends BaseActivity {

    private static final String tag = ViewPagerActivity.class.getSimpleName();

    private TabLayout mTabLayout;
    @SuppressWarnings("FieldCanBeLocal")
    private Adapter mViewPagerAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);

            mTabLayout = (TabLayout) findViewById(R.id.tabs);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    protected int getLayoutResource() { return R.layout.activity_viewpager; }

    @Override
    protected int getNavigationSelectedPosition() { return 4; }

    @Override
    protected int getNavigationMenuItemId() { return R.id.nav_viewpager_w_cab; }

    @Override
    protected void setupHomeUp() {
        ActionBar ab = getSupportActionBar();
        if (ab==null) { return; }

        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("ViewPager w/ CAB");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void animateAppBarBackgroundColor(@ColorRes int colorFrom, @ColorRes int colorTo) {
        if (mAppBar==null || !Config.isAboveOrEqualAPILvl(Build.VERSION_CODES.HONEYCOMB)) {
            return;
        }

        int from = Config.getResourceColor(this, colorFrom);
        int to = Config.getResourceColor(this, colorTo);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAppBar.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    public void updateTabTitle(int position, @NonNull String title) {
        TabLayout.Tab tab = mTabLayout.getTabAt(position);
        if (tab==null) {
            Log.w(tag, "TabLayout.Tab as position " + position + " is null.");
            return;
        }
        tab.setText(title);
    }

    private void setupViewPager(@Nullable ViewPager viewPager) {
        if (viewPager==null) { return; }

        mViewPagerAdapter = new Adapter(getSupportFragmentManager());
        Random r = new Random();
        mViewPagerAdapter.addFragment(SimpleFragment
                .createFragment(0, "Tab 1".toUpperCase(Locale.US), r.nextInt(25-1+1)+1));
        mViewPagerAdapter.addFragment(SimpleFragment
                .createFragment(1, "Tab 2".toUpperCase(Locale.US), r.nextInt(25-1+1)+1));
        mViewPagerAdapter.addFragment(SimpleFragment
                .createFragment(2, "Tab 3".toUpperCase(Locale.US), r.nextInt(25-1+1)+1));
        viewPager.setAdapter(mViewPagerAdapter);
//        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                if (((BaseFragment) mViewPagerAdapter.getItem(position)).showFAB()) {
//                    // Patient List
//                    // Transcriptions|Signed notes
//                    mBtnAnimaControl.setEnabled(true);
//                    if (!mIsFabIconLoaded) {
//                        Fragment frag = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        if (frag instanceof BaseFragment) {
//                            ((BaseFragment)frag).finishActionMode();
//                            ((FloatingActionButton) mBtnAnimaControl.getAnimatedView())
//                                    .setImageDrawable(((BaseFragment) frag).getFABIconDrawable());
//                        }
//                        mIsFabIconLoaded = true;
//                    }
//                    ViewCompat.setTranslationY(mBtnAnimaControl.getAnimatedView(), 0F);
//                    ((FloatingActionButton)mBtnAnimaControl.getAnimatedView()).show();
//                } else {
//                    // Messages
//                    mBtnAnimaControl.setEnabled(false);
//                    ((FloatingActionButton)mBtnAnimaControl.getAnimatedView()).hide();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    Fragment frag = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                    if (frag instanceof BaseFragment) {
//                        ((FloatingActionButton) mBtnAnimaControl.getAnimatedView())
//                                .setImageDrawable(((BaseFragment) frag).getFABIconDrawable());
//                    }
//                }
//            }
//        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public Adapter(FragmentManager fm) { super(fm); }

        public void addFragment(Fragment fragment) { mFragments.add(fragment); }

        @Override
        public Fragment getItem(int position) { return mFragments.get(position); }

        @Override
        public int getCount() { return mFragments.size(); }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mFragments.get(position) instanceof BaseFragment) {
                return ((BaseFragment)mFragments.get(position)).getTitle();
            }
            return "";
        }
    }
}
