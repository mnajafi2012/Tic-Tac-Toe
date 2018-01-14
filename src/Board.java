import java.util.ArrayList;
import java.util.List;

/**
 * Board.java
 * 
 * @author Maryam Najafi
 * @version 1.0 1/14/2017
 * 
 *          Objective: Create a new empty board for the Tic Tac Toe board game
 * 
 */

public class Board {

	// the board b
	private int[][] b;
	// learning weights
	protected double[] W;
	// game status (true/false >>> over/continue)
	private boolean over = false;
	// V_train for every board (a finished game)
	double V_train;
	// Estimated V (for every board state) It helps determine the next move
	double V_hat;
	// save winners or tie (1 or 2 as winners or 0 as tie)
	int result;
	// features (explained in detail in the Main class)
	private int dim_features = 7; private int dim_weights = 7;
	int[] features = new int[dim_features]; //x0 = 1, x1, x2, x3, x4, x5, x6, x7
	
	
	

	// constructor
	public Board(int dim, double w) {
		this.b = new int[dim][dim];
		this.W = new double[] { w, w, w, w, w, w, w };
		this.setOver(false);
	}
	
	public Board(){
		int[][] b = new int[3][3];
		this.b = b;
		this.W = new double[this.dim_weights];
	}
	
	public PerformanceSystem play(boolean playGame) {
		return(new PerformanceSystem(playGame, this));

	}
	
	public PerformanceSystem play(String a1, String a2, String a3) {
		return(new PerformanceSystem(a1, a2, a3, this));

	}
	
	public Critic getTrainedExamples(ArrayList<Board> h, int res) {
		// Critic
		return (new Critic(h, res));
		
	}
	
	public Generalizer calHypothesis(ArrayList<Board> history, double etha, boolean upTOdown, double num) {
		// generalizer takes the training examples and outputs hypothesis
		// Vhat (using updated weights)
		return (new Generalizer(history, etha, upTOdown, num));
		
	}
	
	public Board cloneBoard(){
		Board clonned = new Board();
		
		for (int i = 0; i<b.length; i++){
			for (int j= 0; j< b[i].length; j++){
				clonned.b[i][j] = this.b[i][j];
			}
			
		}
		clonned.setWeight(this.getWeight());
		clonned.setFeatures(this.getFeatures());
		clonned.setOver(this.getOver());
		clonned.setV_hat(this.getV_hat());
		clonned.setV_train(this.getV_train());
		clonned.setResult(this.getResult());
		return clonned;
	}
	
	private void setFeatures(int[] features) {
		for (int i = 0; i < features.length; i++){
			this.features[i] = features[i];
		}	
	}

	public int[] getFeatures() {
		return this.features;
	}
	
	protected void setWeight(double[] argin) {
		for (int i = 0; i < argin.length; i++){
			this.W[i] = argin[i];
		}
	}
	
	public double[] getWeight() {
		return this.W;
	}

	// override arg input onto this board
	public void override (Board argin){
		for (int i = 0; i<b.length; i++){
			for (int j= 0; j< b[i].length; j++){
				this.b[i][j] = argin.b[i][j];
			}
			
		}
		this.setFeatures(argin.getFeatures());
		this.setOver(argin.getOver());
		
	}
	
	public void init (int dim, double[] W){
		for (int i= 0; i < dim; i++){
			for ( int j = 0; j < dim; j++){
				this.b[i][j] = 0;
			}
		}
		this.W = new double[W.length];
		this.W = W;
		this.setOver(false);
		int[] features = new int[dim_features];
		this.setFeatures(features);
		this.result = 0;
		this.setV_hat(0.0);
		this.setV_train(0.0);
		
	}

	public int[][] getBoardCells() {
		return this.b;

	}

	// overloaded methods I (this.setItem)
	public void setItem(int pos1, int pos2, String val) {

		// pos1 and pos2 mean the location of the move
		// val means either X or O (1, 2, and 0 for X, O, and empty)
		switch (val.toLowerCase()) {
		case "x":
			this.b[pos1][pos2] = 1;
			break;
		case "o":
			this.b[pos1][pos2] = 2;
			break;
		default:
			this.b[pos1][pos2] = 0;
			break;
		}

	}

	// overloaded methods II (this.setItem)
	public void setItem(int pos1, int pos2, int argin) {
		// pos1 and pos2 mean the position of the move
		// argin represents who performs the movement. Leader or Teacher
		this.b[pos1][pos2] = argin;

		}
	
	public int getItem(int pos1, int pos2){
		return this.b[pos1][pos2];
	}
	

	public boolean isEmpty() {
		for (int[] C : this.getBoardCells()) {
			for (int c : C) {
				if (c != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isFull() {
		for (int[] C : this.getBoardCells()) {
			for (int c : C) {
				if (c == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void display() {
		System.out.println("_________");
		String a, b, c;
		for (int[] C : this.getBoardCells()) {
			// a = C[0] == 0 ? "_" : "X";
			a = replace(C[0]);
			b = replace(C[1]);
			c = replace(C[2]);

			System.out.printf("%s %3s %3s %n", a, b, c);
		}
		System.out.println("_________");
	}

	private String replace(int i) {
		String a;
		switch (i) {
		case 1:
			a = " X";
			break;
		case 2:
			a = " O";
			break;
		default:
			a = "..";
			break;
		}
		return a;
	}

	public void defineStat() {
		// check whether the game is over
		// all cells are filled out
		setOver(isFull() == true ? true : false);

		// either player wins the game
		// 0 means no winner in the rows.
		// 1 means X is a winner in one of the rows (likewise for 2)
		int row = checkRows();
		int col = checkColumns();
		int diag = checkDiags();
		
		if (!getOver())
		setOver((row+col+diag) != 0 ? true : false);
		
		if (getOver()){
			if (row != 0){
				{System.out.println("The winner is: "+ replace (row)); setResult(row);}
			}else if (col != 0){
				{System.out.println("The winner is: "+ replace (col)); setResult(col);}
			}else if (diag != 0){
				{System.out.println("The winner is: "+ replace (diag)); setResult(diag);}
			}else{
				{System.out.println("TIE!");}
			}
		}
		
	}

	private int checkDiags() {
		int winner = 0;
		int sum = 0;
		int[][] tmpboard = this.getBoardCells();
		for (int i = 0; i < tmpboard.length; i++) {
			
			if (tmpboard[i][i] == 0 ) {sum = 0; break;}
			 // retrieve the descending diagonal
			sum += tmpboard[i][i];
			
			// If cumulative value of cells in a column be
			// 0, 3, and 6 it means that the row is empty, full of all
			// 1's (X's), and all are 2's (O's).
			// Otherwise the row is not full.
		}
			switch (sum) {
			case 3:
				winner = 1; break;
			case 6:
				winner = 2; break;
			default:
				winner = 0; break;
			}
			if (winner != 0) {
				return winner;
			}

		
		sum = 0;
		for (int i = 0; i < tmpboard.length; i++) {
			for (int j = 0; j < tmpboard.length; j++){
				if ((i + j) == 2){
					if (tmpboard[i][j] == 0 ) {sum = 0; return 0;}
					// retrieve the ascending diagonal
					sum += tmpboard[i][j];
				}
			}

		}
		// If cumulative value of cells in a diagonal be
		// 0, 3, and 6 it means that the row is empty, full of all
		// 1's (X's), and all are 2's (O's).
		// Otherwise the row is not full.
		switch (sum) {
		case 3:
			winner = 1; break;
		case 6:
			winner = 2; break;
		default:
			winner = 0; break;
		}
		if (winner != 0) {
			return winner;
		}

		return 0;
	}

	private int checkColumns() {
		int winner = 0;
		int sum;
		int[][] tmpboard = this.getBoardCells();
		for (int i = 0; i < tmpboard.length; i++) {
			sum = 0;
			 // retrieve a column
			for (int j= 0 ; j < tmpboard.length; j++) {
				if (tmpboard[j][i] == 0) {sum = 0; break;}
				sum += tmpboard[j][i];
			}
			// If cumulative value of cells in a column be
			// 0, 3, and 6 it means that the row is empty, full of all
			// 1's (X's), and all are 2's (O's).
			// Otherwise the row is not full.
			switch (sum) {
			case 3:
				winner = 1; break;
			case 6:
				winner = 2; break;
			default:
				winner = 0; break;
			}
			if (winner != 0) {
				return winner;
			}

		}

		return 0;
	}

	private int checkRows() {
		int winner = 0;
		int sum;
		for (int[] C : this.getBoardCells()) {
			sum = 0;
			for (int c : C) {
				if (c == 0) {sum = 0; break;}
				sum += c;
			}
			// If cumulative value of cells in a row be
			// 0, 3, and 6 it means that the row is empty, full of all
			// 1's (X's), and all are 2's (O's).
			// Otherwise the row is not full.
			switch (sum) {
			case 3:
				winner = 1;
				break;
			case 6:
				winner = 2;
				break;
			default:
				winner = 0;
				break;
			}
			if (winner != 0) {
				return winner;
			}
		}
		return 0;
	}

	public void setOver(boolean argin) {
		this.over = argin;
	}

	public boolean getOver() {
		
		return this.over;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void makeMove(String mark){
		
		
		ArrayList<int[]> vacancies = this.getLegalMoves();
		
		// V_train is important for intermediate board states
		// Take a look at the formula below:
		// V_train(b) <--- V_hat(successor (b))
		Pair move_tuple = dobestMove(vacancies, mark);
		this.override((Board) move_tuple.getsecond());
		this.setV_hat((double) move_tuple.getfirst());
	
	}


	public ArrayList<int[]> getLegalMoves(){
		// returned values are tuples of {{1,2},{0,0},..{2, 2}}
		
		ArrayList<int[]> tmp = new ArrayList<int[]>();
		int[] tuple;
		for (int i = 0; i < this.getBoardCells().length; i++) {
			for (int j = 0; j < this.getBoardCells().length; j++) {
				if (this.getBoardCells()[i][j] == 0) {
					tuple = new int[2];
					tuple[0] = i; tuple[1] = j;
					tmp.add(tuple);
				}
			}
		}
		
		return tmp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Pair dobestMove(ArrayList<int[]> vacancies, String mark) {
		// use features to calculate the estimated V (V_hat)
		// This value will become increasingly accurate by playing more.
		// V_hat belongs to a board state.
		// If it is accurate, the error rate gets decreased since:
		// (V_train - V_hat) approaches 0.
		
		double V_max = -Double.MAX_VALUE;
		
		int idx_best = 0;
		
		// calculate V for all possible move
		// iterate over empty cells (vacancies)
		for (int i = 0; i < vacancies.size(); i++){
			
			// create temp board
			Board board_temp = this.cloneBoard();
			
			// plug a temp (i-th) move into the board (Learner's move)
			if (mark.equalsIgnoreCase("x"))
				board_temp.setItem(vacancies.get(i)[0], vacancies.get(i)[1], 1);
			else
				board_temp.setItem(vacancies.get(i)[0], vacancies.get(i)[1], 2);
				
			// display temporal board
			// board_temp.display();
			
			// calculate V for this move state
			double V_tmp = board_temp.cal_V();
			
			// keep the move state along with it's V (which is the highest)
			if (V_tmp > V_max){
				V_max = V_tmp;
				idx_best = i;
			}
			
		}
		Board board_temp = this.cloneBoard();
		// update the board with the moves (Learner's move : 1)
		if (mark.equalsIgnoreCase("x"))
			board_temp.setItem(vacancies.get(idx_best)[0], vacancies.get(idx_best)[1], 1);
		else
			board_temp.setItem(vacancies.get(idx_best)[0], vacancies.get(idx_best)[1], 2);
		
		board_temp.updateFeatures();
		
		Pair Vmax_Board = new Pair (V_max, board_temp);
		
		return Vmax_Board;
	}
	
	public void updateFeatures() {
		int[] features = this.findFeatures();
		
		for (int i = 0; i < features.length; i++){
			this.features[i] = features[i];
		}
		
	}

	public double cal_V(){

		int[] features = this.findFeatures();
		
		double sum = 0;
		// element-wise multiplication
		for (int i= 0; i < this.getWeight().length; i++){
			sum += this.getWeight()[i] * features[i];
		}
		
		//System.out.println(sum);
		//sum += this.getWeight()[0]; // add the bias weight
		
		return sum;
	}
	
	public int[] findFeatures(){
		int[] tmpfeatures = new int[dim_features];
		int[][] tmpboard = this.getBoardCells();
		int x = 0,o = 0;
		
		tmpfeatures[0] = 1; // for the bias
		// check rows for all features
		for (int i = 0; i < tmpboard.length; i++) {
			x = 0; o = 0;
			for (int j = 0; j < tmpboard.length; j++){
				switch (tmpboard[i][j]){
				case 1: x++; break;
				case 2: o++; break;
				default: break;
				
				}
			}
			
			// x1
			if ((x == 1) && (o == 0)) { tmpfeatures[1]++; }
			// x2
			if ((x == 2) && (o == 0)) { tmpfeatures[2]++; }
			// x3
			if ((o == 1) && (x == 0)) { tmpfeatures[3]++; }
			// x4
			if ((o == 2) && (x == 0)) { tmpfeatures[4]++; }
			// x5
			if (x == 3)  { tmpfeatures[5]++; }
			// x6
			if (o == 3) { tmpfeatures[6]++; }
			// x7
			//if ((x == 1) && (o == 1)) { tmpfeatures[7]++; }
		
		}
		// check columns for all features
		for (int i = 0; i < tmpboard.length; i++) {
			x = 0; o = 0;
			for (int j = 0; j < tmpboard.length; j++){
				switch (tmpboard[j][i]){
				case 1: x++; break;
				case 2: o++; break;
				default: break;
				
				}
			}
			
			// x1
			if ((x == 1) && (o == 0)) { tmpfeatures[1]++; }
			// x2
			if ((x == 2) && (o == 0)) { tmpfeatures[2]++; }
			// x3
			if ((o == 1) && (x == 0)) { tmpfeatures[3]++; }
			// x4
			if ((o == 2) && (x == 0)) { tmpfeatures[4]++; }
			// x5
			if (x == 3)  { tmpfeatures[5]++; }
			// x6
			if (o == 3) { tmpfeatures[6]++; }
			// x7
			//if ((x == 1) && (o == 1)) { tmpfeatures[7]++; }
		
			
		}
		// check diagonals for all features
		x = 0; o = 0;
		for (int i = 0; i < tmpboard.length; i++) {
			switch (tmpboard[i][i]){
			case 1: x++; break;
			case 2: o++; break;
			default: break;
			
			}
		}
			
		// x1
		if ((x == 1) && (o == 0)) { tmpfeatures[1]++; }
		// x2
		if ((x == 2) && (o == 0)) { tmpfeatures[2]++; }
		// x3
		if ((o == 1) && (x == 0)) { tmpfeatures[3]++; }
		// x4
		if ((o == 2) && (x == 0)) { tmpfeatures[4]++; }
		// x5
		if (x == 3)  { tmpfeatures[5]++; }
		// x6
		if (o == 3) { tmpfeatures[6]++; }
		// x7
		//if ((x == 1) && (o == 1)) { tmpfeatures[7]++; }
					

		x = 0; o = 0;
		for (int i = 0; i < tmpboard.length; i++) {
			for (int j = 0; j < tmpboard.length; j++){
				if ((i + j) == 2){
					switch (tmpboard[i][j]){
					case 1: x++; break;
					case 2: o++; break;
					default: break;	
					}
				}
			}
		}

		// x1
		if ((x == 1) && (o == 0)) { tmpfeatures[1]++; }
		// x2
		if ((x == 2) && (o == 0)) { tmpfeatures[2]++; }
		// x3
		if ((o == 1) && (x == 0)) { tmpfeatures[3]++; }
		// x4
		if ((o == 2) && (x == 0)) { tmpfeatures[4]++; }
		// x5
		if (x == 3)  { tmpfeatures[5]++; }
		// x6
		if (o == 3) { tmpfeatures[6]++; }
		// x7
		//if ((x == 1) && (o == 1)) { tmpfeatures[7]++; }
		
		return tmpfeatures;
	}
	
	public void setV_hat(double argin){
		this.V_hat = argin;
	}
	
	public double getV_hat (){
		return this.V_hat;
	}
	
	public void setV_train(double argin){
		this.V_train = argin;
	}
	
	public double getV_train(){
		return this.V_train;
	}

	public int getResult(){
		return this.result;
	}
	
	public void setResult (int argin){
		this.result = argin;
	}
	
	public int getdim_weights(){
		return this.dim_weights;
	}

	public int getdim_features(){
		return this.dim_features;
	}
}
