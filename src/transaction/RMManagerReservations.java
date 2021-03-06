package transaction;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class RMManagerReservations{
    static Registry _rmiRegistry = null;
    public static void main(String[] args) {
        String rmiName = ResourceManager.RMINameReservations;

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/ddb.conf"));
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        String rmiPort = prop.getProperty("rm." + rmiName + ".port");
        try {
            _rmiRegistry = LocateRegistry.createRegistry(Integer
                    .parseInt(rmiPort));
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return;
        }

        if (rmiName == null || rmiName.equals("")) {
            System.err.println("No RMI name given");
            System.exit(1);
        }

        ResourceManagerImpl obj = null;
        try {
            obj = new ResourceManagerImpl(rmiName);
            _rmiRegistry.bind(rmiName, obj);
            System.out.println(rmiName + " bound");
        } catch (Exception e) {
            System.err.println(rmiName + " not bound:" + e);
            System.exit(1);
        }
    }
}
