package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import Model.Products;

public class ProductDetailsActivity extends AppCompatActivity {

    Button btnAddToCart;
    ImageView ivProductDetailImage;
    TextView tvProductDetailName, tvProductDetailPrice;
    Spinner spKg, spG;

    String[] kilograms = {"Kg/L/Unit","0","1","2","3","4","5","6","7","8","9","10"};
    String[] grams ={"g/mL","0","250","500","750"};

    ArrayAdapter<String> adapter, Gadapter ;

    ProgressDialog loadingBar;


    private String productID="",state="Normal",productName="";
    Double quantity=0., Kilo=0.,gram=0.;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        btnAddToCart = findViewById(R.id.add_to_cart);
        spKg = findViewById(R.id.spKg);
        spG=findViewById(R.id.spG);
        ivProductDetailImage=findViewById(R.id.product_image_details);
        tvProductDetailName= findViewById(R.id.product_name_details);
        tvProductDetailPrice = findViewById(R.id.etPrice);

        loadingBar = new ProgressDialog(this);

        productID = getIntent().getStringExtra("pid");

        productName = getIntent().getStringExtra("name");

        if (productName.equals("Pineapple (Sapuri)")||productName.equals("Coconut (Nadia)")||productName.equals("Green Coconut (Paida)")
        ||productName.equals("Egg Tray")||productName.equals("Goat Head")||productName.equals("Lemon (Lembu)")){
            spG.setVisibility(View.GONE);
            Toast.makeText(this, "Select Quantity from First Dropdown Menu", Toast.LENGTH_LONG).show();
        }

        getProductDetails(productID);

        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner,kilograms);
        spKg.setAdapter(adapter);
        Gadapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner2,grams);
        spG.setAdapter(Gadapter);

        spKg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        break;
                    case 1 :
                        Kilo=0.;
                        break;
                    case 2 :
                        Kilo=1.;
                        break;
                    case 3 :
                        Kilo=2.;
                        break;
                    case 4 :
                        Kilo=3.;
                        break;
                    case 5:
                        Kilo=4.;
                        break;
                    case 6:
                        Kilo=5.;
                        break;
                    case 7:
                        Kilo=6.;
                        break;
                    case 8:
                        Kilo=7.;
                        break;
                    case 9:
                        Kilo=8.;
                        break;
                    case 10:
                        Kilo=9.;
                        break;
                    default:
                        Kilo=0.;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ProductDetailsActivity.this, "Please Select Kg/L", Toast.LENGTH_SHORT).show();
            }
        });

        spG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        gram=0.;
                        break;
                    case 2:
                        gram=250.;
                        break;
                    case 3:
                        gram=500.;
                        break;
                    case 4:
                        gram=750.;
                        break;
                    default:
                        gram=0.;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ProductDetailsActivity.this, "Please Select g/mL", Toast.LENGTH_SHORT).show();
            }
        });


        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (state.equals("Order Placed")||state.equals("Order Shipped")){
                   // Toast.makeText(ProductDetailsActivity.this, "You can add Products once you receive your earlier orders", Toast.LENGTH_LONG).show();
               // }
                //else{
                    loadingBar.setTitle("Adding To Cart");
                    loadingBar.setMessage("Please Wait while we are adding the Product to your Cart ! ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addingToCartList(Kilo,gram);
               // }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkOrderState();
    }

    private void addingToCartList(Double Kilo, Double gram) {

        quantity = Kilo + (gram / 1000);

        if (quantity == 0.) {
            Toast.makeText(this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        } else {

            String saveCurrentDate, saveCurrentTime;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            final FirebaseAuth fauth = FirebaseAuth.getInstance();
            final FirebaseUser user = fauth.getCurrentUser();

            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");

            final HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("Product_Id", productID);
            cartMap.put("Name", tvProductDetailName.getText().toString());
            cartMap.put("Price", tvProductDetailPrice.getText().toString());
            cartMap.put("Date", saveCurrentDate);
            cartMap.put("Time", saveCurrentTime);
            cartMap.put("quantity", String.valueOf(quantity));

            cartListRef.child("User View").child(user.getUid()).child("Products").child(productID)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                cartListRef.child("Admin View").child(user.getUid()).child("Products").child(productID)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProductDetailsActivity.this, "Product added to Cart List", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void getProductDetails(String productId){
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    tvProductDetailName.setText(products.getName());
                    tvProductDetailPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(ivProductDetailImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProductDetailsActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkOrderState(){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String ShippingState = dataSnapshot.child("State").getValue().toString();

                    if (ShippingState.equals("Not Shipped")){
                        state = "Order Placed";
                    }

                    else if(ShippingState.equals("Shipped")){
                        state = "Order Shipped";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
