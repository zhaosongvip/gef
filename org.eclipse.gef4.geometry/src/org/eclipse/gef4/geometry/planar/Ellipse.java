/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of an ellipse.
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 * @author Matthias Wienand
 */
public class Ellipse extends AbstractRectangleBasedGeometry<Ellipse> implements
		IShape {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Ellipse} so that it is fully contained within the
	 * framing rectangle defined by (x, y, width, height).
	 * 
	 * @param x
	 *            The x-coordinate of the framing rectangle
	 * @param y
	 *            The y-coordinate of the framing rectangle
	 * @param width
	 *            The width of the framing rectangle
	 * @param height
	 *            The height of the framing rectangle
	 */
	public Ellipse(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a new {@link Ellipse} so that it is fully contained within the
	 * given framing {@link Rectangle}.
	 * 
	 * @param r
	 *            The framing {@link Rectangle} used to construct this
	 *            {@link Ellipse}.
	 */
	public Ellipse(Rectangle r) {
		this(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Tests whether the given {@link Line} is fully contained within this
	 * {@link Ellipse}.
	 * 
	 * @param l
	 *            the {@link Line} to test for containment
	 * @return <code>true</code> in case the given {@link Line} is fully
	 *         contained, <code>false</code> otherwise
	 */
	public boolean contains(Line l) {
		return contains(l.getP1()) && contains(l.getP2());
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		/*
		 * point has to fulfill (x/a)^2 + (y/b)^2 <= 1, where a = width/2 and b
		 * = height/2, if ellipse is centered around origin, so we have to
		 * normalize point p by subtracting the center
		 */
		double normalizedX = p.x - (x + width / 2);
		double normalizedY = p.y - (y + height / 2);

		// then check if it fulfills the ellipse equation
		if (PrecisionUtils.smallerEqual(normalizedX * normalizedX
				/ (width * width * 0.25d) + normalizedY * normalizedY
				/ (height * height * 0.25d), 1d)) {
			return true;
		}

		// is it "on" the outline?
		for (CubicCurve seg : getOutlineSegments()) {
			if (seg.contains(p)) {
				return true;
			}
		}

		// TODO: what about the small space between outline and ellipse area?

		return false;
	}

	/**
	 * @see IShape#contains(IGeometry)
	 */
	public boolean contains(IGeometry g) {
		return CurveUtils.contains(this, g);
	}

	/**
	 * Tests whether this {@link Ellipse} and the ellipse defined by the given
	 * bounds are equal.
	 * 
	 * @param x
	 *            the x-coordinate of the bounds defining define the ellipse to
	 *            test
	 * @param y
	 *            the y-coordinate of the bounds defining the ellipse to test
	 * @param width
	 *            the width of the bounds defining the ellipse to test
	 * @param height
	 *            the height of the bounds defining the ellipse to test
	 * @return <code>true</code> if this {@link Ellipse} and the ellipse defined
	 *         via the given bounds are (imprecisely) regarded to be equal,
	 *         <code>false</code> otherwise
	 */
	public boolean equals(double x, double y, double width, double height) {
		return PrecisionUtils.equal(this.x, x)
				&& PrecisionUtils.equal(this.y, y)
				&& PrecisionUtils.equal(this.width, width)
				&& PrecisionUtils.equal(this.height, height);
	}

	/**
	 * Tests whether this {@link Ellipse} is equal to the given {@link Object}.
	 * 
	 * @return <code>true</code> if the given {@link Object} is an
	 *         {@link Ellipse}, which is (imprecisely) equal to this
	 *         {@link Ellipse}, i.e. whose bounds are (imprecisely) equal to
	 *         this {@link Ellipse}'s bounds
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Ellipse) {
			Ellipse e = (Ellipse) o;
			return equals(e.getX(), e.getY(), e.getWidth(), e.getHeight());
		}
		return false;
	}

	/**
	 * Returns the center location of this {@link Ellipse}.
	 * 
	 * @return a {@link Point}, representing the center of this {@link Ellipse}.
	 */
	public Point getCenter() {
		return new Point(x + width / 2, y + height / 2);
	}

	/**
	 * Returns a new {@link Ellipse} with the same location and size than this
	 * one.
	 * 
	 * @return A copy of this {@link Ellipse}, having the same x, y, width, and
	 *         height values
	 */
	public Ellipse getCopy() {
		return new Ellipse(x, y, width, height);
	}

	/**
	 * Calculates the intersections of this {@link Ellipse} with the given
	 * {@link ICurve}.
	 * 
	 * @param c
	 * @return {@link Point}s of intersection
	 */
	public Point[] getIntersections(ICurve c) {
		if (c instanceof Line) {
			return getIntersections((Line) c);
		}
		return CurveUtils.getIntersections(c, this);
	}

	/**
	 * Calculates the intersections of this {@link Ellipse} with the given other
	 * {@link Ellipse}.
	 * 
	 * @param e2
	 * @return {@link Point}s of intersection
	 */
	public Point[] getIntersections(Ellipse e2) {
		if (equals(e2)) {
			return new Point[] {};
		}

		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getOutlineSegments()) {
			intersections.addAll(Arrays.asList(e2.getIntersections(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the intersection points of this {@link Ellipse}'s outline with
	 * the given {@link Line}.
	 * 
	 * @param line
	 *            the {@link Line} to test for intersection
	 * @return an array containing the intersection points of this
	 *         {@link Ellipse}'s outline with the given {@link Line} in case
	 *         such intersection points exist, an empty array otherwise
	 */
	public Point[] getIntersections(Line line) {
		List<Point> intersections = new ArrayList<Point>(2);

		// ellipse equation x^2/(width/2)^2 + y^2/(height/2)^2 = 1 may be
		// written as x^2/aSq + y^2/bSq = 1 with:
		double a = width / 2;
		double b = height / 2;
		double aSq = width * width * 0.25d;
		double bSq = height * height * 0.25d;

		// ellipse's center
		double xOffset = x + a;
		double yOffset = y + b;

		// normalized line points as if the ellipse was centered around origin
		double x1 = line.getX1() - xOffset;
		double y1 = line.getY1() - yOffset;
		double x2 = line.getX2() - xOffset;
		double y2 = line.getY2() - yOffset;

		// deltas to calculate the line's slope
		double dy = y2 - y1;
		double dx = x2 - x1;

		// special-case the vertical line
		if (PrecisionUtils.equal(dx, 0, +2)) {
			// vertical line
			if (PrecisionUtils.smallerEqual(-a, x1)
					&& PrecisionUtils.smallerEqual(x1, a)) { // -a <= x1 <= a
				// inside the ellipse
				double rad = bSq * (1 - x1 * x1 / aSq);
				double y = rad < 0 ? 0 : Math.sqrt(rad);

				if (PrecisionUtils.greaterEqual(y1, y)) {
					if (PrecisionUtils.smallerEqual(y2, y)) {
						intersections.add(new Point(x1, y));
					}
					if (!PrecisionUtils.equal(y, 0)
							&& PrecisionUtils.smallerEqual(y2, -y)) {
						intersections.add(new Point(x1, -y));
					}
				} else if (PrecisionUtils.smallerEqual(y1, -y)) {
					if (PrecisionUtils.greaterEqual(y2, -y)) {
						intersections.add(new Point(x1, -y));
					}
					if (!PrecisionUtils.equal(y, 0)
							&& PrecisionUtils.greaterEqual(y2, y)) {
						intersections.add(new Point(x1, y));
					}
				}
			}
		} else {
			// calculating the line function's slope and y-offset:
			double m = dy / dx;
			double n = y1 - m * x1;

			// substituting y within ellipse equation leads to a quadratic
			// equation of the form q2*x^2 + q1*x + q0 = 0 with:
			double q2 = bSq + aSq * m * m;
			double q1 = 2 * m * aSq * n;
			double q0 = aSq * (n * n - bSq);

			// values for the pq-formula:
			double p = q1 / q2;
			double q = q0 / q2;

			// check if equation has at least one solution
			double d = p * p / 4 - q;
			if (PrecisionUtils.equal(d, 0, +2)) {
				// discriminant equals zero, so one possible solution
				double px = -p / 2;
				double py = px * m + n;
				intersections.add(new Point(px, py));
			} else if (d > 0) {
				// discriminant greater than zero, so two possible solutions
				double sqrt = d < 0 ? 0 : Math.sqrt(d);

				// first
				double px = -p / 2 + sqrt;
				double py = px * m + n;
				intersections.add(new Point(px, py));

				// second
				px = -p / 2 - sqrt;
				py = px * m + n;
				intersections.add(new Point(px, py));
			}
		}

		// sort out all points that do not lie on the given line and translate
		// the coordinates back:
		for (Iterator<Point> i = intersections.iterator(); i.hasNext();) {
			Point p = i.next();
			p.translate(xOffset, yOffset);
			if (!line.contains(p)) {
				i.remove();
			}
		}

		return intersections.toArray(new Point[] {});
	}

	public IPolyCurve getOutline() {
		return CurveUtils.getOutline(this);
	}

	/**
	 * Calculates the border segments of this {@link Ellipse}. The
	 * border-segments are approximated by {@link CubicCurve}s. These curves are
	 * generated as in the {@link Ellipse#toPath()} method.
	 * 
	 * @return border-segments
	 */
	public CubicCurve[] getOutlineSegments() {
		CubicCurve[] segs = new CubicCurve[4];
		// see http://whizkidtech.redprince.net/bezier/circle/kappa/ for details
		// on the approximation used here
		final double kappa = 4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d;
		double a = width / 2;
		double b = height / 2;

		double ox = x + a;
		double oy = y;

		segs[0] = new CubicCurve(ox, oy, x + a + kappa * a, y, x + width, y + b
				- kappa * b, x + width, y + b);

		ox = x + width;
		oy = y + b;

		segs[1] = new CubicCurve(ox, oy, x + width, y + b + kappa * b, x + a
				+ kappa * a, y + height, x + a, y + height);

		ox = x + a;
		oy = y + height;

		segs[2] = new CubicCurve(ox, oy, x + width / 2 - kappa * width / 2, y
				+ height, x, y + height / 2 + kappa * height / 2, x, y + height
				/ 2);

		ox = x;
		oy = y + height / 2;

		segs[3] = new CubicCurve(ox, oy, x,
				y + height / 2 - kappa * height / 2, x + width / 2 - kappa
						* width / 2, y, x + width / 2, y);

		return segs;
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	public IGeometry getTransformed(AffineTransform t) {
		// choose a path implementation
		return toPath().getTransformed(t);
	}

	/**
	 * Returns a path representation of this {@link Ellipse}, which is an
	 * approximation of the four segments by means of cubic Bezier curves.
	 * 
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		// see http://whizkidtech.redprince.net/bezier/circle/kappa/ for details
		// on the approximation used here
		final double kappa = 4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d;
		final Path p = new Path();
		double a = width / 2;
		double b = height / 2;
		p.moveTo(x + a, y);
		p.curveTo(x + a + kappa * a, y, x + width, y + b - kappa * b,
				x + width, y + b);
		p.curveTo(x + width, y + b + kappa * b, x + a + kappa * a, y + height,
				x + a, y + height);
		p.curveTo(x + width / 2 - kappa * width / 2, y + height, x, y + height
				/ 2 + kappa * height / 2, x, y + height / 2);
		p.curveTo(x, y + height / 2 - kappa * height / 2, x + width / 2 - kappa
				* width / 2, y, x + width / 2, y);
		return p;
	}

	@Override
	public String toString() {
		return "Ellipse: (" + x + ", " + y + ", " + //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				width + ", " + height + ")";//$NON-NLS-2$//$NON-NLS-1$
	}

}
