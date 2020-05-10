package com.example.inlocker_shopkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Currency;

public class StoreItemAdapter extends FirestoreRecyclerAdapter<StoreItem, StoreItemAdapter.StoreItemHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StoreItemAdapter(@NonNull FirestoreRecyclerOptions<StoreItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StoreItemHolder holder, int position, @NonNull StoreItem model) {
        Currency currency = Currency.getInstance("JPY");
        holder.textViewItemName.setText(model.getName());
        String editedPriceText = currency.getSymbol() + String.valueOf(model.getPrice());
        holder.textViewItemPrice.setText(editedPriceText);
        String editedAmountText = "Amount: " + String.valueOf(model.getAmount());
        holder.textViewItemAmount.setText(editedAmountText);
    }

    @NonNull
    @Override
    public StoreItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_layout, parent, false);
        return new StoreItemHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class StoreItemHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName;
        TextView textViewItemPrice;
        TextView textViewItemAmount;

        public StoreItemHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.itemName_itemList_textView);
            textViewItemAmount = itemView.findViewById(R.id.itemAmount_itemList_textView);
            textViewItemPrice = itemView.findViewById(R.id.itemPrice_itemList_textView);
        }
    }
}
