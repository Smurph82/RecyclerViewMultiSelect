package com.smurph.recyclerviewmultiselect.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
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
import com.smurph.recyclerviewmultiselect.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by ben on 8/30/15.
 *
 */
public class SimpleStringRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    private int mBackground;
    private List<String> mValues;
    private MultiSelectHelper mHelper;
    private Context mContext;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({HELPER_MULTI_WITH_CAB, HELPER_SINGLE_WITH_CAB,
            HELPER_MULTI_WITHOUT_CAB, HELPER_SINGLE_WITHOUT_CAB})
    public @interface HelperTypeDef{}
    public static final int HELPER_MULTI_WITH_CAB = 1;
    public static final int HELPER_SINGLE_WITH_CAB = 2;
    public static final int HELPER_MULTI_WITHOUT_CAB = 3;
    public static final int HELPER_SINGLE_WITHOUT_CAB = 4;

    public SimpleStringRecyclerViewAdapter(@NonNull Context context,
                                           @Nullable List<String> items,
                                           @HelperTypeDef int type) {
        mContext = context;

        switch (type) {
            case HELPER_MULTI_WITH_CAB:
                //NOTE  Helper with Contextual Action Mode (CAB)
                mHelper = new MultiSelectHelper(context, R.id.tag_position);
                mHelper.setActionModeCallback(mActionModeCallback);
                break;
            case HELPER_SINGLE_WITH_CAB:
                // NOTE Helper with CAB but only selects one item at a time
                mHelper = new MultiSelectHelper(context, R.id.tag_position)
                        .setSingleSelectMode(true);
                mHelper.setActionModeCallback(mActionModeCallback);
                break;
            case HELPER_SINGLE_WITHOUT_CAB:
                //NOTE Helper that has no CAB and will only select one item at a time
                mHelper = new MultiSelectHelper(context, R.id.tag_position)
                    .setActionModeEnabled(false)
                    .setSingleSelectMode(true);
                break;
            case HELPER_MULTI_WITHOUT_CAB:
            // NOTE Helper that has no CAB but still can select multiple positions
                mHelper = new MultiSelectHelper(context, R.id.tag_position)
                        .setActionModeEnabled(false);
                break;
            default: break;
        }

        //NOTE Examples to show setting selected color at runtime.
//            mHelper.setSelectedColor(Color.CYAN);
//            mHelper.setSelectedColor(MainActivity.this, R.color.theme_cyan);
        TypedValue mTypedValue = new TypedValue();
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

    public void restoreSelectedItems(@NonNull Context context, @Nullable Bundle bundle) {
        if (bundle==null) { return; }

        mHelper.restoreSelectedPositions(context, bundle);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        v.setBackgroundResource(mBackground);
//        mHelper.setView(v);
        mHelper.setViewHolder(new ViewHolder(v));
        mHelper.setOnMultiSelectListener(mListener);
//        return new ViewHolder(v);
        return (ViewHolder) mHelper.getViewHolder();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Should be no longer needed
//        holder.itemView.setTag(R.id.tag_position, position);
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

                    Toast.makeText(v.getContext(), "Position " +
                            v.getTag(R.id.tag_position) +
                            " clicked.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean onLongClick(View v) { return true; }

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
                    Toast.makeText(mContext, "Delete these.", Toast.LENGTH_SHORT).show();
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
