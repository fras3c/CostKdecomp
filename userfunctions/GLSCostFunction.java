package userfunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import parser.DatalogParser;
import solver.HypertreeDecomp;
import struct.HyperEdge;
import struct.HyperGraph;
import util.Mapping;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;

import functions.DomainValue;
import functions.WeightingFunction;

/**
 * @author Francesco creato il 29/lug/2014 18:20:28
 */

public class GLSCostFunction implements WeightingFunction<GLSDV> {

	private HyperGraph hg;
	private Mapping g;
	private int[] joinVarIndexes; // ci sono gli indici delle variabili di join della CHI di questo nodo con il figlio che stiamo considerando
	
	private boolean colorComputation = true;
	private IntDoubleHashMap varColoring = new IntDoubleHashMap();
	
	public GLSCostFunction() {
		
	}
	
	public GLSCostFunction(HyperGraph hg, Mapping g) {
		// TODO Auto-generated constructor stub
		this.hg = hg;
		this.g = g;
		joinVarIndexes = new int[hg.nov()];
	}
	
	@Override
	public DomainValue<GLSDV> gh(IntArrayList chi, IntArrayList lambda, DomainValue<GLSDV>[] values) {
		// TODO Auto-generated method stub
		
		if(colorComputation) {
			computeColorings();
			colorComputation = false;
		}
		
		DomainValue<GLSDV> dv = new DomainValue<>();

//		dv.getT().setLambda(lambda);
		
		double rows = 1;
		
		double[] varsChiColors = new double[chi.size()]; // anche qua per questioni di efficienza potremmo evitare di crearlo e usare un limite
		
		Arrays.fill(varsChiColors, Double.MAX_VALUE);
		
		for(int i = 0; i < chi.size(); i++) {
		
			int var = chi.get(i);
			if(!HypertreeDecomp.completeDecomposition || !DatalogParser.getArtificialVars().contains(var)) {
					double y_i = varColoring.get(var);
					if(y_i < varsChiColors[i])
						varsChiColors[i] = y_i;
			}			
		}
		
		for(int i = 0; i < varsChiColors.length; i++) {
			rows *= Math.pow(2, varsChiColors[i]);
		}
		
//		for (int i = 0; i < hg.getHyperEdges().size(); i++) {
//			HyperEdge c = hg.getHyperEdges().get(i);
//			if (lambda.contains(c.getId())) {
//				for (int j = 0; j < c.getVariables().size(); j++)
//					if (chi.contains(c.getVariables().get(j))) {
//						double y_i = varColoring.get(c.getId()).get(c.getVariables().get(j));
//						cost *= Math.pow(2, y_i);
//				    }
//			}
//		}	
		// 
		
		if (values == null) { // questa risorsa è una foglia
			dv.setT(new GLSDV(rows, rows));
			dv.getT().setChi(chi);
			
			return dv;
		}
		
//		sort(values); // figli ordinati rispetto alla loro cardinalità
			
		double cost = rows; // purtroppo così non teniamo conto della size delle relazioni intermedie
		                    // invece se facciamo la multiway join va bene
		
		  					// da notare che questa stima tiene già conto dell'operazione di semijoin, perchè il
		                    // programma lineare prende in input tutta l'istanza
				
		for(DomainValue<GLSDV> d : values) { // il costo della risorsa dovrebbe essere la somma dei costi dei sotto alberi + il suo agm bound + il costo della semijoin 
			
			//cost+= d.getT().getEstimatedRows() + minCosto + d.toReal(); // d.toReal() = costo del sottoalbero
			
			cost += d.toReal(); // costo del sottoalbero
			
//			cost += rows; // costo scansione padre
			
			int lim = 0;
			
			IntArrayList childChi = d.getT().getChi();
			
			lim = intersect(chi, childChi, lim);
			
			double interfaceSize = 1;
			
			for(int i = 0; i < lim; i++)
				interfaceSize *= Math.pow(2, varsChiColors[joinVarIndexes[i]]);
			
			cost += interfaceSize;
						
//			for(int i = 0; i < childChi.size(); i++) {
//				
//				int var = childChi.get(i);
//				
//				int fatherIndexVar = chi.indexOf(var);
//				
//				if(fatherIndexVar >= 0) {
//					joinVars[lim] = Math.max(varsChiColors[fatherIndexVar], d.getT().getVarsChiColors()[i]); 
//					lim++;
//				}
//			}
//			
////			double temp = rows * d.getT().getEstimatedRows();
////			
////			Arrays.sort(joinVars, 0, lim);
////			
////			for(int j = lim -1; j > 0; j--) // lascio volutamente fuori il color più piccolo
////				temp *= 1/Math.pow(2,joinVars[j]);
////			
////			if(temp < rows)
////				rows = temp;
//			
////			double t = getSelectivities(num_dist, d.getT().getDegrees());
////					
////			double temp = d.getT().getEstimatedRows() * minCosto * t;
////			
////			if(temp < minCosto) {
////				minCosto = temp;
////			}			
		}
			
		dv.setT(new GLSDV(cost, rows));
		dv.getT().setChi(chi);

		return dv;
	}
	
	/**
	 * @param chi
	 * @param childChi
	 * @param lim
	 */
	private int intersect(IntArrayList lista1, IntArrayList lista2, int lim) {
		// TODO Auto-generated method stub
		int i = 0;
		int j = 0;
		while( i < lista1.size() && j < lista2.size()) {				
			if(lista1.get(i) < lista2.get(j))
				i++;
			else if(lista1.get(i) >lista2.get(j))
				j++;
			else {
				joinVarIndexes[lim++] = i;
				i++;
				j++;
			}
		}
		return lim;
	}

	public Mapping getG() {
		return g;
	}

	public void setG(Mapping g) {
		this.g = g;
	}

	/**
	 * @param numDist
	 * @param numDist2
	 * @return
	 */
//	private double getSelectivities(IntDoubleHashMap numDist, IntDoubleHashMap numDist2) {
//		// TODO Auto-generated method stub
//		
//		double selectivity = 1;
//		Iterator<IntDoubleCursor> it = numDist.iterator();
//		
//		while(it.hasNext()) {
//			IntDoubleCursor x = it.next();
//			Iterator<IntDoubleCursor> it2 = numDist2.iterator();
//			int varX = x.key;
//			while(it2.hasNext()) {
//				IntDoubleCursor y = it2.next();
//				int varY = y.key;
//				if(varX == varY) {
//					if(y.value > x.value)
//						numDist.put(varX, y.value);
//					selectivity *= 1/y.value;
//				}
//					
//			}
//		}
//		return selectivity;
//	}

	/**
	 * @param values
	 */
	private void sort(DomainValue<GLSDV>[] values) {
		// TODO Auto-generated method stub
	
			for (int j = 1; j < values.length; j++) {
			
				DomainValue temp = values[j];
			
				int i = j - 1;
				for (; (i >= 0) && ( values[i].getT().getEstimatedRows() > ((GLSDV)temp.getT()).getEstimatedRows()); i--) {
					values[i + 1] = values[i];
				
				}
				values[i + 1] = temp;
			
			}	
	}

//	/**
//	 * @param min
//	 * @return
//	 */
//	private double updateNumDist(AtomVarDegree avd, double agm) {
//		// TODO Auto-generated method stub
//		// Update number of distinct values w.r.t. current view sizes
//		return avd.getDegree() *  agm / g.getAtomsSize().get(avd.getAtomID());
//	}

	public HyperGraph getHg() {
		return hg;
	}

	public void setHg(HyperGraph hg) {
		this.hg = hg;
		joinVarIndexes = new int[hg.nov()];
	}


//	public double estimateJoinCost(IntArrayList chi, IntArrayList lambda, double[] weights, ObjectArrayList<ObjectArrayList<AtomVarDegree>> selectivities) {
//		
//		//TODO manca l'aggiunta delle keys semplici...
//		
//		int size = lambda.size();
//		
//		double[] costs = new double[size];
//		
//		for(int i = 0; i < costs.length; i++) 
//		   costs[i] = Math.log(weights[i])/Math.log(2);
//		
//		Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
//		
//		for(int i = 0; i < chi.size(); i++) {
//			
//			ObjectArrayList<AtomVarDegree> SelChiI = new ObjectArrayList<>(size);
//			
//			selectivities.add(SelChiI);
//			
//			double[] constraint = new double[size];
//			
//			for(int j = 0; j < size; j++) {
//				for(int k = 0; k < hg.getHyperEdges().size(); k++) {
//					if(hg.getHyperEdges().get(k).getId() == lambda.get(j)) {
//						if(hg.getHyperEdges().get(k).getVariables().contains(chi.get(i))) {
//							constraint[j] = 1;
//							SelChiI.add(new AtomVarDegree(lambda.get(j), chi.get(i), g.getDegreeAtomVar(lambda.get(j), chi.get(i))));
//						}
//						break;
//					}
//				}
//			}
//			constraints.add(new LinearConstraint(constraint, Relationship.GEQ, 1));
//		}
//		
//		for(int j = 0; j < size; j++) {
//			double[] constraint = new double[size];
//			constraint[j] = 1;
//			constraints.add(new LinearConstraint(constraint, Relationship.GEQ, 0));
//		}
//				
//		LinearObjectiveFunction f = new LinearObjectiveFunction(costs, 0);
//
//	    SimplexSolver solver = new SimplexSolver();
//	    PointValuePair optSolution = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),GoalType.MINIMIZE, new NonNegativeConstraint(true)); 
//	  
//	    return Math.round(Math.pow(2, optSolution.getValue()));
////	    return Math.round((Math.pow(2, optSolution.getValue())) + Math.round(Math.pow(2, optSolution.getValue())/Math.pow(2, chi.size()))/2);
//	    
//	  }
	
	private void computeColorings() {
		
		/**
		 * sono incluse le variabili artificiali, una per ogni atomo, per tanto non è necessario aggiungere le variabili di slack
		 */
		int size = hg.getVars().size(); // + hg.getHyperEdges().size(); 
		
		if(!HypertreeDecomp.completeDecomposition)
			size += hg.getHyperEdges().size(); // dobbiamo aggiungere le variabili di slack
		
		/**
		 * Funzione obiettivo
		 */
		
		double[] costs = new double[size];
		
		int ii = 0;
		
		int realVars = hg.getVars().size() - hg.getHyperEdges().size(); // escludo le variabili di slack
		
		for(; ii < realVars; ii++) 
		   costs[ii] = 0;
		
		for(; ii < costs.length; ii++) 
			costs[ii] = 1;
		
		/**
		 * Vincoli
		 */
		
		Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		
		for(int j = 0; j < hg.getHyperEdges().size(); j++) {
			
			double[] constraintJ =  new double[size];
			
			constraintJ[realVars + j] = 1; // slackVar
						
			HyperEdge c = hg.getHyperEdges().get(j);
					
			for(int i = 0; i < hg.getVars().size(); i++) {
			
				int var = hg.getVars().get(i);
				if(!HypertreeDecomp.completeDecomposition || !DatalogParser.getArtificialVars().contains(var)) {
					if(c.getVariables().contains(var))
						constraintJ[i] = Math.log(g.getVarDistinctValues(var))/Math.log(2);
//					constraintJ[i] = Math.log(p.getDomainByName(p.getVariables().get(i).getDomainName()).getnValues())/Math.log(2);
					else
						constraintJ[i] = 0;
				}
		  }
			constraints.add(new LinearConstraint(constraintJ, Relationship.GEQ, Math.log(g.getAtomsSize().get(c.getId()))/Math.log(2))); // >= log(|r_j|)
		}
		
		for(int i = 0; i < size; i++) {
			double[] constraint1 = new double[size];   // Y_i >= 0
			constraint1[i] = 1;
			constraints.add(new LinearConstraint(constraint1, Relationship.GEQ, 0));
		}
		
		for(int i = 0; i < realVars; i++) {
			double[] constraint1 = new double[size];   // Y_i \in [m] <= 1
			constraint1[i] = 1;
			constraints.add(new LinearConstraint(constraint1, Relationship.LEQ, 1));
		}
				
		LinearObjectiveFunction f = new LinearObjectiveFunction(costs, 0);

	    SimplexSolver solver = new SimplexSolver();
	    PointValuePair optSolution = solver.optimize(new MaxIter(1000), f, new LinearConstraintSet(constraints), GoalType.MINIMIZE, new NonNegativeConstraint(true)); 

	    for(int i = 0; i < hg.getVars().size(); i++) {
	    	int var = hg.getVars().get(i);
	    	if(!HypertreeDecomp.completeDecomposition || !DatalogParser.getArtificialVars().contains(var)) {
				double log = Math.log(g.getVarDistinctValues(var))/Math.log(2);
				double col = optSolution.getKey()[i]*log;
    			varColoring.put(var, col);
			}
	    }
	  	   
	}

}
