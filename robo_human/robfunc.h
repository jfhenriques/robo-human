#ifndef __ROBFUNC_H__
#define __ROBFUNC_H__

#include "RobSock.h"

#define RUN         1
#define STOP        2
#define WAIT        3
#define RETURN      4
#define FINISHED    5




#define JOY_DEV "/dev/input/js0"


#define JOYS_OK			0x0
#define JOYS_NOT_FOUND	0x1
#define JOYS_LOST		0x2


int InitJoystick();
int DetermineAction(int beaconToFollow, float *lPow, float *rPow);


#endif
