import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

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
    }


    public Dimension getPreferredSize() {
        return new Dimension(Constants.width,Constants.height);
        
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
    	
        updateBuffer();
       
        
        
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
    	this.repaint();
    }
    
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
    	/*if(angle1 >= 180)
    		angle1 -= 360;*/
    	
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
        				(dist/2)-3 <= Math.sqrt(((sensorX-j)*(sensorX-j))+((sensorY-i)*(sensorY-i))) &&
        						Math.toRadians(angle1) >= Math.atan2(y, x) &&
                				Math.toRadians(angle2) <=  Math.atan2(y, x)
        				){
     
        			
        			bufferedImage.setRGB(j, i,(new Color(255, 0, 0)).getRGB());
        		}
    			
        	}
        }
    	this.repaint();
    }
  /*
    private void drawSensor(double sensor, int direcao) {
		double angleTop = 0;
		double angleBot = 0;

		int robotX= (int) ((x-centerX)*tamanhoPixel+janela.width/2);
		int robotY= (int) ((centerY-y)*tamanhoPixel+janela.height/2);

		if(direcao==1){
			angleTop = compass+80;
			angleBot = compass+20;
		}		
		else if(direcao==2){
			angleTop = compass-20;
			angleBot =compass-80;
		}
		else if(direcao==0){
			angleTop = compass+30;
			angleBot =compass-30;
		}

		double proximidade = sensor;
		double barulho =cif.GetNoiseBeaconSensor()/100/2;

		// por cada angulo vamos traçar os pontos àquela distância
		for(int angulo = 0; angulo < (int)(angleTop-angleBot);angulo++){
			for(int raio=0; raio < raioSensor; raio++){
				double xTop = Math.cos(Math.toRadians(angleTop-angulo))*raio;
				double yTop = Math.sin(Math.toRadians(angleTop-angulo))*raio;
				double pActual = area[robotX+(int)xTop][robotY-(int)yTop];
				if(raio>=raioSensor-proximidade*raioSensor){
					double forcaSensor = barulho;
					if(sensor>1){
						forcaSensor= 1-barulho;
						area[robotX+(int)xTop][robotY-(int)yTop]=forcaSensor*pActual/(forcaSensor*pActual+(1-forcaSensor)*(1-pActual));
					}
				}
				else{
					double forcaSensor=barulho;
					area[robotX+(int)xTop][robotY-(int)yTop]=forcaSensor*pActual/(forcaSensor*pActual+(1-forcaSensor)*(1-pActual));
				}

			}
		}
		updateGrid();

	}*/
    
    
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
