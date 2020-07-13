package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class Categories extends AppCompatActivity {
    RecyclerView home_list;
    RecyclerView.LayoutManager layoutManager;
    String category;
    DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        home_list=findViewById(R.id.home_list);
        home_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        home_list.setLayoutManager(layoutManager);

        dataRef = FirebaseDatabase.getInstance().getReference().child("Products");



    }

    @Override
    protected void onStart() {
        super.onStart();

        category=getIntent().getStringExtra("categoryHome");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(dataRef.orderByChild("Category").startAt(category).endAt(category),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.product_name.setText(products.getName());
                productViewHolder.product_price.setText("Price: Rs."+products.getPrice());
                Picasso.get().load(products.getImage()).into(productViewHolder.product_image);


                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Categories.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",products.getProduct_Id());
                        intent.putExtra("name",products.getName());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        home_list.setAdapter(adapter);
        adapter.startListening();
    }
}
