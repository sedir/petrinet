package br.ufrn.msed.s20141.dsj.petrinet.models;


public class Arc
extends PetrinetObject {

    Place place;
    Transition transition;
    Direction direction;
    int weight = 1;
    
    enum Direction {

        
        PLACE_TO_TRANSITION {
            @Override
            public boolean canFire(Place p, int weight) {
                return p.hasAtLeastTokens(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.removeTokens(weight);
            }

        },
        
        TRANSITION_TO_PLACE {
            @Override
            public boolean canFire(Place p, int weight) {
                return ! p.maxTokensReached(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.addTokens(weight);
            }

        };

        public abstract boolean canFire(Place p, int weight);

        public abstract void fire(Place p, int weight);
    }
    
    private Arc(String name, Direction d, Place p, Transition t,int weight) {
        super(name);
        this.direction = d;
        this.place = p;
        this.transition = t;
        if (weight>0)
        	this.weight = weight;
    }
    protected Arc(String name, Place p, Transition t) {
        this(name, Direction.PLACE_TO_TRANSITION, p, t,1);
        t.addIncoming(this);
    }  
    protected Arc(String name, Place p, Transition t,int weight) {
        this(name, Direction.PLACE_TO_TRANSITION, p, t,weight);
        t.addIncoming(this);
    }
    protected Arc(String name, Transition t, Place p) {
        this(name, Direction.TRANSITION_TO_PLACE, p, t,1);
        t.addOutgoing(this);
    }
    protected Arc(String name, Transition t, Place p,int weight) {
        this(name, Direction.TRANSITION_TO_PLACE, p, t,weight);
        t.addOutgoing(this);
    }

    public boolean canFire() {
        return direction.canFire(place, weight);
    }
    
    public void fire() {
        this.direction.fire(place, this.weight);
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public int getWeight() {
        return weight;
    }

	public Place getPlace()
	{
		return place;
	}
	
	public Transition getTransition()
	{
		return transition;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getWeight()!=1?getWeight()+"":"";
	}
}
