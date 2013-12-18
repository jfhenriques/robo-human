
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
       
       frame.add(new MyPanel());
       frame.pack();
       frame.setVisible(true);

       
	   
   }
   
   
}

