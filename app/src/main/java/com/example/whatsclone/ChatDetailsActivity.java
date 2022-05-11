package com.example.whatsclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatsclone.Adapters.ChatAdapter;
import com.example.whatsclone.Models.MessageModel;
import com.example.whatsclone.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String profilePic = getIntent().getStringExtra("profilePic");
        String userName = getIntent().getStringExtra("userName");

        binding.nameUser.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.user).into(binding.picUser);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatDetailsActivity.this, MainActivity.class));
                finish();
            }
        });

       final ArrayList<MessageModel> messageModels = new ArrayList<>();

       final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId+receiverId;
        final String receiverRoom = receiverId+senderId;

//        final DocumentReference docRef1 = db.collection("chats").document(senderRoom).collection("chatDetails");
//        docRef1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("TAG", "Listen failed.", e);
//                    return;
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    Log.d("TAG", "Current data: " + snapshot.getData());
//                } else {
//                    Log.d("TAG", "Current data: null");
//                }
//            }
//        });


        // Reading chat details
        CollectionReference docRef = db.collection("chats").document(senderRoom).collection("chatDetails");
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                messageModels.clear();
                            for (DocumentSnapshot d : list) {
                                Log.d("tag", String.valueOf(d.getData()));

                                MessageModel model = d.toObject(MessageModel.class);
                                model.setMessageID(d.getId());
                                messageModels.add(model);
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    chatAdapter.notifyDataSetChanged();

                                }
                            });
                            chatAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("tag", "ChatDetails Empty!");
                        }
                        chatAdapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", "Failed to get ChatDetails "+e);
               }
        });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etMsg.getText().toString().isEmpty()){
                    binding.etMsg.setError("Message Box is Empty!");
                    return;
                }

                final String message = binding.etMsg.getText().toString();

                final MessageModel model = new MessageModel(senderId, message);
                model.setTimeStamp(new Date().getTime());

                binding.etMsg.setText("");

                // Sender Msg Storing in FireStore
                db.collection("chats").document(senderRoom).
                        collection("chatDetails").document(model.getTimeStamp().toString())
                        .set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Sender Msg successfully written!");

                        // Receiver Msg Storing in FireStore
                        db.collection("chats").document(receiverRoom).
                                collection("chatDetails").document(model.getTimeStamp().toString())
                                .set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "Receiver Msg successfully written!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing Receiver Msg", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing Sender Msg", e);
                    }
                });
            }
        });
    }


}