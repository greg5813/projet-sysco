

import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Irc extends Frame {
	public TextArea		text;
	public TextField	data;
	SharedObject		sentence;
	static String		myName;

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}
		// create the graphical part
		new Irc(s);
	}

	public Irc(SharedObject s) {
	
		setLayout(new FlowLayout());
	
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);
	
		data=new TextField(60);
		add(data);
	
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		add(write_button);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		add(read_button);
		Button unlock_button = new Button("unlock");
		unlock_button.addActionListener(new unlockListener(this));
		add(unlock_button);
		
		setSize(470,300);
		text.setBackground(Color.black); 
		show();
		
		sentence = s;
	}
}



class readListener implements ActionListener {
	Irc irc;
	public readListener (Irc i) {
		irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// lock the object in read mode
		irc.sentence.lock_read();
		
		// invoke the method
		String s = ((Sentence)(irc.sentence.obj)).read();
		
		
		// display the read value
		irc.text.append(s+"\n");
		
		System.out.println("irc read");
	}
}

class writeListener implements ActionListener {
	Irc irc;
	public writeListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// get the value to be written from the buffer
        	String s = irc.data.getText();
        	
        	// lock the object in write mode
		irc.sentence.lock_write();
		
		// invoke the method
		((Sentence)(irc.sentence.obj)).write(Irc.myName+" wrote "+s);
		irc.data.setText("");
		
		System.out.println("irc write");
		
	}
}

class unlockListener implements ActionListener {
	Irc irc;
	public unlockListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
				
		// unlock the object
		irc.sentence.unlock();
		
		System.out.println("irc unlock");
	}
}
