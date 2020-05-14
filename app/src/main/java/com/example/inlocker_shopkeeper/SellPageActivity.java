package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SellPageActivity extends AppCompatActivity implements View.OnClickListener {
    Button toSellerView, toFinishedOrder;
    ImageButton toSettings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_page);

        //as no. of buttons is too many this time...
        toSellerView = findViewById(R.id.toSellerView_sellPageBtn);
        toFinishedOrder = findViewById(R.id.toFinishedOrders_sellPageBtn);
        toSettings = findViewById(R.id.settings_sellPageBtn);

        toSellerView.setOnClickListener(this);
        toFinishedOrder.setOnClickListener(this);
        toSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent fromSellPageActivity = null;

        switch(v.getId()){
            case R.id.toSellerView_sellPageBtn:
                fromSellPageActivity = new Intent(this, SellerViewActivity.class);
                break;
            case R.id.toFinishedOrders_sellPageBtn:
                fromSellPageActivity = new Intent(this, FinishedOrderActivity.class);
                break;
            case R.id.settings_sellPageBtn:
                fromSellPageActivity = new Intent(this, SetStoreInfoActivity.class);
                break;
            default:
                //do nothing
                break;
        }
        fromSellPageActivity.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(fromSellPageActivity);
    }
}
