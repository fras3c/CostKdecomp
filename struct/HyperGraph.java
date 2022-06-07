package struct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.Mapping;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntObjectHashMap;

import com.carrotsearch.hppc.ObjectArrayList;

/**
 * @author Francesco creato il 16/mag/2014 11:41:48
 */

public class HyperGraph {

	private ObjectArrayList<HyperEdge> vincoli;
	private ObjectArrayList<HyperEdge> vincoliOri;
	private IntObjectHashMap<HyperEdge> vincoliK;
	
	private boolean[][] adjacency;
	private IntArrayList vars;
	private IntArrayList[] adjacencyList;
	private Mapping mapping;
	

	public HyperGraph() {
		vincoli = new ObjectArrayList<HyperEdge>();
		vincoliOri = new ObjectArrayList<HyperEdge>();
		vincoliK = new IntObjectHashMap<>();
		vars = new IntArrayList();
		
	}

	public HyperGraph(ObjectArrayList<HyperEdge> atoms, IntArrayList vars) {
		this.vincoli = atoms;
		this.vars = vars;
		vincoliK = new IntObjectHashMap<>();
	}
	
	
	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public void createAdjacencyList(int n) {
		adjacencyList = new IntArrayList[n];
		
		for(int i = 0 ; i < adjacencyList.length; i++)
			adjacencyList[i] = new IntArrayList();
	}
	
	public ObjectArrayList<HyperEdge> getHyperEdges() {
		return vincoli;
	}
	public IntObjectHashMap<HyperEdge> getVincoliK() {
		return vincoliK;
	}

	public void setHyperEdges(ObjectArrayList<HyperEdge> constraintHyperEdges) {
		this.vincoli = constraintHyperEdges;
	}

	
	public ObjectArrayList<HyperEdge> getVincoliOri() {
		return vincoliOri;
	}

	public IntArrayList getVars() {
		return vars;
	}

	public void setVars(IntArrayList vars) {
		this.vars = vars;
	}


	public int nov() {

		return vars.size();
	}


	public int noa() {

		return vincoli.size();
	}
	
	
	public void setAdjacencyMatrix(boolean[][] m) {
		this.adjacency = m;
	}
	
	public boolean[][] getAdjacencyMatrix() {
		return adjacency;
	}
	
	public IntArrayList[] getAdjacencyList() {
		return adjacencyList;
	}
	
	public void freeSpace () {
		adjacency = null;
		adjacencyList = null;
		System.gc();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
		try {
			finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public IntArrayList parallelRemove(IntArrayList lista1, IntArrayList lista2) {
		IntArrayList except = new IntArrayList();
	 	int i = 0;
		int j = 0;
		
		while( i < lista1.size() && j < lista2.size()) {
	
				if( lista1.get(i) < lista2.get(j)) {
				 except.add(lista1.get(i));
				 i++;
			 }
			else if(lista1.get(i) > lista2.get(j)){
//				except.add(lista2.get(j));
				j++;
			}
			else {
//				except.add(lista2.get(j));
				i++;
				j++;
			}
		}
		while( i < lista1.size()) {
			except.add(lista1.get(i));
			i++;
		}
//		while( j < lista2.size()) {
//			except.add(lista2.get(j));
//			j++;
//		}
//		System.out.println();
		return except;
	}
	
	public ObjectArrayList<HyperGraph> calcolaComponentiConnesse(IntArrayList hr, IntArrayList attivi) {
		
		ObjectArrayList<HyperGraph> componenti = new ObjectArrayList<HyperGraph>();
		
		IntArrayList unionVars = new IntArrayList(attivi);
		
		boolean[] enabled = null;
		
//		if(GreedyTreeProjection.SAT_INSTANCE) // potrebbe mancare qualche variabile nel caso di SAT
//			enabled = new boolean[adjacency.length];		
//		else
			enabled = new boolean[vars.size()];	
		
//		for(int i = 0; i < hr.size(); i++)
//			unionVars.removeFirstOccurrence(hr.get(i));//..removeAll(hr);
		
		unionVars = parallelRemove(unionVars, hr);
		
		for(int i = 0 ; i < unionVars.size(); i++)
			   enabled[unionVars.get(i)] = true;
		
		while( unionVars.size() > 0) {
			
//			System.out.println(unionVars.size());
			
			int src = unionVars.remove(0);
			
//			for(int j = 0 ; j < componenti.size(); j++) {
//				HyperGraph componente = componenti.get(j);
//				if(componente.getVars().contains(src)) // variabile già elaborata
//					continue loop;
//			}
			
			IntArrayList visited = new IntArrayList();
//			if(!visited.contains(src))
				visited.add(src);
				enabled[src] = false;
//				System.out.println("INIZIO");
			dfs(src, visited, unionVars, enabled);
//			System.out.println("FINE");
//			System.out.println(unionVars.size());
			HyperGraph component = new HyperGraph();
			visited.trimToSize();
			Arrays.parallelSort(visited.buffer);
			component.setVars(visited);
			HyperEdge h = new HyperEdge(-1, visited);
			component.getHyperEdges().add(h);
			componenti.add(component);
		}
		
//		System.out.println("CIAOOOO");
		
		componenti.trimToSize();
		return componenti;		
		
	}
	
	public void dfs(int v, IntArrayList visited, IntArrayList unionVars, boolean[] enabled) {
		
//			for(int k = 0; k < unionVars.size(); k++) {
		for(int k = 0; k < adjacencyList[v].size(); k++) {
				int x = adjacencyList[v].get(k);
				if(enabled[x]) {
					enabled[x] = false;
//				if (unionVars.contains(x)) {
					visited.add(x);
					unionVars.removeFirst(x);
					dfs(x, visited, unionVars, enabled);
				}
			}
	}
	
	private  boolean adjacent(int v, int k) {
		// TODO Auto-generated method stub

//		for (int i=0 ; i < vincoli.size(); i++) {
//			HyperEdge h = vincoli.get(i);
//			if (h.getVariables().contains(v) && h.getVariables().contains(k))
		if(adjacency[v][k])
				return true;
		
		return false;
	}
//  public ObjectArrayList<HyperGraph> calcolaComponentiConnesse(IntArrayList hr, IntArrayList attivi) {
//	  
//	  ObjectArrayList<HyperGraph> componenti = new ObjectArrayList<HyperGraph>();
//	  
//	  IntArrayList unionVars = new IntArrayList(attivi);
//
//	  for(int i = 0; i < hr.size(); i++)
//		  unionVars.removeFirstOccurrence(hr.get(i));//..removeAll(hr);
//	  
//	  		IntArrayList reachedNodes = new IntArrayList();
//			
//			
//	loop:	while( unionVars.size() > 0) {
//		
//				System.out.println(unionVars.size());
//				
//				int src = unionVars.remove(0);
//				
//				for(int j = 0 ; j < componenti.size(); j++) {
//					HyperGraph componente = componenti.get(j);
//					if(componente.getVars().contains(src)) // variabile già elaborata
//						continue loop;
//				}
//				if(!reachedNodes.contains(src))
//				reachedNodes.add(src);
//				
//				IntArrayList visited = new IntArrayList();
//				if(!visited.contains(src))
//				  visited.add(src);
//
//			while (!reachedNodes.isEmpty()) {
//				System.out.println("reachNodes " + reachedNodes.size());
//				
//				int v = reachedNodes.remove(0);
//				for(int k = 0; k < unionVars.size(); k++) {
//					if (adjacent(v, unionVars.get(k)) && v!=unionVars.get(k) && !visited.contains(unionVars.get(k))) {
//						if(!reachedNodes.contains(unionVars.get(k)))
//						reachedNodes.add(unionVars.get(k));
//						visited.add(unionVars.get(k));
//						
//					}
//				}
//			}
//			HyperGraph component = new HyperGraph();
//			component.setVars(visited);
//			HyperEdge h = new HyperEdge(-1, visited);
//			component.getHyperEdges().add(h);
//			componenti.add(component);
//		}
//			
//	return componenti;		
//
//	  
//  }

/**
 * @param v
 * @param k
 * @return
 */
//	private boolean adjacent(int v, int k) {
//		// TODO Auto-generated method stub
//
//		for (int i=0 ; i < vincoli.size(); i++) {
//			HyperEdge h = vincoli.get(i);
//			if (h.getVariables().contains(v) && h.getVariables().contains(k))
//				return true;
//		}
//		return false;
//	}

	@Override
	public String toString() {

		String result = "Vars: ";

		for (int i = 0; i < vars.size(); i++)

			result += vars.get(i) + ", ";

		result += "\n";

		for (int i = 0; i < vincoli.size(); i++) {
			HyperEdge h = vincoli.get(i);
			result += h.toString() + "\n";
		}
		return result;
	}
	
	public HyperEdge inizializzaCombinazioniTreeDecomp(int[] currentCombination, ObjectArrayList<HyperEdge> vincoli, int k) { 
		int j; // index

//		currentCombination = new int[k];

		// initialize: vector[0, ..., k - 1] are 0, ..., k - 1
//		int[] cacheEdgeID = new int[k];
//		
	
			
		
		for (j = 0; j < k; j++) {
			currentCombination[j] = j;
	//		cacheEdgeID[j] = vincoli.get(currentCombination[j]).getId();
		}
		
		int cacheEdgeIdHashCode = Arrays.hashCode(currentCombination);
		
		HyperEdge he = vincoliK.get(cacheEdgeIdHashCode);
		
		if(he!=null)
			return he;
		
		int i = 0;
		
		// union aggiunge duplicati verificare....
		// HyperEdge [a0, a1] -> 0, 1, 2, 3, 2,
		
		  boolean externalFlag = true;
			for(int i1 = 0; i1 < currentCombination.length; i1++) {
				boolean flag = false;
				for(int j1 = 0; j1 < currentCombination.length; j1++) {
					if(i1!=j1) {
					 for(int k1 = 0; k1 < vincoli.size(); k1++)
						if(this.vincoli.get(k1).getVariables().contains(currentCombination[i1]) && this.vincoli.get(k1).getVariables().contains(currentCombination[j1])) {
							flag = true;
							break;
						}
					 if(flag)
						 break;
					}
				  }
				if(!flag) {
					externalFlag = false;
					break;
				}
			}
			
			if(externalFlag) {
		
				
		IntArrayList union = IntArrayList.from(currentCombination);
		String name = Arrays.toString(currentCombination);
		
//		for( i = 2; i < currentCombination.length; i++) {
//			union =	union(union, vincoli.get(currentCombination[i]).getVariables());
//			name += ", " +vincoli.get(currentCombination[i]).getName(); 
//		}
		union.trimToSize();
		int id = lastID() + 1;
		setLastID(id);
		he = new HyperEdge(id, union);
		he.setName(name);
		he.getVariables().trimToSize();
		Arrays.parallelSort(he.getVariables().buffer);
		vincoliK.put(cacheEdgeIdHashCode, he);
		 return he;
			}
			else {
				do 
				{
				he = generateNextHKv(currentCombination, vincoli, vars.size(), k);
				} while( he == null);
				return he;
			}
		
		
	}
	 
	 
	public HyperEdge generateNextTreeKv(int[] currentCombination, ObjectArrayList<HyperEdge> vincoli, int n, int k) {
			
		int hasNextCombination;
		
		boolean externalFlag = true;
		do {
			hasNextCombination = nextCombination(currentCombination, n, k);		
			if(hasNextCombination == 1)
				return null;
//		int[] cacheEdgeID = new int[k];
//		for(int j = 0 ; j < k; j++)
//			cacheEdgeID[j] = vincoli.get(currentCombination[j]).getId();
		
	    int cacheEdgeIdHashCode = Arrays.hashCode(currentCombination);
			
	    HyperEdge he = vincoliK.get(cacheEdgeIdHashCode);
		
	
		if(he!=null)
			return he;
		
		int i = 0;
		
	   externalFlag = true;
	   for(int i1 = 0; i1 < currentCombination.length; i1++) {
			boolean flag = false;
			for(int j1 = 0; j1 < currentCombination.length; j1++) {
				if(i1!=j1) {
				 for(int k1 = 0; k1 < vincoli.size(); k1++)
					if(this.vincoli.get(k1).getVariables().contains(currentCombination[i1]) && this.vincoli.get(k1).getVariables().contains(currentCombination[j1])) {
						flag = true;
						break;
					}
				 if(flag)
					 break;
				}
			  }
			if(!flag) {
				externalFlag = false;
				break;
			}
		}
		
		if(externalFlag) {
			
		
			IntArrayList union = IntArrayList.from(currentCombination);
			String name = Arrays.toString(currentCombination);
		
//		for( i = 2; i < currentCombination.length; i++) {
//			union =	union(union, vincoli.get(currentCombination[i]).getVariables());
//			name += ", " +vincoli.get(currentCombination[i]).getName(); 
//		}
		
		int id = lastID() + 1;

		setLastID(id);
		he = new HyperEdge(id, union);
		he.setName(name);
		he.getVariables().trimToSize();
		Arrays.parallelSort(he.getVariables().buffer);
//		if(!vincoliK.containsKey(cacheEdgeIdHashCode))
		 vincoliK.put(cacheEdgeIdHashCode, he);
		 	return he;
		} 
	} while(!externalFlag && hasNextCombination != 1);
		
		return null;
	}
	
	public HyperEdge inizializzaCombinazioni(int[] currentCombination, ObjectArrayList<HyperEdge> vincoli, int n, int k) {
		int j; // index
		
//		currentCombination = new int[k];
		
		// initialize: vector[0, ..., k - 1] are 0, ..., k - 1
//		int[] cacheEdgeID = new int[k];
//		
		for (j = 0; j < k; j++) {
			currentCombination[j] = j;
			//		cacheEdgeID[j] = vincoli.get(currentCombination[j]).getId();
		}
		
		int cacheEdgeIdHashCode = -1;
		for(int i = 0 ; i < k; i++) 
			cacheEdgeIdHashCode += vincoli.get(currentCombination[i]).hashCode();
		
		HyperEdge he = vincoliK.get(cacheEdgeIdHashCode);
		
		if(he!=null)
			return he;
		
		int i = 0;
		
		// union aggiunge duplicati verificare....
		// HyperEdge [a0, a1] -> 0, 1, 2, 3, 2, 
		IntArrayList union = null;
		String name = "";
		if(k > 1) {
		union = union(vincoli.get(currentCombination[i]).getVariables(), vincoli.get(currentCombination[i + 1]).getVariables());
		name = vincoli.get(currentCombination[i]).getName() + ", " + vincoli.get(currentCombination[i + 1]).getName();
		
		for( i = 2; i < currentCombination.length; i++) {
			union =	union(union, vincoli.get(currentCombination[i]).getVariables());
			name += ", " +vincoli.get(currentCombination[i]).getName(); 
		 }
		}
		else {
			union = new IntArrayList();
			union.addAll(vincoli.get(currentCombination[i]).getVariables());
			name = vincoli.get(currentCombination[i]).getName();
		}
		union.trimToSize();
		int id = lastID() + 1;
		setLastID(id);
		he = new HyperEdge(id, union);
		he.setName(name);
		vincoliK.put(cacheEdgeIdHashCode, he);
		return he; 
		
	}
	
	public IntArrayList union(IntArrayList lista1, IntArrayList lista2) {
		IntArrayList union = new IntArrayList();
		int i = 0;
		int j = 0;
		
		while( i < lista1.size() && j < lista2.size()) {
			
			if( lista1.get(i) < lista2.get(j)) {
				union.add(lista1.get(i));
				i++;
			}
			else if(lista1.get(i) > lista2.get(j)){
				union.add(lista2.get(j));
				j++;
			}
			else {
				union.add(lista2.get(j));
				i++;
				j++;
			}
		}
		while( i < lista1.size()) {
			union.add(lista1.get(i));
			i++;
		}
		while( j < lista2.size()) {
			union.add(lista2.get(j));
			j++;
		}
//			System.out.println();
		return union;
	}
	
	public HyperEdge generateNextHKv(int[] currentCombination, ObjectArrayList<HyperEdge> vincoli, int n, int k) {
		
		int hasNextCombination = nextCombination(currentCombination, n, k);
		
		if(hasNextCombination == 1)
			return null;
		
//		int[] cacheEdgeID = new int[k];
//		for(int j = 0 ; j < k; j++)
//			cacheEdgeID[j] = vincoli.get(currentCombination[j]).getId();
		
		int cacheEdgeIdHashCode = -1;
		
		for(int j = 0 ; j < k; j++) 
			cacheEdgeIdHashCode += vincoli.get(currentCombination[j]).hashCode();
		
		HyperEdge he = null;
		
		he = vincoliK.get(cacheEdgeIdHashCode);
		if(he!=null)
			return he;
		
		int i = 0;
		
		IntArrayList union = null;
		String name = "";
		
		if(k > 1) {
		union = union(vincoli.get(currentCombination[i]).getVariables(), vincoli.get(currentCombination[i + 1]).getVariables());
		name = vincoli.get(currentCombination[i]).getName() + ", " + vincoli.get(currentCombination[i + 1]).getName();
		
		for( i = 2; i < currentCombination.length; i++) {
			union =	union(union, vincoli.get(currentCombination[i]).getVariables());
			name += ", " +vincoli.get(currentCombination[i]).getName(); 
		}
		}
		else {
			union = new IntArrayList();
			union.addAll(vincoli.get(currentCombination[i]).getVariables());
			name = vincoli.get(currentCombination[i]).getName();
		}
		int id = lastID() + 1;
		
		setLastID(id);
		he = new HyperEdge(id, union);
		he.setName(name);
//		if(!vincoliK.containsKey(cacheEdgeIdHashCode))
		vincoliK.put(cacheEdgeIdHashCode, he);
		return he;
	}
	
	/**
	 * @param currentCombination
	 * @return
	 */
	private int hashCode(int[] currentCombination) {
		// TODO Auto-generated method stub	
	        if (currentCombination == null)
	            return 0;
	        				
	        int result = 1;
	        for (int element : currentCombination) {
	        	result = result^element;
	        	result = result*31;
	        }
	        return result + 3;
	    
	}

	int nextCombination(int[] currentCombination, int n, int k) {
		int j; // index

		// easy case, increase rightmost element
		if (currentCombination[k - 1] < n - 1) {
			currentCombination[k - 1]++;
			return 0;
		}

		// find rightmost element to increase
		for (j = k - 2; j >= 0; j--)
			if (currentCombination[j] < n - k + j)
				break;

		// terminate if vector[0] == n - k
		if (j < 0)
			return 1;

		// increase
		currentCombination[j]++;

		// set right-hand elements
		while (j < k - 1) {
			currentCombination[j + 1] = currentCombination[j] + 1;
			j++;
		}

		return 0;
	}
	
	public ObjectArrayList<HyperEdge> generateHyperTreeKvertex(int noa, int k) {
		// usa l'algoritmo di combinazioni di Knuth
		
		
		ObjectArrayList<HyperEdge> augmentedResource = new ObjectArrayList<HyperEdge>();
		
		if (noa == k) {  // se k >= numberOfAtoms generiamo un kvertex che contiene tutte le variabili dell'ipergrafo
			HyperEdge he = new HyperEdge(lastID() + 1, new IntArrayList());
			for (int z = 0; z < vars.size(); z++)
//				if(he.getVariables().contains(vars.get(z)))
					he.getVariables().add(vars.get(z));
				augmentedResource.add(he);	
//				vincoli.addAll(augmentedResource);
				return augmentedResource;
		}
		
		int i;
		int[] c = new int[k + 3];
		int j = 1;
		int x = 0;

		for (i = 1; i <= k; i++) {
			c[i] = i;
			c[k + 1] = noa + 1;
			c[k + 2] = 0;
			j = k;
		}

		int id = lastID() + 1;
		System.out.println(id);
		
		for (;; j--) {
		HyperEdge he = new HyperEdge(id++, new IntArrayList());
		String name = "";
		StringBuilder sb = new StringBuilder();
			for (i = k; i >= 1; i--) { // qui vanno create le risorse.
				int heID = c[i];
					
				if(i>1)
					sb.append(mapping.getAtomMap().get(heID) + ",");
				else 
					sb.append(mapping.getAtomMap().get(heID));
				
					he.setNoa(he.getNoa() + 1); // sto usando più atomi
				
//				if (he.getName().equals(""))
//					he.setName(heID + "");
//				else {
//					he.setName(he.getName() + ", " + heID);
//					he.setNoa(he.getNoa() + 1); // sto usando più atomi
//				}
				
//				System.out.print(heID + " ");
				for(int i1 = 0; i1 < vincoli.size(); i1++) {
					
					if(vincoli.get(i1).getId() == heID)
					   for(int j1 = 0; j1 < vincoli.get(i1).getVariables().size(); j1++)
						if(!he.getVariables().contains(vincoli.get(i1).getVariables().get(j1)))
							he.getVariables().add(vincoli.get(i1).getVariables().get(j1));
				}
			}
			
			String[] lista = sb.toString().split(",");
			
			for(int w = lista.length - 1; w >= 0; w--)
				if(w==lista.length - 1)
					name+=lista[w];
				else
					name+=", " + lista[w];
			
			he.setName(name);
			he.getVariables().trimToSize();
			Arrays.parallelSort(he.getVariables().buffer);
			augmentedResource.add(he);

//			System.out.println();
			if (j > 0)
				x = j + 1;
			else {
				if (c[1] + 1 < c[2]) {
					c[1] += 1;
					continue;
				}

				for (j = 2;; j++) {
					c[j - 1] = j - 1;
					x = c[j] + 1;
					if (x != c[j + 1])
						break;
				}
				if (j > k)
					break;
			}

			c[j] = x;
		}
	
//		vincoli.addAll(augmentedResource);
		return augmentedResource;
	}
	public ObjectArrayList<HyperEdge> generateTreeKvertex(int noa, int k) {
		// usa l'algoritmo di combinazioni di Knuth
		
		
		
		ObjectArrayList<HyperEdge> augmentedResource = new ObjectArrayList<HyperEdge>();
		int id = 0;
		if(k == 2) {
			for(int i = 0 ; i < vincoli.size(); i++) {
				if(vincoli.get(i).getVariables().size() == 2)
					augmentedResource.add(vincoli.get(i));
				else
					for(int j = 0 ; j < vincoli.get(i).getVariables().size(); j++) {
						for(int z = 0; z < vincoli.get(i).getVariables().size(); z++) {
							HyperEdge he = new HyperEdge(id++, new IntArrayList());
							he.getVariables().add(vincoli.get(i).getVariables().get(z));
							augmentedResource.add(he);
						}
					}
//				vincoli.remove(i);
//				i--;
			}
//			vincoli.addAll(augmentedResource); 
			return augmentedResource;
		}
		
		if (vars.size() == k) {  // se k >= numberOfAtoms generiamo un kvertex che contiene tutte le variabili dell'ipergrafo
			HyperEdge he = new HyperEdge(lastID() + 1, new IntArrayList());
			for (int z = 0; z < vars.size(); z++)
//				if(he.getVariables().contains(vars.get(z)))
				he.getVariables().add(vars.get(z));
				augmentedResource.add(he);	
//				vincoli.addAll(augmentedResource);
			return augmentedResource;
		}
		
	
		int i;
		int[] c = new int[k + 3];
		int j = 1;
		int x = 0;
		
		for (i = 1; i <= k; i++) {
			c[i] = i;
			c[k + 1] = noa + 1;
			c[k + 2] = 0;
			j = k;
		}
		
		id = lastID() + 1;
		System.out.println(id);
		
		for (;; j--) {
			HyperEdge he = new HyperEdge(id++, new IntArrayList());
			String name = "";
			StringBuilder sb = new StringBuilder();
			for (i = k; i >= 1; i--) { // qui vanno create le risorse.
				
				
				int vID = c[i] - 1;
				
				if(i>1)
					sb.append(mapping.getVarNameById(vID) + ",");
				else 
					sb.append(mapping.getVarNameById(vID));
				
				// System.out.print(vID + " ");
//				if (!he.getVariables().contains(vID))
					he.getVariables().add(vID);
			}
			
			String[] lista = sb.toString().split(",");
			
			for(int w = lista.length - 1; w >= 0; w--)
				if(w==lista.length - 1)
					name+=lista[w];
				else
					name+=", " + lista[w];
			
			he.setName(name);
			he.getVariables().trimToSize();
		
			boolean externalFlag = true;
			for(int i1 = 0; i1 < he.getVariables().size(); i1++) {
				boolean flag = false;
				for(int j1 = 0; j1 < he.getVariables().size(); j1++) {
					if(i1!=j1) {
					 for(int k1 = 0; k1 < vincoli.size(); k1++)
						if(vincoli.get(k1).getVariables().contains(he.getVariables().get(i1)) && vincoli.get(k1).getVariables().contains(he.getVariables().get(j1))) {
							flag = true;
							break;
						}
					 if(flag)
						 break;
					}
				  }
				if(!flag) {
					externalFlag = false;
					break;
				}
			}
			
			if(externalFlag) {
				Arrays.parallelSort(he.getVariables().buffer);
				augmentedResource.add(he);
			}
			
//			System.out.println();
			if (j > 0)
				x = j + 1;
			else {
				if (c[1] + 1 < c[2]) {
					c[1] += 1;
					continue;
				}

				for (j = 2;; j++) {
					c[j - 1] = j - 1;
					x = c[j] + 1;
					if (x != c[j + 1])
						break;
				}
				if (j > k)
					break;
			}

			c[j] = x;
		}
		
//		vincoli.addAll(augmentedResource);
		return augmentedResource;
	}

	/**
	 * @return
	 */
	private int lastID = 0;
	
	private int lastID() {
		// TODO Auto-generated method stub
		if(vincoliK.size() == 0)
			lastID = vincoli.get(vincoli.size() - 1) .getId();
		return lastID;
	}
	private void setLastID(int id) {
		// TODO Auto-generated method stub
		lastID = id;
	}
	
	public int intersect(IntArrayList vars1, IntArrayList vars2) {

		int counter = 0;
		int i = 0;
		int j = 0;
		while (i < vars1.size() && j < vars2.size()) {

			if (vars1.get(i) < vars2.get(j))
				i++;
			else if (vars1.get(i) > vars2.get(j))
				j++;
			else {
				counter++;
				i++;
				j++;
			}
		}
		// usatePerAttaccare = counter;
		return counter;
	}

	/**
	 * @param size
	 * @param i
	 */
//	public ObjectArrayList<HyperEdge> generateTreeKvertex(int nov, int k) {
//		// TODO Auto-generated method stub
//		ObjectArrayList<HyperEdge> augmentedResource = new ObjectArrayList<HyperEdge>();
//		int id = noa();
//		if (nov == k) {  // se k = numberOfVertex generiamo un kvertex che contiene tutte le variabili dell'ipergrafo
//			
//			HyperEdge he = new HyperEdge(id++, new IntArrayList());
//			for (int z = 0; z < vars.size(); z++)
//				if(he.getVariables().contains(vars.get(z)))
//					he.getVariables().add(vars.get(z));
//				augmentedResource.add(he);	
//				vincoli.addAll(augmentedResource);
//				return;
//		}
//		
//		int i;
//		int[] c = new int[k + 3];
//		int j = 1;
//		int x = 0;
//
//		for (i = 1; i <= k; i++) {
//			c[i] = i;
//			c[k + 1] = nov + 1;
//			c[k + 2] = 0;
//			j = k;
//		}
//
//		for (;; j--) {
//			HyperEdge he = new HyperEdge(id++, new IntArrayList());
//			for (i = k; i >= 1; i--) { // qui vanno create le risorse.
//				int vID = c[i] - 1;
//				// System.out.print(vID + " ");
//				if (!he.getVariables().contains(vID))
//					he.getVariables().add(vID);
//			}
//			
//			augmentedResource.add(he);
//
////			System.out.println();
//			if (j > 0)
//				x = j + 1;
//			else {
//				if (c[1] + 1 < c[2]) {
//					c[1] += 1;
//					continue;
//				}
//
//				for (j = 2;; j++) {
//					c[j - 1] = j - 1;
//					x = c[j] + 1;
//					if (x != c[j + 1])
//						break;
//				}
//				if (j > k)
//					break;
//			}
//
//			c[j] = x;
//		}
//	
//		vincoli.addAll(augmentedResource);
//	}
	
}
