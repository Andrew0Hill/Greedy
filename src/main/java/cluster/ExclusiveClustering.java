package cluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ExclusiveClustering<V> {

    List<Set<V>> getClusterClasses();
    Map<V,Integer> getClusters();
    int getNumberClusters();


}
