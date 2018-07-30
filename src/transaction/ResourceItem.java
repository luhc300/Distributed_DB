/*
 * Created on 2005-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package transaction;

import java.io.Serializable;

/**
 * @author RAdmin
 * <p>
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface ResourceItem extends Cloneable, Serializable {
    public String[] getColumnNames();

    public String[] getColumnValues();

    public Object getIndex(String indexName) throws InvalidIndexException;

    public Object getKey();

    public boolean isDeleted();

    public void delete();

    public Object clone();
    public static final String RICars = "cars";
    public static final String RIFlights = "flights";
    public static final String RIRooms = "rooms";
    public static final String RICustomers = "customers";
    public static final String RIReservations = "reservations";
}