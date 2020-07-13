package com.kitchenbasket.kb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

import Model.Orders;

public class Online extends AppCompatActivity {

    private static final int UPI_PAYMENT =0 ;
    EditText etPayeeName, etNote;
    Button btnPay;
    TextView tvUPI, tvPayAmount;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        etPayeeName = findViewById(R.id.etPayeeName);
        etNote=findViewById(R.id.etNote);
        btnPay=findViewById(R.id.btnPay);
        tvUPI =findViewById(R.id.tvUPI);
        tvPayAmount=findViewById(R.id.tvPayAmount);


        final String amount = getIntent().getStringExtra("totalAmount");

        tvPayAmount.setText("Amount to be Paid : Rs."+amount);

        final String UPI = "kitchenbasket@icici";
        tvUPI.setText("Pay To : "+ "Kitchen Basket");


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPayeeName.getText().toString().isEmpty()||etNote.getText().toString().isEmpty()){
                    Toast.makeText(Online.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    String name = etPayeeName.getText().toString();
                    String note = etNote.getText().toString();

                    payUsingUPI(name, UPI, amount, note);
                }
            }
        });
    }
    void payUsingUPI(String name, String UPI, String amount, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", UPI)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay With :");
        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, "No UPI App Installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if (RESULT_OK == resultCode || resultCode == 11) {
                    if (data != null) {
                        String txt = data.getStringExtra("response");
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add(txt);
                        upiPaymentDataOperation(datalist);
                    } else {
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add("Nothing");
                        upiPaymentDataOperation(datalist);
                    }
                } else {
                    ArrayList<String> datalist = new ArrayList<>();
                    datalist.add("Nothing");
                    upiPaymentDataOperation(datalist);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Online.this)) {
            String str = data.get(0);
            String paymentCancel = "";
            if (str == null)
                str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
                            equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by the User";
                }
            }
            if (status.equals("success")) {
                Toast.makeText(this, "Transaction Successful", Toast.LENGTH_LONG).show();
                DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child("Orders");
                dataref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment").setValue("Online");


                startActivity(new Intent(Online.this,HomeActivity.class));
                Online.this.finish();

            } else if ("Payment cancelled by the User".equals(paymentCancel)) {
                Toast.makeText(this, "Payment cancelled by the User", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction Failed. Please Try Again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Internet Connection is unavailable ! ", Toast.LENGTH_SHORT).show();
        }
    }

    public static Boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
