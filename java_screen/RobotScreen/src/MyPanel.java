import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;




class MyPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//static int[][] buffer = new int[Constants.height][Constants.width];
	BufferedImage bufferedImage = new BufferedImage(Constants.width,Constants.height, BufferedImage.TYPE_INT_RGB);
	Image robo_1;
	Image robo_2;
	int robo_width;
	int robo_height;
	int state;
	static public enum ObstacleSensor  {LEFT, CENTER, RIGHT}
	
    public MyPanel() {
    	setBorder(BorderFactory.createLineBorder(Color.black));

    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
    			bufferedImage.setRGB(j, i,(new Color(0, 0, 0)).getRGB());
        	}
        }
    	robo_1 = Toolkit.getDefaultToolkit().getImage("rob_run.png");
    	robo_2 = Toolkit.getDefaultToolkit().getImage("rob_return.png");
    	robo_width = 34;
    	robo_height = 34;
    	updateBuffer();
    	state = 1;
    }


    public Dimension getPreferredSize() {
        return new Dimension(Constants.width,Constants.height);
        
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        g.drawImage(bufferedImage, 0, 0, this);
     //   g.drawImage(robo, Constants.x_rob, Constants.y_rob, this);
        AffineTransform at = new AffineTransform();
       
        at.translate(Constants.x_rob+robo_width/2+1-robo_width, Constants.y_rob-robo_height/2+1);
        at.rotate(Math.toRadians(-Constants.rot_rob), robo_width/2, robo_height/2);
        Graphics2D g2d = (Graphics2D) g;
        if(state == 4)
        	g2d.drawImage(robo_2, at, null);
        else 
        	g2d.drawImage(robo_1, at, null);
        
    } 
    
    public void updateBuffer(){
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){

    			if(robo_height/2 >= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
    					((Constants.y_rob-i)*(Constants.y_rob-i)))){
    				bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
    			}
        	}
        }
    	//this.repaint();
    }
   

    public void updateBuffer(Arg arg){
    	//update state 
    	if(arg.state != -1)
    		state = arg.state;
    	
    	//update rotate
		if(arg.rot != -200){
			Constants.rot_rob = arg.rot;
			if(Constants.rot_rob > 180)
				Constants.rot_rob -= 360;
			if(Constants.rot_rob < -180)
				Constants.rot_rob += 360;
		}
		
		double ang = Constants.rot_rob + arg.beacon;
    	if(ang > 180)
    		ang -= 360;
    	if(ang < -180)
    		ang += 360;
    	
    	
		ArrayList<Float> angle1 = new ArrayList<Float>();
		ArrayList<Float> angle2 = new ArrayList<Float>();
		ArrayList<Float> dist = new ArrayList<Float>();
		ArrayList<Integer> sensorX = new ArrayList<Integer>();
		ArrayList<Integer> sensorY = new ArrayList<Integer>();
		if(arg.left != -1){
			angle1.add((float)Constants.rot_rob + 90);
			angle2.add((float) Constants.rot_rob + 30);
		}else{
			angle1.add((float) -500);
			angle2.add((float) -500);
		}
		dist.add(arg.left);
		sensorX.add((int) (arg.x + Math.cos(Math.toRadians(arg.rot + 60))*robo_height/2));
		sensorY.add((int) (arg.y - Math.sin(Math.toRadians(arg.rot + 60))*robo_height/2));
		
		if(arg.center != -1){
			angle1.add((float)Constants.rot_rob + 30);
			angle2.add((float) Constants.rot_rob - 30);
		}else{
			angle1.add((float) -500);
			angle2.add((float) -500);
		}
		dist.add(arg.center);
		sensorX.add((int) (arg.x + Math.cos(Math.toRadians(arg.rot))*robo_height/2));
		sensorY.add((int) (arg.y - Math.sin(Math.toRadians(arg.rot))*robo_height/2));
		
		if(arg.right != -1){
			angle1.add((float)Constants.rot_rob -30);
			angle2.add((float) Constants.rot_rob - 90);
		}else{
			angle1.add((float) -500);
			angle2.add((float) -500);
		}
		dist.add(arg.right);
		sensorX.add((int) (arg.x + Math.cos(Math.toRadians(arg.rot - 60))*robo_height/2));
		sensorY.add((int) (arg.y - Math.sin(Math.toRadians(arg.rot - 60))*robo_height/2));
		
		for(int i = 0; i < angle1.size(); i++){
			if(angle1.get(i) >= 180)
	    		angle1.set(i, angle1.get(i)-360);
	    	if(angle2.get(i) >= 180)
	    		angle2.set(i, angle1.get(i)-360);
	    	if(angle1.get(i) <= -180)
	    		angle1.set(i, angle1.get(i)+360);
	    	if(angle2.get(i) <= -180)
	    		angle2.set(i, angle1.get(i)+360);
		}


		
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        		

        		
        		//update route
        		if(arg.x != -1 && arg.y != -1){
        			if(Math.abs(arg.x - Constants.x_rob) < 5 && Math.abs(arg.y - Constants.y_rob) < 5 ){
        				//shortest distance
            			if(robo_height/2 >= Math.sqrt(((arg.x-j)*(arg.x-j))+
            					((arg.y-i)*(arg.y-i)))){
            				bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
            			}	
            		}else{
            			if(robo_height/2 >= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
            					((Constants.y_rob-i)*(Constants.y_rob-i))) && (robo_height/2)-1.5 <= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
            	    					((Constants.y_rob-i)*(Constants.y_rob-i)))){
            				bresenham_algorithm(j,i,arg.x+(j-Constants.x_rob), arg.y+(i-Constants.y_rob), new Color(255,255,255));
            			}
            		}
        		}
        		
        		//update obstacles
        		for(int z = 0; z < angle1.size(); z++){
        			if(angle1.get(z) == -500)
        				continue;
        			int y = sensorY.get(z)-i;
            		int x = j-sensorX.get(z);
            		if(y == 0)
            			y = 1;
            		
            		if(x == 0)
            			x = 1;
            		
            		if(dist.get(z)/2 >= Math.sqrt(((sensorX.get(z)-j)*(sensorX.get(z)-j))+((sensorY.get(z)-i)*(sensorY.get(z)-i))) && 
        				(dist.get(z)/2)-3 <= Math.sqrt(((sensorX.get(z)-j)*(sensorX.get(z)-j))+((sensorY.get(z)-i)*(sensorY.get(z)-i)))
        				
        				){
            			if(angle2.get(z) > 120 &&( Math.toRadians(angle1.get(z)) >= Math.atan2(y, x) || Math.toRadians(angle2.get(z)) <=  Math.atan2(y, x) )){
            				bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
            			}else if(Math.toRadians(angle1.get(z)) >= Math.atan2(y, x) && Math.toRadians(angle2.get(z)) <=  Math.atan2(y, x)){
            				bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
            			}
            		}
        			
        		}
        		
        		//update initposition
        		if(10 >= Math.sqrt(((Constants.x_robInit-j)*(Constants.x_robInit-j))+((Constants.y_robInit-i)*(Constants.y_robInit-i)))){
        			bufferedImage.setRGB(j, i,(new Color(0, 255, 255)).getRGB());
        		}
        		//update beacon
        		if(arg.beacon != -1){
        			if(5*robo_height>= Math.sqrt(((arg.x-j)*(arg.x-j))+((arg.y-i)*(arg.y-i))) && 
            				(5*robo_height)-1 <= Math.sqrt(((arg.x-j)*(arg.x-j))+((arg.y-i)*(arg.y-i)))
            				&& Math.toRadians(ang) >= Math.atan2(arg.y-i, j-arg.x)
            				&& Math.toRadians(ang-0.5) <= Math.atan2(arg.y-i, j-arg.x)){
            			bresenham_algorithm(arg.x, arg.y, j, i, new Color(0,255,0));
            		}
        		}
        
        		
        	}
        }
    	
    	//update position
    	if(arg.x != -1 && arg.y != -1){
    		Constants.x_rob = arg.x;
    		Constants.y_rob = arg.y;
    	}
    	
    	this.repaint();
    }
    
    //bresenham line algorithm
    private void bresenham_algorithm(int x,int y,int x2, int y2, Color color) {
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
        	bufferedImage.setRGB(x, y,color.getRGB());
          //  putpixel(x,y,color) ;
            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }
    }
    
   
    
}
