package algorithms;

import cluster.Cluster;
import cluster.ExclusiveClustering;
import data.RecordVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class GreedyIncrementalLinkage<V,E> implements ExclusiveClustering<V> {
    // Holds reference to the backend graph
    private SimpleWeightedGraph<V, E> initial_graph;
    // The working set contains all of the clusters we are currently considering in the clustering.
    private Queue<Cluster<V,E>> working_set;
    // The cluster ID map maps a RecordVertex to Cluster.
    private HashMap<V,Cluster<V,E>> cluster_id_map;
    // Set to hold all of the clusters we are not currently considering.
    private Set<Cluster<V,E>> finished_set;

    // This constructor handles running incremental linkage where the initial linkage has not already been performed.
    // it performs the initial linkage internally.
    public GreedyIncrementalLinkage(SimpleWeightedGraph<V, E> initial_graph){
        this.working_set = new LinkedBlockingQueue<>();
        this.initial_graph = initial_graph;
        this.cluster_id_map = new HashMap<>();
        this.finished_set = new HashSet<>();
        runInitialLinkage();
    }

    // This constructor handles running incremental linkage on a dataset where the initial linkage already exists
    public GreedyIncrementalLinkage(SimpleWeightedGraph<V,E> initial_graph,HashMap<V,Cluster<V,E>> cluster_labels){
        this.working_set = new LinkedBlockingQueue<>();
        this.initial_graph = initial_graph;
        this.cluster_id_map = cluster_labels;
        this.finished_set = new HashSet<>();
        // Add all initial clusters to the finished set.
        this.finished_set.addAll(cluster_labels.values());
    }

    // Computes the correlation clustering penalty on the current working set.
    private double computeGlobalPenalty(){
        return computeLocalPenalty(this.initial_graph,this.cluster_id_map);
    }

    private double computeLocalPenalty(Graph<V,E> graph, HashMap<V,Cluster<V,E>> labels){
        double penalty = 0;
        for(E edge : graph.edgeSet()){
            V src = graph.getEdgeSource(edge);
            V trgt = graph.getEdgeTarget(edge);

            int src_lbl = labels.get(src).getId();
            int trgt_lbl = labels.get(trgt).getId();

            if(src_lbl == trgt_lbl){
                penalty += 1-graph.getEdgeWeight(edge);
            }else{
                penalty += graph.getEdgeWeight(edge);
            }
        }
        return penalty;
    }

    public void runIncrementalLinkage(Set<V> vertices){

        // This set is used so that we don't insert multiple references to a single Cluster<> into the
        // working queue. The HashSet will take care of any duplicates transparently.
        Set<Cluster<V,E>> temp_working_set = new HashSet<>();

        // Make new singleton clusters for all new vertices
        for(V vert:vertices){
            // Vertex set.
            Set<V> sngl = new HashSet<>();
            sngl.add(vert);
            // Add a reference in the cluster map.
            Cluster<V,E> new_clust = new Cluster<>(initial_graph,sngl);
            cluster_id_map.put(vert,new_clust);
            // By default, all new clusters should go into the working queue.
            temp_working_set.add(new_clust);
        }

        for(V vert : vertices){
            // Get the set of edges that are connected to this one.
            Set<E> directly_connected = initial_graph.outgoingEdgesOf(vert);
            // For each of these edges, get:
            // 1. The associated V vertex
            // 2. The Cluster assignment of this Vertex.
            for(E edge : directly_connected){
                // This graph is undirected, so we aren't sure whether the neighbor node is the "source" or "target" vertex.
                // We check to make sure that we are assigning the neighbor vertex that is not equal to 'vert'
                V neig_vert = initial_graph.getEdgeSource(edge) != vert ? initial_graph.getEdgeSource(edge) : initial_graph.getEdgeTarget(edge);
                // Add this vertex's cluster to the temp_working_set
                temp_working_set.add(cluster_id_map.get(neig_vert));
            }
        }

        // At this point we should have the clusters that are directly connected to the increment in
        // temp_working_set. We can now add these to the working set and run the clustering.
        working_set.addAll(temp_working_set);
        // We also need to remove any instances of these clusters from the finished set, because not doing so will
        // cause duplicate references. Any Clusters that are in temp_working but not finished_set will not be removed.
        finished_set.removeAll(temp_working_set);

        System.out.println("There are " + this.working_set.size() + " clusters before clustering.");
        System.out.println("Current Global Penalty: " + computeGlobalPenalty());

        while(!working_set.isEmpty()){
            Cluster<V,E> cur_clust = working_set.remove();
            boolean changed = false;
            changed = merge(cur_clust);
            if(!changed)
                changed = split(cur_clust);
            if(!changed)
                changed = move(cur_clust);
            if(!changed){
                System.out.println("Removing Cluster" + cur_clust.getId() + " from the working set.");
                this.finished_set.add(cur_clust);
            }
        }

        printClustering();

        System.out.println("Done.");

    }

    public void runInitialLinkage(){
        // Build singleton clusters for each vertex in the set.

        for(V vrt : initial_graph.vertexSet()){
            // Create the vertex set.
            HashSet<V> vertex_set = new HashSet<>();
            vertex_set.add(vrt);
            // Create the edge set by selecting all of the edges that participate in this vertex.
            //Set<E> edge_set = initial_graph.edgesOf(vrt);
            // Create a singleton Cluster, which contains one vertex and all of that vertex's incident edges.
            Cluster<V,E> singleton = new Cluster<>(initial_graph,vertex_set);
            working_set.add(singleton);
            // Add the reference to the cluster ID.
            cluster_id_map.put(vrt,singleton);
        }

        System.out.println("There are " + this.working_set.size() + " clusters before clustering.");
        System.out.println("Current Global Penalty: " + computeGlobalPenalty());

        while(!working_set.isEmpty()){
            Cluster<V,E> cur_clust = working_set.remove();
            boolean changed = false;
            changed = merge(cur_clust);
            if(!changed)
                changed = split(cur_clust);
            if(!changed)
                changed = move(cur_clust);
            if(!changed){
                System.out.println("Removing Cluster" + cur_clust.getId() + " from the working set.");
                this.finished_set.add(cur_clust);
            }
        }

        printClustering();

        System.out.println("Done.");
    }

    public void printClustering(){
        for(Cluster<V,E> clust : this.finished_set){
            System.out.println("Cluster " + clust.getId());
            for(V vert : clust.vertexSet()){
                RecordVertex rv = (RecordVertex) vert;
                System.out.println("\t" + rv.getName());
            }
        }
    }

    public boolean merge(Cluster<V,E> clust){
        boolean changed = false;
        // Step 1: Iterate through each cluster in the working set (This cluster won't be included).
        for(Cluster<V,E> neigh : this.working_set){

            // Step 2: Calculate the local penalty under the current clustering.
            double glbl_pnl = computeGlobalPenalty();
            int save_id = changeClustID(neigh,clust.getId());
            double merg_pnl = computeGlobalPenalty();

            if(merg_pnl < glbl_pnl){
                // If the change results in a lower penalty, then we should remove this cluster from the working set,
                // and merge them together.
                System.out.println("Merge: Global penalty decreased from " + glbl_pnl + " to " + merg_pnl);
                mergeClusters(clust,neigh);
                changed = true;
                break;
            }else{
                // If it doesn't work out, revert the cluster to its old ID.
                changeClustID(neigh,save_id);
            }

        }
        return changed;
    }

    private void mergeClusters(Cluster<V,E> c1, Cluster<V,E> c2){

        this.working_set.remove(c1);
        this.working_set.remove(c2);

        Set<V> new_vert_set = new HashSet<>();
        new_vert_set.addAll(c1.vertexSet());
        new_vert_set.addAll(c2.vertexSet());
        // Get the connected component of this merged cluster.
        Set<E> new_edge_set = new_vert_set.stream().map(vert -> initial_graph.incomingEdgesOf(vert)).flatMap(Set::stream).collect(Collectors.toSet());
        // Make the new Cluster.
        Cluster<V,E> new_clust = new Cluster<>(initial_graph,new_vert_set,new_edge_set);
        // Add it to the working set.
        this.working_set.add(new_clust);

        // Update the cluster IDs to point to the new cluster
        for(V vert : new_vert_set){
            cluster_id_map.put(vert,new_clust);
        }

    }

    private int changeClustID(Cluster<V,E> c, int new_id){
        // We'll always merge IDs downward, so we choose the largest ID to get rid of.
        int old_id = c.getId();
        // Change the ID of this cluster.
        c.setId(new_id);
        return old_id;
    }

    private Cluster<V,E> changeVertexCluster(V vertex){
        // Get a reference to the cluster currently containing this vertex.
        Cluster<V,E> old_cluster = this.cluster_id_map.get(vertex);
        // Make a new temporary cluster.
        Set<V> vert_set = new HashSet<>();
        vert_set.add(vertex);
        Cluster<V,E> temp_cluster = new Cluster<>(initial_graph,vert_set);
        // Update the reference to the new cluster
        this.cluster_id_map.put(vertex,temp_cluster);
        // Return the old cluster so we can reverse this later.
        return old_cluster;
    }

    private boolean split(Cluster<V,E> c){
        // Get the initial penalty.
        double penalty = computeGlobalPenalty();
        // Marker to see if we changed anything
        boolean changed = false;
        // If we do find a vertex that should be moved out of this cluster, then
        // new_cluster will hold the new Cluster, and cluster_basis will hold the vertex.
        V cluster_basis = null;
        Cluster<V,E> new_cluster = null;
        for(V vert : c.vertexSet()){
            // Change this vertex's cluster
            new_cluster = changeVertexCluster(vert);
            double new_penalty = computeGlobalPenalty();
            if(new_penalty < penalty){
                System.out.println("Split: Global penalty decreased from " + penalty + " to " + new_penalty);
                // At this point, we need to evaluate every node except the node we just removed, to see if moving
                // this node to a new cluster improves the clustering.
                changed = true;
                cluster_basis = vert;
                break;
            }else{
                // Otherwise undo
                this.cluster_id_map.put(vert,c);
            }
        }
        if(changed) {
            for (V vert : c.vertexSet()) {
                // If the Vertex we are iterating on is not the cluster basis
                if(vert != cluster_basis){
                    double cur_penalty = computeGlobalPenalty();
                    // Check if moving this vertex to the new cluster improves it.
                    this.cluster_id_map.put(vert,new_cluster);
                    // Get the penalty after moving this vertex.
                    double new_penalty = computeGlobalPenalty();
                    // If the new penalty is lower, keep the change.
                    if(new_penalty < cur_penalty){
                        System.out.println("Split: Global penalty decreased from " + penalty + " to " + new_penalty);
                    }
                    // Otherwise move the vertex back to its old cluster.
                    else{
                        this.cluster_id_map.put(vert,c);
                    }
                }
            }
            // Add the new cluster we generated from splitting into the working set.
            this.working_set.add(new_cluster);
            this.working_set.add(c);
        }
        return changed;
    }

    // TODO: Find a way to do this that isn't linear in complexity.
    private int getMaxClusterLabel(){
        int max_val = 0;
        for(Cluster<V,E> c : this.cluster_id_map.values()){
            if(c.getId() > max_val)
                max_val = c.getId();
        }
        return max_val;
    }

    private boolean move(Cluster<V,E> c){
        // For each vertex in the vertex set, check if moving this vertex to another cluster improves the clustering.
        // Original clustering penalty.

        // Change marker
        boolean changed = false;
        for(Cluster<V,E> neighbor : this.working_set) {
            // Consider all of the points in the original cluster and the neighboring cluster.
            Set<V> vertex_union = new HashSet<>();
            vertex_union.addAll(c.vertexSet());
            vertex_union.addAll(neighbor.vertexSet());
            // Iterate over all the vertices in the union.
            for (V vertex : vertex_union) {
                double old_penalty = computeGlobalPenalty();
                // Get a reference to the cluster that this vertex does not belong to.
                Cluster<V,E> home_clust = cluster_id_map.get(vertex);
                Cluster<V,E> opposite_clust = home_clust.equals(neighbor) ? c : neighbor;

                this.cluster_id_map.put(vertex,opposite_clust);
                opposite_clust.addVertex(vertex);
                home_clust.removeVertex(vertex);
                double new_penalty = computeGlobalPenalty();
                // Keep if the score is below the old penalty.
                if(new_penalty < old_penalty){
                    System.out.println("Move: Global Penalty decreased from " + old_penalty + " to " + new_penalty);
                    changed = true;
                }
                // If it doesn't work out, undo the cluster assignment.
                else{
                    this.cluster_id_map.put(vertex,home_clust);
                    opposite_clust.removeVertex(vertex);
                    home_clust.addVertex(vertex);
                }
            }

        }
        if(changed)
            this.working_set.add(c);
        return changed;
    }

    @Override
    public List<Set<V>> getClusterClasses() {
        return null;
    }

    @Override
    public Map<V, Integer> getClusters() {
        return null;
    }

    @Override
    public int getNumberClusters() {
        return 0;
    }
}
