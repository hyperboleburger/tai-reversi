package game;

import java.util.Scanner;

import game.Reversi.BestMove;
import treeStructure.Node;

public class PlayReversi {
	public static void main(String[] args) {

		Reversi game = new Reversi();

		boolean playerVsAI = true;
		boolean goodInput = false;
		double timeLimit = 0;

		Scanner reader = new Scanner(System.in);
		do {
			System.out.println("Player vs. Computer? (y/n)");
			String answer = reader.nextLine();
			if (answer.equals("y")) {
				playerVsAI = true;
				goodInput = true;
			} else if (answer.equals("n")) {
				playerVsAI = false;
				goodInput = true;
			} else {
				System.out.println("Bad input! Try again.");
			}
		} while (goodInput == false);

		goodInput = false;

		do {
			System.out
					.println("What is the time limit? (Decimals allowed, in seconds)");
			String output = reader.nextLine();
			try{
				timeLimit = Double.parseDouble(output.toString());
				goodInput = true;
			}catch(Exception E){
				System.out.println("Bad input! Try again.");
			}
		} while (goodInput == false);

		boolean gameOver = false;
		
		game.printBoard();
		
		//Human vs Computer
		if (playerVsAI == true) {
			while (gameOver == false) {
				int activePlayer = game.getActivePlayer();
				
				// Human turn
				if (activePlayer == 1) {
					goodInput = false;
					do {
						System.out
								.println("Player turn: Make a move! (Form: XY)");
						String answer = reader.nextLine();
						int x = answer.charAt(0) - '1';
						int y = answer.charAt(1) - 'a';
						int[][] currentBoard = game.getState().getData();
						currentBoard = game.legalMove(currentBoard, x, y,
								activePlayer);
						if (currentBoard != null) {
							game.setBoard(currentBoard);
							goodInput = true;
						} else {
							System.out.println("Illegal move! Try again.");
						}
					} while (goodInput == false);
				
				// Computer turn
				} else {
					System.out.println("Computer (O) is thinking..");
					Node<int[][]> currentState = game.getState();
					long startTime = System.nanoTime();
					long endTime = System.nanoTime();
					
					// Calculate best move using Minimax with alpha-beta pruning
					int depth = 1;
					BestMove bMove;
					BestMove lastBMove = null;
					do{
						bMove = game.alphaBeta(currentState, activePlayer, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, startTime, timeLimit);
						if(bMove.timedOut() == true){
							bMove = lastBMove;
						}else{
							depth++;
						}
						endTime = System.nanoTime();
						lastBMove = bMove;
					}while(((endTime-startTime)/1000000000.0 < timeLimit));
					
					if(bMove == null){
						reader.close();
						throw new RuntimeException("Computer timed out before making a move");
					}
					System.out.println("Computer (O) executed move with depth " + depth + "\n");
					game.setState(bMove.getBestNode());
				}
				
				game.printBoard();
				game.changePlayer();
				gameOver = game.isGameOver();
			}
			int winner = game.calcWinner();
			if(winner > 0){
				System.out.println("X (BLACK) wins!");
			}else if (winner < 0){
				System.out.println("O (WHITE) wins!");
			}else{
				System.out.println("It's a tie!");
			}
			reader.close();
			return;
		}
		
		//Computer vs Computer
		else {
			while (gameOver == false) {
				int activePlayer = game.getActivePlayer();
				Node<int[][]> currentState = game.getState();
				
				// Calculate best move using Minimax
				int depth = 1;
				BestMove bMove = null;
				BestMove lastBMove = null;
				
				if(activePlayer == 1){
					System.out.println("Computer (X) is thinking..");					
				}else{
					System.out.println("Computer (O) is thinking..");
				}
				
				long startTime = System.nanoTime();
				long endTime = System.nanoTime();
				do{
					bMove = game.alphaBeta(currentState, activePlayer, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, startTime, timeLimit);
					if(bMove.timedOut() == true){
						bMove = lastBMove;
					}else{
						depth++;
					}
					endTime = System.nanoTime();
					lastBMove = bMove;
				}while(((endTime-startTime)/1000000000.0 < timeLimit));
				if(bMove == null){
					reader.close();
					throw new RuntimeException("Computer timed out before making a move.");
				}
				
				//The following prints will produce interesting information up until the game is almost over, at
				//which point the number of possible moves left will be fewer than the greatest depth achievable 
				//in a search, resulting in ridiculously great "depths".
				if(activePlayer == 1){
					System.out.println("Computer (X) executed move with depth " + depth + "\n");					
				}else{
					System.out.println("Computer (O) executed move with depth " + depth + "\n");
				}
				game.setState(bMove.getBestNode());
				
				game.printBoard();
				game.changePlayer();
				gameOver = game.isGameOver();
			}
		}
		int winner = game.calcWinner();
		if(winner > 0){
			System.out.println("X (BLACK) wins!");
		}else if (winner < 0){
			System.out.println("O (WHITE) wins!");
		}else{
			System.out.println("It's a tie!");
		}
		reader.close();
		return;
	}
}
