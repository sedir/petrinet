package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;

public class IncidenceMatrixPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1506452882714039301L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("br.ufrn.msed.s20141.dsj.petrinet.gui.messages"); //$NON-NLS-1$
	private Petrinet petrinet;

	public IncidenceMatrixPane(Petrinet petrinet) {
		// TODO Auto-generated constructor stub
		super();
		this.petrinet = petrinet;
		
		GridBagLayout gridBagLayout = new GridBagLayout();

		setLayout(gridBagLayout);
		setBorder(
				BorderFactory.createTitledBorder(BUNDLE.getString("StatusPane.btnMatrizDeIncidncia.text")));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = c.ipady = 10;
		double[][] matrix = petrinet.incidenceMatrix();
		//		c.insets = new Insets(10,10,10,10);
		Label empty = new Label("");
		empty.setAlignment(Label.CENTER);
		empty.setBackground(Color.GRAY);
		add(empty,c);
		Dimension cellDim = new Dimension(30, 30);
		c.gridy = 0;
		c.gridx = 1;
		for (Place p : petrinet.getPlaces()) {
			Label l = new Label(p.getName());
			l.setAlignment(Label.CENTER);
			l.setBackground(Color.GRAY);
			l.setForeground(Color.WHITE);
			l.setPreferredSize(cellDim);
			add(l,c);
			c.gridx++;
		}
		c.gridy = 1;
		c.gridx = 0;		
		for (Transition t : petrinet.getTransitions()) {
			Label l = new Label(t.getName());
			l.setAlignment(Label.CENTER);
			l.setBackground(Color.GRAY);
			l.setForeground(Color.WHITE);
			l.setPreferredSize(cellDim);
			add(l,c);
			c.gridy++;
		}
		c.gridy = 0;
		c.gridx = 0;
		for (int i = 0; i < matrix.length; i++) {
			c.gridy +=1;
			c.gridx = 0;
			for (int j = 0; j < matrix[i].length; j++) {
				c.gridx +=1;
				Label l = new Label(String.format("%d",(int) matrix[i][j]));
				l.setAlignment(Label.CENTER);
				l.setPreferredSize(cellDim);
				add(l,c);
				
			}
		}

	}

}
