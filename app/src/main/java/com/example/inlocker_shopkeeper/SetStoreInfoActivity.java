package com.example.inlocker_shopkeeper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SetStoreInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SetStoreInfoAcitivity >> ";
    EditText editTextStoreName, editTextStorePhoneNo, editTextStoreAddr;
    Button saveInfo;
    ProgressBar progressBar;
    ImageView trademarkImageView;

    FirebaseUser user;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference trademarkRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_store_info);

        progressBar = findViewById(R.id.setStoreInfo_progressBar);
        editTextStoreName = findViewById(R.id.storeName_setStoreInfo_editText);
        editTextStorePhoneNo = findViewById(R.id.phoneNo_setStoreInfo_editText);
        editTextStoreAddr = findViewById(R.id.storeAddr_setStoreInfo_editText);
        saveInfo = findViewById(R.id.saveInfo_setStoreInfoBtn);
        trademarkImageView = findViewById(R.id.trademark_setStoreInfo_ImageView);

        //get shopkeeper account details
        user = fAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "User not yet login", Toast.LENGTH_SHORT).show();
            //go back to login page
            Intent toMenu = new Intent(getApplicationContext(), MainActivity.class);
            toMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toMenu);
        }

        //initialize for connection to Firestore
        String uid = user.getUid();
        documentReference = fStore.collection("storeList").document(uid);
        trademarkRef = storageReference.child("buyerList/" + uid + "/profile.jpg");

        //load store info if exist
        retrieveStoreInfoIfExist();
        retrieveDataFromFirebase();

        saveInfo.setOnClickListener(this);

        //trademark uploading
        trademarkImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveInfo_setStoreInfoBtn:
                uploadInfoToFirestore();
                break;
            case R.id.trademark_setStoreInfo_ImageView:
                chooseTrademarkImage();
                break;
        }
    }

    private void retrieveStoreInfoIfExist() {
        retrieveStoreTrademark();
        retrieveDataFromFirebase();
    }

    private void retrieveStoreTrademark() {
        progressBar.setVisibility(View.VISIBLE);
        trademarkRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(trademarkImageView);
            }
        });
    }

    private void retrieveDataFromFirebase() {
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

    private void uploadInfoToFirestore() {
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

    //open gallery, choose trademark
    private void chooseTrademarkImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        trademarkRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                trademarkRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(trademarkImageView);
                        Toast.makeText(getApplicationContext(), "Logo Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
