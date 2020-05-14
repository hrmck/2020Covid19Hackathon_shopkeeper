package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewUploadItemActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static String TAG;
    Spinner spinner;
    EditText editTextItemName, editTextItemPrice, editTextItemAmount;
    Button btnItemSave, btnItemClear;
    String chosenCategory, uid;
    ProgressBar progressBar;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user;
    CollectionReference collectionProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_upload_item);

        TAG = getApplicationContext().toString();
        //set up popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;

        getWindow().setLayout((int) (displayWidth * 0.9), (int) (displayHeight * 0.5));

        user = fAuth.getCurrentUser();
        //assume logged in
        uid = user.getUid();
        collectionProduct = fStore.collection("storeList").document(uid).collection("Products");

        editTextItemName = findViewById(R.id.itemName_NewUploadItem_editText);
        editTextItemPrice = findViewById(R.id.itemPrice_NewUploadItem_editText);
        editTextItemAmount = findViewById(R.id.itemAmount_NewUploadItem_editText);
        btnItemSave = findViewById(R.id.itemSave_NewUploadItemBtn);
        btnItemClear = findViewById(R.id.itemClear_NewUploadItemBtn);

        btnItemSave.setOnClickListener(this);
        btnItemClear.setOnClickListener(this);

        spinner = findViewById(R.id.itemCategory_NewUploadItem_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.itemCategory, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    //Spinner functions
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            Toast.makeText(this,
                    "Category chosen: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing to do
    }

    //Button functions
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemSave_NewUploadItemBtn:
                saveItem();
                break;
            case R.id.itemClear_NewUploadItemBtn:
                clearContents();
                break;
        }
    }

    //EditText related
    private void clearContents() {
        editTextItemName.getText().clear();
        editTextItemPrice.getText().clear();
        editTextItemAmount.getText().clear();
        spinner.setSelection(0, true);
    }

    //Firebase related functions
    private void saveItem() {
        String itemName = editTextItemName.getText().toString();
        String itemPriceString = editTextItemPrice.getText().toString();
        String itemAmountString = editTextItemAmount.getText().toString();
        int chosenCategoryPos = spinner.getSelectedItemPosition();

        //validation
        if (itemName.trim().isEmpty() || itemPriceString.trim().isEmpty() || itemAmountString.trim().isEmpty() || chosenCategoryPos == 0) {
            Toast.makeText(this, "Please enter a valid name, price, amount and category", Toast.LENGTH_SHORT).show();
            return;
        }

        chosenCategory = getResources().getStringArray(R.array.itemCategory)[chosenCategoryPos];
        int itemPrice = Integer.parseInt(itemPriceString);
        int itemAmount = Integer.parseInt(itemAmountString);

        if (itemPrice == 0 || itemAmount == 0) {
            Toast.makeText(this, "Amount and price should be larger than 0", Toast.LENGTH_SHORT).show();
            return;
        }
        //end validation

        String itemCategory = chosenCategory;

        //upload item to db, skip unique check due to time constraints
        /*
        if (!isUniqueItem(itemName)){
            Toast.makeText(getApplicationContext(),
                    "Item already added", Toast.LENGTH_SHORT).show();
        }
        else{*/

        uploadToFirestore(itemName, itemPrice, itemAmount, itemCategory);

    }

    private void uploadToFirestore(String itemName, int itemPrice, int itemAmount, String itemCategory) {
        Map<String, Object> item = new HashMap<>();
        item.put("Name", itemName);
        item.put("Price", itemPrice);
        item.put("Amount", itemAmount);
        item.put("Category", itemCategory);

        collectionProduct
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(),
                                "New item added successfully", Toast.LENGTH_SHORT).show();
                        //go back to seller view
                        Intent intent = new Intent(getApplicationContext(), SellerViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Some error occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


    /*
    private boolean isUniqueItem(String itemName) {
        final boolean[] isUnique = {true};
        Query query = collectionProduct.whereEqualTo("Name", itemName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d("Duplicate :", "duplicate found");
                            isUnique[0] = false;
                        }
                        else{
                            Log.d("No duplicate", "no duplicate found");
                        }
                    }
                });

        return isUnique[0];
    }

     */
