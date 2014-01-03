
/* mainRob.C
 *
 * Basic Robot Agent
 * Very simple version for demonstration
 *
 * For more information about the CiberRato Robot Simulator 
 * please see http://microrato.ua.pt/ or contact us.
 */

#include <stdio.h>
#include <stdlib.h>

#include <math.h>
#include <string.h>

#include <sys/time.h> 
#include <time.h>
#include <unistd.h>

#include "RobSock.h"

#include "robfunc.h"

 #define USEC_TO_SEC(a)		(((double)a)/1000000.0)

static double _get_elapsed_secs(struct timeval *t1, struct timeval *t2)
{
	if( !t1 || !t2 )
		return -1.0f;

	return (((double)t2->tv_sec) + USEC_TO_SEC(t2->tv_usec)) - (((double)t1->tv_sec) + USEC_TO_SEC(t1->tv_usec));
}

int main(int argc, char *argv[])
{
	char host[100]="localhost";
	char rob_name[20]="robsample";
	float lPow,rPow;
	int state=STOP, stoppedState=RUN, rob_id = 1;
	int beaconToFollow=0;
	double elapsed1 = 0.0, elapsed2 = 0.0, realTotal = 0.0;
	struct timeval t1, t2, t3;
	bool firstTimeStart = 1;

	printf( " Sample Robot\n Copyright (C) 2001-2011 Universidade de Aveiro\n" );

	 /* processing arguments */
	while (argc > 2) /* every option has a value, thus argc must be 1, 3, 5, ... */
	{
		if (strcmp(argv[1], "-host") == 0)
		{
		   strncpy(host, argv[2], 99);
		   host[99]='\0';
		}
		else if (strcmp(argv[1], "-robname") == 0)
		{
		   strncpy(rob_name, argv[2], 19);
		   rob_name[19]='\0';
		}
		else if (strcmp(argv[1], "-pos") == 0)
		{
			if(sscanf(argv[2], "%d", &rob_id)!=1)
			   argc=0; /* error message will be printed */
		}
		else
		{
				break; /* the while */
		}
		argc -= 2;
		argv += 2;
	}

	if (argc != 1)
	{
		fprintf(stderr, "Bad number of parameters\n"
				"SYNOPSIS: mainRob [-host hostname] [-robname robotname] [-pos posnumber]\n");

		return 1;
	}

	/* Connect Robot to simulator */
	if(InitRobot(rob_name, rob_id, host)==-1)
	{
	   printf( "%s Failed to connect\n", rob_name); 
	   exit(1);
	}
	printf( "%s Connected\n", rob_name );
	state=STOP;
	while(1)
	{
		/* Reading next values from Sensors */
		ReadSensors();

		if(GetFinished()) /* Simulator has received Finish() or Robot Removed */
		{
			printf(  "%s Exiting\n", rob_name );

			gettimeofday(&t3, NULL);

			elapsed2 = _get_elapsed_secs(&t2, &t3);
			realTotal = _get_elapsed_secs(&t1, &t3);

			printf("to beacon | to start | total | real total\n");
			printf("& %.2f & %.2f & %.2f & %.2f \n", elapsed1, elapsed2, elapsed1 + elapsed2, realTotal);

			exit(0);
		}
		if(state==STOP && GetStartButton())
		{
			state=stoppedState;  /* Restart     */

			if( firstTimeStart )
			{
				firstTimeStart = 0;
				printf("Started counting elapsed time\n");
				
				gettimeofday(&t1, NULL);
			}
		}
		if(state!=STOP && GetStopButton())  {
			stoppedState=state;
			state=STOP; /* Interrupt */
		}

		switch (state) { 
				 case RUN:    /* Go */


					if( GetVisitingLed() )
					{
						gettimeofday(&t2, NULL);

						elapsed1 = _get_elapsed_secs(&t1, &t2);

						printf("Elapsed from origin to beacon: %f\n", elapsed1);


						state = WAIT;
					}

				  if(GetGroundSensor()==0) {         /* Visit Target */
					 SetVisitingLed(1);
					 printf("%s visited target at %d\n", rob_name, GetTime());
				  }

				  else {
					 DetermineAction(0,&lPow,&rPow);
					 DriveMotors(lPow,rPow);
				  }
				  break;
		 case WAIT: /* Wait for others to visit target */
			 if(GetReturningLed()) {
			 		state = RETURN;

			 		gettimeofday(&t2, NULL);
			 	}

					 DriveMotors(0.0,0.0);
					 break;
		 case RETURN: /* Return to home area */
			 if(GetGroundSensor()==1) { /* Finish */
						 Finish();
						 printf("%s found home at %d\n", rob_name, GetTime());
					 }
					 else {
						DetermineAction(1,&lPow,&rPow);
						DriveMotors(lPow,rPow);
					 }
					 break;
	}

		Say(rob_name);

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

