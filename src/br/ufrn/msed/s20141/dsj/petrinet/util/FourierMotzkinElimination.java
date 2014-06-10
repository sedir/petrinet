package br.ufrn.msed.s20141.dsj.petrinet.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

import Jama.Matrix;
/**
 * 
 * @author Jailton Carlos
 * Baseado no artigo  Models and Languages for Computational Systems Biology – Lecture 15
 * Structural Analysis of Petri Nets 2 
 * Jane Hillston (LFCS and CSBE, University of Edinburgh)
 * 7th March 2011 - with thanks to Stephen Gilmore and Peter Kemper
 */
public class FourierMotzkinElimination {
	private static final String nl = "\n";
	private List<double[]> listD=new ArrayList<double[]>();
	private int m_places;
	private int n_transitions;
	/**
	 * incidenceMatrix é a matriz de incidência com m linhas de lugares(places)
	 * e n colunas de transições (transitions).
	 * @param incidenceMatrix 
	 */
	public FourierMotzkinElimination(double[][] incidenceMatrix)  {
		int m = incidenceMatrix.length; //numbers of places 
		int n = incidenceMatrix[0].length;// numbers of transitions
		this.m_places = m;
		this.n_transitions = n;
		double[][] I = Matrix.identity(m,m).getArrayCopy();

		this.columnMerge(incidenceMatrix, I);
		System.out.printf("Matriz D %dx%d\n", m,n+m);
		System.out.println(this);

		for (int i = 0; i < n; i++) {
			System.out.printf("Columns %s :",i+1);
			for (int l1 = 0; l1 < m-1; l1++) {
				for (int l2 = l1+1; l2 < m; l2++) {
					if (listD.size() > l2) {
						//						System.out.println("l1["+l1+"]|l2["+l2+"]");
						double d1[] = listD.get(l1);
						double d2[] = listD.get(l2);

						//tem sinais oposto
						if (this.oppositeSigns(d1[i], d2[i])) {
							System.out.printf("rows %s+%s | values %s e %s", l1+1, l2+1, d1[i], d2[i]);
							double d[] = new double[m+n];
							double dl[] = new double[m+n];
							double gcd=0;
							if (l1==3 && l2==4)
								System.out.println("Pare aqui.");
							//calcula d := |d2(i)| * d1 + |d1(i)| * d2 ; (∗d(i) = 0∗)
							for (int k=0; k < (n+m); k++) {
								d[k] = Math.abs(d2[i]) * d1[k] 
										+ Math.abs(d1[i]) * d2[k];
								//								gcd =this.gcd(d1[k],d2[k]);
								//								dl[k] = d[k]/ gcd;
								//								if (dl[k]==-0)
								//									dl[k] = 0;
								gcd =Math.abs(this.gcd(d[k],gcd));
							}
							//d' := d /gcd (d(1), d(2), ... , d(m + n));
							//Não ficou muito claro para mim o cáculo do MDC
							//entendi que o array d será divido pelo mdc resultante de toda a coluna dp array d.

							for (int k = 0; k < (n+m); k++) {
								dl[k] = d[k]/ gcd;
								//gcd =this.gcd(d[k],gcd);
							}
							listD.add(dl);
							System.out.println(this);
						}	
					}
				}
			}

			//delete all rows of the (augmented) matrix Di −1 whose i-th component
			//is different from 0, the result is Di ;

			Iterator<double[]> iter = listD.iterator();
			while (iter.hasNext()) {
				if (iter.next()[i] !=0)
					iter.remove();
			}
			System.out.println("(purge)");
			System.out.println(this);			
		}


	}
	public double[] getInvariants() {
		//		A place vector is called a P-invariant if it is a nontrivial nonnegative
		//		integer solution of the linear equation system x · C = 0.
		int n = this.m_places;
		int m = this.n_transitions;
		double pInvariantes[] = new double[n];

		for (int i = 0; i < listD.size(); i++) {
			for (int j = 0; j < n; j++) {
				if(listD.get(i)[m+j] >0) {
					pInvariantes[j]=listD.get(i)[m+j];
				}
			}			
		}
		return pInvariantes;
	}
	public boolean isConservative() {
		double[] invariantes = this.getInvariants();
		for (int i = 0; i < invariantes.length; i++) {
			if (invariantes[i]<=0)
				return false;
		}
		return true;
	}
	//preeche a lista listD com os itens da matriz C e I
	private void columnMerge(double[][] C, double[][] I){	
		int m = I.length; //num of rows
		int n = C[0].length; //num of columns
		for (int i = 0; i < m; i++) {
			double d[] = new double [m+n];
			for (int j = 0; j < (m+n); j++) {
				if (j < n) 
					d[j] = C[i][j];
				else 
					d[j] = I[i][j-n];
			}
			listD.add(d);
		}	
		//		SimpleMatrix I = SimpleMatrix.identity(5);
		//		SimpleMatrix C = new SimpleMatrix(C_);
		//		SimpleMatrix r = C.combine(0, C.numRows()-1, I);
	}
	private boolean oppositeSigns(double x, double y)
	{
		if (x>=0 && y>=0)
			return false;
		if (x<=0 && y<=0 )
			return false;
		return true;
	}
	//calcula o MDC
	private double gcd(double a, double b) {
		if (b==0)
			if (a==0)
				return 1;
			else
				return a;
		return gcd(b,a%b);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\nFourier-Motzkin: ");
		sb.append(nl);
		if (listD.size() >0) {
			int n = listD.get(0).length;
			for (double[] ds : listD) {
				for (int i = 0; i < n; i++) {
					sb.append(ds[i]+"\t");
				}		
				sb.append(nl);
			}
		}
		return sb.toString();
	} 

}
