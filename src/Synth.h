#ifndef SYNTH_H
#define SYNTH_H
#include <iostream>
#include <cmath>
#include <bitset>
#include "portaudio.h"

#include "com_mangrajalkin_synth_Synth.h"


static double SAMPLE_RATE = 44100;
static double PI = std::atan2(0,-1);
static double PI_2 = PI * 2;
static double TIME_PER_FRAME = 1 / SAMPLE_RATE;
static double A = 440;

typedef struct {
	double time;
	std::bitset<128> midiNotes;
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
