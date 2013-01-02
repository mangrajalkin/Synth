package com.mangrajalkin.synth;
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.util.Map;
import java.util.HashMap;
public class UniversalListener extends WindowAdapter implements MouseListener, KeyListener{
	private Synth synth;
	private Map<Integer, Integer> keyBindings;
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
	@Override
	public void windowClosing(WindowEvent e){
		if (JOptionPane.showOptionDialog(null,
				"Are you sure you want to exit?",
				"Confirm Exit",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,null,null) == 0){
			synth.shutdown();
			System.exit(0);
		}
	}
	public void keyPressed(KeyEvent evt){
		Integer note = keyBindings.get(evt.getKeyCode());
		if (note != null)
			synth.noteOn(note);
	}
	
	public void keyReleased(KeyEvent evt){
		Integer note = keyBindings.get(evt.getKeyCode());
		if (note != null)
			synth.noteOff(note);
	}
	
	public void keyTyped(KeyEvent evt){}
	
	public void mouseClicked(MouseEvent evt){}
	
	public void mousePressed(MouseEvent evt){
		synth.noteOn((Integer)
			((JComponent)(evt.getComponent())).getClientProperty("MIDI"));
	}
	
	public void mouseReleased(MouseEvent evt){
		synth.noteOff((Integer)
			((JComponent)(evt.getComponent())).getClientProperty("MIDI"));
	}
	
	public void mouseEntered(MouseEvent evt){
		
	}
	
	public void mouseExited(MouseEvent evt){
		
	}
}
