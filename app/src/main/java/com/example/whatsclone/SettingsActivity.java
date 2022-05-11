package com.example.whatsclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatsclone.Models.Users;
import com.example.whatsclone.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("users").document(FirebaseAuth.getInstance().getUid())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                Users users = value.toObject(Users.class);
                                Picasso.get().load(users.getProfilePic())
                                        .placeholder(R.drawable.whatsapp).into(binding.profile);

                                binding.profileUserName.setText(users.getName());
                                binding.profileStatus.setText(users.getStatus());
                            }
                        });
            }
        }).start();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.profileUserName.getText().toString();
                String status = binding.profileStatus.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put("name", userName);
                data.put("status", status);

                db.collection("users").document(FirebaseAuth.getInstance().getUid())
                        .set(data, SetOptions.merge());

                Toast.makeText(SettingsActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null){
            Uri sFile = data.getData();
            binding.profile.setImageURI(sFile);

            // Create a storage reference from our app
           final StorageReference storageRef = storage.getReference()
                   .child("Profile Pictures").child(FirebaseAuth.getInstance().getUid());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    storageRef.putFile(sFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("profilePic", uri.toString());

                                    db.collection("users").document(FirebaseAuth.getInstance().getUid())
                                            .set(data, SetOptions.merge());

                                    Toast.makeText(SettingsActivity.this, "Profile Pic Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, "Failed to Change the Profile Image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();



        }
    }
}