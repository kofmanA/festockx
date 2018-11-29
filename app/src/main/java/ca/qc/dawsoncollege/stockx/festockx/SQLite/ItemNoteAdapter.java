package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.NewNoteActivity;
import ca.qc.dawsoncollege.stockx.festockx.NoteActivity;
import ca.qc.dawsoncollege.stockx.festockx.NoteItemActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

import static android.support.v4.content.ContextCompat.startActivity;

public class ItemNoteAdapter extends RecyclerView.Adapter<ItemNoteAdapter.ItemViewHolder> {


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView itemNoteView;

        private ItemViewHolder(View itemView){
            super(itemView);
            itemNoteView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    private final LayoutInflater nInflater;
    private static RecyclerViewClickListener itemListener;
    private List<ItemNote> lNotes;

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }

    public ItemNoteAdapter(Context context){
        nInflater = LayoutInflater.from(context);

    }

    public void setRecyclerClick(RecyclerViewClickListener RVCL){
        this.itemListener = RVCL;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = nInflater.inflate(R.layout.recyclerview_item, parent, false);
        View image = itemView.findViewById(R.id.imageNote);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteItemActivity.class);
                TextView TV = itemView.findViewById(R.id.textView);
                intent.putExtra("note",TV.getTag().toString());
                //The explanation of why I use the tag and not the getText() is in the method below
                v.getContext().startActivity(intent);
            }
        });
        return new ItemViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (lNotes != null) {
            ItemNote current = lNotes.get(position);
            String note = current.getNote();
            holder.itemNoteView.setTag(note);
            //I'm kind of cheating here. Since I only have access to the textview in my onCreateViewHolder
            //and that that textview is already being cropped here, I need a way to save the full note.
            //So I use the tag attribute so secretely hide the non-cropped message.
            if(note.length() >40){
                note = note.substring(0,40)+"...";
            }

            holder.itemNoteView.setText(note);
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
