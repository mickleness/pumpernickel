/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.data.branch;

public class MemoryBranchTest extends BranchTest<MemoryBranch<String>> {

	@Override
	public MemoryBranch<String> createRoot() {
		return new MemoryBranch<String>("test-root");
	}

}