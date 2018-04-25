package adventure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Location {
	private String id;
	private String name;
	private String shortDesc;
	private String longdesc;
	private String article;
	private Map<Direction, String> exits;
	private Map<String, Item> items;
	public Boolean visited;
	
	public Location(String id, String name, String desc, String longdesc, String article, Map<Direction,String> exits, Map<String,Item> items, Map<String,ArrayList<String>> containsMap)
	{
		this.id=id;
		this.name=name;
		this.shortDesc=desc;
		this.longdesc=longdesc;
		this.article=article;
		this.exits=exits;
		this.items=items;
		if(article==null)
			this.article="";
		visited=false;
		parseContains(containsMap);
	}

	public ArrayDeque<Item> getSmartItem(String in) {
		if(in!=null)
			for(Entry<String, Item> item:items.entrySet()) {
				ArrayDeque<Item> smartItem=item.getValue().has(in);
				if(smartItem!=null)
					return smartItem;
			}
		return null;
	}
	public int getPortableItemNum() {
		int out=0;
		for(Entry<String, Item> item:items.entrySet()) {
			if(item.getValue().getPortable()) {
				out++;
			}
		}
		return out;
	}
	public boolean remove(Item item) {
		ArrayDeque<Item> path=getSmartItem(item.getName());
		if(path!=null) {
			Item end=path.getLast();
			if(!item.getName().equals(end.getName()) ) {
				path.pop();
				return path.getFirst().getContainer().remove(item);
			} else { 
				return items.remove(item.getName())!=null;
			}
		
		} else {return false;}
	}
	
	public void parseContains(Map<String,ArrayList<String>> containsMap) {
		if(containsMap!=null) {
			for(Entry<String, ArrayList<String>> containsArray :containsMap.entrySet()) {
				Item container=items.get(containsArray.getKey());
				if(container!=null&&container.getIsContainer()) {
					for(String toPut:containsArray.getValue()) {
						container.getContainer().add(items.get(toPut));
						items.remove(toPut);
					}
				}
			}
		}
	}
	
	//Getter Methods
	public String getId(){return id;}
	public String getName(){return name;}
	public String getShortdesc(){return shortDesc;}
	public String getDesc() {
		if(visited) {
			return shortDesc+"\n";
		}
		else {
			visited=true;
			return longdesc;
		}
	}
	public String getLongdesc(){return longdesc;}
	public String getArticle(){return article;}
	public Map<Direction, String> getExits(){return exits;}
	public Map<String, Item> getItems(){return items;}
}
