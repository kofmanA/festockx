package ca.qc.dawsoncollege.stockx.festockx.Portfolio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.qc.dawsoncollege.stockx.festockx.R;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.ItemNoteAdapter;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.NoteItemActivity;

class OwnedStockAdapter extends RecyclerView.Adapter {
    HashMap<String, Integer> ownedStocks;
    Context context;
    private static ItemNoteAdapter.RecyclerViewClickListener itemListener;

    private final LayoutInflater inflater;

    public OwnedStockAdapter(Context context, HashMap<String, Integer> ownedStocks){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.ownedStocks = ownedStocks;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ticker;
        TextView quantity;

        public ItemViewHolder(View itemView){
            super(itemView);
            ticker = (TextView)itemView.findViewById(R.id.ticker);
            quantity = (TextView)itemView.findViewById(R.id.stockQuantity);
            Log.d("ID", "ItemViewHolder: " + ticker.getId());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v,this.getLayoutPosition());
        }
    }


    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }

    public void setRecyclerClick(ItemNoteAdapter.RecyclerViewClickListener RVCL){
        this.itemListener = RVCL;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.recyclerview_owned_stock, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ItemViewHolder ivh = ((ItemViewHolder) viewHolder);
        ownedStocks.keySet();
        List<String> keys = new ArrayList<>();
        for(String s : ownedStocks.keySet()){
            keys.add(s);
        }
        String ticker = keys.get(i);
        int quantity = ownedStocks.get(ticker);
        Log.d("TEST", "onBindViewHolder: " + quantity);
        Log.d("TEST", "onBindViewHolder: " + ticker);
        ivh.quantity.setText(quantity + "");
        ivh.ticker.setText(ticker);
    }

    @Override
    public int getItemCount() {
        return ownedStocks.size();
    }

    public String[] getStock(int position){
      //Return new stock information.
        return new String[0];
    }

}
