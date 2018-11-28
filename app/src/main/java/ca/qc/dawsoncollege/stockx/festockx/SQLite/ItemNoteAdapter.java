package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class ItemNoteAdapter extends RecyclerView.Adapter<ItemNoteAdapter.ItemViewHolder> {
    private ItemClickListener mClickListener;


    class ItemViewHolder  extends RecyclerView.ViewHolder implements ViewStub.OnClickListener{
        private final TextView itemNoteView;

        private ItemViewHolder(View itemView){
            super(itemView);
            itemNoteView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }


    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private final LayoutInflater nInflater;
    private List<ItemNote> lNotes;
    private ItemClickListener onItemClickListener;


    public ItemNoteAdapter(Context context){
        nInflater = LayoutInflater.from(context);

    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = nInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (lNotes != null) {
            ItemNote current = lNotes.get(position);
            holder.itemNoteView.setText(current.getNote());
        } else {
            holder.itemNoteView.setText(R.string.noNotes);
        }
    }

    @Override
    public int getItemCount(){
        if (lNotes !=null){
            return lNotes.size();
        }
        return 0;
    }

   public void setNotes(List<ItemNote> notes){
        this.lNotes = notes;
        notifyDataSetChanged();
   }

   public void removeItem(int pos){
        lNotes.remove(pos);
        notifyDataSetChanged();
   }

   public void restoreItem(ItemNote note, int pos){
       lNotes.add(pos,note);
       notifyDataSetChanged();
   }






}
