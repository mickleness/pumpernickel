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