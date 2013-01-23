/*
 * This file is part of Synth.
 *
 * Synth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Synth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Synth.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#ifndef LIBSYNTH_HPP
#define LIBSYNTH_HPP
#include <iostream>
#include <cmath>
#include <bitset>
#include <map>
#include "portaudio.h"
#include "Note.hpp"
#include "Envelope.hpp"

#include "com_mangrajalkin_synth_Synth.h"


static double SAMPLE_RATE = 44100;
static double PI = std::atan2(0,-1);
static double TAO = PI * 2;
static double TIME_PER_FRAME = 1 / SAMPLE_RATE;
static double A = 440;

typedef struct {
	double time;
	// std::bitset<128> midiNotes;
	Note *midiNotes[128];
} paData;

static paData data;
static PaStream *stream;

static int paCallback(const void *inputBuffer,
							void *outputBuffer,
							unsigned long framesPerBuffer,
							const PaStreamCallbackTimeInfo *timeInfo,
							PaStreamCallbackFlags statusFlags,
							void *userData);
bool executedOk(PaError error);
int cleanup();
void noteOn(int midiNote);
void noteOff(int midiNote);
int startup();
int shutdown();

#endif
