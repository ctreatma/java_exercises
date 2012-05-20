# Makefile for final project

.SUFFIXES: .cpp .o
FILES = hash.cpp treeinfo.cpp
OBJECTS = hash.o treeinfo.o

treeinfo : $(OBJECTS) hash.h
	g++ $(OBJECTS) -o treeinfo

hash.cpp : hash.h

.cpp.o:
	g++ -c $<