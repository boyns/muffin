# Generated automatically from Makefile.in by configure.
SHELL = /bin/sh

srcdir = .

JAVAC = /usr/lib/jdk1.1/bin/javac
JAVACFLAGS = -O
JAR = /usr/lib/jdk1.1/bin/jar

SRCS = $(shell $(srcdir)/srclist $(srcdir))

OBJS = $(patsubst %.java,%.class,$(SRCS))

img_dir = org/doit/muffin/images
doc_dir = org/doit/muffin/doc

all:	classes jar

classes:
	CLASSPATH=$(srcdir) $(JAVAC) -d . $(JAVACFLAGS) $(SRCS)

jar:
	$(RM) muffin.jar
	rm -rf $(img_dir) $(doc_dir)
	mkdir -p $(img_dir) $(doc_dir)
	cp $(srcdir)/../images/*.jpg $(img_dir)
	cp $(srcdir)/../doc/filter/*.txt $(doc_dir)
	cp $(srcdir)/../COPYING $(doc_dir)/COPYING.txt
	$(JAR) cvf muffin.jar Muffin.class UK gnu org sdsu

clean:
	$(RM) $(OBJS) muffin
	$(RM) -rf $(img_dir) $(doc_dir)

distclean: clean
