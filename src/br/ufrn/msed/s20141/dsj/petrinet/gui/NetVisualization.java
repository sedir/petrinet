package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import br.ufrn.msed.s20141.dsj.petrinet.gui.support.MultiVertexRenderer;
import br.ufrn.msed.s20141.dsj.petrinet.models.Arc;
import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.PetrinetObject;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class NetVisualization extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441662300562931930L;

	Petrinet pn;
	VisualizationViewer<PetrinetObject, Arc> vv;


	public NetVisualization(Petrinet pn) {
		super(new BorderLayout());
		this.pn = pn;

		vv = new VisualizationViewer<PetrinetObject,Arc>(new KKLayout<PetrinetObject,Arc>(pn.getGraphicRepresentation()), new Dimension(500,500));

		// Definicao de borda da visualizacao
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        panel.setBorder(
    		    BorderFactory.createTitledBorder("Rede de Petri"));
        panel.add(vv);
        this.add(panel);
        
        
        // Listener de clique nas Transitions
		final PickedState<PetrinetObject> pickedState = vv.getPickedVertexState();
		pickedState.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
				// The graph uses Integers for vertices
				if (subject instanceof Transition) {
					Transition transition = (Transition) subject;
					if (pickedState.isPicked(transition)) {
						if (transition.fire()){
							pickedState.pick(transition, false);
						}
					}
				}

			}
		});

		
		// Definicao de estilo
		vv.setBackground(Color.white);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<PetrinetObject>());
		vv.setVertexToolTipTransformer(new ToStringLabeller<PetrinetObject>());
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

		// Definicao de cor do Place
		Transformer<PetrinetObject,Paint> vertexPaint = new Transformer<PetrinetObject,Paint>() {
			public Paint transform(PetrinetObject i) {
				if (i instanceof Place)
					return Color.GRAY;
				else{
					if (((Transition) i).canFire())
						return Color.RED;
					else
						return Color.BLACK;
				}

			}
		};

		// Definicao de estilo do Arc
//		float dash[] = {10.0f};
//		final Stroke edgeStroke = new BasicStroke(1.5f, BasicStroke.JOIN_MITER,
//				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//		Transformer<Arc, Stroke> edgeStrokeTransformer =
//				new Transformer<Arc, Stroke>() {
//			public Stroke transform(Arc s) {
//				return edgeStroke;
//			}
//		};

		// Definicao de distincao entre os Nodes do Grafo
		Transformer<PetrinetObject, Shape> shapeTransformer = new Transformer<PetrinetObject, Shape>() {
			@Override
			public Shape transform(PetrinetObject arg0) {
				// TODO Auto-generated method stub
				if (arg0 instanceof Transition)
					return new Rectangle(0, -18, 8, 36);
				else
					return new Ellipse2D.Float(-25.0f, -15f, 35.0f, 35.0f);
			}

		};

		vv.getRenderContext().setVertexShapeTransformer(shapeTransformer);
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderer().setVertexRenderer(new MultiVertexRenderer<PetrinetObject,Arc>());
		//		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<PetrinetObject>());
		//		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);

		vv.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
		vv.getRenderContext().setVertexIconTransformer(new Transformer<PetrinetObject, Icon>() {

			@Override
			public Icon transform(PetrinetObject obj) {

				if (obj instanceof Place){
					Place place = (Place) obj;
					BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
					Graphics2D graphics = img.createGraphics();
					graphics.drawString(place.getTokens()+"",0,20);
					return new ImageIcon(img);
				}

				return new ImageIcon();
			}
		});

		// Listener de Mouse e Teclado
		DefaultModalGraphMouse<PetrinetObject,Arc> gm = new DefaultModalGraphMouse<PetrinetObject,Arc>();

		gm.setMode(ModalGraphMouse.Mode.PICKING);

		vv.setGraphMouse(gm); 
		vv.addKeyListener(gm.getModeKeyListener());



	}
	

	public VisualizationViewer<PetrinetObject, Arc> getViewer() {
		return vv;
	}

}
