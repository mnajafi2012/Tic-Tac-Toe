import java.util.ArrayList;

/**
 * Generalizer.java
 * @author Maryam Najafi
 * @version 1.0 1/14/2017
 * 
 * Objective: Produces an estimate of the target function V for each training example
 * @param Training examples
 * @return Estimated V (Vhat)
 * 
 * Description:
 * The Generalizer corresponds to the Least Mean Squares (LMS) algorithm.
 * Vhat is computed using current weights given the following formula:
 *     Vhat = w_0 + w_1*x_1 + ... + w_n*x_n
 * Then weights are updated using the following formula:
 *     w_i = w_i + etha (V_train(b) - Vhat(b)) x_i
 * 
 */
public class Generalizer {
	
	private double[] updated_W;
	private double num; // optimal value is 0.1
	                    // default is 0.0 to ignore this effect

	// constructor
	public Generalizer (ArrayList<Board> history, double etha, boolean upTOdown, double numerator){
		this.updated_W = new double[history.get(0).getdim_weights()];
		this.setNum(numerator);
		updateWeights(history, etha, upTOdown);
	}

	private void updateWeights(ArrayList<Board> history, double etha, boolean upTOdown) {
		
		if (upTOdown){
		    // Learn from the oldest to newest in history
			setupdated_W(history.get(0).getWeight());

			double[] game_weights = cal_game_weights(history);
			
			int j = 0;
			
			for (Board b: history){
				// update features
				b.updateFeatures();
				
				double coeff = game_weights[j];
				
				// update weights
				for (int i = 0; i < b.getWeight().length; i++){
					
					double t = b.getV_train() - b.getV_hat();
					this.setupdated_W(i, this.getupdated_W()[i] + 
							//(etha * (t)) * b.getFeatures()[i] );
							(etha * (t)) * b.getFeatures()[i] * coeff); 
				}
				j++;
			}
			
		}else{
			// Learn from the newest to oldest in history
			setupdated_W(history.get(0).getWeight());

			for (int j = history.size()-1; j >=0; j--) {
				Board b = history.get(j);
				// update features
				b.updateFeatures();

				// update weights
				for (int i = 0; i < b.getWeight().length; i++) {

					double t = b.getV_train() - b.getV_hat();
					// double t = history.get(history.size()-1).getV_train() -
					// b.getV_hat();
					// System.out.println(t);
					this.setupdated_W(i, this.getupdated_W()[i] + (etha * (t)) * b.getFeatures()[i]);
				}
			}
		}
		
		// last board state's features to print out
		/*int [] features = history.get((history.size()-1)).getFeatures();
		for (int i = 0; i < this.getupdated_W().length; i++){
			System.out.printf("(%.3f ", this.getupdated_W()[i]);
			System.out.printf("x%d: %d), ", (i), features[i]);
		}
		
		double v_hat = history.get(history.size()-1).cal_V();
		System.out.printf("%nVtrain: %f%n", history.get(history.size()-1).getV_train());
		System.out.printf("Vhat: %f%n", history.get(history.size()-1).getV_hat());
		//System.out.printf("Vhat2: %f%n", v_hat);
		 */
	};

	private double[] cal_game_weights(ArrayList<Board> history) {
		double[] game_weights = new double[history.size()];
		game_weights[game_weights.length - 1] = 1;
		
		// determine the weight of each board state and normalize it
		double denominator = history.size();
		double diff = getNum()/denominator;
		
		for (int i = history.size() - 1; i > 0; i--){
			game_weights[i-1] = game_weights[i] - diff;
			//game_weights[i-1] = 1;
		}
		
		return game_weights;
	}

	protected double[] getupdated_W(){
		return this.updated_W;
	}
	
	private void setupdated_W (double[] argin){
		for (int i = 0; i < argin.length; i++){
			this.getupdated_W()[i] = argin[i];
		}
	}
	
	private void setupdated_W (int pos, double val){
		this.getupdated_W()[pos] = val;
	}
	
	private void setNum(double argin){
		this.num = argin;
	}
	
	private double getNum(){
		return this.num;
	}
	
}
