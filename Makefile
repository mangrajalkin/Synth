# This file is part of Synth.

# Synth is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# Synth is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with Synth.  If not, see <http://www.gnu.org/licenses/>.

PROJECT = Synth

LIBSOURCES = libsynth.cpp
JSOURCES = Synth.java GUI.java UniversalListener.java
JPACKAGE = com.mangrajalkin.synth
JMAINCLASS = Synth
JNIHEADER = $(subst .,_,$(JPACKAGE))_$(JMAINCLASS).h

BUILDDIR = build
SRCDIR = src
BINDIR = bin
INCLUDEDIR = include
LIBDIRS = lib $(JDKHOME)/jre/lib/i386/client $(JDKHOME)/jre/lib/amd64/server $(JDKHOME)/lib 
INCLUDEDIRS = $(INCLUDEDIR) $(JDKHOME)/include $(JDKHOME)/include/linux $(JDKHOME)/include/win32

MAINLIBS = jvm
LIBLIBS = portaudio

JAR = $(BINDIR)/$(PROJECT).jar
MANIFEST = MANIFEST.MF

ifdef SystemRoot
LIB = $(BINDIR)/synth.dll
EXECUTABLE = $(PROJECT:%=$(BINDIR)/%).exe
else
LIB = $(BINDIR)/libsynth.so
EXECUTABLE = $(PROJECT:%=$(BINDIR)/%)
endif

CC = g++
LD = g++
JAVAC = $(JDKHOME)/bin/javac
CFLAGS = -c -fPIC -Wall -std=gnu++0x $(INCLUDEDIRS:%=-I%)
LIBFLAGS = -shared
MAINLDFLAGS = $(LIBDIRS:%=-L%) $(MAINLIBS:%=-l%)
LIBLDFLAGS = $(LIBDIRS:%=-L%) $(LIBLIBS:%=-l%)
JCLASSES = $(JSOURCES:%.java=$(BUILDDIR)/$(subst .,/,$(JPACKAGE))/%.class)
OBJECTS = $(CSOURCES:%.cpp=$(BUILDDIR)/%.o)
LIBOBJECTS = $(LIBSOURCES:%.cpp=$(BUILDDIR)/%.o)

all: $(JAR) $(LIB)

clean:
	rm $(OBJECTS)
	
run: all
ifdef SystemRoot
	cd $(BINDIR) && $(JDKHOME)/bin/java -jar $(PROJECT).jar && cd ..	
else
	cd $(BINDIR) && $(JDKHOME)/bin/java -jar ./$(PROJECT).jar && cd ..
endif
	
remake: clean all

# Target to link the native library
$(LIB): $(LIBOBJECTS) $(BINDIR)
	$(LD) $(LIBOBJECTS) -o $@ $(LIBLDFLAGS) $(LIBFLAGS)

# Target to compile the C++ objects
$(BUILDDIR)/%.o: $(SRCDIR)/%.cpp $(INCLUDEDIR)/$(JNIHEADER)
	$(CC) $(CFLAGS) -o $@ $<

# Target to create the JNI header file.
$(INCLUDEDIR)/$(JNIHEADER): $(JCLASSES)
	$(JDKHOME)/bin/javah -classpath $(BUILDDIR) -d $(INCLUDEDIR) $(JPACKAGE).$(JMAINCLASS)

# Target to create the jar file.
$(JAR): $(MANIFEST) $(JCLASSES) $(BINDIR)
	$(JDKHOME)/bin/jar -cvfm $(JAR) $(MANIFEST) -C $(BUILDDIR) com

# Target to create the jar Manifest file
$(MANIFEST):
	echo "Manifest-Version: 1.0" > $(MANIFEST)
	echo "Main-Class: $(JPACKAGE).$(JMAINCLASS)" >> $(MANIFEST)
	echo "" >> $(MANIFEST)

# Target to compile the java .class files.
# All the classes must be compiled at once,
# due to interdependancies.
$(BUILDDIR)/$(subst .,/,$(JPACKAGE))/%.class: $(SRCDIR)/%.java $(BUILDDIR)
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)/*.java

# Targets to create output directories.
# javah and some compilers break without
# having the target directories already
# in existance.
$(BUILDDIR):
	mkdir $(BUILDDIR)

$(BINDIR):
	mkdir $(BINDIR)

$(BUILDDIR)/lib: $(BUILDDIR)
	mkdir $(BUILDDIR)/lib

