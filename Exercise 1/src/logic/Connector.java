package logic;

import interfaces.ConnectorInterface;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Connector implements ConnectorInterface {


	public void connect(){
		System.setSecurityManager(new RMISecurityManager());
		try{
			Connector obj = new Connector();
			ConnectorInterface stub = (ConnectorInterface) UnicastRemoteObject.exportObject(obj, 0); 

			Naming.rebind("//localhost/rmi", stub); 
			System.out.println("ShapeList server ready");
		}catch(Exception e) {
			System.out.println("ShapeList server main " + e.getMessage());}
	}

	@Override
	public String sendMessage() throws RemoteException {
		// TODO Auto-generated method stub
		return "Testing";
	}
	public void clientConnect(){
        String host = "";
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ConnectorInterface stub = (ConnectorInterface) registry.lookup("//localhost/rmi");
            if(stub != null){
            	String response = stub.sendMessage();
            	System.out.println("response: " + response);
            }
            else{
            	connect();
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
	}

}
