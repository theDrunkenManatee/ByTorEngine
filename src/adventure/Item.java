package adventure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Item {
	private String name;
	private ArrayList<String> aliases;
	private String desc;
	private boolean portable;
	private boolean isContainer;
	private Container container;
	private String goal;
	private String goalmessage;
	private int score;
	
	public Item(String name, ArrayList<String> aliases, String desc, boolean portable, boolean isContainer, String goal, String goalmessage, int score)
	{
		this.name=name;
		this.aliases=aliases;
		this.desc=desc;
		this.portable=portable;
		this.isContainer=isContainer;
		this.goal=goal;
		this.goalmessage=goalmessage;
		this.score=score;
		if(isContainer) {
			container=new Container(0, goalmessage);
		} else {
			container=null;
		}
	}

	public boolean is(String name) {
		if(aliases!=null)
			for(String alias:aliases) {
				if(alias.equals(name))
					return true;
			}
		
		return name.equals(this.name);
	}
	public ArrayDeque<Item> has(String name) {
		ArrayDeque<Item> itemPath=new ArrayDeque<>();
		if(is(name)) {
			itemPath.add(this);
			return itemPath;
		} 
		else if(isContainer&&container.getMap().size()>0) {
			for(Entry<String, Item> item:container.getMap().entrySet()) {
				ArrayDeque<Item> tempDeque=item.getValue().has(name);
				if(item.getValue().has(name)!=null) {
					for(Item temp:tempDeque) {
						itemPath.add(temp);
					}
					itemPath.add(this);
					return itemPath;
				}
			}
		}
		return null;
	}
	public String getFullDesc() {
		String out=desc;
		if(isContainer) {
			if(container.getMap().size()>0) {
				out+="Currently has: ";
				int spot = 0;
				for(Entry<String, Item> held: container.getMap().entrySet()) {
					if (spot > 0)
						out+=", ";
					out+=held.getValue().getName();
					spot++;
				}
				out+=".\n";
			} else {
				out+="It's empty right now.\n";
			}
		}
		return out;
	}
	public String getFullName() {
		String out=name;
		if(isContainer) {
			if(container.getMap().size()>0) {
				out+="(";
				int spot = 0;
				for(Entry<String, Item> held: container.getMap().entrySet()) {
					if (spot > 0)
						out+=", ";
					out+=held.getValue().getFullName();
					spot++;
				}
				out+=")";
			} else {
//				out+="(empty)";
			}
		}
		
		
		return out;
	}
	
	
	//Getter Methods
	public String getName(){return name;}
	public  ArrayList<String> getAliases(){return aliases;}
	public String getPureDesc(){return desc;}
	public boolean getPortable(){return portable;}
	public boolean getIsContainer() {return isContainer;}
	public Container getContainer() {return container;}
	public String getGoal(){return goal;}
	public String getGoalMessage(){return goalmessage;}
	public int getScore() {return score;}

}
