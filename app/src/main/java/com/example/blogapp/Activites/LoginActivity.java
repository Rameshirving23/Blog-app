package com.example.blogapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText Lmail, Lpassword ;
    ImageView Lpic ;
    Button Lbutton, Lregister;
    FirebaseAuth mAuth;
    ProgressBar Login_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Lmail = findViewById(R.id.login_mail);
        Lpassword = findViewById(R.id.login_password);
        Lbutton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        Login_progressbar = findViewById(R.id.Login_progressbar);
        Lregister = findViewById(R.id.login_register);
        Login_progressbar.setVisibility(View.INVISIBLE);


        Lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_Mail = Lmail.getText().toString();
                String login_password = Lpassword.getText().toString();
                Log.i(TAG, "onClick: " + login_Mail);
                Log.i(TAG, "onClick: " + login_password);

                if(login_Mail.isEmpty()||login_password.isEmpty()){
                    showToast("please provide required details");
                }
                else{
                    Lbutton.setVisibility(View.INVISIBLE);
                    Lregister.setVisibility(View.INVISIBLE);
                    Login_progressbar.setVisibility(View.VISIBLE);
                    signIn(login_Mail, login_password);
                }
            }
        });

        Lregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(reg);
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void signIn(String m,String p){
        mAuth.signInWithEmailAndPassword(m,p)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Login_progressbar.setVisibility(View.VISIBLE);
                            Lbutton.setVisibility(View.INVISIBLE);
                            Lregister.setVisibility(View.INVISIBLE);
                            updateUI();
                        }
                        else{
                            Lbutton.setVisibility(View.VISIBLE);
                            Lregister.setVisibility(View.VISIBLE);
                            Login_progressbar.setVisibility(View.INVISIBLE);
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    public void updateUI(){

        Intent home = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!= null){
            updateUI();
        }
    }
}
