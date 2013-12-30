import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;



public class GreetingServer extends Thread {
	   private ServerSocket serverSocket;
	   private MyPanel panel;
	   
	   public GreetingServer(int port, MyPanel _panel) throws IOException
	   {
		   panel = _panel;
	      serverSocket = new ServerSocket(port);
	      ///serverSocket.setSoTimeout(10000);
	   }

	   public void run()
	   {
		  Socket server = null;
		try {
			server = serverSocket.accept();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	      while(true)
	      {
	         try
	         {
	            
	           
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            Arg arg = new Arg();
	            try {
					JSONObject json = new JSONObject(in.readUTF());
					if(json.getBoolean("x")){
						arg.x = json.getInt("x");
					}
					if(json.getBoolean("y")){
						arg.x = json.getInt("y");
					}
					if(json.getBoolean("rotate")){
						arg.rot = json.getInt("rotate");
					}
					if(json.getBoolean("left")){
						float l = json.getLong("left");
						l = (4-l)*5;
						arg.left = l;
					}
					if(json.getBoolean("right")){
						float r = json.getLong("right");
						r = (4-r)*5;
						arg.right = r;
					}
					if(json.getBoolean("center")){
						float c = json.getLong("center");
						c = (4-c)*5;
						arg.center = c;
					}
					if(json.getBoolean("beacon")){
						arg.beacon = json.getLong("beacon");
					}
					panel.updateBuffer(arg);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	         
	         
	            /*
	            System.out.println(in.readUTF());
	            DataOutputStream out =
	                 new DataOutputStream(server.getOutputStream());
	            out.writeUTF("Thank you for connecting to "
	              + server.getLocalSocketAddress() + "\nGoodbye!");*/
	          
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
	      try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
}
