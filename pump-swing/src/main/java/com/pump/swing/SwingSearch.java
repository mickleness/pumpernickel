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

import java.awt.Toolkit;
import java.lang.reflect.Constructor;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import com.pump.plaf.AbstractSearchHighlight;

/** Some static methods relating to searching a <code>JTextComponent</code> or <code>JTable</code>.
 * 
 */
public class SwingSearch {

	/** This performs a basic search, starting from the current selection.
	 * Also this consults <code>UIManager.get("beepWhenSearchFails")</code>
	 * to determine whether to beep or not if a match is not found.
	 * 
	 * @param textComponent the component to search.
	 * @param searchPhrase the phrase to search for.
	 * @param forward whether to move forward or backward (aka "next" or "previous").
	 * @param matchCase whether to match the case or not.
	 * @return true if a match is found, false otherwise.
	 */
	public static boolean find(JTextComponent textComponent, String searchPhrase,
			boolean forward,boolean matchCase) {
		boolean returnValue = find2(textComponent,searchPhrase,forward,matchCase);
		if(!returnValue) {
			notifyFailedSearch();
		}
		return returnValue;
	}

	/** This performs a basic search, starting from the current selection.
	 * Also this consults <code>UIManager.get("beepWhenSearchFails")</code>
	 * to determine whether to beep or not if a match is not found.
	 * 
	 * @param table the component to search.
	 * @param searchPhrase the phrase to search for.
	 * @param forward whether to move forward or backward (aka "next" or "previous").
	 * @param matchCase whether to match the case or not.
	 * @return true if a match is found, false otherwise.
	 */
	public static boolean find(JTable table, String searchPhrase,
			boolean forward,boolean matchCase) {
		boolean returnValue = find2(table,searchPhrase,forward,matchCase);
		if(!returnValue) {
			notifyFailedSearch();
		}
		return returnValue;
	}
	
	private static void notifyFailedSearch() {
		Boolean beep = (Boolean)UIManager.get("beepWhenSearchFails");
		if(beep==null) beep = Boolean.TRUE;
		if(beep.booleanValue())
			Toolkit.getDefaultToolkit().beep();
	}
	
	private static boolean find2(JTextComponent textComponent, String searchPhrase,
			boolean forward,boolean matchCase) {
		if (textComponent == null)
			throw new NullPointerException(
					"No text component was provided to search.");
		if (searchPhrase == null)
			throw new NullPointerException(
					"No search phrase was provided to search for.");
		
		if(searchPhrase.length()==0)
			return false;

		if(matchCase==false)
			searchPhrase = searchPhrase.toUpperCase();
		
		int[] selection = new int[2];
		int startingIndex;
		if(forward) {
			startingIndex = Math.max(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
		} else {
			startingIndex = Math.min(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
		}
		int endIndex = forward ? textComponent.getDocument().getLength() : 0;
		if(find(textComponent, searchPhrase, forward, matchCase, true, startingIndex, endIndex, selection)) {
			int prevStart = Math.min(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
			int prevEnd = Math.max(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
			
			//if the selection doesn't change, do nothing.
			if(prevStart==selection[0] && prevEnd==selection[1])
				return false;
			
			textComponent.setSelectionStart(selection[0]);
			textComponent.setSelectionEnd(selection[1]);
			highlight(textComponent, selection[0], selection[1]);
			return true;
		}
		return false;
	}

	private static boolean find2(JTable table, String searchPhrase,
			boolean forward,boolean matchCase) {
		if (table == null)
			throw new NullPointerException(
					"No table component was provided to search.");
		if (searchPhrase == null)
			throw new NullPointerException(
					"No search phrase was provided to search for.");
		
		if(searchPhrase.length()==0)
			return false;

		if(matchCase==false)
			searchPhrase = searchPhrase.toUpperCase();
		
		int selectedColumn = table.getSelectedColumn();
		int selectedRow = table.getSelectedRow();
		
		int[] selection = new int[] { selectedRow, selectedColumn };
		if(find(table, searchPhrase, forward, matchCase, true, selection)) {	
			//if the selection doesn't change, do nothing.
			if(selectedRow==selection[0] && selectedColumn==selection[1])
				return false;

			table.changeSelection(selection[0], selection[1], false, false);
			
			highlight(table, selection[0], selection[1]);
			return true;
		}
		return false;
	}
	
	/** Returns the number of occurances of a phrase in a text component.
	 * 
	 * @param textComponent the text component to search.
	 * @param searchPhrase the phrase we're looking for.
	 * @param matchCase whether we have to be case sensitive or not.
	 * @return the number of occurrences of the phrase in the text component.
	 */
	public static int countOccurrence(JTextComponent textComponent,String searchPhrase,boolean matchCase) {
		if (textComponent == null)
			throw new NullPointerException(
					"No text component was provided to search.");
		if (searchPhrase == null)
			throw new NullPointerException(
					"No search phrase was provided to search for.");
		
		if(searchPhrase.length()==0)
			return 0;

		if(matchCase==false)
			searchPhrase = searchPhrase.toUpperCase();
		
		int sum = 0;
		int index = 0;
		int docLength = textComponent.getDocument().getLength();
		int[] selection = new int[2];
		while(index<docLength) {
			if(find(textComponent, searchPhrase, true, matchCase, false, index, docLength, selection)) {
				//alternatively, this could be: index = selection[1]?
				index = selection[0]+1;
				sum++;
			} else {
				return sum;
			}
		}
		return sum;
	}

	/** This performs a basic search, starting from a specific index.
	 * If a match is found, the indices are stored in the <code>selection</code> array,
	 * and <code>true</code> is returned.
	 * 
	 * @param textComponent the component to search.
	 * @param searchPhrase the phrase to search for.
	 * @param forward whether to move forward or backward
	 * @param matchCase whether to match the case or not.
	 * @param startingIndex the index in the document to begin searching.
	 * @param selection the array to store the indices in if a match is found.
	 * @return true if the array provided now contains two indices enclosing
	 * the search phrase.
	 */
	public static boolean find(JTextComponent textComponent, String searchPhrase,
			boolean forward,boolean matchCase,int startingIndex,int[] selection) {

		if(matchCase==false)
			searchPhrase = searchPhrase.toUpperCase();
		
		if(forward) {
			int length = textComponent.getDocument().getLength();
			return find(textComponent, searchPhrase, forward, matchCase, false, startingIndex, length, selection);
		} else {
			return find(textComponent, searchPhrase, forward, matchCase, false, startingIndex, 0, selection);
		}
	}
	
	/**
	 * 
	 * @param textComponent
	 * @param searchPhrase if matchCase is false, this must be uppercase
	 * @param forward
	 * @param matchCase
	 * @param wrapAround
	 * @param startingIndex the index at which we start reading
	 * @param endIndex the index at which we stop reading
	 * @param selection the selection start and end will be stored in this 2-int array
	 * @return true if the "selection" argument is now populated with the location of
	 * this search phrase
	 */
	private static boolean find(JTextComponent textComponent, String searchPhrase,
			boolean forward,boolean matchCase,boolean wrapAround,int startingIndex, int endIndex,int[] selection) {
		/** I don't have a lot of experience with large text documents in Java.
		 * 
		 * It would be easiest to just convert the textComponent's document into
		 * a String, and then use indexOf and lastIndexOf to perform these searches:
		 * but it seems like that might be really wasteful in terms of large
		 * documents.  (Especially consider the countOccurrence method, which may
		 * call this method thousands of times with every keystroke.)
		 * 
		 * So -- while I admit I don't know what I'm doing -- it seems like using
		 * the Segment class might be the better way to do this?
		 */
		int spl = searchPhrase.length();
		Document doc = textComponent.getDocument();
		char[] array = new char[spl];
		Segment segment = new Segment(array, 0, 0);
		
		try {
			if(forward) { //searching forward
				int index = startingIndex;
				int matching = 0;
				segment.setPartialReturn(true);
				
				while(index<endIndex) {
					int remainingCharsInDoc = endIndex-index;
					int remainingCharsInSearchPhrase = spl-matching;
					int searchLength = remainingCharsInDoc>remainingCharsInSearchPhrase ? 
							remainingCharsInSearchPhrase : remainingCharsInDoc;
					doc.getText(index, searchLength, segment);
					
					char[] workingArray;
					int arrayOffset;
					if(matchCase==false) {
						for(int a = 0; a<segment.count; a++) {
							array[a] = Character.toUpperCase( segment.array[a+segment.offset] );
						}
						workingArray = array;
						arrayOffset = 0;
					} else {
						workingArray = segment.array;
						arrayOffset = segment.offset;
					}
					
					scan : {
						for(int a = 0; a<segment.count; a++) {
							if(searchPhrase.charAt(matching)==workingArray[arrayOffset+a]) {
								matching++;
								if(matching==spl) {
									selection[1] = index+matching-(spl-segment.count);
									selection[0] = selection[1]-spl;
									return true;
								}
							} else {
								if(matching>0) {
									matching = 0;
									index = index-matching+1;
									break scan;
								}
								matching = 0;
							}
						}
						
						index+=segment.count;
					}
				}
				
				if(wrapAround && startingIndex!=0) {
					startingIndex = Math.min( textComponent.getDocument().getLength(), startingIndex+spl);
					return find(textComponent, searchPhrase, forward, matchCase, false, 0, startingIndex, selection);
				}
				return false;
			} else { //searching backward
				int index = Math.max(startingIndex-spl,0);
				int matching = 0;
				segment.setPartialReturn(false);
				
				while(index>endIndex) {
					int remainingCharsInDoc = index-endIndex;
					int remainingCharsInSearchPhrase = spl-matching;
					int searchLength = remainingCharsInDoc>remainingCharsInSearchPhrase ? 
							remainingCharsInSearchPhrase : remainingCharsInDoc;
					doc.getText(index+matching, searchLength, segment);
					
					char[] workingArray;
					int arrayOffset;
					if(matchCase==false) {
						for(int a = 0; a<segment.count; a++) {
							array[a] = Character.toUpperCase( segment.array[a+segment.offset] );
						}
						workingArray = array;
						arrayOffset = 0;
					} else {
						workingArray = segment.array;
						arrayOffset = segment.offset;
					}
					
					scan : {
						for(int a = segment.count-1; a>=0; a--) {
							if(searchPhrase.charAt(spl-1-matching)==workingArray[arrayOffset+a]) {
								matching++;
								if(matching==spl) {
									selection[0] = index+matching-(segment.count-1-a)-1;
									selection[1] = selection[0]+spl;
									return true;
								}
							} else {
								if(matching>0) {
									matching = 0;
									index = index+matching-1;
									break scan;
								}
								matching = 0;
							}
						}
						
						index-=segment.count;
					}
				}
				
				int docLength = textComponent.getDocument().getLength();
				if(wrapAround && startingIndex!=docLength) {
					startingIndex = Math.max(0, startingIndex-spl);
					return find(textComponent, searchPhrase, forward, matchCase, false, docLength, startingIndex, selection);
				}
				return false;
			}
		} catch(BadLocationException e) {
			//this shouldn't happen, right?
			RuntimeException e2 = new RuntimeException();
			e2.initCause(e);
			throw e2;
		}
	}
	

	
	/**
	 * 
	 * @param textComponent
	 * @param searchPhrase if matchCase is false, this must be uppercase
	 * @param forward
	 * @param matchCase
	 * @param wrapAround
	 * @param selection this 2-int array represents the [row, column] of the current selection,
	 * and will be modified to a new cell coordinate if this method returns true.
	 * @return true if the "selection" argument is now populated with the location of
	 * this search phrase
	 */
	private static boolean find(JTable table, String searchPhrase,
			boolean forward,boolean matchCase,boolean wrapAround,int[] selection) {
		if(!matchCase)
			searchPhrase = searchPhrase.toUpperCase();
		
		int rowCount = table.getRowCount();
		int columnCount = table.getColumnCount();
		
		int currentRow = selection[0];
		int currentColumn = selection[1];
		if(currentRow==-1) currentRow = 0;
		if(currentColumn==-1) currentColumn = 0;
		int initialRow = currentRow;
		int initialColumn = currentColumn;
		
		while(true) {
			if(forward) {
				currentColumn++;
				if(currentColumn==columnCount) {
					currentColumn = 0;
					currentRow++;
				}
				if(currentRow==rowCount) {
					if(wrapAround) {
						currentRow = 0;
					} else {
						return false;
					}
				}
			} else {
				currentColumn--;
				if(currentColumn==-1) {
					currentColumn = columnCount - 1;
					currentRow--;
				}
				if(currentRow==-1) {
					if(wrapAround) {
						currentRow = rowCount-1;
					} else {
						return false;
					}
				}
			}
			if(currentRow == initialRow && currentColumn == initialColumn) {
				return false;
			}
			
			Object value = table.getModel().getValueAt(currentRow, currentColumn);
			String text = value==null ? "" : value.toString();
			if(!matchCase) {
				text = text.toUpperCase();
			}
			if(text.contains(searchPhrase)) {
				selection[0] = currentRow;
				selection[1] = currentColumn;
				return true;
			}
		}
	}

	/** This can create a highlight effect for a selection of text.
	 * Originally this was designed to work with the {@link com.pump.plaf.AbstractSearchHighlight} class,
	 * but it can work with any object that has a constructor accepting the
	 * same arguments this method uses.
	 * <P>By default this will use the <code>AquaSearchHighlight</code>,
	 * but you can call <Code>UIManager.put("textSearchHighlightEffect", myClassName)</code>
	 * to change this default class.
	 * 
	 * @param tc the text component.
	 * @param selectionStart the beginning of the selection to highlight.
	 * @param selectionEnd the end of the selection to highlight.
	 */
	public static void highlight(JTextComponent tc, int selectionStart,
			int selectionEnd) {
		String className = UIManager.getString("textSearchHighlightEffect");
		if(className==null) {
			className = "com.pump.plaf.AquaSearchHighlight";
		}
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> constructor = c.getConstructor(new Class[] {JTextComponent.class, Integer.TYPE, Integer.TYPE });
			constructor.newInstance(new Object[] {tc, new Integer(selectionStart), new Integer(selectionEnd)});
		} catch(Throwable t) {
			if(t instanceof RuntimeException)
				throw (RuntimeException)t;
		}
	}

	/** This can create a highlight effect for a selection of text.
	 * Originally this was designed to work with the {@link com.pump.plaf.AbstractSearchHighlight} class,
	 * but it can work with any object that has a constructor accepting the
	 * same arguments this method uses.
	 * <P>By default this will use the <code>AquaSearchHighlight</code>,
	 * but you can call <Code>UIManager.put("textSearchHighlightEffect", myClassName)</code>
	 * to change this default class.
	 * 
	 * @param table the table to highlight.
	 * @param selectedRow the row that is selected.
	 * @param selectedColumn the column that is selected.
	 */
	public static void highlight(JTable table,int selectedRow,int selectedColumn) {
		String className = UIManager.getString("textSearchHighlightEffect");
		if(className==null) {
			className = "com.pump.plaf.AquaSearchHighlight";
		}
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> constructor = c.getConstructor(new Class[] {JTable.class, Integer.TYPE, Integer.TYPE });
			constructor.newInstance(new Object[] {table, new Integer(selectedRow), new Integer(selectedColumn)});
		} catch(Throwable t) {
			if(t instanceof RuntimeException)
				throw (RuntimeException)t;
		}
	}
	
	public static void clearHighlights(JTextComponent jtc) {
		AbstractSearchHighlight.clearHighlights();
	}
}