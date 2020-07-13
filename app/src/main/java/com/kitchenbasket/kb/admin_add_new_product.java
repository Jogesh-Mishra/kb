package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class admin_add_new_product extends AppCompatActivity {

    private String CategoryName;

    private StorageReference ProductImagesRef;

    private DatabaseReference ProductsRef;

    private String productRandomKey, saveCurrentDate, saveCurrentTime, downloadImageUrl, Price, Pname;

    private Uri imageUri;


    ProgressDialog loadingBar;
    ImageView ivSelectImage;
    EditText etProductName, etProductPrice;
    Button btnAddNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        CategoryName = getIntent().getStringExtra("category");

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();

        ivSelectImage = findViewById(R.id.select_product_image);
        etProductName = findViewById(R.id.product_name);
        etProductPrice = findViewById(R.id.product_price);
        btnAddNewProduct = findViewById(R.id.add_new_product);

        loadingBar = new ProgressDialog(this);

        ivSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etProductName.getText().toString().isEmpty()||etProductPrice.getText().toString().isEmpty()){
                    Toast.makeText(admin_add_new_product.this, "Enter All Fields ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Pname = etProductName.getText().toString().trim();
                    Price = etProductPrice.getText().toString();

                    if (imageUri==null){
                        Toast.makeText(admin_add_new_product.this, "Product Image is Must", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        storageInformation();
                    }

                }
            }
        });
    }
    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    private void storageInformation(){

        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please Wait while we are checking credentials ! ");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate =currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime =currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate+saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(admin_add_new_product.this, "Error : "+message, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(admin_add_new_product.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(admin_add_new_product.this, "Image Download Url saved to Database Successfully", Toast.LENGTH_LONG).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });

            }
        });
    }

    private void SaveProductInfoToDatabase(){
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("Product_Id",productRandomKey);
        productMap.put("Date",saveCurrentDate);
        productMap.put("Time",saveCurrentTime);
        productMap.put("Image",downloadImageUrl);
        productMap.put("Category",CategoryName);
        productMap.put("Price",Price);
        productMap.put("Name",Pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(admin_add_new_product.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            admin_add_new_product.this.finish();
                            Toast.makeText(admin_add_new_product.this, "Product Added Successfully !", Toast.LENGTH_LONG).show();
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(admin_add_new_product.this, "Error : "+ task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            ivSelectImage.setImageURI(imageUri);
        }
    }
}
