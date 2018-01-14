import java.util.ArrayList;

/**
 * Critic.java
 * @author Maryam Najafi
 * @version 1.0 1/14/2017
 * 
 * Objective: Produces a set of training examples of the target function
 * @param the history or trace of the game
 * @return a set of training examples
 * 
 * Description: Each training example corresponds to some game state in the trace.
 * {<b1, V_train(b1)>},...,{<bn, V_train(bn)>} will be the ultimate output tuples out of this class as training examples.
 * These pairs are equivalent to:
 * {<b1, V_hat(b3)>},...,{<bn, V_hat(bn+1)>}
 */
public class Critic {
	
	ArrayList<Board> history = new ArrayList<Board>();
	
	// constructor
	Critic(ArrayList<Board> history, int res){
		deployTuples(history, res);
	}

	private void deployTuples(ArrayList<Board> h, int res) {
		/* for each board state in the history of the game, assign
		V_hat of the successor board state.
		*/
		
		for (int i = 0; i < h.size(); i++) {

			if ((i % 2) == 0) {
				try {

					h.get(i).setV_train(h.get(i + 2).getV_hat());

					// add this board to the history
					add2History(h.get(i));

				} catch (IndexOutOfBoundsException e) {
					
					// add this board to the history
					add2History(h.get(i));
					
					assignresult(res);				

				}

			}

		}
		
	}
	private void add2History(Board board) {
		this.getHistory().add(board.cloneBoard());
		
	}

	private void assignresult(int res) {
		int size = this.getHistory().size();
		switch (res){
		// Machine wins
		case 1: this.getHistory().get(size - 1).setV_train(100); break;
		// Machine loses
		case 2: this.getHistory().get(size - 1).setV_train(-100); break;
		// tie
		default: this.getHistory().get(size - 1).setV_train(0); break;
		}
		
		//return h;
	}
	

	private void updateHistory(ArrayList<Board> h){
		for (Board b: h){
			getHistory().add(b.cloneBoard());
		}
		
	}
	
	public ArrayList<Board> getHistory(){
		return this.history;
	}
	
}
