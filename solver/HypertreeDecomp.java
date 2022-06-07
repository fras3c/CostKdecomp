package solver;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import parser.DatalogParser;
import struct.HyperEdge;
import struct.HyperGraph;
import userfunctions.AGMCostFunction;
import userfunctions.GLSCostFunction;
import util.Mapping;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import functions.ReflectionFunction;
import functions.Value;
import functions.WeightingFunction;
//import graphic.HyperGraphVisualizer;
//import graphic.TreeProjectionTabs;
//import graphic.Visualizer;

/**
 * @author Francesco
 * creato il 20/mag/2016 16:29:30
 */
public class HypertreeDecomp {

//	private int lastID = 0;

	public static boolean completeDecomposition;
	
	private ObjectArrayList<HyperEdge> risorse = new ObjectArrayList<>();
	
	private boolean[] risorseUsate;
	
	private int noa; // number of atoms
	
	private int nov; // number of vars
	
	private DatalogParser parser;
	
	private HyperGraph constraintHypergraph;

	private HyperGraph resourceHypergraph;

	private Mapping mapping;

	String checkConditionResults = "";
	
	public WeightingFunction<? extends Value> weightFunction;

	private int KV;
	
	public static int DOMAIN_VALUE_ID = 0;

	private boolean COMPONENT_REUSE = true;
	
	public int nodesID = 1;

	public IntObjectHashMap<ComponentNode> cacheComponentNodes;

	public ObjectArrayList<ComponentNode> componentNodes;

	public ObjectArrayList<ResourceNode> resourceNodes;

	private ComponentNode root;
	
	private int width;
	
	private String outputFileName;
	
	public HypertreeDecomp(String functionClassFullName, int width, DatalogParser parser, boolean completeDecomp, String outputFileName) {
		
		completeDecomposition = completeDecomp;
		
		cacheComponentNodes = new IntObjectHashMap<ComponentNode>();
		
		KV = width;
		
		this.parser =  parser;
				
		constraintHypergraph = parser.getConstraintHyperGraph();
		
		resourceHypergraph = parser.getConstraintHyperGraph();
		
		Mapping mapping = parser.getMapping();
		
		this.nov = parser.nov();
		
		this.noa = parser.noa();
		
		componentNodes = new ObjectArrayList<>();

		resourceNodes = new ObjectArrayList<>();
		
		
		weightFunction = createFunction("userfunctions."+functionClassFullName);
		
		if(weightFunction instanceof GLSCostFunction) {
			((GLSCostFunction) weightFunction).setG(mapping);
			((GLSCostFunction) weightFunction).setHg(constraintHypergraph);
		}
		
		this.mapping = mapping;
		
		DOMAIN_VALUE_ID = 0;
		
		this.outputFileName = outputFileName;
	}
	
	public HypertreeDecomp(String datalogExpression, String functionClassFullName, int width, boolean completeDecomp, String outputFileName) {
		
		completeDecomposition = completeDecomp;
		
		this.outputFileName = outputFileName;
		
		cacheComponentNodes = new IntObjectHashMap<ComponentNode>();
		
		KV = width;
		
		this.parser = new DatalogParser(completeDecomp);
		
		parser.parse(datalogExpression);
		
		constraintHypergraph = parser.getConstraintHyperGraph();
		
		resourceHypergraph = parser.getConstraintHyperGraph();
		
		Mapping mapping = parser.getMapping();
		
		this.nov = parser.nov();
		
		this.noa = parser.noa();
		
		componentNodes = new ObjectArrayList<>();

		resourceNodes = new ObjectArrayList<>();
	
		weightFunction = createFunction("userfunctions."+functionClassFullName);
		
		this.mapping = mapping;
		
		DOMAIN_VALUE_ID = 0;
		
	}

	public HypertreeDecomp(String datalogExpression, String atomSizes, String domainValues, String functionClassFullName, int width, boolean completeDecomp, String outputFileName) {
		
		completeDecomposition = completeDecomp;
		
		this.outputFileName = outputFileName;
		
		cacheComponentNodes = new IntObjectHashMap<ComponentNode>();

		KV = width;
	
		this.parser = new DatalogParser(completeDecomp);

//		kvertex = new Object[KV];
		
		parser.parse(datalogExpression);
		
		parser.fetchAtomSizes(atomSizes);
		
		parser.fetchDistinctValues(domainValues);
		
//		hgr.parseDocument(constraintHGPath, resourceHGPath);

		constraintHypergraph = parser.getConstraintHyperGraph();

		resourceHypergraph = parser.getConstraintHyperGraph();
		
		Mapping mapping = parser.getMapping();
		
		this.nov = parser.nov();
		
		this.noa = parser.noa();
		
		componentNodes = new ObjectArrayList<>();
//		allComponentNodes = new ObjectArrayList<>();
		resourceNodes = new ObjectArrayList<>();
		
//		tpg = new TreeProjectionTabs();
		
//		visualizer = new Visualizer(mapping, tpg);
		
		weightFunction = createFunction("userfunctions."+functionClassFullName);
		
		if(weightFunction instanceof AGMCostFunction) {
			 ((AGMCostFunction) weightFunction).setG(mapping);
			 ((AGMCostFunction) weightFunction).setHg(constraintHypergraph);
		}
		else if(weightFunction instanceof GLSCostFunction) {
			 ((GLSCostFunction) weightFunction).setG(mapping);
			 ((GLSCostFunction) weightFunction).setHg(constraintHypergraph);
		}

		this.mapping = mapping;
		
//		gw = new GvWriter();
		
//		gw.setMapping(this.mapping);
		
//		if (enableTreeDecomposition)
//			KV++;
		
		DOMAIN_VALUE_ID = 0;
		
//		if(ANYTIME_SOLUTION)
//			visualizeComponentGraph = false;
		
//		if(!ANYTIME_SOLUTION && monotoneOptimization || hgr instanceof CspParser) // modificare e abilitare nel caso di unweighted.... va estesa al caso normale
			
//		if(!monotoneOptimization)
//			COMPONENT_REUSE = false;
	
//		resourceHypergraph.freeSpace();

	}
	
	public int getWidth() {
		return width; // risovere problema python che printa width errata
	}
	
    private  int binarySearch(IntArrayList a, int chiave) {
        int inf = 0;
        int sup = a.size() - 1;
        while (inf <= sup) {
            int mid = inf + (sup - inf) / 2;
            if (chiave < a.get(mid)) 
            	sup = mid - 1;
            else if (chiave > a.get(mid))
            		inf = mid + 1;
            else return mid;
        }
        return -1;
    }

	/**
	 * @param functionClassfullName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private WeightingFunction<? extends Value> createFunction(String functionClassfullName) {
		// TODO Auto-generated method stub
		try {
			return (WeightingFunction<? extends Value>) ReflectionFunction.newInstance(functionClassfullName);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public ComponentNode getRoot() {
		return root;
	}
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public static void main(String[] args) {
				
//		String domainValues = "testAtomVarDistinctVals.txt";
		String domainValues = "testDistinctValues.txt";
		
		String atomSizes = "testSizes.txt";
		
		HypertreeDecomp htd = new HypertreeDecomp("test.txt", atomSizes, domainValues, "GLSCostFunction", 4, false, "test");
//		HypertreeDecomp htd = new HypertreeDecomp("test.txt", "StructuralCostFunction", 3, false, "test");
		
//		htd.computeTD();
		
		htd.compute();
//		htd.computeHTD();
//		htd.computeUnWeighted();
		
	}

	public void buildDecomposition(ComponentNode root, Map<ResourceNode, Set<Integer>> map) {
		// TODO Auto-generated method stub
		// System.out.println(root.getWeight());

		ResourceNode x = root.getMinWeightResourceNode();
//		System.out.println(root);
//		System.out.println(x);
//		if(ANYTIME_SOLUTION)
//		x.setWeight(x.getBestDomainValue().toReal());
		if (!map.containsKey(x))
			map.put(x, new LinkedHashSet<Integer>());

		
		for ( ObjectCursor<ComponentNode> y : x.getChildren()) {
			ResourceNode z = y.value.getMinWeightResourceNode();
			map.get(x).add(z.getId());
		}

		for (ObjectCursor<ComponentNode> y : x.getChildren())
			buildDecomposition(y.value, map);
	}


	public boolean fastIntersect(IntArrayList lista1, IntArrayList lista2) {
		
		int i = 0;
		int j = 0;
		while( i < lista1.size() && j < lista2.size()) {
			
			if(lista1.get(i) < lista2.get(j))
				i++;
			else if(lista1.get(i) >lista2.get(j))
				j++;
			else 
				return true;		
		}
		
		return false;
	}
	
	public IntArrayList calcolaFrontiera(IntArrayList hr, IntArrayList attivi) {
		
		IntArrayList frontiera = new IntArrayList(); // è tutto hr meno i vertici irraggiungibili
		boolean check;
		for (int i = 0; i < hr.size(); i++) { // x è raggiungibile se condivide un iperArco con la componente
			int x = hr.get(i);
			check = false;
			
			loop: for (int k = 0; k < constraintHypergraph.getHyperEdges().size(); k++) {
					HyperEdge he = constraintHypergraph.getHyperEdges().get(k);
					if (fastIntersect(he.getVariables(), attivi) && binarySearch(he.getVariables(), x) != -1 ) {
							check = true;
							break loop;
					}
			     }
			if (check)
				frontiera.add(x);
		}
		frontiera.trimToSize();
		Arrays.parallelSort(frontiera.buffer);
		return frontiera;
		
	}

	public int nov() { // numero di variabili
		return constraintHypergraph.nov();
	}
	
	private double solverTime;
	
	public double getTime() {
		return solverTime;
	}
	
	public void computeHTD() {
		
		long tStart = System.nanoTime();
		
		root = new ComponentNode(nodesID);
		root.setUsed(true);
		
		componentNodes.add(root);
		
		for (int i = 0; i < nov(); i++)
			// nella root le variabili da attaccare sono tutte attive e la
			// frontiera è vuota
			root.getAttive().add(i);
		
		resourceHypergraph.setMapping(mapping);
		
		risorse.addAll(resourceHypergraph.getHyperEdges());
		
		int k = 2;
		
		while(k <= KV) {
			computeResources(k);
			k++;
		}
		
		risorseUsate = new boolean[risorse.size()];
		
		solve(root);
		
		long tEnd = System.nanoTime() - tStart;
		
		solverTime = tEnd / 1e9;
		
		if (root.getMinWeightResourceNode() != null ) 			
			root.setWeight(root.getMinWeightResourceNode().getWeight());		
		
	}
	
	public void computeTD() {
		
		long tStart = System.nanoTime();
		
		root = new ComponentNode(nodesID);
		root.setUsed(true);
		
		componentNodes.add(root);
		
		IntArrayList artificialVars = DatalogParser.getArtificialVars();
		
		for (int i = 0; i < nov(); i++) {
			// nella root le variabili da attaccare sono tutte attive e la
			// frontiera è vuota
			if(!artificialVars.contains(i)) {
				root.getAttive().add(i);
			
				IntArrayList variables = new IntArrayList();
				variables.add(i);
				HyperEdge e = new HyperEdge(i, mapping.getVarNameById(i), variables);
			
				risorse.add(e); 
			}
		}
		
		resourceHypergraph.setMapping(mapping);
		
		
//		risorse.addAll(resourceHypergraph.getHyperEdges());
		
		int k = 2;
		
		while(k <= KV) {
			computeResourcesTD(k);
			k++;
		}
		
		risorseUsate = new boolean[risorse.size()];
		
		solve(root);
		
		long tEnd = System.nanoTime() - tStart;
		
		solverTime = tEnd / 1e9;
		
		ObjectArrayList<ResourceNode> decomp = null;
		if (root.getMinWeightResourceNode() != null ) 		{	
			root.setWeight(root.getMinWeightResourceNode().getWeight());
		    decomp = buildDecomp();
			printDecompTD();
		}
		else if (root.getBestWeight() == Double.MAX_VALUE) {
			System.out.println("Impossibile decomporre :(... suggerimento: aumenta la width!");			
		}
		
		
		if (decomp == null || (root.getMinWeightResourceNode() != null && checkConditionsTD(decomp))) {

//			root.getMinWeightResource().setWeight(root.getMinWeightResource().getBestDomainValue().toReal());
						
			root.setWeight(root.getMinWeightResourceNode().getWeight());
//			printDecomp();
			
			System.out.println("OPTIMAL COST BASED TREE DECOMPOSITION FOUND!");
			  
	         System.out.println("cost                         = " + String.format("%.3f",root.getMinWeightResourceNode().getWeight()));
	         System.out.println("tree-width              = " + width);
             System.out.println("vertices                     = " + getNumberOfVertices());
             System.out.println("query atoms                  = " + noa);
             System.out.println("query variables              = " + nov);
             System.out.println("GV output written to: " + outputFileName+".gv");
             
		} 
		
		else if (root.getBestWeight() == Double.MAX_VALUE) {
			System.out.println("Impossibile decomporre :(... suggerimento: aumenta la width!");			
		}
	}
	
	/**
	 * @return
	 */
	ObjectArrayList<ResourceNode> decomp = new ObjectArrayList<>();
	
	public ObjectArrayList<ResourceNode> buildDecomp() {
		// TODO Auto-generated method stub
		
		boolean freezeRoot = false;
			
		ResourceNode decompositionRoot = root.getMinWeightResourceNode();
		
		ObjectArrayList<ResourceNode> stack = new ObjectArrayList<>();
		
		stack.add(decompositionRoot);
		
		while(stack.size() > 0) {
			
			ResourceNode currentNode = stack.remove(0);
			
			// postProcessing
			
			boolean prune = false;
			
			ResourceNode child = null;
			
			for(int i = 0; i < currentNode.getChildren().size(); i++) {
				
				child = currentNode.getChildren().get(i).getMinWeightResourceNode();
				
				if(lambdaInclusion(currentNode, child) && chiInclusion(currentNode, child)) {
					prune = true;
					break;
				}
				
			}
			
			if(prune) {
				
				for(int i = 0; i < currentNode.getChildren().size(); i++) {
					
					ResourceNode c = currentNode.getChildren().get(i).getMinWeightResourceNode();
					if(!c.equals(child))
						child.getChildren().add(currentNode.getChildren().get(i));
				}
				stack.add(child);
			}
			
			else {
			
			for(int i = 0; i < currentNode.getChildren().size(); i++) {
					
				stack.add(currentNode.getChildren().get(i).getMinWeightResourceNode());
				
			}
			
			if(!freezeRoot) {
				root.setMinWeightResourceNode(currentNode);
				root.setWeight(currentNode.getWeight());
				freezeRoot = true;
			}			
			decomp.add(currentNode); 
		  }
		}
				
		return decomp;
	}

	/**
	 * @param currentNode
	 * @param child
	 * @return
	 */
	private boolean lambdaInclusion(ResourceNode currentNode, ResourceNode child) {
		// TODO Auto-generated method stub
		
		IntArrayList currentNodeLambda = currentNode.getLambda();
		for(int i = 0; i < currentNodeLambda.size(); i++)
			if(binarySearch(child.getLambda(), currentNodeLambda.get(i)) == -1)
				return false;
			
		return true;
	}

	/**
	 * @param currentNode
	 * @param child
	 * @return
	 */
	private boolean chiInclusion(ResourceNode currentNode, ResourceNode child) {
		// TODO Auto-generated method stub
		IntArrayList currentNodeChi = currentNode.getChi();
		for(int i = 0; i < currentNodeChi.size(); i++)
			if(binarySearch(child.getChi(), currentNodeChi.get(i)) == -1)
				return false;
			
		return true;
	}

	public void compute() {
	
		long tStart = System.nanoTime();
		
//		String functionClassFullName = "userfunction.FunzioneMinMemory";
			
//		if (HYPERGRAPH_VISUALIZATION)
//			visualizeInputHyperGraphs(constraintHypergraph, mapping, tpg);
		
		root = new ComponentNode(nodesID);
		root.setUsed(true);
		
		componentNodes.add(root);
		
		for (int i = 0; i < nov(); i++)
			// nella root le variabili da attaccare sono tutte attive e la
			// frontiera è vuota
			root.getAttive().add(i);
		
		resourceHypergraph.setMapping(mapping);
		
//		root.createCurrentCombination(KV);
//		System.out.println("size pre " + resourceHypergraph.getHyperEdges().size());
//		filterSmallHedges(resourceHypergraph.getHyperEdges());
//		System.out.println("size post " +resourceHypergraph.getHyperEdges().size());
		
		
		risorse.addAll(resourceHypergraph.getHyperEdges());
		
		int k = 2;
		
		while(k <= KV) {
		  computeResources(k);
		  k++;
		}

		risorseUsate = new boolean[risorse.size()];
	    
		solve(root);
		
		long tEnd = System.nanoTime() - tStart;
		
		double elapsedTime = tEnd / 1e9;
		
		System.out.printf("Computation took %.3f secs%n", elapsedTime / 1e9);
		
		if (root.getMinWeightResourceNode() != null ) {
			buildDecomp();
			
			if(checkConditionsHD()) {
//			root.getMinWeightResource().setWeight(root.getMinWeightResource().getBestDomainValue().toReal());
						
//			root.setWeight(root.getMinWeightResourceNode().getWeight());
			printDecomp();
			
			System.out.println("OPTIMAL COST BASED HYPERTREE DECOMPOSITION FOUND!");
			  
	         System.out.println("cost                         = " + String.format("%.3f",root.getMinWeightResourceNode().getWeight()));
	         System.out.println("hypertree-width              = " + width);
             System.out.println("vertices                     = " + getNumberOfVertices());
             System.out.println("query atoms                  = " + noa);
             System.out.println("query variables              = " + nov);
             System.out.println("GV output written to: " + outputFileName+".gv");
             
			}

		} 
		
		else if (root.getBestWeight() == Double.MAX_VALUE) {
			System.out.println("Impossibile decomporre :(... suggerimento: aumenta la width!");			
		}
		
	}
	
    /**
	 * @return
	 * 
	 */
	
	public String conditionsResult() {
		return checkConditionResults;
	}
	
	public boolean checkHTDConditions() { // python interaction
		StringBuilder sb = new StringBuilder();
		/**
		 *  1. for each atom 'A' of 'atoms(Q)',
		 *     there exists 'p' of 'vertices(T)'
		 *     such that 'var(A)' is included in 'chi(p)'
		 *  2. for each variable 'Y' of 'var(Q)',
		 *     the set {'p' of 'vertices(T)' | 'Y' is included in 'chi(p)'}
		 *     induces a (connected) subtree of 'T'
		 *  3. for each vertex 'p' of 'vertices(T)',
		 *     'chi(p)' is a subset of 'var(lambda(p))'
		 *  4. for each vertex 'p' of 'vertices(T)',
		 *     ('var(lambda(p))' intersect 'chi(Tp)') is a subset of 'chi(p)'
		 */
		
		sb.append("Condition 1: ");
		
		boolean ok1 = true;
		
		int atomID = -1;
		
		for(int i = 0; i < constraintHypergraph.getHyperEdges().size() && ok1; i++) {
			
			ok1 = false;
			
			IntArrayList varsA = constraintHypergraph.getHyperEdges().get(i).getVariables();
			
			for(int j = 0; j < decomp.size(); j++)
				if(isIncluded(varsA, decomp.get(j).getChi())) {
					ok1 = true;
					break;
				}
			
			if(!ok1) 
				atomID = constraintHypergraph.getHyperEdges().get(i).getId();
			
		}
		
		if(!ok1) {
			sb.append("violated. Check atom " + mapping.getAtomNameById(atomID));
			return false;
		}
		
		sb.append("satisfied\n");
		
		sb.append("Condition 2: ");
		
		boolean ok2 = true;
		
		int varID = -1;
		
		for(int i = 0; i < constraintHypergraph.getVars().size() && ok2; i++) {
			
			int var = constraintHypergraph.getVars().get(i);
			
			ResourceNode rootResource = findRoot(root.getMinWeightResourceNode(), var);
			
			ok2 = checkSubTree(rootResource, var, true, false);
			
			if(!ok2)
				varID = var;
		}	
		
		if(!ok2) {
			sb.append("violated. Check var " + mapping.getVarNameById(varID));
			return false;
		}
		
		sb.append("satisfied\n");
		
		sb.append("Condition 3: ");
		
		boolean ok3 = true;
		
		int idHypertreeNode = -1;
		
		for(int j = 0; j < decomp.size() && ok3; j++) {
			
			IntArrayList chiP = decomp.get(j).getChi();
			
			IntArrayList lambdaP = decomp.get(j).getLambda();
			
			if(lambdaP.size() > width)
				width = lambdaP.size();
			
			IntHashSet chiLambdaP = new IntHashSet();
			
			for(int i = 0; i < lambdaP.size(); i++)  {
				for(int k = 0; k < constraintHypergraph.getHyperEdges().size(); k++) {
					HyperEdge he = constraintHypergraph.getHyperEdges().get(k);
					if(he.getId() == lambdaP.get(i)) {
						chiLambdaP.addAll(he.getVariables());
						break;
					}
				}
			}
			
			for(int i = 0; i < chiP.size(); i++)
				if(!chiLambdaP.contains(chiP.get(i))) {
					ok3 = false;
					idHypertreeNode = decomp.get(j).getId();
					break;
				}	
		}
		
		if(!ok3) {
			sb.append("violated. Check hypertreeNode " + idHypertreeNode);
			return false;
		}
		
		sb.append("satisfied\n");
		
		sb.append("Condition 4: ");
		
		
		boolean ok4 = true;
		
		for(int j = 0; j < decomp.size() && ok4; j++) {
			
			IntArrayList chiP = decomp.get(j).getChi();
			
			IntArrayList lambdaP = decomp.get(j).getLambda();
			
			IntHashSet chiLambdaP = new IntHashSet();
			
			for(int i = 0; i < lambdaP.size(); i++)  {
				for(int k = 0; k < constraintHypergraph.getHyperEdges().size(); k++) {
					HyperEdge he = constraintHypergraph.getHyperEdges().get(k);
					if(he.getId() == lambdaP.get(i)) {
						chiLambdaP.addAll(he.getVariables());
						break;
					}
				}
			}
			
			IntHashSet chiTp = new IntHashSet();
			
			computeChiTp(decomp.get(j), chiTp);
			
			chiTp.retainAll(chiLambdaP);
			
			Iterator<IntCursor> x = chiTp.iterator();
			
			while(x.hasNext()) {
				if(!chiP.contains(x.next().value)) {
					ok4 = false;
					break;
				}				
			}
		}
		
		if(!ok4) {
			sb.append("violated.");
			return false;
		}
		
		sb.append("satisfied\n");
		
		checkConditionResults = sb.toString();
		return true;
	}
	
	private boolean checkConditionsHD() {
		
		/**
		 *  1. for each atom 'A' of 'atoms(Q)',
		 *     there exists 'p' of 'vertices(T)'
		 *     such that 'var(A)' is included in 'chi(p)'
		 *  2. for each variable 'Y' of 'var(Q)',
		 *     the set {'p' of 'vertices(T)' | 'Y' is included in 'chi(p)'}
		 *     induces a (connected) subtree of 'T'
		 *  3. for each vertex 'p' of 'vertices(T)',
		 *     'chi(p)' is a subset of 'var(lambda(p))'
		 *  4. for each vertex 'p' of 'vertices(T)',
		 *     ('var(lambda(p))' intersect 'chi(Tp)') is a subset of 'chi(p)'
		 */
		
		System.out.println("Checking conditions...");
		
		System.out.print("Condition 1: ");
		
		boolean ok1 = true;
		
		int atomID = -1;
		
		for(int i = 0; i < constraintHypergraph.getHyperEdges().size() && ok1; i++) {
			
			ok1 = false;
			
			IntArrayList varsA = constraintHypergraph.getHyperEdges().get(i).getVariables();
			
			for(int j = 0; j < decomp.size(); j++)
				if(isIncluded(varsA, decomp.get(j).getChi())) {
					ok1 = true;
					break;
				}
			
			if(!ok1) 
				atomID = constraintHypergraph.getHyperEdges().get(i).getId();
			
		}
		
		if(!ok1) {
			System.out.println("violated. Check atom " + mapping.getAtomNameById(atomID));
			return false;
		}
		
		System.out.println("satisfied");
		
		System.out.print("Condition 2: ");
		
		boolean ok2 = true;
		
		int varID = -1;
		
		for(int i = 0; i < constraintHypergraph.getVars().size() && ok2; i++) {
			
			int var = constraintHypergraph.getVars().get(i);
			
			ResourceNode rootResource = findRoot(root.getMinWeightResourceNode(), var);
			
			ok2 = checkSubTree(rootResource, var, true, false);
			
			if(!ok2)
				varID = var;
		}	
		
		if(!ok2) {
			System.out.println("violated. Check var " + mapping.getVarNameById(varID));
			return false;
		}
		
		System.out.println("satisfied");
		
		System.out.print("Condition 3: ");
		
		boolean ok3 = true;
		
		int idHypertreeNode = -1;
		
			for(int j = 0; j < decomp.size() && ok3; j++) {
				
				IntArrayList chiP = decomp.get(j).getChi();
				
				IntArrayList lambdaP = decomp.get(j).getLambda();
				
				if(lambdaP.size() > width)
					width = lambdaP.size();
				
				IntHashSet chiLambdaP = new IntHashSet();
				
				for(int i = 0; i < lambdaP.size(); i++)  {
					for(int k = 0; k < constraintHypergraph.getHyperEdges().size(); k++) {
						HyperEdge he = constraintHypergraph.getHyperEdges().get(k);
						if(he.getId() == lambdaP.get(i)) {
							chiLambdaP.addAll(he.getVariables());
							break;
						}
					}
				}
				
				for(int i = 0; i < chiP.size(); i++)
					if(!chiLambdaP.contains(chiP.get(i))) {
						ok3 = false;
						idHypertreeNode = decomp.get(j).getId();
						break;
					}	
			}
			
			if(!ok3) {
				System.out.println("violated. Check hypertreeNode " + idHypertreeNode);
				return false;
			}
			
			System.out.println("satisfied");
			
			System.out.print("Condition 4: ");
			
			
			boolean ok4 = true;
			
			for(int j = 0; j < decomp.size() && ok4; j++) {
				
				IntArrayList chiP = decomp.get(j).getChi();
				
				IntArrayList lambdaP = decomp.get(j).getLambda();
				
				IntHashSet chiLambdaP = new IntHashSet();
				
				for(int i = 0; i < lambdaP.size(); i++)  {
					for(int k = 0; k < constraintHypergraph.getHyperEdges().size(); k++) {
						HyperEdge he = constraintHypergraph.getHyperEdges().get(k);
						if(he.getId() == lambdaP.get(i)) {
							chiLambdaP.addAll(he.getVariables());
							break;
						}
					}
				}
				
				IntHashSet chiTp = new IntHashSet();
				
				computeChiTp(decomp.get(j), chiTp);
				
				chiTp.retainAll(chiLambdaP);
				
				Iterator<IntCursor> x = chiTp.iterator();
				
				while(x.hasNext()) {
					if(!chiP.contains(x.next().value)) {
						ok4 = false;
						break;
					}				
				}
			}
			
			if(!ok4) {
				System.out.println("violated.");
				return false;
			}
			
			System.out.println("satisfied");
			
		 
		
		return true;
	}
	
	private boolean checkConditionsTD(ObjectArrayList<ResourceNode> decomp) {
				
		/**
		 *  1. for each variable 'Y' of 'var(Q)', there exists 'p' of 'vertices(T)'
		 *     such that 'Y' is included in 'chi(p)'
		 *     
		 *  2. for each atom 'a' of 'atoms(Q)', there exists 'p' of 'vertices(T)'
		 *     such that 'vars(a)' is included in 'chi(p)'
		 *      
		 *  3. for each variable 'Y' of 'var(Q)',
		 *     the set {'p' of 'vertices(T)' | 'Y' is included in 'chi(p)'}
		 *     induces a (connected) subtree of 'T'
		 */
		
		System.out.println("Checking conditions...");
		
		System.out.print("Condition 1: ");
		
		boolean ok1 = true;
		
		int varID = -1;
		
		for(int i = 0; i < nov; i++) {
			varID = i;
			ok1 = false;
			for(int j = 0; j < decomp.size(); j++)
				if(decomp.get(j).getChi().contains(i)) {
					ok1 = true;
					break;
				}
		}
		
		if(!ok1) {
			System.out.println("violated. Check var " + mapping.getVarNameById(varID));
			  return false;
		}
		
		System.out.println("satisfied");
		
		System.out.print("Condition 2: ");
		
		boolean ok2 = true;
		
		int atomID = -1;
		
		for(int i = 0; i < constraintHypergraph.getHyperEdges().size() && ok2; i++) {
			
			ok2 = false;
			
			IntArrayList varsA = constraintHypergraph.getHyperEdges().get(i).getVariables();
			
			for(int j = 0; j < decomp.size(); j++)
				if(isIncluded(varsA, decomp.get(j).getChi())) {
					ok2 = true;
					break;
				}
			
			if(!ok2) 
				atomID = constraintHypergraph.getHyperEdges().get(i).getId();
			
		}
		
		if(!ok2) {
			System.out.println("violated. Check atom " + mapping.getAtomNameById(atomID));
			return false;
		}
		
		System.out.println("satisfied");
		
		System.out.print("Condition 3: ");
		
		boolean ok3 = true;
		
	
		
		for(int i = 0; i < constraintHypergraph.getVars().size() && ok3; i++) {
			
			int var = constraintHypergraph.getVars().get(i);
			
			ResourceNode rootResource = findRoot(root.getMinWeightResourceNode(), var);
			
			ok3 = checkSubTree(rootResource, var, true, false);
			
			if(!ok3)
				varID = var;
		}	
		
		if(!ok3) {
			System.out.println("violated. Check var " + mapping.getVarNameById(varID));
			return false;
		}

		System.out.println("satisfied");
		
		return true;
	}
	
	public int getNumberOfVertices() {
		return nDecompVertices;
	}
	
	
	/**
	 * @param resourceNode
	 * @param chiTp
	 */
	private void computeChiTp(ResourceNode resourceNode, IntHashSet chiTp) {
		// TODO Auto-generated method stub
		
		chiTp.addAll(resourceNode.getChi());
		
		for (int i = 0; i < resourceNode.getChildren().size(); i++) {

			ComponentNode childID = resourceNode.getChildren().get(i);
	
			ResourceNode child = childID.getMinWeightResourceNode();
			
			computeChiTp(child, chiTp);
		}
		
	}

	/**
	 * @param minWeightResourceNode
	 * @return
	 */
	private ResourceNode findRoot(ResourceNode rootNode, int var) {
		// TODO Auto-generated method stub
		
		if (rootNode.getChi().contains(var))
			return rootNode;
		
		for(int i = 0; i < rootNode.getChildren().size(); i++) {
			ResourceNode r =  findRoot(rootNode.getChildren().get(i).getMinWeightResourceNode(), var);
			if(r!=null)
				return r;
		}
			
		return null;
	}

	private boolean checkSubTree(ResourceNode root, int v, boolean start, boolean end) {
		
		if (root.getChildren().size() == 0)
			return true;
		
		boolean oldStart = start;
		boolean oldEnd = end;
		
		for (int i = 0; i < root.getChildren().size(); i++) {

			ComponentNode childID = root.getChildren().get(i);
			start = oldStart;
			end = oldEnd;
			ResourceNode child = childID.getMinWeightResourceNode();
//			for (int j = 0; j < ht.getHypertreeNodes().size(); j++) {
				
//				HNode child = ht.getHypertreeNodes().get(j);
				
//				if (child.getId() == childID) {
					
					boolean trovato = child.getChi().contains(v);//Arrays.binarySearch(child.getChi().buffer, v) >= 0;
					
					if (!start && trovato)
						  start = true;
					else if(start && !trovato)
						end = true;
					
					else if(end && trovato)
						return false;
					
					boolean sat = checkSubTree(child, v, start, end);
					
					if(!sat)
						return false;
					
//					break;
				}
//			}
//		}
		
		return true;
	}

/**
	 * @param varsA
	 * @param chi
	 * @return
	 */
	private boolean isIncluded(IntArrayList varsA, IntArrayList chi) {
		// TODO Auto-generated method stub
		
		int i = 0;
		int j = 0;
		
		while(i < varsA.size() && j < chi.size()) {
			
			if(varsA.get(i) == chi.get(j)) {
				i++;
				j++;
			}
			else if(varsA.get(i) < chi.get(j)) {
				i++;
			}
			else
				j++;			
		}
						
		return i == varsA.size();
	}

	/**
	 * Usa l'algoritmo di combinazioni di Knuth per generare le k-uple
	 */
	
	private void computeResources(int k) {
		
		  if (noa == k) { // se k >= numberOfAtoms generiamo un kvertex che contiene tutte le variabili dell'ipergrafo
				
			  HyperEdge he = new HyperEdge(risorse.get(risorse.size() - 1).getId() + 1, new IntArrayList());
				
			  for (int z = 0; z < nov; z++)
				 he.getVariables().add(z);
				
			  String name = "{";
				
			  for(int i = 0; i < noa - 1; i++)
				 name+=mapping.getAtomNameById(i)+",";
				
			  name+=mapping.getAtomNameById(noa-1)+"}";
				
			  he.setName(name);
				
			  risorse.add(he);
				
			  return ;
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

			
//			System.out.println(id);

			for (;; j--) {		
				int id = risorse.get(risorse.size() - 1).getId() + 1;
				HyperEdge he = new HyperEdge(id, new IntArrayList());
				String name = "{";
				StringBuilder sb = new StringBuilder();
				
				for (i = k; i >= 1; i--) { // qui vanno create le risorse.
					
					int heID = c[i] - 1;

					if (i > 1)
						sb.append(mapping.getAtomMap().get(heID) + ",");
					else
						sb.append(mapping.getAtomMap().get(heID));

					he.setNoa(he.getNoa() + 1); // sto usando più atomi ---> strano, a che serve?

					for (int i1 = 0; i1 < resourceHypergraph.getHyperEdges().size(); i1++) {

						if (resourceHypergraph.getHyperEdges().get(i1).getId() == heID)
							for (int j1 = 0; j1 < resourceHypergraph.getHyperEdges().get(i1).getVariables()
									.size(); j1++)
								if (!he.getVariables().contains(
										resourceHypergraph.getHyperEdges().get(i1).getVariables().get(j1)))
									he.getVariables().add(
											resourceHypergraph.getHyperEdges().get(i1).getVariables().get(j1));
					}
				}

				String[] lista = sb.toString().split(",");

				for (int w = lista.length - 1; w >= 0; w--)
					if (w == 0)
						name += lista[w] + "}";
					else
						name += lista[w]+ ",";

				he.setName(name);
				he.getVariables().trimToSize();
				Arrays.parallelSort(he.getVariables().buffer);
				risorse.add(he);

				// System.out.println();
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

			// vincoli.addAll(augmentedResource);	
	}
	private void computeResourcesTD(int k) {
		
		if (nov == k) { // se k >= numberOfVars generiamo un kvertex che contiene tutte le variabili dell'ipergrafo
			
			HyperEdge he = new HyperEdge(risorse.get(risorse.size() - 1).getId() + 1, new IntArrayList());
			
			for (int z = 0; z < nov; z++)
				if(!DatalogParser.getArtificialVars().contains(z))
					he.getVariables().add(z);
			
			String name = "{";
			
			for(int i = 0; i < nov - 1; i++)
			 if(!DatalogParser.getArtificialVars().contains(i))
				name+=mapping.getVarNameById(i)+",";
			if(!DatalogParser.getArtificialVars().contains(nov-1))
			name+=mapping.getVarNameById(nov-1)+"}";
			
			he.setName(name);
			
			risorse.add(he);
			
			return ;
		}
		
		int i;
		int[] c = new int[k + 3];
		int j = 1;
		int x = 0;
		
		for (i = 1; i <= k; i++) {
			c[i] = i;
			c[k + 1] = nov + 1;
			c[k + 2] = 0;
			j = k;
		}
		
//			System.out.println(id);
		
		for (;; j--) {		
			int id = risorse.get(risorse.size() - 1).getId() + 1;
			HyperEdge he = new HyperEdge(id, new IntArrayList());
			String name = "{";
			StringBuilder sb = new StringBuilder();
			
			for (i = k; i >= 1; i--) { // qui vanno create le risorse.
				
				int heID = c[i] - 1;
				
				if (i > 1)
					sb.append(mapping.getVarsMap().get(heID) + ",");
				else
					sb.append(mapping.getVarsMap().get(heID));
				
				he.setNoa(he.getNoa() + 1); // sto usando più atomi ---> strano, a che serve?
				
				he.getVariables().add(heID);
//				for (int i1 = 0; i1 < resourceHypergraph.getHyperEdges().size(); i1++) {
//					
//					if (resourceHypergraph.getHyperEdges().get(i1).getId() == heID)
//						for (int j1 = 0; j1 < resourceHypergraph.getHyperEdges().get(i1).getVariables().size(); j1++)
//							if (!he.getVariables().contains(resourceHypergraph.getHyperEdges().get(i1).getVariables().get(j1)))
//								he.getVariables().add(resourceHypergraph.getHyperEdges().get(i1).getVariables().get(j1));
//				}
			}
			
			String[] lista = sb.toString().split(",");
			
			for (int w = lista.length - 1; w >= 0; w--)
				if (w == 0)
					name += lista[w] + "}";
				else
					name += lista[w]+ ",";
			
			he.setName(name);
			he.getVariables().trimToSize();
			Arrays.parallelSort(he.getVariables().buffer);
			risorse.add(he);
			
			// System.out.println();
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
		
		// vincoli.addAll(augmentedResource);	
	}

	private int nDecompVertices;
	
	

	public void printDecompTD() {
		
		ResourceNode decompositionRoot = root.getMinWeightResourceNode();
		
		File file = new File(outputFileName+".gv");
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("graph  {\n");
		IntArrayList artificialVars = DatalogParser.getArtificialVars(); // brutto
		
		ObjectArrayList<ResourceNode> stack = new ObjectArrayList<>();
		
		stack.add(decompositionRoot);
		
		while(stack.size() > 0) {
			
			ResourceNode currentNode = stack.remove(0);
			
			for(int i = 0; i < currentNode.getChildren().size(); i++) {
				stack.add(currentNode.getChildren().get(i).getMinWeightResourceNode());
			}
			
//			String lambda = "{";
//			
//			for(int j = 0; j < currentNode.getLambda().size() - 1; j++)
//				lambda += mapping.getVarNameById(currentNode.getLambda().get(j))+", ";
//			lambda += mapping.getVarNameById(currentNode.getLambda().get(currentNode.getLambda().size() - 1));
//			lambda += "}";
			
			String chi = " {";
			for(int j = 0; j < currentNode.getChi().size() - 1; j++)
				if(!artificialVars.contains(currentNode.getChi().get(j)))
					chi += mapping.getVarNameById(currentNode.getChi().get(j))+", ";
			if(!artificialVars.contains(currentNode.getChi().get(currentNode.getChi().size() - 1)))
				chi += mapping.getVarNameById(currentNode.getChi().get(currentNode.getChi().size() - 1));
			else
				chi = chi.substring(0, chi.length() - 2);
			chi += "}";
			
			addNode(sb, currentNode.getId(), chi);
//			addNode(sb, currentNode.getId(), lambda + chi + "\n$"+ String.format( "%.3f", currentNode.getWeight()));
		}
		
		stack.add(decompositionRoot);
		
		while(stack.size() > 0) {
			
			ResourceNode currentNode = stack.remove(0);
			
			for(int i = 0; i < currentNode.getChildren().size(); i++)  {
				ResourceNode children = currentNode.getChildren().get(i).getMinWeightResourceNode(); 
				stack.add(children);
				addEdge(sb, currentNode.getId(), children.getId(), "\"\"");
			}
		}
		sb.append("}");
		
		try {
			Files.write(FileSystems.getDefault().getPath(".", outputFileName+".gv"), sb.toString().getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void printDecomp() {
			
//		ResourceNode decompositionRoot = root.getMinWeightResourceNode();
		
		File file = new File(outputFileName+".gv");
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("graph  {\n");
		
		IntArrayList artificialVars = DatalogParser.getArtificialVars(); // brutto
		
//		ObjectArrayList<ResourceNode> stack = new ObjectArrayList<>();
//		
//		stack.add(decompositionRoot);
		
		int ii = 0;
		
		while(ii < decomp.size()) {
//		while(stack.size() > 0) {
			
			ResourceNode currentNode = decomp.get(ii);
			
//			ResourceNode currentNode = stack.remove(0);
			
//			for(int i = 0; i < currentNode.getChildren().size(); i++) {
//				stack.add(currentNode.getChildren().get(i).getMinWeightResourceNode());
//			}
			
			String lambda = "{";
			
			for(int j = 0; j < currentNode.getLambda().size() - 1; j++)
				lambda += mapping.getAtomNameById(currentNode.getLambda().get(j))+", ";
			lambda += mapping.getAtomNameById(currentNode.getLambda().get(currentNode.getLambda().size() - 1));
			lambda += "}";
		
			String chi = " {";
			for(int j = 0; j < currentNode.getChi().size() - 1; j++)
				if(!completeDecomposition || !artificialVars.contains(currentNode.getChi().get(j)))
					chi += mapping.getVarNameById(currentNode.getChi().get(j))+", ";
			if(!completeDecomposition || !artificialVars.contains(currentNode.getChi().get(currentNode.getChi().size() - 1)))
				chi += mapping.getVarNameById(currentNode.getChi().get(currentNode.getChi().size() - 1));
			else
				chi = chi.substring(0, chi.length() - 2);
			chi += "}";
	
			addNode(sb, currentNode.getId(), lambda + chi + "\n$"+  String.format( "%.3f", currentNode.getWeight()));
//			addNode(sb, currentNode.getId(), lambda + chi + "\n$"+ String.format( "%.3f", currentNode.getWeight()));
			ii++;
		}
		
//		stack.add(decompositionRoot);
		ii = 0;
		while(ii < decomp.size()) {
//		while(stack.size() > 0) {
			
			ResourceNode currentNode = decomp.get(ii);
//			ResourceNode currentNode = stack.remove(0);
			
			for(int i = 0; i < currentNode.getChildren().size(); i++)  {
				ResourceNode children = currentNode.getChildren().get(i).getMinWeightResourceNode(); 
//				stack.add(children);
				addEdge(sb, currentNode.getId(), children.getId(), "\"\"");
			}
			ii++;
		}
		sb.append("}");
		
		try {
			Files.write(FileSystems.getDefault().getPath(".", outputFileName+".gv"), sb.toString().getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void addNode(StringBuilder sb, int id, String name) {
		// TODO Auto-generated method stub
		nDecompVertices++;
		
		sb.append("	 "+id+"	  [label=\""+name+"\",\n" +
				   "         vgj=\"[ labelPosition \\\"in\\\" shape \\\"Rectangle\\\" ]\"];\n");
	
	}
	
	/**
	 * @param string
	 */
	private void addEdge(StringBuilder sb, int id1, int id2, String label) {
		// TODO Auto-generated method stub
		sb.append("  "+id1+" -- "+id2+"	   [label="+label+"];\n");
	}
	
	
	public  void shuffle(Object[] a) {
	    int n = a.length;
	    for (int i = 0; i < n; i++)
	    {
	        // between i and n-1
	        int r = i + (int) (Math.random() * (n-i));
	        Object tmp = a[i];    // swap
	        a[i] = a[r];
	        a[r] = tmp;
	    }
	}
	
	
	private int[] countingSort(int[] weights, int maxWeight) {

		int N = weights.length;

		int[] count = new int[maxWeight + 1];

		for (int i = 0; i < N; i++) { // freq. count, stored shifted for
										// stability
			count[weights[i]]++;
		}

		// for (int r = 0; r < maxWeight; r++) {// cum. freq.
		// count[r + 1] += count[r];
		// }

		count[0]--;
		for (int i = 1; i < count.length; i++) {
			count[i] = count[i] + count[i - 1];
		}

		// Sort the array right to the left
		// 1) look up in the array of occurences the last occurence of the given
		// value
		// 2) place it into the sorted array
		// 3) decrement the index of the last occurence of the given value
		// 4) continue with the previous value of the input array (goto: 1),
		// terminate if all values were already sorted

		
		
		int[] ans = new int[N];

		for (int i = weights.length - 1; i >= 0; i--) {
			ans[count[weights[i]]--] = i;
		}

		// for (int i = 0; i < N; i++) {// move
		// ans[count[weights[i]]] = i;
		// }

		return ans;
	}
	
	
 public boolean cover(IntArrayList x, IntArrayList y) {
		
		int counter = 0;
		int i = 0;
		int j = 0;
		while( i < x.size() && j < y.size()) {
			
			if(x.get(i) < y.get(j))
				i++;
			else if(x.get(i) > y.get(j))
				j++;
			else {
				counter++;
				i++;
				j++;
			}
		}
//		usatePerAttaccare = counter;
		return counter == y.size();
	}
	

 	public void solve(ComponentNode cNode) {

//		ObjectArrayList<HyperEdge> boundEdges = getBoundEdges(risorse, cNode.getFrontiera(), weights); // risorse incidenti alla frontiera e ordinate per numero di nodi incidenti

		int idLastUsed = -1;
		
		for(int i1 = 0; i1 < risorse.size(); i1++) {
			
			HyperEdge h = risorse.get(i1);
			
			if(risorseUsate[h.getId()])
			   continue;
			
			IntArrayList e = h.getVariables();
			
			if(!cover(e, cNode.getFrontiera()))
				continue;
			
			IntArrayList hr = h.intersect1(cNode.getAttive());
			
			if(hr.size() > 0) 
				hr = resourceHypergraph.union(hr, cNode.getFrontiera());
			
			else 
				continue;
			
			risorseUsate[h.getId()] = true;
			
			if(idLastUsed != -1)
				risorseUsate[idLastUsed] = false;
			
			idLastUsed = h.getId();
			
				ResourceNode resourceNode = new ResourceNode(h.getName(), nodesID);
				
				resourceNode.setChi(hr);
				
				resourceNode.setWeightingFunction(weightFunction);

				String[] lambda = h.getName().split("[{},]");
				
				for(int i = 1; i < lambda.length; i++)
				resourceNode.getLambda().add(mapping.getAtomID(lambda[i]));
				
				ObjectArrayList<HyperGraph> componenti = constraintHypergraph.calcolaComponentiConnesse(hr, cNode.getAttive());
				
				for (int i = 0; i < componenti.size(); i++) {
					
					HyperGraph hg = componenti.get(i);
					if (hg.getVars().size() == 0) { // non ci sono componenti...
						
						System.err.println("La componente " + hg + " non coniente variabili!");
						resourceNode.setWeight(Double.MAX_VALUE);
						break;
					}
						
					else {

						IntArrayList frontiera = calcolaFrontiera(hr, hg.getVars());
					
						IntArrayList attive = hg.getVars();
						
						ComponentNode componentNode = null;

						if (COMPONENT_REUSE) {
							
							final int prime = 19;
							
							int hash = prime  + ((attive == null) ? 0 : attive.hashCode());
							hash = prime * hash + ((frontiera == null) ? 0 : frontiera.hashCode());
											
							componentNode = cacheComponentNodes.get(hash);
								
							if(componentNode == null)	{
								nodesID++;
								componentNode = new ComponentNode(nodesID);
								componentNode.setAttive(hg.getVars());
								componentNode.setFrontiera(frontiera);
//								componentNode.createCurrentCombination(KV);
								cacheComponentNodes.put(componentNode.hashCode(), componentNode);
								componentNodes.add(componentNode); // backward compatibility
								
							}
							
							else {
								if(componentNode.getBestWeight() < Double.MAX_VALUE) {
									resourceNode.getChildren().add(componentNode);
//									resourceNode.incrOutDegree();
//									componentNode.getPredecessors().add(resourceNode);
//									componentNode.incrInDegree();
									continue;
								}
								else {
									resourceNode.setWeight(Double.MAX_VALUE);
									break;
								}
							}
							
						}

						if(componentNode.getBestWeight() == Double.MAX_VALUE) { // a questo punto mi pare scontato... se arrivamo qui
							
							solve(componentNode); // chiamata ricorsiva... visita in profondità!
							
							if(componentNode.getWeight() == Double.MAX_VALUE) {
								resourceNode.setWeight(Double.MAX_VALUE);
								break;
							}
							
							resourceNode.getChildren().add(componentNode);
							
						 }
					}
				}
				
				if(resourceNode.getChildren().size() != componenti.size()) {
//					cNode.setWeight(Double.MAX_VALUE);
					for(int k = 0 ; k < resourceNode.getChildren().size(); k++)
						componentNodes.removeFirst(resourceNode.getChildren().get(k));

					resourceNode = null;	
					continue;
				}
				
				else {
						resourceNodes.add(resourceNode);				
						resourceNode.computeWeight();
				
						cNode.getChildren().put(resourceNode, resourceNode.getWeight());
					
						if (cNode.getMinWeightResourceNode() == null) {
							
							cNode.setWeight(resourceNode.getWeight());
							cNode.setBestWeight(resourceNode.getWeight());
							cNode.setMinWeightResourceNode(resourceNode);							
					
						} else if( cNode.getBestWeight() > resourceNode.getWeight()) {
							cNode.setWeight(resourceNode.getWeight());
							cNode.setBestWeight(resourceNode.getWeight());
							cNode.setMinWeightResourceNode(resourceNode);
						}
				}
		 } 
	}
	
	public int noIntersect(IntArrayList lista1, IntArrayList lista2 ) {
		int i = 0;
		int j = 0;
		int count = 0;
		while( i < lista1.size() && j < lista2.size()) {
			
			if( lista1.get(i) < lista2.get(j)) {
				count++;
				i++;
			}
			else if(lista1.get(i) > lista2.get(j)){
//				remove.add(lista2.get(j));
				j++;
			}
			else {
//				remove.add(lista2.get(j));
				i++;
				j++;
			}
		}
		while( i < lista1.size()) {
			count++;
			i++;
		}
//		while( j < lista2.size()) {
//			remove.add(lista2.get(j));
//			j++;
//		}
//		System.out.println();
		return count;
	}

	
}

