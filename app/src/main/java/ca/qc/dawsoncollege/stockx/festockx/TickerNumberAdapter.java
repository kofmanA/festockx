package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TickerNumberAdapter extends BaseAdapter {
    Context context;
    int numTickers;
    private static LayoutInflater inflater = null;


    @Override
    public Object getItem(int position){
        //return
        return position;
    }

    public TickerNumberAdapter(Activity stockNumberSelectActivity, int numTickers){
        context = stockNumberSelectActivity;
        this.numTickers = numTickers;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    //Replace with returning a real category count
    public int getCount(){
        return numTickers;
    }

    public class Holder
    {
        EditText et;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_ticker_input, null);
        holder.et = (EditText) rowView.findViewById(R.id.tickerInput);
        return rowView;
    }



}
