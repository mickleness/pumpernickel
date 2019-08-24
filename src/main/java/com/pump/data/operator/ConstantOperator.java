package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ConstantOperator extends Operator {
	private static final long serialVersionUID = 1L;

	private boolean value;

	ConstantOperator(boolean value) {
		this.value = value;
	}

	@Override
	public int getOperandCount() {
		return 0;
	}

	@Override
	public Object getOperand(int index) {
		return null;
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		return value;
	}

	@Override
	protected String toString(boolean negated) {
		boolean v = negated ? !value : value;
		return Boolean.valueOf(v).toString();
	}

	@Override
	protected Operator createCanonicalOperator() {
		return this;
	}

	@Override
	public int hashCode() {
		return Boolean.valueOf(value).hashCode();
	}

	@Override
	public Collection<String> getAttributes() {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Operator operator, boolean strictEquivalency) {
		if (strictEquivalency && operator instanceof ConstantOperator) {
			ConstantOperator other = (ConstantOperator) operator;
			return value == other.value;

		}
		return super.equals(operator, strictEquivalency);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeBoolean(value);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			value = in.readBoolean();
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms() {
		return new HashMap<>();
	}

	@Override
	protected boolean evaluateTestAtoms(Map<String, TestAtom> values) {
		return value;
	}
}