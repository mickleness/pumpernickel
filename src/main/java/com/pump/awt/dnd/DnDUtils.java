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
package com.pump.awt.dnd;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import com.pump.icon.IconUtils;
import com.pump.io.icon.FileIcon;
import com.pump.util.JVM;

/**
 * A set of static methods related to drag and drop functionality.
 * 
 */
public class DnDUtils {

	public static final String KEY_FILE = FileLabel.class.getName() + "#File";

	/**
	 * This configures a DragSource so the argument will always initiate a drag
	 * operation for a file. The java.io.File object used is
	 * <code>component.getClientProperty(KEY_FILE)</code> (which may change for
	 * every drag).
	 * 
	 * @param component
	 *            a JComponent which should define the client property KEY_FILE
	 *            as a java.io.File.
	 * @param actions
	 *            one of the DnDConstants.ACTION_X constants. A good default is
	 *            COPY_OR_MOVE.
	 */
	public static void setupFileDragSource(final JComponent component,
			final int actions) {
		if (!(actions == DnDConstants.ACTION_COPY
				|| actions == DnDConstants.ACTION_COPY_OR_MOVE
				|| actions == DnDConstants.ACTION_LINK
				|| actions == DnDConstants.ACTION_MOVE
				|| actions == DnDConstants.ACTION_NONE || actions == DnDConstants.ACTION_REFERENCE))
			throw new IllegalArgumentException(
					"actions should be a DnDConstants.ACTION_X field");

		final DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(component, actions,
				new DragGestureListener() {

					@Override
					public void dragGestureRecognized(DragGestureEvent dge) {
						DragSourceListener dsl = new DragSourceAdapter() {
						};
						Icon icon = getFileIcon();
						Cursor dragCursor = Cursor
								.getPredefinedCursor(Cursor.MOVE_CURSOR);
						File file = (File) component
								.getClientProperty(KEY_FILE);
						if (file != null) {
							Transferable transferable = new FileTransferable(
									file);
							if (icon != null) {
								BufferedImage image = new BufferedImage(icon
										.getIconWidth(), icon.getIconHeight(),
										BufferedImage.TYPE_INT_ARGB);
								Graphics2D g = image.createGraphics();
								icon.paintIcon(null, g, 0, 0);
								g.dispose();
								Point pt = new Point(-image.getWidth() / 2,
										-image.getHeight() / 2);
								dragSource.startDrag(dge, dragCursor, image,
										pt, transferable, dsl);
							} else {
								dragSource.startDrag(dge, dragCursor,
										transferable, dsl);
							}
						}
					}

					private Icon getFileIcon() {
						Icon i;

						File file = (File) component
								.getClientProperty(KEY_FILE);
						i = FileIcon.get().getIcon(file);

						if (i == null) {
							i = UIManager.getIcon("FileView.fileIcon");
						}
						if (i == null) {
							i = UIManager.getIcon("Tree.leafIcon");
						}
						if (JVM.isMac && i != null && i.getIconWidth() < 64) {
							i = IconUtils.createScaledIcon(i, 64, 64);
						}
						return i;
					}
				});
	}
}