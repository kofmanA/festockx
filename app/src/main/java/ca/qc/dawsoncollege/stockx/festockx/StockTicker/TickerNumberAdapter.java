package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

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

import org.json.JSONObject;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.R;

/**This is the adapter that contains the Spinner
 * Also reloads tickers from landscape
 * @author Alex
 */
public class TickerNumberAdapter extends BaseAdapter {
    Context context;
    int numTickers;
    private static LayoutInflater inflater = null;
    String[] data;

    @Override
    public Object getItem(int position){
        //return
        return position;
    }

    public TickerNumberAdapter(Activity stockNumberSelectActivity, int numTickers, String[] data){
        context = stockNumberSelectActivity;
        this.numTickers = numTickers;
        //Pass all data from constructor
        this.data = data;
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
        View rowView = convertView;
        if(rowView == null) {
            rowView = inflater.inflate(R.layout.activity_ticker_input, null);
            holder.et = (EditText) rowView.findViewById(R.id.tickerInput);
            //If data contains elements to reload into the ticker boxes
            if(data != null && data.length != 0){
                if(data.length > position) {
                    holder.et.setText(data[position]);
                    Log.d("TEST", "getView: " + data[position]);
                }
            }
        }
        return rowView;
    }



}
