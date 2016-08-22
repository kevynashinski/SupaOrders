package com.ryletech.supaorders.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.model.SuperMarket;

import java.util.ArrayList;

/**
 * Created by sydney on 6/26/2016.
 */
public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.ViewHolder> {

    ArrayList<SuperMarket> nearBySuperMarkets = new ArrayList<>();
    Context context;

    public SupermarketAdapter(ArrayList<SuperMarket> nearBySuperMarkets, Context context) {
        this.nearBySuperMarkets = nearBySuperMarkets;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_supermarket, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SuperMarket superMarket = nearBySuperMarkets.get(position);

        holder.supermarketName.setText(superMarket.getPlaceName());
        holder.vicinity.setText(superMarket.getVicinity());
        Glide.with(context)
                .load(superMarket.getIcon())
                .asBitmap()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.map_mode)
                .error(R.drawable.ic_error)
                .into(holder.supermarketIcon);
    }

    @Override
    public int getItemCount() {
        return null != nearBySuperMarkets ? nearBySuperMarkets.size() : 0;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView supermarketIcon;
        TextView supermarketName, vicinity;

        ViewHolder(View itemView) {
            super(itemView);

            supermarketIcon = (ImageView) itemView.findViewById(R.id.supermarketIcon);
            supermarketName = (TextView) itemView.findViewById(R.id.supermarketName);
            vicinity = (TextView) itemView.findViewById(R.id.vicinity);
        }
    }
}
