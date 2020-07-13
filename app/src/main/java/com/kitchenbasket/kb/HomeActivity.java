package com.kitchenbasket.kb;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference ProductsRef;
    private RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;



    private String type = "";
    private AppBarConfiguration mAppBarConfiguration;

    ProgressDialog loadingBar;

    Float val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        loadingBar = new ProgressDialog(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getStringExtra("Admin");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Admin")) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "You are Now in the Cart !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final FloatingActionButton WA = findViewById(R.id.WA);
        WA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Admin")) {
                    WA.setVisibility(View.GONE);
                } else {
                    WA.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, "Message us on WhatsApp !", Toast.LENGTH_LONG).show();
                    String contact = "+91 9438393588";
                    String url = "https://api.whatsapp.com/send?phone=" + contact;
                    try {
                        PackageManager pm = getApplicationContext().getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(HomeActivity.this, "WhatsApp app not installed in your phone", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView user_profile_name = headerView.findViewById(R.id.user_profile_name);

        if (!type.equals("Admin")) {
            user_profile_name.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        else{
            user_profile_name.setText("Welcome Admin !");
        }

        if (!type.equals("Admin")) {
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
            drawer.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.product_name.setText(products.getName());
                productViewHolder.product_price.setText("Price: Rs." + products.getPrice());
                Picasso.get().load(products.getImage()).into(productViewHolder.product_image);


                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("Admin")) {
                            Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                            intent.putExtra("pid", products.getProduct_Id());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid", products.getProduct_Id());
                            intent.putExtra("name",products.getName());
                            startActivity(intent);
                        }
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

        recycler_menu.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

            int id = item.getItemId();
            if (id == R.id.nav_cart) {
                if (!type.equals("Admin")){
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "You are Now in the Cart !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Option not available for Admin", Toast.LENGTH_LONG).show();
                }
            }
            else if (id == R.id.nav_categories) {
                nav_categories();
            }
            else if (id == R.id.nav_Orders) {
                if (!type.equals("Admin")){
                    startActivity(new Intent(HomeActivity.this, CustomerOrder.class));
                    Toast.makeText(this, "You can see your Pending Orders Here", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Option not available for Admin", Toast.LENGTH_LONG).show();
                }
            }
            else if (id == R.id.nav_orders) {
                Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Search for your Product !", Toast.LENGTH_LONG).show();
            }
            else if (id == R.id.nav_logout) {
                if (!type.equals("Admin")){
                    logout();
                }
                else{
                    Toast.makeText(this, "Option not available for Admin", Toast.LENGTH_LONG).show();
                }

            } else if (id == R.id.nav_contactUs) {
                if (!type.equals("Admin")){
                    Intent intent = new Intent(HomeActivity.this, ContactUs.class);
                    startActivity(intent);
                    Toast.makeText(this, "Feel Free to Contact Us", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Option not available for Admin", Toast.LENGTH_LONG).show();
                }
            }
            else if (id==R.id.nav_feedback){
                if (!type.equals("Admin")){
                    feedback();
                }
                else{
                    Toast.makeText(this, "Option not available for Admin", Toast.LENGTH_LONG).show();
                }
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
    }


    private void logout(){
        CharSequence options[] = new CharSequence[]{"Yes","No"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Are you Sure ?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "User Logged Out Successfully ! ", Toast.LENGTH_SHORT).show();
                    HomeActivity.this.finish();
                }
                if (which==1){
                   dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void nav_categories(){
        final CharSequence options[] = new CharSequence[]{"Fruits","Vegetables","Non-Veg","Milk and Milk Products","Exit"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Select Category :");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    Intent intent = new Intent(HomeActivity.this,Categories.class);
                    intent.putExtra("categoryHome","Fruits");
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "Category : Fruits", Toast.LENGTH_LONG).show();
                }
                if (which==1){
                    Intent intent = new Intent(HomeActivity.this,Categories.class);
                    intent.putExtra("categoryHome","Vegetables");
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "Category : Vegetables ", Toast.LENGTH_LONG).show();
                }
                if (which==2){
                    Intent intent = new Intent(HomeActivity.this,Categories.class);
                    intent.putExtra("categoryHome","Non-Veg");
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "Category : Non-Veg ", Toast.LENGTH_LONG).show();
                }
                if (which==3){
                    Intent intent = new Intent(HomeActivity.this,Categories.class);
                    intent.putExtra("categoryHome","Milk and Milk Products");
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "Category : Milk and Milk Products ", Toast.LENGTH_LONG).show();
                }
                if (which==4){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void feedback(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.feedback,null);

        final Button btnSubmitFeedback = mview.findViewById(R.id.btnSubmitFeedback);
        final EditText etFeedback = mview.findViewById(R.id.etFeedback);
        final RatingBar ratingBar = mview.findViewById(R.id.ratingBar);
        final TextView mRatingScale = mview.findViewById(R.id.tvRatingScale);

        alert.setView(mview);

        final AlertDialog alertDialog = alert.create();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("Not Rated");
                }
                val =v;
            }
        });

        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etFeedback.getText().toString().isEmpty()){
                    alertDialog.dismiss();
                }
                else{
                    loadingBar.setTitle("Sending FeedBack");
                    loadingBar.setMessage("Please Wait while we are adding your Feedback ! ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    String feedback = etFeedback.getText().toString();
                    String rating = mRatingScale.getText().toString();

                    FirebaseAuth fauth = FirebaseAuth.getInstance();
                    String userEmail = fauth.getCurrentUser().getUid();

                    DatabaseReference feedRef = FirebaseDatabase.getInstance().getReference().child("Feedback").child(userEmail);
                    String saveCurrentDate, saveCurrentTime;

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    final HashMap<String,Object> feedMap = new HashMap<>();
                    feedMap.put("Feedback",feedback);
                    feedMap.put("Rating",String.valueOf(val)+": "+rating);
                    feedMap.put("Date",saveCurrentDate);
                    feedMap.put("Time",saveCurrentTime);

                    feedRef.updateChildren(feedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Feedback Sent Successfully", Toast.LENGTH_LONG).show();
                                etFeedback.setText("");
                                ratingBar.setRating(0);
                                loadingBar.dismiss();
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
        alertDialog.show();
    }
}
