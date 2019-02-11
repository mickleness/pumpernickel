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
package com.pump.diagram;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public enum Relationship {
	NONE, MANY() {

		@Override
		public void appendDecoration(GeneralPath path, Point target,
				Point source) {
			double theta = Math.atan2(source.y - target.y, source.x - target.x);
			double k1 = 10;
			double k2 = 4;
			path.moveTo(target.x + k1 * Math.cos(theta),
					target.y + k1 * Math.sin(theta));
			path.lineTo(target.x + k2 * Math.cos(theta + Math.PI / 2.0),
					target.y + k2 * Math.sin(theta + Math.PI / 2.0));

			path.moveTo(target.x + k1 * Math.cos(theta),
					target.y + k1 * Math.sin(theta));
			path.lineTo(target.x + k2 * Math.cos(theta - Math.PI / 2.0),
					target.y + k2 * Math.sin(theta - Math.PI / 2.0));
		}
	},
	ONE() {

		@Override
		public void appendDecoration(GeneralPath path, Point target,
				Point source) {
			double theta = Math.atan2(source.y - target.y, source.x - target.x);
			double k1 = 8;
			double k2 = 6;
			Point2D p = new Point2D.Double(target.x + k1 * Math.cos(theta),
					target.y + k1 * Math.sin(theta));
			path.moveTo(p.getX() + k1 * Math.cos(theta + Math.PI / 2.0),
					p.getY() + k2 * Math.sin(theta + Math.PI / 2.0));
			path.lineTo(p.getX() + k1 * Math.cos(theta - Math.PI / 2.0),
					p.getY() + k2 * Math.sin(theta - Math.PI / 2.0));

		}
	},
	PLAIN;

	public void appendDecoration(GeneralPath path, Point target, Point source) {

	}
}