package com.example.blogapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class post_detail extends AppCompatActivity {

    ImageView detail_post_img, detail_user_image, detail_viewer_img;
    TextView detail_Title, detail_description, detail_username;
    Button detail_addButton;
    TextView detail_comment_box;
    FirebaseUser user;
    DatabaseReference databaseReference, commentReference;
    String PostKey;
    RecyclerView comment_rv;
    Comment_Adapter cmtAdapter;
    List<comment> commentsList;
    private static final String TAG = "post_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();

        comment_rv = findViewById(R.id.post_detail_comments);
        detail_addButton = findViewById(R.id.detail_comment_button);
        detail_comment_box = findViewById(R.id.detail_comment_box);
        detail_Title = findViewById(R.id.detail_title);
        detail_description = findViewById(R.id.detail_description);
        detail_user_image = findViewById(R.id.detail_creator);
        detail_post_img = findViewById(R.id.detail_img);
       // detail_viewer_img = findViewById(R.id.detail_userImage);
        detail_username = findViewById(R.id.detail_author);

        /*Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();*/

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(detail_post_img);

        String postTitle = getIntent().getExtras().getString("title");
        detail_Title.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        //Glide.with(this).load(userpostImage).into(detail_user_image);
        detail_user_image.setImageURI(Uri.parse(userpostImage));

        String postDescription = getIntent().getExtras().getString("description");
        detail_description.setText(postDescription);

        //Glide.with(this).load(user.getPhotoUrl()).into(detail_viewer_img);

        PostKey = getIntent().getExtras().getString("postKey");

        detail_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detail_addButton.setClickable(false);
                databaseReference = FirebaseDatabase.getInstance().getReference("comment").child(PostKey).push();
                String comment = detail_comment_box.getText().toString();
                String uid = user.getUid();
                String uname = user.getDisplayName();
                Log.i(TAG, "onClick: " + uname.toString());
                String uimg = user.getPhotoUrl().toString();
                comment cmt = new comment(comment,uid,uname,uimg);

                if(comment.isEmpty()){
                    showMessage("comment box is empty!");
                }
                else {
                    databaseReference.setValue(cmt).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            detail_addButton.setClickable(true);
                            showMessage("comment added");
                            detail_comment_box.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            detail_addButton.setClickable(true);
                            showMessage("comment not added..try again");
                            detail_comment_box.setText("");
                        }
                    });
                }

            }
        });

        iniCommentRecyclerView();

    }

    private void iniCommentRecyclerView() {

        comment_rv.setLayoutManager(new LinearLayoutManager(this));

        commentReference = FirebaseDatabase.getInstance().getReference("comment").child(PostKey);
        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList = new ArrayList<>();
                for(DataSnapshot datasnap: snapshot.getChildren()){

                    comment comment = datasnap.getValue(com.example.blogapp.Activites.comment.class);
                    commentsList.add(comment);

                }

                cmtAdapter = new Comment_Adapter(commentsList,getApplicationContext());
                comment_rv.setAdapter(cmtAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
