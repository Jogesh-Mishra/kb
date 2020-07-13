package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;

import Model.Orders;

public class AdminNewOrdersActivity extends AppCompatActivity {

    RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        ordersList = findViewById(R.id.orderitems);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(ordersRef,Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders, AdminOrderViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder adminOrderViewHolder,final int i, @NonNull final Orders orders) {
                adminOrderViewHolder.tvName.setText("NAME : "+orders.getName());
                adminOrderViewHolder.tvTime.setText("Order Placed on : "+orders.getDate()+" "+orders.getTime());
                adminOrderViewHolder.tvPrice.setText("AMOUNT : "+orders.getPrice()+"   MODE : "+orders.getPayment());
                adminOrderViewHolder.tvPhone.setText("PHONE : "+orders.getPhone());
                adminOrderViewHolder.tvAddress.setText("ADDRESS : "+orders.getAddress());
                adminOrderViewHolder.tvSlot.setText("DELIVERY ON : "+orders.getTimeSlot());

                adminOrderViewHolder.btnShowProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uID = getRef(i).getKey();

                        Intent intent =new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });

                adminOrderViewHolder.tvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "tel:"+orders.getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                adminOrderViewHolder.tvAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = orders.getAddress();
                        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    }
                });

                adminOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Shipped","Not Shipped"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Options For Admin :");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    String uID = getRef(i).getKey();
                                    RemoveOrder(uID);
                                }
                                else if (which==1){
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                return new AdminOrderViewHolder(view);
            }
        };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{

       public TextView tvName, tvPrice, tvAddress, tvPhone, tvTime,tvSlot;
       public  Button btnShowProducts;


        public AdminOrderViewHolder(View itemView){
            super(itemView);
            tvAddress = itemView.findViewById(R.id.Address);
            tvName=itemView.findViewById(R.id.Username);
            tvPhone=itemView.findViewById(R.id.PhoneNum);
            tvPrice=itemView.findViewById(R.id.totalCash);
            tvTime =itemView.findViewById(R.id.date_time);
            btnShowProducts=itemView.findViewById(R.id.showProducts);
            tvSlot = itemView.findViewById(R.id.slot);
        }
    }

    private void RemoveOrder(String uID){
        ordersRef.child(uID).removeValue();

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");
        cartRef.child("Admin View").child(uID).child("Products").removeValue();
    }
}
