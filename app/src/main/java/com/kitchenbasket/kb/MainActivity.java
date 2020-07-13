package com.kitchenbasket.kb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etMail, etPass;
    Button btnSubLogin, btnSubRegister;
    TextView tvReset, tvAdmin;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_bar);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        if (user!=null){
            showProgress(true);
            tvLoad.setText("Checking Credentials. Please Wait !");
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            showProgress(false);
            Toast.makeText(this, "User Logged In Successfully !", Toast.LENGTH_SHORT).show();
            MainActivity.this.finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activitybar);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMail = findViewById(R.id.etMail);
        etPass = findViewById(R.id.etPass);
        btnSubLogin = findViewById(R.id.btnSubLogIn);
        btnSubRegister = findViewById(R.id.btnSubRegister);
        tvReset = findViewById(R.id.tvReset);
        tvAdmin =findViewById(R.id.tvAdmin);


        btnSubLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (etMail.getText().toString().isEmpty() || etPass.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Fill Required Details", Toast.LENGTH_LONG).show();
                    btnSubLogin.setBackground(getDrawable(R.drawable.buttons));
                }

                else {

                    String email = etMail.getText().toString().trim();
                    String password = etPass.getText().toString().trim();

                    FirebaseAuth FAuth = FirebaseAuth.getInstance();

                    showProgress(true);
                    tvLoad.setText("Logging In. Please Wait !");

                    FAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                                showProgress(false);
                                Toast.makeText(MainActivity.this, "User Logged In Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                btnSubLogin.setBackground(getDrawable(R.drawable.buttons));
                                Toast.makeText(MainActivity.this, "Error:" + task.getException(), Toast.LENGTH_LONG).show();
                                showProgress(false);
                            }
                        }
                    });
                }

            }
        });

        btnSubRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerRegister.class);
                startActivity(intent);
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                View mview = getLayoutInflater().inflate(R.layout.pass_reset,null);

                final Button btnResetSubmit = mview.findViewById(R.id.btnResetSubmit);
                final EditText etResetMail = mview.findViewById(R.id.etResetMail);

                alert.setView(mview);

                final AlertDialog alertDialog = alert.create();

                btnResetSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etResetMail.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "Please Enter The E-mail", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String resetMail = etResetMail.getText().toString().trim();

                            FirebaseAuth fAuth = FirebaseAuth.getInstance();

                            showProgress(true);

                            fAuth.sendPasswordResetEmail(resetMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Password Reset Mail has been Sent !", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                        showProgress(false);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Error:"+task.getException(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                        alertDialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });
                alertDialog.show();
            }
        });

        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMail.setVisibility(View.GONE);
                btnSubRegister.setVisibility(View.GONE);
                tvReset.setVisibility(View.GONE);
                tvAdmin.setVisibility(View.GONE);
                btnSubLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvLoad.setText("Logging in Admin...");
                        if (etPass.getText().toString().equals("kitchenbasket")){
                            Intent intent = new Intent(MainActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
