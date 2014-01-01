import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;



public class GreetingServer extends Thread {

	private Socket _server = null;
	private MyPanel _panel;
	private DataInputStream _in;
	private PrintWriter _pw = null;
	private Constants _const = new Constants();
	
	private int _id;

	private static final boolean LOG_INPUT = true;



	public GreetingServer(Socket server, MyPanel panel, Constants constants, int id)
	{
		System.out.println("Received new connection");
		
		this._server = server;
		this._panel = panel;
		this._id = id;
		this._const = constants;
		
		try {
			_pw =  new PrintWriter("input_log_ " + id + ".txt", "UTF-8");

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
		
		if( _in == null )
			return;


		while(true)
		{
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

				if( LOG_INPUT )
					_pw.println(str);
				
				
				Arg arg = processJSON(_const, new JSONObject(str));
				
				if( arg == null )
					break;

				_panel.updateBuffer(arg);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		_do_cleanup();
		System.out.println("Exiting naturally: " + _id);

	}
	
	
	public static Arg processJSON(Constants constants, JSONObject json) throws JSONException
	{
		Arg arg = new Arg();
		double dAux;
		
		if(json.has("state"))
			arg.state = json.getInt("state");
		
		if( arg.state == 5 ) // Finished
			return null;
		
		
		if(json.has("x"))
		{
			dAux = json.getDouble("x");
			
			if(constants.x_delta == null)
				constants.x_delta = dAux;

			arg.x = (int)(constants.x_robInit + ((dAux - constants.x_delta) * constants.robot_delta_displacement));
		}
		
		if(json.has("y"))
		{
			dAux = json.getDouble("y");
			
			if(constants.y_delta == null)
				constants.y_delta = dAux;

			arg.y = (int)(constants.y_robInit + ((dAux - constants.y_delta) * constants.robot_delta_displacement));

		}
		
		if(json.has("rotate"))
		{
			dAux = json.getDouble("rotate");
			
			if(constants.rot_delta == null)
				constants.rot_delta = dAux;

			arg.rot = (float) (dAux - constants.rot_delta);
			
			if(arg.rot > 180.0f)
				arg.rot -= 360.0f;
			else
			if(arg.rot < -180.0f)
				arg.rot += 360.0f;
		}
		
		if(json.has("left"))
		{
			dAux = json.getDouble("left");
			dAux = (4.0 - dAux) * 5.0;
			
			arg.left = (float) dAux;
		}
		
		if(json.has("right"))
		{
			dAux = json.getDouble("right");
			dAux = (4.0 - dAux) * 5.0;
			
			arg.right = (float) dAux;
		}
		
		if(json.has("center"))
		{
			dAux = json.getDouble("center");
			dAux = (4.0 - dAux) * 5.0;
			
			arg.center = (float) dAux;
		}
		
		if(json.has("beacon"))
		{
			arg.beacon = (float) json.getDouble("beacon");
		}
	
		
		return arg;
	}


}
