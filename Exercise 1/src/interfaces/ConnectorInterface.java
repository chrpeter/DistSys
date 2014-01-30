package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectorInterface extends Remote {
	

    String sendMessage() throws RemoteException;
	
	



}
