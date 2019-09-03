package examples;

import algorithms.GreedyIncrementalLinkage;
import cluster.Cluster;
import data.RecordVertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import util.GraphBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IncrementalJoin {

    public static void main(String[] args) throws Exception{
        System.out.println("Reading first file");

        List<List<String>> data_file = util.DataReader.getEdgeListFile("increment_test_4/orig_link_result.csv",",",true);

        // Build the initial graph.
        HashMap<Integer,List<String>> backdata_map = new HashMap<>();
        HashMap<RecordVertex, Cluster<RecordVertex, DefaultWeightedEdge>> cluster_assignment = new HashMap<>();
        SimpleWeightedGraph<RecordVertex, DefaultWeightedEdge> graph = GraphBuilder.buildGraphFromEdgeList(data_file,backdata_map,cluster_assignment);
        // Run the first linkage operation.
        GreedyIncrementalLinkage<RecordVertex,DefaultWeightedEdge> linkage = new GreedyIncrementalLinkage<>(graph,cluster_assignment);
        //linkage.runInitialLinkage();
        // Read in the incremental data.
        List<List<String>> increment_file = util.DataReader.getEdgeListFile("increment_test_4/inc_link_result.csv",",",true);
        // Update the graph with the new nodes. Returns the new set of vertices that were added.
        Set<RecordVertex> modified_verts = GraphBuilder.updateGraphFromEdgeList(graph,increment_file);
        // Run linkage only on the new modified vertices.
        linkage.runIncrementalLinkage(modified_verts);
        System.out.println("Done.");
        linkage.printClusteringToFile("increment_test_4/output_improved_3.csv");
    }
}
