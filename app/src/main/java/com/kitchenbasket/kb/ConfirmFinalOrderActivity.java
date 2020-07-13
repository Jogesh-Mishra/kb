package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    EditText etBuyerName, etBuyerPhoneNumber,etBuyerAddress;
    Button btnConfirmDetails;
    TextView tvShipmentAmount;
    Spinner spSlot;


    String timeslot;

    String[] availableSlots = {"Select Time Slot","Today (9:00 AM- 12:00 PM)","Today (5:00 PM- 8:00 PM)","Tomorrow (9:00 AM- 12:00 PM)","Tomorrow (5:00 PM- 8:00 PM)","Day After Tomorrow (9:00 AM- 12:00 PM)","Day After Tomorrow (5:00 PM- 8:00 PM)"};

    DatabaseReference ordersRef, delRef;

    ArrayAdapter<String> adapter;

    private String totalAmount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Amount to be paid is Rs."+totalAmount, Toast.LENGTH_LONG).show();

        etBuyerAddress=findViewById(R.id.buyer_Address);
        etBuyerName=findViewById(R.id.buyer_name);
        etBuyerPhoneNumber=findViewById(R.id.buyer_phoneNumber);
        btnConfirmDetails =findViewById(R.id.btnConfirmDetails);
        tvShipmentAmount=findViewById(R.id.tvShipmentAmount);
        spSlot =findViewById(R.id.spSlot);

        tvShipmentAmount.setText("Total Amount : Rs."+totalAmount);

        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner3,availableSlots);
        spSlot.setAdapter(adapter);

        spSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        timeslot="Select Time Slot";
                        break;
                    case 1:
                        timeslot=availableSlots[1];
                        break;
                    case 2:
                        timeslot=availableSlots[2];
                        break;
                    case 3:
                        timeslot=availableSlots[3];
                        break;
                    case 4:
                        timeslot=availableSlots[4];
                        break;
                    case 5:
                        timeslot=availableSlots[5];
                        break;
                    case 6:
                        timeslot=availableSlots[6];
                        break;
                    case 7:
                        timeslot=availableSlots[7];
                        break;
                    default:
                        timeslot=availableSlots[0];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ConfirmFinalOrderActivity.this, "Select Time Slot !", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBuyerAddress.getText().toString().isEmpty()||etBuyerName.getText().toString().isEmpty()||etBuyerPhoneNumber.getText().toString().isEmpty()){
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Enter All Fields", Toast.LENGTH_LONG).show();
                }
                else{
                    confirmOrder(timeslot);
                }
            }
        });
    }
    private void confirmOrder(String timeslot){
        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


         ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("Name",etBuyerName.getText().toString());
        ordersMap.put("Phone",etBuyerPhoneNumber.getText().toString());
        ordersMap.put("Address",etBuyerAddress.getText().toString());
        ordersMap.put("Price",totalAmount);
        ordersMap.put("Date",saveCurrentDate);
        ordersMap.put("Time",saveCurrentTime);
        ordersMap.put("State","Not Shipped");
        ordersMap.put("Payment","NOT SET");
        ordersMap.put("TimeSlot",timeslot);

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart_List").child("User View")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Order Placed Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,Payment.class);
                                        intent.putExtra("totalAmount",totalAmount);
                                        startActivity(intent);
                                        ConfirmFinalOrderActivity.this.finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
