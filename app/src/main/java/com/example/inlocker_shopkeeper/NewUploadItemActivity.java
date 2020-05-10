package com.example.inlocker_shopkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewUploadItemActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner spinner;
    EditText editTextItemName, editTextItemPrice;
    Button btnItemSave, btnItemClear;
    String chosenCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_upload_item);

        editTextItemName = findViewById(R.id.itemName_NewUploadItem_editText);
        editTextItemPrice = findViewById(R.id.itemPrice_NewUploadItem_editText);
        btnItemSave = findViewById(R.id.itemSave_NewUploadItemBtn);
        btnItemClear = findViewById(R.id.itemClear_NewUploadItemBtn);

        btnItemSave.setOnClickListener(this);
        btnItemClear.setOnClickListener(this);

        spinner = findViewById(R.id.itemCategory_NewUploadItem_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.itemCategory, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenCategory = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Category chosen: " + chosenCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing to do
    }

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

    private void saveItem() {
        String itemName = editTextItemName.getText().toString();
        //assume price is int
        int itemPrice = Integer.parseInt(editTextItemPrice.getText().toString());

        if (itemName.trim().isEmpty() || itemPrice == 0) {
            Toast.makeText(this, "Please enter a valid name and price", Toast.LENGTH_SHORT).show();
            return;
        }
        // should create a new map, let SellerUploadActivity to read it,
        // check conflicts with other maps
    }

    private void clearContents() {
        editTextItemName.getText().clear();
        editTextItemPrice.getText().clear();
        spinner.setSelection(0, true);
    }

}
