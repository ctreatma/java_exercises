// file_fun.h

#ifndef CS280_READ_FILE_H
#define CS280_READ_FILE_H

char* read_char_file(char* file_name, int* size);
void  write_file(char* file_name, char* file_arr, int size);
long long* read_long_long_file(char* name, int* size);
char** split_string(char* str, int size, int bsize, int* new_size);

#endif
