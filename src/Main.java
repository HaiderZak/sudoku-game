//Zak Haider

import javax.swing.JFrame;

public class Main{
	public Main(Sudoku sudoku) {
		JFrame frame = new JFrame("Sudoku Solver by Zak Haider");
        frame.pack();
		frame.setSize(710,580);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(sudoku);
		frame.setVisible(true);
		sudoku.start();
	}
}
