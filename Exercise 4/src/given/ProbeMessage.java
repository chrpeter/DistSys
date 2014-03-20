
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ProbeMessage extends Thread {

	private int transactionId;
	private ArrayList<Integer> probeList;
	private Server newServer;
	private int resource_id;

	public ProbeMessage(int transactionId, ArrayList<Integer> probeList, Server newServer, int resource_id) {
		// TODO Auto-generated constructor stub
		this.transactionId = transactionId;
		this.probeList = probeList;
		this.newServer = newServer;
		this.resource_id = resource_id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{

			//Server newServer = serverImpl.getServer(newServerID);
			probeList.add(transactionId);
			System.out.println("PROBELIST: " + probeList.toString());
			
			newServer.sendProbe(probeList, resource_id);


		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}

}
