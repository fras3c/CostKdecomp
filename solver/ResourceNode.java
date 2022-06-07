package solver;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ObjectArrayList;

import functions.DomainValue;
import functions.Value;
import functions.WeightingFunction;

/**
 * @author Francesco
 * creato il 18/mag/2016 16:13:26
 */
public class ResourceNode {

	private WeightingFunction<? extends Value> f;
	
	private DomainValue<? extends Value> bestDomainValue = new DomainValue<>();
	
	private DomainValue<? extends Value>[] values; // domainValues figli
	
	private ObjectArrayList<DomainValue<? extends Value>> domainValues; 
	
	private IntArrayList chi; // set di variables
	
	private IntArrayList lambda;
	
	private ObjectArrayList<ComponentNode> children;
	
	private int id;

	private int counterNewDomainValues;
	
	private double weight;
	
	public ResourceNode(String name, int id) {
		// TODO Auto-generated constructor stub
		this.id = id;
		chi = new IntArrayList();
		lambda = new IntArrayList();
		this.children = new ObjectArrayList<ComponentNode>();
//		predecessors = new ObjectArrayList<ComponentNode>();
		weight = Double.MAX_VALUE;
		
//		values = new PS[children.size()];	
//		lastCombinationsChildrenDomainValues = new ArrayList<>(); // contiene le ultime combinazioni
//		allCombinationsChildrenDomainValues = new ArrayList<>(); // contiene tutte le combinazioni
		
		domainValues = new ObjectArrayList<>();
	}
	
	public WeightingFunction<? extends Value> getF() {
		return f;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setWeightingFunction(WeightingFunction<? extends Value> f) {
		this.f = f;
	}
	
	public DomainValue<? extends Value> getBestDomainValue() {
		return bestDomainValue;
	}

	public double getWeight() {
		return weight;
	}
	

	public void setChi(IntArrayList chi) {
		this.chi = chi;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setBestDomainValue(DomainValue<? extends Value> bestDomainValue) {
		this.bestDomainValue = bestDomainValue;
	}


	public ObjectArrayList<DomainValue<? extends Value>> getDomainValuesList() {
		return domainValues;
	}


	public void setDomainValuesList(ObjectArrayList<DomainValue<? extends Value>> domainValues) {
		this.domainValues = domainValues;
	}
	
	public void setLambda(IntArrayList lambda) {
		// TODO Auto-generated method stub
		this.lambda = lambda;
	}

	public IntArrayList getLambda() {
		return lambda;
	}
	
    public void computeWeight() {  
//			System.out.println(f);
		if(children.size() == 0) 
			bestDomainValue = f.gh(chi, lambda, null);	
//			
		else {			
			
			DomainValue[] dvs= new DomainValue[children.size()];
			values = dvs;
			for(int i = 0; i < children.size(); i++) 
				values[i] = children.get(i).getMinWeightResourceNode().getBestDomainValue();
			
			bestDomainValue = f.gh(chi, lambda, dvs);	
			
		}
		
		bestDomainValue.setResOwner(this);
		weight = bestDomainValue.toReal();
			
    }

	public IntArrayList getChi() {
		return chi;
	}

	public ObjectArrayList<ComponentNode> getChildren() {
		return children;
	}

	public void setChildren(ObjectArrayList<ComponentNode> children) {
		this.children = children;
	}
	
}
