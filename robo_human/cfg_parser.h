
#ifndef __CFG_PARSER_H
#define __CFG_PARSER_H




#define DEF_JOY_DEV			"/dev/input/js0"
#define DEF_HOSTNAME		"localhost"
#define DEF_ROBONAME		"R1"
#define DEF_ROBOID			1

#define DEF_VIEWER_PORT		5555



#define ROB_PARSE_OK		0x0
#define ROB_PARSE_NOT_FOUND	0x1
#define ROB_PARSE_ERROR		0x2
#define ROB_NOT_ALLOCATED	0x3



#define ROB_VIEWER_WAIT		0x0
#define ROB_VIEWER_ERROR	0x1
#define ROB_VIEWER_OK		0x2



typedef struct {
	char *hostname;
	int port;

	int state;

	int sockfd;
	struct addrinfo *addrinfo;
} rob_viewer_cfg_t;

typedef struct {

	char *robo_name;

	int robo_id;
	int robo_pos;

	char *joys_dev;
	char *hostname;

	rob_viewer_cfg_t *rob_viewers;
	size_t rob_viewer_size;

} rob_cfg_t;


int cfg_parser_parse(const char *cfg_name, rob_cfg_t *cfg);
void cfg_parser_close(rob_cfg_t *cfg);
void cfg_parser_connect_viewers(rob_cfg_t *cfg);



#endif /* __CFG_PARSER_H */
