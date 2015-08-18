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
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Ben Murphy on 8/6/2015.
 * This class is meant to help with multi-item selection and the ActionMode for Android using
 * the RecyclerView
 */
public class MultiSelectHelper implements
        View.OnClickListener, View.OnLongClickListener {

    protected SparseBooleanArray mIsSelected = new SparseBooleanArray();

    public static final String SAVE_KEY_SELECTED_POSITIONS =
            "com.smurph.multiselectlib.SAVED_POSITIONS";

    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;

    private final int TAG_ID;
    private int ACCENT_COLOR;
    private final ColorStateList COLOR_STATE_LIST;

    public interface OnMultiSelectListener {
        void onClick(View v, boolean isSelectionMode);
        boolean onLongClick(View v);
        void itemChangedAt(int position);
    }
    private OnMultiSelectListener mListener;

    public MultiSelectHelper(@NonNull Context context, @IdRes int tagId) {
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

        TAG_ID = tagId;
    }

    public void setView(@NonNull View v) {
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    public void setOnMultiSelectListener(
            @NonNull OnMultiSelectListener l) { mListener = l; }

    public void setActionModeCallback(@NonNull ActionMode.Callback callback) {
        mActionModeCallback = callback;
    }

    public void destroyActionMode() {
        if (isSelectionMode() && mListener != null) {
            for (int i=0,c=mIsSelected.size();i<c;i++) {
                mListener.itemChangedAt(mIsSelected.keyAt(i));
            }
        }
        clearSelected();
        mActionMode = null;
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
    }

    public void restoreSelectedPositions(@NonNull Context context, @NonNull Bundle bundle) {
        ArrayList<Integer> list = bundle.getIntegerArrayList(SAVE_KEY_SELECTED_POSITIONS);

        if (list==null) { return; }

        for (Integer i : list) { mIsSelected.put(i, true); }

        if (isSelectionMode()) { startActionMode(context); }
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag(TAG_ID);
        if (isSelectionMode() && position!=null) {
            if (mListener!=null) { mListener.onClick(v, isSelectionMode()); }
            toggleSelection(position);
            if (!isSelectionMode() && mActionMode!=null) { mActionMode.finish(); }
            return;
        }
        if (mListener!=null) { mListener.onClick(v, isSelectionMode()); }
    }

    @Override
    public boolean onLongClick(View v) {
        Integer position = (Integer) v.getTag(TAG_ID);
        if (mActionMode != null || position == null) { return false; }

        startActionMode(v.getContext());

        toggleSelection(position);

        return mListener != null && mListener.onLongClick(v);
    }

    private void startActionMode(@NonNull Context context) {
        if (context instanceof AppCompatActivity) {
            mActionMode = ((AppCompatActivity) context)
                    .startSupportActionMode(mActionModeCallback);
        }
    }

    public void toggleSelection(int position) {
        if (mIsSelected.get(position, false)) { mIsSelected.delete(position); }
        else { mIsSelected.put(position, true); }

        if (mListener!=null) { mListener.itemChangedAt(position); }
    }

    public boolean isSelectionMode() { return mIsSelected.size()>0; }

    public boolean getIsSelected(int position) { return mIsSelected.get(position, false); }

    public void setSelectedColor(int color) { ACCENT_COLOR = color; }

    @TargetApi(Build.VERSION_CODES.M)
    public void setSelectedColor(@NonNull Context context, @ColorRes int id) {
        if (isAboveOrEqualAPILvl(Build.VERSION_CODES.M)) {
            setSelectedColor(context.getColor(id));
        } else {
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

}
