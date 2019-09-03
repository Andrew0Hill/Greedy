package algorithms;

import cluster.Cluster;
import data.RecordVertex;
import org.jgrapht.Graph;
import sun.awt.image.ImageWatched;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

class GreedyIncrementalLinkageTask<V,E> implements Runnable{
    // Subgraph of the larger cluster graph.
    private Graph<V,E> graph;
    // The working set contains all of the clusters we are currently considering in the clustering.
    private Queue<Cluster<V,E>> working_set;
    // The cluster ID map maps a RecordVertex to Cluster.
    private HashMap<V,Cluster<V,E>> cluster_id_map;
    // Set to hold all of the clusters we are not currently considering.
    private Set<Cluster<V,E>> finished_set;
    // Reference to the global cluster list, which we will access at the end of the clustering computation.
    private ConcurrentHashMap<V,Cluster<V,E>> g_cluster_id_map;
    private String prefix;
    public GreedyIncrementalLinkageTask(Graph<V,E> graph, HashMap<V,Cluster<V,E>> cluster_labels, ConcurrentHashMap<V,Cluster<V,E>> glbl_clust_list){
        this.graph = graph;
        this.working_set = new LinkedBlockingQueue<>();

        HashSet<Cluster<V,E>> clust_set = new HashSet<>(cluster_labels.values());
        this.working_set.addAll(clust_set);
        this.cluster_id_map = cluster_labels;
        this.finished_set = new HashSet<>();
        //this.finished_set.addAll(cluster_labels.values());
        this.g_cluster_id_map = glbl_clust_list;
        this.prefix = "[" + Runtime.getRuntime().availableProcessors() + "]";
    }

    // Computes the correlation clustering penalty on the current working set.
    private double computeGlobalPenalty(){
        return computeLocalPenalty(this.graph,this.cluster_id_map);
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

    private boolean merge(Cluster<V,E> clust){
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


    private void mergeClusters(Cluster<V,E> c1, Cluster<V,E> c2){

        this.working_set.remove(c1);
        this.working_set.remove(c2);

        Set<V> new_vert_set = new HashSet<>();
        new_vert_set.addAll(c1.vertexSet());
        new_vert_set.addAll(c2.vertexSet());
        // Get the connected component of this merged cluster.
        Set<E> new_edge_set = new_vert_set.stream().map(vert -> graph.incomingEdgesOf(vert)).flatMap(Set::stream).collect(Collectors.toSet());
        // Make the new Cluster.
        Cluster<V,E> new_clust = new Cluster<>(graph,new_vert_set,new_edge_set);
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
        Cluster<V,E> temp_cluster = new Cluster<>(graph,vert_set);
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

    @Override
    public void run() {
        String prefix = "[" + Thread.currentThread().getName() + "]";
        while(!this.working_set.isEmpty()){
            System.out.println(prefix + " Iteration");
            Cluster<V,E> cur_clust = this.working_set.remove();
            boolean changed = false;
            for(V vert : cur_clust.vertexSet()){
                RecordVertex rv = (RecordVertex) vert;
                if(rv.getName().equals("55276")){
                    System.out.println("Found");
                }
            }
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
        // Return the result to the global cluster map.
        this.g_cluster_id_map.putAll(this.cluster_id_map);
    }
}
