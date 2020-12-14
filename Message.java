import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable
{ 
	private static final long serialVersionUID = 1L;
	static final int MSG_REQUEST_INIT = 1;//sent from server to client
	static final int MSG_RESPONSE_INIT = 2; //sent from client to server
	static final int MSG_REQUEST_PLAY = 3; //sent from server to client
	static final int MSG_RESPONSE_PLAY = 4;//sent from client to server
	static final int MSG_REQUEST_GAME_OVER = 5; //sent from server to client
	
	private int msgType=-1;
	private String msg = null;
	public String blockBomb = new String(); //x, y coordinates of the block on the opponent's board ot be bombed; this is for the MSG_RESPONSE_PLAY message
	
	public ArrayList<String> ships = new ArrayList<String>();
	
	public BattleShipTable Ftable = null;//the player's own board (F-board)
	public BattleShipTable Ptable = null;//the player hits and misses on the opponent board (P-board)
	
	
	
	//getters
	public String getMsg(){
		return this.msg;
	}
	
	public int getMsgType(){
		return this.msgType;
	}
	//setters
	public void setMsg(String m){
		this.msg = m;
	}
	
	public void setMsgType(int type){
		this.msgType = type;
	}

	// constructor
	public Message() 
	{ 
		Ftable = new BattleShipTable();
		Ptable = new BattleShipTable();
	} 
	
} 

