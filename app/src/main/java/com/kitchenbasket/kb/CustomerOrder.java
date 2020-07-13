package com.kitchenbasket.kb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.Orders;

public class CustomerOrder extends AppCompatActivity {

    TextView tvName,tvPrice,tvTime,tvAddress,tvPhone,tvPendingSLot;
    Button bnshowProducts, btnPayOrder;
    String amount, state;
    DatabaseReference ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        tvAddress = findViewById(R.id.pendingAddress);
        tvName = findViewById(R.id.pendingName);
        tvPhone = findViewById(R.id.pendingPhone);
        tvPrice = findViewById(R.id.pendingCash);
        tvTime = findViewById(R.id.pendingdate_time);
        bnshowProducts = findViewById(R.id.pendingshowProducts);
        btnPayOrder=findViewById(R.id.btnPayOrder);
        tvPendingSLot =findViewById(R.id.pendingSlot);

        tvAddress.setVisibility(View.GONE);
        tvPendingSLot.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);
        tvPrice.setVisibility(View.GONE);
        tvPhone.setVisibility(View.GONE);
        bnshowProducts.setVisibility(View.GONE);
        btnPayOrder.setVisibility(View.GONE);

        final ArrayList<String> list = new ArrayList<>();
        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(uID);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvAddress.setVisibility(View.VISIBLE);
                    tvPendingSLot.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvName.setVisibility(View.VISIBLE);
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPhone.setVisibility(View.VISIBLE);
                    bnshowProducts.setVisibility(View.VISIBLE);
                    btnPayOrder.setVisibility(View.VISIBLE);
                    Orders orders = dataSnapshot.getValue(Orders.class);
                    tvAddress.setText("ADDRESS :\n "+ orders.getAddress());
                    tvName.setText("ORDERED BY :\n "+orders.getName());
                    tvTime.setText("ORDERED ON :\n "+orders.getDate() + " " + orders.getTime());
                    tvPhone.setText("PHONE :\n"+orders.getPhone());
                    tvPrice.setText("AMOUNT :\n Rs."+orders.getPrice()+" "+orders.getPayment());
                    tvPendingSLot.setText("DELIVERY SLOT :\n "+orders.getTimeSlot());
                    amount = orders.getPrice();
                    state = orders.getPayment();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CustomerOrder.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        bnshowProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerOrder.this,AdminUserProductsActivity.class);
                intent.putExtra("uid",uID);
                startActivity(intent);
            }
        });

        btnPayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence charSequence[] = new CharSequence[]{"COD","Online","Remove Order"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(CustomerOrder.this);
                builder.setTitle("Choose Payment Option");
                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child("Orders");
                            dataref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment").setValue("COD");

                            Intent intent = new Intent(CustomerOrder.this,HomeActivity.class);
                            startActivity(intent);
                            CustomerOrder.this.finish();
                        }
                        if (which==1){
                            Intent intent = new Intent(CustomerOrder.this,Online.class);
                            intent.putExtra("totalAmount",amount);
                            startActivity(intent);
                            CustomerOrder.this.finish();
                        }
                        if (which==2){
                            ordersRef.removeValue();
                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");
                            cartRef.child("Admin View").child(uID).child("Products").removeValue();
                            CustomerOrder.this.finish();
                            Toast.makeText(CustomerOrder.this, "Order Cancelled ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.show();
            }
        });
    }
}
