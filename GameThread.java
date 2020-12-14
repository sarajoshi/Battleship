import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GameThread extends Thread {

	private final int PLAYER_ONE = 0;
	private final int PLAYER_TWO = 1;
	public boolean pTwoConnection;
	
	private ArrayList<String> player_one_ships = new ArrayList<String>();
	private ArrayList<String> player_two_ships = new ArrayList<String>();
	
	private final int MAXIMUM_HITS = 2 * BattleShipTable.DESTROYER_SIZE
								 + 2 * BattleShipTable.AIRCRAFT_CARRIER_SIZE
								 + 2 * BattleShipTable.SUBMARINE_SIZE;

	private BattleShipTable[] fBoard = {new BattleShipTable(), new BattleShipTable()};
	private BattleShipTable[] pBoard = {new BattleShipTable(), new BattleShipTable()};
	private int[] hits = {0, 0};

	private Socket[] cSocket;
	private ObjectInputStream[] inFromClient;
	private ObjectOutputStream[] outToClient;
	
	public GameThread(Socket c) {
		cSocket = new Socket[2];
		outToClient = new ObjectOutputStream[2];
		inFromClient = new ObjectInputStream[2];
		addPlayer(c, PLAYER_ONE, false);
	}
	
	public void initializePlayer(Socket c, int id) {
		cSocket[id] = c;
		try {
			outToClient[id] = new ObjectOutputStream(c.getOutputStream());
			inFromClient[id] = new ObjectInputStream(c.getInputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void addPlayer(Socket c, int player, boolean connection) {
		initializePlayer(c, player);
		pTwoConnection = connection;

		if(connection)
			System.out.println("Player two successfully connected");
	}

	public void run() {

		boolean sentMessageReq[] = {false, false};

		Message messageReq = new Message();
		messageReq.setMsgType(Message.MSG_REQUEST_INIT);

		while (!(sentMessageReq[PLAYER_ONE] && sentMessageReq[PLAYER_TWO])) {

			if (!sentMessageReq[PLAYER_ONE]) {
				try{
					outToClient[PLAYER_ONE].writeObject(messageReq);
					sentMessageReq[PLAYER_ONE] = true;
					System.out.println("Sending player 1 request");
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
			}
			System.out.println("");
			if (!sentMessageReq[PLAYER_TWO] && pTwoConnection) {
				try {
					outToClient[PLAYER_TWO].writeObject(messageReq);
					sentMessageReq[PLAYER_TWO] = true;
					System.out.println("Sending player 2 request");
				}catch(Exception ex){
					System.out.println(ex.getMessage());
				}
			}
		}

		int playerID = PLAYER_ONE;
		int boards = 0;
		boolean gameOver = false;
		
		while (!gameOver) {
			try {
				System.out.println("Waiting for player " + (playerID+1) + " response");
				Message received = (Message) inFromClient[playerID].readObject();

				switch (received.getMsgType()) {
//				case Message.MSG_RESPONSE_INIT:
				case 2:
					fBoard[playerID] = received.Ftable;
					++boards;
					System.out.println("player board " + (playerID + 1) + "\n" + received.Ftable);
					
					if(playerID == PLAYER_ONE) {
						for(String i: received.ships) {
							player_one_ships.add(i);
						} 
					} else {
						for(String i: received.ships) {
							player_two_ships.add(i);
						}
					}
					
					playerID = (playerID + 1) % 2;

					if(boards == 2) {
						System.out.println("Boards successfully initialized");
					
						Message sendMessage = new Message();
						sendMessage.Ftable = fBoard[playerID];
						sendMessage.Ptable = pBoard[playerID];
//						sendMessage.setMsgType(Message.MSG_REQUEST_PLAY);
						sendMessage.setMsgType(3);
						outToClient[playerID].writeObject(sendMessage);
						outToClient[playerID].reset();
					}

					break;
//				case Message.MSG_RESPONSE_PLAY:
				case 4:
					System.out.println("response received from player" + (playerID+1));
			
					String bombLocation = received.blockBomb;
					int otherPlayer = (playerID + 1) % 2;

					System.out.println(fBoard[otherPlayer]);
					
					if (fBoard[otherPlayer].isHit(bombLocation)) {
						pBoard[playerID].insertHit(bombLocation);
						++hits[playerID];
					}
					else {
						pBoard[playerID].insertMiss(bombLocation);
					}
					fBoard[otherPlayer].insertHit(bombLocation);
					
					if (hits[playerID] == MAXIMUM_HITS) {
						gameOver = true;
						gameOver();
						break;
					}
				
					playerID = (playerID + 1) % 2;
					Message sendMsg = new Message();
					sendMsg.setMsgType(Message.MSG_REQUEST_PLAY);
					sendMsg.Ftable = fBoard[playerID];
					sendMsg.Ptable = pBoard[playerID];
					
					if(playerID == PLAYER_ONE) {
						if(fBoard[playerID].checkInsertDestroyer(player_one_ships.get(0), player_one_ships.get(1))) {
								sendMsg.setMsg("Ship has been sunk!");
								fBoard[playerID].insertDestroyerSunk(player_one_ships.get(0),player_one_ships.get(1));
						}
						if(fBoard[playerID].checkInsertDestroyer(player_one_ships.get(2), player_one_ships.get(3))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertDestroyerSunk(player_one_ships.get(2),player_one_ships.get(3));
						}
						if(fBoard[playerID].checkInsertAirCarrier(player_one_ships.get(4), player_one_ships.get(5))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertAircraftCarrierSunk(player_one_ships.get(4),player_one_ships.get(5));
						}
						if(fBoard[playerID].checkInsertAirCarrier(player_one_ships.get(6), player_one_ships.get(7))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertAircraftCarrierSunk(player_one_ships.get(6),player_one_ships.get(7));
						}
						if(fBoard[playerID].checkInsertSubmarine(player_one_ships.get(8))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertSubSunk(player_one_ships.get(8));
						}
						if(fBoard[playerID].checkInsertSubmarine(player_one_ships.get(9))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertSubSunk(player_one_ships.get(9));
						}
					}
					
					if(playerID == PLAYER_TWO) {
						if(fBoard[playerID].checkInsertDestroyer(player_two_ships.get(0), player_two_ships.get(1))) {
								sendMsg.setMsg("Ship has been sunk!");
								fBoard[playerID].insertDestroyerSunk(player_two_ships.get(0),player_two_ships.get(1));
						}
						if(fBoard[playerID].checkInsertDestroyer(player_two_ships.get(2), player_two_ships.get(3))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertDestroyerSunk(player_two_ships.get(2),player_two_ships.get(3));
						}
						if(fBoard[playerID].checkInsertAirCarrier(player_two_ships.get(4), player_two_ships.get(5))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertAircraftCarrierSunk(player_two_ships.get(4),player_two_ships.get(5));
						}
						if(fBoard[playerID].checkInsertAirCarrier(player_two_ships.get(6), player_two_ships.get(7))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertAircraftCarrierSunk(player_two_ships.get(6),player_two_ships.get(7));
						}
						if(fBoard[playerID].checkInsertSubmarine(player_two_ships.get(8))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertSubSunk(player_two_ships.get(8));
						}
						if(fBoard[playerID].checkInsertSubmarine(player_two_ships.get(9))) {
							sendMsg.setMsg("Ship has been sunk!");
							fBoard[playerID].insertSubSunk(player_two_ships.get(8));
						}
					}
					
					outToClient[playerID].writeObject(sendMsg);
					outToClient[playerID].reset();
					break;
					
				default:
					System.out.println("Error: message type" + received.getMsgType());
					break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void gameOver() throws IOException {
		for (int playerID = 0; playerID < 2; ++playerID) {
			Message endMessage = new Message();
			
			int otherPlayer = (playerID + 1) % 2;
			endMessage.Ftable = fBoard[playerID];
			endMessage.Ptable = fBoard[otherPlayer];
			
			outToClient[playerID].writeObject(endMessage);
		}
	}
}

