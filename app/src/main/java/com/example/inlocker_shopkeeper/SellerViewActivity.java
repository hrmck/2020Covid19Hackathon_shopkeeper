package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerViewActivity extends AppCompatActivity implements View.OnClickListener {
    Button toAddNewItem;
    TextView textViewStoreName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();    //assume user logeed in
    private CollectionReference productListRef = db.collection("storeList").document(user.getUid()).collection("Products");

    private StoreItemAdapter adapter;
    private String TAG = "sellerView";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view);

        toAddNewItem = findViewById(R.id.addNewItem_sellerViewBtn);
        toAddNewItem.setOnClickListener(this);

        textViewStoreName = findViewById(R.id.storeName_sellerView_textView);
        setStoreName();
        setUpRecyclerView();
    }

    private void setStoreName() {
        db.collection("storeList").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "retrieveSuccess: loading store info");
                        Toast.makeText(getApplicationContext(),
                                "Info loaded successfully", Toast.LENGTH_SHORT).show();
                        textViewStoreName.setText(documentSnapshot.getString("Name"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "some error");
            }
        });
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<StoreItem> options = new FirestoreRecyclerOptions.Builder<StoreItem>()
                .setQuery(productListRef, StoreItem.class)
                .build();

        adapter = new StoreItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.sellerView_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNewItem_sellerViewBtn) {
            Intent intent = new Intent(this, NewUploadItemActivity.class);
            startActivity(intent);
        }
    }
}
