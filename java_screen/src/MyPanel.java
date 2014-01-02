import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.json.JSONException;
import org.json.JSONObject;




class MyPanel extends JPanel{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private Constants _const;
	//static int[][] buffer = new int[Constants.height][Constants.width];
	BufferedImage bufferedImage = null;
	Image robo_1;
	Image robo_2;
	
	float robo_width = -1;
	float robo_height = -1;
	
	float half_robo_width = 0;
	float half_robo_height = 0;
	
	boolean rob_size_acquired = false;
	
	int state;
	static public enum ObstacleSensor  {LEFT, CENTER, RIGHT}
	
	
	private ArrayList<Float> angle1 = new ArrayList<Float>();
	private ArrayList<Float> angle2 = new ArrayList<Float>();
	private ArrayList<Float> dist = new ArrayList<Float>();
	private ArrayList<Integer> sensorX = new ArrayList<Integer>();
	private ArrayList<Integer> sensorY = new ArrayList<Integer>();
	
	
    public MyPanel(Constants constants)
    {
    	setBorder(BorderFactory.createLineBorder(Color.black));
    	
    	_const = constants;
    	bufferedImage = new BufferedImage(_const.width, _const.height, BufferedImage.TYPE_INT_RGB);

    	for(int i = 0; i < bufferedImage.getHeight(); i++)
    	{
        	for(int j = 0; j < bufferedImage.getWidth(); j++)
    			bufferedImage.setRGB(j, i,(new Color(0, 0, 0)).getRGB());
        }
    	robo_1 = Toolkit.getDefaultToolkit().getImage("rob_run.png");
    	robo_2 = Toolkit.getDefaultToolkit().getImage("rob_return.png");
    	
    	updateBuffer();
    	state = 1;
    }


    public Dimension getPreferredSize() {
    	
        return new Dimension(Constants.WINDOW_WIDTH,Constants.WINDOW_HEIGHT);
        
    }
    
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
    	g.setColor(Color.BLACK);
    	g.fillRect(0, 0, getWidth(), getHeight());
        
        Rectangle rect = getBounds();
		AffineTransform at = new AffineTransform();
		AffineTransform atR = new AffineTransform();
		
		Graphics2D g2d = (Graphics2D)g;
		
		int wwCenter = (int) (rect.getWidth()/2),
			whCenter = (int) (rect.getHeight()/2);

		at.translate(wwCenter - _const.x_rob, whCenter - _const.y_rob);
		at.rotate(Math.toRadians(-90.0f + _const.rot_rob), _const.x_rob, _const.y_rob);

		g2d.drawImage(bufferedImage, at, null);
		
		atR.translate( wwCenter - half_robo_width, whCenter - half_robo_height );
		atR.scale(_const.robo_scale, _const.robo_scale);
		
		
        if(state == 4)
        	g2d.drawImage(robo_2, atR, null);
        else 
        	g2d.drawImage(robo_1, atR, null);

    } 
    
    public void updateBuffer()
    {
    	for(int i = 0; i < _const.height; i++)
    	{
        	for(int j = 0; j < _const.width; j++)
        	{
    			if(half_robo_height >= Math.sqrt(
				    					((_const.x_rob-j)*(_const.x_rob-j))+
				    					((_const.y_rob-i)*(_const.y_rob-i))) )
    			{
    				bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
    			}
        	}
        }
    	//this.repaint();
    }
    
    
    private void reset_robot_size()
    {
    	this.robo_width = (float) (this.robo_1.getWidth(this) * _const.robo_scale);
    	this.robo_height = (float) (this.robo_1.getHeight(this) * _const.robo_scale);
    	
    	if( robo_width > 0 && robo_height > 0 )
    	{
    		rob_size_acquired = true;
    		
        	half_robo_width = robo_width/ 2.0f;
        	half_robo_height = robo_height/ 2.0f;
    	}
    }
   

    public void updateBuffer(Arg arg)
    {
    	double ang,dTmp,angRad,angRad05;
    	float fTmpI,fTmpJ,
    		  tRobInitI,tRobInitJ,
    		  fConstI,fConstJ,
    		  argR2,constR2,
    		  tAngle1,tAngle2,thDist,
    		  tSqrtZ,atanYX,atan2ArgXY,
    		  a1Rad,a2Rad,
    		  t5RobHeight,
    		  t5RobHeight1;
    	int x,y,tSensX,tSensY,i,j;
    	
    	t5RobHeight = robo_height * 5.0f;
    	t5RobHeight1 = t5RobHeight -1;
    	
    	if( !this.rob_size_acquired )
    		this.reset_robot_size();
    	
    	//update state 
    	if( arg.state >= 0 )
    		state = arg.state;
    	
    	//update rotate
		if(arg.rot > Float.NEGATIVE_INFINITY)
		{
			_const.rot_rob = arg.rot;
			
			if(_const.rot_rob > 180.0)
				_const.rot_rob -= 360.0;
			else
			if(_const.rot_rob < -180.0)
				_const.rot_rob += 360.0;
		}
		
		ang = _const.rot_rob + arg.beacon;
		
    	if(ang > 180.0)
    		ang -= 360.0;
    	else
    	if(ang < -180.0)
    		ang += 360.0;
    	
    	angRad = Math.toRadians(ang);
    	angRad05 = Math.toRadians(ang-0.5);
    	
    	
		angle1.clear();
		angle2.clear();
		dist.clear();
		sensorX.clear();
		sensorY.clear();
		
		
		if( arg.left > -1 )
		{
			angle1.add(_const.rot_rob + 90.0f);
			angle2.add(_const.rot_rob + 30.0f);
		} else {
			angle1.add(Float.MIN_VALUE);
			angle2.add(Float.MIN_VALUE);
		}
		
		dist.add(arg.left);
		
		dTmp = Math.toRadians(arg.rot + 60);
		sensorX.add((int) (arg.x + Math.cos(dTmp)*half_robo_height));
		sensorY.add((int) (arg.y - Math.sin(dTmp)*half_robo_height));
		
		if(arg.center != -1)
		{
			angle1.add(_const.rot_rob + 30.0f);
			angle2.add(_const.rot_rob - 30.0f);
		} else {
			angle1.add(Float.MIN_VALUE);
			angle2.add(Float.MIN_VALUE);
		}
		
		dist.add(arg.center);
		
		dTmp = Math.toRadians(arg.rot);
		sensorX.add((int) (arg.x + Math.cos(dTmp)*half_robo_height));
		sensorY.add((int) (arg.y - Math.sin(dTmp)*half_robo_height));
		
		if(arg.right != -1)
		{
			angle1.add(_const.rot_rob - 30.0f);
			angle2.add(_const.rot_rob - 90.0f);
		} else {
			angle1.add(Float.MIN_VALUE);
			angle2.add(Float.MIN_VALUE);
		}
		
		dist.add(arg.right);
		
		dTmp = Math.toRadians(arg.rot - 60);
		sensorX.add((int) (arg.x + Math.cos(dTmp)*half_robo_height));
		sensorY.add((int) (arg.y - Math.sin(dTmp)*half_robo_height));
		
		for(i = 0; i < angle1.size(); i++)
		{
			tAngle1 = angle1.get(i);
			tAngle2 = angle2.get(i);
			
			if(tAngle1 >= 180.0f)
	    		angle1.set(i, tAngle1 - 360.0f);
			else
	    	if(tAngle1 <= -180.0f)
	    		angle1.set(i, tAngle1 + 360.0f);
			
	    	if(tAngle2 >= 180.0f)
	    		angle2.set(i, tAngle2 - 360.0f);
	    	else
	    	if(tAngle2 <= -180.0f)
	    		angle2.set(i, tAngle2 + 360.0f);
		}


		
    	for(i = 0; i < _const.height; i++)
    	{
    		fTmpI = (arg.y-i)*(arg.y-i);
    		fConstI = (_const.y_rob-i)*(_const.y_rob-i);
    		
    		tRobInitI = (_const.y_robInit-i) * (_const.y_robInit-i);
    		
        	for(j = 0; j < _const.width; j++)
        	{
        		fTmpJ = (arg.x - j) * (arg.x - j);
        		tRobInitJ = (_const.x_robInit-j) * (_const.x_robInit-j);
        		
        		argR2 = (float) Math.sqrt(fTmpJ + fTmpI);

        		
        		//update route
        		if(    arg.x != -1 
        			&& arg.y != -1 )
        		{
        			
        			if(    Math.abs(arg.x - _const.x_rob) < 5.0 
        				&& Math.abs(arg.y - _const.y_rob) < 5.0 )
        			{
        				//shortest distance
            			if( half_robo_height >= argR2 )
            				bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
            		}
        			else
        			{
        				fConstJ = (_const.x_rob - j) * (_const.x_rob - j);
        				constR2 = (float) Math.sqrt(fConstJ + fConstI);
        				
            			if(     half_robo_height      >= constR2
            				&& (half_robo_height-1.5) <= constR2 )
            				bresenham_algorithm(j, i, (int)(arg.x+(j-_const.x_rob)), (int)(arg.y+(i-_const.y_rob)), new Color(255,255,255));
            		}
        		}
        		
        		//update obstacles
        		for(int z = 0; z < angle1.size(); z++)
        		{
        			tAngle1 = angle1.get(z);
        			
        			if(tAngle1 < -9999.0f)
        				continue;
        			
        			tSensX = sensorX.get(z);
        			tSensY = sensorY.get(z);
        			thDist = dist.get(z)/2.0f;
        			
        			tSqrtZ = (float) Math.sqrt( ((tSensX-j)*(tSensX-j)) + ((tSensY-i)*(tSensY-i)) );
        			
        			y = tSensY-i;
            		x = j-tSensX;
            		
            		if(y == 0) y = 1;
            		if(x == 0) x = 1;
            		
            		if(     thDist    >= tSqrtZ
            			&& (thDist-3) <= tSqrtZ )
            		{
            			tAngle2 = angle2.get(z);
            			
            			a1Rad = (float) Math.toRadians(tAngle1);
            			a2Rad = (float) Math.toRadians(tAngle2);
            			atanYX = (float) Math.atan2(y, x);
            			
            			if(    ( a1Rad >= atanYX && a2Rad <= atanYX )
            				|| (    tAngle2 > 120
            					 && ( a1Rad >= atanYX || a2Rad <= atanYX ) ) )
            				bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
            		}
        		}
        		
        		//update initposition
        		if(10 >= Math.sqrt(tRobInitJ + tRobInitI))
        			bufferedImage.setRGB(j, i,(new Color(0, 255, 255)).getRGB());
        		
        		//update beacon
        		if(arg.beacon != -1)
        		{
        			if(    t5RobHeight  >= argR2
        			    && t5RobHeight1 <= argR2
            		    && angRad   >= (atan2ArgXY = (float) Math.atan2(arg.y-i, j-arg.x))
            		    && angRad05 <= atan2ArgXY )
            			bresenham_algorithm((int)arg.x, (int)arg.y, j, i, new Color(0,255,0));
        		}
        	}
        }
    	
    	//update position
    	if(arg.x != -1 && arg.y != -1)
    	{
    		_const.x_rob = arg.x;
    		_const.y_rob = arg.y;
    	}
    	
    	this.repaint();
    }
    
    //bresenham line algorithm
    private void bresenham_algorithm(int x,int y,int x2, int y2, Color color)
    {
        int w = x2 - x ;
        int h = y2 - y ;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;            
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
        	try {
        		bufferedImage.setRGB(x, y,color.getRGB());
        	} catch(IndexOutOfBoundsException ioe) {
        		
        	}
          //  putpixel(x,y,color) ;
            numerator += shortest ;
            if (!(numerator<longest))
            {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }
    }
    
    
    
    public static void main(String [] args) throws IOException, JSONException
    {
    	Constants c = new Constants();
		JFrame frame = new JFrame("Robot Screen View [" + 0 + "]");
		MyPanel panel = new MyPanel(c);
		
		try
		{
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.add(panel);
			panel.setFocusable(true);
			panel.requestFocusInWindow();
	
			frame.pack();
			frame.setVisible(true);
			
			
			BufferedReader in = new BufferedReader(new FileReader("input_log_good.txt"));
			String line;
	
			while (in.ready())
			{
				line = in.readLine();
				
				Arg arg = GreetingServer.processJSON(c, new JSONObject(line));
				
				if( arg == null )
					break;
	
				panel.updateBuffer(arg);	
			}
			
			in.close();
			
		} finally {

			frame.setVisible(false);
			frame.dispose();
			
		}

    	
    }
    
   
    
}
