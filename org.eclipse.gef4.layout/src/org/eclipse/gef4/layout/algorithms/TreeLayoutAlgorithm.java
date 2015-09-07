/*******************************************************************************
 * Copyright (c) 2005, 2015 The Chisel Group and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Casey Best, Ian Bull, Rob Lintern, Jingwei Wu (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Miles Parker - optional node space configuration
 *               Matthias Wienand (itemis AG) - refactorings
 *               
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.Iterator;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.algorithms.TreeLayoutObserver.TreeNode;

/**
 * The TreeLayoutAlgorithm class implements a simple algorithm to arrange graph
 * nodes in a layered tree-like layout.
 * 
 * @author Casey Best
 * @author Ian Bull
 * @author Rob Lintern
 * @author Jingwei Wu
 * @author Mateusz Matela
 * @author Miles Parker
 * @author mwienand
 */
public class TreeLayoutAlgorithm implements ILayoutAlgorithm {

	/**
	 * Tree direction constant for which root is placed at the top and branches
	 * spread downwards
	 */
	public final static int TOP_DOWN = 1;

	/**
	 * Tree direction constant for which root is placed at the bottom and
	 * branches spread upwards
	 */
	public final static int BOTTOM_UP = 2;

	/**
	 * Tree direction constant for which root is placed at the left and branches
	 * spread to the right
	 */
	public final static int LEFT_RIGHT = 3;

	/**
	 * Tree direction constant for which root is placed at the right and
	 * branches spread to the left
	 */
	public final static int RIGHT_LEFT = 4;

	private int direction = TOP_DOWN;

	private boolean resize = false;

	private ILayoutContext context;

	private Rectangle bounds;

	private double leafSize, layerSize;

	private TreeLayoutObserver treeObserver;

	private Dimension nodeSpace;

	/**
	 * Create a default Tree Layout.
	 */
	public TreeLayoutAlgorithm() {
	}

	/**
	 * Create a Tree Layout with a specified direction.
	 * 
	 * @param direction
	 *            The direction, one of {@link TreeLayoutAlgorithm#BOTTOM_UP},
	 *            {@link TreeLayoutAlgorithm#LEFT_RIGHT},
	 *            {@link TreeLayoutAlgorithm#RIGHT_LEFT},
	 *            {@link TreeLayoutAlgorithm#TOP_DOWN}
	 */
	public TreeLayoutAlgorithm(int direction) {
		this(direction, null);
	}

	/**
	 * Create a Tree Layout with fixed size spacing around nodes. If nodeSpace
	 * is not null, the layout will size the container to the ideal space to
	 * just contain all nodes of fixed size without any overlap. Otherwise, the
	 * algorithm will size for the container's available space.
	 * 
	 * @param direction
	 *            The direction, one of {@link TreeLayoutAlgorithm#BOTTOM_UP},
	 *            {@link TreeLayoutAlgorithm#LEFT_RIGHT},
	 *            {@link TreeLayoutAlgorithm#RIGHT_LEFT},
	 *            {@link TreeLayoutAlgorithm#TOP_DOWN}
	 * @param nodeSpace
	 *            the size to make each node. May be null.
	 */
	public TreeLayoutAlgorithm(int direction, Dimension nodeSpace) {
		setDirection(direction);
		this.nodeSpace = nodeSpace;
	}

	/**
	 * @param nodeSpace
	 *            the nodeSpace size to set
	 */
	public void setNodeSpace(Dimension nodeSpace) {
		this.nodeSpace = nodeSpace;
	}

	/**
	 * Returns the direction of this {@link TreeLayoutAlgorithm}.
	 * 
	 * @return The direction of this {@link TreeLayoutAlgorithm}.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Changes the direction of this {@link TreeLayoutAlgorithm} to the given
	 * value. The direction may either be {@link #TOP_DOWN}, {@link #BOTTOM_UP},
	 * {@link #LEFT_RIGHT}, or {@link #RIGHT_LEFT}.
	 * 
	 * @param direction
	 *            The new direction for this {@link TreeLayoutAlgorithm}.
	 */
	public void setDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP
				|| direction == LEFT_RIGHT || direction == RIGHT_LEFT)
			this.direction = direction;
		else
			throw new IllegalArgumentException(
					"Invalid direction: " + direction);
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
	}

	public void setLayoutContext(ILayoutContext context) {
		if (treeObserver != null) {
			treeObserver.stop();
		}
		this.context = context;
		if (context != null) {
			treeObserver = new TreeLayoutObserver(context, null);
		}
	}

	public ILayoutContext getLayoutContext() {
		return context;
	}

	public void applyLayout(boolean clean) {
		if (!clean)
			return;

		internalApplyLayout();

		IEntityLayout[] entities = context.getEntities();

		if (resize)
			AlgorithmHelper.maximizeSizes(entities);
		scaleEntities(entities);
	}

	private void scaleEntities(IEntityLayout[] entities) {
		if (nodeSpace == null) {
			Rectangle bounds2 = new Rectangle(bounds);
			int insets = 4;
			bounds2.setX(bounds2.getX() + insets);
			bounds2.setY(bounds2.getY() + insets);
			bounds2.setWidth(bounds2.getWidth() - 2 * insets);
			bounds2.setHeight(bounds2.getHeight() - 2 * insets);
			AlgorithmHelper.fitWithinBounds(entities, bounds2, resize);
		}
	}

	/**
	 * Performs a layout pass for the tree without scaling the entities to
	 * maximum size / use the whole bounds.
	 */
	void internalApplyLayout() {
		TreeNode superRoot = treeObserver.getSuperRoot();
		bounds = LayoutProperties.getBounds(context);
		updateLeafAndLayerSizes();
		int leafCountSoFar = 0;
		for (Iterator<TreeNode> iterator = superRoot.getChildren()
				.iterator(); iterator.hasNext();) {
			TreeNode rootInfo = iterator.next();
			computePositionRecursively(rootInfo, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + rootInfo.numOfLeaves;
		}
	}

	private void updateLeafAndLayerSizes() {
		if (nodeSpace != null) {
			if (getDirection() == TOP_DOWN || getDirection() == BOTTOM_UP) {
				leafSize = nodeSpace.getWidth();
				layerSize = nodeSpace.getHeight();
			} else {
				leafSize = nodeSpace.getHeight();
				layerSize = nodeSpace.getWidth();
			}
		} else {
			TreeNode superRoot = treeObserver.getSuperRoot();
			if (direction == TOP_DOWN || direction == BOTTOM_UP) {
				leafSize = bounds.getWidth() / superRoot.numOfLeaves;
				layerSize = bounds.getHeight() / superRoot.height;
			} else {
				leafSize = bounds.getHeight() / superRoot.numOfLeaves;
				layerSize = bounds.getWidth() / superRoot.height;
			}
		}
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(TreeNode entityInfo,
			int relativePosition) {
		double breadthPosition = relativePosition
				+ entityInfo.numOfLeaves / 2.0;
		double depthPosition = (entityInfo.depth + 0.5);

		switch (direction) {
		case TOP_DOWN:
			LayoutProperties.setLocation(entityInfo.getNode(),
					breadthPosition * leafSize, depthPosition * layerSize);
			break;
		case BOTTOM_UP:
			LayoutProperties.setLocation(entityInfo.getNode(),
					breadthPosition * leafSize,
					bounds.getHeight() - depthPosition * layerSize);
			break;
		case LEFT_RIGHT:
			LayoutProperties.setLocation(entityInfo.getNode(),
					depthPosition * layerSize, breadthPosition * leafSize);
			break;
		case RIGHT_LEFT:
			LayoutProperties.setLocation(entityInfo.getNode(),
					bounds.getWidth() - depthPosition * layerSize,
					breadthPosition * leafSize);
			break;
		}

		for (Iterator<TreeNode> iterator = entityInfo.children
				.iterator(); iterator.hasNext();) {
			TreeNode childInfo = iterator.next();
			computePositionRecursively(childInfo, relativePosition);
			relativePosition += childInfo.numOfLeaves;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TreeLayout { direction : ");
		switch (direction) {
		case BOTTOM_UP:
			sb.append("bottom -> top");
			break;
		case LEFT_RIGHT:
			sb.append("left -> right");
			break;
		case RIGHT_LEFT:
			sb.append("right -> left");
			break;
		case TOP_DOWN:
			sb.append("top -> down");
			break;
		}
		sb.append(", resize : " + resize);
		sb.append(" }");
		// TODO: include node space??
		return sb.toString();
	}
}
