#ifndef __ROBFUNC_H__
#define __ROBFUNC_H__

#include "RobSock.h"
#include "cfg_parser.h"


#define RUN         1
#define STOP        2
#define WAIT        3
#define RETURN      4
#define FINISHED    5




#define JOYS_OK			0x0
#define JOYS_NOT_FOUND	0x1
#define JOYS_LOST		0x2


int InitJoystick(const char * dev_name);
int DetermineAction(int beaconToFollow, float *lPow, float *rPow);
void CloseAndFreeJoystick(void);


int send_viewer_message(rob_viewer_cfg_t *viewer, char* msg, size_t size);
void send_all_viewer_message(rob_cfg_t *cfg, char* msg, size_t size);
void send_all_viewer_state_message(rob_cfg_t *cfg, rob_state_t *state);



#endif /* __ROBFUNC_H__ */
