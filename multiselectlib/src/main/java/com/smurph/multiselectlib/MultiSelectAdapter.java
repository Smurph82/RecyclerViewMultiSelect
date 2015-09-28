package com.smurph.multiselectlib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created by Ben on 9/24/2015.
 * This is an abstract RecyclerViewAdapter
 */
@SuppressWarnings("unused")
public abstract class MultiSelectAdapter<VH extends MultiSelectViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String tag = MultiSelectAdapter.class.getSimpleName();

    protected MultiSelectHelper mHelper;
    private WeakReference<Context> mWeakContext;

    public interface OnItemClickedListener {
        void onItemClicked(@NonNull View v, int position, boolean isSelectionMode,
                           boolean isExitActionMode);
        boolean onItemLongClicked(@NonNull View v, int position, boolean isSelectionMode);
    }
    protected OnItemClickedListener mItemClickedListener;

    public MultiSelectAdapter(@NonNull Context context) { this(context, null); }

    public MultiSelectAdapter(@NonNull Context context, @Nullable MultiSelectHelper helper) {
        mWeakContext = new WeakReference<>(context);
        setMultiSelectHelper(helper);
    }

    @Nullable
    public Context getContext() { return mWeakContext.get(); }

    public void saveSelectedItems(@NonNull Bundle bundle) { mHelper.saveSelectedPositions(bundle); }

    public void restoreSelectedItems(@NonNull Context context, @Nullable Bundle bundle) {
        if (bundle == null) { return; }
        mHelper.restoreSelectedPositions(context, bundle);
    }

    public String getSelectedPositions() {
        return mHelper==null ? "-1" : mHelper.getSelectedPositions().toString();
    }

    public int getSelectedCount() { return mHelper==null ? 0 : mHelper.getSelectedCount(); }

    /**
     * Your instance of the {@link MultiSelectHelper}
     * @param helper {@link MultiSelectHelper}
     */
    public void setMultiSelectHelper(@Nullable MultiSelectHelper helper) {
        if (helper==null) { return; }
        mHelper = helper;
        mHelper.setOnMultiSelectListener(mListener);
    }

    /**
     * Must be called to set everything up.
     * {@link #setMultiSelectHelper(MultiSelectHelper)} must be call before this.
     * @param holder The ViewHolder you create in {@link #onCreateViewHolder(ViewGroup, int)}
     * @return <code>true</code> is everything is good, <code>false</code> if something failed
     */
    public boolean attachViewHolder(@NonNull RecyclerView.ViewHolder holder) {
        if (!(holder instanceof MultiSelectViewHolder)) {
            Log.e(tag, "ViewHolder not an instance of MultiSelectViewHolder.");
            return false;
        }
        if (mHelper==null) {
            Log.e(tag, "MultiSelectHelper is null. You must call setMultiSelectHelper() " +
                    "before you attach teh ViewHolder()");
            return false;
        }

        ((MultiSelectViewHolder)holder).setOnClickListenerFromAdapter(mOnClickListener,
                mOnLongClickListener);
        return true;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(R.id.tag_viewholder, holder);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { mHelper.onClick(v); }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) { return mHelper.onLongClick(v); }
    };

    /**
     *
     * @param v The {@link View} that was clicked
     * @param type 1 for click, 2 for long click,
     */
    private boolean handleClicks(@NonNull View v, RecyclerView.ViewHolder h, int type,
                              boolean isSelectionMode, boolean isExitingActionMode) {
        if (mItemClickedListener==null) { return false; }

        switch (type) {
            case 1: // Short click
                mItemClickedListener.onItemClicked(v, h.getLayoutPosition(), isSelectionMode,
                        isExitingActionMode);
                break;
            case 2: // Long Click
                return mItemClickedListener.onItemLongClicked(v, h.getLayoutPosition(),
                        isSelectionMode);
            default:
                break;
        }
        return false;
    }

    private MultiSelectHelper.OnMultiSelectListener mListener =
            new MultiSelectHelper.OnMultiSelectListener() {
                @Override
                public void onClick(View v, boolean isSelectionMode, boolean isExitingActionMode) {
                    handleClicks(v, ((ViewHolder)v.getTag(R.id.tag_viewholder)), 1,
                            isSelectionMode, isExitingActionMode);
                }
                @Override
                public boolean onLongClick(View v, boolean isSelectionMode) {
                    // TODO May need work with the isExitingActionMode
                    return handleClicks(v, ((ViewHolder)v.getTag(R.id.tag_viewholder)), 2,
                            isSelectionMode, false);
                }
                @Override
                public void itemChangedAt(int position) { notifyItemChanged(position); }
            };

    public void setOnItemClickedListener(@Nullable OnItemClickedListener l) {
        mItemClickedListener = l;
    }

    public static class SimpleOnItemClickedListener implements OnItemClickedListener {
        @Override
        public void onItemClicked(@NonNull View v, int position, boolean isSelectionMode,
                                  boolean isExitingActionMode) { }
        @Override
        public boolean onItemLongClicked(@NonNull View v, int position, boolean isSelectionMode) {
            return true;
        }
    }
}
