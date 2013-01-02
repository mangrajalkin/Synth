# This file is part of Synth.
#
# Synth is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Synth is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Synth.  If not, see <http://www.gnu.org/licenses/>.

PROJECT = Synth

CSOURCES = Synth.cpp
JSOURCES = Synth.java GUI.java UniversalListener.java
JPACKAGE = com.mangrajalkin.synth
JMAINCLASS = Synth
JNIHEADER = $(subst .,_,$(JPACKAGE))_$(JMAINCLASS).h

BUILDDIR = build
SRCDIR = src
BINDIR = bin
INCLUDEDIR = include
LIBDIRS = lib
INCLUDEDIRS = $(INCLUDEDIR) $(JDKHOME)/include $(JDKHOME)/include/linux $(JDKHOME)/include/win32

LIBS = portaudio

EXECUTABLE = $(PROJECT:%=$(BINDIR)/%)
CC = g++
LD = g++
JAVAC = $(JDKHOME)/bin/javac
CFLAGS = -c -Wall -std=gnu++0x $(INCLUDEDIRS:%=-I%)
DLLFLAGS = -shared
LDFLAGS = $(LIBDIRS:%=-L%) $(LIBS:%=-l%)
JCLASSES = $(JSOURCES:%.java=$(BINDIR)/$(subst .,/,$(JPACKAGE))/%.class)
OBJECTS = $(CSOURCES:%.cpp=$(BUILDDIR)/%.o)

all: | $(BUILDDIR) $(OBJECTS) $(EXECUTABLE)

clean:
	rm $(OBJECTS)
	
run: all
	cd $(BINDIR) && ./$(PROJECT) && cd ..
	
remake: clean all

$(EXECUTABLE): | $(BINDIR) $(A_OBJECTS)
	$(LD) $(OBJECTS) -o $@ $(LDFLAGS)
	
$(BUILDDIR)/%.o: $(SRCDIR)/%.cpp $(JNIHEADER)
	$(CC) $(CFLAGS) -o $@ $<

$(JNIHEADER): $(JCLASSES)
	javah -classpath $(BUILDDIR) -d $(INCLUDEDIR) $(JPACKAGE).$(JMAINCLASS)

$(BINDIR)/$(subst .,/,$(JPACKAGE))/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)/*.java
	
$(BUILDDIR):
	mkdir $(BUILDDIR)

$(BINDIR):
	mkdir $(BINDIR)

