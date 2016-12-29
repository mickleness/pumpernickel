/*
 * @(#)AudioPlayerComponent.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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
package com.pump.swing;

import java.net.URL;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.pump.audio.AudioPlayer;
import com.pump.audio.AudioPlayer.StartTime;
import com.pump.plaf.AudioPlayerUI;

public class AudioPlayerComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "AudioPlayerUI";
	
	public static final String SOURCE_KEY = AudioPlayerComponent.class.getName()+".source";
	public static final String PLAYER_KEY = AudioPlayerComponent.class.getName()+".player";
	
	public AudioPlayerComponent() {
        updateUI();
	}
	
	public synchronized AudioPlayer getAudioPlayer() {
		return getAudioPlayer(false);
	}
	
	private synchronized AudioPlayer getAudioPlayer(boolean createIfMissing) {
		AudioPlayer player = (AudioPlayer)getClientProperty(PLAYER_KEY);
		if(player==null && createIfMissing) {
			player = new AudioPlayer(getSource());
			putClientProperty(PLAYER_KEY, player);
		}
		return player;
	}
	
	public synchronized void pause() {
		AudioPlayer player = getAudioPlayer(false);
		if(player!=null) {
			player.pause();
		}
	}
	
	public synchronized void play(StartTime startTime) {
		AudioPlayer player = getAudioPlayer(true);
		try {
			player.play(startTime);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public synchronized void play() {
		play(new StartTime(0, false));
	}
	
	public synchronized float getTime() {
		AudioPlayer player = getAudioPlayer(false);
		if(player==null) {
			return -1;
		}
		return player.getElapsedTime();
	}
	
	public synchronized void setSource(URL url) {
		putClientProperty(SOURCE_KEY, url);
	}
	
	public synchronized URL getSource() {
		URL url = (URL)getClientProperty(SOURCE_KEY);
		AudioPlayer player = getAudioPlayer();
		if(player!=null) {
			player.pause();
			putClientProperty(PLAYER_KEY, null);
		}
		return url;
	}
	
    @Override
	public String getUIClassID() {
        return uiClassID;
    }
	
    @Override
	public void updateUI() {
    	if(UIManager.getDefaults().get(uiClassID)==null) {
    		UIManager.getDefaults().put(uiClassID, "com.pump.plaf.BasicAudioPlayerUI");
    	}
    	setUI((AudioPlayerUI)UIManager.getUI(this));
    }
	
	public void setUI(AudioPlayerUI ui) {
        super.setUI(ui);
	}
	
	public AudioPlayerUI getUI() {
		return (AudioPlayerUI)ui;
	}
}
