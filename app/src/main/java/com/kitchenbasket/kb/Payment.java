package com.kitchenbasket.kb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import Model.Orders;

public class Payment extends AppCompatActivity {

    Button btnCOD, btnOnline;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        final String totalAmount = getIntent().getStringExtra("totalAmount");



        btnOnline = findViewById(R.id.btnOnline);
        btnCOD =findViewById(R.id.btnCOD);

        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Payment.this,Online.class);
                intent.putExtra("totalAmount",totalAmount);
                startActivity(intent);
                Payment.this.finish();
            }
        });

        btnCOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child("Orders");
                dataref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment").setValue("COD");
                Intent intent = new Intent(Payment.this,HomeActivity.class);
                startActivity(intent);
                Payment.this.finish();
            }
        });
    }
}
