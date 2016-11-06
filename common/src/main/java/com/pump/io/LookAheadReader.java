/*
 * @(#)LookAheadReader.java
 *
 * $Date: 2015-12-26 01:54:45 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.text.CharacterIterator;

/**
 * This reads data but also provides a couple of convenient .peek() methods to look ahead. 
 * My compiler professor would consider this approach to parsing "cheating", but I think it makes the code more readable.
 * <p>The internals of this class use an array-backed circular queue.
 */
public class LookAheadReader extends Reader implements AutoCloseable
{

	Reader		m_reader;
	char[] m_queue;
	int m_queueLength = 0;
	int m_queuePosition = 0;
	
	/** The total number of chars read. */
	long m_read = 0;

	
	public static final int DEFAULT_QUEUE_SIZE = 2048;
	
	/** Create a new LookAheadReader can look ahead up to 2048 chars. */
	public LookAheadReader(Reader reader) throws IOException
	{
		this(reader, DEFAULT_QUEUE_SIZE);
	}
	
	/** This constructor is primarily intended for unit tests. */
	protected LookAheadReader(Reader reader,int queueSize) throws IOException
	{
		this.m_queue = new char[queueSize];
		this.m_reader = reader;
		populateQueue();
	}

	/** Fill the queue to its maximum storage potential. */
	protected synchronized void populateQueue() throws IOException
	{
		while (m_queueLength < m_queue.length)
		{
			int off = (m_queuePosition + m_queueLength)%m_queue.length;
			int len = Math.min(m_queue.length - m_queueLength, m_queue.length - off);
			
			int i = m_reader.read(m_queue, off, len);
			if (i == -1)
			{
				return;
			}
			m_queueLength += i;
		}
	}
	
	/** Return the number of characters read so far.
	 * 
	 * @return the number of characters read so far.
	 */
	public synchronized long getPosition() 
	{
		return m_read;
	}

	/**
	 * Return the current character from this reader.
	 * 
	 * @return the current character from this reader, or {@link java.text.CharacterIterator#DONE} if finished
	 */
	public synchronized char current()
	{
		if (m_queueLength == 0)
		{
			return CharacterIterator.DONE;
		}
		
		return m_queue[m_queuePosition];
	}

	/** Peek at a character several characters ahead.
	 * @param length
	 *            the number of characters to look ahead. If this is zero, then this method is equivalent to {@link #current()}.
	 *            If this is one, then this returns the Character after current(), etc.
	 * @return the character that is length-many spaces ahead, or {@link java.text.CharacterIterator#DONE} if the end
	 * of the character stream is reached.
	 */
	public synchronized char peek(int length)
	{
		if (length >= m_queue.length)
			throw new IllegalArgumentException("this reader can only peek up to "+m_queue.length+" chars (requested "+length+")");
		if(length>=m_queueLength)
			return CharacterIterator.DONE;
		
		int newPosition = (m_queuePosition + length)%m_queue.length;
		return m_queue[newPosition];
	}

	/**
	 * Cycle to the next character.
	 * 
	 * @return true if a new character is available, false if we've reached the end of the stream.
	 * @throws IOException
	 */
	public synchronized boolean next() throws IOException
	{
		return next(true);
	}
	
	protected synchronized boolean next(boolean repopulateQueue) throws IOException {
		m_queuePosition = (m_queuePosition + 1)%m_queue.length;
		m_queueLength--;
		if(repopulateQueue)
		{
			populateQueue();
		}
		m_read++;
		return m_queueLength > 0;
	}

	/** Close the underlying reader. */
	public synchronized void close() throws IOException
	{
		m_reader.close();
	}

	/**
	 * Return true if there is a character after the current one.
	 */
	public synchronized boolean hasNext()
	{
		return peek(1) != CharacterIterator.DONE;
	}

	/**
	 * Skip a fixed amount of characters.
	 * 
	 * @param charsToSkip
	 *            the number of characters to skip.
	 * @return the number of characters that were skipped (this may be different from the argument if the end of the
	 *         file is reached).
	 * @throws IOException
	 */
	public synchronized long skip(long charsToSkip) throws IOException
	{
		long processed = 0;
		
		//first disregard what's in our small queue:
		while (charsToSkip > 0 && m_queueLength>0)
		{
			next(false);
			charsToSkip--;
			processed++;
		}
		
		//the efficient way to skip:
		while(charsToSkip>0)
		{
			long skipped = m_reader.skip(charsToSkip);
			if(skipped==0) 
			{
				return processed;
			}
			charsToSkip -= skipped;
			processed += skipped;
			m_read += skipped;
		}
		
		populateQueue();
		
		return processed;
	}

	/**
	 * Skip the current line break, if any.
	 * <p>
	 * This looks for any of the following 3 combinations: \n, \r, or \r\n .
	 */
	public synchronized boolean skipLineBreak() throws IOException
	{
		char current = current();
		if ('\r'==current && '\n'==peek(1) )
		{
			skip(2);
			return true;
		}
		else if (isNewLine(current))
		{
			skip(1);
			return true;
		}
		return false;
	}

	/**
	 * Return true if the argument is \n or \n.
	 * 
	 * @param c
	 *            a character to check
	 * @return true if the argument is \n or \r, false for any other value (including null)
	 */
	public boolean isNewLine(char c)
	{
		return c=='\n' || c=='\r';
	}

	/**
	 * Read one line of text from this reader.
	 * 
	 * @param charLimit
	 *            the maximum number of characters to read before throwing an exception.
	 * @return a line of text
	 * @throws IOException
	 *             if an IOException occurred reading data, or if the charLimit was exceeded, or an EOFException.
	 */
	public synchronized String readLine(int charLimit) throws IOException
	{
		int origLimit = charLimit;

		StringBuffer sb = new StringBuffer();
		while (charLimit > 0)
		{
			int toRead = Math.min(charLimit, m_queueLength);
			for (int a = 0; a < toRead; a++)
			{
				char c = current();
				next();
				if (isNewLine(c))
				{
					skipLineBreak();
					return sb.toString();
				}
				sb.append(c);
			}
			charLimit -= toRead;
			populateQueue();

			if (m_queueLength == 0)
			{
				if (sb.length() == 0)
				{
					throw new EOFException();
				}
				return sb.toString();
			}
		}

		throw new IOException("The limit (" + origLimit
				+ ") of chars were read without reaching a line break.");
	}

	/** Peek at remaining characters.
	 * 
	 * @param includeLetters if true then letters are allowed/included.
	 * @param includeDigits if true then digits are allowed/included.
	 * @param includeOtherNonWhitespace if true then other characters (such as punctuation) are allowed/included.
	 * @return a peek of the next available word.
	 */
	public String peekWord(boolean includeLetters, boolean includeDigits, boolean includeOtherNonWhitespace) {
		StringBuffer sb = new StringBuffer();
		boolean done = false;
		while(!done)
		{
			char ch = peek(sb.length());
			if(ch==CharacterIterator.DONE)
			{
				done = true;
			}
			else if(Character.isLetter(ch))
			{
				if(includeLetters)
				{
					sb.append(ch);
				}
				else
				{
					done = true;
				}
			}
			else if(Character.isDigit(ch))
			{
				if(includeDigits)
				{
					sb.append(ch);
				}
				else
				{
					done = true;
				}
			}
			else if(!Character.isWhitespace(ch))
			{
				if(includeOtherNonWhitespace)
				{
					sb.append(ch);
				}
				else
				{
					done = true;
				}
			} 
			else
			{
				//it's whitespace:
				done = true;
			}
		}
		return sb.toString();
	}

	/** Skip whitespace until either the reader is finished or a non-whitespace char is the current char. */
	public void skipWhitespace() throws IOException {
		while(CharacterIterator.DONE!=current() && Character.isWhitespace(current())) {
			next();
		}
	}

	@Override
	public synchronized int read(char[] cbuf, int off, int len) throws IOException {
		
		/*
		 * Suppose our queue resembles:
		 * mnopqrstuvwxyyz  abcdefghijkl
		 * --------------]  [-----------
		 * 
		 * (where it starts at 'a')
		 * Then in this operation we have two distinct
		 * arraycopy operations we can perform: the right 
		 * chunk and the left chunk.
		 * 
		 * NOTE: this method body is nearly identical to the next method. If you
		 * modify this method, please change that one as well.
		 * 
		 */
		int read = 0;
		int rightChunkLength = Math.min(m_queue.length - m_queuePosition, m_queueLength);
		int charsToRead = Math.min( rightChunkLength, len);
		if(charsToRead>0) {
			System.arraycopy(m_queue, m_queuePosition, cbuf, off, charsToRead);
			read += charsToRead;
			len -= charsToRead;

			m_queuePosition = (m_queuePosition + charsToRead)%m_queue.length;
			m_queueLength -= charsToRead;
		}
		
		//feel free to try the left chunk too:
		if(len>0 && m_queueLength>0) {
			int leftChunkLength = m_queueLength;
			charsToRead = Math.min( leftChunkLength, len);
			if(charsToRead>0) {
				System.arraycopy(m_queue, 0, cbuf, off + read, charsToRead);
				read += charsToRead;
				len -= charsToRead;
				m_queuePosition = (m_queuePosition + charsToRead)%m_queue.length;
				m_queueLength -= charsToRead;
			}
		}
		
		populateQueue();
		return read;
	}

	@Override
	public int read(CharBuffer target) throws IOException {
        int len = target.remaining();

		/*
		 * Suppose our queue resembles:
		 * mnopqrstuvwxyyz  abcdefghijkl
		 * --------------]  [-----------
		 * 
		 * (where it starts at 'a')
		 * Then in this operation we have two distinct
		 * arraycopy operations we can perform: the right 
		 * chunk and the left chunk.
		 * 
		 * NOTE: this method body is nearly identical to the previous method. If you
		 * modify this method, please change that one as well.
		 */
		int read = 0;
		int rightChunkLength = Math.min(m_queue.length - m_queuePosition, m_queueLength);
		int charsToRead = Math.min( rightChunkLength, len);
		if(charsToRead>0) {
			target.put(m_queue, m_queuePosition, charsToRead);
			read += charsToRead;
			len -= charsToRead;

			m_queuePosition = (m_queuePosition + charsToRead)%m_queue.length;
			m_queueLength -= charsToRead;
		}
		
		//feel free to try the left chunk too:
		if(len>0 && m_queueLength>0) {
			int leftChunkLength = m_queueLength;
			charsToRead = Math.min( leftChunkLength, len);
			if(charsToRead>0) {
				target.put(m_queue, 0, charsToRead);
				read += charsToRead;
				len -= charsToRead;
				m_queuePosition = (m_queuePosition + charsToRead)%m_queue.length;
				m_queueLength -= charsToRead;
			}
		}
		
		populateQueue();
		return read;
	}
}
