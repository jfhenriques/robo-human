
/* mainRob.C
 *
 * Basic Robot Agent
 * Very simple version for demonstration
 *
 * For more information about the CiberRato Robot Simulator 
 * please see http://microrato.ua.pt/ or contact us.
 */

#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "RobSock.h"

#include "robfunc.h"

#include "common.h"



int main(int argc, char *argv[])
{
	char cfg[256]="config.json";
	
	float lPow,rPow;
	int state=STOP, stoppedState=RUN;
	int beaconToFollow=0;
	rob_cfg_t rob_cfg;

	 /* processing arguments */
	while (argc > 2)
	{
		if (strcmp(argv[1], "-cfg") == 0)
		{
		   strncpy(cfg, argv[2], 99);
		   cfg[255]='\0';
		}
		else
		{
				break; /* the while */
		}
		argc -= 2;
		argv += 2;
	}

	rob_parse_config(cfg, &rob_cfg);

	InitJoystick(rob_cfg.joys_dev);

	/* Connect Robot to simulator */
	if(InitRobot(rob_cfg.robo_name, rob_cfg.robo_id, rob_cfg.hostname)==-1)
	{
	   printf( "%s Failed to connect\n", rob_cfg.robo_name); 
	   exit(1);
	}
	printf( "Connected: %s\n", rob_cfg.robo_name );
	state=STOP;
	while(1)
	{
		/* Reading next values from Sensors */
		ReadSensors();

		if(GetFinished()) /* Simulator has received Finish() or Robot Removed */
		{
			//TODO: enviar mensagem ao viewer

		   printf(  "Exiting: %s\n", rob_cfg.robo_name );
		   exit(0);
		}
		if(state==STOP && GetStartButton()) state=stoppedState;  /* Restart     */
		if(state!=STOP && GetStopButton())  {
			stoppedState=state;
			state=STOP; /* Interrupt */
		}

		switch (state) { 
				 case RUN:    /* Go */
		  if( GetVisitingLed() ) state = WAIT;
				  if(GetGroundSensor()==0) {         /* Visit Target */
					 SetVisitingLed(1);
					 printf("%s visited target at %d\n", rob_cfg.robo_name, GetTime());
				  }

				  else {
					 DetermineAction(0,&lPow,&rPow);
					 DriveMotors(lPow,rPow);
				  }
				  break;
		 case WAIT: /* Wait for others to visit target */
			 if(GetReturningLed()) state = RETURN;

					 DriveMotors(0.0,0.0);
					 break;
		 case RETURN: /* Return to home area */
			 if(GetGroundSensor()==1) { /* Finish */
						 Finish();
						 printf("%s found home at %d\n", rob_cfg.robo_name, GetTime());
					 }
					 else {
						DetermineAction(1,&lPow,&rPow);
						DriveMotors(lPow,rPow);
					 }
					 break;
	}

		Say(rob_cfg.robo_name);

	 //Request Sensors for next cycle
	  if(GetTime() % 2 == 0) {
			RequestObstacleSensor(CENTER);

			if(GetTime() % 8 == 0 || beaconToFollow == GetNumberOfBeacons())
				RequestGroundSensor();
			else
				RequestBeaconSensor(beaconToFollow);

		 }
		 else {
			RequestSensors(2, "IRSensor1", "IRSensor2");
		 }

	}
	return 1;
}

