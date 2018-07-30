/*
author Jett
Basic Exception Test
 */
package transaction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;


import lockmgr.DeadlockException;

public class Client2 {
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
    

    //Exception TransactionManager Die After Commit
    try{
            System.out.println("TransactionManager Die After Commit");
        int xid=wc.start();
        wc.dieTMAfterCommit();
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!");
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
            System.out.println("Waiting");
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    //Exception ResourceManager Die Before Commit
    try{
        System.out.println(" ResourceManager Die Before Commit");
        int xid=wc.start();
        wc.dieRM(ResourceManager.RMINameFlights,"BeforeCommit");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!");
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    //Exception ResourceManager Die Before Abort
    try{
        System.out.println(" ResourceManager Die Before Abort");
        int xid=wc.start();
        wc.dieRM(ResourceManager.RMINameFlights,"BeforeAbort");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!");
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }

    //Exception ResourceManager Die After Enlist
    try{
        System.out.println(" ResourceManager Die After Enlist");
        int xid=wc.start();
        wc.dieRMAfterEnlist(ResourceManager.RMINameCars);
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!");
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }

    try {
        System.out.println(" ResourceManager Die Before Prepare");
        int xid = wc.start();
        wc.dieRM(ResourceManager.RMINameFlights, "AfterPrepare");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try {
            wc.commit(xid);
        } catch (TransactionAbortedException e) {
            System.err.println("TransactionAbortedException" + e);
        } catch (RemoteException e) {
            System.err.println("RemoteException" + e);
        }
    } catch (Exception e) {
        System.err.println(e);
    }

    try {
        System.out.println(" ResourceManager Die After Prepare");
        int xid = wc.start();
        wc.dieRM(ResourceManager.RMINameFlights, "BeforePrepare");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!");
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try {
            wc.commit(xid);
        } catch (TransactionAbortedException e) {
            System.err.println("TransactionAbortedException" + e);
        } catch (RemoteException e) {
            System.err.println("RemoteException" + e);
        }
    } catch (Exception e) {
        System.err.println(e);
    }
	}
}