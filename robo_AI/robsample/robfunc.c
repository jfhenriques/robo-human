#include "robfunc.h"

/* Calculate the power of left and right motors */
void DetermineAction(int beaconToFollow, float *lPow, float *rPow)
{
	static int counter=0;

	bool   beaconReady;
	static struct beaconMeasure beacon;
	static float  left, right, center;
	static int    Ground;
	static bool   Collision;
	static float Compass;

	/*Access to values from Sensors - Only ReadSensors() gets new values */
	//Calcula a distancia 
	if(IsObstacleReady(LEFT))
	left=     GetObstacleSensor(LEFT);
	if(IsObstacleReady(RIGHT))
	right=    GetObstacleSensor(RIGHT);
	if(IsObstacleReady(CENTER))
	center=   GetObstacleSensor(CENTER);

	beaconReady = IsBeaconReady(beaconToFollow);
	if(beaconReady) {
	beacon =  GetBeaconSensor(beaconToFollow);
	}
	else beaconReady=0;

	if(IsGroundReady())
	Ground=    GetGroundSensor();
	if(IsBumperReady())
	Collision= GetBumperSensor();
	if(IsCompassReady())
	Compass= GetCompassSensor();



	/*
	if(Collision) { 
		if(counter % 600 < 300) {
		   *lPow=0.06;
		   *rPow=-0.06; }
		else {
		   *lPow=-0.06;
		   *rPow=0.06; }
	}else if(center > 8){
		if(counter % 600 < 300) {
		   *lPow=0.06;
		   *rPow=0.03; }
		else {
		   *lPow=0.03;
		   *rPow=0.06; }
	}
	else if(right>3) { 
	  	*lPow=0.01;
		*rPow=0.03;
	}
	else if(left>3) {
		*lPow=0.03;
		*rPow=0.01;
	}
	else {
		if(beaconReady && beacon.beaconVisible && beacon.beaconDir>20.0) { 
		   *lPow=0.0;
		   *rPow=0.3;
		}
		else if(beaconReady && beacon.beaconVisible && beacon.beaconDir<-20.0) {
		   *lPow=0.3;
		   *rPow=0.0;
		}
		else { 
		  *lPow=0.1;
		   *rPow=0.1;
		}
	}*/

	//if(Collision && 
    if(center>4.5||Collision) { /* Close Obstacle - Rotate */
        
	/*if(center>4.5){
		if(Compass < -90 && Compass > 90){
			 *lPow=-0.05;
          		 *rPow=0.05; 
		}else{
			 *lPow=0.05;
      			 *rPow=-0.05; 
		}
	}else{*/
		if(counter % 400 < 200) {
		   *lPow=0.05;
		   *rPow=-0.05; }
		else {
		   *lPow=-0.05;
		   *rPow=0.05; }
		
	//}	
	counter++;
	return;

    }


if(beaconReady && beacon.beaconVisible && beacon.beaconDir>20.0) { /* turn to Beacon */
           *lPow=0.0;
           *rPow=beacon.beaconDir*0.08;
        }
        else if(beaconReady && beacon.beaconVisible && beacon.beaconDir<-20.0) {
           *lPow=-(beacon.beaconDir*0.08);
           *rPow=0.0;
        } else if(beaconReady && beacon.beaconVisible ) {
		*lPow=0.1;
          	*rPow=0.1;
	}
    /*else if(center > 2){
	if(r == -10.0){
		r = ((rand() % 10)-5)*0.1;
		counter++;
		return;
	}
}*/else
	        if(center > 1 && right > 2.0){
			*lPow=0.03;
			*rPow=0.1;
			counter++;
			return;
		}else if(center > 1 && left > 4.0){
			*lPow=0.1;
			*rPow=0.03;
			counter++;
			return;
		}else /*if(center > 2 && right > 4.0 && Compass < -90 && Compass > 90){
			*lPow=0.1;
			*rPow=0.07;

		}else if(center > 2 && left > 4.0 && (Compass >= -90 || Compass <= 90)){
			*lPow=0.07;
			*rPow=0.1;

		}else*/
	
	
   if(right>4) { 
        *lPow=-0.04;
        *rPow=0.04;
    }
    else if(left>4) {
        *lPow=0.04;
        *rPow=-0.04;

   }else    if(right>1.5) {
        *lPow=0.0;
        *rPow=0.1;
	 printf("right");
    }
    else if(left>1.5) {
        *lPow=0.1;
        *rPow=0.0;
 	printf("left");
    }
    else { 
       
      
           *lPow=0.1;
           *rPow=0.1;
       
    }
	counter++;
}
