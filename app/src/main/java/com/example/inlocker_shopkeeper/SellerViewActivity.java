package com.example.inlocker_shopkeeper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerViewActivity extends AppCompatActivity implements View.OnClickListener {
    private String uid;
    String[] categoryFromFirestore;

    Button toAddNewItem, toEditCategory;
    Button[] categoryBtns;
    TextView textViewStoreName;
    ImageView imageViewStoreImage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseUser user;
    private DocumentReference storeDocumentRef;
    private CollectionReference productListRef;
    private StorageReference storeImageRef;

    private StoreItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view);

        toAddNewItem = findViewById(R.id.addNewItem_sellerViewBtn);
        toEditCategory = findViewById(R.id.editCategoriesBtn);
        imageViewStoreImage = findViewById(R.id.storePic_sellerView_imageView);
        textViewStoreName = findViewById(R.id.storeName_sellerView_textView);

        toAddNewItem.setEnabled(false);
        toEditCategory.setEnabled(false);

        categoryBtns = new Button[5];
        categoryBtns[0] = findViewById(R.id.category1_sellerViewBtn);
        categoryBtns[1] = findViewById(R.id.category2_sellerViewBtn);
        categoryBtns[2] = findViewById(R.id.category3_sellerViewBtn);
        categoryBtns[3] = findViewById(R.id.category4_sellerViewBtn);
        categoryBtns[4] = findViewById(R.id.category5_sellerViewBtn);

        user = fAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "User not yet login", Toast.LENGTH_SHORT).show();
            //go back to login page
            Intent toMenu = new Intent(getApplicationContext(), MainActivity.class);
            toMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toMenu);
        }

        uid = user.getUid();
        storeDocumentRef = db.collection("storeList").document(uid);
        productListRef = storeDocumentRef.collection("Products");
        storeImageRef = storageReference.child("storeList/" + uid + "/storeImage.jpg");

        setStoreImage();
        setStoreName();
        setStoreCategoryButton();
        setUpRecyclerView();

        toAddNewItem.setOnClickListener(this);
        toEditCategory.setOnClickListener(this);
        imageViewStoreImage.setOnClickListener(this);
    }

    private void setStoreCategoryButton() {
        setStoreCategories();
        if (!TextUtils.isEmpty(categoryBtns[0].getText().toString())) {
            return;
        }
        for (Button categoryBtn : categoryBtns) {
            categoryBtn.setText("none");
        }
        toEditCategory.setEnabled(true);
        toAddNewItem.setEnabled(true);
    }

    private void setStoreCategories() {
        storeDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList results = (ArrayList) documentSnapshot.get("categories");
                if (results == null) {
                    return;
                }
                categoryFromFirestore = new String[results.size() + 1];
                categoryFromFirestore[0] = "Please choose category";
                for (int ix = 0; ix < categoryBtns.length; ix++) {
                    categoryBtns[ix].setText(results.get(ix).toString());
                    categoryFromFirestore[ix + 1] = results.get(ix).toString();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewItem_sellerViewBtn:
                if (categoryFromFirestore == null) {
                    Toast.makeText(getApplicationContext(), "No categories set. Please set categories first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, NewUploadItemActivity.class);
                intent.putExtra("categories", categoryFromFirestore);
                startActivity(intent);
                break;
            case R.id.storePic_sellerView_imageView:
                chooseStoreImage();
                break;
            case R.id.editCategoriesBtn:
                Intent intent1 = new Intent(this, EditCategoriesActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void chooseStoreImage() {
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
        storeImageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageViewStoreImage);

                        Map<String, Object> storeImageLink = new HashMap<>();
                        storeImageLink.put("storeImageLink", uri.toString());

                        db.collection("storeList").document(uid)
                                .set(storeImageLink, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Store Image Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void setStoreImage() {
        storeImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageViewStoreImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int colorRed = getResources().getColor(R.color.red);
                int colorWhite = getResources().getColor(R.color.white);
                Drawable storeImageDemo = TextDrawable.builder()
                        .beginConfig().fontSize(30).textColor(colorRed).endConfig()
                        .buildRect("Click here to set store image", colorWhite);
                imageViewStoreImage.setImageDrawable(storeImageDemo);
            }
        });
    }

    private void setStoreName() {
        db.collection("storeList").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        textViewStoreName.setText(documentSnapshot.getString("Name"));
                        Toast.makeText(getApplicationContext(),
                                "Items loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "some error occurred", Toast.LENGTH_SHORT).show();
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
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
}
