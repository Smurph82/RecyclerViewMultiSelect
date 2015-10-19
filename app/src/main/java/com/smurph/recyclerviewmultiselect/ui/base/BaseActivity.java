package com.smurph.recyclerviewmultiselect.ui.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.smurph.recyclerviewmultiselect.Config;
import com.smurph.recyclerviewmultiselect.R;
import com.smurph.recyclerviewmultiselect.ui.MultiWithCABActivity;
import com.smurph.recyclerviewmultiselect.ui.MultiWithoutCABActivity;
import com.smurph.recyclerviewmultiselect.ui.SingleWithCABActivity;
import com.smurph.recyclerviewmultiselect.ui.SingleWithoutCABActivity;
import com.smurph.recyclerviewmultiselect.ui.ViewPagerActivity;

/**
 * Created by Ben on 6/17/2015.
 *
 */
public abstract class BaseActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private Toolbar mActionBarToolbar;
    protected AppBarLayout mAppBar;

    protected long TRANSITION_DURATION;
    // Default ActionBar height 52
    protected static final int SWIPE_PROGRESS_VIEW_OFFSET_DEFAULT = 62;
    private boolean mIsFragmentRefresh;

    private static final String tag = BaseActivity.class.getSimpleName();

    protected static final boolean D = Config.DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.isFragmentRefresh});
        mIsFragmentRefresh = a.getBoolean(0, false);
        a.recycle();

        updateFromBundle(savedInstanceState);

        // TRANSITION_DURATION must be set before setupWindowTransition() is called.
        TRANSITION_DURATION = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        setupWindowTransition();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupDrawerLayout();
        setupNavigationView();
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    protected void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState==null) { return; }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); }

    private void setupWindowTransition() {
        if (Config.isAboveOrEqualAPILvl(Build.VERSION_CODES.LOLLIPOP)) { setupWindowAnimation(); }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupWindowAnimation() {
        getWindow().setEnterTransition(new Explode().setDuration(TRANSITION_DURATION));
        getWindow().setReturnTransition(new Fade().setDuration(TRANSITION_DURATION));
    }

    private void setupToolbar() { if (getActionBarToolbar()!=null) { setupHomeUp(); } }

    protected Toolbar getActionBarToolbar() {
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) { setSupportActionBar(mActionBarToolbar); }
        }
        return mActionBarToolbar;
    }

    protected void setupHomeUp() {
        // Space for rent.
    }

    protected boolean containsDrawerLayout() { return mDrawerLayout!=null; }

    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    public boolean isFragmentRefresh() { return mIsFragmentRefresh; }

    private void setupNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            navigationView.getMenu().getItem(getNavigationSelectedPosition()).setChecked(true);
        }
    }

    protected abstract int getNavigationSelectedPosition();

    private void setupDrawerContent(@NonNull NavigationView navigationView) {
        if (mDrawerLayout==null) { return; }

        navigationView.setNavigationItemSelectedListener(getNavigationListener());
    }

    protected abstract int getNavigationMenuItemId();

    protected NavigationView.OnNavigationItemSelectedListener getNavigationListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (getNavigationMenuItemId() == menuItem.getItemId()) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                final Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_multi_w_cab:
                        intent = new Intent(BaseActivity.this, MultiWithCABActivity.class);
                        break;
                    case R.id.nav_single_w_cab:
                        intent = new Intent(BaseActivity.this, SingleWithCABActivity.class);
                        break;
                    case R.id.nav_multi_wo_cab:
                        intent = new Intent(BaseActivity.this, MultiWithoutCABActivity.class);
                        break;
                    case R.id.nav_single_wo_cab:
                        intent = new Intent(BaseActivity.this, SingleWithoutCABActivity.class);
                        break;
                    case R.id.nav_viewpager_w_cab:
                        intent = new Intent(BaseActivity.this, ViewPagerActivity.class);
                        break;
                    default:
                        intent = null;
                        break;
                }
                if (intent != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            finish();
                        }
                    }, TRANSITION_DURATION);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        };
    }

    protected void setRefreshing(boolean refreshing) {
        if (mSwipeLayout!=null) {  mSwipeLayout.setRefreshing(refreshing); }
    }

    protected void setRefreshingEnabled(boolean enabled) {
        if (mSwipeLayout!=null) { mSwipeLayout.setEnabled(enabled); }
    }

    protected @NonNull int[] setSwipeRefreshColorSchemeResources() {
        return new int[] { R.color.theme_primary_light, R.color.theme_primary,
                R.color.theme_primary_dark, R.color.theme_accent_dark };
    }

    protected void setSwipeRefreshOffset(int pixels) {
        if (mSwipeLayout==null) { return; }
        mSwipeLayout.setProgressViewOffset(false, 0, pixels);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        if (mSwipeLayout!=null) {
            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onSwipeLayoutRefresh();
                }
            });
//            setSwipeRefreshOffset(dipsToPix(SWIPE_PROGRESS_VIEW_OFFSET_DEFAULT));
            mSwipeLayout.setColorSchemeResources(setSwipeRefreshColorSchemeResources());
        }
    }

    protected void onSwipeLayoutRefresh() { }

    protected void showEditTextError(@NonNull final EditText v, @Nullable String message) {
        if (v.getParent() instanceof TextInputLayout) {
            final TextInputLayout p = (TextInputLayout) v.getParent();
            p.setError(message);
            p.postDelayed(new Runnable() {
                @Override
                public void run() { p.setError(null); }
            }, 2000);
        } else {
            v.setError(message);
            v.postDelayed(new Runnable() {
                @Override
                public void run() { v.setError(null); }
            }, 2000);
        }
    }

    protected void showView(@NonNull View v, boolean animate) {
        if (animate) {
            if (v.getVisibility() != View.VISIBLE) {
                v.setVisibility(View.VISIBLE);
                ViewCompat.setAlpha(v, 0F);

                ViewCompat.animate(v).setDuration(TRANSITION_DURATION).alpha(1)
                        .setListener(new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(View view) {
                            }
                        });
            }
        } else {
            if (ViewCompat.getAlpha(v)!=1) {
                ViewCompat.setAlpha(v, 1F);
            }
            v.setVisibility(View.VISIBLE);
        }
    }

    protected void hideView(@NonNull View v, boolean animate, final int setVisibility) {
        if (animate) {
            if (v.getVisibility() == View.VISIBLE) {
                ViewCompat.setAlpha(v, 1);

                ViewCompat.animate(v).setDuration(TRANSITION_DURATION).alpha(0)
                        .setListener(new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(View view) {
                                view.setVisibility(setVisibility);
                            }
                        });
            }
        } else {
            v.setVisibility(setVisibility);
        }
    }
}
