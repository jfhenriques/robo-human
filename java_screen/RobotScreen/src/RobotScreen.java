
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
	   final MyPanel panel = new MyPanel();
	   
	   int port = 5555;
	   try
	   {
		   Thread t = new GreetingServer(port, panel);
		   t.start();

	   }catch(IOException e){
		   e.printStackTrace();
	   }
	   JFrame frame = new JFrame("Meu primeiro frame em Java");
      // frame.setSize(400,400);
     
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
			//panel.updateBuffer();
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			/*case KeyEvent.VK_LEFT:
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
			case KeyEvent.VK_A:
				panel.updateBuffer( 500, 500);
				break;
			case KeyEvent.VK_L:
				panel.detectObstacl(MyPanel.ObstacleSensor.RIGHT, 20);
				break;
			case KeyEvent.VK_R:
				Constants.rot_rob += 3;
				if(Constants.rot_rob >= 180)
					Constants.rot_rob -= 360;
				break;
			case KeyEvent.VK_T:
				panel.detectBeacon(50);
				break;*/
			case KeyEvent.VK_Q:
				Arg arg = new Arg();
				arg.x = Constants.x_rob+30;
				arg.y = Constants.y_rob+3;
				arg.rot = Constants.rot_rob+180;
				arg.left = 20;
				arg.right= 20;
				arg.center = 40;
				arg.beacon = 50;
				panel.updateBuffer(arg);
				
			}
			
			
			
		}
	});
   frame.pack();
   frame.setVisible(true);
  
   }
   
   
}

