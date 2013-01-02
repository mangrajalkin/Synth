EXECUTABLE = Synth

CSOURCES = test.cpp
JSOURCES = Synth.java GUI.java UniversalListener.java
JPACKAGE = com.mangrajalkin.synth
JMAINCLASS = Synth
JNIHEADER = $(subst .,_,$(JPACKAGE))_$(JMAINCLASS).h

BUILDDIR = build/
SRCDIR = src/
BINDIR = bin/
INCLUDEDIR = include/ 
INCLUDEDIRS = $(INCLUDEDIR)

LIBDIRS = lib/

LIBS = portaudio

A_SOURCES = $(SOURCES:%=$(SRCDIR)%)
A_EXECUTABLE = $(EXECUTABLE:%=$(BINDIR)%)
CC = g++
LD = g++
JAVAC = javac
CFLAGS = -c -Wall -std=gnu++0x $(INCLUDEDIRS:%=-I%)
DLLFLAGS = -shared
LDFLAGS = $(LIBDIRS:%=-L%) $(LIBS:%=-l%)
COBJECTS = $(CSOURCES:.cpp=.o)
JCLASSES = $(JSOURCES:%.java=$(BUILDDIR)$(subst .,/,$(JPACKAGE))/%.class)
A_OBJECTS = $(COBJECTS:%=$(BUILDDIR)%)
	
all: $(A_OBJECTS) $(A_EXECUTABLE)

$(A_EXECUTABLE): $(A_OBJECTS)
	$(LD) $(A_OBJECTS) -o $@ $(LDFLAGS)
	
$(BUILDDIR)%.o: $(SRCDIR)%.cpp $(JNIHEADER)
	$(CC) $(CFLAGS) -o $@ $<

$(JNIHEADER): $(JCLASSES)
	javah -classpath $(BUILDDIR) -d $(INCLUDEDIR) $(JPACKAGE).$(JMAINCLASS)

$(BUILDDIR)$(subst .,/,$(JPACKAGE))%.class: $(SRCDIR)%.java
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)*.java

dll: $(A_OBJECTS)
	$(CC) $(DLLFLAGS) -o lib$(EXECUTABLE).so $< $(LDFLAGS)
	
clean:
	rm $(A_OBJECTS) $(JCLASSES)
	
run: all
	cd $(BINDIR) & $(EXECUTABLE)
	
remake: clean all

