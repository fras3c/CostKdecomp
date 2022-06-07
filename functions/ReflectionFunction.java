package functions;

import javax.management.RuntimeErrorException;

import userfunctions.CostDV;

import com.carrotsearch.hppc.IntArrayList;


/**
 * @author frank
 *
 * Created on Jul 24, 2014, 7:50:18 PM
 */

public class ReflectionFunction {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		if(true)
		   throw new RuntimeErrorException(new Error("Non ancora implementato"));
		
		WeightingFunction<? extends Value> function = null;
		
		function = (WeightingFunction<? extends Value>) newInstance("function.CostFunction");
		
		DomainValue<CostDV> a = new DomainValue<>();
		a.setT(new CostDV(3, 0, null));
//		DomainValue<PS> b = new DomainValue<>();
//		b.setT(new PS(500, 0));
		DomainValue<CostDV> c = new DomainValue<>();
		c.setT(new CostDV(1, 0, null));
//		DomainValue<PS> d = new DomainValue<>();
//		d.setT(new PS(500, 0));
		
		

		DomainValue<? extends Value> dv = function.gh(new IntArrayList(), new IntArrayList(), new DomainValue[] { a, c });
		
		
		System.out.println("weight " + dv.toReal());
		System.out.println(dv);
	
		
	}
	public static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	    Class<?> clazz = getClass(className);
	    if (clazz==null) {
	        return null;
	    }
	    return clazz.newInstance();
	}
	
	@SuppressWarnings("unchecked")
	public static Class<WeightingFunction<? extends Value>> getClass(String className) throws ClassNotFoundException {
		return (Class<WeightingFunction<? extends Value>>) Class.forName(className);
	}
}



 
// class FunzioneSBilanciata implements WeightingFunction<Double>{
//	 
//	 
//	 /* (non-Javadoc)
//	  * @see function.Function#gh(java.util.ArrayList)
//	  */
//	 @Override
//	 public DomainValue<Double> gh(ArrayList<DomainValue<Double>> values) {
//		 // TODO Auto-generated method stub
//		 DomainValue<Double> dv = new DomainValue<>(0.0);
//		 
//		 java.util.function.BiFunction<Double, Double, Double> f = (x,y) -> x+y;
//		 
//		 values.forEach(d -> dv.getT().setValue((f.apply(d.getT().getValue(), dv.getT().getValue()))));
//		 
//		 return dv;
//	 }
//	 
// }
 

 