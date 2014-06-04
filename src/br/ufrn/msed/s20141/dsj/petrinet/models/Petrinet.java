package br.ufrn.msed.s20141.dsj.petrinet.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufrn.msed.s20141.dsj.petrinet.models.Arc.Direction;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Petrinet
extends PetrinetObject {

    private static final String nl = "\n";
    StateTree state = null;
    List<Place> places              = new ArrayList<Place>();
    List<Transition> transitions    = new ArrayList<Transition>();
    List<Arc> arcs                  = new ArrayList<Arc>();
    List<InhibitorArc> inhibitors   = new ArrayList<InhibitorArc>();
    
    public Petrinet(String name) {
        super(name);
    }
    
    public Petrinet() {
    	super(null);
    }

    public void add(PetrinetObject o) {
        if (o instanceof InhibitorArc) {
            inhibitors.add((InhibitorArc) o);
        } else if (o instanceof Arc) {
            arcs.add((Arc) o);
        } else if (o instanceof Place) {
            places.add((Place) o);
        } else if (o instanceof Transition) {
            transitions.add((Transition) o);
        }
    }
    
    public List<Transition> getTransitionsAbleToFire() {
        ArrayList<Transition> list = new ArrayList<Transition>();
        for (Transition t : transitions) {
            if (t.canFire()) {
                list.add(t);
            }
        }
        return list;
    }
    
    public Transition transition(String name) {
        Transition t = new Transition(name);
        transitions.add(t);
        return t;
    }
    
    public Place place(String name) {
        Place p = new Place(name);
        places.add(p);
        return p;
    }
    
    public Place place(String name, int initial) {
        Place p = new Place(name, initial);
        places.add(p);
        return p;
    }
    
    public Arc arc(String name, Place p, Transition t) {
        Arc arc = new Arc(name, p, t);
        arcs.add(arc);
        return arc;
    }
    
    public Arc arc(String name, Transition t, Place p) {
        Arc arc = new Arc(name, t, p);
        arcs.add(arc);
        return arc;
    }
    
    public Arc arc(Place p, Transition t) {
        Arc arc = new Arc("", p, t);
        arcs.add(arc);
        return arc;
    }
    
    public Arc arc(Transition t, Place p) {
        Arc arc = new Arc("", t, p);
        arcs.add(arc);
        return arc;
    }
    
    public InhibitorArc inhibitor(String name, Place p, Transition t) {
        InhibitorArc i = new InhibitorArc(name, p, t);
        inhibitors.add(i);
        return i;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Petrinet ");
        sb.append(super.toString()).append(nl);
        sb.append("---Transitions---").append(nl);
        for (Transition t : transitions) {
            sb.append(t).append(nl);
        }
        sb.append("---Places---").append(nl);
        for (Place p : places) {
            sb.append(p).append(nl);
        }
        return sb.toString();
    }

	public Place getPlace(String name) {
		for (Place p : places) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return this.place(name);
	}
	
    public List<Place> getPlaces() {
        return places;
    }

	public Transition getTransition(String name) {
		for (Transition t : transitions) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return this.transition(name);
	}
	
    public List<Transition> getTransitions() {
        return transitions;
    }

	public Arc getArc(String name) {
		for (Arc a : arcs) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}

    public List<Arc> getArcs() {
        return arcs;
    }
    public boolean hasPlaceReachable(double[] states) {
		List<Node> lista = getStateTree().getNodeList();
		for (Node node : lista) {
//			System.out.println(node);
			if (Arrays.equals(states, node.currentState))
				return true;
		}
    	return false;
    }
    public boolean getTransitions(double[] states) {
		List<Node> lista = getStateTree().getNodeList();
		for (Node node : lista) {
		}
    	return false;
    }
    public boolean hasDeadlock() {
    	if (!this.getStateTree().getBlockingStates().isEmpty())
    		return true;
    	return false;
    }
    /**
     * Uma condição necessária e suficiente para que uma 
     * rede de Petri seja limitada é que o símbolo w
     * nunca aparece em uma árvore de cobertura.
     * @return
     */
    public boolean hasBounded() {
		List<Node> lista = getStateTree().getNodeList();
		for (Node node : lista) {
			if (node.hasTokenUnbounded())
				return false;
		}
    	return true;
    }
    public boolean existTransition(String name){
    	for (Transition t : transitions) {
            if (t.getName().equalsIgnoreCase(name))
            	return true;
        }
    	return false;
    }
    
    public boolean existPlace(String name){
    	for (Place t : places) {
            if (t.getName().equalsIgnoreCase(name))
            	return true;
        }
    	return false;
    }

    public List<InhibitorArc> getInhibitors() {
        return inhibitors;
    }
    
    public DirectedSparseMultigraph<PetrinetObject,Arc> getGraphicRepresentation(){
    	DirectedSparseMultigraph<PetrinetObject,Arc> graph = new DirectedSparseMultigraph<PetrinetObject,Arc>();
    	
    	for (Place place : places) {
			graph.addVertex(place);
		}
    	
    	for (Arc arc : arcs) {
    		if (arc.direction == Direction.PLACE_TO_TRANSITION)
    			graph.addEdge(arc, arc.getPlace(), arc.getTransition(), EdgeType.DIRECTED);
    		else
    			graph.addEdge(arc, arc.getTransition(), arc.getPlace(), EdgeType.DIRECTED);
		}
    	
    	return graph;
    }
    
    public double[][] incidenceMatrix(){
    	int maxT = transitions.size();
    	int maxP = places.size();
    	
    	double[][] matrix = new double[maxT][maxP];
    	
    	for (Transition t : transitions) {
    		int tIndex = transitions.indexOf(t);
			for (Arc a : t.getIncoming()) {
				int pIndex = places.indexOf(a.place);
				matrix[tIndex][pIndex] -= a.getWeight();
			}
			for (Arc a : t.getOutgoing()) {
				int pIndex = places.indexOf(a.place);
				matrix[tIndex][pIndex] += a.getWeight();
			}
		}
    	
    	return matrix;
    }
    
    public void printIncidenceMatrix(){
    	double[][] matrix = incidenceMatrix();
    	
    	int maxT = transitions.size();
    	int maxP = places.size();
    	
    	for (int i = 0; i < maxT; i++) {
			for (int j = 0; j < maxP; j++) {
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
    }
    public boolean[] getEnabledTransitions() {
    	int maxT = this.transitions.size();
    	boolean[] enabled = new boolean[maxT];
    	int i=0;
		for (Transition t : transitions) {
			enabled[i] = t.canFire();
			i++;
		}    	
		return enabled;
    }
    public double[] stateVector(){
    	double[] vector = new double[places.size()];
    	
    	for (int i = 0; i < vector.length; i++) {
			vector[i] = places.get(i).getTokens();
		}
    	
    	return vector;
    }
    
    public void printStateVector(){
    	double[] vector = stateVector();

    	for (int i = 0; i < vector.length; i++) {
			System.out.print(vector[i]+"\t");
		}
    	System.out.println();
    }
    public StateTree getStateTree() {
    	if (this.state == null)
    		this.state = new StateTree(this);
    	return this.state;
    }
    
}
