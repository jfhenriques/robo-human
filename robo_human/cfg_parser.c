
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#include <unistd.h>

#include <string.h>
#include <jansson.h>

#include "robfunc.h"
#include "cfg_parser.h"


#define JSON_KEY_JOYSTICK		"joystickDevice"
#define JSON_KEY_ROBO_ID		"roboId"
#define JSON_KEY_ROBO_NAME		"roboName"
#define JSON_KEY_HOSTNAME		"hostname"
#define JSON_KEY_VIEWERS		"viewers"

#define JSON_KEY_VIEWERS_HOST	"host"
#define JSON_KEY_VIEWERS_PORT	"port"




static char* _json_string_helper(json_t *root, const char* key, const char* def)
{
	char *ptr = (char *)def,
		 *out = NULL;

	json_t *node = NULL;

	if( root && key )
	{
		node = json_object_get(root, key);

		if(    node
			&& json_is_string(node) )
			ptr = (char *)json_string_value(node);
	}

	if( ptr )
		out = (char *)malloc( (1 + strlen(ptr)) * sizeof(char) );

	if( out )
		strcpy(out, ptr);

	return out;
}

static json_int_t _json_integer_helper(json_t *root, const char* key, int def)
{
	char *ptr = NULL;
	json_t *node;

	if( root && key )
	{
		node = json_object_get(root, key);

		if( node )
		{

			if( json_is_string(node) )
			{
				ptr = (char *)json_string_value(node);

				return atoi(ptr);
			}
			else
			if( json_is_real(node) )
				return (json_int_t)json_real_value(node);

			else
			if( json_is_integer(node) )
				return json_integer_value(node);

		}
	}

	return def;
}


int cfg_parser_parse(const char *cfg_name, rob_cfg_t *cfg)
{

	json_error_t error;
	json_t *j_root = NULL,
		   *value = NULL,
		   *array = NULL;
	size_t index,total;
	int out = ROB_PARSE_OK;
	char *str = NULL;
	rob_viewer_cfg_t *view;

	if( cfg_name )
		j_root = json_load_file(cfg_name, 0, &error);

	if( !j_root )
	{
		printf("[WARN] Error in '%s' at %d:%d: \"%s\"\n",
					cfg_name, error.line, error.column, error.text);

		out = ROB_PARSE_NOT_FOUND;
	}

	cfg->joys_dev = _json_string_helper(j_root, JSON_KEY_JOYSTICK, DEF_JOY_DEV);
	cfg->robo_name = _json_string_helper(j_root, JSON_KEY_ROBO_NAME, DEF_ROBONAME);
	cfg->hostname = _json_string_helper(j_root, JSON_KEY_HOSTNAME, DEF_HOSTNAME);

	cfg->robo_id = _json_integer_helper(j_root, JSON_KEY_ROBO_ID, DEF_ROBOID);


	array = json_object_get(j_root, JSON_KEY_VIEWERS);
	cfg->rob_viewer_size = 0;
	
	if(    array 
		&& json_is_array( array )
		&& ( total = json_array_size( array ) ) > 0 )
	{

		cfg->rob_viewers = (rob_viewer_cfg_t *) calloc(total, sizeof(rob_viewer_cfg_t));

		if( cfg->rob_viewers )
		{
			json_array_foreach(array, index, value)
			{
				if( !json_is_object(value) )
					continue;

				str = _json_string_helper(value, JSON_KEY_VIEWERS_HOST, NULL);

				if( !str )
					continue;

				view = &(cfg->rob_viewers[ cfg->rob_viewer_size ]);

				view->state = ROB_VIEWER_WAIT ;
				view->hostname = str;
				view->port = _json_integer_helper(value, JSON_KEY_VIEWERS_PORT, DEF_VIEWER_PORT);

				cfg->rob_viewer_size++;
			}
		}
	}

	if( j_root )
		json_decref(j_root);

	return out;
}



void cfg_parser_connect_viewers(rob_cfg_t *cfg)
{
	size_t i;
	rob_viewer_cfg_t *view;
	char port[100];

	if( cfg )
	{
		for(i = 0; i < cfg->rob_viewer_size; i++)
		{
			view = &(cfg->rob_viewers[i]);

			if(    view->hostname
				&& view->port )
			{
				struct addrinfo hints, *res = NULL;
				int error;

				sprintf(port, "%d", view->port);

				memset(&hints, 0, sizeof(hints));
				hints.ai_family = PF_UNSPEC;
				hints.ai_socktype = SOCK_STREAM;

				printf("[VIEWER] Connecting to %s:%d...", view->hostname, view->port);

				error = getaddrinfo(view->hostname, port, &hints, &view->addrinfo);

				if( error )
					printf("FAILED: %s\n", gai_strerror(error));

				else
				{
					for (res = view->addrinfo; res != NULL; res = res->ai_next)
					{
						view->sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);

						if( view->sockfd < 0 )
							continue;

						if ( connect(view->sockfd, res->ai_addr, res->ai_addrlen) == -1)
						{
							close(view->sockfd);
							view->sockfd = -1;

							continue;
						}

						break;
					}

					if( res == NULL )
						printf("FAILED\n");

					else
					{
						printf("OK\n");

						view->state = ROB_VIEWER_OK;

						continue;
					}
				}

			}

			if( view->addrinfo )
			{
				freeaddrinfo( cfg->rob_viewers[i].addrinfo );
				view->addrinfo = NULL;
			}

			view->state = ROB_VIEWER_ERROR;
		}
	}
}




void cfg_parser_close(rob_cfg_t *cfg)
{
	size_t i;

	if( cfg )
	{
		if(    cfg->rob_viewers
			&& cfg->rob_viewer_size )
		{
			for(i = 0; i < cfg->rob_viewer_size; i++ )
			{
				if(cfg->rob_viewers[i].hostname)
					free( cfg->rob_viewers[i].hostname );

				if( cfg->rob_viewers[i].sockfd >= 0 )
					close( cfg->rob_viewers[i].sockfd );

				if( cfg->rob_viewers[i].addrinfo )
					freeaddrinfo( cfg->rob_viewers[i].addrinfo );
			}

			memset( cfg->rob_viewers, 0, cfg->rob_viewer_size * sizeof( rob_viewer_cfg_t ) );
			free( cfg->rob_viewers );
		}

		if( cfg->robo_name )
			free( cfg->robo_name );

		if( cfg->joys_dev )
			free( cfg->joys_dev );

		if( cfg->hostname )
			free( cfg->hostname );

		memset( cfg, 0, sizeof( rob_cfg_t ) );
	}
}


