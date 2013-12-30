import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;



public class GreetingServer extends Thread {
	   private ServerSocket serverSocket;
	   private MyPanel panel;
	   private double x_delta = -1;
	   private double y_delta = -1;
	   private double rot = -1;
	   
	   
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
	   						if(x_delta == -1){
	   							x_delta = json.getDouble("x");
	   						}
	   						float aux = (float) (Constants.x_robInit+((json.getDouble("x")-x_delta)*Constants.width)/Constants.alpha);
	   						arg.x = (int)aux;
	   					}
	   					if(json.has("y")){
	   						if(y_delta == -1){
	   							y_delta = json.getDouble("y");
	   						}
	   						float aux = (float) (Constants.y_robInit+((json.getDouble("y")-y_delta)*Constants.height)/Constants.alpha);
	   						arg.y = (int)aux;
	   						
	   					}
	   					if(json.has("rotate")){
	   						if(rot == -1){
	   							rot = json.getDouble("rotate");
	   						}
	   						arg.rot = (int) (json.getDouble("rotate")-rot);
	   						if(arg.rot > 180)
	   							arg.rot -= 360;
	   				    	if(arg.rot < -180)
	   				    		arg.rot += 360;
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
