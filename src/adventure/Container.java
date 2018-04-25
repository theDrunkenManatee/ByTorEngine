package adventure;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Container {
	private Map<String, Item> inventory;
	private int capacity;
	private String fullMessage;
	
	public Container(int capacity, String fullMessage) {
		this.capacity=0;
		this.fullMessage=fullMessage;
		inventory=new HashMap<String,Item>();
	}
	
	public boolean add(Item item) {
		if(capacity==0) {
			if(inventory.containsKey(item.getName())) {
				System.out.println("ERROR: name already in inventory");
				return false;
			} else {
				inventory.put(item.getName(), item);
				return true;
			}
		} else {
			System.out.println("Sorry, but this is already full!");
			return false;
		}
	}
	public boolean remove(Item item) {
		return inventory.remove(item.getName(), item);
	}
	
	public ArrayDeque<Item> getSmartItem(String in) {
		if(in!=null)
			for(Entry<String, Item> item:inventory.entrySet()) {
				ArrayDeque<Item> smartItem=item.getValue().has(in);
				if(smartItem!=null)
					return smartItem;
			}
		return null;
	}
	
	
	public int getCapacity() {return capacity;}
	public String getFullMessage() {return fullMessage;}
	public Map<String,Item> getMap() {return inventory;}

}
