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

#include "libsynth.hpp"
/*
 * Main callback for sound data. Called by PortAudio as needed.
 * Don't do any malloc() or free() stuff here. Needs to be as fast
 * as possible.
 */
static int paCallback(
			const void *inputBuffer,
			void *outputBuffer,
			unsigned long framesPerBuffer,
			const PaStreamCallbackTimeInfo *timeInfo,
			PaStreamCallbackFlags statusFlags,
			void *userData){
	paData *data = (paData*)userData;
	float *out = (float*)outputBuffer;
	unsigned int i;
	(void) inputBuffer; // This line is to disable "unused variable" compiler warnings
	
	for (i=0;i<framesPerBuffer;i++){
	
		float value = 0.001;
		int noteCount = 0;
		for(int i=0;i<128;i++){
			double amplitude = data->midiNotes[i]->envelope->amplitude;
			if (amplitude > 0){
				noteCount++;
				double frequency = A * std::pow(2, (i-69.0)/12.0);
				double omega = TAO * frequency;
				double phi = 0;
				value += amplitude * std::sin((omega * data->time) + phi);
			}
			data->midiNotes[i]->envelope->addTime(TIME_PER_FRAME);
		}
		if (noteCount > 1)
			value /= noteCount;
		data->time += TIME_PER_FRAME;
		*out++ = value;
		*out++ = value;
	}
	return 0;
}

int startup(){
	std::cout << "Starting PortAudio..." << std::endl;
		
	if (! executedOk(Pa_Initialize())){
		cleanup();
		return -2;
	}
	if (! executedOk(Pa_OpenDefaultStream(
				&stream,	// the stream
				0,		// number of input channels
				2,		// number of output channels
				paFloat32,	// data type
				SAMPLE_RATE,	// the sample rate
				256,		// frames per buffer.
				paCallback,	// the callback function
				&data ))){	// persistant user data
		cleanup();
		return -3;
	}
	if (! executedOk(Pa_StartStream(stream))){
		cleanup();
		return -4;
	}
	for (int i=0;i<128;i++)
		data.midiNotes[i] = new Note(new Envelope(1.0, 1.0, 0.5, 1.0));
	return 0;
}

int shutdown(){
	if (! executedOk(Pa_StopStream(stream))){
		cleanup();
		return -5;
	}
	if (! executedOk(Pa_CloseStream(stream))){
		cleanup();
		return -6;
	}
	return cleanup();
}

bool executedOk(PaError error){
	if (error == paNoError)
		return true;
	std::cout << "PortAudio encountered an error: "
		<< Pa_GetErrorText(error)
		<< std::endl;
	return false;
}

int cleanup(){
	for (int i=0;i<128;i++){
		delete data.midiNotes[i];
	}
	PaError error = Pa_Terminate();
	if (error != paNoError) {
		std::cout << "PortAudio could not shut down properly: "
		<< Pa_GetErrorText(error)
		<< std::endl;
		return -1;
	}
	return 0;
}

void noteOn(int midiNumber){
	data.midiNotes[midiNumber]->envelope->noteIsOn = true;
}

void noteOff(int midiNumber){
	data.midiNotes[midiNumber]->envelope->noteIsOn = false;
}

/*
 * JNI methods
 */
JNIEXPORT jint JNICALL Java_com_mangrajalkin_synth_Synth_startup
   (JNIEnv *env, jobject obj){
	return startup();
}

JNIEXPORT jint JNICALL Java_com_mangrajalkin_synth_Synth_shutdown
   (JNIEnv *env, jobject obj){
	return shutdown();
}

JNIEXPORT void JNICALL Java_com_mangrajalkin_synth_Synth_noteOn
   (JNIEnv *env, jobject obj, jint midiNumber){
	noteOn(midiNumber);
}

JNIEXPORT void JNICALL Java_com_mangrajalkin_synth_Synth_noteOff
   (JNIEnv *env, jobject obj, jint midiNumber){
	noteOff(midiNumber);
}
