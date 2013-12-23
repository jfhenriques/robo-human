
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RobotScreen
{
	

   public static void main(String [] args) throws IOException
   {
	   
	   
	   
	  /* int port = 2004;
	   try
	   {
		   Thread t = new GreetingServer(port);
		   t.start();

	   }catch(IOException e){
		   e.printStackTrace();
	   }*/
	   JFrame frame = new JFrame("Meu primeiro frame em Java");
      // frame.setSize(400,400);
       final MyPanel panel = new MyPanel();
       frame.add(panel);
       panel.setFocusable(true);
       panel.requestFocusInWindow();
       panel.addKeyListener(new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			panel.updateBuffer();
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("pres");
			switch(e.getKeyCode()){
			case KeyEvent.VK_LEFT:
				Constants.x_rob -= 2;
				break;
			case KeyEvent.VK_RIGHT:
				Constants.x_rob += 2;
				break;
			case KeyEvent.VK_DOWN:
				Constants.y_rob += 2;
				break;
			case KeyEvent.VK_UP:
				Constants.y_rob -= 2;
				break;
			}
			
		}
	});
   frame.pack();
   frame.setVisible(true);
  
   }
   
   
}

