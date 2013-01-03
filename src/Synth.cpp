#include "Synth.h"

int main(int argc, char**argv){
	JavaVMOption options[3];
	JNIEnv *env;
	JavaVM *jvm;
	JavaVMInitArgs vmArgs;
	long status;
	jclass clazz;
	jmethodID method;
	jvalue *argArray = 0;

	// Set the command-line options for the JVM
	options[0].optionString = (char *)"-Djava.class.path=./SynthGUI.jar";
	options[1].optionString = (char *)"-Djava.library.path=/var/dev/synth/bin";
	options[2].optionString = (char *)"-verbose";
	memset(&vmArgs, 0, sizeof(vmArgs));
	vmArgs.version = JNI_VERSION_1_2;
	vmArgs.nOptions = 3;
	vmArgs.options = options;
	status = JNI_CreateJavaVM(&jvm, (void**)&env, &vmArgs);
	
	if (status != JNI_ERR){
		clazz = env->FindClass("com/mangrajalkin/synth/Synth");
		jthrowable e = env->ExceptionOccurred();
		if (e){
			env->ExceptionDescribe();
		} else {
			method = env->GetStaticMethodID(clazz,
				"main",
				"([java/lang/String;)V");
			env->CallStaticVoidMethodA(clazz, method, argArray);
			//jvm->DestroyJavaVM();
			return 0;
		}
	} else {
		std::cout << "Error loading JVM" << std::endl;
	}
	return -1;
}
