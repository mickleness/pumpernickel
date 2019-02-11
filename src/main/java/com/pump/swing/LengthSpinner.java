/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.Length;

/**
 * A <code>JSpinner</code> that adjusts <code>Length</code> values.
 * <P>
 * You can monitor the unit this spinner is currently using two ways:
 * <P>
 * 1. Add a <code>PropertyChangeListener</code> to this object, and listen for
 * <code>PROPERTY_UNIT</code> changes. Likewise you can call
 * <code>putClientProperty(PROPERTY_UNIT, ...)</code> to change the unit
 * currently displayed.
 * <P>
 * 2. Get the <code>SpinnerModel</code> for this spinner, which will be a
 * <code>LengthSpinnerModel</code>. This class contains methods to get and set
 * the <code>Length.Unit</code> that is in use.
 * 
 * @see <a
 *      href="http://javagraphics.blogspot.com/2008/11/internationalization-measuring-lengths.html">Internationalization:
 *      Measuring Lengths</a>
 */
public class LengthSpinner extends JSpinner {
	private static final long serialVersionUID = 1L;

	public static BufferedImage createBlurbGraphic(Dimension preferredSize)
			throws Exception {
		final LengthSpinner spinner = new LengthSpinner(5, 1, 1, Length.INCH,
				"");
		JFrame frame = new JFrame();
		frame.getContentPane().add(spinner);
		frame.pack();

		final BufferedImage image = new BufferedImage(spinner.getWidth(),
				spinner.getHeight(), BufferedImage.TYPE_INT_ARGB);

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				Graphics2D g = image.createGraphics();
				spinner.paint(g);
				g.dispose();
			}
		});

		return image;
	}

	/**
	 * A simple demo program to test <code>LengthSpinners</code>.
	 * 
	 * @param args
	 *            the application's arguments. (This is unused.)
	 */
	public static void main(String[] args) {
		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lf);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridBagLayout());
		f.getContentPane().add(panel);
		panel.setPreferredSize(new Dimension(200, 70));

		final LengthSpinner lengthSpinner1 = new LengthSpinner(5, 36, 100,
				Length.INCH, "prefs");
		final LengthSpinner lengthSpinner2 = new LengthSpinner(5, 36, 100,
				Length.INCH, "prefs");
		final JLabel label1 = new JLabel(" ");
		final JLabel label2 = new JLabel(" ");
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 0;
		panel.add(lengthSpinner1, c);
		c.gridy++;
		panel.add(lengthSpinner2, c);
		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx++;
		c.gridy = 0;
		panel.add(label1, c);
		c.gridy++;
		panel.add(label2, c);
		c.gridx++;
		c.gridy = 0;
		c.gridheight = 2;
		c.weightx = 1;
		panel.add(new JPanel(), c);

		final DecimalFormat format = new DecimalFormat("#.###");

		ChangeListener changeListener1 = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				LengthSpinnerModel model = (LengthSpinnerModel) lengthSpinner1
						.getModel();
				Length l = (Length) model.getValue();
				label1.setText(format.format(l.getValue(Length.METER)) + " m");
			}
		};
		ChangeListener changeListener2 = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				LengthSpinnerModel model = (LengthSpinnerModel) lengthSpinner2
						.getModel();
				Length l = (Length) model.getValue();
				label2.setText(format.format(l.getValue(Length.FOOT)) + " ft");
			}
		};

		lengthSpinner1.addChangeListener(changeListener1);
		lengthSpinner2.addChangeListener(changeListener2);

		// force the labels to update once
		changeListener1.stateChanged(null);
		changeListener2.stateChanged(null);

		LengthSpinnerGroup group = new LengthSpinnerGroup();
		group.add(lengthSpinner1);
		group.add(lengthSpinner2);

		f.pack();
		f.setVisible(true);
	}

	public static final String PROPERTY_UNIT = "unit";

	/**
	 * This is where the last-used unit is stored for
	 * <code>LengthSpinners</code> with a shared <code>preferenceKey</code>. By
	 * default this value is: <BR>
	 * <code>Preferences.userNodeForPackage(LengthSpinner.class)</code>
	 */
	public static Preferences prefs = Preferences
			.userNodeForPackage(LengthSpinner.class);
	private String preferenceKey = null;

	private ChangeListener unitChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			LengthSpinnerModel model = (LengthSpinnerModel) getModel();
			Length.Unit currentUnit = model.getCurrentUnit();
			putClientProperty(PROPERTY_UNIT, currentUnit);
		}
	};

	private PropertyChangeListener unitPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			Length.Unit newUnit = (Length.Unit) e.getNewValue();
			LengthSpinnerModel model = (LengthSpinnerModel) getModel();
			model.setUnit(newUnit);

			if (preferenceKey != null) {
				prefs.put(preferenceKey, newUnit.getAbbreviation());
			}
		}
	};

	private static Map<Length.Unit, Length> defaultStepTable = new HashMap<>();

	static {
		defaultStepTable.put(Length.INCH, new Length(1, Length.INCH));
		defaultStepTable.put(Length.FOOT, new Length(1, Length.FOOT));
		defaultStepTable.put(Length.MILLIMETER,
				new Length(1, Length.MILLIMETER));
		defaultStepTable.put(Length.CENTIMETER,
				new Length(1, Length.CENTIMETER));
		defaultStepTable.put(Length.DECIMETER, new Length(1, Length.DECIMETER));
		defaultStepTable.put(Length.METER, new Length(.5, Length.METER));
		defaultStepTable.put(Length.YARD, new Length(.5, Length.YARD));
	}

	private static Map<Length.Unit, Length> createTable(double inch, double cm) {
		Map<Length.Unit, Length> table = new HashMap<>();

		table.put(Length.INCH, new Length(inch, Length.INCH));
		table.put(Length.FOOT, new Length(inch / 12, Length.FOOT));
		table.put(Length.YARD, new Length(inch / 36, Length.YARD));
		table.put(Length.MILLIMETER, new Length(cm * 10, Length.MILLIMETER));
		table.put(Length.CENTIMETER, new Length(cm, Length.CENTIMETER));
		table.put(Length.DECIMETER, new Length(cm / 10, Length.DECIMETER));
		table.put(Length.METER, new Length(cm / 100, Length.METER));

		return table;
	}

	private static Map<Length.Unit, Length> wrap(double v, Length.Unit u) {
		Map<Length.Unit, Length> table = new HashMap<Length.Unit, Length>();
		table.put(u, new Length(v, u));
		return table;
	}

	/**
	 * Creates a new <code>LengthSpinner</code> using numbers meant for only 1
	 * type of unit.
	 * <P>
	 * This is not really recommended, unless your users have to comply with
	 * certain length restrictions. For example, if the default unit is inch and
	 * the step size is 1, then when the user increases or decreases this
	 * spinner it will always use a step size of 1 inch. So if this spinner is
	 * set to work in centimeters: then the user will see a step size of 2.54
	 * cm.
	 * 
	 * @param value
	 *            the initial value
	 * @param min
	 *            the minimal value
	 * @param max
	 *            the maximum value
	 * @param stepSize
	 *            the step size
	 * @param unit
	 *            the default unit
	 * @param numberFormat
	 *            an optional format to specify how to round/display values.
	 * @param preferenceKey
	 *            an optional key to save the unit the user views this data in
	 *            across sessions. If this is null, then the preferred unit is
	 *            not saved between sessions. If this is non-null, then this key
	 *            is used to store/retrieve the unit the user prefers for this
	 *            spinner (and all other spinners that share the same key).
	 */
	public LengthSpinner(double value, double min, double max, double stepSize,
			Length.Unit unit, DecimalFormat numberFormat, String preferenceKey) {
		this(value, wrap(min, unit), wrap(max, unit), wrap(stepSize, unit),
				unit, numberFormat, preferenceKey);
	}

	/**
	 * Creates a <code>LengthSpinner</code> with a "standard" set of step sizes
	 * and minimums of zero.
	 * 
	 * @param value
	 *            the initial value, in the default unit
	 * @param inchMax
	 *            the maximum value in inches. (The maximum value for feet/yards
	 *            will be computed from this value.)
	 * @param cmMax
	 *            the maximum value in centimeters. (The maximum value for other
	 *            metric lengths will be computed from this value.)
	 * @param defaultUnit
	 *            the default unit to use when the preferenceKey is null or
	 *            uninitialized.
	 * @param preferenceKey
	 *            an optional key to save the unit the user views this data in
	 *            across sessions. If this is null, then the preferred unit is
	 *            not saved between sessions. If this is non-null, then this key
	 *            is used to store/retrieve the unit the user prefers for this
	 *            spinner (and all other spinners that share the same key).
	 */
	public LengthSpinner(double value, double inchMax, double cmMax,
			Length.Unit defaultUnit, String preferenceKey) {
		this(value, 0, 0, inchMax, cmMax, defaultUnit, preferenceKey);
	}

	/**
	 * Creates a <code>LengthSpinner</code> with a "standard" set of step sizes
	 * (each step size is 1 or .5).
	 * 
	 * @param value
	 *            the initial value, in the default unit
	 * @param inchMin
	 *            the minimum value in inches. (The minimum value for feet/yards
	 *            will be computed from this value.)
	 * @param cmMin
	 *            the minimum value in centimeters. (The minimum value for other
	 *            metric lengths will be computed from this value.)
	 * @param inchMax
	 *            the maximum value in inches. (The maximum value for feet/yards
	 *            will be computed from this value.)
	 * @param cmMax
	 *            the maximum value in centimeters. (The maximum value for other
	 *            metric lengths will be computed from this value.)
	 * @param defaultUnit
	 *            the default unit to use when the preferenceKey is null or
	 *            uninitialized.
	 * @param preferenceKey
	 *            an optional key to save the unit the user views this data in
	 *            across sessions. If this is null, then the preferred unit is
	 *            not saved between sessions. If this is non-null, then this key
	 *            is used to store/retrieve the unit the user prefers for this
	 *            spinner (and all other spinners that share the same key).
	 */
	public LengthSpinner(double value, double inchMin, double cmMin,
			double inchMax, double cmMax, Length.Unit defaultUnit,
			String preferenceKey) {
		this(value, createTable(inchMin, cmMin), createTable(inchMax, cmMax),
				defaultStepTable, defaultUnit, null, preferenceKey);
		// by default we're going to allow for millimeters; so let's
		// shorten a little bit:
		setColumns(getColumns() - 1);
	}

	/**
	 * Creates a new <code>LengthSpinner</code>.
	 * <P>
	 * This is the constructor all other constructors delegate to.
	 * 
	 * @param value
	 *            the initial value, in the default unit.
	 * @param mins
	 *            a table of minimum values. Each entry in this table has a
	 *            key/value pair of Length.Unit/Length. Depending on the unit
	 *            the user is currently using, different minimums may be used.
	 *            If a minimum is not defined for the current unit, then the
	 *            minimum for the default unit is used. (At the very least this
	 *            table <i>must</i> contain an entry for the default unit; an
	 *            exception is thrown otherwise.)
	 * @param maxs
	 *            a table of maximum values. Each entry in this table has a
	 *            key/value pair of Length.Unit/Length. Depending on the unit
	 *            the user is currently using, different maximums may be used.
	 *            If a maximum is not defined for the current unit, then the
	 *            maximum for the default unit is used. (At the very least this
	 *            table <i>must</i> contain an entry for the default unit; an
	 *            exception is thrown otherwise.)
	 * @param stepSizes
	 *            a table of step size values. Each entry in this table has a
	 *            key/value pair of Length.Unit/Length. Depending on the unit
	 *            the user is currently using, different step sizes may be used.
	 *            If a step size is not defined for the current unit, then the
	 *            step size for the default unit is used. (At the very least
	 *            this table <i>must</i> contain an entry for the default unit;
	 *            an exception is thrown otherwise.)
	 * @param unit
	 *            the default unit to use when the preferenceKey is null or
	 *            uninitialized.
	 * @param numberFormat
	 *            an optional format for the numeric part of the length.
	 * @param preferenceKey
	 *            an optional key to save the unit the user views this data in
	 *            across sessions. If this is null, then the preferred unit is
	 *            not saved between sessions. If this is non-null, then this key
	 *            is used to store/retrieve the unit the user prefers for this
	 *            spinner (and all other spinners that share the same key).
	 */
	public LengthSpinner(double value, Map<Length.Unit, Length> mins,
			Map<Length.Unit, Length> maxs, Map<Length.Unit, Length> stepSizes,
			Length.Unit unit, DecimalFormat numberFormat, String preferenceKey) {
		super(new LengthSpinnerModel(value, mins, maxs, stepSizes, unit));
		this.preferenceKey = preferenceKey;

		LengthSpinnerModel model = (LengthSpinnerModel) getModel();

		String preferredUnitName = preferenceKey == null ? null : prefs.get(
				preferenceKey, null);
		if (preferredUnitName != null) {
			Length.Unit preferredUnit = Length.getUnitByName(preferredUnitName);
			model.setUnit(preferredUnit);
		}
		putClientProperty(PROPERTY_UNIT, model.getCurrentUnit());

		setEditor(new LengthSpinnerEditor(this, numberFormat));

		model.addChangeListener(unitChangeListener);
		addPropertyChangeListener(PROPERTY_UNIT, unitPropertyListener);
	}

	/**
	 * This directly sets the number of columns in the text field in the spinner
	 * editor.
	 * <P>
	 * The field picks a reasonable number of default columns, but you may find
	 * it necessary to increase this in certain cases.
	 */
	public void setColumns(int c) {
		LengthSpinnerEditor editor = (LengthSpinnerEditor) getEditor();
		editor.setColumns(c);
	}

	/**
	 * This returns the number of columns the text field in the spinner editor
	 * is designed for.
	 */
	public int getColumns() {
		LengthSpinnerEditor editor = (LengthSpinnerEditor) getEditor();
		return editor.getColumns();
	}

	/**
	 * Sets the spinner editor. If the argument is not a
	 * <code>LengthSpinnerEditor</code> then an
	 * <code>IllegalArgumentException</code> is thrown.
	 */
	@Override
	public void setEditor(JComponent c) {
		if (getEditor() instanceof LengthSpinnerEditor
				&& (c instanceof LengthSpinnerEditor) == false)
			throw new IllegalArgumentException(
					"LengthSpinner.setEditor() can only be called for LengthSpinnerEditors ("
							+ c.getClass().getName() + ")");
		super.setEditor(c);
	}

	/**
	 * Sets the spinner model. If the argument is not a
	 * <code>LengthSpinnerModel</code> then an
	 * <code>IllegalArgumentException</code> is thrown.
	 */
	@Override
	public void setModel(SpinnerModel c) {
		if (getModel() instanceof LengthSpinnerModel
				&& (c instanceof LengthSpinnerModel) == false)
			throw new IllegalArgumentException(
					"LengthSpinner.setModel() can only be called for LengthSpinnerModels ("
							+ c.getClass().getName() + ")");
		super.setModel(c);
	}

	/**
	 * Returns the current value as a <code>Length</code>.
	 */
	@Override
	public Object getValue() {
		return super.getValue();
	}

	/**
	 * Sets the current value.
	 * 
	 * @param value
	 *            the new value.
	 */
	@Override
	public void setValue(Object value) {
		setValue(value, true);
	}

	/**
	 * Sets the current value.
	 * 
	 * @param value
	 *            the new value.
	 * @param changeUnitsToMatchArgument
	 *            if this is false, then the units currently being used in this
	 *            <code>LengthSpinner</code> are retained. Otherwise this
	 *            spinner takes on the units of the argument.
	 */
	public void setValue(Object value, boolean changeUnitsToMatchArgument) {
		if (changeUnitsToMatchArgument == false) {
			Length newV = (Length) value;
			Length oldV = (Length) getValue();
			Length.Unit oldUnit = oldV.getUnit();
			if (oldUnit.equals(newV.getUnit()) == false) {
				double d = newV.getValue(oldUnit);
				newV = new Length(d, oldUnit);
			}

			value = newV;
		}
		super.setValue(value);
	}
}