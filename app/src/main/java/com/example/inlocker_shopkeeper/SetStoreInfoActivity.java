package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SetStoreInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SetStoreInfoAcitivity >> ";
    EditText editTextStoreName, editTextStorePhoneNo, editTextStoreAddr;
    Button saveInfo;
    ProgressBar progressBar;
    FirebaseUser user;
    DocumentReference documentReference;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_store_info);

        progressBar = findViewById(R.id.setStoreInfo_progressBar);
        editTextStoreName = findViewById(R.id.storeName_setStoreInfo_editText);
        editTextStorePhoneNo = findViewById(R.id.phoneNo_setStoreInfo_editText);
        editTextStoreAddr = findViewById(R.id.storeAddr_setStoreInfo_editText);
        saveInfo = findViewById(R.id.saveInfo_setStoreInfoBtn);

        user = fAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "User not yet login", Toast.LENGTH_SHORT).show();
            //go back to login page
            Intent intent = new Intent(this, MainActivity.class);
            getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        //initialize for connection to Firestore
        String uid = user.getUid();
        documentReference = fStore.collection("storeList").document(uid);

        //load store info if exist
        retrieveDataFromFirebaseIfExist();

        saveInfo.setOnClickListener(this);
    }

    private void retrieveDataFromFirebaseIfExist() {
        progressBar.setVisibility(View.VISIBLE);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "retrieveSuccess: loading store info");
                Toast.makeText(getApplicationContext(),
                        "Info loaded successfully", Toast.LENGTH_SHORT).show();
                editTextStoreName.setText(documentSnapshot.getString("Name"));
                editTextStorePhoneNo.setText(documentSnapshot.getString("PhoneNo"));
                editTextStoreAddr.setText(documentSnapshot.getString("Address"));
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveInfoToFirestore(){
        progressBar.setVisibility(View.VISIBLE);

        String storeName = editTextStoreName.getText().toString();
        String storePhoneNo = editTextStorePhoneNo.getText().toString();
        String storeAddr = editTextStoreAddr.getText().toString();

        Map<String, Object> store = new HashMap<>();
        store.put("Name", storeName);
        store.put("PhoneNo", storePhoneNo);
        store.put("Address", storeAddr);

        documentReference.set(store).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess: store info updated, uid: " + user.getUid());
                Toast.makeText(getApplicationContext(),
                        "Info saved successfully,\ngoing back to main page", Toast.LENGTH_SHORT).show();
                //go back to main page if update info successfully
                Intent intent = new Intent(getApplicationContext(), SellPageActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure" + e.toString());
                Toast.makeText(getApplicationContext(),
                        "Info saved failed: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.saveInfo_setStoreInfoBtn){
            saveInfoToFirestore();
        }
    }
}
