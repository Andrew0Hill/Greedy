package examples;

import algorithms.GreedyIncrementalLinkage;
import data.RecordVertex;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.IntegerComponentNameProvider;
import util.GraphBuilder;

import java.io.FileWriter;
import java.util.List;
import java.util.Set;

public class IncrementalFromFile {

    public static void writeGraphToFile(SimpleWeightedGraph graph) throws Exception{
        ComponentNameProvider<DefaultWeightedEdge> edgeProvider = new ComponentNameProvider<DefaultWeightedEdge>() {
            @Override
            public String getName(DefaultWeightedEdge defaultWeightedEdge) {
                return Double.toString(graph.getEdgeWeight(defaultWeightedEdge));
            }
        };

        ComponentNameProvider<RecordVertex> vertexProvider = new ComponentNameProvider<RecordVertex>() {
            @Override
            public String getName(RecordVertex recordVertex) {
                return recordVertex.getName();
            }
        };
        IntegerComponentNameProvider<RecordVertex> vertexIDProvider = new IntegerComponentNameProvider<>();
        DOTExporter exporter = new DOTExporter(vertexIDProvider,vertexProvider,edgeProvider);
        FileWriter w = new FileWriter("output_graph.html");
        exporter.exportGraph(graph,w);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Processing first file");

        String separator = ",";
        List<List<String>> initial_data = util.DataReader.getDataFile(new String[]{"starbucks_initial.csv"}, separator,false).get(0);

        // Build the initial graph.
        SimpleWeightedGraph<RecordVertex,DefaultWeightedEdge> graph = GraphBuilder.buildGraphFromData(initial_data);
        // Write the graph to a file.
        writeGraphToFile(graph);

        // Run clustering on the initial graph.
        GreedyIncrementalLinkage<RecordVertex,DefaultWeightedEdge> linkage = new GreedyIncrementalLinkage<>(graph);

        // Read in the other files.
        String[] data_files = {"starbucks/sbc_inc_1.csv","starbucks/sbc_inc_2.csv","starbucks/sbc_inc_3.csv","starbucks/sbc_inc_4.csv"};
        List<List<List<String>>> increment_data = util.DataReader.getDataFile(data_files,separator,false);

        for(List<List<String>> file : increment_data){
            Set<RecordVertex> increment_vertices = GraphBuilder.updateGraphFromData(graph,file);
            linkage.runIncrementalLinkage(increment_vertices);
        }

        System.out.println("Done");
    }
}
