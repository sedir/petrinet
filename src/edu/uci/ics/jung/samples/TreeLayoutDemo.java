/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package edu.uci.ics.jung.samples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

import br.ufrn.msed.s20141.dsj.petrinet.models.Node;
import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.StateTree;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * Demonsrates TreeLayout and RadialTreeLayout.
 * @author Tom Nelson
 * 
 */
@SuppressWarnings("serial")
public class TreeLayoutDemo extends JApplet {

    /**
     * the graph
     */
    Forest<Node, Integer> graph;
    
    Factory<DirectedGraph<Node, Integer>> graphFactory = 
    	new Factory<DirectedGraph<Node, Integer>>() {

			public DirectedGraph<Node, Integer> create() {
				return new DirectedSparseMultigraph<Node, Integer>();
			}
		};
			
	Factory<Tree<Node, Integer>> treeFactory =
		new Factory<Tree<Node, Integer>> () {

		public Tree<Node, Integer> create() {
			return new DelegateTree<Node, Integer>(graphFactory);
		}
	};
	
	Factory<Integer> edgeFactory = new Factory<Integer>() {
		int i=0;
		public Integer create() {
			return i++;
		}};
    
    Factory<String> vertexFactory = new Factory<String>() {
    	int i=0;
		public String create() {
			return "V"+i++;
		}};

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Node, Integer> vv;
    
    VisualizationServer.Paintable rings;
    
    String root;
    
    TreeLayout<Node, Integer> treeLayout;
    
    RadialTreeLayout<Node, Integer> radialLayout;

    public TreeLayoutDemo() {
        
        // create a simple graph for the demo
        graph = new DelegateForest<Node, Integer>();

        createTree();
        
        treeLayout = new TreeLayout<Node, Integer>(graph);
        radialLayout = new RadialTreeLayout<Node, Integer>(graph);
        radialLayout.setSize(new Dimension(600,600));
        vv =  new VisualizationViewer<Node, Integer>(treeLayout, new Dimension(600,600));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        rings = new Rings();

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        
        JToggleButton radial = new JToggleButton("Radial");
        radial.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					LayoutTransition<Node, Integer> lt =
						new LayoutTransition<Node, Integer>(vv, treeLayout, radialLayout);
					Animator animator = new Animator(lt);
					animator.start();
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.addPreRenderPaintable(rings);
				} else {
					LayoutTransition<Node, Integer> lt =
						new LayoutTransition<Node, Integer>(vv, radialLayout, treeLayout);
					Animator animator = new Animator(lt);
					animator.start();
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.removePreRenderPaintable(rings);
				}
				vv.repaint();
			}});

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(radial);
        controls.add(scaleGrid);
        controls.add(modeBox);

        content.add(controls, BorderLayout.SOUTH);
    }
    
    class Rings implements VisualizationServer.Paintable {
    	
    	Collection<Double> depths;
    	
    	public Rings() {
    		depths = getDepths();
    	}
    	
    	private Collection<Double> getDepths() {
    		Set<Double> depths = new HashSet<Double>();
    		Map<Node,PolarPoint> polarLocations = radialLayout.getPolarLocations();
    		for(Node v : graph.getVertices()) {
    			PolarPoint pp = polarLocations.get(v);
    			depths.add(pp.getRadius());
    		}
    		return depths;
    	}

		public void paint(Graphics g) {
			g.setColor(Color.lightGray);
		
			Graphics2D g2d = (Graphics2D)g;
			Point2D center = radialLayout.getCenter();

			Ellipse2D ellipse = new Ellipse2D.Double();
			for(double d : depths) {
				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
						center.getX()+d, center.getY()+d);
				Shape shape = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
    }
    
    /**
     * 
     */
    private void createTree() {
    	Petrinet pn = new Petrinet("Minha rede");
		Place p1 = pn.addPlace("p1",1);
		Place p2 = pn.place("p2");
		Place p3 = pn.place("p3");
		Place p4 = pn.place("p4");
		
		Transition t1 = pn.addTransition("t1");
		Transition t2 = pn.addTransition("t2");
		Transition t3 = pn.addTransition("t3");

		pn.arc(p1, t1);
		pn.arc(t1, p2);
		pn.arc(t1, p3);
		
		pn.arc(t2, p1);
		pn.arc(p2, t2);
		
		pn.arc(p2, t3);
		pn.arc(t3, p4);
		
		DenseMatrix64F A = new DenseMatrix64F(pn.incidenceMatrix());
		
		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);

        if( !svd.decompose(A) )
            throw new RuntimeException("Decomposition failed");

        DenseMatrix64F U = svd.getU(null,false);
        DenseMatrix64F W = svd.getW(null);
        DenseMatrix64F V = svd.getV(null,false);
        
        DenseMatrix64F result = new DenseMatrix64F(A.numRows,A.numCols); 
        		
        SingularOps.nullSpace(svd, result, 0.00001);
        
        System.out.println(result);
        
        System.out.println(A);
				
		graph = new StateTree(pn).getGraphModelRepresentation();
    }


    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new TreeLayoutDemo());
        frame.pack();
        frame.setVisible(true);
    }
}
