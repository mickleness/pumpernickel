package com.pump.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.pump.util.Receiver;

/**
 * This is simple parser for CSV files.
 * 
 * TODO: write unit tests
 */
public class CsvParser {
	
	public static class ParseException extends IOException {
		private static final long serialVersionUID = 1L;

		int lineNumber;
		int position;
		
		public ParseException(String msg, int pos, int lineNumber) {
			super(msg);
			this.position = pos;
			this.lineNumber = lineNumber;
		}
		
		/**
		 * Return the line number this exception occurred on.
		 */
		public int getLineNumber() {
			return lineNumber;
		}
		
		/**
		 * Return the character position within this line that this exception occurred on.
		 */
		public int getPosition() {
			return position;
		}
	}
	
	protected char delimiter = ',';
	protected char textQualifier = '\"';
	
	/**
	 * Create a CsvParser that uses a comma as the delimited and a double quotation
	 * as the text qualifier.
	 */
	public CsvParser() {
		
	}
	
	/**
	 * Create a CsvParser.
	 * 
	 * @param delimiter the delimiter between cell values
	 * @param textQualifier the character used to enclose cells that include new lines or delimiters.
	 */
	public CsvParser(char delimiter,char textQualifier) {
		this.delimiter = delimiter;
		this.textQualifier = textQualifier;
	}
	
	/**
	 * Parse an InputStream.
	 * 
	 * @param in the stream to parse.
	 * @param charset the character set to apply to the InputStream.
	 * @param receiver the Receiver that will be notified as new lines as parsed.
	 */
	public void parse(InputStream in,Charset charset,Receiver<List<String>> receiver) throws IOException {
		parse(new InputStreamReader(in, charset), receiver);
	}

	/**
	 * Parse a Reader.
	 * 
	 * @param reader the reader to parse.
	 * @param receiver the Receiver that will be notified as new lines as parsed.
	 */
	public void parse(Reader reader, Receiver<List<String>> receiver) throws IOException {
		class MeasuredReader {
			Reader reader;
			int pos, lineNumber;
			MeasuredReader(Reader r,int lineNumber) {
				reader = r;
				this.lineNumber = lineNumber;
			}
			
			public int read() throws IOException {
				int returnValue = reader.read();
				pos++;
				return returnValue;
			}
		}
		
		
		List<String> currentRow = new LinkedList<>();
		MeasuredReader lineReader = new MeasuredReader(reader, 1);
		int t = lineReader.read();
		while(t!=-1) {
			if(t==textQualifier) {
				//read a text-qualifier-enclosed cell:
				StringBuilder sb = new StringBuilder();
				t = lineReader.read();
				readCell : while(t!=-1) {
					if(t==textQualifier) {
						t = lineReader.read();
						if(t==textQualifier) {
							sb.append( textQualifier );
						} else {
							break readCell;
						}
					} else {
						sb.append( (char)t );
					}
					t = lineReader.read();
				}
				currentRow.add(sb.toString());
				
				while(t!=-1 && (t==' ' || t=='\t')) {
					t = lineReader.read();
				}
				
				if(t=='\n' || t=='\r') {
					//finished this row:
					while(t=='\n' || t=='\r') {
						t = lineReader.read();
					}
					receiver.add( new List[] { currentRow });
					currentRow = new ArrayList<>(currentRow.size());
					lineReader = new MeasuredReader(reader, lineReader.lineNumber+1);
				} else if(!(t==-1 || t==delimiter)) {
					throw new ParseException("Unexpected character after a wrapped cell value. Line "+lineReader.lineNumber+", pos="+lineReader.pos, lineReader.pos, lineReader.lineNumber);
				}
			} else {
				if(t==delimiter) {
					//empty!
					currentRow.add(new String());
					t = lineReader.read();
				} else {
					StringBuilder sb = new StringBuilder();
					while(t!=-1 && t!=delimiter && t!='\n' && t!='\r') {
						sb.append( (char)t );
						t = lineReader.read();
					}
					
					currentRow.add(sb.toString());
					if(t=='\n' || t=='\r') {
						//finished this row:
						while(t=='\n' || t=='\r') {
							t = lineReader.read();
						}
						receiver.add( new List[] { currentRow });
						currentRow = new ArrayList<>(currentRow.size());
						lineReader = new MeasuredReader(reader, lineReader.lineNumber+1);
					} else if(t==delimiter) {
						t = lineReader.read();
						continue;
					}
					if(t==-1) {
						if(currentRow.size()>0)
							receiver.add( new List[] { currentRow });
						return;
					}
				}
			}
		}
	}
}
