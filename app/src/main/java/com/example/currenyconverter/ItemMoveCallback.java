package com.example.currenyconverter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class that handles recyclerview's drag and drop event
 */
public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if (viewHolder instanceof ConverterAdapter.ConverterViewHolder) {
            ConverterAdapter.ConverterViewHolder ConverterViewHolder =
                    (ConverterAdapter.ConverterViewHolder) viewHolder;
            ConverterAdapter.ConverterViewHolder targetViewHolder =
                    (ConverterAdapter.ConverterViewHolder) target;
            mAdapter.onRowMoved(ConverterViewHolder, targetViewHolder, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ConverterAdapter.ConverterViewHolder) {
                ConverterAdapter.ConverterViewHolder ConverterViewHolder =
                        (ConverterAdapter.ConverterViewHolder) viewHolder;
                mAdapter.onRowSelected(ConverterViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ConverterAdapter.ConverterViewHolder) {
            ConverterAdapter.ConverterViewHolder ConverterViewHolder =
                    (ConverterAdapter.ConverterViewHolder) viewHolder;
            mAdapter.onRowClear(ConverterViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(ConverterAdapter.ConverterViewHolder ConverterViewHolder, ConverterAdapter.ConverterViewHolder targetViewHolder, int fromPosition, int toPosition);

        void onRowSelected(ConverterAdapter.ConverterViewHolder ConverterViewHolder);

        void onRowClear(ConverterAdapter.ConverterViewHolder ConverterViewHolder);

    }

}