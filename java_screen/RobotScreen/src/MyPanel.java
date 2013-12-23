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
	static int[][] buffer = new int[Constants.height][Constants.width];
	BufferedImage bufferedImage = new BufferedImage(Constants.width,Constants.height, BufferedImage.TYPE_INT_RGB);
	Image robo;
	int robo_width;
	int robo_height;
	
    public MyPanel() {
    	setBorder(BorderFactory.createLineBorder(Color.black));
    	
    	for(int i = 0; i < Constants.height; i++){
 		   for(int j = 0; j < Constants.width; j++){
 			   buffer[i][j] = 0;
 		   }
 	   }
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        		
        		switch(buffer[i][j]){
        		case 0:
        			bufferedImage.setRGB(j, i,(new Color(0, 0, 0)).getRGB());
        			break;
        		case 1:
        			bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
        			break;
        		}
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

        at.translate(Constants.x_rob+robo_width/2+1, Constants.y_rob-robo_height/2+1);
        at.rotate(Math.toRadians(Constants.rot_rob));
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(robo, at, null);
        
    } 
    
    public void updateBuffer(){
    	for(int i = 0; i < Constants.height; i++){
        	for(int j = 0; j < Constants.width; j++){
        		if(buffer[i][j] == 1)
        			continue;
        		if(buffer[i][j] == 0){
        			if(robo_height/2 >= Math.sqrt(((Constants.x_rob-j)*(Constants.x_rob-j))+
        					((Constants.y_rob-i)*(Constants.y_rob-i)))){
        				buffer[i][j] = 1;
        				bufferedImage.setRGB(j, i,(new Color(255, 255, 255)).getRGB());
        			}
        		}
        	}
        }
    	this.repaint();
    }
    
   
    
}
