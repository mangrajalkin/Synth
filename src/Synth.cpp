#include "Synth.h"
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
	
		float value = 0;
		int noteCount = 0;
		for(int i=0;i<128;i++){
			if (data->midiNotes[i]){
				double amplitude = 1.0;
				double frequency = A * std::pow(2, (i-69.0)/12.0);
				double omega = PI_2 * frequency;
				double phi = 0;
				value += amplitude * std::sin((omega * data->time) + phi);
				noteCount++;
			}
		}
		if (noteCount > 0){
			value /= noteCount;
			data->time += TIME_PER_FRAME;			
		} else {
			data->time = 0;
		}
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

int main(int argc, char **argv){
	int status = startup();
	
	if (status != 0)
		return status;
	
	noteOn(69);
	Pa_Sleep(2 * 1000);
	noteOn(60);
	Pa_Sleep(1 * 1000);
	noteOff(69);
	Pa_Sleep(8 * 1000);
	noteOff(60);
	
	return shutdown();
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
	data.midiNotes.set(midiNumber);
}

void noteOff(int midiNumber){
	data.midiNotes.reset(midiNumber);
}

/*
 * JNI methods
 */
JNIEXPORT jint JNICALL Java_com_mangrajalkin_synth_Synth_synthStartup
   (JNIEnv *env, jobject obj){
	return startup();
}

JNIEXPORT jint JNICALL Java_com_mangrajalkin_synth_Synth_synthShutdown
   (JNIEnv *env, jobject obj){
	return shutdown();
}

JNIEXPORT void JNICALL Java_com_mangrajalkin_synth_Synth_synthNoteOn
   (JNIEnv *env, jobject obj, jint midiNumber){
	noteOn(midiNumber);
}

JNIEXPORT void JNICALL Java_com_mangrajalkin_synth_Synth_synthNoteOff
   (JNIEnv *env, jobject obj, jint midiNumber){
	noteOff(midiNumber);
}
