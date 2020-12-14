import java.io.*; 
import java.net.*;
import java.util.Scanner; 
class Client {
	static Message requestMessage = new Message();
	static Message responseMessage = new Message();
	static Scanner console;

    public static void main(String args[]) throws Exception 
    {  
        int messageType;
        boolean quit = false;

        Socket clientSocket = new Socket("localhost", 5000); 
        
        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        
        while(!quit) {
        	requestMessage = (Message) inFromServer.readObject();
        	messageType = requestMessage.getMsgType();
        	switch(messageType){
        	case 1:
        		initializeBoard();
        		responseMessage.setMsgType(2);
        		outToServer.writeObject(responseMessage);
        		outToServer.flush();
        		outToServer.reset();
        		break;
        	case 3:
        		System.out.print(requestMessage.Ftable.toString());
        		System.out.print(requestMessage.Ptable.toString());
        		if(requestMessage.getMsg() != null) {
        			System.out.println(requestMessage.getMsg());
        		}
        		bomb();
        		responseMessage.setMsgType(4);
        		outToServer.writeObject(responseMessage);
        		outToServer.flush();
        		outToServer.reset();
        		break;
        	case 5:
        		System.out.print(requestMessage.Ftable.toString());
        		System.out.print(requestMessage.Ptable.toString());
        		System.out.println("GAME OVER");
        		quit = true;
        	}
        }
        clientSocket.close();
    }
    
    public static void initializeBoard() {
    	console = new Scanner(System.in);
    	System.out.println(requestMessage.Ftable.toString());
		System.out.println("Where do you want to place your destroyer?");
		String d1 = console.next().toUpperCase();
		responseMessage.ships.add(d1);
		System.out.println("Where do you want to place your first destroyer?");
		String d2 = console.next().toUpperCase();
		responseMessage.ships.add(d2);
		responseMessage.Ftable.insertDestroyer(d1, d2);
		System.out.println("Where do you want to place your second destroyer?");
		String d3 = console.next().toUpperCase();
		responseMessage.ships.add(d3);
		System.out.println("Where do you want to place your second destroyer?");
		String d4 = console.next().toUpperCase();
		responseMessage.ships.add(d4);
		responseMessage.Ftable.insertDestroyer(d3, d4);
		System.out.println("Where do you want to place your first aircraft carrier?");
		String a1 = console.next().toUpperCase();
		responseMessage.ships.add(a1);
		System.out.println("Where do you want to place your first aircraft carrier?");
		String a2 = console.next().toUpperCase();
		responseMessage.ships.add(a2);
		responseMessage.Ftable.insertAirCarrier(a1, a2);
		System.out.println("Where do you want to place your second aircraft carrier?");
		String a3 = console.next().toUpperCase();
		responseMessage.ships.add(a3);
		System.out.println("Where do you want to place your second aircraft carrier?");
		String a4 = console.next().toUpperCase();
		responseMessage.ships.add(a4);
		responseMessage.Ftable.insertAirCarrier(a3, a4);
		System.out.println("Where do you want to place your first submarine?");
		String s1 = console.next().toUpperCase();
		responseMessage.ships.add(s1);
		responseMessage.Ftable.insertSubmarine(s1);
		System.out.println("Where do you want to place your second submarine?");
		String s2 = console.next().toUpperCase();
		responseMessage.ships.add(s2);
		responseMessage.Ftable.insertSubmarine(s2);
    }
    
    public static void bomb() {
    	console = new Scanner(System.in);
    	System.out.println("Enter the X and Y coordinate of the block bomb:");
    	String x = console.next();
    	responseMessage.blockBomb = x;
    }
}
