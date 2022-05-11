package com.example.whatsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.whatsclone.Adapters.ChatAdapter;
import com.example.whatsclone.Models.MessageModel;
import com.example.whatsclone.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter adapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // Reading Group Chat Details
        db.collection("GroupChat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {
                        Log.d("tag", String.valueOf(d.getData()));

                        MessageModel model = d.toObject(MessageModel.class);
                        messageModels.add(model);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("tag", "ChatDetails Empty!");
                }
                adapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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

                db.collection("GroupChat").document(model.getTimeStamp().toString())
                        .set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Sender Group Chat Msg successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing Sender Group chat Msg", e);                    }
                });

            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupChatActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}