package examples;

import algorithms.GreedyIncrementalLinkage;
import cluster.Cluster;
import data.RecordVertex;
import eval.RandIndex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import util.GraphBuilder;

import java.util.*;

public class IncremenalFromEdgeList {


    public static void main(String[] args) throws Exception {
        System.out.println("Processing first file");

        String separator = ",";
        List<List<String>> initial_data = util.DataReader.getEdgeListFile("increment_test/orig_data.csv",separator,true);

        List<List<String>> backing_data = util.DataReader.getDataFile(new String[]{"increment_test/concat_v2_revised.csv"},separator,true).get(0);
        HashMap<Integer,List<String>> backdata_map = util.DataReader.formatBackingData(backing_data);
        // Build the initial graph.
        HashMap<RecordVertex, Cluster<RecordVertex,DefaultWeightedEdge>> cluster_assignment = new HashMap<>();
        SimpleWeightedGraph<RecordVertex, DefaultWeightedEdge> graph = GraphBuilder.buildGraphFromEdgeList(initial_data,backdata_map,cluster_assignment);
        // Write the graph to a file.
        //writeGraphToFile(graph);

        // Run clustering on the initial graph.
        GreedyIncrementalLinkage<RecordVertex,DefaultWeightedEdge> linkage = new GreedyIncrementalLinkage<>(graph,cluster_assignment);


        // Get only the nodes that were not added in the first pass
        Set<Integer> left_out_verts = new HashSet<>();
        for(int i = 0; i < 500000; ++i){
            left_out_verts.add(i);
        }
        for(RecordVertex rv : graph.vertexSet()){
            left_out_verts.remove(Integer.parseInt(rv.getName()));
        }
        List<List<String>> leftout_backing = new ArrayList<>();
        for(int i : left_out_verts){
            leftout_backing.add(backing_data.get(i));
        }
        // Update the graph with the new nodes.
        Set<RecordVertex> update_verts = GraphBuilder.updateGraphFromDataCURL(graph,leftout_backing);
        linkage.runIncrementalLinkage(update_verts);

        // Read in the other files.
//        String[] data_files = {"starbucks/sbc_inc_1.csv","starbucks/sbc_inc_2.csv","starbucks/sbc_inc_3.csv","starbucks/sbc_inc_4.csv"};
//        List<List<List<String>>> increment_data = util.DataReader.getDataFile(data_files,separator);
//
//        for(List<List<String>> file : increment_data){
//            Set<RecordVertex> increment_vertices = GraphBuilder.updateGraphFromData(graph,file);
//            linkage.runIncrementalLinkage(increment_vertices);
//        }
        RandIndex.write_labels_to_file(cluster_assignment,cluster_assignment);
    }

}
