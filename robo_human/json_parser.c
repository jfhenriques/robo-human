
#include <string.h>
#include <jansson.h>

#include "robfunc.h"
#include "common.h"


#define JSON_KEY_JOYSTICK	"joystickDevice"
#define JSON_KEY_ROBO_ID	"roboId"
#define JSON_KEY_ROBO_POS	"roboPos"
#define JSON_KEY_ROBO_NAME	"roboName"
#define JSON_KEY_HOSTNAME	"hostname"
#define JSON_KEY_VIEWERS	"viewers"



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



int rob_parse_config(const char *cfg_name, rob_cfg_t *cfg)
{

	json_error_t error;
	json_t *j_root = NULL;
	int out = ROB_PARSE_OK;

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
	cfg->robo_pos = _json_integer_helper(j_root, JSON_KEY_ROBO_POS, DEF_ROBOPOS);

	puts(cfg->joys_dev);
	puts(cfg->robo_name);
	puts(cfg->hostname);

	printf("%d\n", cfg->robo_id);
	printf("%d\n", cfg->robo_pos);

	if( j_root )
		json_decref(j_root);

	return out;
}
