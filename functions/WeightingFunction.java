package functions;

import com.carrotsearch.hppc.IntArrayList;


/**
 * @author Francesco creato il 25/lug/2014 12:46:44
 * @param <T>
 */

public interface WeightingFunction<T extends Value> {

	public DomainValue<T> gh(IntArrayList chi, IntArrayList lambda, DomainValue<T>[] dvs);

}