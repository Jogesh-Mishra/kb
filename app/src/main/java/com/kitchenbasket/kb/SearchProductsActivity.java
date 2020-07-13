package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class SearchProductsActivity extends AppCompatActivity {

    Button btnSearch;
    EditText etSearch;
    RecyclerView search_list;
    RecyclerView.LayoutManager layoutManager;
    ProgressDialog loadingBar;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        search_list = findViewById(R.id.search_list);
        search_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SearchProductsActivity.this);
        search_list.setLayoutManager(layoutManager);

        loadingBar = new ProgressDialog(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = etSearch.getText().toString().trim();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options;
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

           options = new FirebaseRecyclerOptions.Builder<Products>()
                   .setQuery(reference.orderByChild("Name").startAt(searchInput), Products.class)
                   .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.product_name.setText(products.getName());
                productViewHolder.product_price.setText("Price: "+products.getPrice());
                Picasso.get().load(products.getImage()).into(productViewHolder.product_image);


                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchProductsActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",products.getProduct_Id());
                        intent.putExtra("name",products.getName());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };

        search_list.setAdapter(adapter);
        adapter.startListening();
    }
}
