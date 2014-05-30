package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import br.ufrn.msed.s20141.dsj.petrinet.models.Node;
import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.PetrinetObject;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.StateTree;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
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

public class TreeVisualization extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5301071321270067755L;
	Petrinet petrinet;
    Forest<Node, Integer> graph;
    
    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Node, Integer> vv;
    
    VisualizationServer.Paintable rings;
    
    String root;
    
    TreeLayout<Node, Integer> treeLayout;
    
    RadialTreeLayout<Node, Integer> radialLayout;
	
	public TreeVisualization(Petrinet petrinet) {
		super(new BorderLayout());
		this.petrinet = petrinet;
		if (this.petrinet.getPlaces().size()>1)
			this.graph = this.petrinet.getStateTree().getGraphModelRepresentation();
		else
			this.graph = new DelegateForest<Node,Integer>();

		
		// Definicao de layout
        treeLayout = new TreeLayout<Node, Integer>(this.graph);
        radialLayout = new RadialTreeLayout<Node, Integer>(this.graph);
        radialLayout.setSize(new Dimension(500,500));
        vv =  new VisualizationViewer<Node, Integer>(treeLayout, new Dimension(500,500));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
//        rings = new Rings();

        // Adiciona borda na visualizacao
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        this.add(panel);
        panel.setBorder(
    		    BorderFactory.createTitledBorder("Árvore de cobertura"));
        panel.add(vv);
        this.add(panel);
        
        // Define o listener de Mouse e Teclado
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		
		// Define a cor dos Nodes, a depender se for: Raiz, bloqueante e não bloqueante
        Transformer<Node,Paint> vertexPaint = new Transformer<Node,Paint>() {
			public Paint transform(Node i) {
				if (i == i.getRoot())
					return Color.GREEN;
				if (i.isBlocking())
					return Color.RED;
				else
					return Color.LIGHT_GRAY;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		

//        JComboBox modeBox = graphMouse.getModeComboBox();
//        modeBox.addItemListener(graphMouse.getModeListener());
//        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

//        final ScalingControl scaler = new CrossoverScalingControl();
//
//        JButton plus = new JButton("+");
//        plus.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                scaler.scale(vv, 1.1f, vv.getCenter());
//            }
//        });
//        JButton minus = new JButton("-");
//        minus.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                scaler.scale(vv, 1/1.1f, vv.getCenter());
//            }
//        });
//        
//        JToggleButton radial = new JToggleButton("Radial");
//        radial.addItemListener(new ItemListener() {
//
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange() == ItemEvent.SELECTED) {
//					
//					LayoutTransition<Node, Integer> lt =
//						new LayoutTransition<Node, Integer>(vv, treeLayout, radialLayout);
//					Animator animator = new Animator(lt);
//					animator.start();
//					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
//					vv.addPreRenderPaintable(rings);
//				} else {
//					LayoutTransition<Node, Integer> lt =
//						new LayoutTransition<Node, Integer>(vv, radialLayout, treeLayout);
//					Animator animator = new Animator(lt);
//					animator.start();
//					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
//					vv.removePreRenderPaintable(rings);
//				}
//				vv.repaint();
//			}});
//
//        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
//        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

//        JPanel controls = new JPanel();
//        scaleGrid.add(plus);
//        scaleGrid.add(minus);
//        controls.add(radial);
//        controls.add(scaleGrid);
//        controls.add(modeBox);
//
//        this.add(controls, BorderLayout.SOUTH);
        
        
		
		
		
	}
	
	
	
	
	
	// Visualizacao em anel, nao utilizada por hora
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
    
    public VisualizationViewer<Node, Integer> getViewer() {
		return vv;
	}

}
