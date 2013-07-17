#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <strings.h>
#include <map>

using namespace std;

typedef map<const char *, FILE *> file_map_t;
file_map_t file_map;

void print_data(const char *format, ...) {
	va_list list;
	va_start(list, format);
	
	for (int i = 0; i < strlen(format); i++) {
		switch(format[i]) {
			case 'd': {
				double val=va_arg(list,double);
				printf("%f ", val);
				break;
			}
			case 'i': {
				int val = va_arg(list,int);
				printf("%d ", val);
				break;
			}
			default:
				printf("unknown arg!");
				abort();
		}
	}
	
	va_end(list);
}

void save_data(const char *filename,const char *format, ...) {
	va_list list;
	va_start(list, format);
	
	FILE *f;
	file_map_t::iterator iter = file_map.find(filename);
	if (iter == file_map.end()) {
		f = fopen(filename, "w");
		if (f == NULL) abort();
		file_map.insert(pair<const char *, FILE *>(filename, f));
	} else {
		f = iter->second;
	}
	
	for (int i = 0; i < strlen(format); i++) {
		switch(format[i]) {
			case 'd': {
				double val=va_arg(list,double);
				fwrite(&val, sizeof(double), 1, f);
				break;
			}
			case 'i': {
				int val = va_arg(list,int);
				fwrite(&val, sizeof(int), 1, f);
				break;
			}
			default:
				printf("unknown arg!");
				abort();
		}
	}
	
	va_end(list);
}

void close_all() {
	for (file_map_t::iterator i = file_map.begin(); i != file_map.end(); i++) {
		FILE *f = i->second;
		fclose(f);
	}
}

/*
int main() {
	print_data("di", 3.5, 5);
	save_data("vara", "dd", 3.5, 7.5);
	
	close_all();
	return 0;
}*/