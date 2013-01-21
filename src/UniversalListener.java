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
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.util.Map;
import java.util.HashMap;
/**
 * Listener to handle GUI events
 *
 * @author Ben Kern <Benjamin.L.Kern@gmail.com>
 */
public class UniversalListener implements MouseListener, KeyListener{
	/** Parent Synth to pass events back to */
	private Synth synth;
	/** Map to hold key bindings */
	private Map<Integer, Integer> keyBindings;
	/**
	 * Class constructor
	 *
	 * @param	synth	parent Synth object to pass events back to
	 */
	protected UniversalListener(Synth synth){
		this.synth = synth;
		keyBindings = new HashMap<Integer, Integer>();
		keyBindings.put(87, 52);
		keyBindings.put(69, 53);
		keyBindings.put(52, 54);
		keyBindings.put(82, 55);
		keyBindings.put(53, 56);
		keyBindings.put(84, 57);
		keyBindings.put(54, 58);
		keyBindings.put(89, 59);
		keyBindings.put(85, 60);
		keyBindings.put(56, 61);
		keyBindings.put(73, 62);
		keyBindings.put(57, 63);
		keyBindings.put(79, 64);
		keyBindings.put(80, 65);
	}

	/**
	 * Handle keypress events.
	 * Checks binding map for note.
	 * If it exists, turn the note on.
	 *
	 * @param	evt	the key event that triggered this function
	 */
	public void keyPressed(KeyEvent evt){
		Integer note = keyBindings.get(evt.getKeyCode());
		if (note != null)
			synth.noteOn(note);
	}

	/**
	 * Handle keyup events.
	 * Checks binding map for note.
	 * If it exists, turn note off.
	 *
	 * @param	evt	the key event that triggered this function
	 */
	public void keyReleased(KeyEvent evt){
		Integer note = keyBindings.get(evt.getKeyCode());
		if (note != null)
			synth.noteOff(note);
	}
	
	public void keyTyped(KeyEvent evt){}
	
	public void mouseClicked(MouseEvent evt){}

	/**
	 * Handle click events on keyboard keys.
	 * Retrieves the MIDI note number from the {@link JComponent} client property
	 * from the event's parent and turns that note on.
	 *
	 * @param	evt	the mouse event that triggered this function
	 */
	public void mousePressed(MouseEvent evt){
		synth.noteOn((Integer)
			((JComponent)(evt.getComponent())).getClientProperty("MIDI"));
	}

	/**
	 * Handle mouseup events on keyboard keys.
	 * Retrieves the MIDI note number from the {@link JComponent} client property
	 * from the event's parent and turns that note off.
	 *
	 * @param	evt	the mouse event that triggered this function
	 */
	public void mouseReleased(MouseEvent evt){
		synth.noteOff((Integer)
			((JComponent)(evt.getComponent())).getClientProperty("MIDI"));
	}
	
	public void mouseEntered(MouseEvent evt){
		
	}
	
	public void mouseExited(MouseEvent evt){
		
	}
}
