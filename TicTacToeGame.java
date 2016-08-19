package games.tictactoe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * Game board window
 */
class GameBoard extends JDialog {
	private enum State { EMPTY, PLAYERX, PLAYERO }
	
	// Start with cross
	private State turn = State.PLAYERX;

	// Initialize game board
	GameBoard(JFrame parent, int rows, int cols) {
		super(parent, "Play", true);
		
		// Setup active grids in panel using GridLayout 
		setLayout(new GridLayout(rows, cols));  
		for (int i = 0; i < cols * rows; i++)
			add(new Grid());
		
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
	class Grid extends JPanel {
		private State state = State.EMPTY;

		// Initialize board grid
		public Grid() {
			// Make grid responsive to mouse click
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// Add game logic to mouse grid click
					if (state == State.EMPTY) {
						state = turn;
						turn = (turn == State.PLAYERX ? State.PLAYERO : State.PLAYERX);
						repaint();
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
			if (state == State.PLAYERX) {
				g.drawLine(x1, y1, x1 + wide, y1 + high);
				g.drawLine(x1, y1 + high, x1 + wide, y1);
			}
			if (state == State.PLAYERO)
				g.drawOval(x1, y1, x1 + wide / 2, y1 + high / 2);
		}
	}
}

/*
 * Game main window
 */
public class TicTacToeGame {
	private int rows = 3, cols = 3;
	
	class SpinnerPanel extends JPanel {
	    public SpinnerPanel() {
	        super(new SpringLayout());

	        String[] labels = {"Rows: ", "Columns: "};
	        int numPairs = labels.length;

	        //Add the first labeled spinner
	        SpinnerModel rowsModel = new SpinnerNumberModel(rows, rows - 0, rows + 3, 1);
	        JSpinner rowsSpinner = addLabeledSpinner(this, labels[0], rowsModel);

	        //Add second labeled spinner
	        SpinnerModel colsModel = new SpinnerNumberModel(cols, cols - 0, cols + 3, 1);
	        JSpinner colsSpinner = addLabeledSpinner(this, labels[1], colsModel);


	        //Lay out the panel: rows, cols, initX, initY, xPad, yPad
	        SpringUtilities.makeCompactGrid(this, numPairs, 2, 10, 10, 6, 10);
	        
	        rowsSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					rows = (int) rowsModel.getValue();
				}
	        });
	        
	        colsSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					cols = (int) colsModel.getValue();
				}
	        });
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

	public TicTacToeGame() {
		// Initialize main window
		JFrame frame = new JFrame("Tic Tac Toe Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up input data panel
		frame.add(new SpinnerPanel());
			
		// Create button to start the game
		JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog board = new GameBoard(null, rows, cols);
				board.setVisible(true);
			}
		});
		
		// Create button to exit the game
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
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
				new TicTacToeGame();
			}
		});
	}
}
