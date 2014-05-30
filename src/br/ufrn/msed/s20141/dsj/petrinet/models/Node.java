
package br.ufrn.msed.s20141.dsj.petrinet.models;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

public class Node {
	List<Node> children;
	Node parent;
	StateTree tree;
	boolean w;
	boolean dominant;
	boolean duplicated;
	boolean blocking;
	double[] currentState;
	int transition = -1;
	Petrinet net;
	static int gid = 1;
	int id;

	public Node(Petrinet net, StateTree tree) {
		this.tree = tree;
		this.children = new ArrayList<Node>();
		this.net = net;
	}

	public Node(Petrinet net, double[] actualState, StateTree tree) {
		this.tree = tree;
		this.children = new ArrayList<Node>();
		this.currentState = actualState;
		this.net = net;
	}

	public Node(Petrinet net, double[] actualState, int transition, StateTree tree) {
		this.tree = tree;
		this.children = new ArrayList<Node>();
		this.currentState = actualState;
		this.net = net;
		this.transition = transition;
	}

	public Node(Petrinet net, double[] actualState, boolean duplicated, StateTree tree) {
		this.tree = tree;
		this.children = new ArrayList<Node>();
		this.currentState = actualState;
		this.duplicated = duplicated;
		if (!duplicated)
			checkAndExpand();
	}

	public Node(Petrinet net, double[] actualState, boolean duplicated, int transition, StateTree tree) {
		this.tree = tree;
		this.children = new ArrayList<Node>();
		this.currentState = actualState;
		this.transition = transition;
		this.duplicated = duplicated;

		if (!duplicated)
			checkAndExpand();
	}

	public void checkAndExpand(){
		double[][] incMat = this.net.incidenceMatrix();
		Matrix A = new Matrix(incMat);
		Matrix x0 = new Matrix(this.currentState,1);
		int[] possibleFires = new int[incMat.length];
		for (int i = 0; i < incMat.length; i++) {
			boolean ok = true;
			for (int j = 0; j < incMat[0].length; j++) {
				if (incMat[i][j] < 0){
					double val = incMat[i][j]+currentState[j];
					if (val < 0){
						ok = false;
						break;
					}
				}
			}
			possibleFires[i] = ok?1:0;
		}
		boolean duplicate = getRoot().searchDuplicates(this);
		if (!duplicate){
			for (int i = 0; i < possibleFires.length; i++) {
				if (possibleFires[i]==1){
					double[] disparo = new double[possibleFires.length];
					for (int j = 0; j < possibleFires.length; j++) {
						disparo[j] = i==j?1:0;
					}
					Matrix disp = new Matrix(disparo,1);
					Matrix b = disp.times(A);
					double[] x0temp = this.currentState.clone();
					Matrix x = x0.plus(b);
					double[] estadoFilho = x.getRowPackedCopy();
					for (int j = 0; j < currentState.length; j++) {
						if (x0temp[j] < 0){
							estadoFilho[j] = -1;
						}
					}
					Node newChild = new Node(net, estadoFilho,i,tree);
					addChild(newChild);
					newChild.checkDominance();
					newChild.checkAndExpand();
				}
			}
		}else{
			this.duplicated = true;
		}
	}

	public boolean searchDuplicates(Node node){
		if (!this.equals(node) && this.compareDuplicate(node))
			return true;
		for (Node child : children) {
			return child.searchDuplicates(node);
		}
		return false;
	}


	public void searchBlockingStates(List<Node> nodes){
		if ((this.w || this.duplicated))
			return;
		else if (this.children.isEmpty()){
			this.blocking = true;
			nodes.add(this);
		}
		else
			for (Node child : children) {
				child.searchBlockingStates(nodes);
			}
	}

	public void recursiveListing(List<Node> nodes){
		nodes.add(this);
		for (Node child : children) {
			child.recursiveListing(nodes);
		}
	}
	
	public Node getRoot(){
		return tree.root;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public boolean isBlocking(){
		return blocking;
	}

	public void addChild(Node node){
		this.children.add(node);
		node.parent = this;
		node.net = this.net;
	}

	public void removeChild(Node node){
		this.children.remove(node);
		node.parent = null;
	}

	public boolean isDominant() {
		return dominant;
	}

	public boolean isDuplicated() {
		return duplicated;
	}

	public boolean isW() {
		return w;
	}

	public double[] getActualState() {
		return currentState;
	}

	public void setDominant(boolean dominant) {
		this.dominant = dominant;
	}

	public void setDuplicated(boolean duplicated) {
		this.duplicated = duplicated;
	}

	public void setW(boolean w) {
		this.w = w;
	}

	public void setActualState(double[] actualState) {
		this.currentState = actualState;
		checkAndExpand();
	}

	public void setNet(Petrinet net) {
		this.net = net;
	}

	public Petrinet getNet() {
		return net;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	public Node getParent() {
		return parent;
	}

	public boolean compareDuplicate(Node t) {
		boolean igual = true;
		for (int i = 0; i < currentState.length; i++) {
			if (this.currentState[i]!=t.currentState[i]){
				igual = false;
				break;
			}
		}

		return igual;
	}

	public void printActualState(){
		System.out.print("[ ");
		for (double d : currentState) {
			System.out.print(d+" ");
		}
		System.out.print("] ");

	}

	public void checkDominance() {
		Node parent = this;

		while( (parent = parent.parent)!=null ){
			int i;
			for (i = 0; i < currentState.length; i++) {
				if (currentState[i] < parent.currentState[i] )
					break;

			}
			if (i == currentState.length){
				for (i = 0; i < currentState.length; i++) {
					if (currentState[i] > parent.currentState[i] )
						// adicionar "w".
						currentState[i] = -1;
				}
			}
		}
	}

	public double[] fireTransition(double[] trigger) {
		Matrix A = new Matrix(net.incidenceMatrix());
		Matrix x0 = new Matrix(this.currentState, 1);

		Matrix disp = new Matrix(trigger, 1);
		Matrix b = disp.times(A);
		double[] x0temp = this.currentState.clone();
		Matrix x = x0.plus(b);
		double[] estadoFilho = x.getRowPackedCopy();
		for (int j = 0; j < currentState.length; j++) {
			if (x0temp[j] < 0) {
				estadoFilho[j] = -1;
			}
		}
		return estadoFilho;
	}
	
	public boolean canFireTransition(double[] trigger){
		int count = 0;
		int transition = -1;
		for (int i=0 ; i<trigger.length ; i++){
			if (trigger[i]==1){
				count++;
				transition = i;
			}
			
		}
		if (count!=1)
			return false;
		
		for (Node node : children) {
			if (node.transition==transition)
				return true;
		}
		
		return false;
	}

	public boolean canReach(Node node){
		if (this.equalState(node))
			return true;
		for (Node child : children) {
			return child.canReach(node);
		}
		return false;
	}
	
	public boolean equalState(double[] state){
		boolean equal = true;
		if (state.length!=currentState.length)
			return false;
		for (int i=0 ; i<state.length ; i++) {
			if (state[i]!=currentState[i])
				equal = false;
		}
		return equal;
	}
	
	public boolean equalState(Node node){
		return equalState(node.currentState);
	}
	/**
	 * Usado para indicar que há um estado com 
	 * marcação ilimitada, isto é, com o símobo w.
	 * @return
	 */
	public boolean hasTokenUnbounded() {
		for (double d : currentState) {
			if (d==-1)
				return true;
		} 		
		return false;
	}
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(transition>=0?"t"+(transition+1)+" ":"");
		strb.append("[ ");
		for (double d : currentState) {
			if (d==-1)
				strb.append("w, ");	
			else
				strb.append(((int)(d))+", ");
		} 
		strb.deleteCharAt(strb.length()-2);
		strb.append("] ");
		
		return strb.toString();
	}
}
