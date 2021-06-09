//Zak Haider

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	private String num = "";
	private boolean enter = false;
	private boolean delete = false;
	
	public KeyInput() {
		
	}
	
	public boolean getEnter() {
		return enter;
	}
	
	public void setEnter(boolean e) {
		this.enter = e;
	}
	
	public boolean getDelete() {
		return delete;
	}
	
	public void setDelete(boolean d) {
		this.delete = d;
	}
	
	public String getNum() {
		return num;
	}
	
	public void setNum(String num) {
		this.num = num;
	}
	
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
       
        if(key == KeyEvent.VK_0 || key == KeyEvent.VK_9 || key == KeyEvent.VK_8 || key == KeyEvent.VK_7 || key == KeyEvent.VK_6 
        		|| key == KeyEvent.VK_5 || key == KeyEvent.VK_4 || key == KeyEvent.VK_3 || key == KeyEvent.VK_2 || key == KeyEvent.VK_1) {
        	num = KeyEvent.getKeyText(key);
        }
        if(key == KeyEvent.VK_ENTER) {
        	enter = true;
        }
        if(key == KeyEvent.VK_BACK_SPACE) {
        	delete = true;
        }
    }
}
