import java.io.Serializable;
public class BattleShipTable implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/* Constants*/
	//Size of each type of ship
	static final int AIRCRAFT_CARRIER_SIZE = 5;
	static final int DESTROYER_SIZE = 3;
	static final int SUBMARINE_SIZE = 1;
	
	//symbols use on the board
	/*
	   "A": Aircraft
	   "D": Destroyer
	   "S": Submarine
	   
	   "X": Hit
	   "O": Miss
	   "-": default value
	*/
	
	static final String AIRCRAFT_CARRIER_SYMBOL = "A";
	static final String DESTROYER_SYMBOL = "D";
	static final String SUBMARINE_SYMBOL = "S";
	static final String HIT_SYMBOL = "X";
	static final String MISS_SYMBOL = "O";
	static final String ORIGINAL_SYMBOL = "-";
	static final String SUNK_SYMBOL = "*";
	
	String [][]table = null;


	// constructor 
	public BattleShipTable() 
	{ 
		System.out.println("create table");
		this.table = new String[10][10];
		//set default values
		for(int i=0;i<10;++i){
			for(int j=0;j<10;++j){
				this.table[i][j] = ORIGINAL_SYMBOL;
			}		
		}		
	} 
	/*convert alpha_numeric to the X and Y coordinates*/
	private int[] AlphaNumerictoXY(String alpha_coordinates) throws NumberFormatException {
		//get the alpha part
		int []ret = new int[2];
		ret[0] = this.helperAlphaToX(alpha_coordinates.charAt(0));
		//get the numeric part
		ret[1] = Integer.parseInt(alpha_coordinates.substring(1));
		return ret;
	}
	private int helperAlphaToX(char alpha){
		return (int)alpha - (int)'A';
	}

	private String XYToAlphaNumeric(int []xy){
		return "" + ((char)(xy[0] + (int)'A')) + "" + xy[1];
	}
	//print out the table
	public String toString(){
		String ret = new String();
		System.out.println("    0   1   2   3   4   5   6   7   8   9  ");
		for(int i=0;i<10;++i){
		ret = ret + "" + (char)((int)'A' + i) + " | ";
			for(int j=0;j<10;++j){
			ret = ret + this.table[i][j] + " | ";
			}
			ret = ret + "\n";
		}
		return ret;
	}
	public boolean isHit(String x1) {
		int[] xy = AlphaNumerictoXY(x1);
		return !table[xy[0]][xy[1]].equals(ORIGINAL_SYMBOL);
	}
	public void insertHit(String x1){
		this.insertSinglePoint(this.AlphaNumerictoXY(x1), HIT_SYMBOL);
	}
	public void insertSubSunk(String x1){
		this.insertSinglePoint(this.AlphaNumerictoXY(x1), SUNK_SYMBOL);
	}
	public void insertDestroyerSunk(String x1, String x2){
		this.insertShip(x1, x2, BattleShipTable.DESTROYER_SIZE, SUNK_SYMBOL);
	}
	public void insertAircraftCarrierSunk(String x1, String x2){
		this.insertShip(x1, x2, BattleShipTable.AIRCRAFT_CARRIER_SIZE, SUNK_SYMBOL);
	}
	public void insertMiss(String x1) {
		this.insertSinglePoint(this.AlphaNumerictoXY(x1), MISS_SYMBOL);
	}
	
	public boolean insertSubmarine(String x1){
		//check if it can be inserted
		if(this.insertSinglePoint(this.AlphaNumerictoXY(x1), SUBMARINE_SYMBOL))
			return true;
		else
			return false;
	}	
	
	public boolean checkInsertSubmarine(String x1){
		//check if it can be inserted
		if(this.checkInsertSinglePoint(this.AlphaNumerictoXY(x1)))
			return true;
		else
			return false;
	}	
	
	public boolean insertAirCarrier(String x1, String x2){
		//check if it can be inserted
		if(this.insertShip(x1, x2, BattleShipTable.AIRCRAFT_CARRIER_SIZE, AIRCRAFT_CARRIER_SYMBOL))
			return true;
		else
			return false;
	}
	
	public boolean checkInsertAirCarrier(String x1, String x2){
		//check if it can be inserted
		if(this.checkInsertShip(x1, x2, BattleShipTable.AIRCRAFT_CARRIER_SIZE))
			return true;
		else
			return false;
	}
	
	public boolean insertDestroyer(String x1, String x2){
		//check if it can be inserted	
		if(this.insertShip(x1, x2, BattleShipTable.DESTROYER_SIZE, DESTROYER_SYMBOL))
			return true;
		else
			return false;
	}
	
	public boolean checkInsertDestroyer(String x1, String x2){
		//check if it can be inserted	
		if(this.checkInsertShip(x1, x2, BattleShipTable.DESTROYER_SIZE))
			return true;
		else
			return false;
	}

	private boolean insertShip(String x1, String x2, int len, String s){
		int []xy1 = this.AlphaNumerictoXY(x1);
		int []xy2 = this.AlphaNumerictoXY(x2);
		if(!(xy1[0]>=0 && xy1[0]<=9 && xy1[1]>=0 && xy1[1]<=9)) return false;
		if(!(xy2[0]>=0 && xy2[0]<=9 && xy2[1]>=0 && xy2[1]<=9)) return false;
		
		if(xy1[0] == xy2[0] && (xy1[1]+1) == xy2[1]){// along the x axis
			if(checkAlongXAxis(this.AlphaNumerictoXY(x1),len)){//insert the battleship
				this.insertAlongXAxis(this.AlphaNumerictoXY(x1), len, s);
				return true;
			}else{//prompt the user again
				return false;
			}
		}else if(xy1[1] == xy2[1] && (xy1[0]+1) == xy2[0]){// along the y axis
			if(checkAlongYAxis(this.AlphaNumerictoXY(x1), len)){//insert the battleship
				this.insertAlongYAxis(this.AlphaNumerictoXY(x1), len, s);
				return true;
			}else{//prompt the user again
				return false;
			}
		}else
			return false;
	}
	
	private boolean checkInsertShip(String x1, String x2, int len){
		int []xy1 = this.AlphaNumerictoXY(x1);
		int []xy2 = this.AlphaNumerictoXY(x2);
		if(!(xy1[0]>=0 && xy1[0]<=9 && xy1[1]>=0 && xy1[1]<=9)) return false;
		if(!(xy2[0]>=0 && xy2[0]<=9 && xy2[1]>=0 && xy2[1]<=9)) return false;
		
		if(xy1[0] == xy2[0] && (xy1[1]+1) == xy2[1]){// along the x axis
			if(checkAlongX(this.AlphaNumerictoXY(x1),len)){//insert the battleship
				return true;
			}else{//prompt the user again
				return false;
			}
		}else if(xy1[1] == xy2[1] && (xy1[0]+1) == xy2[0]){// along the y axis
			if(checkAlongY(this.AlphaNumerictoXY(x1), len)){//insert the battleship
				return true;
			}else{//prompt the user again
				return false;
			}
		}else
			return false;
	}
	
	private boolean insertSinglePoint(int[] xy, String s){
		this.table[xy[0]][xy[1]] = s;
		return true;
	}
	
	private boolean checkInsertSinglePoint(int[] xy){
		if(this.table[xy[0]][xy[1]] == HIT_SYMBOL) {
			return true;
		}
		return false;
	}
	
	private boolean checkAlongXAxis(int[] xy, int len){
		if(xy[1]+len > 10) return false;
		for(int j=xy[1];j<xy[1]+len;++j){
			if(this.table[xy[0]][j].equals(ORIGINAL_SYMBOL) && this.table[xy[0]][j].equals(HIT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private boolean checkAlongX(int[] xy, int len){
		if(xy[1]+len > 10) return false;
		for(int j=xy[1];j<xy[1]+len;++j){
			if(!this.table[xy[0]][j].equals(HIT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private void insertAlongXAxis(int[] xy, int len, String s){
		for(int j=xy[1];j<xy[1]+len;++j){
			this.table[xy[0]][j] = s;
		}
	}
	
	private boolean checkAlongYAxis(int[] xy, int len){
		if(xy[0]+len > 10) return false;
		for(int i=xy[0];i<xy[0]+len;++i){
			if(this.table[i][xy[1]].equals(ORIGINAL_SYMBOL) && this.table[i][xy[1]].equals(HIT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private boolean checkAlongY(int[] xy, int len){
		if(xy[0]+len > 10) return false;
		for(int i=xy[0];i<xy[0]+len;++i){
			if(!this.table[i][xy[1]].equals(HIT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private void insertAlongYAxis(int[] xy, int len, String s){
		for(int i=xy[0];i<xy[0]+len;++i){
			this.table[i][xy[1]] = s;				
		}		
	}	
	
//	public static void main(String args[]) 
//	{ 
//		BattleShipTable t = new BattleShipTable();
//	} 
} 
