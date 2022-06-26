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
package com.pump.diagram;

public class BinaryRelationship {
	public final Relationship relationshipSideA, relationshipSideB;

	public BinaryRelationship(Relationship relationshipSideA,
			Relationship relationshipSideB) {
		this.relationshipSideA = relationshipSideA;
		this.relationshipSideB = relationshipSideB;
	}
}