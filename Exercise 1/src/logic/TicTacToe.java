package logic;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

/**
 * A Tic Tac Toe application.
 * Currently this is a stand-alone application where
 * players take alternating turns using the same computer.
 * <p/>
 * The task is to transform it to a networking application using RMI.
 */
@SuppressWarnings("serial")
public class TicTacToe extends JFrame implements ListSelectionListener, ActionListener{
	private static final int BOARD_SIZE = 15;
	private final BoardModel boardModel;
	private final JTable board;
	private final JLabel statusLabel = new JLabel();
	private final char playerMarks[] = {'X', 'O'}; //X is the server, O is the client.
	private int currentPlayer = 0; // Player to set the next mark.
	private Connector connector;
	private ConnectorInterface player;
	private char mark;
	private boolean playerTurn;
	private boolean gameIsWon = false;
	private JButton newGameButton;
	public static void main(String args[]){
		new TicTacToe();
		System.setSecurityManager(new LiberalSecurityManager());
	}

	public TicTacToe(){

		super("TDT4190: Tic Tac Toe");


		boardModel = new BoardModel(BOARD_SIZE);
		connector = new Connector(boardModel, this);
		player = connector.clientConnect();


		if(connector.isServer()){
			playerTurn = true;
			mark = 'X';
		}
		else{
			playerTurn = false;

			mark = 'O';
		}
		System.out.println(player);

		board = new JTable(boardModel);
		board.setFont(board.getFont().deriveFont(25.0f));
		board.setRowHeight(30);
		board.setCellSelectionEnabled(true);
		for (int i = 0; i < board.getColumnCount(); i++)
			board.getColumnModel().getColumn(i).setPreferredWidth(30);
		board.setGridColor(Color.BLACK);
		board.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultTableCellRenderer dtcl = new DefaultTableCellRenderer();
		dtcl.setHorizontalAlignment(SwingConstants.CENTER);
		board.setDefaultRenderer(Object.class, dtcl);
		board.getSelectionModel().addListSelectionListener(this);
		board.getColumnModel().getSelectionModel().addListSelectionListener(this);

		statusLabel.setPreferredSize(new Dimension(statusLabel.getPreferredSize().width, 40));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    newGameButton = new JButton("Start new game");

		
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(board, BorderLayout.CENTER);
		contentPane.add(statusLabel, BorderLayout.SOUTH);
		contentPane.add(newGameButton, BorderLayout.NORTH);
		
		
		pack();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		newGameButton.addActionListener(this);

		int centerX = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getSize().width) / 2;
		int centerY = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getSize().height) / 2;
		setLocation(centerX, centerY);
		setVisible(true);
	}

	void setStatusMessage(String status){
		statusLabel.setText(status);
	}

	/**
	 * This has to be modified. Currently the application is stand-alone so
	 * both players have to use the same computer.
	 * <p/>
	 * When completed, marks from the first player originates from a ListSelectionEvent
	 * and is then sent to the second player. And marks from the second player is received
	 * and added to the board of the first player.
	 */
	public void valueChanged(ListSelectionEvent e){
		System.out.println(playerTurn);
		if(player != null && playerTurn && !gameIsWon){
			//if(connector.myTurn()){

			if (e.getValueIsAdjusting())
				return;
			int x = board.getSelectedColumn();
			int y = board.getSelectedRow();

			if (x == -1 || y == -1 || !boardModel.isEmpty(x, y))
				return;
			try {
				boardModel.setCell(x, y, mark);
	
				if (player.setMark(x, y, mark)){
					gameIsWon = true;
					setStatusMessage("Player " + mark + " won!");
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				player.serverTurn(true);
				playerTurn = false;
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	

			currentPlayer = 1 - currentPlayer; // The next turn is by the other player.
		}

	}
	public void setPlayer(ConnectorInterface player){
		this.player = player;
	}
	public void playerTurn(boolean playerTurn){
		this.playerTurn = playerTurn;
	}
	public void setGameIsWon(boolean gameIsWon){
		this.gameIsWon = gameIsWon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(connector.isServer()){
			try {
				playerTurn = true;
				player.serverTurn(true);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else{
			try {
				playerTurn = false;
				player.serverTurn(false);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		boardModel.cleanBoard(BOARD_SIZE);
		this.gameIsWon = false;
		try {
			player.setGameIsWon(false);
			player.resetGame(BOARD_SIZE);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setStatusMessage("");
		this.repaint();
	}
}
