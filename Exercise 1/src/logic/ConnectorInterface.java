package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectorInterface extends Remote {
	

    String sendMessage(String message) throws RemoteException;
    boolean setMark(int x, int y, char mark) throws RemoteException;
    boolean serverTurn(boolean server) throws RemoteException;
    void setOpponent(ConnectorInterface opponent) throws RemoteException;
    void setGameIsWon(boolean gameIsWon) throws RemoteException;
    void resetGame(int boardSize) throws RemoteException;



}
