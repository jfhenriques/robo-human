import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;



public class GreetingServer extends Thread {
	   private ServerSocket serverSocket;
	   private MyPanel panel;
	   
	   
	   private static final boolean LOG_INPUT = true;
	   
	   
	   private class ServerProcessor extends Thread
	   {
		   private Socket _server = null;
		   private DataInputStream _in;
		   private PrintWriter _pw = null;
		   
		   public ServerProcessor(Socket server)
		   {
			   System.out.println("Received new connection");
			   this._server = server;
			   try {
				   _pw =  new PrintWriter("input_log.txt", "UTF-8");
				   
				   if( LOG_INPUT )
					   _in = new DataInputStream(_server.getInputStream());
			   } catch (IOException e) {
				   e.printStackTrace();
				   _in = null;
			   }
		   }
		   
		   private void _do_cleanup()
		   {
			   if( LOG_INPUT && _pw != null )
				   _pw.close();
			   
			   
				try {
					if( _server != null )
						_server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		   }
           
           	
           	public void run()
           	{

           		byte[] buffer = new byte[8192];
           		String str;
           		int b,counter;

           		
           		while(true)
           		{
	   	            Arg arg = new Arg();
	   	            try {
	   	            	//String t =_in.readUTF();
	   	            	
	   	            	counter = 0;
	   	            	
	   	            	while(true)
	   	            	{
	   	            		b =_in.read();
	   	            		if( b < 0 )
	   	            		{
	   	            			_do_cleanup();
	   	            			return;
	   	            		}
	   	            		
	   	            		if( b > 0 )
	   	            			buffer[counter++] = (byte)b;
	   	            		else
	   	            			break;
	   	            	}
	   	            	
	   	            	if( counter == 0 )
	   	            		continue;
	   	            	
	   	            	str = new String(buffer, 0, counter);
	   	            	_pw.println(str);
	   	            	
	   					JSONObject json = new JSONObject(str);
	   					if(json.has("x")){
	   						arg.x = (int)json.getDouble("x");
	   					}
	   					if(json.has("y")){
	   						arg.x = (int)json.getDouble("y");
	   					}
	   					if(json.has("rotate")){
	   						arg.rot = (int)json.getDouble("rotate");
	   					}
	   					if(json.has("left")){
	   						float l = (float)json.getDouble("left");
	   						l = (4-l)*5;
	   						arg.left = l;
	   					}
	   					if(json.has("right")){
	   						float r = (float)json.getDouble("right");
	   						r = (4-r)*5;
	   						arg.right = r;
	   					}
	   					if(json.has("center")){
	   						float c = (float)json.getDouble("center");
	   						c = (4-c)*5;
	   						arg.center = c;
	   					}
	   					if(json.has("beacon")){
	   						arg.beacon = json.getLong("beacon");
	   					}
	   					
	   					
	   					
	   					
	   					panel.updateBuffer(arg);
	   					
	   				} catch (JSONException e) {
	   					// TODO Auto-generated catch block
	   					e.printStackTrace();
	   				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
           		}
           		
           	}

	   }
	   
	   
	   
	   
	   public GreetingServer(int port, MyPanel _panel) throws IOException
	   {
		   panel = _panel;
	      serverSocket = new ServerSocket(port);
	      ///serverSocket.setSoTimeout(10000);
	   }

	   public void run()
	   {
	      while(true)
	      {
	         try
	         {
	            
	            Socket server = serverSocket.accept();
	            
	            (new ServerProcessor(server)).start();
	         
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
	   }
}
