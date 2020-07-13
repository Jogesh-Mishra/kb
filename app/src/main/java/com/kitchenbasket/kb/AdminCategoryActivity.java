package com.kitchenbasket.kb;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminCategoryActivity extends AppCompatActivity {

    ImageView ivApple, ivVegetables, ivNonVeg , ivMilkProducts;
    Button btnAdminLogout;
    Button btnCheckOrder;
    Button btnMaintain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        Toast.makeText(this, "Welcome Admin !", Toast.LENGTH_SHORT).show();

        ivApple = findViewById(R.id.apple);
        ivNonVeg =findViewById(R.id.nonVeg);
        ivVegetables=findViewById(R.id.vegetables);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnCheckOrder = findViewById(R.id.checkOrders);
        btnMaintain = findViewById(R.id.btnmaintain);
        ivMilkProducts = findViewById(R.id.milk_products);

        btnMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        btnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminCategoryActivity.this,MainActivity.class));
                AdminCategoryActivity.this.finish();
            }
        });

        ivApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,admin_add_new_product.class);
                intent.putExtra("category","Fruits");
                startActivity(intent);
            }
        });
        ivVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,admin_add_new_product.class);
                intent.putExtra("category","Vegetables");
                startActivity(intent);
            }
        });
        ivNonVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,admin_add_new_product.class);
                intent.putExtra("category","Non-Veg");
                startActivity(intent);
            }
        });

        ivMilkProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,admin_add_new_product.class);
                intent.putExtra("category","Milk and Milk Products");
                startActivity(intent);
            }
        });

        btnCheckOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

    }
}
