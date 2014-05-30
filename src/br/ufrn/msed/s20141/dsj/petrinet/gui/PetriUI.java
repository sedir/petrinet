package br.ufrn.msed.s20141.dsj.petrinet.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.util.MarkupProcessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class PetriUI extends JApplet implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7321886236679765344L;
	private JPanel contentPane;
	private Petrinet petrinet;
	private JPanel visualizationPane;
	private JTextArea scriptTextPane;
	private JPanel southPane;
	private JButton runButton;
	private JButton loadButton;
	private JButton saveButton;
	private JButton saveTreeImageButton;
	private JButton saveNetImageButton;
	private NetVisualization net;
	private TreeVisualization tree;
	private StatusPane statusPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PetriUI ui = new PetriUI();
					JFrame frame = new JFrame("SSRP PetriNet");
					frame.add(ui);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PetriUI() {
		//		petrinet = new MarkupProcessor(new File("rede.txt")).getPetrinet();

		// Cria Petrinet vazia
		petrinet = new Petrinet();

		// Cria painel geral, definindo tamanhos
		setBounds(50, 50, 1000, 500);
		contentPane = new JPanel();
		visualizationPane = new JPanel(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// Cria e encaixa as visualizacoes (Rede de Petri e Arvore de Cobertura)
		contentPane.add(visualizationPane, BorderLayout.CENTER);
		visualizationPane.add(net = new NetVisualization(petrinet),BorderLayout.WEST);
		visualizationPane.add(tree = new TreeVisualization(petrinet),BorderLayout.CENTER);

		statusPane = new StatusPane();
		statusPane.reset();

		// Cria painel de componentes de controle no rodape (SOUTH)
		southPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;


		// Define os controles e liga-os com listeners
		runButton = new JButton("Carregar rede", new ImageIcon(this.getClass().getResource("images/load.png")));
		runButton.addActionListener(this);
		loadButton = new JButton("Abrir arquivo", new ImageIcon(this.getClass().getResource("images/open.png")));
		loadButton.addActionListener(this);
		saveButton = new JButton("Salvar arquivo", new ImageIcon(this.getClass().getResource("images/save.png")));
		saveButton.addActionListener(this);
		saveNetImageButton = new JButton("Exportar Rede de Petri", new ImageIcon(this.getClass().getResource("images/export.png")));
		saveNetImageButton.addActionListener(this);
		saveTreeImageButton = new JButton("Exportar Arvore de Cobertura", new ImageIcon(this.getClass().getResource("images/export.png")));
		saveTreeImageButton.addActionListener(this);

		scriptTextPane = new JTextArea();
		scriptTextPane.setWrapStyleWord(true);
		scriptTextPane.setLineWrap(true);      

		JScrollPane scriptScroller = new JScrollPane();   
		scriptScroller.setPreferredSize(new Dimension(scriptScroller.getWidth(),150));
		scriptScroller.setBorder(
				BorderFactory.createTitledBorder("PetriMarkup"));
		scriptScroller.setViewportView(scriptTextPane);



		// Define posicionamento dos controles
		southPane.setPreferredSize(new Dimension(southPane.getWidth(), 150));


		c.gridheight = 1;
		c.weightx = 0.01;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.20;
		southPane.add(loadButton, c);
		c.gridx = 0;
		c.gridy = 1;
		southPane.add(saveButton, c);
		c.weighty = 0.60;
		c.gridx = 0;
		c.gridy = 2;
		southPane.add(runButton, c);


		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.98;
		c.gridheight = 3;
		southPane.add(scriptScroller, c);


		c.weightx = 0.01;
		c.gridheight = 3;
		c.gridx = 2;
		c.gridy = 0;
		southPane.add(statusPane, c);

		c.gridheight = 2;
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 0.01;
		c.weighty = 0.01;
		southPane.add(saveNetImageButton, c);
		c.weighty = 0.99;
		c.gridy = 2;
		southPane.add(saveTreeImageButton, c);


		contentPane.add(southPane,BorderLayout.SOUTH);
		this.invalidate();
		this.validate();
	}

	// Listener dos botoes
	@Override
	public void actionPerformed(ActionEvent e) {

		// Botao Abrir Arquivo
		if (e.getSource() == loadButton){
			Frame parent = new Frame();
			FileDialog fd = new FileDialog(parent, "Escolha um arquivo de texto PetriMark",
					FileDialog.LOAD);
			fd.setVisible(true);

			File selectedItem = fd.getFiles()[0];

			if (selectedItem != null)
				try {
					MarkupProcessor mp = new MarkupProcessor(selectedItem);
					scriptTextPane.setText(mp.getScript());
					setPetrinet(mp.getPetrinet());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				}


		} 
		// Botao Carregar Rede
		else if (e.getSource() == runButton){
			try {
				setPetrinet(new MarkupProcessor(scriptTextPane.getText()).getPetrinet());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);

			}
		}
		// Botao Salvar Arquivo
		else if (e.getSource() == saveButton){
			Frame parent = new Frame();
			FileDialog fd = new FileDialog(parent, "Salvar arquivo PetriMark",
					FileDialog.SAVE);
			fd.setVisible(true);

			File selectedItem = fd.getFiles()[0];

			if (selectedItem != null)
				try {
					selectedItem.delete();	
					PrintWriter out = new PrintWriter(selectedItem);
					out.println(scriptTextPane.getText());
					out.close();
					JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				}
		}
		else if (e.getSource() == saveNetImageButton){
			Frame parent = new Frame();
			FileDialog fd = new FileDialog(parent, "Salvar imagem da Rede de Petri",
					FileDialog.SAVE);
			fd.setVisible(true);

			File selectedItem = fd.getFiles()[0];

			if (selectedItem != null)
				try {
					writeJPEGImage(selectedItem, true);
					JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				}
		}
		else if (e.getSource() == saveTreeImageButton){
			Frame parent = new Frame();
			FileDialog fd = new FileDialog(parent, "Salvar imagem da Arvore de Cobertura",
					FileDialog.SAVE);
			fd.setVisible(true);

			File selectedItem = fd.getFiles()[0];

			if (selectedItem != null)
				try {
					writeJPEGImage(selectedItem, false);
					JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				}
		}




	}
	
	private void setPetrinet(Petrinet petrinet){
		this.petrinet = petrinet;
		visualizationPane.removeAll();
		visualizationPane.add(net = new NetVisualization(petrinet),BorderLayout.WEST);
		visualizationPane.add(tree = new TreeVisualization(petrinet),BorderLayout.CENTER);
		statusPane.setPetrinet(petrinet);
		this.invalidate();
		this.validate();
	}

	public void writeJPEGImage(File file, boolean isNet) {
		VisualizationViewer vv;

		if (isNet)
			vv = this.net.getViewer();
		else
			vv = this.tree.getViewer();

		int width = vv.getWidth();
		int height = vv.getHeight();

		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		vv.paint(graphics);
		graphics.dispose();

		try {
			ImageIO.write(bi, "jpeg", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
