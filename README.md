# CostKdecomp
An optimal cost-based algorithm (cost-k-decomp) for optimal hypertree decompositions inspired by https://dl.acm.org/doi/10.1145/1055558.1055587

Please note that I've developed a simple python script (please check my github repos) which wraps the main algorithm to allow for an easier usage. 
By default this script computes and optimal hypertree decomposition using a structural cost function, that is, it exploits only information about the hypergraph representation.

However, for those situations where we have further information at hand on the given problem, for instance consider concrete contexts like database query answering where we can use statistics such as the size of relations, the selectivity of attributes, indexes, etc., we have built a custom cost (or weighting) function which is able to exploit such statistics in the computation of the Hypertree Decomposition which will improve the runtimes of join algorithms. This cost function is called GLSFunction. 

The nice thing here is that everyone can contribute to this project by building a new cost function which will be "automatically" loaded in the framework by using Java Reflection. 

For details about how those cost functions work and for insight on how to build them please have a look at Section 4.5 of my PhD thesis https://scholar.google.it/citations?view_op=view_citation&hl=it&user=icnAjqQAAAAJ&citation_for_view=icnAjqQAAAAJ:Y0pCki6q_DkC


If there is enough interest in this tool we may consider to add new contributions.  
