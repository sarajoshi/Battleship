import java.util.ArrayList;
import java.io.*; 
import java.net.*; 

class Server { 

	private final static int PLAYER_TWO = 1;
	public static ArrayList<GameThread> gameList = new ArrayList<>();

	public static void main(String argv[]) throws Exception { 
		ServerSocket sSocket = new ServerSocket(5000);
//		InetAddress IP = InetAddress.getLocalHost();	  
		int i = 0;
		while(true) {
			Socket cSocket = sSocket.accept();
			System.out.println("Player " + Integer.toString(i+1) + " connected.");
			
			if (i % 2 == 0) {
				GameThread game = new GameThread(cSocket);
				gameList.add(game);
				game.start();
			}
			else {
				gameList.get(i/2).addPlayer(cSocket,PLAYER_TWO,true);
			}
			++i;
		}
	}

} 