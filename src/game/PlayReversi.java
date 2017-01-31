package game;

import java.util.ArrayList;

import game.Reversi.BestMove;
import treeStructure.Node;

public class PlayReversi {
	public static void main(String[] args) {
		
		Reversi game = new Reversi();
		game.printBoard();
		
		boolean gameOver = false;
		Node<int[][]> currentState = game.getState();
		int depth = 5;
		
		while(gameOver == false){
			int activePlayer = game.getActivePlayer();
			BestMove bMove = game.miniMax(currentState, activePlayer ,depth);
			game.setState(bMove.getBestNode());
			game.printBoard();
		}
		return;
	}
}
