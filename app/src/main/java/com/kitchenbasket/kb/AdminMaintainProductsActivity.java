package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    Button btnSubmitMaintain;
    EditText etNameMaintain, etPriceMaintain;
    ImageView ivImageMaintain;

    private String productId ="";

    DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        btnSubmitMaintain = findViewById(R.id.submit_maintain);
        etNameMaintain = findViewById(R.id.product_name_maintain);
        etPriceMaintain=findViewById(R.id.product_price_maintain);
        ivImageMaintain = findViewById(R.id.product_image_maintain);

        productId = getIntent().getStringExtra("pid");

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        displaySpecificProductInfo();

        btnSubmitMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String Productname = dataSnapshot.child("Name").getValue().toString();
                    String Productprice = dataSnapshot.child("Price").getValue().toString();
                    String Productimage = dataSnapshot.child("Image").getValue().toString();

                    etNameMaintain.setText(Productname);
                    etPriceMaintain.setText(Productprice);
                    Picasso.get().load(Productimage).into(ivImageMaintain);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void applyChanges() {
        if (etPriceMaintain.getText().toString().isEmpty() || etNameMaintain.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
        }
        else {
            String name = etNameMaintain.getText().toString().trim();
            String price = etPriceMaintain.getText().toString().trim();

            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("Product_Id",productId);
            productMap.put("Price",price);
            productMap.put("Name",name);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied Successfully !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainProductsActivity.this,AdminCategoryActivity.class);
                        startActivity(intent);
                        AdminMaintainProductsActivity.this.finish();
                    }
                }
            });
        }
    }
}
