#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <strings.h>

typedef struct {
	char fn[128];
	FILE *f;
} files_t;


#define MAX_FILES 10
static files_t files[MAX_FILES];
static int files_index = 0;


FILE *get_file(const char *filename) {
	for (int i = 0; i < files_index; i++) {
		if (strcmp(filename, files[i].fn) == 0) return files[i].f;
	}
	
	strcpy(files[files_index].fn, filename);
	files[files_index].f = fopen(filename, "w");
	files_index++;
	
	return files[files_index - 1].f;
}

void close_all() {
	for (int i = 0; i < files_index; i++) {
		fclose(files[i].f);
	}
}

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

void save_array(const char *filename, const double *array, int count) {
	FILE *f = get_file(filename);

	int x = fwrite(array, sizeof(double), count, f);
	if (x != count) abort();
}

/*
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
}*/



/*
int main() {
	print_data("di", 3.5, 5);
	save_data("vara", "dd", 3.5, 7.5);
	
	close_all();
	return 0;
}*/