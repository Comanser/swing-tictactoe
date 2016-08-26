package games.tictactoe;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

/**
 * @author Mario Misiuna 
 * 
 * TicTacToe game model with implemented MiniMax AI algorithm
 * Computer never loose and win if it is possible.
 * 
 */

enum Status { EMPTY, PLAYER_X, PLAYER_O, DRAW, IN_PROGRESS }

class ModelUtils {
	private ModelUtils() {}
	
	public static Status switch_player(Status player) {
		if (player == Status.PLAYER_X)
	        return Status.PLAYER_O;
	    else
	    	return Status.PLAYER_X;
	}
}

/**
 * Move holder 
 */
class Move implements Comparable<Move> {
	private final int row;
	private final int col;
	private int score;
	
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "( " + getRow() + ", " + getCol() + " ): " + getScore();
	}

	/*
	 * Method allows to sort list of Move object
	 */
	@Override
	public int compareTo(Move o) {
		return score < o.score ? -1 : ( score == o.score ? 0 : 1 );
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
	private Status startingPlayer = Status.PLAYER_X;
	
	/**
	 * Initialize empty standard ([3, 3]) Tic Tac Toe game board
	 */
	public TTTBoard() {
		this(3);
	}

	/**
	 * Initialize empty board with the given dimension [dim, dim]
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
	 * Place player sign (PLAYERX or PLAYERO) on the board
	 * at position (row, col) if pointed board grid is not empty
	 * @param row
	 * @param col
	 * @param player
	 */
	protected void move(int row, int col, Status player) {
		if (gridStatus[row][col] == Status.EMPTY) {
			gridStatus[row][col] = player;
		}
	}
	
	/**
	 * More convenient version of move method 
	 * @param move
	 * @param player
	 */
	public void makeMove(Move move, Status player) {
		move(move.getRow(), move.getCol(), player);
	}
	
	/**
	 * @return the result of the game:
	 * 	- PLAYERX or PLAYERO If relevant won
	 * 	- DRAW If game ended as tie
	 * 	- IN_PROGRESS If game is in progress
	 */
	public Status getGameResult() {
		List<List<Status>> lines = new ArrayList<>();
		List<Status> newLine;
		
		// Add all possible winning lines to the lines list
		
		// Add rows
		for (int row = 0; row < dim; row++) {
			newLine = new ArrayList<>();
			for (int col = 0; col < dim; col++)
				newLine.add(gridStatus[row][col]);
			lines.add(newLine);
		}
		
		// Add columns
		for (int col = 0; col < dim; col++) {
			newLine = new ArrayList<>();
			for (int row = 0; row < dim; row++)
				newLine.add(gridStatus[row][col]);
			lines.add(newLine);
		}
		
		// Add diagonals
		newLine = new ArrayList<>();
		for (int row = 0; row < dim; row++)
			newLine.add(gridStatus[row][row]);
		lines.add(newLine);
		
		newLine = new ArrayList<>();
		for (int row = 0; row < dim; row++)
			newLine.add(gridStatus[dim-row-1][row]);
		lines.add(newLine);
		
		// Check for winner
		for (List<Status> line : lines)
			if (line.get(0) != Status.EMPTY && new HashSet<>(line).size() == 1)
					return line.get(0);
		
		// Check for draw
		if (getPotentialMoves().size() == 0)
			return Status.DRAW;
		else
			return Status.IN_PROGRESS;
	}
	
	public boolean isGameOver() {
		return getGameResult() != Status.IN_PROGRESS; 
	}
	
	/**
	 * 
	 * @return empty grids as list of next potential moves
	 */
	public List<Move> getPotentialMoves() {
		List<Move> potentialMoves = new ArrayList<>();
		
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				if (gridStatus[row][col] == Status.EMPTY) {
					potentialMoves.add(new Move(row, col));
				};
		
		return potentialMoves;
	}

	/**
	 * Get deep copy of TTTBoard excluding gridPanel array
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
				getGameResult();
		for (Status[] status : gridStatus)
			str += "\n" + Arrays.toString(status);
		return str;
	}
	
	// Other shared data related to the board and game
	public JPanel getGridPanel(int col, int row) {
		return gridPanel[row][col];
	}

	public void assignPanelToGrid(int col, int row, JPanel jp) {
		gridPanel[row][col] = jp;
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

	public Status getStartingPlayer() {
		return startingPlayer;
	}

	public void setStartingPlayer(Status startingPlayer) {
		this.startingPlayer = startingPlayer;
	}
}

/**
 * Scoring values for MiniMax algorithm 
 */
class MiniMaxScores {
	Map<Status, Integer> scores;
	
	public MiniMaxScores() {
		scores = new HashMap<>();
		scores.put(Status.PLAYER_X, 1);
		scores.put(Status.DRAW, 0);
		scores.put(Status.PLAYER_O, -1);
	}
	
	public int getScore(Status status) {
		return scores.get(status);
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
	 * given board and the second element is the desired move as a Move object
	 */
	public static Move getMiniMaxMove(TTTBoard board, Status player) throws InterruptedException {
		MiniMaxScores miniMaxScores = new MiniMaxScores();
		List<Move> moves = new ArrayList<>();
		
		List<Move> potentialMoves = board.getPotentialMoves();
		Collections.shuffle(potentialMoves);
		
		Move move, dummyMove;
		TTTBoard boardClone;
		Status gameResult;
		int score;
		while (potentialMoves.size() > 0) {
			if(Thread.currentThread().isInterrupted()) throw new InterruptedException();
			move = potentialMoves.remove(potentialMoves.size()-1);
			boardClone = board.clone();
			boardClone.makeMove(move, player);
			gameResult = boardClone.getGameResult();
			if (gameResult != Status.IN_PROGRESS) {
				score = miniMaxScores.getScore(gameResult);
				if (score * miniMaxScores.getScore(player) == 1) {
					move.setScore(score);
					return move;  
				}
			} else {
				dummyMove = getMiniMaxMove(boardClone, ModelUtils.switch_player(player));
				score = dummyMove.getScore();
				move.setScore(score);
				if (score * miniMaxScores.getScore(player) == 1) {
					return move;  
				}
			}
			moves.add(move);
		}
		// Choose appropriate move based on current player
		moves.sort(null);
		if (player == Status.PLAYER_X) {
			// set move to one with max score 
			move = moves.get(moves.size() - 1);
		} else {
			// set move to one with min score 
			move = moves.get(0);
		}
		//System.out.println(moves);
		return move;
	}
	
	/**
	 * @param board
	 * @param player
	 * @return winning move for given board and player or null if not exists
	 */
	public static Move getNextWinningMove(TTTBoard board, Status player) {
		List<Move> potentialMoves = board.getPotentialMoves();
		TTTBoard boardClone;
		Move move;
		
		while (potentialMoves.size() > 0) {
			move = potentialMoves.remove(potentialMoves.size()-1);
			boardClone = board.clone();
			boardClone.makeMove(move, player);
			if (boardClone.getGameResult() == player) return move;
		}
		
		return null;
	}
	
	/**
	 * Make random move for given player
	 *  
	 * @param board
	 * @param player
	 * @return random move for given player or null if not possible
	 */
	public static Move makeNextRandomMove(TTTBoard board, Status player) {
		List<Move> potentialMoves = board.getPotentialMoves();
		
		System.out.println(potentialMoves);
		if (potentialMoves.size() > 0) {
			Collections.shuffle(potentialMoves);
			Move pickedMove = potentialMoves.get(0);
			board.makeMove(pickedMove, player);
			return pickedMove;
		}
		return null;
	}
	
	/**
	 * Make next move for given board and player 
	 * 
	 * @param board
	 * @param player
	 * @return next move for given board and player or null if not possible
	 * @throws InterruptedException
	 */
	public static Move makeNextMove(TTTBoard board, Status player) throws InterruptedException {
		Move move;
		if (board.isGameOver()) return null;
		
		// Make winning move if exists
		move = getNextWinningMove(board, player);
		if (move != null) {
			board.makeMove(move, player);
			return move;
		}
		
		// Speed up finding move for boards with dimension > 3 and 
		// first move for board with dim = 3
		if (board.getPotentialMoves().size() > 8) {
			move = getNextWinningMove(board, ModelUtils.switch_player(player));
			if (move != null) { // Block opponent winning move if exists
				board.makeMove(move, player);
				return move;
			} else { // or if not exists
				return makeNextRandomMove(board, player);
			}
		}
		
		// Make minimax move
		move = getMiniMaxMove(board, player);
		board.makeMove(move, player);
		return move;
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Basic TTTBoard tests:");
		TTTBoard b = new TTTBoard();
		System.out.println("dimension = " + b.getDimension());
		System.out.println(b);
		
		TTTBoard c = b.clone(); c.move(0, 0, Status.PLAYER_X);
		System.out.println("\nC "+c);
		System.out.println("\nB "+b);
		
		for (int i = 0; i < b.getDimension(); i++)
			for (int j = 0; j < b.getDimension(); j++)
				b.move(i, j, Status.PLAYER_X);
		System.out.println("\nB "+b);
		System.out.println("Potential moves: " + b.getPotentialMoves());
		System.out.println("Is game over? " + b.isGameOver());
			
		for (int i = 0; i < b.getDimension(); i++)
			for (int j = 0; j < b.getDimension(); j++)
				b.move(i, j, Status.values()[new Random().nextInt(Status.values().length)]);
		System.out.println("\nB "+b);
		System.out.println("\nC "+c);
		System.out.println("Potential moves: " + c.getPotentialMoves());
		System.out.println("Is game over? " + c.isGameOver());
		System.out.println("Get MiniMaxMove: " + getMiniMaxMove(c, Status.PLAYER_X));
	}
}
