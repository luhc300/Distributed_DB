package transaction;

import java.io.Serializable;
import InvalidIndexExceprion;
public class Customers implements ResourceItem, Serializable{
    private String custName;
    private boolean isdeleted=false;
    public Customers(String custName){
        super();
        this.custName=custName;
    }
    public String getCustName(){
        return custName;
    }
    public void setCustName(String custName){
        this.custName=custName;
    }

    @Override
    public String[] getColumnNames(){
        return new String[]{"custname"};
    }

    @Override
    public String[] getColumnValues(){
        return new String[]{custname};
    }

    @Override
    public String getIndex{String indexName} throws InvalidIndexException{
        if(indexName.equals("custName")){
            return custName;
        }
        else{
            throw new InvalidIndexException(indexName);
        }
    }
    @Override
    public Object getKey() {
        return custName;
    }
    @Override
    public boolean isDeleted() {
        return isdeleted;
    }
    @Override
    public void delete() {
        isdeleted = true;
    }
    public Object clone() {
        Customers cust = new Customers(getCustName());
        cust.isdeleted = isdeleted;
        return cust;
    }
}