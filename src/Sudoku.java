import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class Sudoku extends Canvas implements Runnable, MouseListener {
	private static final long serialVersionUID = -1969447397438808410L;
	private boolean running;
	private Thread thread;
	KeyInput ki;
	private int[][] arr;
	private int[] selectedBox;
	private boolean clicked = false;
	private int blockWidth;
	private int blockHeight;
	private int[] pair;
	List<List<Integer>> allPairs;
	private boolean same = false;
	private int[][] newArr; // temp board
	private int i = 0;
	private int checkValid = 0;
	private BufferedImage image;
	
	public Sudoku() {
		new Main(this);
		addMouseListener(this);
		if(gameState == STATE.MENU) {
	        URL resource = getClass().getResource("menu.png");
	        try {
	            image = ImageIO.read(resource);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	public void initBoard() {
		if(gameState == STATE.PLAYING) {
			ki = new KeyInput();
			this.addKeyListener(ki);
			selectedBox = new int[2];
			blockWidth = this.getWidth() / 9;
			blockHeight = this.getHeight() / 9;
			allPairs = new ArrayList<List<Integer>>();
			arr = new int[][] {{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},
							   {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},
							   {-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1},{-1,-1,-1,-1,-1,-1,-1,-1,-1}};
			populateBoard();	
		}
	}
	
	public enum STATE {
		MENU,
		PLAYING
	}
	
	public STATE gameState = STATE.MENU;
	
	// Initialization of board
	public void populateBoard() {
		arr[0][3] = 7;
		arr[0][4] = 9;
		arr[0][7] = 5;
		arr[1][0] = 3;
		arr[1][1] = 5;
		arr[1][2] = 2;
		arr[1][5] = 8;
		arr[1][7] = 4;
		arr[2][7] = 8;
		arr[3][1] = 1;
		arr[3][4] = 7;
		arr[3][8] = 4;
		arr[4][0] = 6;
		arr[4][3] = 3;
		arr[4][5] = 1;
		arr[4][8] = 8;
		arr[5][0] = 9;
		arr[5][4] = 8;
		arr[5][7] = 1;
		arr[6][1] = 2;
		arr[7][1] = 4;
		arr[7][3] = 5;
		arr[7][6] = 8;
		arr[7][7] = 9;
		arr[7][8] = 1;
		arr[8][1] = 8;
		arr[8][4] = 3;
		arr[8][5] = 7;
	}
	
	public int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		if(gameState == STATE.MENU) {
			if(e.getX() >= 237 && e.getX() <= 470 && e.getY() >= 334 && e.getY() <= 380) {
				gameState = STATE.PLAYING;
				initBoard();
			}
			if(e.getX() >= 237 && e.getX() <= 470 && e.getY() >= 400 && e.getY() <= 446) {
				System.exit(0);
			}
		}
		else {
			selectedBox[0] = e.getX();
			selectedBox[1] = e.getY();
			pair = getBlockFromMouse(selectedBox[0], selectedBox[1]);
			clicked = true;
			allPairs.add(new ArrayList<Integer>());
			allPairs.get(i).add(pair[0]);
			allPairs.get(i).add(pair[1]);	
			if(allPairs.size() > 1 && allPairs.get(i-1).equals(allPairs.get(i))) {
				same = true;
			}
			else {
				same = false;
			}
			i++;			
		}		
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
		if(gameState == STATE.PLAYING) {
			newArr = new int[9][9];
			for(int c=0; c<arr.length; c++) {
				for(int d=0; d<arr.length; d++) {
					newArr[c][d] = arr[c][d];
				}
			}
			if(ki.getEnter()) {
				if(ki.getNum() != "") {
					newArr[pair[0]][pair[1]] = Integer.parseInt(ki.getNum());	
					if(isValid(newArr)) {
						arr = newArr;
						checkValid = 2;
					}
					else {
						checkValid = 1;
					}
				}
				ki.setEnter(false);
			}
			if(ki.getDelete() && pair != null) {
				if(arr[pair[0]][pair[1]] != -1) {
					ki.setNum("");
					arr[pair[0]][pair[1]] = -1;
				}
				ki.setDelete(false);
			}	
		}
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
	 * Searches through rows and columns of 2D list to find duplicate values
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
		
		if(gameState == STATE.MENU) {
			g.drawImage(image, 0, 0, this);
		}
		if(gameState == STATE.PLAYING) {
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
			
			// Draw selected boxes onto screen
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 35)); 
			int boxType = 0;
			if(clicked) {
				g.setColor(Color.GREEN);

				if((pair[0] == 2 && pair[1] == 2) || (pair[0] == 5 && pair[1] == 2) || (pair[0] == 2 && pair[1] == 5) || (pair[0] == 5 && pair[1] == 5)) {
					g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-2, blockHeight-2);		
					boxType = 1;
				}
				else if(pair[1] == 2 || pair[1] == 5) {
					g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-1, blockHeight-2);		
					boxType = 2;
				}
				else if(pair[0] == 2 || pair[0] == 5) {
					g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-2, blockHeight-1);		
					boxType = 3;
				}
				else {
					g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-1, blockHeight-1);		
					boxType = 4;
				}
				
				if(checkValid == 1) { // Not valid
					if(same) {
						g.setColor(Color.RED);
					}
					else {
						g.setColor(Color.GREEN);
						checkValid = 0;
					}
					if(boxType == 1) {
						g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-2, blockHeight-2);							
					}
					if(boxType == 2) {
						g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-1, blockHeight-2);							
					}
					if(boxType == 3) {
						g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-2, blockHeight-1);							
					}
					if(boxType == 4) {
						g.fillRect(pair[0] * blockWidth+1, pair[1] * blockHeight+1, blockWidth-1, blockHeight-1);							
					}
				}
				
				g.setColor(Color.BLACK);
				if(same) {
					g.drawString(ki.getNum(), pair[0] * blockWidth + 29,  pair[1] * blockHeight + 42);
				}
				else {
					ki.setNum("");
					g.drawString(ki.getNum(), pair[0] * blockWidth + 29,  pair[1] * blockHeight + 42);	
					same = true;
				}
			}

			for(int row=0; row<9; row++) {
				for(int col=0; col<9; col++) {
					if(arr[row][col] != -1) {
						g.drawString(arr[row][col] + "", row * blockWidth + 29,  col * blockHeight + 42);											
					}
				}
			}	
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
