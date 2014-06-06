package br.ufrn.msed.s20141.dsj.petrinet.util;

import java.util.ArrayList;
import java.util.List;

public class FourierMotzkin {


//	public int[][] elimination(final int[][] A, int[] b,int m){
//		int[][] Ab=MatrixUtils.columnMerge(A, b);
//		return elimination(Ab,m);
//	}

	public double[][] elimination(final double[][] Ab,int m) {
		if(Ab.length==0) return null;//MatrixUtils.ZeroMatrix;
		List<double[]> list=new ArrayList<double[]>();
		for(int i=0;i<Ab.length;i++){
			if(Ab[i][m]==0){
				double[] inequal=new double[Ab[0].length-1];
				for(int k=0,k2=0;k<inequal.length;k2++){
					if(k2==m) continue;
					inequal[k]=Ab[i][k2];
					k++;
				}
				list.add(inequal);//原系统中不涉及到m的约束
			}
		}
		for(int i=0;i<Ab.length-1;i++){
			if(Ab[i][m]==0) continue;
			boolean flag=Ab[i][m]>0;
			for(int j=i+1;j<Ab.length;j++){
				if(Ab[j][m]==0) continue;
				boolean flag2=Ab[j][m]>0;
				if(flag&&!flag2){
					doElimination(Ab, m, list, i, j);
				}else if(!flag&&flag2){
					doElimination(Ab, m, list, j, i);
				}
			}
		}
		double[][] ret=new double[list.size()][Ab[0].length-1];
		for(int i=0;i<ret.length;i++) ret[i]=list.get(i);
		return ret;
	}

	private void doElimination(double[][] Ab, int m, List<double[]> list, int i, int j) {
		double[] ineuqal=new double[Ab[0].length-1];
		boolean allZero=true;//系数ai全部为0
		double c1=Ab[i][m];
		double c2=Ab[j][m];
		double gcd=this.gcd(c1, c2);
		if(gcd>1){
			c1/=gcd;c2/=gcd;
		}
		for(int k=0,k2=0;k<ineuqal.length;k2++){
			if(k2==m) continue;
			ineuqal[k]=c1*Ab[j][k2]-c2*Ab[i][k2];
			if(k<ineuqal.length-1) allZero&=(ineuqal[k]==0);
			k++;
		}
		if(allZero){
			if(ineuqal[ineuqal.length-1]<0) throw new IllegalArgumentException("NoSolutionException");//b<0,条件不能满足
		}else{
			list.add(ineuqal);
		}
	}
	private double gcd(double a, double b) {
		if (b==0) return a;
		return gcd(b,a%b);
	}
	public static void printMatrix(double[][] matrix) {
		int m = matrix.length;
		int n = matrix[0].length;

		for (int t = 0; t < m; t++) {
			for (int a = 0; a < n; a++) {
				System.out.print(matrix[t][a]+"\t");
			}
			System.out.println();
		}
	}

}
