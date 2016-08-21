package games.tictactoe;

import java.util.*;

/**
 * @author Mario Misiuna 
 */

enum Status { EMPTY, PLAYER_X, PLAYER_O, DRAW, IN_PROGRESS }

/**
 * Move holder 
 */
class Move {
	private final int row;
	private final int col;
	Status score;
	
	public Move (int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Status getScore() {
		return score;
	}

	public void setScore(Status score) {
		this.score = score;
	}
}

/*
 * Tic-Tac-Toe board game representation
 */
class TTTBoard implements Cloneable {
	private final int dim;
	private Status[][] array;
	private Status turn = Status.PLAYER_X;
	
	/**
	 * Initialize empty standard ([3, 3]) Tic Tac Toe game board
	 */
	public TTTBoard() {
		this(3);
	}

	/**
	 * Initialize the empty board with the given dimension [dim, dim]
	 */
	public TTTBoard(int dim) {
		this.dim = dim;
		array = new Status[dim][dim];
		
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				array[row][col] = Status.EMPTY;				
	}

	/**
	 * Copy constructor
	 */
	public TTTBoard(TTTBoard board) {
		dim = board.dim;
		array = new Status[dim][];
		
		for (int row = 0; row < dim; ++row)
			array[row] = Arrays.copyOf(board.array[row], dim);
	}

	/**
	 * 
	 * @return the dimension of the board.
	 */
	public int getDimension() {
		return dim;
	}
	
	// Only for testing
	public Status[][] getArray() {
		return array;
	}

	/**
	 * @param row
	 * @param col
	 * @return the status (EMPTY, PLAYERX, PLAYERO) of the grid at position (row, col)
	 */
	public Status getGridStatus(int row, int col) {
		return array[row][col];
	}
	
	/**
	 * 
	 * @return empty grids as list of Move objects - next move options
	 */
	public List<Move> getEmptyGrids() {
		List<Move> emptyGrids = new ArrayList<>();
		
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				if (array[row][col] == Status.EMPTY) {
					emptyGrids.add(new Move(row, col));
				};
		
		return emptyGrids;
	}
	
	/**
	 * Do random move if possible
	 */
	public void nextMove() {
		List<Move> moves = getEmptyGrids();
		if (moves.size() > 0) {
			long seed = System.nanoTime();
			Collections.shuffle(moves, new Random(seed));
			moves.get(0);
		}
	}
	
	/**
	 * Place player (PLAYERX or PLAYERO) on the board at position (row, col)
	 * if pointed board grid is not empty
	 * @param row
	 * @param col
	 * @param player
	 */
	public void move(int row, int col, Status player) {
		if (array[row][col] == Status.EMPTY) {
			array[row][col] = player;
		}
	}
	
	/**
	 * @return the status of the game":
	 * 	- player (PLAYERX or PLAYERO) If any won
	 * 	- DRAW If game is a draw
	 * 	- IN_PROGRESS If game is in progress
	 */
	public Status getGameStatus() {
		// !!! Not yet implemented !!!
		if (getEmptyGrids().size() == 0) {
			return Status.DRAW;
		} else
			return Status.IN_PROGRESS;
	}
	
	public boolean isGameOver() {
		return getGameStatus() == Status.DRAW; 
	}

	/**
	 * Get deep copy of TTTBoard
	 */
	@Override
	public TTTBoard clone() {
		try {
			TTTBoard board = (TTTBoard) super.clone();
			board.array = new Status[dim][];
			for (int i = 0; i < dim; ++i)
				board.array[i] = Arrays.copyOf(array[i], dim);
			return board;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Should not happen
		}
	}
	
	/**
	 * Human readable board representation
	 */
	@Override public String toString() {
		String str = "Board (dim " + dim + "): "; 
		for (Status[] status : array)
			str += "\n" + Arrays.toString(status);
		return str;
	}
	
	/**
	 * @return current game turn
	 */
	public Status getTurn() {
		return turn;
	}

	/**
	 * Holding current game turn
	 * @param turn
	 */
	public void setTurn(Status turn) {
		this.turn = turn;
	}
}

/*
 * Tic Tac Toe AI algorithm
 */
public class TTTModel {
	private TTTModel() {
	}

	/*
	 * @param
	 * 
	 * @return an object with 2 elements. The first element is the score of the
	 * given board and the second element is the desired move as a Grid object
	 */
	public static void MinimaxMove() {

	}

	public static void main(String[] args) {
		System.out.println("Basic TTTBoard tests:");
		TTTBoard b = new TTTBoard();
		System.out.println("dimension = " + b.getDimension());
		System.out.println(b);
		
		TTTBoard c = b.clone(); c.getArray()[0][0] = Status.PLAYER_X;
		System.out.println("B "+b); System.out.println(b.getGameStatus());
		System.out.println("C "+c); System.out.println(c.getGameStatus());
		
		for (int i = 0; i < c.getDimension(); i++)
			for (int j = 0; j < c.getDimension(); j++)
				c.getArray()[i][j] = Status.PLAYER_X;
		System.out.println("C "+c); System.out.println(c.getGameStatus());

		b.move(1, 1, Status.PLAYER_O);
		System.out.println("B "+b); System.out.println(b.getGameStatus());
	}
}
