//Zak Haider

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	private int num;
	private boolean enter = false;
	
	public KeyInput() {
		
	}
	
	public int getNum() {
		return num;
	}
	
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
       
        if(key == KeyEvent.VK_0 || key == KeyEvent.VK_9 || key == KeyEvent.VK_8 || key == KeyEvent.VK_7 || key == KeyEvent.VK_6 
        		|| key == KeyEvent.VK_5 || key == KeyEvent.VK_4 || key == KeyEvent.VK_3 || key == KeyEvent.VK_2 || key == KeyEvent.VK_1) {
        	num = Integer.parseInt(KeyEvent.getKeyText(key));
        }
        if(key == KeyEvent.VK_ENTER) {
        	enter = true;
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_ENTER) {
        	enter = false;
        }
    }
}
