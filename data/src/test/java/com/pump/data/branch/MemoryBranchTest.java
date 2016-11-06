package com.pump.data.branch;


public class MemoryBranchTest extends BranchTest<MemoryBranch<String>> {
	
	@Override
	public MemoryBranch<String> createRoot() {
		return new MemoryBranch<String>("test-root");
	}
	
}
