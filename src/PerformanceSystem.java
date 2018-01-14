import java.util.ArrayList;
import java.util.Scanner;

/**
 * PerformanceSystem.java
 * 
 * @author Maryam Najafi
 * @version 1.0 1/14/2017
 * 
 *          Objective: Plays the game on the board
 * @param an
 *            instance of a new problem (new board game)
 * @return game history (the trace of its solution)
 * 
 */

public class PerformanceSystem {
	
	//private boolean playGame = false;
	Scanner sc = new Scanner (System.in);
	// game history
	private ArrayList<Board> history = new ArrayList<Board>();
	// save winners or tie (1 or 2 as winners or 0 as tie)
	int result;
		
	//public PerformanceSystem(boolean playGame, Board board){
    public PerformanceSystem(String l_u, String l_l, String l_r, Board board){
		
    	String input = l_u.concat(l_l).concat(l_r);
    	
    	//this.setTeacher (playGame);
		int decimal = Integer.parseInt(input, 2);
		
		switch (decimal){
		case 4: this.play_learner_user(board);break;
		case 2: this.play_learner_learner_(board);break;
		case 1: this.play_learner_random(board);break;
		default: this.play_learner_user(board);break;
		}
		
		// also define the result for the performance system
		// (as well as the last board state of each game)
		int sz = this.getHistory().size();
		this.getHistory().get(sz - 1).defineStat();
		this.setResult(this.getHistory().get(sz - 1).getResult());
		
	}
    
	private void play_learner_random(Board board) {

		// 2. learner vs. randomly picked board states
		while (!board.getOver()) {

			// machine plays
			// ____________________________
			// fill in considering the features
			board.makeMove("x");
			record(board);
			// show the updated board
			board.display();
			board.defineStat();
			if (board.getOver())
				break;

			// random move by opponent
			// ____________________________
			ArrayList<int[]> vacancies = board.getLegalMoves();
			int l = board.getLegalMoves().size();
			
			int idx = (int) (Math.random() * (l - 1));

			//System.out.println(idx);
			
			board.setItem(vacancies.get(idx)[0], vacancies.get(idx)[1], 2);
			board.updateFeatures();
			
			double v = board.cal_V();
			board.setV_hat(v);
			
			record(board);
			// show the updated board
			board.display();
			board.defineStat();
			if (board.getOver())
				break;

		}
		
	}

	private void play_learner_learner_(Board board) {
		System.out.println("Learner vs. Learner game begins:");
		// 2. (self-teaching)
		while (!board.getOver()) {

			// machine plays
			// ____________________________
			// fill in considering the features
			board.makeMove("x");
			record(board);
			// show the updated board
			board.display();
			board.defineStat();
			if (board.getOver())
				break;

			// random move by opponent
			// ____________________________
			//int pos1 = 0, pos2 = 0;
			board.makeMove("o");
			// update the board with the moves (Teacher's move : 2)
			//board.setItem(pos1, pos2, 2);
			record(board);
			// show the updated board
			board.display();
			board.defineStat();
			if (board.getOver())
				break;

		}
		
	}

	private void play_learner_user(Board board) {
		System.out.println("Learner vs. User game begins:");
			// 1. learn using the teacher
			while (!board.getOver()){
				
				// machine plays
				//____________________________
				// fill in considering the features
				board.makeMove("x");
				record(board);
				// show the updated board
				board.display();
				board.defineStat();
				if (board.getOver()) break;
				
				
				// teacher plays
				//____________________________
				int pos1 = 0, pos2 = 0; boolean repeat = true;
				do {
					System.out.println("Choose an empty cell: ");
					
					while (repeat == true){
						try{
							System.out.print("Enter position (x-axis): ");
							pos1= Integer.valueOf(sc.nextLine().substring(0, 1));
							System.out.print("Enter position (y-axis): ");
							pos2= Integer.valueOf(sc.nextLine().substring(0, 1));
							repeat = ((pos1 > 2) || (pos2 > 2))? true: false;
						}catch(StringIndexOutOfBoundsException | NumberFormatException  e){
							System.out.println("invalid input");
				        
						}
					}
					repeat = true;
					
				}
				while ((board.getItem(pos1, pos2) != 0));
				// update the board with the moves (Teacher's move : 2)
				board.setItem(pos1, pos2, 2);
				record(board);
				// show the updated board
				board.display();
				board.defineStat();
				if (board.getOver()) break;
				
			}
		
	}

	public PerformanceSystem(boolean playGame, Board board){
		//this.play(board);
		
		// also define the result for the performance system
		// (as well as the last board state of each game)
		int sz = this.getHistory().size();
		this.getHistory().get(sz - 1).defineStat();
		this.setResult(this.getHistory().get(sz - 1).getResult());
		
	}
	
	public PerformanceSystem(ArrayList<Board> h){
		for (Board b: h){
			this.record(b);
		}
		// also define the result for the performance system
		// (as well as the last board state of each game)
		int sz = this.getHistory().size();
		this.getHistory().get(sz - 1).defineStat();
		this.setResult(this.getHistory().get(sz - 1).getResult());

		
	}

	protected void record(Board board) {
		// keep the trace recorded in an arraylist of board-and-Vhat tuples
		Board b = board.cloneBoard();
		this.history.add(b);
		
	}
	public ArrayList<Board> getHistory() {
		return this.history;
	}
	
	public void setResult (int argin){
		this.result = argin;
	}
	
	public int getResult (){
		return this.result;
	}
}
