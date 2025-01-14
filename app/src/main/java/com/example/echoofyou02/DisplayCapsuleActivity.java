package com.example.echoofyou02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DisplayCapsuleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CapsuleAdapter capsuleAdapter;
    private FirebaseFirestore firestore;
    private List<Capsule> capsuleList;
    private ImageButton backButton, homeButton, createCapsuleButton, myAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_capsule);

        // initialize views
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        homeButton = findViewById(R.id.HomeButton);
        createCapsuleButton = findViewById(R.id.creatCapsuleButton);
        myAccountButton = findViewById(R.id.myAccountButton);

        // initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // initialize capsule list and adapter
        capsuleList = new ArrayList<>();
        capsuleAdapter = new CapsuleAdapter(capsuleList);

        // set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(capsuleAdapter);

        // load capsules from Firestore
        loadCapsules();


        createCapsuleButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddCapsuleActivity.class);
            startActivity(intent);
        });

    }

    private void loadCapsules() {
        firestore.collection("capsules").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                capsuleList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Capsule capsule = document.toObject(Capsule.class);
                    capsuleList.add(capsule);
                }
                capsuleAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load capsules", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
