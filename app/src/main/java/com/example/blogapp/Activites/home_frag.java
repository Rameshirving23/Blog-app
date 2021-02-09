package com.example.blogapp.Activites;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.sql.DatabaseMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class home_frag extends Fragment {
    private static final String TAG = "home_frag";
    FloatingActionButton fab;
    Dialog popAddpost;
    ProgressBar popup_progressBar;
    ImageView popup_button,popup_Postimage,popup_UserImage;
    TextView popup_title,popup_description;
    FirebaseUser user;
    static int PReqCode = 3;
    static int ReqCode = 4;
    private Uri pickedImageUrl = null;
    RecyclerView PostRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Post> postList;
    ProgressBar progressBar;

    public home_frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragmentView = inflater.inflate(R.layout.fragment_home_frag, container, false);
        PostRecyclerView = fragmentView.findViewById(R.id.post_Recycler_View);
        PostRecyclerView.setHasFixedSize(true);
        PostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.pop_progress);
        progressBar.setVisibility(View.VISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        iniPopup();

        fab = view.findViewById(R.id.popup_floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddpost.show();
            }
        });

        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup_button.setVisibility(View.INVISIBLE);
                popup_progressBar.setVisibility(View.VISIBLE);

                final String title = popup_title.getText().toString();
                final String Description = popup_description.getText().toString();
                if(!title.equals("") && !Description.equals("") && pickedImageUrl != null ) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUrl.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUrl)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    imageFilePath.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Date date = new Date();
                                                    String ImagedownloadLink = uri.toString();
                                                    Post post = new Post(title,Description,ImagedownloadLink
                                                            ,user.getUid(),user.getPhotoUrl().toString(),new Timestamp(date.getTime()));

                                                    addPost(post);
                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessage(e.getMessage());
                                            popup_button.setVisibility(View.VISIBLE);
                                            popup_progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });

                }
                else {
                    showMessage("Kindly provide Title,Description and picture");
                    popup_button.setVisibility(View.VISIBLE);
                    popup_progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        popup_Postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });



    }

    private void iniPopup() {
        popAddpost = new Dialog(getActivity());
        popAddpost.setContentView(R.layout.create_popup);
        popAddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddpost.getWindow().getAttributes().gravity = Gravity.TOP;
        popup_progressBar = popAddpost.findViewById(R.id.popup_progressBar);
        popup_progressBar.setVisibility(View.INVISIBLE);
        popup_button =  popAddpost.findViewById(R.id.popup_button);
        popup_Postimage = popAddpost.findViewById(R.id.popup_Postimage);
        popup_UserImage = popAddpost.findViewById(R.id.popup_UserImg);
        popup_title = popAddpost.findViewById(R.id.popup_title);
        popup_description = popAddpost.findViewById(R.id.popup_descripton);


        Glide.with(getActivity()).load(user.getPhotoUrl()).into(popup_UserImage);

    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getActivity(),"kindly grant required permissions",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else{
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,ReqCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!= null){
            pickedImageUrl = data.getData();
            popup_Postimage.setImageURI(pickedImageUrl);
        }else{
            showMessage("no image");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    public void addPost(Post post){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mRef = database.getReference("Posts").push();

        String key = mRef.getKey();
        post.setKey(key);

        mRef.setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Post added Succesfully");
                        popup_button.setVisibility(View.VISIBLE);
                        popup_progressBar.setVisibility(View.INVISIBLE);
                        popAddpost.dismiss();
                    }
                });

    }

    @Override
    public void onStart() {

        super.onStart();

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();

                for(DataSnapshot postsnap : snapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter = new PostAdapter(getActivity(),postList);
                PostRecyclerView.setAdapter(postAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }


}
