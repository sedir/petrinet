package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.Insets;

import javax.swing.ImageIcon;

import java.util.ResourceBundle;

public class StatusPane extends JPanel {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("br.ufrn.msed.s20141.dsj.petrinet.gui.messages"); //$NON-NLS-1$
	private static final String STANDARD = "                                      ";

	
	private JLabel lblBlockingStates;
	private JLabel lblConservability;
	private JLabel lblNonLimitedStates;
	public StatusPane() {
		super();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{1.0};

		setLayout(gridBagLayout);
		setBorder(
				BorderFactory.createTitledBorder(BUNDLE.getString("StatusPane.title")));
		
		lblBlockingStates = new JLabel(BUNDLE.getString("StatusPane.lblBlockingStates.yes")); //$NON-NLS-1$
		lblBlockingStates.setIcon(new ImageIcon(StatusPane.class.getResource("images/yes.png")));
		GridBagConstraints gbc_lblBlockingStates = new GridBagConstraints();
		gbc_lblBlockingStates.insets = new Insets(0, 0, 5, 0);
		gbc_lblBlockingStates.anchor = GridBagConstraints.WEST;
		gbc_lblBlockingStates.gridx = 0;
		gbc_lblBlockingStates.gridy = 0;
		add(lblBlockingStates, gbc_lblBlockingStates);

		lblConservability = new JLabel(BUNDLE.getString("StatusPane.lblConservability.no")); //$NON-NLS-1$
		lblConservability.setIcon(new ImageIcon(StatusPane.class.getResource("images/no.png")));
		GridBagConstraints gbc_lblConservability = new GridBagConstraints();
		gbc_lblConservability.insets = new Insets(0, 0, 5, 0);
		gbc_lblConservability.anchor = GridBagConstraints.WEST;
		gbc_lblConservability.gridx = 0;
		gbc_lblConservability.gridy = 1;
		add(lblConservability, gbc_lblConservability);

		lblNonLimitedStates = new JLabel(BUNDLE.getString("StatusPane.lblNonLimitedStates.no")); //$NON-NLS-1$
		lblNonLimitedStates.setIcon(new ImageIcon(StatusPane.class.getResource("/br/ufrn/msed/s20141/dsj/petrinet/gui/images/yes.png")));
		GridBagConstraints gbc_lblNonLimitedStates = new GridBagConstraints();
		gbc_lblNonLimitedStates.anchor = GridBagConstraints.WEST;
		gbc_lblNonLimitedStates.gridx = 0;
		gbc_lblNonLimitedStates.gridy = 2;
		add(lblNonLimitedStates, gbc_lblNonLimitedStates);

	}
	
	public void setBlockingStates(boolean value) {
		String string = value?"yes":"no";
		lblBlockingStates.setText(BUNDLE.getString(String.format("StatusPane.lblBlockingStates.%s",string)));
		lblBlockingStates.setIcon(new ImageIcon(StatusPane.class.getResource(String.format("images/%s.png",string))));
	}
	
	public void setConservability(boolean value) {
		String string = value?"yes":"no";
		lblConservability.setText(BUNDLE.getString(String.format("StatusPane.lblConservability.%s",string)));
		lblConservability.setIcon(new ImageIcon(StatusPane.class.getResource(String.format("images/%s.png",string))));
	}
	
	public void setNonLimitedStates(boolean value){
		String string = value?"yes":"no";
		lblNonLimitedStates.setText(BUNDLE.getString(String.format("StatusPane.lblNonLimitedStates.%s",string)));
		lblNonLimitedStates.setIcon(new ImageIcon(StatusPane.class.getResource(String.format("images/%s.png",string))));
	}
	
	public void reset(){
		lblBlockingStates.setText(STANDARD);
		lblNonLimitedStates.setText(STANDARD);
		lblConservability.setText(STANDARD);
		
		lblBlockingStates.setIcon(null);
		lblConservability.setIcon(null);
		lblNonLimitedStates.setIcon(null);
	}
}
