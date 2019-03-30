/*
 * $RCSfile: JaiI18N.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1178 $
 * $Date: 2012-01-20 03:47:29 -0500 (Fri, 20 Jan 2012) $
 * $State: Exp $
 */
package javax.media.jai;

import java.text.MessageFormat;
import java.util.Locale;

class JaiI18N {
	static String packageName = "javax.media.jai";

	public static String getString(String key) {
		// return PropertyUtil.getString(packageName, key);
		return key;
	}

	public static String formatMsg(String key, Object[] args) {
		MessageFormat mf = new MessageFormat(getString(key));
		mf.setLocale(Locale.getDefault());

		return mf.format(args);
	}
}
