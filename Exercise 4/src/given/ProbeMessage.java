package given;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ProbeMessage extends Thread {

	private int transactionId;
	private ArrayList<Integer> probeList;
	private Server newServer;

	public ProbeMessage(int transactionId, ArrayList<Integer> probeList, Server newServer) {
		// TODO Auto-generated constructor stub
		this.transactionId = transactionId;
		this.probeList = probeList;
		this.newServer = newServer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{

			//Server newServer = serverImpl.getServer(newServerID);
			probeList.add(transactionId);
			System.out.println("PROBELIST: " + probeList.toString());

			newServer.sendProbe(probeList);


		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}

}
