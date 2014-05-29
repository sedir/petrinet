package br.ufrn.msed.s20141.dsj.petrinet.models;

public class InhibitorArc
extends Arc {

    
    protected InhibitorArc(String name, Place p, Transition t) {
        super(name, p, t);
    }

    
    @Override
    public boolean canFire() {
        return (place.getTokens() < this.getWeight());
    }
    
    
    @Override
    public void fire() {
        // do nothing
    }

    
}
