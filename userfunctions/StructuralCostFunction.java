package userfunctions;

import com.carrotsearch.hppc.IntArrayList;
import functions.DomainValue;
import functions.WeightingFunction;

/**
 * @author Francesco creato il 29/lug/2014 18:20:28
 */

public class StructuralCostFunction implements WeightingFunction<StructuralDV> {

	
	@Override
	public DomainValue<StructuralDV> gh(IntArrayList chi, IntArrayList lambda, DomainValue<StructuralDV>[] values) {
		// TODO Auto-generated method stub
		
		DomainValue<StructuralDV> dv = new DomainValue<>();
		
		double width = lambda.size();
		
		if (values == null) { // questa risorsa è una foglia
			dv.setT(new StructuralDV(width, width));
			return dv;
		}
					
		double cost = width; 
		                    		
		for(DomainValue<StructuralDV> d : values) // il costo della risorsa dovrebbe essere la somma dei costi dei sotto alberi + il suo agm bound + il costo della semijoin 
			cost += d.toReal(); 
				
		dv.setT(new StructuralDV(cost, width));

		return dv;
	}
	


}
