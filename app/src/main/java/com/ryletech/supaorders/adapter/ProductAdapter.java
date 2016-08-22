package com.ryletech.supaorders.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.TextView;

import com.github.pierry.simpletoast.SimpleToast;
import com.hanks.library.AnimateCheckBox;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.model.Product;

import java.util.ArrayList;

/**
 * Created by sydney on 8/14/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Product> products = new ArrayList<>();
    private Context context;

    public ProductAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(context.getResources().getString(R.string.price).concat(product.getProductPrice()));
        holder.checkBox.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if(isChecked){
                    SimpleToast.info(context,"Ready to Add to Cart");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != products ? products.size() : 0;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CategoryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CategoryAdapter.ClickListener clickListener) {
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

        AnimateCheckBox checkBox;
        TextView productName, productPrice;

        ViewHolder(View itemView) {
            super(itemView);

            productName = (TextView) itemView.findViewById(R.id.productName);
            checkBox = (AnimateCheckBox) itemView.findViewById(R.id.checkbox);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
        }
    }
}
