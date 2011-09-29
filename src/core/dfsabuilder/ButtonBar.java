package core.dfsabuilder;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import utilities.LayoutBuilder;
import core.algorithm.dynamic.DynamicFSA;
import core.gui.ListeningJPanel;

public class ButtonBar extends ListeningJPanel {
	private static final long serialVersionUID = 1L;

	DFSAPanel dfsapanel;
	
	// ---
	JTextField name;
	JTextField value;
	// ---
	JButton open;
	JButton save;
	// ---
	JButton node;
	
	private JLabel getSeparator() { return new JLabel(" "); } 
	
	public ButtonBar(DFSAPanel dfsapanel)
	{
		setLayout(LayoutBuilder.buildDefault());
		setBackground(Color.GRAY);
		this.dfsapanel=dfsapanel;
		// ---
		JLabel namelabel = new JLabel("name");
		namelabel.setForeground(Color.WHITE);
		add(namelabel);
		add(getSeparator());
		name = new JTextField("dfsa name");
		name.setMargin(new Insets(0,0,0,0));
		name.setBorder(new LineBorder(Color.BLACK,1));
		name.addMouseListener(this);
		add(name);
		add(getSeparator());
		JLabel valuelabel = new JLabel(" value ");
		valuelabel.setForeground(Color.WHITE);
		add(valuelabel);
		add(getSeparator());
		value = new JTextField("dfsa value");
		value.setMargin(new Insets(0,0,0,0));
		value.setBorder(new LineBorder(Color.BLACK,1));
		value.addMouseListener(this);
		add(value);
		add(getSeparator());
		// ---
		open = new JButton("open");
		open.setBorder(new LineBorder(Color.BLACK,1));
		open.addActionListener(this);
		add(open);
		save = new JButton("save");
		save.setBorder(new LineBorder(Color.BLACK,1));
		save.addActionListener(this);
		add(save);
		// ---
		node = new JButton("node");
		node.setBorder(new LineBorder(Color.BLACK,1));
		node.addActionListener(this);
		add(node);
		setVisible(true);
	}
	
	// -------------------------------------------------------
	
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		if(source==open) { 
			DynamicFSA[] dfsa = load(); 
			if(dfsa!=null) dfsapanel.loadDFSAs(dfsa); }
		else if(source==save) { 
			String dfsa = dfsapanel.getStringForm();
			if(dfsa!=null) save(dfsa); }
		else if(source==node) { dfsapanel.newNode(); }
	}
	
	public void mouseClicked(MouseEvent event)
	{
		Object source = event.getSource();
		if(source==name) {
			if(name.getText().equals("dfsa name")) { 
				name.setText(""); }}
		else if(source==value) {
			if(value.getText().equals("dfsa value")) {
				value.setText(""); }}
	}
	
	// -------------------------------------------------------
	
	/**
	 * load a file
	 * @return
	 */
	public DynamicFSA[] load()
	{
		//Create a file chooser
		final JFileChooser fc = new JFileChooser(new File("."));
        int returnVal = fc.showDialog(this, "Open");
        // 'load'
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file = fc.getSelectedFile();
        	return DynamicFSA.loadDFSAs(file); }
        // cancelled
        else { return null; }	
	}

	/**
	 * save a file
	 * @param dfsa
	 * @return
	 */
	public boolean save(String dfsa)
	{
		//Create a file chooser
		final JFileChooser fc = new JFileChooser(new File("."));
        int returnVal = fc.showDialog(this, "Save");
        // 'save'
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file = fc.getSelectedFile();
        	boolean save = true;
        	if(file.exists()) {
        		String query = "Overwrite existing file?";
        		String title = "File already exists";
        		int optiontype = JOptionPane.YES_NO_OPTION;
        		int type = JOptionPane.WARNING_MESSAGE;
        		int overwrite = JOptionPane.showConfirmDialog(this,query,title,optiontype,type);
        		save = (overwrite==0);
        	}
        	if(save) { return save(dfsa, file); }
        	else { return false; }}
        // cancelled
        else { return false; }
	}
	
	// save to File resource
	private boolean save(String dfsa, File file)
	{
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(dfsa);
			out.flush();
			out.close();
			return true; }
		catch(IOException e) { e.printStackTrace(); }
		return false;
	}
}