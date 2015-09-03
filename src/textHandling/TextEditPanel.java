package textHandling;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * This class makes the text edit panel. It designs
 * the GUI and makes the logic for editing text panel. This is the component put underneath
 * the text area where the text added is made input.
 * @author anmol
 *
 */
@SuppressWarnings("serial")
public class TextEditPanel extends JPanel {
	
	private JPanel subPanel; // Might be able to remove this
	private JTextArea textArea;
	private JScrollPane scrollPane;
	protected JTextField timeField;
	private JTextField fontSizeField;
	private JComboBox<String> fontSelectionComboBox;
	protected Font selectedFont;
	protected String selectedFontPath;
	private JComboBox<String> colorComboBox;
	private String[] colorStringArray = {"Black", "Green", "Red", "Blue", "Yellow"};
	private Color[] colorArray = {Color.BLACK, Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW};
	private JCheckBox enableCheckBox;
	private boolean isEnabled = false;
	//protected JComboBox fontPlacement;
	//private String[] positions = {"Top","Middle","Bottom"};
	
	TextEditPanel (String title, Vector<String> fontNames, final ArrayList<Font> fontList) {
		
		
		//fontPlacement = new JComboBox(positions);
		
		Border blackline = BorderFactory.createLineBorder(Color.black);
		textArea = new JTextArea(10, 26);
		textArea.setDocument(new JTextAreaLimitedSize(100));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel(title));
		scrollPane = new JScrollPane(textArea);
		this.add(scrollPane);
		
		subPanel = new JPanel(new BorderLayout());
		
		timeField = new JTextField(2);
		timeField.setMaximumSize(new Dimension(50, 30));
		timeField.setDocument(new IntegerDocument());
		timeField.setText("10");
		fontSizeField = new JTextField(2);
		fontSizeField.setMaximumSize(new Dimension(50, 30));
		fontSizeField.setDocument(new IntegerDocument());
		fontSizeField.setText("12");
		
		fontSelectionComboBox = new JComboBox<String>(fontNames);
		
		// add a listner to the font selection box to show the dynamic changes
		fontSelectionComboBox.addItemListener(new ItemListener() {
			int index = 0;
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int newIndex = fontSelectionComboBox.getSelectedIndex();
	
				if (newIndex != index) {
					index = newIndex;
					selectedFont = fontList.get(index);
					
					if (fontSizeField.getText().isEmpty()) {
						fontSizeField.setText("12");
					}
					selectedFont = selectedFont.deriveFont(Float.parseFloat(fontSizeField.getText()));
					textArea.setFont(selectedFont);
				}
			}
			
		});
		
		fontSelectionComboBox.setPreferredSize(new Dimension(170, 20));
		// add a listener to the font size so that the dynamic change is shown
		fontSizeField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedFont = fontList.get(fontSelectionComboBox.getSelectedIndex());
				if (fontSizeField.getText().isEmpty()) {
					fontSizeField.setText("12");
				}
				selectedFont = selectedFont.deriveFont(Float.parseFloat(fontSizeField.getText()));
				textArea.setFont(selectedFont);
				
			}
		});
		
		
		JPanel p1 = new JPanel();
		enableCheckBox = new JCheckBox("Enable");
		p1.add(enableCheckBox);
		p1.add(new JLabel("Time to show text: "));
		p1.add(timeField);
	
		subPanel.add(p1, BorderLayout.NORTH);
		JPanel p2 = new JPanel();
		p2.add(new JLabel("Font size: "));
		p2.add(fontSizeField);
		colorComboBox = new JComboBox<String>(colorStringArray);
		p2.add(colorComboBox);
		//p2.add(fontPlacement);
		subPanel.add(p2, BorderLayout.CENTER);
	
		JPanel p3 = new JPanel();
		p3.add(new JLabel("Font: "));
		p3.add(fontSelectionComboBox);
		subPanel.add(p3, BorderLayout.SOUTH);
		subPanel.setMaximumSize(new Dimension(2000, 200));
		
		this.add(subPanel);
		this.setBorder(blackline);
	
		// add a listener to the cpmbobox so that the change to colour will be shown dynamically.
		colorComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedColorIndex = colorComboBox.getSelectedIndex();
				textArea.setForeground(colorArray[selectedColorIndex]);
				
			}
		});
		
		// add a listener to the enable checkbox to get the result when it is clicked
		enableCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isEnabled) {
					isEnabled = false;
				} else {
					isEnabled = true;
				}
			}
		});
		

	}
	
	/**
	 * returns true of there is text in the textarea, else it returns false
	 * @return
	 */
	public boolean hasText() {
		if (textArea.getText().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * sets the text in text area
	 * @return
	 */
	public String getText() {
		return textArea.getText();
	}
	
	/**
	 * gets the font index and returns it.
	 * @return
	 */
	public int getFontIndex() {
		return fontSelectionComboBox.getSelectedIndex();
	}
	
	/**
	 * Gets the time value and returns it. Default value is 10 seconds.
	 * @return
	 */
	public int getTimeValue() {
		try {
			return Integer.parseInt(timeField.getText());
		} catch (NumberFormatException e) {
			return 10;
		}
	}
	
	/**
	 * Gets the colour chosen and then returns it.
	 * @return
	 */
	public String getColor() {
		return (String)colorComboBox.getSelectedItem();
	}
	
	/**
	 * Gets the font size chosen and then returns it.
	 * @return
	 */
	public int getFontSize() {
		try {
			return Integer.parseInt(fontSizeField.getText());
		} catch (NumberFormatException e) {
			return 12;
		}
	}
	
	/**
	 * This method returns the boolean true if text is to be added and false
	 * if not to be added.
	 * @return
	 */
	public boolean shouldProcess() {
		return isEnabled;
	}
	
	/**
	 * This gets the settings in an arrau and then return a string array.
	 * @return
	 */
	public String[] getSettingsArray() {
		String enabled;
		if (isEnabled) {
			enabled = "1";
		} else {
			enabled = "0";
		}
		
		String[] settings = {enabled, timeField.getText(), fontSizeField.getText(), colorComboBox.getSelectedIndex() + "", fontSelectionComboBox.getSelectedIndex() + "", "{", textArea.getText(), "}"};
		
		return settings;
	}
	
	/**
	 * This sets the settings all at once when everything has been put in.
	 * @param settings
	 */
	public void setSettings(String[] settings) {
		
		if (settings[0].equals("0")) {
			enableCheckBox.setSelected(false);
		} else if (settings[0].equals("1")) {
			enableCheckBox.setSelected(true);
		} else {
			// TODO something went wrong warn user and move on to next setting
		}
		
		timeField.setText(settings[1]);
		fontSizeField.setText(settings[2]);
		try {
			colorComboBox.setSelectedIndex(Integer.parseInt(settings[3]));
		} catch (IllegalArgumentException e) {
			// TODO warn user and move on
		}
		try {
			fontSelectionComboBox.setSelectedIndex(Integer.parseInt(settings[4]));
		} catch (IllegalArgumentException e) {
			// TODO warn user and move on
		}
		
		textArea.setText(settings[5]);
		
		
	}
	
}
