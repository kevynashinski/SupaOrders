package com.ryletech.supaorders.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.ProductAdapter;
import com.ryletech.supaorders.database.ProductsDBHelper;

public class CartActivity extends AppCompatActivity {

    private CoordinatorLayout productsCoordinatorLayout;
    private RecyclerView productsRecyclerView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout productsSwipeRefreshLayout;
    private ProductsDBHelper productsDBHelper;
    private ImageView cartEmptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assignViews();

        productsDBHelper = new ProductsDBHelper(getBaseContext());

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        productsRecyclerView.addOnItemTouchListener(new CategoryAdapter.RecyclerTouchListener(getBaseContext(), categoriesRecyclerView, this));

        productsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void loadProducts() {
        if (productsDBHelper.cartItemsCount() > 0) {
//            hide the empty layout
            if (cartEmptyLayout.getVisibility() == View.VISIBLE)
                cartEmptyLayout.setVisibility(View.GONE);

            productsRecyclerView.setAdapter(new ProductAdapter(productsDBHelper.getCartProducts(), getBaseContext()));
        }
    }

    private void assignViews() {
        productsCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.productsCoordinatorLayout);
        productsRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);
        productsSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.productsSwipeRefreshLayout);
        cartEmptyLayout = (ImageView) findViewById(R.id.cartEmptyLayout);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                close this activity and return to previous one if any
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
