package logic;

import java.io.Serializable;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Connector implements ConnectorInterface {
	
	private BoardModel boardModel;
	private ConnectorInterface serverSide = null;
	private ConnectorInterface opponentForServer = null;
	private ConnectorInterface clientSide = null;
	private boolean isServer;
	private TicTacToe tacToe;
	ConnectorInterface test = null;
	
	public Connector(BoardModel borBoardModel, TicTacToe tacToe){
		this.boardModel = borBoardModel;
		this.tacToe = tacToe;
	}
	
	
	public void serverConnect(){
		try{
			Connector obj = new Connector(boardModel, tacToe);
			serverSide = (ConnectorInterface) UnicastRemoteObject.exportObject(obj, 0);
			if(LocateRegistry.getRegistry(3050) == null){
				System.out.println("HEHE");
			}
			
			Registry reg = LocateRegistry.createRegistry(3050);
			System.out.println(reg.list().length);
			if(reg.list().length == 1){
				throw new Exception("Server exists");
			}
			serverSide.sendMessage("x er dum");
			isServer = true;

			//Naming.rebind();
			reg.rebind("Hello", serverSide);
			
			
			System.out.println("Server ready");
		}catch(Exception e) {
			isServer = false;
			System.err.println("Something wrong with the server " + e.getMessage());
		}
		
	}
	


	@Override
	public String sendMessage(String message) throws RemoteException {
		// TODO Auto-generated method stub
		return message;
	}
	public ConnectorInterface clientConnect(){
		String host = "78.91.83.100";
		serverConnect();

		try {		
			Registry reg = LocateRegistry.getRegistry(host, 3050);
			clientSide = (ConnectorInterface) reg.lookup("Hello");
			
			ConnectorInterface obj = new Connector(boardModel, tacToe);
			System.out.println("YE" + serverSide);
			
			clientSide.setOpponent(serverSide);
			String response = clientSide.sendMessage("y er dum");

		}
		catch (NotBoundException nbe) {
			System.err.println("Ingen tjener er registrert " + nbe.getMessage());
			nbe.printStackTrace();
			clientSide = null;

		}
		catch (Exception e) {
			System.err.println("UPS");
			clientSide = null;
			e.printStackTrace();
		}
		System.out.println(clientSide);
		if(clientSide != null){
			return clientSide;
		}
		return serverSide;
	}


	@Override
	public boolean setMark(int x, int y, char mark) throws RemoteException {
		// TODO Auto-generated method stub
		
		return boardModel.setCell(x, y, mark);
	}


	@Override
	public boolean serverTurn(boolean server) throws RemoteException {
		// TODO Auto-generated method stub
		//myTurn = server;
		tacToe.playerTurn(server);
		return server;
	}
	
	public boolean isServer(){
		return isServer;
	}



	@Override
	public void setOpponent(ConnectorInterface opponent) throws RemoteException {
		this.opponentForServer = opponent;
		System.out.println(opponent);
		tacToe.setPlayer(opponent);
	}
	public ConnectorInterface getOpponent(){
		System.out.println("RR: " + this.opponentForServer);
		return this.opponentForServer;
	}

}
