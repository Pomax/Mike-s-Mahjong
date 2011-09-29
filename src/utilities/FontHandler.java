package utilities;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontHandler {

	/**
	 * Freely available from DejaVu Fonts at http://dejavu-fonts.org/wiki/Main_Page
	 */
	public static final String regular_font = "config/fonts/DejaVuCondensedSans.ttf";

	/**
	 * Freely available from Hakushuu at http://www.hakusyu.com/download/kyokan.html
	 */
	public static final String japanese_font = "config/fonts/hkgyoprokk.ttf";

	/**
	 * Set the master font for the component tree under "master" 
	 * @param master	Component tree root
	 * @param fontname	Font to load from file
	 * @throws IOException 
	 * @throws FontFormatException 
	 */
    public static void setMasterFont(Component master, String fontname) throws FontFormatException, IOException
    {
    	int type = Font.TRUETYPE_FONT;
    	int style = Font.PLAIN;
        Object[] objs = UIManager.getLookAndFeel().getDefaults().keySet().toArray();
        for(int i=0; i<objs.length; i++) {
            if(objs[i].toString().toUpperCase().indexOf(".FONT")!=-1) {
                Font font = Font.createFont(type , new File(fontname));
                font = font.deriveFont(style, 12f);
                UIManager.put(objs[i], new FontUIResource(font)); }}
        SwingUtilities.updateComponentTreeUI(master);
        master.repaint();
    }
	
}
