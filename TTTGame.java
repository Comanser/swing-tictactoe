package games.tictactoe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author Mario Misiuna
 * General GUI design idea based on Bruce Eckiel
 * TicTacToe.java printed in Thinking in Java book
 */

class Utilities {
	private Utilities() {};
	
	public static void showResult(Status status) {
		String message = "";
		switch (status) {
		case DRAW:
			message = "We have a Tie!";
			break;
		case PLAYER_O:
			message = "Computer Won!";
			break;
		case PLAYER_X:
			message = "Player Won!";
			break;
		default:
			break;
		}

		JOptionPane.showMessageDialog(null,
	            message, "Komunikat", JOptionPane.INFORMATION_MESSAGE);
	}
}
/**
 * Thread used to finding out the best computer move
 */
class ComputerMove implements Runnable {
	private static int counter = 0;
	private final int id = counter++;
	private TTTBoard board; // current TTTBoard

	public ComputerMove(TTTBoard board) {
		this.board = board;
	}

	public void run() {
		System.out.println(this + " I'm thinking ...");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(this + " interrupted");
			return;
		}
		// Add game logic for computer move
		Move move = board.doNextRandomMove();
		if (move != null) {
			board.getGridPanel(move.getRow(), move.getCol()).repaint();
			System.out.println(board);
			Status gameStatus = board.getGameStatus();
			if (gameStatus != Status.IN_PROGRESS) {
				Utilities.showResult(gameStatus);
				board.reset();
				board.repaint();
			}
		}
		board.setTurn(Status.PLAYER_X);
		System.out.println(this + " finished");
	}

	public String toString() {
		return "Task " + id;
	}

	public long id() {
		return id;
	}
}

/**
 * Game board window
 */
class BoardWindow extends JDialog {
	// Start with cross
	private TTTBoard board;
	ExecutorService executor = Executors.newSingleThreadExecutor();

	// Initialize game board
	BoardWindow(JFrame parent, int dim) {
		super(parent, "Gameplay", true);

		// Initialize board model
		board = new TTTBoard(dim);

		// Setup active grids as JPanels in GridLayout Dialog window
		setLayout(new GridLayout(dim, dim));
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				BoardGrid bg = new BoardGrid(i, j);
				board.assignePanelToGrid(i, j, bg);
				add(bg);
			}
		}

		// ((limitMax - limitMin) * (baseMax - value) / (baseMax - baseMin)) + limitMin;
		int boxSize = (int) ((15 * (6 - dim) / 3) + 90);

		setSize(dim * boxSize, dim * boxSize);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Shutdown computer move computation thread
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				executor.shutdownNow();
				e.getWindow().dispose();
			}
		});
	}
	
	/**
	 * Board grid
	 */
	public class BoardGrid extends JPanel {
		private final int row, col;

		// Initialize board grid
		BoardGrid(int row, int col) {
			// Assign board coordinates to this grid
			this.col = col; this.row = row;
			
			// Make grid responsive to mouse click
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// Add game logic for user
					if (board.getGridStatus(row, col) == Status.EMPTY && board.getTurn() == Status.PLAYER_X) {
						board.makeMove(row, col, Status.PLAYER_X); System.out.println(board);
						repaint(); // sign marked by move made above
						Status gameStatus = board.getGameStatus();
						if (gameStatus != Status.IN_PROGRESS) {
							Utilities.showResult(gameStatus);
							board.reset();
							board.repaint();
							return;
						}
						board.setTurn(Status.PLAYER_O);
						if (executor.isShutdown()) {
							System.out.println("Start new queue if executor was shutdown for eny reason");
							executor = Executors.newSingleThreadExecutor();
						}
						// Schedule computer move computation
						ComputerMove task = new ComputerMove(board);
						executor.execute(task);
						System.out.println(task + " schedulled");
					}
				}
			});
		}

		// Draw grid 
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// Draw grid border
			int cornerX = 1, cornerY = 1;
			int width = getSize().width - 2, height = getSize().height - 2;
			
			g.drawRect(cornerX, cornerY, width, height);

			// Draw sign based on grid status
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			cornerX = width / 4; cornerY = height / 4;
			width = width / 2; height = height / 2;
			
			if (board.getGridStatus(row, col) == Status.PLAYER_X) {
				g.drawLine(cornerX, cornerY, cornerX + width, cornerY + height);
				g.drawLine(cornerX, cornerY + height, cornerX + width, cornerY);
			} else if (board.getGridStatus(row, col) == Status.PLAYER_O) {
				//g.setColor(Color.BLUE);
				g.drawOval(cornerX, cornerY, cornerX + width / 2, cornerY + height / 2);
			}
		}
	}
}

/**
 * Main Tic Tac Toe game window 
 */
public class TTTGame {
	private int dim = 3, cols = 3;

	/**
	 * Input data panel
	 * Code is based on Oracle sample
	 */
	class SpinnerPanel extends JPanel {
		public SpinnerPanel() {
			super(new SpringLayout());

			String[] labels = { "Board dimension: " };
			int numPairs = labels.length;

			// Add the first labeled spinner
			SpinnerModel rowsModel = new SpinnerNumberModel(dim, dim - 0, dim + 3, 1);
			JSpinner rowsSpinner = addLabeledSpinner(this, labels[0], rowsModel);

			// Lay out the panel: rows, initX, initY, xPad, yPad
			SpringUtilities.makeCompactGrid(this, numPairs, 2, 10, 10, 6, 10);

			rowsSpinner.addChangeListener((e) -> dim = (int) rowsModel.getValue());
		}

		protected JSpinner addLabeledSpinner(Container c, String label, SpinnerModel model) {
			JLabel l = new JLabel(label);
			c.add(l);

			JSpinner spinner = new JSpinner(model);
			l.setLabelFor(spinner);
			c.add(spinner);

			return spinner;
		}
	}

	// Initialize main window
	public TTTGame() {
		JFrame frame = new JFrame("Tic Tac Toe Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add input data panel
		frame.add(new SpinnerPanel());

		// Define button to start the game
		JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener((e) -> {
			JDialog board = new BoardWindow(null, dim);
			board.setVisible(true);
		});

		// Define button to exit the game
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener((e) -> System.exit(0));

		// Add buttons
		frame.add(btnExit, BorderLayout.WEST);
		frame.add(btnNewGame, BorderLayout.EAST);

		// Display the main window
		frame.pack();
		frame.setVisible(true);
	}

	// Run the game
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TTTGame();
			}
		});
	}
}
