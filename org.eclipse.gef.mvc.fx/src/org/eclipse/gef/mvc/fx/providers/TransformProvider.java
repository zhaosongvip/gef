/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link TransformProvider} can be registered on an {@link IVisualPart} to
 * insert an {@link Affine} into its visual's transformations list and access
 * that {@link Affine}. Per default, this {@link Affine} is manipulated to
 * relocate or transform an {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
// TODO: Replace with transform as first-level concept (property) in IVisualPart
public class TransformProvider
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
		implements Provider<Affine> {

	private Affine affine = null;

	@Override
	public Affine get() {
		if (affine == null) {
			affine = new Affine();
			if (!(getAdaptable() instanceof IBendableContentPart)) {
				getAdaptable().getVisual().getTransforms().add(affine);
			}
		}
		return affine;
	}
}
