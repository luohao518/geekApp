package xyz.geekweb.stock.pojo;

/**
 * @author lhao
 */
public class DataPO {
    String id;
    String name;
    double currentPrice;
    double value;
    double diffValue;

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(currentPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(diffValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPO dataPO = (DataPO) o;

        if (Double.compare(dataPO.currentPrice, currentPrice) != 0) return false;
        if (Double.compare(dataPO.value, value) != 0) return false;
        if (Double.compare(dataPO.diffValue, diffValue) != 0) return false;
        if (id != null ? !id.equals(dataPO.id) : dataPO.id != null) return false;
        return name != null ? name.equals(dataPO.name) : dataPO.name == null;
    }

    @Override
    public String toString() {
        return "DataPO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice=" + currentPrice +
                ", value=" + value +
                ", diffValue=" + diffValue +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(double diffValue) {
        this.diffValue = diffValue;
    }
}
