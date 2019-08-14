package util;

import cluster.Cluster;
import data.RecordVertex;
import data.Similarity;
import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import sun.security.provider.certpath.Vertex;

import java.lang.reflect.Array;
import java.util.*;

public class GraphBuilder {


    public static SimpleWeightedGraph<RecordVertex, DefaultWeightedEdge> buildGraphFromEdgeList(List<List<String>> edges,
                                                                                                HashMap<Integer,List<String>> backing_data,
                                                                                                HashMap<RecordVertex, Cluster<RecordVertex,DefaultWeightedEdge>> cluster_vertex_map){
        HashMap<String,RecordVertex> vertex_map = new HashMap<>();
        HashMap<Integer,Cluster<RecordVertex,DefaultWeightedEdge>> cluster_number_map = new HashMap<>();

        SimpleWeightedGraph<RecordVertex, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for(List<String> edge : edges){
            // Parse values from the raw data.
            String node1 = edge.get(2);
            String node2 = edge.get(4);
            int clust_id = Integer.parseInt(edge.get(5));
            double score = Double.parseDouble(edge.get(7))/100;

            // If either of the keys is not already a Vertex in the graph, add them.
            if(!vertex_map.containsKey(node1)){
                RecordVertex rv = new RecordVertex(node1,backing_data.get(Integer.parseInt(node1)));
                vertex_map.put(node1,rv);
            }
            if(!vertex_map.containsKey(node2)){
                RecordVertex rv = new RecordVertex(node2,backing_data.get(Integer.parseInt(node2)));
                vertex_map.put(node2,rv);
            }

            // Get references to both, and add the edge between them.
            RecordVertex left = vertex_map.get(node1);
            RecordVertex mid = vertex_map.get(node2);
            graph.addVertex(left);
            graph.addVertex(mid);
            // Add the edge between these nodes.
            graph.addEdge(left,mid);
            graph.setEdgeWeight(left,mid,score);
            // Make a new vertex set to hold the pair of vertices.
            Set<RecordVertex> vert_pair = new HashSet<>();
            vert_pair.add(left);
            vert_pair.add(mid);
            // Make a new edge set to hold the set of edges
            Set<DefaultWeightedEdge> edge_set = new HashSet<>();
            edge_set.add(graph.getEdge(left,mid));

            if(!cluster_number_map.containsKey(clust_id)){
                Cluster<RecordVertex,DefaultWeightedEdge> clust = new Cluster<>(graph,vert_pair,edge_set,clust_id);
                cluster_number_map.put(clust_id,clust);
            }
            Cluster<RecordVertex,DefaultWeightedEdge> network_id = cluster_number_map.get(clust_id);
            network_id.addVertex(left);
            network_id.addVertex(mid);
            network_id.addEdge(left,mid,graph.getEdge(left,mid));

            cluster_vertex_map.put(left,network_id);
            cluster_vertex_map.put(mid,network_id);

        }
        return graph;
    }

    public static SimpleWeightedGraph<RecordVertex, DefaultWeightedEdge> buildGraphFromData(List<List<String>> init_data){

        // Build the graph
        SimpleWeightedGraph<RecordVertex,DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Add the vertices to the graph.
        for(List<String> row : init_data){
            RecordVertex rv = new RecordVertex(row.get(1),row);
            graph.addVertex(rv);
        }

         // Compute the edges of the graph.
        ArrayList<RecordVertex> vertex_list = new ArrayList<>(graph.vertexSet());

        for(int i=0; i < vertex_list.size(); ++i){
            for(int j=i+1; j < vertex_list.size(); ++j){
                double siml = Similarity.similarity(vertex_list.get(i).getData(),vertex_list.get(j).getData());
                // If similarity above threshold, add the edge.
                if(siml > 0.75){
                    graph.addEdge(vertex_list.get(i),vertex_list.get(j));
                    graph.setEdgeWeight(vertex_list.get(i),vertex_list.get(j),siml);
                }
            }
        }

        return graph;
    }

    public static Set<RecordVertex> updateGraphFromDataCURL(SimpleWeightedGraph<RecordVertex,DefaultWeightedEdge> graph, List<List<String>> update){

        Set<RecordVertex> update_vertices = new HashSet<>();

        for(List<String> row : update){
            RecordVertex rv = new RecordVertex(row.get(17),row);
            graph.addVertex(rv);
            update_vertices.add(rv);
        }

        // Compute the similarities for the edges of the graph.
        // For an update, we need to calculate the similarities between the new set of vertices, and the entire vertex set
        // (existing + update vertices)
        for(RecordVertex new_vert : update_vertices) {
            for (RecordVertex nb_vert : graph.vertexSet()) {
                if (new_vert != nb_vert) {
                    double siml = Similarity.similarity(new_vert.getData(), nb_vert.getData());

                    if (siml > 0.75) {
                        graph.addEdge(new_vert, nb_vert);
                        graph.setEdgeWeight(new_vert, nb_vert, siml);
                    }
                }
            }
        }
        return update_vertices;
    }

    public static Set<RecordVertex> updateGraphFromData(SimpleWeightedGraph<RecordVertex,DefaultWeightedEdge> graph, List<List<String>> update){

        Set<RecordVertex> update_vertices = new HashSet<>();

        for(List<String> row : update){
            RecordVertex rv = new RecordVertex(row.get(1),row);
            graph.addVertex(rv);
            update_vertices.add(rv);
        }

        // Compute the similarities for the edges of the graph.
        // For an update, we need to calculate the similarities between the new set of vertices, and the entire vertex set
        // (existing + update vertices)
        for(RecordVertex new_vert : update_vertices) {
            for (RecordVertex nb_vert : graph.vertexSet()) {
                if (new_vert != nb_vert) {
                    double siml = Similarity.similarity(new_vert.getData(), nb_vert.getData());

                    if (siml > 0.75) {
                        graph.addEdge(new_vert, nb_vert);
                        graph.setEdgeWeight(new_vert, nb_vert, siml);
                    }
                }
            }
        }
        return update_vertices;
    }

    public static Set<RecordVertex> updateGraphFromEdgeList(SimpleWeightedGraph<RecordVertex,DefaultWeightedEdge> graph, List<List<String>> update){
        HashSet<RecordVertex> vertices = new HashSet<>();
        // Make new vertices, and check if they already exist in the graph.
        for(List<String> row : update){
            // Parse values from the raw data.
            String node1 = row.get(2);
            String node2 = row.get(4);
            int clust_id = Integer.parseInt(row.get(5));
            double score = Double.parseDouble(row.get(7))/100;


            RecordVertex rv_1 = new RecordVertex(node1,null);
            RecordVertex rv_2 = new RecordVertex(node2,null);

            if(!graph.containsVertex(rv_1)){
                graph.addVertex(rv_1);
            }
            if(!graph.containsVertex(rv_2)){
                graph.addVertex(rv_2);
            }

            graph.addEdge(rv_1,rv_2);
            graph.setEdgeWeight(rv_1,rv_2,score);

            vertices.add(rv_1);
            vertices.add(rv_2);
        }
        return vertices;
    }
}
