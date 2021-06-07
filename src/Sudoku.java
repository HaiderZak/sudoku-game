import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Sudoku extends Canvas implements Runnable, MouseListener {
	private boolean running;
	private Thread thread;
	KeyInput ki;
	private int[][] arr;
	private int[] selectedBox;
	private boolean clicked = false;
	private int blockWidth;
	private int blockHeight;
	private int[] pair;
	
	public Sudoku() {
		new Main(this);
		arr = new int[9][9];
		addMouseListener(this);
		selectedBox = new int[2];
		blockWidth = this.getWidth() / 9;
		blockHeight = this.getHeight() / 9;
		ki = new KeyInput();
		this.addKeyListener(ki);
		int[][] lst = new int[][] {{1,-1,-1},{2,3,5},{2,5,6}};
		System.out.println(isValid(lst));
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		selectedBox[0] = e.getX();
		selectedBox[1] = e.getY();
		clicked = true;
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				delta--;
			}
			render();
	
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
			
		}
		stop();			
	}
	
	public void tick() {
	}
	
	/*
	 * Algorithm to find duplicates in a 1-dimensional integer list
	 * @param inputArray
	 * @return boolean
	 */
	private boolean findDuplicates(int[] inputArray)
	{
	    for(int i=0; i<inputArray.length; i++) {
	    	for(int j=i+1; j<inputArray.length; j++) {
	    		if(inputArray[i] != -1 && inputArray[i] == inputArray[j]) {
	    			return true;		
	    		}
	    	}
	    }
	    return false;
	}
	
	
	/*
	 * Searches through rows and columns of 2D list to search for duplicate values
	 * @param lst Current integers on board
	 * @return boolean
	 */
	public boolean isValid(int[][] lst) {
		int l = 0;
		for(int i=0; i<lst.length; i++) {
			int[] colList = new int[lst.length];
			for(int j=0; j<lst.length; j++) {
				colList[l] = lst[j][i];
				l++;
			}
			if(findDuplicates(colList)) {
				return false;
			}
			l=0;
		}
		int g = 0;
		for(int i=0; i<lst.length; i++) {
			int[] rowList = new int[lst[i].length];
			for(int j=0; j<lst[i].length; j++) {
				rowList[g] = lst[i][j];
				g++;
			}
			if(findDuplicates(rowList)) {
				return false;
			}
			g=0;
		}
		return true;
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		// DRAW BOARD
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 710, 580);
		
		g.setColor(Color.BLACK);
		int j = 0;
		for(int i=0; i<=this.getWidth(); i+=blockWidth) {
			if(j == 3 || j == 6) {
				drawThickLine(g, i, 0, i, this.getHeight(), 2, Color.BLACK);
			}
			else {
				g.setColor(Color.BLACK);
				g.drawLine(i, 0, i, this.getHeight());				
			}
			j++;
		}
		j = 0;
		for(int i=0; i<=this.getHeight(); i+=blockHeight) {
			if(j == 3 || j == 6) {
				drawThickLine(g,0,i,this.getWidth(),i,2,Color.BLACK);
			}
			else {
				g.setColor(Color.BLACK);
				g.drawLine(0,i,this.getWidth(),i);
			}
			j++;
		}
		
		//
		
		int x = selectedBox[0];
		int y = selectedBox[1];
		pair = getBlockFromMouse(x,y);
		g.setColor(Color.GREEN);
		if(clicked) {
			g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-1, blockHeight-1);									
		}
		
		
		g.dispose();
		bs.show();
	}
	
	public int[] getBlockFromMouse(int x, int y){
		return new int[] {x / blockWidth, y / blockHeight};
	}
	
	//https://www.rgagnon.com/javadetails/java-0260.html => For drawing thicker lines in Java Graphics
	 public void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
			  // The thick line is in fact a filled polygon
			  g.setColor(c);
			  int dX = x2 - x1;
			  int dY = y2 - y1;
			  // line length
			  double lineLength = Math.sqrt(dX * dX + dY * dY);

			  double scale = (double)(thickness) / (2 * lineLength);

			  // The x,y increments from an endpoint needed to create a rectangle...
			  double ddx = -scale * (double)dY;
			  double ddy = scale * (double)dX;
			  ddx += (ddx > 0) ? 0.5 : -0.5;
			  ddy += (ddy > 0) ? 0.5 : -0.5;
			  int dx = (int)ddx;
			  int dy = (int)ddy;

			  // Now we can compute the corner points...
			  int xPoints[] = new int[4];
			  int yPoints[] = new int[4];

			  xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
			  xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
			  xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
			  xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;

			  g.fillPolygon(xPoints, yPoints, 4);
	 }
	
	public static void main(String[] args) {
		new Sudoku();
	}
}
