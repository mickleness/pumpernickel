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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

public abstract class AutocompleteTextField<T> extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public static interface AutocompleteModelChangeListener<T> {
		public void selectionChange(T[] suggestions,int oldSelection,int oldIndication,int newSelection,int newIndication);
		public void indicationChange(T[] suggestions,int oldIndication,int newIndication);
		public void suggestionChange(T[] oldSuggestions,int oldSelection,int oldIndication,T[] newSuggestions,int newSelection,int newIndication);
	}
	
	public static class DefaultSuggestionModel<K> implements SuggestionModel<K> {
		K[] array;
		public DefaultSuggestionModel(K[] array) {
			this.array = array;
		}
		
		@Override
		public void refreshSuggestions(AutocompleteModel<K> dataModel,
				AutocompleteTextField<K> field) {
			String unselectedText = field.getUnselectedText().toLowerCase();
			K prevSelection = dataModel.getSelectedSuggestion();
			K prevIndication = dataModel.getIndicatedSuggestion();
			List<K> matchingList = new ArrayList<K>();
			int newSelectionIndex = -1;
			int newIndicationIndex = -1;
			for(K k : array) {
				String s = field.convertSuggestionToString(k).toLowerCase();
				if(s.contains(unselectedText)) {
					matchingList.add(k);
					if(prevSelection==k)
						newSelectionIndex = matchingList.size()-1;
					if(prevIndication==k)
						newIndicationIndex = matchingList.size()-1;
				}
			}
			K[] newArray = (K[])Array.newInstance( array.getClass().getComponentType(), matchingList.size());
			matchingList.toArray(newArray);
			dataModel.setSuggestions(newArray, newSelectionIndex, newIndicationIndex);
		}
	}
	
	public static interface SuggestionModel<K> {
		/** This may call <code>model.setSuggestions(..)</code>. This is invoked
		 * after the contents of this text field change. This is executed on
		 * the EDT, so it needs to be relatively fast (or pass harder work off to
		 * another thread).
		 */
		public void refreshSuggestions(AutocompleteModel<K> dataModel,AutocompleteTextField<K> field);
	}

	public static class AutocompleteModel<T> {
		List<AutocompleteModelChangeListener<T>> changeListeners = new ArrayList<AutocompleteModelChangeListener<T>>();
		int selectedIndex;
		int indicatedIndex;
		T[] suggestions;
		
		public T getIndicatedSuggestion() {
			if(indicatedIndex<0) return null;
			return suggestions[indicatedIndex];
		}
		
		public T getSelectedSuggestion() {
			if(selectedIndex<0) return null;
			return suggestions[selectedIndex];
		}
		
		public boolean cycleSelectedSuggestion(int delta) {
			int origValue = selectedIndex;
			int newValue = cycleSuggestion(origValue, delta);
			if(origValue==newValue)
				return false;
			
			setSuggestions(suggestions, newValue, indicatedIndex);
			return true;
		}

		
		public boolean cycleIndicatedSuggestion(int delta) {
			int origValue = indicatedIndex;
			int newValue = cycleSuggestion(origValue, delta);
			if(origValue==newValue)
				return false;
			
			setSuggestions(suggestions, selectedIndex, newValue);
			return true;
		}
		
		private int cycleSuggestion(int origValue, int delta) {
			if(delta==0) throw new IllegalArgumentException("delta must be nonzero");
			
			if(suggestions==null || suggestions.length==0)
				return -1;
			
			if(origValue<0) {
				//special behavior for when there is no selection:
				if(delta>0) {
					return 0;
				} else {
					return suggestions.length-1;
				}
			}
			int newIndex = (origValue + delta + suggestions.length)%suggestions.length;
			return newIndex;
			
		}
		
		public synchronized T[] getSuggestions() {
			return suggestions;
		}
		
		public synchronized void setSuggestions(T[] newSuggestions,int newSelectedIndex,int newIndicatedIndex) {
			if(newSuggestions==null) throw new NullPointerException();
			for(int a = 0; a<newSuggestions.length; a++) {
				if(newSuggestions[a]==null)
					throw new NullPointerException("element "+a+" was null");
			}
			
			T[] oldSuggestions = suggestions;
			int oldSelection = selectedIndex;
			int oldIndication = indicatedIndex;
			
			boolean listChange = false;
			if(suggestions==null || suggestions.length!=newSuggestions.length)
				listChange = true;
			for(int a = 0; (!listChange) && a<suggestions.length; a++) {
				if(!suggestions[a].equals(newSuggestions[a]))
					listChange = true;
			}
			suggestions = newSuggestions;
			boolean selectedIndexChange = selectedIndex!=newSelectedIndex;
			boolean indicatedIndexChange = indicatedIndex!=newIndicatedIndex;
			selectedIndex = newSelectedIndex;
			indicatedIndex = newIndicatedIndex;
			
			if(listChange) {
				for(AutocompleteModelChangeListener<T> l : changeListeners) {
					try {
						l.suggestionChange(oldSuggestions, oldSelection, oldIndication, newSuggestions, selectedIndex, indicatedIndex);
					} catch(RuntimeException e) {
						e.printStackTrace();
					}
				}
			} else if(selectedIndexChange) {
				for(AutocompleteModelChangeListener<T> l : changeListeners) {
					try {
						l.selectionChange(newSuggestions, oldSelection, oldIndication, selectedIndex, indicatedIndex);
					} catch(RuntimeException e) {
						e.printStackTrace();
					}
				}
			} else if(indicatedIndexChange) {
				for(AutocompleteModelChangeListener<T> l : changeListeners) {
					try {
						l.indicationChange(newSuggestions, oldIndication, indicatedIndex);
					} catch(RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public void addChangeListener(AutocompleteModelChangeListener<T> l) {
			changeListeners.add(l);
		}
		
		public void removeChangeListener(AutocompleteModelChangeListener<T> l) {
			changeListeners.remove(l);
		}
	}
	
	DocumentListener docListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		Runnable refreshSuggestionsRunnable = new Runnable() {
			public void run() {
				if(suggestionModel!=null)
					suggestionModel.refreshSuggestions(model, AutocompleteTextField.this);
				if(preferences!=null) {
					preferences.put(preferenceKey, getText());
				}
			}
		};
		@Override
		public void changedUpdate(DocumentEvent e) {
			SwingUtilities.invokeLater(refreshSuggestionsRunnable);
		}
		
	};
	
	AutocompleteModelChangeListener<T> modelListener = new AutocompleteModelChangeListener<T>() {

		@Override
		public void selectionChange(T[] suggestions, int oldSelection,int oldIndication,int newSelection,int newIndication) {
			updateTextField();
			updatePopupVisibility(suggestions, newIndication);
		}
		
		private void updateTextField() {
			T selection = model.getSelectedSuggestion();
			if(selection==null) return;
			
			String text = getText();
			final String newText = convertSuggestionToString(selection);
			
			final String textWithoutSelection = getUnselectedText();
			if(newText.startsWith(textWithoutSelection)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getDocument().removeDocumentListener(docListener);
						try {
							setText(newText);
							setSelectionStart(textWithoutSelection.length());
							setSelectionEnd(newText.length());
						} finally {
							getDocument().addDocumentListener(docListener);
						}
					}
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getDocument().removeDocumentListener(docListener);
						try {
							setText(newText);
							setSelectionStart(newText.length());
							setSelectionEnd(newText.length());
						} finally {
							getDocument().addDocumentListener(docListener);
						}
					}
				});
			}
		}

		@Override
		public void suggestionChange(T[] oldSuggestions, int oldSelection,int oldIndication,
				T[] newSuggestions, int newSelection,int newIndication) {
			updateTextField();

			updatePopupVisibility(newSuggestions, newIndication);
		}

		private void updatePopupVisibility(T[] newSuggestions, int newIndication) {
			if(newSuggestions!=null && newSuggestions.length>=1) {
				suggestionList.removeListSelectionListener(listListener);
				try {
					suggestionList.setListData(model.getSuggestions());
					showPopup();
					suggestionList.setSelectedIndex(newIndication);
					if(newIndication>=0) {
						Rectangle r = suggestionList.getCellBounds(newIndication, newIndication);
						if(!suggestionList.getVisibleRect().contains(r.x+r.width/2,r.y+r.height/2)) {
							JScrollBar vbar = scrollPane.getVerticalScrollBar();
							int v = suggestionList.getFixedCellHeight()*newIndication;
							vbar.setValue( v );
						}
						//why didn't this work:
						//Rectangle r = suggestionList.getCellBounds(newIndication, newIndication);
						//scrollPane.scrollRectToVisible(r);
					}
				} finally {
					suggestionList.addListSelectionListener(listListener);
				}
			} else {
				popup.setVisible(false);
			}
		}

		@Override
		public void indicationChange(T[] suggestions, int oldIndication,
				int newIndication) {
			updatePopupVisibility(suggestions, newIndication);
		}
		
	};
	
	ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			getModel().setSuggestions(model.getSuggestions(), model.selectedIndex, suggestionList.getSelectedIndex());
		}
	};
	
	protected SuggestionModel<T> suggestionModel = null;
	protected JList<T> suggestionList = new JList<T>();
	protected JScrollPane scrollPane = new JScrollPane(suggestionList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	protected AutocompleteModel<T> model = new AutocompleteModel<T>();
	protected JPopupMenu popup = new JPopupMenu();
	/** An optional Preferences object use to store and autopopulate this field. 
	 * @see #preferenceKey
	 */
	protected Preferences preferences;
	/** This optional key name combined with the "preference" field is used to store/retrieve the last
	 * value the user applied to this field
	 * 
	 * @see #preferences
	 */
	protected String preferenceKey;
	
	KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER) {
				if(popup.isShowing()) {
					model.setSuggestions(model.suggestions, model.indicatedIndex, model.indicatedIndex);
					popup.setVisible(false);
					return;
				}
				int length = getText().length();
				setSelectionStart(length);
				setSelectionEnd(length);
				e.consume();
			} else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
				if(popup.isShowing()) {
					if(cycleIndicatedSuggestionForward())
						e.consume();
				} else {
					if(suggestionModel!=null)
						suggestionModel.refreshSuggestions(model, AutocompleteTextField.this);
					if(cycleIndicatedSuggestionForward()) {
						e.consume();
					} else if(model.suggestions.length>0) {
						showPopup();
						e.consume();
					}
				}
			} else if(e.getKeyCode()==KeyEvent.VK_UP) {
				if(popup.isShowing()) {
					if(cycleIndicatedSuggestionBackward())
						e.consume();
				} else {
					if(suggestionModel!=null)
						suggestionModel.refreshSuggestions(model, AutocompleteTextField.this);
					if(cycleIndicatedSuggestionBackward())
						e.consume();
				}
			}
			if((!e.isConsumed()) && popup.isShowing()) {
				popup.setVisible(false);
				e.consume();
			}
		}
	};

	public AutocompleteTextField() {
		super();
		setup();
	}

	public AutocompleteTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		setup();
	}

	public AutocompleteTextField(int columns) {
		super(columns);
		setup();
	}

	public AutocompleteTextField(String text, int columns) {
		super(text, columns);
		setup();
	}

	public AutocompleteTextField(String text) {
		super(text);
		setup();
	}

	/** Select the next indicated suggestion.
	 * 
	 * @return true if a change occurred. False otherwise.
	 * <p>This may return false if the only suggestion is already selected,
	 * or if no suggestions are available.
	 */
	public boolean cycleIndicatedSuggestionForward() {
		return model.cycleIndicatedSuggestion(1);
	}

	/** Select the previous indication suggestion.
	 * 
	 * @return true if a change occurred. False otherwise.
	 * <p>This may return false if the only suggestion is already selected,
	 * or if no suggestions are available.
	 */
	public boolean cycleIndicatedSuggestionBackward() {
		return model.cycleIndicatedSuggestion(-1);
	}
	
	/** Install listeners during construction that are unique for the AutocompleteTextField.
	 */
	private void setup() {
		suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		popup.add(scrollPane);
		suggestionList.setFixedCellHeight(20);
		suggestionList.setFocusable(false);
		scrollPane.setFocusable(false);
		popup.setFocusable(false);
		suggestionList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int i = suggestionList.getUI().locationToIndex(suggestionList, e.getPoint());
				getModel().setSuggestions(model.suggestions, model.selectedIndex, i);
			}
		});
		
		getDocument().addDocumentListener(docListener);
		addKeyListener(keyListener);
		model.addChangeListener(modelListener);
		suggestionList.addListSelectionListener(listListener);
	}
	
	@Override
	public void setText(String s) {
		//don't let someone else calling setText() ruin our selection
		if(getText().equals(s))
			return;
		super.setText(s);
	}
	
	public AutocompleteModel<T> getModel() {
		return model;
	}
	
	public void setSuggestionModel(SuggestionModel<T> suggestionModel) {
		this.suggestionModel = suggestionModel;
		suggestionModel.refreshSuggestions(model, this);
	}
	
	protected abstract String convertSuggestionToString(T suggestion);
	
	protected String getUnselectedText() {
		String text = getText();
		int s1 = getSelectionStart();
		int s2 = getSelectionEnd();
		return text.substring(0, s1)+text.substring(s2);
	}
	
	protected void showPopup() {
		int rowCount = Math.min(model.suggestions.length, 5);
		Dimension d = new Dimension(getWidth(), rowCount*suggestionList.getFixedCellHeight());
		applyBorderInsets(scrollPane, d);
		scrollPane.setPreferredSize(d);
		if(popup.isShowing()) {
			popup.revalidate();
		} else if(AutocompleteTextField.this.isShowing()) {
			popup.show(AutocompleteTextField.this, 0, getHeight());
		}
	}
	
	private void applyBorderInsets(JComponent jc,Dimension d) {
		Border b = jc.getBorder();
		if(b==null)
			return;
		Insets i = b.getBorderInsets(jc);
		if(i==null) return;
		d.width += i.left + i.right;
		d.height += i.top + i.bottom;
	}

	public void setPreferenceKey(Class<?> type, String string)
	{
		if(type==null)
			throw new NullPointerException();
		if(string==null)
			throw new NullPointerException();
		
		preferences = java.util.prefs.Preferences.userNodeForPackage(type);
		preferenceKey = string;
		
		String value = preferences.get(preferenceKey, null);
		if(value!=null) {
			setText(value);
		}
	}
}