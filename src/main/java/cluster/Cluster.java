package cluster;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;

import java.util.Set;

public class Cluster<V,E> extends AsSubgraph<V,E>  {
    private static int clustID  = 0;
    private int id;
    public Cluster(Graph<V,E> base){
        super(base);
        assignID();
    }
    public static synchronized int assignID(){
        int id_to_return = clustID;
        clustID++;
        return id_to_return;
    }
    public static synchronized void setClustID(int newID){
        clustID = newID;
    }
    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset){
        super(base,vertexSubset);
        id = assignID();
    }

    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset){
        super(base,vertexSubset,edgeSubset);
        id = assignID();
    }

    public Cluster(Graph<V,E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset,Integer man_id){
        super(base,vertexSubset,edgeSubset);
        id = man_id;
        setClustID(man_id+1);
    }

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
}
