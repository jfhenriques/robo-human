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
	Image robo;
	int robo_width;
	int robo_height;
	static public enum ObstacleSensor  {LEFT, CENTER, RIGHT}
	
    public MyPanel() {
    	setBorder(BorderFactory.createLineBorder(Color.black));

    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
    			bufferedImage.setRGB(j, i,(new Color(0, 0, 0)).getRGB());
        	}
        }
    	robo = Toolkit.getDefaultToolkit().getImage("rob.png");
    	robo_width = 34;
    	robo_height = 34;
    	updateBuffer();
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
        g2d.drawImage(robo, at, null);
        
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
    /*
    public void updateBuffer(int x_dest, int y_dest){

    	
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        			
    			if(robo_height/2 >= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
    					((Constants.y_rob-i)*(Constants.y_rob-i))) && (robo_height/2)-1.5 <= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
    	    					((Constants.y_rob-i)*(Constants.y_rob-i)))){
    				bresenham_algorithm(j,i,x_dest+(j-Constants.x_rob), y_dest+(i-Constants.y_rob));
    			}
        	}
        }
    	
    	Constants.x_rob = x_dest;
		Constants.y_rob = y_dest;	
    }
    
    public void detectObstacl(ObstacleSensor sensor, float dist){
    	double angle1 = 0;
    	double angle2 = 0;
    	int sensorX = 0;
    	int sensorY=0;
    	switch (sensor) {
		case LEFT:
			angle1 = Constants.rot_rob + 90;
			angle2 = Constants.rot_rob + 30;
			sensorX = (int) (Constants.x_rob + Math.cos(Math.toRadians(Constants.rot_rob + 60))*robo_height/2);
			sensorY = (int) (Constants.y_rob - Math.sin(Math.toRadians(Constants.rot_rob + 60))*robo_height/2);
			break;
		case CENTER:
			
				angle1 = Constants.rot_rob + 30;
				angle2 = Constants.rot_rob - 30;
			
			
			sensorX = (int) (Constants.x_rob + Math.cos(Math.toRadians(Constants.rot_rob))*robo_height/2);
			sensorY = (int) (Constants.y_rob - Math.sin(Math.toRadians(Constants.rot_rob))*robo_height/2);
			break;
		case RIGHT:
			angle1 = Constants.rot_rob - 30;
			angle2 = Constants.rot_rob - 90;
			sensorX = (int) (Constants.x_rob + Math.cos(Math.toRadians(Constants.rot_rob-60))*robo_height/2);
			sensorY = (int) (Constants.y_rob - Math.sin(Math.toRadians(Constants.rot_rob-60))*robo_height/2);
			break;
		}
    	if(angle1 >= 180)
    		angle1 -= 360;
    	if(angle2 >= 180)
    		angle2 -= 360;
    	if(angle1 <= -180)
    		angle1 += 360;
    	if(angle2 <= -180)
    		angle2 += 360;

    	
    	
    	System.out.println(angle1 + "---" +angle2);
    	
    	bufferedImage.setRGB(sensorX, sensorY,(new Color(255, 255, 255)).getRGB());
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        		int y = sensorY-i;
        		int x = j-sensorX;
        		if(y == 0)
        			y = 1;
        		
        		if(x == 0)
        			x = 1;
        		
        		if(dist/2 >= Math.sqrt(((sensorX-j)*(sensorX-j))+((sensorY-i)*(sensorY-i))) && 
    				(dist/2)-3 <= Math.sqrt(((sensorX-j)*(sensorX-j))+((sensorY-i)*(sensorY-i)))
    				
    				){
        			if(angle2 > 120 &&( Math.toRadians(angle1) >= Math.atan2(y, x) || Math.toRadians(angle2) <=  Math.atan2(y, x) )){
        				bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
        			}else if(Math.toRadians(angle1) >= Math.atan2(y, x) && Math.toRadians(angle2) <=  Math.atan2(y, x)){
        				bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
        			}
        		}
        		
    			
        	}
        }
    	this.repaint();
    }
    
    public void detectBeacon(double angle){
    	double ang = Constants.rot_rob + angle;
    	if(ang > 180)
    		ang -= 360;
    	if(ang < -180)
    		ang += 360;
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        		if(5*robo_height>= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+((Constants.y_rob-i)*(Constants.y_rob-i))) && 
        				(5*robo_height)-1 <= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+((Constants.y_rob-i)*(Constants.y_rob-i)))
        				&& Math.toRadians(ang) >= Math.atan2(Constants.y_rob-i, j-Constants.x_rob)
        				&& Math.toRadians(ang-0.5) <= Math.atan2(Constants.y_rob-i, j-Constants.x_rob)){
        			bresenham_algorithm(Constants.x_rob, Constants.y_rob, j, i);
        		}	
        	}
    	}
    	
    	this.repaint();
    }*/

    public void updateBuffer(Arg arg){
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
            				bresenham_algorithm(j,i,arg.x+(j-Constants.x_rob), arg.y+(i-Constants.y_rob));
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
        		//update beacon
        		if(arg.beacon != -1){
        			if(5*robo_height>= Math.sqrt(((arg.x-j)*(arg.x-j))+((arg.y-i)*(arg.y-i))) && 
            				(5*robo_height)-1 <= Math.sqrt(((arg.x-j)*(arg.x-j))+((arg.y-i)*(arg.y-i)))
            				&& Math.toRadians(ang) >= Math.atan2(arg.y-i, j-arg.x)
            				&& Math.toRadians(ang-0.5) <= Math.atan2(arg.y-i, j-arg.x)){
            			bresenham_algorithm(arg.x, arg.y, j, i);
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
    private void bresenham_algorithm(int x,int y,int x2, int y2) {
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
        	bufferedImage.setRGB(x, y,(new Color(255, 255, 255)).getRGB());
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
