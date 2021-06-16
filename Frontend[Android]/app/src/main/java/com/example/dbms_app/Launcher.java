package com.example.dbms_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Launcher extends AppCompatActivity implements TextWatcher {
    TextView createAccount;
    TextInputEditText usrName,pass;
    SharedPreferences sp;
    String MYPREFS = "MYPREFS";
    Context context;
    ConstraintLayout launcherconstraint;

    // Material Components variables
    MaterialButton loginButton;
    TextInputLayout usr,loginPass;
    public static Connect connect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        launcherconstraint = findViewById(R.id.launcherConstraint);
        connect = new Connect();

        createAccount = findViewById(R.id.create_Acc);
        loginButton = findViewById(R.id.loginButton);
        usrName = findViewById(R.id.userName);
        pass = findViewById(R.id.passwordEditText);
        sp = getSharedPreferences(MYPREFS, MODE_PRIVATE);
        context = Launcher.this;
        usr = findViewById(R.id.userNameTextLayout);
        loginPass = findViewById(R.id.passwordTextLayout);

        usrName.addTextChangedListener(this);
        pass.addTextChangedListener(this);

        boolean isfirst = sp.getBoolean("isFirst", true);
        if(!isfirst){
            Intent i = new Intent(Launcher.this, Show.class);
            startActivity(i);
            finish();
        }

        loginButton.setOnClickListener(v -> {
            String user = usrName.getText().toString().trim();
            String password = pass.getText().toString().trim();
            if(user.isEmpty()) {
                usr.setErrorEnabled(true);
                usr.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake));
                vibratePhone();
                usr.setError("Username cannot be empty");
            }
            if(password.isEmpty()) {
                loginPass.setErrorEnabled(true);
                loginPass.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake));
                vibratePhone();
                loginPass.setError("Password cannot be empty");
            }
            if(password.isEmpty() && user.isEmpty()) {
                usr.setErrorEnabled(true);
                loginPass.setErrorEnabled(true);
                usr.setError("Username cannot be empty");
                loginPass.setError("Password cannot be empty");
            }
            else {
                password = get_SHA1(password);
                connect.verify(user, password, context);
            }
        });
        createAccount.setOnClickListener(v -> {
            Intent i = new Intent(Launcher.this, MainActivity.class);
            startActivity(i);
            this.finish();
        });
    }
    private String get_SHA1(String s){
        MessageDigest md ;
        String hashtext = "";
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(s.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;
    }
    public void verify_callback(String username, String uid){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst", false);
            editor.putString("username", username);
            editor.putString("uid", uid);
            editor.apply();

            Intent i = new Intent(context, Show.class);
            startActivity(i);
            finish();
    }
    public void vibratePhone(){
        int DURATION = 100;
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(DURATION);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(loginPass.isErrorEnabled() || charSequence.length()>0){
            loginPass.setError(null);
            loginPass.setErrorEnabled(false);
        }
        if(usr.isErrorEnabled() || charSequence.length()>0) {
            usr.setError(null);
            usr.setErrorEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}