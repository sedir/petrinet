package br.ufrn.msed.s20141.dsj.petrinet.models;

public class PetrinetObject {

    private String name;
    
    public PetrinetObject(String name) {
        super();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
