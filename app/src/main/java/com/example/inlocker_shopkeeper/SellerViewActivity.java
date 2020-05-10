package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SellerViewActivity extends AppCompatActivity implements View.OnClickListener {
    Button toAddNewItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view);

        toAddNewItem = findViewById(R.id.addNewItem_sellerViewBtn);
        toAddNewItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNewItem_sellerViewBtn) {
            Intent intent = new Intent(this, NewUploadItemActivity.class);
            startActivity(intent);
        }
    }
}
