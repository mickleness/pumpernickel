package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

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
	protected boolean canonicalEquals(Operator canonicalOther) {
		if (!(canonicalOther instanceof ConstantOperator))
			return false;
		ConstantOperator other = (ConstantOperator) canonicalOther;
		return value == other.value;
	}

	@Override
	public Collection<Operator> split() {
		return Collections.singleton(this);
	}

	@Override
	public int hashCode() {
		return Boolean.valueOf(value).hashCode();
	}

	@Override
	public Operator getCanonicalOperator() {
		return this;
	}

	@Override
	public Collection<String> getAttributes() {
		return Collections.emptySet();
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
}