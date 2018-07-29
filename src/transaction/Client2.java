/*
author Jett
Basic Exception Test
 */
package transaction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

import rm.ResourceManager;
import rm.ResourceManagerUnaccessibleException;
import tm.TransactionAbortedException;
import tm.TransactionCommitException;
import wc.WorkflowController;

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
            wc = (WorkflowController) Naming.lookup("//localhost/" + WorkflowController.RMIName);
            System.out.println("Bound to WC");
        } catch (Exception e) {
            System.err.println("Cannot bind to WC:" + e);
            System.exit(1);
        }
    }
    //Exception TransactionManager Die Before Commit
    try{
        int xid=wc.start();
        wc.dieTMBeforeCommit();
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!")
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
            System.exit(1);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    //Exception TransactionManager Die After Commit
    try{
        int xid=wc.start();
        wc.dieTMAfterCommit();
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!")
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
            System.exit(1);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    //Exception ResourceManager Die Before Commit
    try{
        int xid=wc.start();
        wc.dieRM(ResourceManager.RMINameFlights,"BeforeCommit");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!")
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
            System.exit(1);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    //Exception ResourceManager Die Before Abort
    try{
        int xid=wc.start();
        wc.dieRM(ResourceManager.RMINameFlights,"BeforeAbort");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!")
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
            System.exit(1);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }

    //Exception ResourceManager Die After Enlist
    try{
        int xid=wc.start();
        wc.dieRMAfterEnlist(ResourceManager.RMINameCars);
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }

        try{
            int res=0;
            res=wc.commit(xid);
            if(res==1)
                System.out.println(xid+" Commit succeeded!");
            else if(res==0)
                System.out.println("Transaction " +xid+"falied to commit!");
            else
                System.out.println(xid+" Commit Exception!")
        }catch(RemoteException e){
            System.err.println("Remote Exception: "+e);
            System.exit(1);
        }catch(TransactionAbortedException e){
            System.err.println("TransactionAbortedException: "+e);
        }catch(InvalidTransactionException e){
            System.err.println("InvalidTransactionException: "+e);
        }catch(InvalidIndexException e){
            System.err.println("InvalidIndexException: "+e);
        }
    }catch(Exception e){
        e.printStackTrace();
    }

    try {
        int xid = wc.start();
        wc.dieRM(ResourceManager.RMINameFlights, "AfterPrepare");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
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
        int xid = wc.start();
        wc.dieRM(ResourceManager.RMINameFlights, "BeforePrepare");
        try{
            if(wc.addCars(xid,"HanDan",200,100))
                System.out.println(xid+" Adding car location at "+"HanDan");
            else
                System.err.println(xid+" Adding car location at "+"HanDan "+"failed!");
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
        }catch(RemoteException e){
            System.err.println("Remote Exception:"+e);
        }

        try{
            if(wc.addFlight(xid,"LianHangG7652",200,1500))
                System.out.println(xid+" Adding flight "+"LianHangG7652"+"!");
            else
                System.out.println(xid+" Adding flight "+"LianHangG7652"+" failed!")
        }catch(DeadlockException e){
            System.err.println("Dead Lock Exception:"+e);
        }catch(ResourceManagerUnaccessibleException e){
            System.err.println("Resource Manager Unaccessible Exception:"+e);
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