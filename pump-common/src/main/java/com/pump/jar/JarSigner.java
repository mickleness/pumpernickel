/*
 * @(#)JarSigner.java
 *
 * $Date$
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
package com.pump.jar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JarSigner {
	File keyStore;
	String keystorePassword;
	String alias, aliasPassword;
	String tsa;
	
	/**
	 * 
	 * @param keyStore
	 * @param keystorePassword
	 * @param alias
	 * @param aliasPassword
	 * @param tsa the optional Timestamping Authority argument. If null then this is omitted.
	 * Recommended value: "http://timestamp.digicert.com"
	 */
	public JarSigner(File keyStore,String keystorePassword,String alias,String aliasPassword,String tsa) {
		this.keyStore = keyStore;
		this.alias = alias;
		this.keystorePassword = keystorePassword;
		this.aliasPassword = aliasPassword;
		this.tsa = tsa;
	}

	public void sign(File jarFile,boolean blocking) {
		ProcessBuilderThread pbt = new ProcessBuilderThread("jarsigner", true);
		List<String> command = new ArrayList<String>();
		command.add("jarsigner");
		
		command.add("-keystore");
		command.add(keyStore.getAbsolutePath());
		
		if(tsa!=null && tsa.length()>0) {
			command.add("-tsa");
			command.add(tsa);
		}

		if(keyStore.getAbsolutePath().toLowerCase().endsWith(".p12")) {
			command.add("-storetype");
			command.add("pkcs12");
		}

		command.add("-storepass");
		command.add(keystorePassword);

		command.add("-keypass");
		command.add(aliasPassword);
		
		command.add(jarFile.getAbsolutePath());
		
		command.add(alias);
		
		pbt.processBuilder.command(command);
		
		pbt.start(blocking);
	}
}
