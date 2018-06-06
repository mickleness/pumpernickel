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
package com.pump.plaf.decorate;

import com.pump.plaf.decorate.DecoratedListUI.ListDecoration;
import com.pump.plaf.decorate.DecoratedTreeUI.TreeDecoration;

/**
 * This decorate can accommodate either JLists or JTrees.
 */
public interface ListTreeDecoration extends ListDecoration, TreeDecoration {

}