
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JFrame;



public class RobotScreen
{



	public static void main(String [] args) throws IOException
	{

		int windowID = 0;

		ServerSocket serverSocket = new ServerSocket(Constants.PORT);
		
		System.out.println("Listening Server started");
		System.out.println("Wating for connections...");

		while(true)
		{
			try
			{
				Socket server = serverSocket.accept();
				int id = ++windowID;
				
				Constants c = new Constants();
				MyPanel panel = new MyPanel(c);
				JFrame frame = new JFrame("Robot Screen View [" + id + "]");

				frame.add(panel);
				panel.setFocusable(true);
				panel.requestFocusInWindow();

				frame.pack();
				frame.setVisible(true);

				(new GreetingServer(server, panel, c, id)).start();


			}catch(SocketTimeoutException s)
			{
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		serverSocket.close();
	}


}

