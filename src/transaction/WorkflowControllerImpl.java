package transaction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

/**
 * Workflow Controller for the Distributed Travel Reservation System.
 * <p>
 * Description: toy implementation of the WC.  In the real
 * implementation, the WC should forward calls to either RM or TM,
 * instead of doing the things itself.
 */

public class WorkflowControllerImpl
        extends java.rmi.server.UnicastRemoteObject
        implements WorkflowController {

    protected int flightcounter, flightprice, carscounter, carsprice, roomscounter, roomsprice;
    protected int xidCounter;

    protected int flightType=1;
    protected int hotelType=2;
    protected int carType=3;

    protected ResourceManager rmFlights = null;
    protected ResourceManager rmRooms = null;
    protected ResourceManager rmCars = null;
    protected ResourceManager rmCustomers = null;
    protected TransactionManager tm = null;

    public static void main(String args[]) {
        System.setSecurityManager(new RMISecurityManager());
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/ddb.conf"));
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        String rmiPort = prop.getProperty("wc.port");

        try {
            LocateRegistry.createRegistry(Integer.parseInt(rmiPort));
            if (rmiPort == null) {
                rmiPort = "";
            } else if (!rmiPort.equals("")) {
                rmiPort = "//:" + rmiPort + "/";
            }
            WorkflowControllerImpl obj = new WorkflowControllerImpl();
            Naming.rebind(rmiPort + WorkflowController.RMIName, obj);
            System.out.println("WC bound");
        } catch (Exception e) {
            System.err.println("WC not bound:" + e);
            System.exit(1);
        }
    }


    public WorkflowControllerImpl() throws RemoteException {
        flightcounter = 0;
        flightprice = 0;
        carscounter = 0;
        carsprice = 0;
        roomscounter = 0;
        roomsprice = 0;
        flightprice = 0;

        xidCounter = 1;

        while (!reconnect()) {
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){

            }
        }
        new Thread()
        {
            public void run() {
                while (true) {
                    try {
                        if (tm != null)
                            tm.ping();
                        if (rmCars != null)
                            rmCars.ping();
                        if (rmCustomers != null)
                            rmCustomers.ping();
                        if (rmFlights != null)
                            rmFlights.ping();
                        if (rmRooms != null)
                            rmRooms.ping();
                    }
                    catch (Exception e) {
                        try {
                            reconnect();
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                        }
                        System.out.println("reconnect tm!");
                    }
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {
                    }
                }
            }
        }.start();
    }


    // TRANSACTION INTERFACE
    public int start()
            throws RemoteException {
        return (xidCounter++);
    }

    //1 success,-1 commitException
    public int commit(int xid)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        System.out.println("Committing");
        int result=0;
        try{
            if(tm.commit(xid)){
                result=1;
            }
        }catch(TransactionCommitException e){
                result=-1;
        }
        return result;
    }
    //1 success, -1 abortException
    public int abort(int xid)
            throws RemoteException, InvalidTransactionException, TransactionAbortedException {
        System.out.println("Abortting");
        int result=0;
        try{
            if(tm.abort(xid)){
                result=1;
            }
        }catch(TransactionAbortedException e){
                result=-1;
        }
        return result;
    }

    public boolean addFlight(int xid, String flightNum, int numSeats, int price)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        boolean flag=true;
        try{
            if(xid<0||flightNum==null||numSeats<=0||price<0)
                return false;
            ResourceItem item=rmFlights.query(xid,WorkflowController.FlightTableName,flightNum);
            int newPrice=0;
            if(item!=null){
                int newSeatNum=0;
                int newAvailNum=0;
                newSeatNum=Integer.parseInt(item.getIndex("seatNum").toString())+numSeats;
                newAvailNum=Integer.parseInt(item.getIndex("availNum").toString())+numSeats;
                newPrice=price<0?Integer.parseInt(item.getIndex("price").toString()):price;
                ResourceItem newItem=new Flight(flightNum,newPrice,newSeatNum,newAvailNum);
                if(!rmFlights.update(xid,WorkflowController.FlightTableName,item.getKey(),newItem)){
                    System.out.println("Xid"+xid+"Update on flight flightNum:"+flightNum+" failed!");
                    return false;
                }
            }else{
                newPrice=price<0?0:price;
                ResourceItem newItem=new Flight(flightNum, newPrice, numSeats,numSeats);
                if(!rmFlights.insert(xid,WorkflowController.FlightTableName,newItem)){
                    System.out.println("Xid"+xid+"Addition of flight  flightNum:"+flightNum+" failed!");
                    return false;
                }
            }
        }catch(DeadlockException e){
            e.printStackTrace();
        }catch(RemoteException e){
            flag=false;
            try{
                tm.abort(xid);
            }catch(TransactionAbortedException e1) {
                e1.printStackTrace();
            }
        }catch(NumberFormatException e){
                e.printStackTrace();
        }catch(InvalidIndexException e){
                e.printStackTrace();
        }
        if(!flag){
            System.out.println("Xid"=xid+"Addition of  flight flightNum:"+ flightNum+" failed!");
            return false;
        }
        return true;
    }

    public boolean deleteFlight(int xid, String flightNum)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        int seatNum=0;
        int availNum=0;
        boolean flag=true;
        try{
            ResourceItem item=rmFlights.query(xid,WorkflowController.ReservationTableName, flightNum);
            if(item!=null){
                seatNum=Integer.parseInt(item.getIndex("seatNum").toString());
                availNum=Integer.parseInt(item.getIndex("availNum").toString());
                if(seatNum==availNum){
                    return rmFlights.delete(xid,WorkflowController.ReservationTableName,flightNum);
                }
            }
            else return true;
        }catch (RemoteException e){
            flag=false;
            tm.abort(xid);
        }catch(DeadlockException e){
            e.printStackTrace();
        }catch(NumberFormatException e){
            e.printStackTrace();
        }catch(InvalidIndexException e){
            e.printStackTrace();
        }
        if(!flag){
            System.out.println("Xid"+xid+" Deletion of flight:"+flightNum+" failed!");
        }
        return false;
    }

    public boolean addRooms(int xid, String location, int numRooms, int price)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        boolean flag = true;
        try{
            if (xid < 0 || location == null || numRooms <= 0|| price<0) {
                return false;
            }
            ResourceItem item = rmRooms.query(xid, WorkflowController.RoomsTableName, location);
            int newPrice=0;
            if (item != null) {
                int newRoomNum = 0;
                int newAvailNum = 0;
                newRoomNum = Integer.parseInt(item.getIndex("roomNum").toString()) + numRooms;
                newAvailNum = Integer.parseInt(item.getIndex("availNum").toString()) + numRooms;
                newPrice = price<0? Integer.parseInt(item.getIndex("price").toString()):price;
                ResourceItem newItem = new Hotel(location, newPrice, newRoomNum, newAvailNum);
                if (!rmRooms.update(xid, WorkflowController.RoomsTableName, item.getKey(), newItem)) {
                    System.out.println("Xid" + xid + " Updatation of Hotel : location:" + location + " failed!");
                    return false;
                }
            } else {
                newPrice = price<0?0:price;
                ResourceItem newItem = new Hotel(location, newPrice, numRooms, numRooms);
                if (!rmRooms.insert(xid, WorkflowController.RoomsTableName, newItem)) {
                    System.out.println("Xid" + xid + " Addition of  hotel : location:" + location + " failed!");
                    return false;
                }
            }
        }
        catch (DeadlockException e) {
            e.printStackTrace();
        }catch (RemoteException e) {
            flag = false;
            tm.abort(xid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        if (!flag) {
            System.out.println("Xid" + xid + " Addition of  hotel location:" + location + " failed!");
            return false;
        }
        return true;
    }

    public boolean deleteRooms(int xid, String location, int numRooms)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException, ResourceManagerUnaccessibleException{
        try {
            ResourceItem ri = rmRooms.query(xid, WorkflowController.RoomsTableName, location);
            if (ri!=null){
                if(numRooms>0 && Integer.parseInt(ri.getIndex("availNum"))>=numRooms) {
                    ResourceItem newR = new ResourceItemImpl(ResourceItem.RIRooms, "location",
                            new String[]{"location", "price", "roomNum", "availNum"},
                            new String[]{location, ri.getIndex("price"),
                                    Integer.toString((Integer.parseInt(ri.getIndex("roomNum")) - numRooms)),
                                    Integer.toString((Integer.parseInt(ri.getIndex("availNum")) - numRooms))});
                    if (!rmRooms.update(xid, WorkflowController.RoomsTableName, location, newR)) {
                        return false;
                    }
                }
                else return false;
            }else{
                return false;
            }
        } catch (RemoteException e) {
            throw new ResourceManagerUnaccessibleException();
        }
        return true;
    }

    public boolean addCars(int xid, String location, int numCars, int price)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        boolean flag = true;
        try{
            if (xid < 0 || location == null || numCars < 0 || price<0) {
                return false;
            }
            int newCarNum=0;
            int newAvailNum = 0;
            int newPrice=0;
            ResourceItem item = rmCars.query(xid, WorkflowController.CarsTableName, location);
            if(item != null){
                try {
                    newCarNum = Integer.parseInt(item.getIndex("carNum").toString()) + numCars;
                    newAvailNum=Integer.parseInt(item.getIndex("availNum").toString()) + numCars;
                    newPrice = price<0?Integer.parseInt(item.getIndex("price").toString()):price;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (InvalidIndexException e) {
                    e.printStackTrace();
                }
                ResourceItem newItem = new Car(location,newPrice,newCarNum,newAvailNum);
                rmCars.update(xid, WorkflowController.CarsTableName, item.getKey(), newItem);
            }
            else{
                newPrice = price<0?0:price;
                ResourceItem newItem = new Car(location,newPrice,numCars,numCars);
                if (!rmCars.insert(xid, WorkflowController.CarsTableName, newItem)) {
                    System.out.println("Xid" + xid + " Addition of Cars location:" + location + " failed");
                    return false;
                }
            }
        } catch (DeadlockException e) {
            e.printStackTrace();
        }catch (RemoteException e) {
            flag = false;
            tm.abort(xid);
        }
        if (!flag) {
            System.out.println("Xid" + xid + " Addition of Cars location:" + location + " failed");
            return false;
        }
        return true;
    }

    public boolean deleteCars(int xid, String location, int numCars)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        boolean flag = true;
        try {
            int curCarNum = 0;
            int curAvailNum = 0;
            int price=0;
            ResourceItem carItem = rmCars.query(xid, WorkflowController.CarsTableName, location);
            if (carItem != null) {
                curCarNum=Integer.parseInt(carItem.getIndex("carNum").toString());
                curAvailNum= Integer.parseInt(carItem.getIndex("availNum").toString());
                price = Integer.parseInt(carItem.getIndex("price").toString());
                if (curAvailNum >= numCars) {
                    ResourceItem newItem  = new Car(location,price,curCarNum-numCars,curAvailNum-numCars);
                    rmCars.update(xid, WorkflowController.CarsTableName, carItem.getKey(), newItem);
                    return true;
                }
            }
        } catch (RemoteException e) {
            flag = false;
            tm.abort(xid);
        } catch (DeadlockException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        if (!flag) {
            System.out.println("Xid" + xid + " Deletion of Car location:" + location + " failed!");
        }
        return false;
    }

    public boolean newCustomer(int xid, String custName)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        if (custName == null) {
            return false;
        }
        boolean flag = true;
        ResourceItem newItem = new Customers(custName);
        try {
            if (!rmCustomers.insert(xid, WorkflowController.CustomersTableName, newItem)) {
                System.out.println("Xid" + xid + " custName:" + custName + " Adding customer failed!");
                return false;
            }
        } catch (DeadlockException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            flag = false;
            tm.abort(xid);
        }
        if (!flag) {
            System.out.println("Xid" + xid + " Addition of customer custName:" + custName + " failed!");
            return false;
        }
        return true;
    }

    public boolean deleteCustomer(int xid, String custName)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        ResourceManagerUnaccessibleException {
            try {
                ResourceItem ri = rmCustomers.query(xid, WorkflowController.CustomersTableName, custName);
                if (ri != null) {
                    rmReservations.delete(xid, WorkflowController.ReservationTableName, "resvKey", custName);
                    if (!rmCustomers.delete(xid, WorkflowController.CustomersTableName, custName)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (RemoteException e) {
                System.err.println("wc delete customer: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            //
            return true;
    }

    // QUERY INTERFACE
    public int queryFlight(int xid, String flightNum)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            flightcounter=0;
            try {
                ResourceItem ri = rmFlights.query(xid, WorkflowController.FlightTableName, flightNum);
                if (ri != null) {
                    flightcounter = Integer.parseInt(ri.getIndex("availNum"));
                } else {
                    flightcounter = -1;
                }
            } catch (RemoteException e) {
                flightcounter = -1;
                System.err.println("wc query flight: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return flightcounter;
    }

    public int queryFlightPrice(int xid, String flightNum)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            flightprice=0;
            try {
                ResourceItem ri = rmFlights.query(xid, WorkflowController.FlightTableName, flightNum);
                if (ri != null) {
                    flightprice = Integer.parseInt(ri.getIndex("price"));
                } else {
                    flightprice = -1;
                }
            } catch (RemoteException e) {
                flightprice = -1;
                System.err.println("wc query flight price: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return flightprice;
    }

    public int queryRooms(int xid, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            try {
                ResourceItem ri = rmRooms.query(xid, WorkflowController.RoomsTableName, location);
                if (ri != null) {
                    roomscounter = Integer.parseInt(ri.getIndex("availNum"));
                } else {
                    roomscounter = -1;
                }
            } catch (RemoteException e) {
                roomscounter = -1;
                System.err.println("wc query room: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return roomscounter;
    }

    public int queryRoomsPrice(int xid, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            try {
                ResourceItem ri = rmRooms.query(xid, WorkflowController.RoomsTableName, location);
                if (ri != null) {
                    roomsprice = Integer.parseInt(ri.getIndex("price"));
                } else {
                    roomsprice = -1;
                }
            } catch (RemoteException e) {
                roomsprice = -1;
                System.err.println("wc query room price: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return roomsprice;
    }

    public int queryCars(int xid, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            carscounter=0;
            try {
                ResourceItem ri = rmCars.query(xid, WorkflowController.CarsTableName, location);
                if (ri != null) {
                    carscounter = Integer.parseInt(ri.getIndex("availNum"));
                } else {
                    carscounter = -1;
                }
            } catch (RemoteException e) {
                carscounter = -1;
                System.err.println("wc query car: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return carscounter;
    }

    public int queryCarsPrice(int xid, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            carsprice=0;
            try {
                ResourceItem ri = rmCars.query(xid, WorkflowController.CarsTableName, location);
                if (ri != null) {
                    carsprice = Integer.parseInt(ri.getIndex("price"));
                } else {
                    carsprice = -1;
                }
            } catch (RemoteException e) {
                carsprice = -1;
                System.err.println("wc query car price: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return carsprice;
    }

    public int queryCustomerBill(int xid, String custName)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            int totalBill = 0;
            Collection c = rmReservations.query(xid, WorkflowController.ReservationTableName,"custName",custName);
            for (Iterator iter = c.iterator(); iter.hasNext();)
            {
                ResourceItem r = (ResourceItem)iter.next();
                if(Integer.parseInt(r.getIndex("resvType"))==1){
                    totalBill += queryFlightPrice(xid,r.getIndex("resvKey"));
                }else if (Integer.parseInt(r.getIndex("resvType"))==2){
                    totalBill += queryRoomsPrice(xid,r.getIndex("resvKey"));
                }else if(Integer.parseInt(r.getIndex("resvType"))==3){
                    totalBill += queryCarsPrice(xid,r.getIndex("resvKey"));
                }
            }
            return totalBill;
    }


    // RESERVATION INTERFACE
    public boolean reserveFlight(int xid, String custName, String flightNum)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            int seatNum;
            int availNum;
            boolean flag = true;
            try {
                ResourceItem flightItem = rmFlights.query(xid,  WorkflowController.FlightTableName, flightNum);
                ResourceItem custItem = rmCustomers.query(xid, WorkflowController.CustomersTableName, custName);
                ResourceItem reserItem = rmReservations.query(xid, WorkflowController.ReservationTableName, new ReservationKey(custName, flightType, flightNum));
                if(flightItem == null)
                    System.out.println("Flight record does not exist!");
                if(custItem == null)
                    System.out.println("Customer record does not exist!");
                if(reserItem != null)
                    System.out.println("Reservation already exists!");
                if (flightItem != null && custItem != null && reserItem == null) {
                    seatNum = Integer.parseInt(flightItem.getIndex("seatNum").toString());
                    availNum = Integer.parseInt(flightItem.getIndex("availNum").toString());
                    if (availNum > 0) {
                        ResourceItem newReseriveItem = new Reservation(custName, flightType, flightNum);
                        ResourceItem newFlightItem = new Flight(flightNum, Integer.parseInt(flightItem.getIndex("price").toString()), seatNum, availNum - 1);
                        if (rmReservations.insert(xid, WorkflowController.ReservationTableName, newReseriveItem) && rmFlights.update(xid, WorkflowController.FlightTableName, flightNum, newFlightItem)) {
                            System.out.println("Xid" + xid + ":custName-" + custName + " reservation success");
                        }
                    }
                }
            } catch (DeadlockException e) {
                flag=false;
                e.printStackTrace();
            } catch (InvalidIndexException e) {
                flag=false;
                e.printStackTrace();
            } catch (RemoteException e) {
                flag = false;
                tm.abort(xid);
            }
            if (!flag) {
                System.out.println("Xid" + xid + "reserveFlight custName:" + custName + " reservation failed");
            }
            return flag;
    }

    public boolean reserveCar(int xid, String custName, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            boolean flag = true;
            int carNum;
            int availNum;
            try {
                ResourceItem carItem = rmCars.query(xid, WorkflowController.CarsTableName, location);
                ResourceItem custItem = rmCustomers.query(xid, WorkflowController.CustomersTableName, custName);
                ResourceItem reserItem = rmReservations.query(xid, WorkflowController.ReservationTableName, new ReservationKey(custName, carType, location));
                if (carItem != null && reserItem == null && custItem!=null) {
                    carNum = Integer.parseInt(carItem.getIndex("carNum").toString());
                    availNum = Integer.parseInt(carItem.getIndex("availNum").toString());
                    if (availNum > 0) {
                        ResourceItem newReserveItem = new Reservation(custName, carType, location);
                        ResourceItem newCarItem = new Car(location, Integer.parseInt(carItem.getIndex("price").toString()), carNum, availNum - 1);
                        if (rmReservations.insert(xid, WorkflowController.ReservationTableName, newReserveItem) && rmCars.update(xid, WorkflowController.CarsTableName, location, newCarItem)) {
                            System.out.println("Xid" + xid + " custName:" + custName + " reservation success!");
                            return true;
                        }
                    }
                }
            } catch (DeadlockException e) {
                e.printStackTrace();
            } catch (InvalidIndexException e) {
                e.printStackTrace();
            }catch (RemoteException e) {
                flag = false;
                tm.abort(xid);
            }
            if (!flag) {
                System.out.println("Xid" + xid + "reserveCar :custName-" + custName + " reservation failed!");
            }
            return false;
    }

    public boolean reserveRoom(int xid, String custName, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException {
            try {
                ResourceItem ri = rmRooms.query(xid, WorkflowController.RoomsTableName, location);
                ResourceItem ri1 = rmCustomers.query(xid, WorkflowController.CustomersTableName, custName);
                if (ri != null && ri1!= null) {
                    int availNum = Integer.parseInt(ri.getIndex("availNum"));
                    if(availNum>=1){
                        ResourceItem newRoom = new ResourceItemImpl(ResourceItem.RIRooms, "location", new String[]{"location", "price", "roomNum", "availNum"}, new String[]{location, ri.getIndex("price"), ri.getIndex("roomNum"), Integer.toString(availNum-1))});
                        if(!rmRooms.update(xid, WorkflowController.RoomsTableName, location, newRoom)) {
                            return false;
                        }
                        ResourceItem newroomRes = new ResourceItemImpl(ResourceItem.RIReservations, new String[]{"custName", "resvType", "resvKey"}, new String[]{custName, 2+"", location});
                        if(!rmReservations.insert(xid, WorkflowController.ReservationTableName, newroomRes)){
                            return false;
                        }
                    }
                    else{
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (RemoteException e) {
                System.err.println("wc resv room: Remote Exception " + e);
                throw new ResourceManagerUnaccessibleException();
            }
            return true;
    }

    // TECHNICAL/TESTING INTERFACE
    public boolean reconnect()
            throws RemoteException {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/ddb.conf"));
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
        String rmiPort[] = new String[5];
        rmiPort[0] = prop.getProperty("rm.RMFlights.port");
        rmiPort[1] = prop.getProperty("rm.RMRooms.port");
        rmiPort[2] = prop.getProperty("rm.RMCars.port");
        rmiPort[3] = prop.getProperty("rm.RMCustomers.port");
        rmiPort[4] = prop.getProperty("tm.port");
        for (int i = 0; i < 5; i++) {
            if (rmiPort[i] == null) {
                rmiPort[i] = "";
            } else if (!rmiPort[i].equals("")) {
                rmiPort[i] = "//:" + rmiPort[i] + "/";
            }
        }
        try {
            rmFlights = (ResourceManager) Naming.lookup(rmiPort[0] + ResourceManager.RMINameFlights);
            System.out.println("WC bound to RMFlights");
            rmRooms = (ResourceManager) Naming.lookup(rmiPort[1] + ResourceManager.RMINameRooms);
            System.out.println("WC bound to RMRooms");
            rmCars = (ResourceManager) Naming.lookup(rmiPort[2] + ResourceManager.RMINameCars);
            System.out.println("WC bound to RMCars");
            rmCustomers = (ResourceManager) Naming.lookup(rmiPort[3] + ResourceManager.RMINameCustomers);
            System.out.println("WC bound to RMCustomers");
            tm = (TransactionManager) Naming.lookup(rmiPort[4] + TransactionManager.RMIName);
            System.out.println("WC bound to TM");
        } catch (Exception e) {
            System.err.println("WC cannot bind to some component:" + e);
            return false;
        }
        try {
            if (rmFlights.reconnect() && rmRooms.reconnect() && rmCars.reconnect() && rmCustomers.reconnect()) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Some RM cannot reconnect:" + e);
            return false;
        }
        return false;
    }

    public boolean dieNow(String who)
            throws RemoteException {
        if (who.equals(TransactionManager.RMIName) || who.equals("ALL")) {
            try {
                tm.dieNow();
            } catch (RemoteException e) {
            }
        }
        if (who.equals(ResourceManager.RMINameFlights) || who.equals("ALL")) {
            try {
                rmFlights.dieNow();
            } catch (RemoteException e) {
            }
        }
        if (who.equals(ResourceManager.RMINameRooms) || who.equals("ALL")) {
            try {
                rmRooms.dieNow();
            } catch (RemoteException e) {
            }
        }
        if (who.equals(ResourceManager.RMINameCars) || who.equals("ALL")) {
            try {
                rmCars.dieNow();
            } catch (RemoteException e) {
            }
        }
        if (who.equals(ResourceManager.RMINameCustomers) || who.equals("ALL")) {
            try {
                rmCustomers.dieNow();
            } catch (RemoteException e) {
            }
        }
        if (who.equals(WorkflowController.RMIName) || who.equals("ALL")) {
            System.exit(1);
        }
        return true;
    }
	public boolean dieRM(String who, String when)
	throws RemoteException {
    	if (who.equals(ResourceManager.RMINameFlights) || who.equals("ALL")) {
    		rmFlights.setDieTime(when);
    	}
    	if (who.equals(ResourceManager.RMINameCars) || who.equals("ALL")) {
    		rmCars.setDieTime(when);
    	}
    	if (who.equals(ResourceManager.RMINameCustomers) || who.equals("ALL")) {
    		rmCustomers.setDieTime(when);
    	}
    	if (who.equals(ResourceManager.RMINameRooms) || who.equals("ALL")) {
    		rmRooms.setDieTime(when);
    	}
    	return true;
    }
    public boolean dieRMAfterEnlist(String who)
        throws RemoteException {
            if (who.equals(ResourceManager.RMINameFlights) || who.equals("ALL")) {
                rmFlights.setDieTime("AfterEnlist");
            }
            if (who.equals(ResourceManager.RMINameCars) || who.equals("ALL")) {
                rmCars.setDieTime("AfterEnlist");
            }
            if (who.equals(ResourceManager.RMINameCustomers) || who.equals("ALL")) {
                rmCustomers.setDieTime("AfterEnlist");
            }
            if (who.equals(ResourceManager.RMINameRooms) || who.equals("ALL")) {
                rmRooms.setDieTime("AfterEnlist");
            }
            return true;
    }

    public boolean dieRMBeforePrepare(String who)
        throws RemoteException {
            if(who.equals(ResourceManager.RMINameCars)||who.equals("ALL")) {
                rmCars.setDieTime("BeforePrepare");
            }
            if(who.equals(ResourceManager.RMINameCustomers)||who.equals("ALL")) {
                rmCustomers.setDieTime("BeforePrepare");
            }
            if(who.equals(ResourceManager.RMINameFlights)||who.equals("ALL")) {
                rmFlights.setDieTime("BeforePrepare");
            }
            if(who.equals(ResourceManager.RMINameRooms)||who.equals("ALL")) {
                rmRooms.setDieTime("BeforePrepare");
            }
            if(who.equals(ResourceManager.RMINameReservations)||who.equals("ALL")) {
                rmReservations.setDieTime("BeforePrepare");
            }
            return true;
    }

    public boolean dieRMAfterPrepare(String who)
            throws RemoteException {
            if(who.equals(ResourceManager.RMINameCars)||who.equals("ALL")) {
                rmCars.setDieTime("AfterPrepare");
            }
            if(who.equals(ResourceManager.RMINameCustomers)||who.equals("ALL")) {
                rmCustomers.setDieTime("AfterPrepare");
            }
            if(who.equals(ResourceManager.RMINameFlights)||who.equals("ALL")) {
                rmFlights.setDieTime("AfterPrepare");
            }
            if(who.equals(ResourceManager.RMINameRooms)||who.equals("ALL")) {
                rmRooms.setDieTime("AfterPrepare");
            }
            if(who.equals(ResourceManager.RMINameReservations)||who.equals("ALL")) {
                rmReservations.setDieTime("AfterPrepare");
            }
            return true;
    }

    public boolean dieTMBeforeCommit()
            throws RemoteException {
            tm.setDieTime("BeforeCommit");
            return true;
    }

    public boolean dieTMAfterCommit()
            throws RemoteException {
            tm.setDieTime("AfterCommit");
            return true;
    }

    public boolean dieRMBeforeCommit(String who)
            throws RemoteException {
            if(who.equals(ResourceManager.RMINameCars)||who.equals("ALL")) {
                rmCars.setDieTime("BeforeCommit");
            }
            if(who.equals(ResourceManager.RMINameCustomers)||who.equals("ALL")) {
                rmCustomers.setDieTime("BeforeCommit");
            }
            if(who.equals(ResourceManager.RMINameFlights)||who.equals("ALL")) {
                rmFlights.setDieTime("BeforeCommit");
            }
            if(who.equals(ResourceManager.RMINameRooms)||who.equals("ALL")) {
                rmRooms.setDieTime("BeforeCommit");
            }
            if(who.equals(ResourceManager.RMINameReservations)||who.equals("ALL")) {
                rmReservations.setDieTime("BeforeCommit");
            }
            return true;
    }

    public boolean dieRMBeforeAbort(String who)
            throws RemoteException {
            if(who.equals(ResourceManager.RMINameCars)||who.equals("ALL")) {
                rmCars.setDieTime("BeforeAbort");
            }
            if(who.equals(ResourceManager.RMINameCustomers)||who.equals("ALL")) {
                rmCustomers.setDieTime("BeforeAbort");
            }
            if(who.equals(ResourceManager.RMINameFlights)||who.equals("ALL")) {
                rmFlights.setDieTime(BeforeAbort");
            }
            if(who.equals(ResourceManager.RMINameRooms)||who.equals("ALL")) {
                rmRooms.setDieTime("BeforeAbort");
            }
            if(who.equals(ResourceManager.RMINameReservations)||who.equals("ALL")) {
                rmReservations.setDieTime("BeforeAbort");
            }
            return true;
    }
}
