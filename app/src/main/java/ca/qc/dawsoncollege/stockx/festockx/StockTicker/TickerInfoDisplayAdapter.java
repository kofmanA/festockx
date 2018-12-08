package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class TickerInfoDisplayAdapter extends RecyclerView.Adapter<TickerInfoDisplayAdapter.Holder> {
    Context context;
    List<TickerStock> tickers;
    private final String TAG = "call";


    public TickerInfoDisplayAdapter(Context context, List<TickerStock> tickers){
        this.context = context;
        this.tickers = tickers;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ticker_info,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        TickerStock tickerStats = tickers.get(position);
        Log.d(TAG,tickerStats.getTickerName());
        holder.symbolName.setText(tickerStats.getTickerName());
        holder.companyName.setText(tickerStats.getCompanyName());
        holder.currentPrice.setText(tickerStats.getPrice());
        holder.currency.setText(tickerStats.getCurrencyType());
        holder.stockExchange.setText(tickerStats.getStockExchange());
        holder.dayChange.setText(tickerStats.getDayChange());
        holder.changePct.setText(tickerStats.getChangePct());
    }

    @Override
    public int getItemCount() {
        return tickers.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        TextView symbolName;
        TextView companyName;
        TextView currentPrice;
        TextView currency;
        TextView stockExchange;
        TextView dayChange;
        TextView changePct;

        public Holder(View itemView) {
            super(itemView);
            symbolName = (TextView) itemView.findViewById(R.id.symbolDisplay);
            companyName = (TextView) itemView.findViewById(R.id.nameDisplay);
            currentPrice = (TextView) itemView.findViewById(R.id.priceDisplay);
            currency = (TextView) itemView.findViewById(R.id.currencyDisplay);
            stockExchange = (TextView) itemView.findViewById(R.id.stockExchangeDisplay);
            dayChange = (TextView) itemView.findViewById(R.id.dayChangeDisplay);
            changePct = (TextView) itemView.findViewById(R.id.changePercentDisplay);
        }
    }
}
