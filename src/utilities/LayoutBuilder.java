package utilities;

import net.miginfocom.swing.MigLayout;

public class LayoutBuilder {

	/**
	 * no frills layout: no insets, no gap, no visual padding
	 * @return
	 */
	public static MigLayout buildDefault()
	{
		return new MigLayout("insets 0 0 0 0, gap 0px, novisualpadding");
	}

	/**
	 * no frills vertical layout: flowy, no insets, no gap, no visual padding
	 * @return
	 */
	public static MigLayout buildVerticalDefault()
	{
		return new MigLayout("flowy, insets 0 0 0 0, gap 0px, novisualpadding");
	}
}
