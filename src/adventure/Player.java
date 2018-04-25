package adventure;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Player {
	private Container inventory;
	
	private String name;
	private int score;
	
	public Player() {
		inventory=new Container(0,"");
		score=0;
		name="";
	}
	
	public ArrayDeque<Item> getSmartItem(String in) {
		return inventory.getSmartItem(in);
	}
	public boolean remove(ArrayDeque<Item> itemDeque) {
		if(itemDeque.size()>0) {
			Item item=itemDeque.pop();
			if(itemDeque.size()>0) {
				itemDeque.peek().getContainer().remove(item);
				return true;
			} else {return false;}
		} else {return false;}
	}
	
	public boolean remove(Item item) {
		ArrayDeque<Item> path=getSmartItem(item.getName());
		if(path!=null) {
			Item end=path.getLast();
			if(!item.getName().equals(end.getName()) ) {
				path.pop();
				return path.getFirst().getContainer().remove(item);
			} else { 
				return inventory.remove(item);
			}
		
		} else {return false;}
	}
	public void addPoints(int score) {this.score+=score;}

	public Container getInventory() {return inventory;}
	public String getName() {return name;}
	public int getScore(){return score;}
}
