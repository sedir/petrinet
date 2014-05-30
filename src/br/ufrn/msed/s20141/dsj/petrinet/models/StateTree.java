
package br.ufrn.msed.s20141.dsj.petrinet.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;

public class StateTree {
	Node root;
	Petrinet net;

	public StateTree(Node root) {
		this.root = root;        
	}

	public StateTree(Petrinet net) {
		this.net = net;
		net.state = this;
		this.root = new Node(net, net.stateVector(), this);
		try{
			this.root.checkAndExpand();
		}catch (Exception e){
			e.printStackTrace();
		}
		this.getBlockingStates();
	}

	public StateTree(Petrinet net, double[] initialState){
		this.net = net;
		net.state = this;
		this.root = new Node(net, initialState, this);
		this.root.checkAndExpand();
		this.getBlockingStates();
	}

	public Node getRoot() {
		return root;
	}

	public Petrinet getNet() {
		return net;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public void print(){
		Node node = root;
		root.printActualState();
		while (node.children!=null) {            

		}
	}

	public List<Node> getBlockingStates(){
		List<Node> nodes = new ArrayList<Node>();
		root.searchBlockingStates(nodes);
		return nodes;
	}

	public List<Node> getNodeList(){
		List<Node> nodes = new ArrayList<Node>();
		root.recursiveListing(nodes);
		return nodes;
	}

	public boolean canReach(Node origin, Node destination){
		List<Node> lista = getNodeList();
		List<Node> origens = new ArrayList<Node>();

		for (Node node : lista) {
			if (origin.equalState(node))
				origens.add(node);
		}
		System.out.println("Nos totais: "+lista.size());
		System.out.println("Nos duplicados: "+origens.size());

		boolean reached = false;

		for (Node node : origens) {
			System.out.println(node);
			reached = node.canReach(destination);
			if (reached)
				return true;
		}
		System.out.println();
		System.out.println();
		return false;
	}

	public Forest<Node, Integer> getGraphModelRepresentation(){
		Forest<Node, Integer> graph = new DelegateForest<Node,Integer>();

		Factory<Integer> edgeFactory = new Factory<Integer>() {
			int i=0;
			public Integer create() {
				return i++;
			}};

			graph.addVertex(root);
			for (Node node : getNodeList()) {
				if (node != root){
					graph.addVertex(node);
					graph.addEdge(edgeFactory.create(), node.getParent(), node);
				}
			}



			return graph;
	}
}
