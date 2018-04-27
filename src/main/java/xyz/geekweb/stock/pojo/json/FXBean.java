package xyz.geekweb.stock.pojo.json;

/**
 * @author lhao
 * @date 2018/4/27
 */
public class FXBean {

    private String symbol;
    private double bid;
    private double ask;
    private double price;
    private long timestamp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = symbol != null ? symbol.hashCode() : 0;
        temp = Double.doubleToLongBits(bid);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ask);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FXBean fxBean = (FXBean) o;

        if (Double.compare(fxBean.bid, bid) != 0) return false;
        if (Double.compare(fxBean.ask, ask) != 0) return false;
        if (Double.compare(fxBean.price, price) != 0) return false;
        if (timestamp != fxBean.timestamp) return false;
        return symbol != null ? symbol.equals(fxBean.symbol) : fxBean.symbol == null;
    }

    @Override
    public String toString() {
        return "FXBean{" +
                "symbol='" + symbol + '\'' +
                ", bid=" + bid +
                ", ask=" + ask +
                ", price=" + price +
                ", timestamp=" + timestamp +
                '}';
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;

    }

}