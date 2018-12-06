package com.gin.ngemart.baseui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.component.NgemartCardView;

import java.util.List;

public class NgemartRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public LayoutInflater inflater;
    public int resource;
    private List<T> items;
    private NgemartCardView.CardListener<T> recyclerClickListener;
    private String containerName = "";
    private NgemartActivity activity;
    private Object[] params;

    public NgemartRecyclerViewAdapter(NgemartActivity activity, int resource, List<T> items, Object... params) {
        this.inflater = LayoutInflater.from(activity);
        this.items = items;
        this.resource = resource;
        this.activity = activity;
        this.params = params;
    }

    public void setParameters(Object... params) {
        this.params = params;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NgemartCardView<T> cardView = (NgemartCardView<T>) inflater.inflate(this.resource, parent, false);
        cardView.setParameters(params);
        return new RecyclerViewHolders(cardView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolders recyclerViewHolders = (RecyclerViewHolders) holder;
        recyclerViewHolders.setData(items.get(position));
    }

    public T GetItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    public void setRecyclerListener(NgemartCardView.CardListener recyclerClickListener) {
        this.recyclerClickListener = recyclerClickListener;
    }

    public void updateSource(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void SetContainer(String containerName) {
        this.containerName = containerName;
    }

    protected NgemartActivity getActivity() {
        return activity;
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder {
        private NgemartCardView<T> ngemartCardView;

        public RecyclerViewHolders(View cardView) {
            super(cardView);
            ngemartCardView = (NgemartCardView<T>) cardView;
            if (recyclerClickListener != null) {
                ngemartCardView.setCardListener(new NgemartCardView.CardListener<T>() {
                    @Override
                    public void onCardClick(int position, View view, T data) {
                        recyclerClickListener.onCardClick(getAdapterPosition(), view, data);
                    }
                });
            }
        }

        public void setData(T data) {
            if (containerName.equals("")) {
                ngemartCardView.setData(data);
            } else {
                ngemartCardView.setData(data, containerName);
            }

        }
    }
}


