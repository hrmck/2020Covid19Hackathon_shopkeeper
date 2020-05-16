package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    EditText[] category;
    Button saveInfoBtn;
    ProgressBar progressBar;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DocumentReference storeDocuememtRef;
    private FirebaseUser user;

    String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        category = new EditText[5];
        category[0] = findViewById(R.id.category1_editText);
        category[1] = findViewById(R.id.category2_editText);
        category[2] = findViewById(R.id.category3_editText);
        category[3] = findViewById(R.id.category4_editText);
        category[4] = findViewById(R.id.category5_editText);

        saveInfoBtn = findViewById(R.id.saveCategoryBtn);
        saveInfoBtn.setOnClickListener(this);

        progressBar = findViewById(R.id.editCategory_progressBar);

        user = fAuth.getCurrentUser();
        uid = user.getUid();
        storeDocuememtRef = fStore.collection("storeList").document(uid);

        setEditText();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveCategoryBtn) {
            uploadCategoriesToFirestore();
        }
    }

    private void uploadCategoriesToFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        String categoryString[] = new String[category.length];

        WriteBatch batch = fStore.batch();

        for (int ix = 0; ix < category.length; ix++) {
            categoryString[ix] = category[ix].getText().toString();
        }

        List<String> categories = Arrays.asList(categoryString);
        Map<String, Object> storeCategoryEntry = new HashMap<>();
        storeCategoryEntry.put("categories", categories);

        storeDocuememtRef.set(storeCategoryEntry, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),
                                "Categories saved successfully\n Going back to store view",
                                Toast.LENGTH_SHORT).show();
                        Intent toSellerView = new Intent(getApplicationContext(), SellerViewActivity.class);
                        startActivity(toSellerView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        "Categories save failed: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEditText() {
        progressBar.setVisibility(View.VISIBLE);
        storeHasSavedCategories();
        if (!TextUtils.isEmpty(category[0].getText().toString())) {
            return;
        }
        for (EditText text : category) {
            text.setText("Set Category");
        }
    }

    private void storeHasSavedCategories() {
        storeDocuememtRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList results = (ArrayList) documentSnapshot.get("categories");
                if (results == null) {
                    return;
                }
                for (int ix = 0; ix < category.length; ix++) {
                    category[ix].setText(results.get(ix).toString());
                }
            }
        });
    }
}
