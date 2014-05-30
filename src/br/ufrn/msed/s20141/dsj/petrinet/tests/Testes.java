package br.ufrn.msed.s20141.dsj.petrinet.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;
import br.ufrn.msed.s20141.dsj.petrinet.util.MarkupProcessor;

public class Testes {
	private static final String nl = "\n";
	private Petrinet petrinet1;
	private Petrinet petrinet2;
	
	@Before
    public void setup(){
		try {
			petrinet1 = new MarkupProcessor(this.getNet1()).getPetrinet();
			petrinet2 = new MarkupProcessor(this.getNet1()).getPetrinet();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	@Test
	public void testnet1_valida_disparos() {
		// Valida estado inicial
		assertArrayEquals(new double [] {2.0, 0.0, 0.0, 0.0}, petrinet1.stateVector(),0);
		Transition t = petrinet1.getTransition("t1");
		t.fire();
		assertArrayEquals(new double [] {1.0, 1.0, 1.0, 0.0}, petrinet1.stateVector(),0);		
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 2.0, 0.0}, petrinet1.stateVector(),0);
		assertEquals(t.canFire(), false);
		
		t = petrinet1.getTransition("t2");
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 1.0, 1.0}, petrinet1.stateVector(),0);
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 0.0, 2.0}, petrinet1.stateVector(),0);		
		
	}
	@Test
	public void testnet1_ha_estado_bloqueante() {
		assertEquals(true, petrinet1.hasDeadlock());
	}
	@Test
	public void testnet2_ha_estado_bloqueante() {
		assertEquals(true, petrinet2.hasDeadlock());
	}
	@Test
	public void testnet1_valida_transicoes_ativas() {
		Transition t = petrinet1.getTransition("t1");
		arrayEquals(new boolean [] {true, false, false}, petrinet1.getEnabledTransitions());
		t.fire();
		arrayEquals(new boolean [] {true, true, false}, petrinet1.getEnabledTransitions());	
		t.fire();
		arrayEquals(new boolean [] {false, true, false}, petrinet1.getEnabledTransitions());	
		assertEquals(t.canFire(), false);
		
		t = petrinet1.getTransition("t2");
		t.fire();
		arrayEquals(new boolean [] {false, true, false}, petrinet1.getEnabledTransitions());	
		t.fire();
		arrayEquals(new boolean [] {false, false, false}, petrinet1.getEnabledTransitions());			
		
	}
	private void arrayEquals(boolean [] expecteds, boolean [] actuals) {
		for (int i = 0; i < actuals.length; i++) {
			if (expecteds[i] != actuals[i])
				assertEquals(true, false);
		}
		assertEquals(true, true);
	}	
	private String getNet1() {
        StringBuilder sb = new StringBuilder("net Minha rede ");
        sb.append(nl);
        sb.append("p p1 2").append(nl);
        sb.append("p p2").append(nl);
        sb.append("p p3").append(nl);
        sb.append("p p4").append(nl);
        sb.append(nl);
        sb.append("t t1").append(nl);
        sb.append("t t2").append(nl);
        sb.append("t t3").append(nl);
        sb.append(nl);
        sb.append("a p1 t1").append(nl);
        sb.append("a p1 t3").append(nl);
        sb.append("a t1 p2").append(nl);
        sb.append("a t1 p3").append(nl);
        sb.append("a p2 t2").append(nl);
        sb.append("a t2 p2").append(nl);
        sb.append("a p3 t2").append(nl);
        sb.append("a p3 t3").append(nl);
        sb.append("a t2 p4").append(nl);
        sb.append("a p4 t3	").append(nl);	
        return sb.toString();
	}
	private String getNet2() {
		
        StringBuilder sb = new StringBuilder("Exemplo Motivação");
        sb.append(nl);
        sb.append("p p1 1").append(nl);
        sb.append("p p2").append(nl);
        sb.append("p p3").append(nl);
        sb.append("p p4").append(nl);
        sb.append(nl);
        sb.append("t t1").append(nl);
        sb.append("t t2").append(nl);
        sb.append("t t3").append(nl);
        sb.append(nl);
        sb.append("a p1 t1").append(nl);
        sb.append("a t1 p3").append(nl);
        sb.append("a t1 p2").append(nl);
        sb.append("a p3 t3").append(nl);
        sb.append("a t3 p3").append(nl);
        sb.append("a p2 t3").append(nl);
        sb.append("a p2 t2").append(nl);
        sb.append("a t2 p1").append(nl);
        sb.append("a t3 p4").append(nl);
        return sb.toString();
	}
	
}
