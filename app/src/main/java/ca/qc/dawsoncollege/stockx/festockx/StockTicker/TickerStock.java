package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

public class TickerStock {
    private String tickerName;
    private String companyName;
    private String price;
    private String currencyType;
    private String stockExchange;
    private String dayChange;
    private String changePct;

    @Override
    public String toString() {
        return "TickerStock{" +
                "tickerName='" + tickerName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", price='" + price + '\'' +
                ", currencyType='" + currencyType + '\'' +
                ", stockExchange='" + stockExchange + '\'' +
                ", dayChange='" + dayChange + '\'' +
                ", changePct='" + changePct + '\'' +
                '}';
    }

    public TickerStock() {
        this.tickerName = "";
        this.companyName = "";
        this.price = "";
        this.currencyType = "";
        this.stockExchange = "";

        this.dayChange = "";
        this.changePct = "";
    }

    public TickerStock(String tickerName, String companyName, String price, String currencyType, String stockExchange, String dayChange, String changePct) {
        this.tickerName = tickerName;
        this.companyName = companyName;
        this.price = price;
        this.currencyType = currencyType;
        this.stockExchange = stockExchange;
        this.dayChange = dayChange;
        this.changePct = changePct;
    }

    public String getTickerName() {
        return tickerName;
    }

    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public String getDayChange() {
        return dayChange;
    }

    public void setDayChange(String dayChange) {
        this.dayChange = dayChange;
    }

    public String getChangePct() {
        return changePct;
    }

    public void setChangePct(String changePct) {
        this.changePct = changePct;
    }
}
