package transaction;

import java.io.Serializable;


public class Car implements ResourceItem, Serializable {

    public static final String INDEX_LOCATION = "location";

    protected String location;
    protected int price;
    protected int numCars;
    protected int numAvail;

    protected boolean isdeleted = false;

    public Car(String location, int price, int numCars, int numAvail) {
        this.location = location;
        this.price = price;
        this.numCars = numCars;
        this.numAvail = numAvail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumCars() {
        return numCars;
    }

    public void setNumCars(int numCars) {
        this.numCars = numCars;
    }

    public int getNumAvail() {
        return numAvail;
    }

    public void setNumAvail(int numAvail) {
        this.numAvail = numAvail;
    }

    public String[] getColumnNames() {
        // TODO Auto-generated method stub
        return new String[]{"location", "price", "numCars", "numAvail"};
    }

    public String[] getColumnValues() {
        // TODO Auto-generated method stub
        return new String[]{location, "" + price, "" + numCars, "" + numAvail};
    }

    public Object getIndex(String indexName) throws InvalidIndexException {
        // TODO Auto-generated method stub
        if (indexName.equals(INDEX_LOCATION)) {
            return location;
        } else if (indexName.equals("price")) {
            return price;
        } else if (indexName.equals("numCars")) {
            return numCars;
        } else if (indexName.equals("numAvail")) {
            return numAvail;
        } else {
            throw new InvalidIndexException(indexName);
        }
    }

    public Object getKey() {
        return location;
    }

    public boolean isDeleted() {

        return isdeleted;
    }

    public void delete() {

        isdeleted = true;
    }

    public Object clone() {
        Car c = new Car(getLocation(), getPrice(), getNumCars(), getNumAvail());
        c.isdeleted = isdeleted;
        return c;
    }
}
