package com.example.blogapp.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    ImageView userImage;
    static int PReqCode = 1;
    static int ReqCode = 2;
    Uri pickedImageUrl;
    TextView userName, userMail, password, conPassword;
    Button regButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userImage = findViewById(R.id.userProPic);
        userName = findViewById(R.id.regName);
        userMail = findViewById(R.id.regEmail);
        password = findViewById(R.id.regPassword);
        conPassword = findViewById(R.id.regConPassword);
        regButton = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > 22){
                    checkAndRequestForPermission();
                }
                else{
                    openGallery();
                }
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String mUserName = userName.getText().toString();
                final String mUserMail = userMail.getText().toString();
                final String mPassword = password.getText().toString();
                final String mConPassword = conPassword.getText().toString();

                if(mUserName.isEmpty()|| mUserMail.isEmpty()|| mPassword.isEmpty()|| !mConPassword.equals(mPassword)){
                    showMessage("Kindly fill the required fields");
                }
                else{

                    regButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    createUserAccount(mUserName,mUserMail,mPassword);
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void createUserAccount(final String mUserName, String mUserMail, String mPassword) {
        mAuth.createUserWithEmailAndPassword(mUserMail,mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Account Created");
                            if(pickedImageUrl != null){
                                updateUserInfo(mUserName,pickedImageUrl,mAuth.getCurrentUser());
                            }
                            else {
                                userInfoWithoutPhoto(mUserName,mAuth.getCurrentUser());
                            }
                        }
                        else{
                            showMessage("Account creation failed");
                            regButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String mUserName, final Uri pickedImageUrl, final FirebaseUser currentUser) {

        StorageReference mRef = FirebaseStorage.getInstance().getReference().child("users_photo");
        final StorageReference imageFilePath = mRef.child(pickedImageUrl.getLastPathSegment());
        imageFilePath.putFile(pickedImageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageFilePath.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(mUserName)
                                            .setPhotoUri(pickedImageUrl)
                                            .build();
                                    
                                    currentUser.updateProfile(profileChangeRequest)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    updateUI();
                                                }
                                            });
                                }
                            });  
                    }
                });
    }

    private void userInfoWithoutPhoto(String mUserName,FirebaseUser currentUser){
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserName)
                .build();

        currentUser.updateProfile(profileChangeRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void openGallery() {
        Log.i(TAG, "openGallery: ");
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,ReqCode);
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,"kindly grant required permissions",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else{
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!= null){
            try {
                pickedImageUrl = data.getData();
                userImage.setImageURI(pickedImageUrl);
            }catch (Exception e){
                showMessage(e.getMessage().toString());
            }
        }else{
            showMessage("no image");
        }
       return;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent log = new Intent(this,LoginActivity.class);
        startActivity(log);
    }
}
