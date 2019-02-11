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
package com.pump.io.location;

import java.util.HashSet;
import java.util.Set;

public class BasicLocationGroup implements IOLocationGroup {
    long consumerCtr = 0;
    Set<Long> checkedOutKeys = new HashSet<Long>();

    public synchronized Object addConsumer() {
	Long returnValue = consumerCtr++;
	checkedOutKeys.add(returnValue);
	return returnValue;

    }

    public synchronized void releaseConsumer(Object key) {
	checkedOutKeys.remove(key);
    }

    public synchronized boolean isActive() {
	return checkedOutKeys.size() > 0;
    }

}