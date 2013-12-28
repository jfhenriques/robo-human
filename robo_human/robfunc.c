

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/joystick.h>

#include <string.h>
#include <pthread.h>


#include "robfunc.h"

#define DIVISOR		32767.0f
#define MIN_VALUE	0.13f
#define MAX_SPEED	0.10f


typedef struct {
	int fd;
	float *axis;
	int num_of_axis;
	int num_of_buttons;

	char *button;
	char name_of_joystick[80];

	struct js_event js;
	pthread_t thread_id;

} joys_t;



joys_t joys;
float x,y;

static void *read_joys_values(void *);


int InitJoystick()
{

	memset(&joys, 0, sizeof(joys_t));


	if( ( joys.fd = open( JOY_DEV , O_RDONLY)) == -1 )
	{
			printf( "Couldn't open joystick\n" );
			return JOYS_NOT_FOUND;
	}

	ioctl( joys.fd, JSIOCGAXES, &joys.num_of_axis );
	ioctl( joys.fd, JSIOCGBUTTONS, &joys.num_of_buttons );
	ioctl( joys.fd, JSIOCGNAME(80), &joys.name_of_joystick );

	joys.axis = (float *) calloc( joys.num_of_axis, sizeof( float ) );
	joys.button = (char *) calloc( joys.num_of_buttons, sizeof( char ) );

	printf("Joystick detected: %s\n\t%d axis\n\t%d buttons\n\n"
			, joys.name_of_joystick
			, joys.num_of_axis
			, joys.num_of_buttons );

	//fcntl( joys.fd, F_SETFL, O_NONBLOCK );   /* use non-blocking mode */

	pthread_create( &joys.thread_id, NULL, read_joys_values, NULL );


	return JOYS_OK;
}



static void *read_joys_values(void *v)
{

	for(;;)
	{
				/* read the joystick state */
		read(joys.fd, &joys.js, sizeof(struct js_event));
		
				/* see what to do with the event */
		switch (joys.js.type & ~JS_EVENT_INIT)
		{
				case JS_EVENT_AXIS:
						joys.axis   [ joys.js.number ] = (joys.js.value/DIVISOR);
						break;
				case JS_EVENT_BUTTON:
						joys.button [ joys.js.number ] = joys.js.value;
						break;
		}
	}
}

/* Calculate the power of left and right motors */
int DetermineAction(int beaconToFollow, float *lPow, float *rPow)
{

	if(    ( joys.axis[0] > MIN_VALUE || joys.axis[0] < -MIN_VALUE )
		|| ( joys.axis[1] > MIN_VALUE || joys.axis[1] < -MIN_VALUE ) )
	{

		x = joys.axis[0] * 0.5f ; // Don't use full turn speed
		y = joys.axis[1] ;

		*rPow = (-y -x) * MAX_SPEED;
		*lPow = (-y +x) * MAX_SPEED;

		if( *rPow > MAX_SPEED )
			*rPow = MAX_SPEED;
		else
		if( *rPow < -MAX_SPEED )
			*rPow = -MAX_SPEED;

		if( *lPow > MAX_SPEED )
			*lPow = MAX_SPEED;
		else
		if( *lPow < -MAX_SPEED )
			*lPow = -MAX_SPEED;
	}
	else
	{
		*rPow = 0.0f;
		*lPow = 0.0f;
	}

	printf("    L: % 1.5f | R: % 1.6f    \r", *lPow, *rPow);
	fflush(stdout);

	return JOYS_OK;
}
