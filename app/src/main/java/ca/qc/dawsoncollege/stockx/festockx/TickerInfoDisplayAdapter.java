package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TickerInfoDisplayAdapter extends BaseAdapter {
    Context context;
    List<TickerStock> tickers;
    private static LayoutInflater inflater = null;


    @Override
    public Object getItem(int position){
        //return
        return position;
    }

    public TickerInfoDisplayAdapter(Activity showTickerInfoActivity, List<TickerStock> tickers){
        context = showTickerInfoActivity;
        this.tickers = tickers;
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
        return tickers.size();
    }

    public class Holder
    {
        TextView symbolName;
        TextView companyName;
        TextView currentPrice;
        TextView currency;
        TextView stockExchange;
        TextView dayChange;
        TextView changePct;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        View tickerView;
        tickerView = inflater.inflate(R.layout.activity_ticker_info, null);
        holder.symbolName = (TextView) tickerView.findViewById(R.id.symbolDisplay);
        holder.companyName = (TextView) tickerView.findViewById(R.id.nameDisplay);
        holder.currentPrice = (TextView) tickerView.findViewById(R.id.priceDisplay);
        holder.currency = (TextView) tickerView.findViewById(R.id.currencyDisplay);
        holder.stockExchange = (TextView) tickerView.findViewById(R.id.stockExchangeDisplay);
        holder.dayChange = (TextView) tickerView.findViewById(R.id.dayChangeDisplay);
        holder.changePct = (TextView) tickerView.findViewById(R.id.changePercentDisplay);

        TickerStock tickerStats = tickers.get(position);
        holder.symbolName.setText(tickerStats.getTickerName());
        holder.companyName.setText(tickerStats.getCompanyName());
        holder.currentPrice.setText(tickerStats.getPrice());
        holder.currency.setText(tickerStats.getCurrencyType());
        holder.stockExchange.setText(tickerStats.getStockExchange());
        holder.dayChange.setText(tickerStats.getDayChange());
        holder.changePct.setText(tickerStats.getChangePct());
        Log.d("VALUE: ",tickerView + "");
        return tickerView;
    }
}
