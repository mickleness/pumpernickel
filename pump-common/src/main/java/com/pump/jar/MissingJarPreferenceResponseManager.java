package com.pump.jar;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class MissingJarPreferenceResponseManager implements MissingJarResponseManager {
	final Preferences rootPrefs;
	final Preferences prefs;
	
	/** Create a new Preferences-backed choice. 
	 * 
	 * @param name the project name, which is used to distinguish the
	 * preferences so different projects can make different choices.
	 */
	public MissingJarPreferenceResponseManager(String name) {
		if(name==null) throw new NullPointerException();
		rootPrefs = Preferences.userNodeForPackage( JarWriter.class );
		prefs = rootPrefs.node(name);
	}

	@Override
	public MissingJarResponse getBehavior(File file,boolean applyToNonrequiredJars) {
		String s = prefs.get(file.getName(), null);
		if(s==null) return null;
		return MissingJarResponse.valueOf(s);
	}
	
	@Override
	public void setBehavior(File file,MissingJarResponse b) {
		prefs.put(file.getName(), b.toString());
	}

	@Override
	public MissingJarResponse guessBehavior(File jarFile) {
		try {
			/** Catalog previous choices by frequency of hits. */
			Map<String, Integer> results = new TreeMap<String, Integer>();
			catalogPrefs(rootPrefs, jarFile, results);
			
			if(results.size()==0)
				return null;
			int max = 0;
			String bestKey = null;
			for(String choice : results.keySet()) {
				int v = results.get(choice);
				if(v>=max) {
					bestKey = choice;
					max = v;
				}
			}
			return MissingJarResponse.valueOf(bestKey);
		} catch(BackingStoreException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void resolveBehaviors(MissingJarResponseManager manager,
			Map<File, MissingJarResponse> requiredJars, Set<File> keySet,
			String name) {
		for(File file : keySet) {
			requiredJars.put(file, guessBehavior(file));
		}
	}
	
	private void catalogPrefs(Preferences prefs,File jarFile,Map<String, Integer> results) throws BackingStoreException {
		for(String name : prefs.childrenNames()) {
			Preferences child = prefs.node(name);
			catalogPrefs(child, jarFile, results);
		}
		String v = prefs.get(jarFile.getName(), null);
		if(v!=null) {
			try {
				MissingJarResponse.valueOf(v);
				Integer i = results.get(v);
				if(i==null) i = 0;
				results.put(v, i+1);
			} catch(RuntimeException e) {}
		}
	}
}
