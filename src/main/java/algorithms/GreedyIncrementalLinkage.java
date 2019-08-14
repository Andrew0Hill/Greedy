package algorithms;

import cluster.Cluster;
import cluster.ExclusiveClustering;
import data.RecordVertex;
import jdk.nashorn.internal.ir.Block;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import sun.awt.image.ImageWatched;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GreedyIncrementalLinkage<V,E> implements ExclusiveClustering<V> {
    // Holds reference to the backend graph
    private SimpleWeightedGraph<V, E> initial_graph;
    // The working set contains all of the clusters we are currently considering in the clustering.
    private Queue<Cluster<V,E>> working_set;
    // The cluster ID map maps a RecordVertex to Cluster.
    private ConcurrentHashMap<V,Cluster<V,E>> cluster_id_map;
    // Set to hold all of the clusters we are not currently considering.
    private Set<Cluster<V,E>> finished_set;
    // Inspector for getting the connected components of the graph.
    private ConnectivityInspector<V,E> ci;
    private ConcurrentHashMap<V,Cluster<V,E>> global_labels;
    // This constructor handles running incremental linkage where the initial linkage has not already been performed.
    // it performs the initial linkage internally.
    public GreedyIncrementalLinkage(SimpleWeightedGraph<V, E> initial_graph){
        this.working_set = new LinkedBlockingQueue<>();
        this.initial_graph = initial_graph;
        this.cluster_id_map = new ConcurrentHashMap<>();
        this.finished_set = new HashSet<>();
        this.global_labels = new ConcurrentHashMap<>();
        ci = new ConnectivityInspector<>(this.initial_graph);
        //runInitialLinkage();
    }

    // This constructor handles running incremental linkage on a dataset where the initial linkage already exists
    public GreedyIncrementalLinkage(SimpleWeightedGraph<V,E> initial_graph,HashMap<V,Cluster<V,E>> cluster_labels){
        this.working_set = new LinkedBlockingQueue<>();
        this.initial_graph = initial_graph;
        this.cluster_id_map = new ConcurrentHashMap<>();
        this.cluster_id_map.putAll(cluster_labels);
        this.finished_set = new HashSet<>();
        ci = new ConnectivityInspector<>(this.initial_graph);
        // Add all initial clusters to the finished set.
        this.finished_set.addAll(cluster_labels.values());
        this.global_labels = new ConcurrentHashMap<>();
        this.global_labels.putAll(cluster_labels);
    }

    public void runIncrementalLinkage(Set<V> vertices) throws Exception{

        // This set is used so that we don't insert multiple references to a single Cluster<> into the
        // working queue. The HashSet will take care of any duplicates transparently.
        Set<Cluster<V,E>> temp_working_set = Collections.newSetFromMap(new ConcurrentHashMap<Cluster<V,E>,Boolean>());
        final int num_processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Running with " + num_processors + " available processors.");
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(num_processors);
        // Make new singleton clusters for all new vertices
        int count = 0;
        for(V vert:vertices){
            tpe.execute(() -> {
                // Vertex set.
                Set<V> sngl = new HashSet<>();
                sngl.add(vert);
                // Add a reference in the cluster map.
                Cluster<V,E> new_clust = new Cluster<>(initial_graph,sngl);
                cluster_id_map.put(vert,new_clust);
                // By default, all new clusters should go into the working queue.
                temp_working_set.add(new_clust);
            });
/*            if(count % 1000 == 0)
                System.out.println(((double) count/vertices.size()) * 100+ "% complete.");
            ++count;
            // Vertex set.
            Set<V> sngl = new HashSet<>();
            sngl.add(vert);
            // Add a reference in the cluster map.
            Cluster<V,E> new_clust = new Cluster<>(initial_graph,sngl);
            cluster_id_map.put(vert,new_clust);
            // By default, all new clusters should go into the working queue.
            temp_working_set.add(new_clust);*/
        }
        tpe.shutdown();
        tpe.awaitTermination(120,TimeUnit.SECONDS);
        Map<Graph<V,E>,HashMap<V,Cluster<V,E>>> connected_cmpnts = new ConcurrentHashMap<>();
        tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(num_processors);
        for(V vert: vertices){
            tpe.execute(new Runnable() {
                @Override
                public void run() {
                    // Get the connected component of this vertex.
                    Set<V> cmpnt = ci.connectedSetOf(vert);
                    // Get the subgraph with this connected component.
                    Graph<V,E> subgraph = new AsSubgraph<>(initial_graph,cmpnt);
                    if(!connected_cmpnts.containsKey(subgraph)){
                        // Get the set of cluster labels within this connected component.
                        // Note: The connected component will be the same for every vertex in the connected component,
                        //       so we only need to do this once per connected component.
                        HashMap<V,Cluster<V,E>> connected_clusts = new HashMap<>();
                        for(V v : cmpnt){
                            connected_clusts.put(v,cluster_id_map.get(v));
                        }
                        connected_cmpnts.put(subgraph,connected_clusts);
                    }
                }
            });
        }
        System.out.println("Finished building.");
        tpe.shutdown();
        tpe.awaitTermination(500,TimeUnit.SECONDS);
        tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(num_processors);
        for(Map.Entry<Graph<V,E>,HashMap<V,Cluster<V,E>>> entry: connected_cmpnts.entrySet()){
            GreedyIncrementalLinkageTask<V,E> gilt = new GreedyIncrementalLinkageTask<>(entry.getKey(),entry.getValue(),this.global_labels);
            System.out.println("Executing Runnable.");
            tpe.execute(gilt);
        }
        tpe.shutdown();
        tpe.awaitTermination(500,TimeUnit.SECONDS);
    }

   /* public void runInitialLinkage(){
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
    }*/

    public void printClustering(){
        for(Cluster<V,E> clust : this.finished_set){
            System.out.println("Cluster " + clust.getId());
            for(V vert : clust.vertexSet()){
                RecordVertex rv = (RecordVertex) vert;
                System.out.println("\t" + rv.getName());
            }
        }
    }

    public void printClusteringToFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("increment_test/output.csv"));
        for(Map.Entry<V,Cluster<V,E>> entry : this.global_labels.entrySet()){
            bw.write(((RecordVertex) entry.getKey()).getName() + "," + Integer.toString(entry.getValue().getId()) + "\n");
        }
        bw.close();
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
