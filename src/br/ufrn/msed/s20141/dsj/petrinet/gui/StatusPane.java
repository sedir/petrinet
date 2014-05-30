package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.omg.CORBA.portable.IndirectionException;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;

public class StatusPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3945410943429128945L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("br.ufrn.msed.s20141.dsj.petrinet.gui.messages"); //$NON-NLS-1$
	private static final String STANDARD = "                                      ";

	
	private JLabel lblBlockingStates;
	private JLabel lblConservability;
	private JLabel lblNonLimitedStates;
	private JButton btnMatrizDeIncidncia;
	private Petrinet petrinet;
	
	public StatusPane() {
		super();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
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
		gbc_lblNonLimitedStates.insets = new Insets(0, 0, 5, 0);
		gbc_lblNonLimitedStates.anchor = GridBagConstraints.WEST;
		gbc_lblNonLimitedStates.gridx = 0;
		gbc_lblNonLimitedStates.gridy = 2;
		add(lblNonLimitedStates, gbc_lblNonLimitedStates);
		
		btnMatrizDeIncidncia = new JButton(BUNDLE.getString("StatusPane.btnMatrizDeIncidncia.text")); //$NON-NLS-1$
		btnMatrizDeIncidncia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog();
				dialog.setContentPane(new IncidenceMatrixPane(petrinet));
				dialog.setModal(true);
				dialog.pack();
				dialog.setTitle("SSRP");
				dialog.setVisible(true);
				
			}
		});
		GridBagConstraints gbc_btnMatrizDeIncidncia = new GridBagConstraints();
		gbc_btnMatrizDeIncidncia.gridx = 0;
		gbc_btnMatrizDeIncidncia.gridy = 3;
		add(btnMatrizDeIncidncia, gbc_btnMatrizDeIncidncia);

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
	
	public void setPetrinet(Petrinet petrinet) {
		this.petrinet = petrinet;
		setBlockingStates(petrinet.hasDeadlock());
		//TODO definir setConservability
		//TODO definir setNonLimitedStates
		
	}
}
