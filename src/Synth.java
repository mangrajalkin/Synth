package com.mangrajalkin.synth;
public class Synth{
	
	protected native int synthStartup();
	protected native int synthShutdown();
	protected native void synthNoteOn(int midiNumber);
	protected native void synthNoteOff(int midiNumber);
	
	static {System.loadLibrary("Synth");}
	
	public static void main(String[] args){
		System.out.println("Starting Java program...");
		Synth synth = new Synth();
		UniversalListener listener = new UniversalListener(synth);
		javax.swing.SwingUtilities.invokeLater(
			new GUI(listener, 48, 24));
	}
	
	protected Synth(){
		if (synthStartup() != 0){
			System.out.println("Could not start native synth");
			System.exit(0);
		}
	}
	
	protected void noteOn(int midiNumber){
		synthNoteOn(midiNumber);
	}
	
	protected void noteOff(int midiNumber){
		synthNoteOff(midiNumber);
	}
	
	protected void shutdown(){
		if (synthShutdown() != 0)
			System.out.println("Could not shut down native synth");
	}
}
