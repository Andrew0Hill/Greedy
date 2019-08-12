package cluster;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;

import java.util.Set;

public class Cluster<V,E> extends AsSubgraph<V,E>  {
    private static int clustID  = 0;
    private int id;
    public Cluster(Graph<V,E> base){
        super(base);
        id = clustID++;
    }

    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset){
        super(base,vertexSubset);
        id = clustID++;
    }

    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset){
        super(base,vertexSubset,edgeSubset);
        id = clustID++;
    }

    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset,Integer man_id){
        super(base,vertexSubset,edgeSubset);
        id = man_id;
        clustID = man_id+1;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
}
