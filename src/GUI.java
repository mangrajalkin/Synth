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
import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
public class GUI implements Runnable{
	private UniversalListener listener;
	private int startingNote;
	private int noteCount;
	
	// Simple constructor
	protected GUI(UniversalListener listener, int startingNote, int noteCount){
		this.listener = listener;
		this.startingNote = startingNote;
		this.noteCount = noteCount;
	}
	
	public void run(){
		// create a new frame for the program
		JFrame frame = new JFrame("Ben's Awesome Software Synth");
		// and a layered pane, so the black keys stay on top
		JLayeredPane pane = new JLayeredPane();
		// put the pane in the frame
		frame.add(pane);
		// allow focus for key event grabbing
		frame.setFocusable(true);
		// add the listener as a KeyListener
		frame.addKeyListener(listener);
		// insets to disable truncating key labels
		Insets insets = new Insets(0, 0, 0, 0);
		// keep track of white keys for placement
		int whiteKeyCount = 0;
		// create as many keys as requested
		for (int i=startingNote;i<=startingNote+noteCount;i++){
			// default to black keys
			boolean whiteKey = false;
			// create JButton for each key
			JButton key = new JButton();
			// add the listener as a Mouse and KeyListeners
			key.addMouseListener(listener);
			key.addKeyListener(listener);
			// add a client property to each key to keep track
			// of the MIDI note number
			key.putClientProperty("MIDI", i);
			// figure out which note the current
			// index applies to and set text and color
			// for white keys
			switch(i%12){
			case  0:
				key.setText("C");
				whiteKey = true;
				break;
			case  1:
				break;
			case  2:
				key.setText("D");
				whiteKey = true;
				break;
			case  3:
				break;
			case  4:
				key.setText("E");
				whiteKey = true;
				break;
			case  5:
				key.setText("F");
				whiteKey = true;
				break;
			case  6:
				break;
			case  7:
				key.setText("G");
				whiteKey = true;
				break;
			case  8:
				break;
			case  9:
				key.setText("A");
				whiteKey = true;
				break;
			case 10:
				break;
			case 11:
				key.setText("B");
				whiteKey = true;
				break;
			}
			if (whiteKey){
				// if it's a white key, decorate as such
				key.setBackground(WHITE);
				key.setMargin(insets);
				key.setVerticalAlignment(SwingConstants.BOTTOM);
				// and place in the current position
				key.setLocation(whiteKeyCount * 20, 0);
				key.setSize(20, 100);
				pane.add(key, 0, -1);
				// and increment the current position
				whiteKeyCount++;
			} else {
				// if it's a black key, decorate as such
				key.setBackground(BLACK);
				key.setSize(15, 70);
				// and place in the current position
				key.setLocation((whiteKeyCount * 20) - 10, 0);
				pane.add(key, 1, -1);
				// NOTE: we don't increment the position, since the black
				// keys overlap the white keys
			}
		}
		// disable default close operation and add listener so we 
		// can close the synth libraries properly
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(listener);
		// set the frame size and show the GUI
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
