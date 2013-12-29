
#ifndef __COMMON_H
#define __COMMON_H




#define DEF_JOY_DEV		"/dev/input/js0"
#define DEF_HOSTNAME	"localhost"
#define DEF_ROBONAME	"R1"
#define DEF_ROBOID		1
#define DEF_ROBOPOS		1



#define ROB_PARSE_OK		0x0
#define ROB_PARSE_NOT_FOUND	0x1
#define ROB_PARSE_ERROR		0x2
#define ROB_NOT_ALLOCATED	0x3


typedef struct {
	char *hostname;
	int port;
} rob_viewer_cfg_t;

typedef struct {

	char *robo_name;

	int robo_id;
	int robo_pos;

	char *joys_dev;
	char *hostname;
	char **rob_viewer_cfg_t;

} rob_cfg_t;


int rob_parse_config(const char *cfg_name, rob_cfg_t *cfg);



#endif /* __COMMON_H */
