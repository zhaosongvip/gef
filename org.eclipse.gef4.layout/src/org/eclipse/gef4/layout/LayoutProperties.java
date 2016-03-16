/*******************************************************************************
 * Copyright (c) 2013, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.layout;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * {@link LayoutProperties} contains all properties which can be accessed on
 * {@link ILayoutContext}, {@link IEdgeLayout}, {@link INodeLayout}, their
 * default values, as well as static accessor methods for those properties.
 */
public class LayoutProperties {

	/**
	 * Stores the bounds in which the graph elements can be placed.
	 */
	public static final String BOUNDS_PROPERTY = "layout_bounds";

	/**
	 * Defines the default value for the {@link #BOUNDS_PROPERTY}.
	 */
	public static final Rectangle DEFAULT_BOUNDS = new Rectangle();

	/**
	 * Stores the location of this EntityLayout.
	 */
	public static final String LOCATION_PROPERTY = "layout_location";

	/**
	 * Defines the default value for the {@link #LOCATION_PROPERTY}.
	 */
	public static final Point DEFAULT_LOCATION = new Point();

	/**
	 * Stores the size of this EntityLayout.
	 */
	public static final String SIZE_PROPERTY = "layout_size";

	/**
	 * Defines the default value for the {@link #SIZE_PROPERTY}.
	 */
	public static final Dimension DEFAULT_SIZE = new Dimension();

	/**
	 * Stores the movable state of this EntityLayout.
	 */
	public static final String MOVABLE_PROPERTY = "layout_movable";

	/**
	 * Defines the default value for the {@link #MOVABLE_PROPERTY}.
	 */
	public static final Boolean DEFAULT_MOVABLE = true;

	/**
	 * Stores the resizable state of this EntityLayout. A resizable EntityLayout
	 * may be resized by a layout algorithm.
	 */
	public static final String RESIZABLE_PROPERTY = "layout_resizable";

	/**
	 * Defines the default value for the {@link #RESIZABLE_PROPERTY}.
	 */
	public static final Boolean DEFAULT_RESIZABLE = true;

	/**
	 * Stores the preferred aspect ratio (width / height) for this EntityLayout.
	 */
	public static final String ASPECT_RATIO_PROPERTY = "layout_aspect-ratio";

	/**
	 * Defines the default value for the {@link #ASPECT_RATIO_PROPERTY}.
	 */
	public static final Double DEFAULT_ASPECT_RATIO = 0d;

	/**
	 * Stores a weight for this connection.
	 */
	public static final String WEIGHT_PROPERTY = "layout_weight";

	/**
	 * Defines the default value for the {@link #WEIGHT_PROPERTY}.
	 */
	public static final Double DEFAULT_WEIGHT = 0d;

	/**
	 * Constant for top-down direction (default).
	 */
	public static final int DIRECTION_TOP_DOWN = 1;

	/**
	 * Constant for bottom-up direction.
	 */
	public static final int DIRECTION_BOTTOM_UP = 2;

	/**
	 * Constant for direction from left to right.
	 */
	public static final int DIRECTION_LEFT_RIGHT = 3;

	/**
	 * Constant for direction from right to left.
	 */
	public static final int DIRECTION_RIGHT_LEFT = 4;

	/**
	 * Stores the direction of this subgraph.
	 */
	public static final String DIRECTION_PROPERTY = "layout_direction";

	/**
	 * Defines the default value for the {@link #DIRECTION_PROPERTY}.
	 */
	public static final Integer DEFAULT_DIRECTION = DIRECTION_TOP_DOWN;

	/**
	 * Returns the value of the {@link #BOUNDS_PROPERTY} of the given
	 * {@link ILayoutContext}.
	 * 
	 * @param context
	 *            The {@link ILayoutContext} whose {@link #BOUNDS_PROPERTY} is
	 *            read.
	 * @return The value of the {@link #BOUNDS_PROPERTY} of the given
	 *         {@link ILayoutContext}.
	 */
	public static Rectangle getBounds(ILayoutContext context) {
		Object bounds = context.getAttributes().get(BOUNDS_PROPERTY);
		if (bounds instanceof Rectangle) {
			return ((Rectangle) bounds).getCopy();
		}
		return DEFAULT_BOUNDS.getCopy();
	}

	/**
	 * Sets the value of the {@link #BOUNDS_PROPERTY} of the given
	 * {@link ILayoutContext} to the given value.
	 * 
	 * @param context
	 *            The {@link ILayoutContext} whose {@link #BOUNDS_PROPERTY} is
	 *            changed.
	 * @param bounds
	 *            The new value for the {@link #BOUNDS_PROPERTY} of the given
	 *            {@link ILayoutContext}.
	 */
	public static void setBounds(ILayoutContext context, Rectangle bounds) {
		context.getAttributes().put(BOUNDS_PROPERTY, bounds);
	}

	/**
	 * Returns the value of the {@link #LOCATION_PROPERTY} of the given
	 * {@link INodeLayout}.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #LOCATION_PROPERTY} is
	 *            read.
	 * @return The value of the {@link #LOCATION_PROPERTY} of the given
	 *         {@link INodeLayout}.
	 */
	public static Point getLocation(INodeLayout node) {
		Object location = node.getAttributes().get(LOCATION_PROPERTY);
		if (location instanceof Point) {
			return ((Point) location).getCopy();
		}
		return DEFAULT_LOCATION.getCopy();
	}

	/**
	 * Returns the value of the {@link #SIZE_PROPERTY} of the given
	 * {@link INodeLayout}.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #SIZE_PROPERTY} is read.
	 * @return The value of the {@link #SIZE_PROPERTY} of the given
	 *         {@link INodeLayout}.
	 */
	public static Dimension getSize(INodeLayout node) {
		Object size = node.getAttributes().get(SIZE_PROPERTY);
		if (size instanceof Dimension) {
			return ((Dimension) size).getCopy();
		}
		return DEFAULT_SIZE.getCopy();
	}

	/**
	 * Returns the value of the {@link #ASPECT_RATIO_PROPERTY} of the given
	 * {@link INodeLayout}.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #ASPECT_RATIO_PROPERTY}
	 *            is read.
	 * @return The value of the {@link #ASPECT_RATIO_PROPERTY} of the given
	 *         {@link INodeLayout}.
	 */
	public static Double getPreferredAspectRatio(INodeLayout node) {
		Object ar = node.getAttributes().get(ASPECT_RATIO_PROPERTY);
		if (ar instanceof Double) {
			return (Double) ar;
		}
		return DEFAULT_ASPECT_RATIO;
	}

	/**
	 * Returns the value of the {@link #RESIZABLE_PROPERTY} of the given
	 * {@link INodeLayout}.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #RESIZABLE_PROPERTY} is
	 *            read.
	 * @return The value of the {@link #RESIZABLE_PROPERTY} of the given
	 *         {@link INodeLayout}.
	 */
	public static Boolean isResizable(INodeLayout node) {
		Object resizable = node.getAttributes().get(RESIZABLE_PROPERTY);
		if (resizable instanceof Boolean) {
			return (Boolean) resizable;
		}
		return DEFAULT_RESIZABLE;
	}

	/**
	 * Returns the value of the {@link #MOVABLE_PROPERTY} of the given
	 * {@link INodeLayout}.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #MOVABLE_PROPERTY} is
	 *            read.
	 * @return The value of the {@link #MOVABLE_PROPERTY} of the given
	 *         {@link INodeLayout}.
	 */
	public static Boolean isMovable(INodeLayout node) {
		Object movable = node.getAttributes().get(MOVABLE_PROPERTY);
		if (movable instanceof Boolean) {
			return (Boolean) movable;
		}
		return DEFAULT_MOVABLE;
	}

	/**
	 * Sets the value of the {@link #LOCATION_PROPERTY} of the given
	 * {@link INodeLayout} to the given value.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #LOCATION_PROPERTY} is
	 *            changed.
	 * @param x
	 *            The new x coordinate for the {@link #LOCATION_PROPERTY} of the
	 *            given {@link INodeLayout}.
	 * @param y
	 *            The new y coordinate for the {@link #LOCATION_PROPERTY} of the
	 *            given {@link INodeLayout}.
	 */
	public static void setLocation(INodeLayout node, double x, double y) {
		if (Double.isNaN(x)) {
			x = 0;
		}
		if (Double.isNaN(y)) {
			y = 0;
		}
		node.getAttributes().put(LOCATION_PROPERTY, new Point(x, y));
	}

	/**
	 * Sets the value of the {@link #SIZE_PROPERTY} of the given
	 * {@link INodeLayout} to the given value.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #SIZE_PROPERTY} is
	 *            changed.
	 * @param w
	 *            The new width for the {@link #SIZE_PROPERTY} of the given
	 *            {@link INodeLayout}.
	 * @param h
	 *            The new height for the {@link #SIZE_PROPERTY} of the given
	 *            {@link INodeLayout}.
	 */
	public static void setSize(INodeLayout node, double w, double h) {
		if (Double.isNaN(w)) {
			w = 0;
		}
		if (Double.isNaN(h)) {
			h = 0;
		}
		node.getAttributes().put(SIZE_PROPERTY, new Dimension(w, h));
	}

	/**
	 * Sets the value of the {@link #RESIZABLE_PROPERTY} of the given
	 * {@link INodeLayout} to the given value.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose {@link #RESIZABLE_PROPERTY} is
	 *            changed.
	 * @param resizable
	 *            The new value for the {@link #RESIZABLE_PROPERTY} of the given
	 *            {@link INodeLayout}.
	 */
	public static void setResizable(INodeLayout node, boolean resizable) {
		node.getAttributes().put(RESIZABLE_PROPERTY, resizable);
	}

	/**
	 * Returns the value of the {@link #WEIGHT_PROPERTY} of the given
	 * {@link IEdgeLayout}.
	 * 
	 * @param edge
	 *            The {@link IEdgeLayout} whose {@link #WEIGHT_PROPERTY} is
	 *            read.
	 * @return The value of the {@link #WEIGHT_PROPERTY} of the given
	 *         {@link IEdgeLayout}.
	 */
	public static Double getWeight(IEdgeLayout edge) {
		Object weight = edge.getAttributes().get(WEIGHT_PROPERTY);
		if (weight instanceof Double) {
			return (Double) weight;
		}
		return DEFAULT_WEIGHT;
	}

	/**
	 * Sets the value of the {@link #WEIGHT_PROPERTY} of the given
	 * {@link IEdgeLayout} to the given value.
	 * 
	 * @param edge
	 *            The {@link IEdgeLayout} whose {@link #WEIGHT_PROPERTY} is
	 *            changed.
	 * @param weight
	 *            The new value for the {@link #WEIGHT_PROPERTY} of the given
	 *            {@link IEdgeLayout}.
	 */
	public static void setWeight(IEdgeLayout edge, double weight) {
		edge.getAttributes().put(WEIGHT_PROPERTY, weight);
	}

}
