
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

#include "cfg_parser.h"


#define MAX_FILE_NAME_SIZE	256

#define USEC_TO_SEC(a)		(((double)a)/1000000.0)


static double _get_elapsed_secs(struct timeval *t1, struct timeval *t2)
{
	if( !t1 || !t2 )
		return -1.0f;

	return (((double)t2->tv_sec) + USEC_TO_SEC(t2->tv_usec)) - (((double)t1->tv_sec) + USEC_TO_SEC(t1->tv_usec));
}


int main(int argc, char *argv[])
{
	char cfg[MAX_FILE_NAME_SIZE]="config.json";
	
	float lPow,rPow;
	int state=STOP, stoppedState=RUN;
	int beaconToFollow=0;
	int ret = 0;
	rob_cfg_t rob_cfg;
	rob_state_t rob_state;
	struct beaconMeasure beacon;
	int totalBeacons = 0,curGroundSensor = -1;
	double elapsed1 = 0.0, elapsed2 = 0.0, realTotal = 0.0;
	struct timeval t1, t2, t3;
	bool firstTimeStart = 1;

	memset(&rob_state, 0, sizeof(rob_state_t));


	 /* processing arguments */
	while (argc > 2)
	{
		if (strcmp(argv[1], "-cfg") == 0)
		{
		   strncpy(cfg, argv[2], 99);
		   cfg[MAX_FILE_NAME_SIZE-1]='\0';
		}
		else
		{
				break; /* the while */
		}
		argc -= 2;
		argv += 2;
	}

	cfg_parser_parse(cfg, &rob_cfg);
	// int i;
	// for(i = 0; i < rob_cfg.rob_viewer_size; i++)
	// 	printf("Viewer: %s:%d\n", rob_cfg.rob_viewers[i].hostname, rob_cfg.rob_viewers[i].port);

	InitJoystick(rob_cfg.joys_dev);

	cfg_parser_connect_viewers(&rob_cfg);

	/* Connect Robot to simulator */
	if( InitRobot(rob_cfg.robo_name, rob_cfg.robo_pos, rob_cfg.hostname) == -1)
	{
		ret = 1;
		printf( "%s Failed to connect\n", rob_cfg.robo_name);
	}

	else
	{
		totalBeacons = GetNumberOfBeacons();

		printf( "Connected: %s, Total beacons: %d\n", rob_cfg.robo_name, totalBeacons);

		state=STOP;
		while(1)
		{
			/* Reading next values from Sensors */
			ReadSensors();

			if(GetFinished()) /* Simulator has received Finish() or Robot Removed */
			{
				printf(  "Exiting: %s\n", rob_cfg.robo_name );
				state = FINISHED;

				gettimeofday(&t3, NULL);

				elapsed2 = _get_elapsed_secs(&t2, &t3);
				realTotal = _get_elapsed_secs(&t1, &t3);

				printf("to beacon | to start | total | real total\n");
				printf("& %.2f & %.2f & %.2f & %.2f \n", elapsed1, elapsed2, elapsed1 + elapsed2, realTotal);

				break;
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

			curGroundSensor = GetGroundSensor();

			switch (state)
			{
				case RUN:    /* Go */
					
					if( GetVisitingLed() )
					{
						gettimeofday(&t2, NULL);

						elapsed1 = _get_elapsed_secs(&t1, &t2);

						printf("Elapsed from origin to beacon: %f\n", elapsed1);


						state = WAIT;
						DriveMotors(0.0,0.0);
					}
					else
					{
						if( curGroundSensor == beaconToFollow )
						{
							beaconToFollow++;
							SetVisitingLed(1);
							printf("%s visited target at %d\n", rob_cfg.robo_name, GetTime());
						}
						else {

							DetermineAction(beaconToFollow, &lPow, &rPow);
							DriveMotors(lPow, rPow);
						}
					}
				
					break;

				case RETURN:    /* Go */

					if( curGroundSensor == totalBeacons )
					{
						printf("%s found home at %d\n", rob_cfg.robo_name, GetTime());
						Finish();
					}
					else {
						DetermineAction(beaconToFollow, &lPow, &rPow);
						DriveMotors(lPow, rPow);
					}
				
					break;

				case WAIT: /* Wait for others to visit target */

					if(GetReturningLed())
					{
						SetVisitingLed(0);
						state = RETURN;

						gettimeofday(&t2, NULL);
					}

					DriveMotors(0.0,0.0);

					break;
			}

			//Say(rob_cfg.robo_name);


			rob_state.state = state;

			if( (rob_state.leftAvail = IsObstacleReady(LEFT)) )
				rob_state.left = GetObstacleSensor(LEFT);

			if( (rob_state.rightAvail = IsObstacleReady(RIGHT)) )
				rob_state.right = GetObstacleSensor(RIGHT);

			if( (rob_state.centerAvail = IsObstacleReady(CENTER)) )
				rob_state.center = GetObstacleSensor(CENTER);


			if(IsGPSReady())
			{
				rob_state.x = GetX();
				rob_state.y = GetY();
			}

			// if( IsGPSDirReady() )
			// 	rob_state.dir = GetDir();

			if( IsCompassReady() )
				rob_state.dir = GetCompassSensor();


			if( ( rob_state.beaconVis = IsBeaconReady(beaconToFollow) ) )
			{
				beacon = GetBeaconSensor(beaconToFollow);

				if( ( rob_state.beaconVis = beacon.beaconVisible ) )
					rob_state.beaconDir = beacon.beaconDir;
			}

			send_all_viewer_state_message(&rob_cfg, &rob_state);


			RequestCompassSensor();

			//Request Sensors for next cycle
			if(GetTime() % 2 == 0) {

				RequestObstacleSensor(CENTER);

				if(    (GetTime() % 8) == 0
					|| beaconToFollow == totalBeacons )
					RequestGroundSensor();
				else
					RequestBeaconSensor(beaconToFollow);

			}
			else {
				RequestSensors(2, "IRSensor1", "IRSensor2");
			}
		}

		send_all_viewer_state_message(&rob_cfg, &rob_state);

	}

	printf("Doing cleanup: %s\n", rob_cfg.robo_name);

	CloseAndFreeJoystick();

	cfg_parser_close(&rob_cfg);

	return ret;
}

