package com.smurph.multiselectlib;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Created by Ben on 9/24/2015.
 * This is a {@link ViewHolder} that supports the {@link MultiSelectHelper}
 */
public class MultiSelectViewHolder extends RecyclerView.ViewHolder {

    // Pass it along :)
    public MultiSelectViewHolder(View itemView) { super(itemView); }

    /**
     * This will set the <code>View.OnClickListener</code> and the
     * <code>View.OnLongClickListener</code> on the
     * {@link ViewHolder#itemView}
     * @param l An instance of {@link android.view.View.OnClickListener}
     * @param ll An instance of {@link android.view.View.OnLongClickListener}
     */
    public void setOnClickListenerFromAdapter(@NonNull View.OnClickListener l,
                                              @NonNull View.OnLongClickListener ll) {
        this.itemView.setOnClickListener(l);
        this.itemView.setOnLongClickListener(ll);
    }
}
