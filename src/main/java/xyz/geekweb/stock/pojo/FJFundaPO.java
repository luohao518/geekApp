package xyz.geekweb.stock.pojo;

public class FJFundaPO {
    String fundaId;
    String fundaName;
    double fundaCurrentPrice;
    double fundaValue;
    double diffValue;

    @Override
    public String toString() {
        return "FJFundaPO{" +
                "fundaId='" + fundaId + '\'' +
                ", fundaName='" + fundaName + '\'' +
                ", fundaCurrentPrice=" + fundaCurrentPrice +
                ", fundaValue=" + fundaValue +
                ", diffValue=" + diffValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FJFundaPO fjFundaPO = (FJFundaPO) o;

        if (Double.compare(fjFundaPO.fundaCurrentPrice, fundaCurrentPrice) != 0) return false;
        if (Double.compare(fjFundaPO.fundaValue, fundaValue) != 0) return false;
        if (Double.compare(fjFundaPO.diffValue, diffValue) != 0) return false;
        if (fundaId != null ? !fundaId.equals(fjFundaPO.fundaId) : fjFundaPO.fundaId != null) return false;
        return fundaName != null ? fundaName.equals(fjFundaPO.fundaName) : fjFundaPO.fundaName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = fundaId != null ? fundaId.hashCode() : 0;
        result = 31 * result + (fundaName != null ? fundaName.hashCode() : 0);
        temp = Double.doubleToLongBits(fundaCurrentPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fundaValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(diffValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String getFundaId() {
        return fundaId;
    }

    public void setFundaId(String fundaId) {
        this.fundaId = fundaId;
    }

    public String getFundaName() {
        return fundaName;
    }

    public void setFundaName(String fundaName) {
        this.fundaName = fundaName;
    }

    public double getFundaCurrentPrice() {
        return fundaCurrentPrice;
    }

    public void setFundaCurrentPrice(double fundaCurrentPrice) {
        this.fundaCurrentPrice = fundaCurrentPrice;
    }

    public double getFundaValue() {
        return fundaValue;
    }

    public void setFundaValue(double fundaValue) {
        this.fundaValue = fundaValue;
    }

    public double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(double diffValue) {
        this.diffValue = diffValue;
    }
}
