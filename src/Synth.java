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
package com.mangrajalkin.synth;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
public class Synth{

	// native library method declarations
	protected native int startup();
	protected native int shutdown();
	protected native void noteOn(int midiNumber);
	protected native void noteOff(int midiNumber);

	public static void main(String[] args){
		// try to load the native library
		try{
			System.loadLibrary("synth");
		// If we can't load the library, try createing a new
		// JVM with the jar's location in the library load path
		} catch (UnsatisfiedLinkError u){
			// Get the path to the currently running jar
			File jar = null;
			try{
				jar = new File(Synth.class.getProtectionDomain()
					.getCodeSource().getLocation()
						.toURI().getPath());
			} catch (URISyntaxException e){
				e.printStackTrace();
			}
			// if we got the file path,
			if (jar != null){
				// get the containing folder
				String location = jar.getParentFile().getPath();
				ArrayList<String> argList = new ArrayList<String>();
				argList.add("java");
				List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
				// If a java.library.path argument has been added, remove it. We'll add it later.
				for (int i=0;i<inputArgs.size();i++){
					if (inputArgs.get(i).startsWith("-Djava.library.path=")){
						inputArgs.remove(i);
						break;
					}
				}
				// Append the library path with the new location
				argList.add("-Djava.library.path="
					+System.getProperty("java.library.path")
					+File.pathSeparator
					+location);
				// Add all the arguments
				argList.addAll(inputArgs);
				// add the jar name
				argList.add("-classpath");
				argList.add(jar.getPath());
				argList.add("com.mangrajalkin.synth.Synth");
				// Add the args passed to main()
				argList.addAll(Arrays.asList(args));
				try{
					// Create a Process for the new JVM instance,
					// redirect cout cin and cerr, and start the process
					final Process childJVM = new ProcessBuilder(argList)
						.inheritIO()
						.directory(new File(location))
						.start();
					// Create a shutdown hook, so that ctrl-C, SIGTERM, etc gets passed on to
					// the child process
					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
						public void run(){
							childJVM.destroy();
						}
					}));
					// Make sure the main process waits for the child, so that signals get
					// forwarded correctly, and return the child's return value
					try{
						childJVM.waitFor();
						System.exit(childJVM.exitValue());
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				} catch (java.io.IOException e){
					e.printStackTrace();
				}
			}
		}
		// create a new Synth object
		Synth synth = new Synth();
		// pass it to a new Listener
		UniversalListener listener = new UniversalListener(synth);
		// and pass it to a new GUI and pass it off to Swing
		javax.swing.SwingUtilities.invokeLater(
			new GUI(listener, 48, 24));
	}
	
	protected Synth(){
		// Try to start up the native synth library
		if (startup() != 0){
			System.out.println("Could not start synth library");
			System.exit(0);
		}
	}
	
	protected void cleanup(){
		// try to clean up the synth library
		if (shutdown() != 0)
			System.out.println("Could not shut down synth library");
	}
}
