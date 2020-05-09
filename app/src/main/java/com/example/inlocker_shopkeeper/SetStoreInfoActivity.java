package com.example.inlocker_shopkeeper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SetStoreInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "";
    EditText editTextStoreName, editTextStorePhoneNo, editTextStoreAddr;
    Button saveInfo;
    FirebaseUser user;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_store_info);

        editTextStoreName = findViewById(R.id.storeName_setStoreInfo_editText);
        editTextStorePhoneNo = findViewById(R.id.phoneNo_setStoreInfo_editText);
        editTextStoreAddr = findViewById(R.id.storeAddr_setStoreInfo_editText);

        saveInfo = findViewById(R.id.saveInfo_setStoreInfoBtn);

        saveInfo.setOnClickListener(this);
    }

    private void saveInfoToFirestore(){
        String storeName = editTextStoreName.getText().toString();
        String storePhoneNo = editTextStorePhoneNo.getText().toString();
        String storeAddr = editTextStoreAddr.getText().toString();

        user = fAuth.getCurrentUser();
        if (user == null){
            Log.d(TAG, "user not login yet");
            return;
        }
        String uid = user.getUid();
        DocumentReference documentReference = fStore.collection("storeList").document(uid);

        Map<String, Object> store = new HashMap<>();
        store.put("Name", storeName);
        store.put("PhoneNo", storePhoneNo);
        store.put("Address", storeAddr);

        documentReference.set(store).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: store info updated, uid: " + user.getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure" + e.toString());
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
