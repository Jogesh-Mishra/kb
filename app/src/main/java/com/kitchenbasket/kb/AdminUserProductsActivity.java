package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Cart_List;
import ViewHolder.CartViewHolder;

public class AdminUserProductsActivity extends AppCompatActivity {

    RecyclerView product_list;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference productsRef;

    private String userID ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        product_list = findViewById(R.id.product_list);
        product_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        product_list.setLayoutManager(layoutManager);

        userID = getIntent().getStringExtra("uid");

        productsRef = FirebaseDatabase.getInstance().getReference().child("Cart_List").child("Admin View")
                .child(userID).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart_List> options = new FirebaseRecyclerOptions.Builder<Cart_List>()
                .setQuery(productsRef,Cart_List.class).build();

        FirebaseRecyclerAdapter<Cart_List, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart_List, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart_List cart_list) {

                cartViewHolder.tvCartProductName.setText(cart_list.getName());
                cartViewHolder.tvCartProductPrice.setText("Rs."+cart_list.getPrice()+" per Kg/L");
                cartViewHolder.tvCartProductQuantity.setText(cart_list.getQuantity()+" kg/L");
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                return new CartViewHolder(view);
            }
        };

        product_list.setAdapter(adapter);
        adapter.startListening();
    }
}
