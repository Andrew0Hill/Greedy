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

    @Override
    // The hash code for a vertex is just the hashCode of its ID string.
    // This is to ensure that we can do lookups on
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    // Equality should be between the ID strings of each RecordVertex.
    public boolean equals(Object rv){

        if(rv == this){
            return true;
        }else if (!(rv instanceof RecordVertex)){
            return false;
        }
        RecordVertex v = (RecordVertex) rv;

        return v.getName().equals(this.name);
    }

}
