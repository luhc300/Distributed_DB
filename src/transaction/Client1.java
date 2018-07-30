/*
author Jett
Basic Function Test
 */
package transaction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

import lockmgr.DeadlockException;

public class Client1 {
    public static void main(String args[]) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/ddb.conf"));
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        String rmiPort = prop.getProperty("wc.port");
        if (rmiPort == null) {
            rmiPort = "";
        } else if (!rmiPort.equals("")) {
            rmiPort = "//:" + rmiPort + "/";
        }

        WorkflowController wc = null;
        try {
            wc = (WorkflowController) Naming.lookup(rmiPort + WorkflowController.RMIName);
            System.out.println("Bound to WC");
        } catch (Exception e) {
            System.err.println("Cannot bind to WC:" + e);
            System.exit(1);
        }
    //Test adding customers,hotel,flight,cars and commit a transaction.
    try{
        int xid=wc.start();
        if(wc.newCustomer(xid,"Jett"))
            System.out.println("Transaction "+xid+ " Adding customer"+" Jett!");
        else
            System.err.println("Transaction "+xid+ " Adding customer"+" Jett failed!");
        if(wc.addRooms(xid,"ZhangJiang",100,400))
            System.out.println("Transaction "+xid+ " Adding location"+" ZhangJiang!");
        else
            System.err.println("Transaction "+xid+ " Adding location"+" ZhangJiang failed!");
        if(wc.addFlight(xid,"ChuanHang5731",300,1000))
            System.out.println("Transaction "+xid+ " Adding flight"+" ChuanHang5731!");
        else
            System.err.println("Transaction "+xid+ " Adding flight"+" ChuanHang5731 failed!");
        if(wc.addCars(xid,"ZhangJiang",100,50))
            System.out.println("Transaction "+xid+ " Adding location"+"ZhangJiang!");
        else
            System.err.println("Transaction "+xid+" Adding location"+" ZhangJiang!");
        if(wc.commit(xid)==1)
            System.out.println("Transaction "+xid+" commited successfully!");
        else
            System.err.println("Transaction "+xid+" commit failed!");
    }catch(Exception e){
        System.err.println(e);
    }
    //Test reservation
    try{
        int xid=wc.start();
        if(wc.reserveCar(xid,"Jett","ZhangJiang"))
            System.out.println("Transacation "+xid+ " Jett "+" Reserving car at "+"ZhangJiang"+"!");
        else
            System.err.println("Transaction "+xid+" Jett "+" Reserving car at "+"ZhangJiang"+" failed!");
        if(wc.reserveRoom(xid,"Jett","ZhangJiang"))
            System.out.println("Transaction "+xid+" Jett"+" Reserving hotel at "+"ZhangJiang"+"!");
        else
            System.err.println("Transaction "+xid+" Jett "+" Reserving hotel at "+"ZhangJiang"+" failed!");
        if(wc.commit(xid)==1)
            System.out.println("Transaction "+xid +" commited successfully!");
        else
            System.err.println("Transaction Exception!");
    }catch(Exception e){
        System.err.println("Exception Info: "+e);
        e.printStackTrace();
    }
    //Test query
    try{
        int xid=wc.start();
        String flightName="ChuanHang5731";
        String hotelLocation="ZhangJiang";
        String carLocation="ZhangJiang";
        int flightSeatNum=0;
        int flightPrice=0;
        int roomNum=0;
        int roomPrice=0;
        int carNum=0;
        int carPrice=0;
        flightSeatNum=wc.queryFlight(xid,flightName);
        flightPrice=wc.queryFlightPrice(xid,flightName);
        System.out.println(flightName+" has "+flightSeatNum+" available seats, price is "+flightPrice+"!");
        roomNum=wc.queryRooms(xid,hotelLocation);
        roomPrice=wc.queryRoomsPrice(xid,hotelLocation);
        System.out.println(hotelLocation+" has "+roomNum+" available rooms, price is "+hotelLocation+"!");
        carNum=wc.queryCars(xid,carLocation);
        carPrice=wc.queryCarsPrice(xid,carLocation);
        System.out.println(carLocation+" has "+carNum+" available cars, price is "+carPrice+"!");
    }catch(Exception e){
        System.err.println("Exception info: "+e);
        e.printStackTrace();
    }
    //Test delete operations
    try{
        int xid=wc.start();
        if(wc.deleteRooms(xid,"ZhangJiang",50))
            System.out.println("Transaction "+xid+" Delete hotel location at "+"ZhangJiang!");
        else
            System.err.println("Transaction "+xid+" Delete hotel location at "+"ZhangJiang"+"failed!");
        if(wc.deleteCars(xid,"ZhangJiang",20))
            System.out.println("Transaction "+xid+" Delete car location at "+"ZhangJiang!");
        else
            System.err.println("Transaction "+xid+" Delete car location at "+"ZhangJiang"+"failed!");
        if(wc.commit(xid)==1)
            System.out.println("Transaction "+xid+" Commited Succesfully!");
        else
            System.err.println("Transaction "+xid+" Commit failed!");
    }catch(Exception e){
        System.err.println("Exception info: "+e);
        e.printStackTrace();
    }
}
}