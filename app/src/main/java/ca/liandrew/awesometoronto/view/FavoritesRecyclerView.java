package ca.liandrew.awesometoronto.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liandrew on 2016-05-19.
 */
public class FavoritesRecyclerView extends RecyclerView {
    private View[] mEmptyViewList;
    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyViews();
        }
    };

    public FavoritesRecyclerView(Context context) {
        super(context);
    }

    public FavoritesRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavoritesRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set a list of views to show and hide upon empty and full
     */
    public void setEmptyViews(View[] emptyViewList) {
        this.mEmptyViewList = emptyViewList;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
        updateEmptyViews();
    }

    private void updateEmptyViews() {
        for(View emptyView : mEmptyViewList){
            if (emptyView != null && getAdapter() != null) {
                boolean isAdapterEmpty = getAdapter().getItemCount() == 0;
                emptyView.setVisibility(isAdapterEmpty ? VISIBLE : GONE);
                setVisibility(isAdapterEmpty ? GONE : VISIBLE);
            }
        }
    }
}