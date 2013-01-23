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

LIBSOURCES = libsynth.cpp Note.cpp Envelope.cpp
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
MANIFEST = $(BUILDDIR)/MANIFEST.MF

ifdef SystemRoot
LIB = $(BINDIR)/synth.dll
else
LIB = $(BINDIR)/libsynth.so
endif

CC = g++
LD = g++
JAVAC = $(JDKHOME)/bin/javac
ifdef SystemRoot
CFLAGS = -c -Wall -std=gnu++0x $(INCLUDEDIRS:%=-I%)
LIBFLAGS = -shared -Wl,--kill-at
else
CFLAGS = -c -fPIC -Wall -std=gnu++0x $(INCLUDEDIRS:%=-I%)
LIBFLAGS = -shared
endif
MAINLDFLAGS = $(LIBDIRS:%=-L%) $(MAINLIBS:%=-l%)
LIBLDFLAGS = $(LIBDIRS:%=-L%) $(LIBLIBS:%=-l%)
JCLASSES = $(JSOURCES:%.java=$(BUILDDIR)/$(subst .,/,$(JPACKAGE))/%.class)
OBJECTS = $(CSOURCES:%.cpp=$(BUILDDIR)/%.o)
LIBOBJECTS = $(LIBSOURCES:%.cpp=$(BUILDDIR)/%.o)

#check if JDKHOME is defined
$(if $(JDKHOME),,$(error ERROR: Please make sure environment variable "JDKHOME" is defined before running make))

all: $(JAR) $(LIB)

clean:
ifdef SystemRoot
	rmdir $(BUILDDIR) /s /q
	rmdir $(INCLUDEDIR) /s /q
else
	rm -R $(BUILDDIR) $(INCLUDEDIR)
endif

run: all
	cd $(BINDIR) && $(JDKHOME)/bin/java -jar $(PROJECT).jar && cd ..	

remake: clean all

# Target to link the native library
$(LIB): $(LIBOBJECTS) $(BINDIR)/.timestamp
	$(LD) $(LIBOBJECTS) -o $@ $(LIBLDFLAGS) $(LIBFLAGS)

# Target to compile the C++ objects
$(BUILDDIR)/%.o: $(SRCDIR)/%.cpp $(INCLUDEDIR)/$(JNIHEADER)
	$(CC) $(CFLAGS) -o $@ $<

# Target to create the JNI header file.
$(INCLUDEDIR)/$(JNIHEADER): $(JCLASSES)
	$(JDKHOME)/bin/javah -classpath $(BUILDDIR) -d $(INCLUDEDIR) $(JPACKAGE).$(JMAINCLASS)

# Target to create the jar file.
$(JAR): $(MANIFEST) $(JCLASSES) $(BINDIR)/.timestamp
	$(JDKHOME)/bin/jar -cvfm $(JAR) $(MANIFEST) -C $(BUILDDIR) com

# Target to create the jar Manifest file
$(MANIFEST): $(BUILDDIR)/.timestamp
	echo Manifest-Version: 1.0 > $@
	echo Main-Class: $(JPACKAGE).$(JMAINCLASS) >> $@

# Target to compile the java .class files.
# All the classes must be compiled at once,
# due to interdependancies.
$(BUILDDIR)/$(subst .,/,$(JPACKAGE))/%.class: $(SRCDIR)/%.java $(BUILDDIR)/.timestamp
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)/*.java

# Targets to create output directories.
# javah and some compilers break without
# having the target directories already
# in existance.
$(BUILDDIR)/.timestamp:
	mkdir $(BUILDDIR)
	@echo "" > $(BUILDDIR)/.timestamp

$(BINDIR)/.timestamp:
	mkdir $(BINDIR)
	@echo "" > $(BINDIR)/.timestamp

