# Makefile for Java port of GNU getopt

all: LongOpt.class Getopt.class

Getopt.class: Getopt.java
	javac Getopt.java

LongOpt.class: LongOpt.java
	javac LongOpt.java

docs: 
	javadoc -author -version -public Getopt.java LongOpt.java
