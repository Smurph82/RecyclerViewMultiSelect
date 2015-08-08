package com.smurph.recyclerviewmultiselect;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView((RecyclerView) findViewById(R.id.recyclerview));
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(this, mList));
    }

    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        protected int mSelectedColor;
        public MultiSelectHelper mHelper;

        public SimpleStringRecyclerViewAdapter(@NonNull Context context,
                                               @Nullable List<String> items) {
            mHelper = new MultiSelectHelper(context, R.id.tag_position);
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground,
                    mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mSelectedColor = context.getResources().getColor(R.color.theme_accent);
            mValues = items;
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
            mHelper.setActionModeCallback(mActionModeCallback);
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
                        List<Integer> list = mHelper.getSelectedPosition();
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
