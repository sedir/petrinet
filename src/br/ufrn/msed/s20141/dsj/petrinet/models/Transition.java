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
     * Verifica se o evento pode ocorrer. O que habilita uma transição é a existência
     * ou n ão do token nos seus lugares de entrada.
     * @return true, caso possa ser disparado
     *
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
    /**
     * Representa o evento ocorrido na transição.
     * Remove a quantidade de marcação (place.token) nos seus lugares de entrada equivalente
   	 * a quantidade de pesos (arc.weight) nos arcos de entrada para os lugares de saída.
     * @return true, caso o disparo tenha ocorrido
     *
     */
    //inclui o this.canFire, pois poderia ocorrer erro se chamasse o método diretamente.
    public boolean fire() {
    	if (this.canFire()) {
	        for (Arc arc : incoming) {
	            arc.fire();
	        }
	        
	        for (Arc arc : outgoing) {
	            arc.fire();
	        }
	        return true;
    	}
    	return false;
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
