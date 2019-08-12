package data;

import java.util.Collections;
import java.util.List;

public class RecordVertex implements Comparable<RecordVertex>{
    private List<String> data;
    private String name;
    public RecordVertex(String name, List<String> data){
        this.name = name;
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(RecordVertex o) {
        return getName().compareTo(o.getName());
    }
}
