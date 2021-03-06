package com.example.VaraBari.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.VaraBari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class LogInScreenActivity extends AppCompatActivity {

    // Views
    private EditText loginEmail, loginPassword;
    private CardView logINButton;
    private Animation animationMix, animationL2R, animationR2L, animationD2U;
    public boolean doubleBackToExitPressedOnce = false;

    // Firebase
    private FirebaseAuth firebaseAuth;

    public static String prefName = "MyPrefsfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        // Hooks
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        logINButton = (CardView) findViewById(R.id.login_button);
        firebaseAuth = FirebaseAuth.getInstance();
        animationMix = AnimationUtils.loadAnimation(LogInScreenActivity.this, R.anim.mixed_anim);
        findViewById(R.id.login_house).startAnimation(animationMix);
        animationL2R = AnimationUtils.loadAnimation(LogInScreenActivity.this, R.anim.lefttoright);
        findViewById(R.id.login_email_layout).startAnimation(animationL2R);
        animationR2L = AnimationUtils.loadAnimation(LogInScreenActivity.this, R.anim.righttoleft);
        findViewById(R.id.login_pass_layout).startAnimation(animationR2L);
        animationD2U = AnimationUtils.loadAnimation(LogInScreenActivity.this, R.anim.downtoup);
        findViewById(R.id.login_screen_buttons).startAnimation(animationD2U);


        logINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Bounce).duration(1000).repeat(2).playOn(logINButton);
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email is required");
                    loginEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password is required");
                    loginPassword.requestFocus();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInScreenActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(LogInScreenActivity.prefName, 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("hasLoggedIn", true);
                                    editor.commit();

                                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                                    finish();
                                } else {
                                    Toast.makeText(LogInScreenActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void button_signupForm(View view) {
        startActivity(new Intent(getApplicationContext(), Signup_Form.class));
//        finish();
    }


    public void reset_password(View view) {
        EditText resetMail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email to receive reset link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LogInScreenActivity.this, "Reset link was sent to your email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(LogInScreenActivity.this, "Reset link could not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passwordResetDialog.create().show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}