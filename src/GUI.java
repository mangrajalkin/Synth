package com.mangrajalkin.synth;
import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
public class GUI implements Runnable{
	private UniversalListener listener;
	private int startingNote;
	private int noteCount;
	
	protected GUI(UniversalListener listener, int startingNote, int noteCount){
		this.listener = listener;
		this.startingNote = startingNote;
		this.noteCount = noteCount;
	}
	
	public void run(){	
		JFrame frame = new JFrame("Ben's Awesome Software Synth");
		JLayeredPane pane = new JLayeredPane();
		frame.add(pane);
		frame.setFocusable(true);
		frame.addKeyListener(listener);
		Insets insets = new Insets(0, 0, 0, 0);
		int whiteKeyCount = 0;
		for (int i=startingNote;i<=startingNote+noteCount;i++){
			boolean whiteKey = false;		
			JButton key = new JButton();
			key.addMouseListener(listener);
			key.addKeyListener(listener);
			key.putClientProperty("MIDI", i);
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
				key.setBackground(WHITE);
				key.setMargin(insets);
				key.setVerticalAlignment(SwingConstants.BOTTOM);
				key.setLocation(whiteKeyCount * 20, 0);
				key.setSize(20, 100);
				pane.add(key, 0, -1);
				whiteKeyCount++;
			} else {
				key.setBackground(BLACK);
				key.setSize(15, 70);
				key.setLocation((whiteKeyCount * 20) - 10, 0);
				pane.add(key, 1, -1);
			}
		}
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(listener);
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
