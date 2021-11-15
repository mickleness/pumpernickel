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
package com.pump.graphics.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;
import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ChangeElementEvent;
import com.pump.util.list.ListListener;
import com.pump.util.list.ObservableList;
import com.pump.util.list.RemoveElementsEvent;
import com.pump.util.list.ReplaceElementsEvent;

/**
 * This only passes targeted Operations to a delegate Graphics2D.
 * <p>
 * For example: you can configure this to only render images. Or to only use the
 * color black for shapes. Or to change the clipping for text. etc.
 */
public class FilteredGraphics2D extends VectorGraphics2D {

	/**
	 * This filters an Operation. The return value (if any) should be rendered.
	 * If this returns null then the Operation is skipped. Also this gives you
	 * the chance to alter the Operation (or its Graphics2DContext).
	 */
	public static interface OperationFilter {

		/**
		 * Return a filtered Operation, or null.
		 */
		Operation filter(Operation incomingOperation);

	}

	/**
	 * This OperationFilter only allows specific Operation classes to render.
	 * <p>
	 * For example if you construct this with StringOperation then this will
	 * only pass through calls to <code>Graphics2D#drawString(str, x, y)</code>.
	 */
	public static class OperationClassFilter implements OperationFilter {
		protected Class<? extends Operation>[] operationTypes;

		/**
		 * Create a new OperationClassFilter.
		 * 
		 * @param operationTypes
		 *            the types of Operations to allow to pass through this
		 *            filter. If an incoming Operation is not one of these types
		 *            then it is skipped.
		 */
		public OperationClassFilter(
				Class<? extends Operation>... operationTypes) {
			Objects.requireNonNull(operationTypes);
			this.operationTypes = operationTypes;
		}

		@Override
		public Operation filter(Operation incomingOperation) {
			for (Class<? extends Operation> t : operationTypes) {
				if (t.isInstance(incomingOperation))
					return incomingOperation;
			}
			return null;
		}
	}

	/**
	 * Create a FilteredGraphics2D that only passes calls to
	 * <code>g.drawString(..)</code> to the delegate.
	 * 
	 * @param input
	 *            the Graphics2D to paint strings to.
	 * @param textColor
	 *            an optional color to apply to all strings. For example: if
	 *            this is black, then all calls to <code>g.drawString(..)</code>
	 *            are rendered in black.
	 * @return
	 */
	public static FilteredGraphics2D createTextFilter(Graphics2D input,
			final Color textColor) {
		@SuppressWarnings("unchecked")
		OperationClassFilter opFilter = new OperationClassFilter(
				StringOperation.class) {

			@Override
			public Operation filter(Operation incomingOperation) {
				Operation op = super.filter(incomingOperation);
				if (op != null && textColor != null) {
					Graphics2DContext c = op.getContext();
					c.setColor(textColor);
					op.setContext(c);
				}
				return op;
			}

		};

		FilteredGraphics2D returnValue = new FilteredGraphics2D(input);
		returnValue.setFilter(opFilter);
		return returnValue;
	}

	protected OperationFilter filter = null;
	protected Graphics2D delegate;
	private ListListener<Operation> listListener = new ListListener<Operation>() {

		@Override
		public void elementsAdded(AddElementsEvent<Operation> event) {
			for (Operation newOp : event.getNewElements()) {
				Operation filteredOp = filter == null ? newOp
						: filter.filter(newOp);
				if (filteredOp != null) {
					Graphics2D g2 = (Graphics2D) delegate.create();
					g2.setTransform(new AffineTransform());
					g2.setClip(null);
					newOp.paint(g2);
					g2.dispose();
				}
			}
		}

		@Override
		public void elementsRemoved(RemoveElementsEvent<Operation> event) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void elementChanged(ChangeElementEvent<Operation> event) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void elementsReplaced(ReplaceElementsEvent<Operation> event) {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * Create a new FilteredGraphics2D.
	 * 
	 * @param input
	 *            the Graphics2D to paint filtered Operations to.
	 * @param operationTypes
	 *            the types of Operations to allow to pass through this filter.
	 *            If an incoming Operation is not one of these types then it is
	 *            skipped.
	 *            <p>
	 *            This argument is converted into a
	 *            {@link OperationClassFilter}. So if you subsequently replace
	 *            the filter by calling {@link #setFilter(OperationFilter)} then
	 *            this argument has no effect.
	 */
	public FilteredGraphics2D(Graphics2D g,
			Class<? extends Operation>... operationTypes) {
		super(new Graphics2DContext(g), new ObservableList<Operation>());
		Objects.requireNonNull(g);
		delegate = g;
		((ObservableList<Operation>) operations).addListListener(listListener,
				false);
		setFilter(new OperationClassFilter(operationTypes));
	}

	/**
	 * Assign a new OperationFilter.
	 * 
	 * @param filter
	 *            this filter will be consulted for every incoming Operation. If
	 *            an Operation is permitted it will render directly to the
	 *            original delegate Graphics2D this object was constructed with.
	 */
	public void setFilter(OperationFilter filter) {
		this.filter = filter;
	}

	/**
	 * Return the current OperationFilter.
	 */
	public OperationFilter getFilter() {
		return filter;
	}
}