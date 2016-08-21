package games.tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

/**
 * @author Mario Misiuna 
 */

/**
 *  Thread used to finding best computer move
 */
class ComputerMove implements Runnable {
	private static int counter = 0;
	private final int id = counter++;
	private TTTBoard board;

	/**
	 * @param board - current TTTBoard
	 */
	public ComputerMove(TTTBoard board) {
		this.board = board;
	}

	public void run() {
		System.out.println(this + " I'm thinking ...");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			System.out.println(this + " interrupted");
			return;
		}
		//board.nextMove();
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

/*
 * Game board window
 */
class BoardWindow extends JDialog {
	// Start with cross
	private TTTBoard board;
	ExecutorService executor = Executors.newSingleThreadExecutor();


	// Initialize game board
	BoardWindow(JFrame parent, int rows, int cols) {
		super(parent, "Gameplay", true);
		
		// Initialize board model
		board = new TTTBoard();
		
		// Setup active grids as panels in GridLayout Dialog window
		setLayout(new GridLayout(rows, cols));  
		for (int i = 0; i < cols * rows; i++)
			add(new BoardGrid());
		
		//((limitMax - limitMin) * (value - baseMin) / (baseMax - baseMin)) + limitMin;
		//int boxSize = (int) ( (15. * (Math.min(gridsWide, gridsHigh) - 3. ) / 3. ) + 90. );
		
		// Scale boxSize
		//((limitMax - limitMin) * (baseMax - value) / (baseMax - baseMin)) + limitMin;
		int boxSize = (int) ( (15 * (6 - Math.min(cols, rows)) / 3 ) + 90 );

		setSize(cols * boxSize, rows * boxSize);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/*
	 * Board grid
	 */
	class BoardGrid extends JPanel {
		private Status status = Status.EMPTY;

		// Initialize board grid
		public BoardGrid() {
			// Make grid responsive to mouse click
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// Add game logic
					if (status == Status.EMPTY && board.getTurn() == Status.PLAYER_X) {
						status = Status.PLAYER_X;
						board.setTurn(Status.PLAYER_O);
						repaint();
						if (executor.isShutdown()) {
							System.out.println("Start new queue");
							executor = Executors.newSingleThreadExecutor();
						}
						ComputerMove task = new ComputerMove(board);
						executor.execute(task);
						System.out.println(task + " schedulled");
					}
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Draw grid border
			int x1 = 1, y1 = 1;
			int x2 = getSize().width - 2, y2 = getSize().height - 2;
			g.drawRect(x1, y1, x2, y2);
			
			// Draw sign based on grid state
			x1 = x2 / 4; y1 = y2 / 4;
			int wide = x2 / 2, high = y2 / 2;
			if (status == Status.PLAYER_X) {
				g.drawLine(x1, y1, x1 + wide, y1 + high);
				g.drawLine(x1, y1 + high, x1 + wide, y1);
			}
			if (status == Status.PLAYER_O)
				g.drawOval(x1, y1, x1 + wide / 2, y1 + high / 2);
		}
	}
}

/*
 * Main Tic Tac Toe game window
 */
public class TTTGame {
	private int rows = 3, cols = 3;
	
	/*
	 * Input data panel setup
	 */
	class SpinnerPanel extends JPanel {
	    public SpinnerPanel() {
	        super(new SpringLayout());

	        String[] labels = {"Board dimension: "};
	        int numPairs = labels.length;

	        //Add the first labeled spinner
	        SpinnerModel rowsModel = new SpinnerNumberModel(rows, rows - 0, rows + 3, 1);
	        JSpinner rowsSpinner = addLabeledSpinner(this, labels[0], rowsModel);

	        //Lay out the panel: rows, initX, initY, xPad, yPad
	        SpringUtilities.makeCompactGrid(this, numPairs, 2, 10, 10, 6, 10);
	        
	        rowsSpinner.addChangeListener( (e) -> rows = (int) rowsModel.getValue());
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
			
		// Add button to start the game
		JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener( (e) -> {
			JDialog board = new BoardWindow(null, rows, rows);
			board.setVisible(true);
		});
		
		// Add button to exit the game
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener( (e) -> System.exit(0) );
		
		frame.add(btnExit, BorderLayout.WEST);
		frame.add(btnNewGame, BorderLayout.EAST);
		
		// Display the main window
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Run the game
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TTTGame();
			}
		});
	}
}
