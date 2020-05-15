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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class EditCategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    EditText[] category;
    Button saveInfoBtn;
    ProgressBar progressBar;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference storeCategoryRef;
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
        storeCategoryRef = fStore.collection("storeList").document(uid).collection("Category");

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
        String categoryString[] = new String[5];
        int ix = 0;
        WriteBatch batch = fStore.batch();
        for (String fromEditText : categoryString) {
            String title = "CategoryName" + ix;
            fromEditText = category[ix].getText().toString();

            Map<String, Object> storeCategoryEntry = new HashMap<>();
            storeCategoryEntry.put(title, fromEditText);
            batch.set(storeCategoryRef.document(title), storeCategoryEntry);
            ix++;
        }
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        storeCategoryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int ix = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        category[ix].setText(document.getString("CategoryName" + ix));
                        ix++;
                    }

                }
            }
        });
    }

}
