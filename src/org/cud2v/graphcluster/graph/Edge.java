package org.cud2v.graphcluster.graph;

import java.util.ArrayList;
import java.util.List;

public class Edge<E> {
	
	private static int ID = 0;
	private int id;
	private double weight;
	private VertName elemone;
	private VertName elemtwo;
	List<VertName> elements = new ArrayList<VertName>();
	
	public Edge(VertName elemone, VertName elemtwo, double distance){
		
		this.id=ID++;
		this.elemone = elemone;
		this.elemtwo = elemtwo;
//		pointers = new LinkedList<Connector<E>>();
		this.weight = distance;
		this.elements.add(elemone);
		this.elements.add(elemtwo);
		
	}
	
	public double getWeight(){
		return this.weight;
	}
	
	public List<VertName> getElements(){
		return this.elements;
	}
	
	public boolean contains(VertName v){
		if(v.getId() == this.elemone.getId() || v.getId() == this.elemtwo.getId() ){
			return true;
		}
		return false;
		
	}
	
	public boolean equals(Edge e){
		if(this.elemone.getId() == e.elemone.getId() && this.elemtwo.getId() == e.elemtwo.getId() && this.weight==e.weight){
			return true;
		}
		return false;
	}

	public VertName getElemone() {
		return elemone;
	}

	public VertName getElemtwo() {
		return elemtwo;
	}

	public int getId() {
		return id;
	}
	

}
