import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CSE-5693 Machine Learning Instructor: Dr. Chan Reference: Machine Learning by
 * TOM M. MITCHELL
 *
 * MAIN.java Objective: The driver to run the machine learning algorithm for Tic
 * Tac Toe Algorithm: Gradient-Descent Linear Regression Cost function: SSE
 * 
 * Features of the problem: x1,x2,x3,x4,x5,x6 are as below: In a row
 * (row/column/diagonal) there is/are: x0: 1 (for offset) x1: 1 X and 0 O. x2: 2
 * X's and 0 O. x3: 1 O and 0 X. x4: 2 O's and 0 X. x5: 3 X's x6: 3 O's
 * Hypotheses (Weight vector): w0(offset),w1,w2,w3,w4,w5,w6
 * 
 * // "numerator" is useful to apply weights on board states in history // We
 * don't use 1 bcz the effect of the 1st board is 0. // We don't use 0.5 bcz the
 * number of boards in history will be alleviated // we use 0.25 to assign the
 * min possible coefficient that is going to be // half.
 *
 * @author Maryam Najafi
 * @version 1.0 1/14/2017 affiliation: Electrical and Computer Engineering,
 *          Florida Institute of Technology
 */
public class Main {

	private static String FILENAME;

	public static void main(String[] args) {

		String path = System.getProperty("user.dir");
		FILENAME = path.concat("/examples.txt");
		
		Scanner sc = new Scanner (System.in);
		

		String train_or_test = args[0];

		if (train_or_test.equalsIgnoreCase("train")) {
			boolean teacherMode = args[1].equalsIgnoreCase("t") ? true : false;
			// t/f use the file or just re-play with machine

			// learn from the oldest (first move) to newest (last move)
			boolean upTOdown = args[2].equalsIgnoreCase("t") ? true : false;

			boolean weighted_boardstates = false;
			// weighted_boardstates = args[2].equalsIgnoreCase("t")?true:false;
			double numerator = 0.0;
			if (weighted_boardstates) {
				// board states base weight
				numerator = 0.1; // optimal value would be greater than 0.03
									// (e.g. 0.1)
									// default is 0.0 to ignore this effect
									// THE OPTIMAL VALUE FOR NUMERATOR IS
									// ROUGHLY 0.1.
			} else {
				// board states base weight
				numerator = 0.0; // optimal value would be greater than 0.03
									// (e.g. 0.1)
									// default is 0.0 to ignore this effect
									// THE OPTIMAL VALUE FOR NUMERATOR IS
									// ROUGHLY 0.1.
			}

			String learner_user = "", learner_learner = "", learner_random = "";
			if (!teacherMode) {
				// these cases matters if teacherMode is false.
				learner_user = args[3].equalsIgnoreCase("1") ? "1" : "0";
				learner_learner = args[4].equalsIgnoreCase("1") ? "1" : "0";
				learner_random = args[5].equalsIgnoreCase("1") ? "1" : "0";
			}
			int limit = 0;
			if (learner_learner.equalsIgnoreCase("1") || (learner_random.equalsIgnoreCase("1"))) {
				limit = 500; // sets of game
			} else {
				limit = 20; // sets of game
			}

			// the board dimension
			int dim;
			// num of weights
			double initweight = 0.1;
			// step length
			double etha = 0.1;
			// record updated weights for every training example
			ArrayList<double[]> updatedWEIGHTS = new ArrayList<double[]>();

			// 0. New problem (Initial game board)
			Board board = new Board(dim = 3, initweight);
			// after 30 times player with opponent
			// double[] w = new double[] {31.448, 23.261, 8.708, 7.281, -20.324,
			// 61.346, 0.100};
			// weights that are learned by two machines (both learners)
			// double[] w = new double[]{60.658, 9.836, 4.113, -4.113, -15.234,
			// 0.000, 0.100 };
			// board.init(dim, w);

			if (teacherMode) {
				// 1. Read and record all games taught by teacher in GAMES
				ArrayList<PerformanceSystem> GAMES = readfromFILE(initweight);
				int i = 0;

				for (PerformanceSystem game : GAMES) {
					
					// System.out.printf("Iteration %d%n", i+1);

					if (i > 0) {
						for (Board b : game.getHistory()) {
							b.setWeight(board.getWeight());
							double V_tmp = b.cal_V();
							b.setV_hat(V_tmp);
						}
					}

					// 2. Critic (get tuples prepared)
					Critic examples = board.getTrainedExamples(game.getHistory(), game.getResult());

					// 3. Generalizer (update weights)
					Generalizer hypothesis = board.calHypothesis(examples.getHistory(), etha, upTOdown, numerator);

					// 4. record updated weights done each game
					updatedWEIGHTS.add(hypothesis.getupdated_W());

					// 5. Experiment Generator
					board.init(dim, hypothesis.getupdated_W());

					i++;

				}

			} else {
				for (int i = 0; i < limit; i++) {
					
					System.out.printf("Iteration %d%n", i + 1);
					// 1. Performance System (play game)
					PerformanceSystem game = board.play(learner_user, learner_learner, learner_random);

					// 2. Critic (get tuples prepared)
					Critic examples = board.getTrainedExamples(game.getHistory(), game.getResult());

					// 3. Generalizer (update weights)
					Generalizer hypothesis = board.calHypothesis(examples.getHistory(), etha, upTOdown, numerator);

					// 4. record updated weights done each game
					updatedWEIGHTS.add(hypothesis.getupdated_W());

					// 5. Experiment Generator
					board.init(dim, hypothesis.getupdated_W());
					
					if (learner_user.equalsIgnoreCase("1")){
						// stop by user's request
						if (sc.hasNextLine()){
							if (sc.nextLine().equalsIgnoreCase("stop")){
								break;
							}
						}
					}
				}
			}

			// display weights
			for (int i = 0; i < updatedWEIGHTS.size(); i++) {
				for (double d : updatedWEIGHTS.get(i)) {
					System.out.printf("%.3f ", d);
				}
				System.out.println();
			}

		} else if (train_or_test.equalsIgnoreCase("test")) {

			String w0 = args[1];
			String w1 = args[2];
			String w2 = args[3];
			String w3 = args[4];
			String w4 = args[5];
			String w5 = args[6];
			String w6 = args[7];

			String learner_user = "1", learner_learner = "0", learner_random = "0";

			double limit = 5;

			double win = 0, loss = 0, tie = 0;

			// the board dimension
			int dim = 3;

			Board board = new Board();
			// after 30 times player with opponent
			// double[] w = new double[] {31.448, 23.261, 8.708, 7.281, -20.324,
			// 61.346, 0.100};
			// weights that are learned by two machines (both learners)
			// double[] w = new double[]{60.658, 9.836, 4.113, -4.113, -15.234,
			// 0.000, 0.100 };

			// weights that are given by user
			double[] w = new double[] { Double.valueOf(w0), Double.valueOf(w1), Double.valueOf(w2), Double.valueOf(w3),
					Double.valueOf(w4), Double.valueOf(w5), Double.valueOf(w6) };

			// double[] w = new double[] {-8.767, -12.782, -7.509, -8.852,
			// -25.255, 54.021, 0.100 };

			for (int i = 0; i < limit; i++) {

				System.out.printf("Iteration %d%n", i + 1);

				// 0. New problem (Initial game board)
				board.init(dim, w);

				// 1. Performance System (play game)
				// true/false >>> With/Without Teacher

				PerformanceSystem game = board.play(learner_user, learner_learner, learner_random);

				switch (game.result) {
				case 1:
					win++;
					break;
				case 2:
					loss++;
					break;
				default:
					tie++;
					break;
				}

				System.out.printf("The precentage of win: %.2f %% %n", (win / limit) * 100);
				System.out.printf("The percentage of loss: %.2f %% %n", (loss / limit) * 100);
				System.out.printf("The percentage of tie: %.2f %% %n", (tie / limit) * 100);
				
				// stop by user's request
				if (sc.hasNextLine()){
					if (sc.nextLine().equalsIgnoreCase("stop")){
						break;
					}
				}
			}

		}

	}

	private static ArrayList<PerformanceSystem> readfromFILE(double initweight) {
		// read each game and add it to the list

		ArrayList<PerformanceSystem> GAMES = new ArrayList<PerformanceSystem>();
		int counter = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {

			String line = reader.readLine();

			do {
				ArrayList<Board> h = new ArrayList<Board>();
				counter++;

				while (!line.equals("")) {
					int pos1 = Integer.valueOf(line.substring(0, 1));
					int pos2 = Integer.valueOf(line.substring(1, 2));
					int mark = (line.substring(2, 3)).equalsIgnoreCase("x") ? 1 : 2; // either
																						// 1
																						// or
																						// 2
																						// for
																						// x
																						// and
																						// o

					Board b = new Board();

					if (counter == 1) {
						double[] ws = new double[b.getdim_weights()];
						for (int j = 0; j < ws.length; j++) {
							ws[j] = initweight;
						}
						b.setWeight(ws);
					}

					if (!h.isEmpty()) {
						b.override(h.get(h.size() - 1));
					}

					b.setItem(pos1, pos2, mark);
					b.updateFeatures();
					b.defineStat();
					double V_tmp = b.cal_V();
					b.setV_hat(V_tmp);

					h.add(b);
					line = reader.readLine();
				}

				PerformanceSystem game = new PerformanceSystem(h);

				GAMES.add(game);
				line = reader.readLine();
			} while (!line.equalsIgnoreCase("EOF")); // end of file

		} catch (IOException e) {
			e.printStackTrace();
		}

		return GAMES;
	}

	public static void writetofile(ArrayList<double[]> argin) {
		try (

				BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {

			String content = "This is the content to write into file\n";
			bw.write(content);
			// no need to close it.
			// bw.close();
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
