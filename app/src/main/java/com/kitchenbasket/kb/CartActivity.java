package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Cart_List;
import ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    TextView tvTotalPrice, tvMessage, tvInfotxt;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnNext;


    private Double overTotalPrice =0.;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        tvTotalPrice = findViewById(R.id.total_price);
        recyclerView = findViewById(R.id.items);
        btnNext = findViewById(R.id.Progress);
        tvMessage=findViewById(R.id.msg1);
        tvInfotxt=findViewById(R.id.infotxt);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overTotalPrice > 300 && overTotalPrice < 600) {
                    overTotalPrice = overTotalPrice + 25;
                    Toast.makeText(CartActivity.this, "Rs.30 added as Delivery Price", Toast.LENGTH_LONG).show();
                }
                if (overTotalPrice < 300) {
                    Toast.makeText(CartActivity.this, "Please Enter Minimum Cart Value of Rs.300", Toast.LENGTH_LONG).show();
                }
                else {
                    tvTotalPrice.setText("Total Price : Rs." + String.valueOf(overTotalPrice));
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");

        FirebaseRecyclerOptions<Cart_List> options = new FirebaseRecyclerOptions.Builder<Cart_List>()
                .setQuery(cartListRef.child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products"),Cart_List.class)
                .build();

        FirebaseRecyclerAdapter<Cart_List, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart_List, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart_List cart_list) {
                cartViewHolder.tvCartProductName.setText(cart_list.getName());
                cartViewHolder.tvCartProductPrice.setText("Rs. : "+cart_list.getPrice());
                cartViewHolder.tvCartProductQuantity.setText("Quantity: "+cart_list.getQuantity());

                double oneTypeProductTPrice = (((Double.parseDouble(cart_list.getPrice())))*(Double.parseDouble(cart_list.getQuantity())));
                cartViewHolder.tvCartProductPrice.setText("Total Amount : Rs."+oneTypeProductTPrice);

                overTotalPrice = overTotalPrice+oneTypeProductTPrice;

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Edit","Remove"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Item Options :");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",cart_list.getProduct_Id());
                                    startActivity(intent);
                                }
                                if (which==1){
                                    cartListRef.child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Products").child(cart_list.getProduct_Id()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item Removed From Cart", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkOrderState(){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("State").getValue().toString();
                    String UserName = dataSnapshot.child("Name").getValue().toString();

                    if (shippingState.equals("Shipped")){
                        tvTotalPrice.setText("Dear "+UserName+"\n Order has been shipped Successfully");
                        recyclerView.setVisibility(View.GONE);
                        tvMessage.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        tvInfotxt.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can Purchase More Products Once you receive for Previous order ", Toast.LENGTH_LONG).show();
                    }
                    else if(shippingState.equals("Not Shipped")){
                        tvTotalPrice.setText("Dear "+UserName+"\n Order is on the way and will be shipped soon");
                        recyclerView.setVisibility(View.GONE);
                        tvMessage.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        tvInfotxt.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can Purchase More Products Once you receive for Previous order ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
