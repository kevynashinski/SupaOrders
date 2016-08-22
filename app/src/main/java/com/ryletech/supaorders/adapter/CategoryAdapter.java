package com.ryletech.supaorders.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.model.Category;

import java.util.ArrayList;

/**
 * Created by sydney on 6/29/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    ArrayList<Category> categories=new ArrayList<>();
    Context context;

    public CategoryAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category=categories.get(position);

        holder.categoryName.setText(category.getCategoryName());
        holder.categoryDescription.setText(category.getCategoryDescription());
        Glide.with(context)
                .load(category.getCategoryIcon())
                .asBitmap()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.category)
                .error(R.drawable.ic_error)
                .into(holder.categoryIcon);
    }

    @Override
    public int getItemCount() {
        return null != categories ? categories.size() : 0;
    }

    public interface ClickListener{
        void onClick(View view, int position);

        void onLongClick(View view,int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clickListener!=null){
                        clickListener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryIcon;
        TextView categoryName,categoryDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            categoryName= (TextView) itemView.findViewById(R.id.categoryName);
            categoryIcon= (ImageView) itemView.findViewById(R.id.categoryIcon);
            categoryDescription= (TextView) itemView.findViewById(R.id.categoryDescription);
        }
    }
}
