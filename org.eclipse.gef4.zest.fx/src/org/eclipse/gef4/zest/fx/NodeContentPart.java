/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXLabeledNode;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class NodeContentPart extends AbstractFXContentPart {

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";

	protected org.eclipse.gef4.graph.Node node;
	protected FXLabeledNode visual = new FXLabeledNode();
	protected IFXAnchor anchor;

	{
		visual.getStyleClass().add(CSS_CLASS);
	}

	public NodeContentPart(org.eclipse.gef4.graph.Node content) {
		node = content;
		Map<String, Object> attrs = node.getAttrs();
		if (attrs.containsKey(ATTR_CLASS)) {
			visual.getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			visual.setId((String) attrs.get(ATTR_ID));
		}

		// TODO: setAdapters() via Guice binding

		setAdapter(NodeLayoutPolicy.class, new NodeLayoutPolicy());
		setAdapter(NodeLayoutBehavior.class, new NodeLayoutBehavior());

		// interaction policies
		setAdapter(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
				new FXRelocateOnDragPolicy());

		// transaction policies
		setAdapter(FXResizeRelocatePolicy.class, new FXResizeRelocatePolicy());
	}

	@Override
	public void doRefreshVisual() {
		Object label = node.getAttrs().get(Attr.Key.LABEL.toString());
		String str = label instanceof String ? (String) label
				: label == null ? "-" : label.toString();
		visual.setLabel(str);
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			// TODO: when to dispose the anchor properly??
			anchor = new FXChopBoxAnchor(visual);
		}
		return anchor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> getContentAnchored() {
		if (getParent() != null) {
			Object content = getViewer().getContentModel().getContents().get(0);
			if (!(content instanceof Graph)) {
				throw new IllegalStateException(
						"Wrong content! Expected <Graph> but got <"
								+ content.getClass() + ">.");
			}
			List<Edge> edges = ((Graph) content).getEdges();
			List<Edge> anchored = new ArrayList<Edge>();
			for (Edge e : edges) {
				if (e.getTarget() == node || e.getSource() == node) {
					anchored.add(e);
				}
			}
			return (List) anchored;
		}
		return super.getContentAnchored();
	}

	@Override
	public Node getVisual() {
		return visual;
	}

}
