package games.tictactoe;

import java.util.*;
import javax.swing.JPanel;

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
	private Status score;
	
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
	private Status[][] gridStatus;
	private JPanel[][] gridPanel;
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
		gridStatus = new Status[dim][dim];
		gridPanel = new JPanel[dim][dim];
		
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				gridStatus[row][col] = Status.EMPTY;				
	}

	/**
	 * Copy constructor
	 */
	public TTTBoard(TTTBoard board) {
		dim = board.dim;
		gridPanel = new JPanel[dim][dim];
		gridStatus = new Status[dim][];
		
		for (int row = 0; row < dim; ++row)
			gridStatus[row] = Arrays.copyOf(board.gridStatus[row], dim);
	}

	/**
	 * Reset board to start new game on this same board
	 */
	public void reset() {
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				gridStatus[row][col] = Status.EMPTY;
	}
	
	/**
	 * Repaint whole board
	 */
	public void repaint() {
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				gridPanel[row][col].repaint();
	}

	/**
	 * 
	 * @return the dimension of the board.
	 */
	public int getDimension() {
		return dim;
	}
	
	/**
	 * @param row
	 * @param col
	 * @return the status (EMPTY, PLAYERX, PLAYERO) of the grid at position (row, col)
	 */
	public Status getGridStatus(int row, int col) {
		return gridStatus[row][col];
	}
	
	/**
	 * 
	 * @return empty grids as list of Move objects - next move options
	 */
	public List<Move> getEmptyGrids() {
		List<Move> emptyGrids = new ArrayList<>();
		
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				if (gridStatus[row][col] == Status.EMPTY) {
					emptyGrids.add(new Move(row, col));
				};
		
		return emptyGrids;
	}
	
	/**
	 * Do random move if possible
	 */
	public Move doNextRandomMove() {
		List<Move> moves = getEmptyGrids();
		if (moves.size() > 0) {
			//long seed = System.nanoTime();
			//Collections.shuffle(moves, new Random(seed));
			Collections.shuffle(moves);
			Move move = moves.get(0);
			makeMove(move.getRow(), move.getCol(), getTurn());
			return move;
		}
		return null;
	}
	
	/**
	 * Place player (PLAYERX or PLAYERO) on the board at position (row, col)
	 * if pointed board grid is not empty
	 * @param row
	 * @param col
	 * @param player
	 */
	public void makeMove(int row, int col, Status player) {
		if (gridStatus[row][col] == Status.EMPTY) {
			gridStatus[row][col] = player;
		}
	}
	
	/**
	 * @return the status of the game":
	 * 	- PLAYERX or PLAYERO If relevant won
	 * 	- DRAW If game ended as tie
	 * 	- IN_PROGRESS If game is in progress
	 */
	public Status getGameStatus() {
		List<List<Status>> lines = new ArrayList<>();
		List<Status> line;
		
		// Add rows
		for (int row = 0; row < dim; row++) {
			line = new ArrayList<>();
			for (int col = 0; col < dim; col++)
				line.add(gridStatus[row][col]);
			lines.add(line);
		}
		
		// Add columns
		for (int col = 0; col < dim; col++) {
			line = new ArrayList<>();
			for (int row = 0; row < dim; row++)
				line.add(gridStatus[row][col]);
			lines.add(line);
		}
		
		// Add diagonals
		line = new ArrayList<>();
		for (int row = 0; row < dim; row++)
			line.add(gridStatus[row][row]);
		lines.add(line);
		
		line = new ArrayList<>();
		for (int row = 0; row < dim; row++)
			line.add(gridStatus[dim-row-1][row]);
		lines.add(line);
		
		// Check for winner
		for (List<Status> ln : lines)
			if (ln.get(0) != Status.EMPTY && new HashSet<>(ln).size() == 1)
					return ln.get(0);
		
		// Check for draw
		if (getEmptyGrids().size() == 0)
			return Status.DRAW;
		else
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
			board.gridStatus = new Status[dim][];
			for (int i = 0; i < dim; ++i)
				board.gridStatus[i] = Arrays.copyOf(gridStatus[i], dim);
			return board;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Should not happen
		}
	}
	
	/**
	 * Human readable board representation
	 */
	@Override public String toString() {
		String str = "Board (dim " + dim + "): " +
				getGameStatus();
		for (Status[] status : gridStatus)
			str += "\n" + Arrays.toString(status);
		return str;
	}
	
	// Other shared data related to the board and game
	public JPanel getGridPanel(int col, int row) {
		return gridPanel[row][col];
	}

	public void assignePanelToGrid(int col, int row, JPanel jp) {
		gridPanel[row][col] = jp;
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
		
		TTTBoard c = b.clone(); c.makeMove(0, 0, Status.PLAYER_X);
		System.out.println("\nB "+b);
		System.out.println("\nC "+c);
		
		for (int i = 0; i < c.getDimension(); i++)
			for (int j = 0; j < c.getDimension(); j++)
				c.makeMove(i, j, Status.PLAYER_X);
		System.out.println("\nC "+c);

		b.makeMove(1, 1, Status.PLAYER_O);
		System.out.println("\nB "+b);
	}
}
