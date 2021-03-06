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

			isServer = true;
			Connector obj = new Connector(boardModel, tacToe);
			serverSide = (ConnectorInterface) UnicastRemoteObject.exportObject(obj, 0); 
			serverSide.sendMessage("x er dum");			
			Registry reg = LocateRegistry.createRegistry(3050);
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
		String host = "78.91.80.41";
		serverConnect();

		try {		
			Registry registry = LocateRegistry.getRegistry(host, 3050);
			clientSide = (ConnectorInterface) registry.lookup("Hello");
			clientSide.setOpponent(serverSide);

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
		
		
		boolean won = boardModel.setCell(x, y, mark);
		if(won){
			tacToe.setStatusMessage("Player " + mark + " won!");
			tacToe.setGameIsWon(true);
		}
		return won;
	}


	@Override
	public boolean serverTurn(boolean server) throws RemoteException {
		tacToe.playerTurn(server);
		return server;
	}
	
	public boolean isServer(){
		return isServer;
	}



	@Override
	public void setOpponent(ConnectorInterface opponent) throws RemoteException {
		tacToe.setPlayer(opponent);
	}



	@Override
	public void setGameIsWon(boolean gameIsWon) throws RemoteException {
		tacToe.setGameIsWon(false);
	}


	@Override
	public void resetGame(int boardSize) throws RemoteException {
		boardModel.cleanBoard(boardSize);
		tacToe.setStatusMessage("");
		tacToe.repaint();
	}

}
