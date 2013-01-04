#include "Synth.h"

int main(int argc, char**argv){
	JavaVMOption options[2];
	JNIEnv *env;
	JavaVM *jvm;
	JavaVMInitArgs vmArgs;
	long status;
	jclass clazz;
	jmethodID method;

	// Set the command-line options for the JVM
	options[0].optionString = (char *)"-Djava.class.path=./SynthGUI.jar";
	options[1].optionString = (char *)"-Djava.library.path=/var/dev/synth/bin";
	memset(&vmArgs, 0, sizeof(vmArgs));
	vmArgs.version = JNI_VERSION_1_2;
	vmArgs.nOptions = 2;
	vmArgs.options = options;
	status = JNI_CreateJavaVM(&jvm, (void**)&env, &vmArgs);
	
	if (status != JNI_ERR){
		clazz = env->FindClass("com/mangrajalkin/synth/Synth");
		if (env->ExceptionOccurred()){
			env->ExceptionDescribe();
		} else {
			method = env->GetStaticMethodID(clazz,
				"main",
				"()V");
			if (env->ExceptionOccurred()){
				env->ExceptionDescribe();
			} else {
				env->CallStaticVoidMethod(clazz, method);
				jvm->DestroyJavaVM();
				std::cout << "Done. Exiting" << std::endl;
				return 0;
			}
		}
	} else {
		std::cout << "Error loading JVM" << std::endl;
	}
	return -1;
}
