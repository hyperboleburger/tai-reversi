package game;

import treeStructure.Node;

public class Reversi {
	private int activePlayer;
	private Node<int[][]> gameState;
	

	public Reversi(){
		activePlayer = 1; //Black starts
		int[][] board = new int[8][8];
		board[3][3] = -1;
		board[3][4] = 1;
		board[4][3] = 1;
		board[4][4] = -1;
		gameState = new Node<int[][]>(board);
	}
	
	public Node<int[][]> getState(){
		return gameState;
	}
	
	public void setState(Node<int[][]> newState){
		gameState = newState;
	}
	
	public void setBoard(int[][] newBoard){
		gameState.removeChildren();
		gameState.setData(newBoard);
	}
	
	public int getActivePlayer(){
		return activePlayer;
	}
	
	public int changePlayer(){
		if(activePlayer == 1) activePlayer = -1;
		else activePlayer = 1;
		return activePlayer;
	}
	
	
	// Attempts to make a move at x,y. Returns the updated board
	// if successful, else null.
	public int[][] legalMove(int[][] currentBoard, int x, int y, int currentPlayer) {
		if (currentBoard[x][y] != 0){
			return null;
		}
		boolean legal = false;
		int[][] copyBoard = currentBoard;
		int flipped = 0;
		int dx = 0;
		int dy = 0;

		// Investigates each direction for possible flips
		for (int i = 0; i < 8; i++) {
			int tempx = x;
			int tempy = y;
			switch (i) {
			case 1:
				dx = 1;
				dy = 1;
				break;
			case 2:
				dx = 1;
				dy = 0;
				break;
			case 3:
				dx = 1;
				dy = -1;
				break;
			case 4:
				dx = 0;
				dy = -1;
				break;
			case 5:
				dx = -1;
				dy = -1;
				break;
			case 6:
				dx = -1;
				dy = 0;
				break;
			case 7:
				dx = -1;
				dy = 1;
				break;
			case 8:
				dx = 0;
				dy = 1;
				break;
			}

			boolean run = true;
			while (run) {
				tempx += dx;
				tempy += dy;
				if (tempx < 0 || tempx > 7 || tempy < 0 || tempy > 7)
					run = false;
				else if (copyBoard[tempx][tempy] == 0)
					run = false;
				else if (copyBoard[tempx][tempy] != currentPlayer)
					flipped++;
				else if (flipped != 0) {
					while (flipped != 0) {
						tempx -= dx;
						tempy -= dy;
						copyBoard[tempx][tempy] = currentPlayer;
						flipped--;
					}
					legal = true;
					run = false;
				} else
					run = false;
			}
		}
		if (legal == true)
			return copyBoard;
		else
			return null;
	}

	// activePlayer = 1: MAXIMIZING, -1: MINIMIZING
	// Risky implementation: expanding nodes in algorithm
	public BestMove miniMax(Node<int[][]> node, int activePlayer, int depth) {
		BestMove bMove = new BestMove();
		expandNode(node, activePlayer);
		if (depth == 0 || node.getChildren().isEmpty()) {
			bMove.value = utility(node);
			bMove.node = node;
			return bMove;
		}
		
		if (activePlayer == 1) {
			bMove.value = Integer.MIN_VALUE;
			bMove.node = node;
			expandNode(node, activePlayer);
			for (Node<int[][]> n : node.getChildren()) {
				int v = miniMax(n, depth - 1, -1).value;
				if(v > bMove.value){
					bMove.value = v;
					bMove.node = n;
				}
			}
			return bMove;
		} else {
			bMove.value = Integer.MAX_VALUE;
			expandNode(node, activePlayer);
			for (Node<int[][]> n : node.getChildren()) {
				int v = miniMax(n, depth - 1, 1).value;
				if(v < bMove.value){
					bMove.value = v;
					bMove.node = n;
				}
			}
			return bMove;
		}
	}
	
	protected class BestMove{
		private int value;
		private Node<int[][]> node;
		
		private BestMove(){
			value = 0;
			node = new Node<int[][]>(null);
		}
		
		public int getBestValue(){
			return value;
		}
		
		public Node<int[][]> getBestNode(){
			return node;
		}
	}

	private void expandNode(Node<int[][]> node, int activePlayer) {
		int[][] currentBoard = node.getData();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int[][] childBoard = legalMove(currentBoard, x, y, activePlayer);
				if (childBoard != null) {
					node.addChild(childBoard);
				}
			}
		}
	}

	private static int parityHeuristic(Node<int[][]> node) {
		int nbrBlk = 0;
		int nbrWht = 0;
		int[][] board = node.getData();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board[x][y] == 1)
					nbrBlk++;
				else if (board[x][y] == -1)
					nbrWht++;
			}
		}
		return 100 * (nbrBlk - nbrWht) / (nbrBlk + nbrWht);
	}

	private static int cornerHeuristic(Node<int[][]> node) {
		int nbrBlk = 0;
		int nbrWht = 0;
		int[][] board = node.getData();
		if (board[0][0] == 1)
			nbrBlk++;
		else if (board[0][0] == -1)
			nbrWht++;
		if (board[7][0] == 1)
			nbrBlk++;
		else if (board[7][0] == -1)
			nbrWht++;
		if (board[7][7] == 1)
			nbrBlk++;
		else if (board[7][7] == -1)
			nbrWht++;
		if (board[0][7] == 1)
			nbrBlk++;
		else if (board[0][7] == -1)
			nbrWht++;

		if (nbrBlk + nbrWht != 0) {
			return 100 * (nbrBlk - nbrWht) / (nbrBlk + nbrWht);
		} else
			return 0;
	}

	private static int utility(Node<int[][]> node) {
		return parityHeuristic(node) + cornerHeuristic(node);
	}

	public void printBoard() {
		int[][] board = gameState.getData();
		StringBuilder output = new StringBuilder("  a b c d e f g h \n");
		for (int i = 0; i < 8; i++) {
			output.append(i + 1);
			for (int j = 0; j < 8; j++) {
				output.append(" ");
				if (board[i][j] == -1) {
					output.append("O");
				} else if (board[i][j] == 1) {
					output.append("X");
				} else {
					output.append("-");
				}
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}

}
