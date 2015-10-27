package com.smurph.multiselectlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Ben Murphy on 8/6/2015.
 * This class is meant to help with multi-item selection and the ActionMode for Android using
 * the RecyclerView
 */
@SuppressWarnings("unused")
public class MultiSelectHelper {

    private static final String tag = MultiSelectHelper.class.getSimpleName();

    protected SparseBooleanArray mIsSelected = new SparseBooleanArray();

    public static final String SAVE_KEY_SELECTED_POSITIONS =
            "com.smurph.multiselectlib.SAVED_POSITIONS";
    public static final String SAVE_KEY_SELECTED_POSITION =
            "com.smurph.multiselectlib.SAVED_POSITION";

    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;

    private int ACCENT_COLOR;
    private final ColorStateList COLOR_STATE_LIST;

    private boolean mIsClickingEnabled = true;
    private boolean mIsActionModeEnabled = true;
    private boolean mIsSingleSelectMode = false;

    private int mSelectedPosition = -1;

    public interface OnMultiSelectListener {
        void onClick(View v, boolean isSelectionMode, boolean isExitingActionMode);
        boolean onLongClick(View v, boolean isSelectionMode);
        void itemChangedAt(int position);
    }
    private OnMultiSelectListener mListener;

    /**
     * @deprecated Please use {@link #MultiSelectHelper(Context)} instead
     * @param context The context of your app.
     * @param tagId The {@link IdRes} of the tag for the position no used.
     */
    public MultiSelectHelper(@NonNull Context context, @IdRes int tagId) { this(context); }

    /**
     * Create a new instance of the MultiSelectHelper
     * @param context The {@link Context} of your app. Will be held it in a
     * {@link java.lang.ref.WeakReference}
     */
    public MultiSelectHelper(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data,
                new int[]{R.attr.colorAccent, R.attr.selectedItemColor});
        ACCENT_COLOR = a.getColor(1, a.getColor(0, Color.rgb(255, 209, 128)));
        a.recycle();

        int accent_color_alt = changeBrightness(ACCENT_COLOR, 0.5f);

        COLOR_STATE_LIST = new ColorStateList(
                new int[][] { new int[]{} },
                new int[] {accent_color_alt}
        );
    }

    public void setOnMultiSelectListener(@NonNull OnMultiSelectListener l) { mListener = l; }

    public void setActionModeCallback(@NonNull ActionMode.Callback callback) {
        mActionModeCallback = callback;
    }

    /**
     * This is called if you want to immediately finish and close the current ActionMode.
     * This results in everything being unselected.
     */
    public void forceFinishActionMode() {
        if (!isSelectionMode()) { return; }
        ArrayList<Integer> selectedPositions = getSelectedPositions();
        mIsSelected.clear();
        if (mListener!=null) { for (Integer i : selectedPositions) { mListener.itemChangedAt(i); } }
        if (mActionMode!=null) { mActionMode.finish(); }
    }

    /**
     * This should be called in the <code>onDestroyActionMode</code> of your
     * {@link android.support.v7.view.ActionMode.Callback}. This handles clean up.
     */
    public void destroyActionMode() {
        if (isSelectionMode() && mListener != null) {
            for (int i=0,c=mIsSelected.size();i<c;i++) {
                mListener.itemChangedAt(mIsSelected.keyAt(i));
            }
        }
        clearSelected();
        mActionMode = null;
        mSelectedPosition=-1;
    }

    public void clearSelected() { mIsSelected.clear(); }

    @NonNull
    public ArrayList<Integer> getSelectedPositions() {
        ArrayList<Integer> list = new ArrayList<>(mIsSelected.size());
        for (int i=0,c=mIsSelected.size();i<c;i++) { list.add(mIsSelected.keyAt(i)); }
        return list;
    }

    public void saveSelectedPositions(@NonNull Bundle bundle) {
        bundle.putIntegerArrayList(SAVE_KEY_SELECTED_POSITIONS, getSelectedPositions());
        bundle.putInt(SAVE_KEY_SELECTED_POSITION, mSelectedPosition);
    }

    public void restoreSelectedPositions(@NonNull Context context, @NonNull Bundle bundle) {
        ArrayList<Integer> list = bundle.getIntegerArrayList(SAVE_KEY_SELECTED_POSITIONS);
        mSelectedPosition = bundle.getInt(SAVE_KEY_SELECTED_POSITION, -1);

        if (list==null) { return; }

        for (Integer i : list) { mIsSelected.put(i, true); }

        if (isSelectionMode()) { startActionMode(context); }
    }

    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_viewholder);
        if (holder==null) {
            Log.e(tag, "You did not call super.onBindViewHolder(holder, position); " +
                    "in your Adapters onBindViewHolder. Please call it.");
            return;
        }
        if (!mIsClickingEnabled) { return; }

        int position = holder.getLayoutPosition();
        if ((isSelectionMode() || !isActionModeEnabled())/* && position!=null*/) {
            if (isSingleSelectMode()) {
                if (mSelectedPosition!=-1) {
                    toggleSelection(mSelectedPosition);
                }
                if (mSelectedPosition==position) {
                    mSelectedPosition=-1;
                    if (mActionMode!=null) { mActionMode.finish(); }
                    return;
                }
                mSelectedPosition = position;
            }
            toggleSelection(position);
            if (mListener!=null) { mListener.onClick(v, isSelectionMode(),
                    (!isSelectionMode() && mActionMode!=null)); }
            if (!isSelectionMode() && mActionMode!=null) { mActionMode.finish(); }
            return;
        }
        // TODO This may need work with the isExitingActionMode
        if (mListener != null) { mListener.onClick(v, isSelectionMode(), false); }
    }

    public boolean onLongClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_viewholder);
        if (holder==null) {
            Log.e(tag, "You did not call setViewHolder(). Please call it and not setView().");
            return true;
        }
        if (!mIsClickingEnabled) { return true; }
        if (!isActionModeEnabled()) {
            return mListener != null && mListener.onLongClick(v, isSelectionMode());
        }

        int position = holder.getLayoutPosition();
        if (isSingleSelectMode()) {
            if (mSelectedPosition !=-1) {
                toggleSelection(mSelectedPosition);
            }
            mSelectedPosition = position;
        }
        if (mActionMode != null) { return false; }

        if (mIsActionModeEnabled) { startActionMode(v.getContext()); }

        toggleSelection(position);

        return mListener != null && mListener.onLongClick(v, isSelectionMode());
    }

    private void startActionMode(@NonNull Context context) {
        if (context instanceof AppCompatActivity && isActionModeEnabled()) {
            mActionMode = ((AppCompatActivity) context)
                    .startSupportActionMode(mActionModeCallback);
        }
    }

    public void toggleSelection(int position) {
        if (!mIsSingleSelectMode) {
            if (mIsSelected.get(position, false)) {
                mIsSelected.delete(position);
            } else {
                mIsSelected.put(position, true);
            }
        } else {
            if (mIsSelected.get(position, false)) {
                mIsSelected.clear();
            } else {
                mIsSelected.clear();
                mIsSelected.put(position, true);
            }
        }

        if (mActionMode!=null) {  mActionMode.invalidate(); }
        if (mListener!=null) { mListener.itemChangedAt(position); }
    }

    public void setIsClickingEnabled(boolean isClickingEnabled) {
        mIsClickingEnabled = isClickingEnabled;
    }

    public MultiSelectHelper setActionModeEnabled(boolean isEnabled) {
        mIsActionModeEnabled = isEnabled;
        return this;
    }

    public boolean isActionModeEnabled() { return mIsActionModeEnabled; }

    public boolean isSelectionMode() { return mIsSelected.size()>0; }

    public boolean getIsSelected(int position) { return mIsSelected.get(position, false); }

    public int getSelectedCount() { return mIsSelected.size(); }

    public MultiSelectHelper setSelectedColor(@ColorInt int color) {
        ACCENT_COLOR = color;
        return this;
    }

    public boolean isSingleSelectMode() { return mIsSingleSelectMode; }

    public MultiSelectHelper setSingleSelectMode(boolean isSingleSelectMode) {
        mIsSingleSelectMode = isSingleSelectMode;
        return this;
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.M)
    public void setSelectedColor(@NonNull Context context, @ColorRes int id) {
        if (isAboveOrEqualAPILvl(Build.VERSION_CODES.M)) {
            setSelectedColor(context.getColor(id));
        } else {
            //noinspection deprecation
            setSelectedColor(context.getResources().getColor(id));
        }
    }

//    public void setSelectedColorARGD(@ColorInt int color) {
//        setSelectedColor(Color.argb(
//                (color >>> 24), // Alpha
//                ((color >> 16) & 0xFF), // Red
//                ((color >> 8) & 0xFF), // Green
//                (color & 0xFF) //Blue
//        ));
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRippleColor(@NonNull View v) {
        if (isAboveOrEqualAPILvl(Build.VERSION_CODES.LOLLIPOP)) {
            v.setBackground(new RippleDrawable(COLOR_STATE_LIST,
                    new ColorDrawable(ACCENT_COLOR), null));
        } else {
            v.setBackgroundColor(ACCENT_COLOR);
        }
    }


    private int changeBrightness(final int color, float fraction) {
        return calculateYiqLuma(color) >= 128
                ? darken(color, fraction)
                : lighten(color, fraction);
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRatio);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRatio);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    /**
     * @return luma value according to to YIQ color space.
     */
    private int calculateYiqLuma(int color) {
        return Math.round((
                299 * Color.red(color) +
                        587 * Color.green(color) +
                        114 * Color.blue(color))
                / 1000f);
    }

    private int darken(final int color, float fraction) {
        return blendColors(Color.BLACK, color, fraction);
    }

    private int lighten(final int color, float fraction) {
        return blendColors(Color.WHITE, color, fraction);
    }

    private boolean isAboveOrEqualAPILvl(int apiLvl) { return Build.VERSION.SDK_INT >= apiLvl; }

//    public static class Builder {
//        private Context lContext;
//        private int lTagId;
//        private boolean lIsActionModeEnabled = true;
//        private boolean lIsSingleSelectMode = false;
//
//        public Builder(@NonNull Context context, @IdRes int tagId) {
//            lContext = context;
//            lTagId = tagId;
//        }
//
//        public Builder setIsActionModeEnabled(boolean isEnabled) {
//            lIsActionModeEnabled = isEnabled;
//            return this;
//        }
//
//        public Builder setIsSingleSelectMode(boolean isEnabled) {
//            lIsSingleSelectMode = isEnabled;
//            return this;
//        }
//
//        public Builder setAccentColor() {
//
//        }
//
//        public MultiSelectHelper create() {
//            return new MultiSelectHelper(lContext, lTagId)
//                    .setActionModeEnabled(lIsActionModeEnabled)
//                    .setSingleSelectMode(lIsSingleSelectMode);
//        }
//    }

}
