package solver;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ObjectDoubleHashMap;

/**
 * @author Francesco
 * creato il 18/mag/2016 16:13:19
 */
public class ComponentNode {

	private IntArrayList attive;

	private IntArrayList frontiera;
	
	private ResourceNode minWeightResourceNode;
	
	private double weight;
	
	private double bestWeight = Double.MAX_VALUE;
	
	private int[] currentCombination;
	
	private int hasNextCombination;
	
	private int id;
	
	private boolean used = false;
	
	private ObjectDoubleHashMap<ResourceNode> children;

	private boolean isExploded;
	
	public ComponentNode(int id) {
		// TODO Auto-generated constructor stub
		this.id = id;
		attive = new IntArrayList();
		frontiera = new IntArrayList();
		children = new ObjectDoubleHashMap<>();
		weight = Double.MAX_VALUE;
		
	}

	
	public boolean isUsed() {
		return used;
	}


	public void setUsed(boolean used) {
		this.used = used;
	}


	public IntArrayList getAttive() {
		return attive;
	}

	public void setAttive(IntArrayList attive) {
		this.attive = attive;
	}

	public IntArrayList getFrontiera() {
		return frontiera;
	}

	public void setFrontiera(IntArrayList frontiera) {
		this.frontiera = frontiera;
	}

	public ResourceNode getMinWeightResourceNode() {
		return minWeightResourceNode;
	}

	public void setMinWeightResourceNode(ResourceNode minWeightResourceNode) {
		this.minWeightResourceNode = minWeightResourceNode;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getBestWeight() {
		return bestWeight;
	}

	public void setBestWeight(double bestWeight) {
		this.bestWeight = bestWeight;
	}
	
	public void createCurrentCombination(int k) {
		currentCombination = new int[k];
	}

	public int[] getCurrentCombination() {
		return currentCombination;
	}

	public void setCurrentCombination(int[] currentCombination) {
		this.currentCombination = currentCombination;
	}

	public int getHasNextCombination() {
		return hasNextCombination;
	}

	public void setHasNextCombination(int hasNextCombination) {
		this.hasNextCombination = hasNextCombination;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ObjectDoubleHashMap<ResourceNode> getChildren() {
		return children;
	}

	public void setChildren(ObjectDoubleHashMap<ResourceNode> children) {
		this.children = children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attive == null) ? 0 : attive.hashCode());
		result = prime * result
				+ ((frontiera == null) ? 0 : frontiera.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentNode other = (ComponentNode) obj;
		if (attive == null) {
			if (other.attive != null)
				return false;
		} else if (!attive.equals(other.attive))
			return false;
		if (frontiera == null) {
			if (other.frontiera != null)
				return false;
		} else if (!frontiera.equals(other.frontiera))
			return false;
		return true;
	}

	public void setExploded() {
		isExploded = true;
	}
	/**
	 * @return
	 */
	public boolean isExploded() {
		// TODO Auto-generated method stub
		return isExploded;
	}
	
}
