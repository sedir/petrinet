package br.ufrn.msed.s20141.dsj.petrinet.models;

import java.util.ArrayList;
import java.util.List;

public class Transition
extends PetrinetObject{

    protected Transition(String name) {
        super(name);
    }

    private List<Arc> incoming = new ArrayList<Arc>();
    private List<Arc> outgoing = new ArrayList<Arc>();
    
    /**
     * @return true, caso possa ser disparado
     */
    public boolean canFire() {
        boolean canFire = true;
        
        canFire = ! this.isNotConnected();
        
        for (Arc arc : incoming) {
            canFire = canFire & arc.canFire();
        }
        
        for (Arc arc : outgoing) {
            canFire = canFire & arc.canFire();
        }
        return canFire;
    }
    
    public void fire() {
        for (Arc arc : incoming) {
            arc.fire();
        }
        
        for (Arc arc : outgoing) {
            arc.fire();
        }
    }
    
    public List<Arc> getIncoming() {
		return incoming;
	}
    
    public List<Arc> getOutgoing() {
		return outgoing;
	}
    
    public void addIncoming(Arc arc) {
        this.incoming.add(arc);
    }
    
    public void addOutgoing(Arc arc) {
        this.outgoing.add(arc);
    }

    public boolean isNotConnected() {
        return incoming.isEmpty() && outgoing.isEmpty();
    }
    
    @Override
    public String toString() {
        return super.toString() + 
               (isNotConnected() ? " Desconexo" : "" );
//               (canFire()? " Disparável" : "");
    }
    
}
