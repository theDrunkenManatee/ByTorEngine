package adventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

/**
 * This class reads a YAML file using the SnakeYAML library and process the
 * resulting objects. It also demonstrates the use of the Command, Location, and
 * Item classes.
 *
 * <p>
 * Copyright 2018 Griffith Samore, Original code by Brent Yorgey. This work is
 * licensed under a
 * <a rel="license" href= "http://creativecommons.org/licenses/by/4.0/">Creative
 * Commons Attribution 4.0 International License</a>.
 * </p>
 *
 * @author Griffith Samore
 * @version April 18, 2018
 *
 */
public class ByTorEngine {

	private static Location currentLocation;
	private static Map<String, Location> locations;
	private static Player player;

	public static void main(String[] args) {

		locations = new HashMap<>();

		player = new Player();

		// If anything goes wrong inside the 'try' (e.g. the file is not found),
		// it will jump to the 'catch' part.
		try {
			InputStream input = new FileInputStream(new File("Chronon.yaml"));
			// Create a Yaml object to parse the .yaml file
			Yaml yaml = new Yaml();
			int temp = 0;
			// Parse the .yaml file and loop over the resulting objects.

			System.out.println("loading...\n");
			printOpening();
			
			System.out.println("{Programmer's Note: Type \"help\" for a list of commands.}\n");
			for (Object thing : yaml.loadAll(input)) {

				// We happen to know that the YAML file contains key-value
				// mappings, so the returned Objects will in fact be Maps, and
				// we can cast them as such. Eclipse shows us a warning here
				// (yellow underline) because this code might crash if we call
				// it on the wrong sort of .yaml file and 'thing' is not
				// actually a Map.
				if (temp == 0) {
					currentLocation = makeLocation((Map<String, Object>) thing);
					locations.put(currentLocation.getId(), currentLocation);
					temp++;
				} else
					sortItems((Map<String, Object>) thing);
				// printItems((Map<String, Object>) thing);

			}
			input.close();

			Scanner in = new Scanner(System.in);
			Command com;

			look();
			currentLocation.visited = true;
			

			do {
				System.out.print("\n> ");

				// Read the user's input and parse it using a Command object.
				com = new Command(in.nextLine());
				runCommands(com);
			} while (com.getVerb() != Verb.QUIT);

			in.close();

		} catch (IOException e) {
			// This is what to do if anything goes wrong, e.g. the file
			// Hendrix.yaml is not found. Just print the error and quit.
			System.out.println(e);
			System.exit(1);
		}
	}

	private static void runCommands(Command com) {
		// query the Command object to find out what the user
		// typed. This example only pays attention to the verb (the
		// first word typed).

		// Invalid verb
		if (com.getVerb().equals(Verb.UNKNOWN)) {
			System.out.println("What?");
		}
		// Go
		else if (com.getVerb().equals(Verb.GO)) {
			if (com.getDirection().equals(Direction.UNKNOWN))
				System.out.println("Please also give a valid direction");
			else
				go(com.getDirection());
		}
		// Look
		else if (com.getVerb().equals(Verb.LOOK)) {
			if (com.getNoun().length() > 0) {
				look(com.getNoun());
			} else {
				if (com.getDirection().equals(Direction.UNKNOWN))
					look();
				else
					look(com.getDirection());
			}
		}
		// Take
		else if (com.getVerb().equals(Verb.TAKE)) {
			if (com.getNoun().length() > 0) {
				take(com.getNoun());
			} else {
				System.out.println("Take what?");
			}
		}
		// Take
		else if (com.getVerb().equals(Verb.DROP)) {
			if (com.getNoun().length() > 0) {
				drop(com.getNoun());
			} else {
				System.out.println("Drop what?");
			}
		}
		//
		else if (com.getVerb().equals(Verb.PUT)) {
			if (com.getNouns()!=null) {
				if(com.getNouns().get(0).length()>0&&com.getNouns().get(2).length()>0)
					put(com.getNouns().get(1), com.getNouns().get(2));
			} else {
				System.out.println("Put what in what?");
			}
		}
		// Inventory
		else if (com.getVerb().equals(Verb.INVENTORY)) {
			inventory();
		}
		// Score
		else if (com.getVerb().equals(Verb.SCORE)) {
			score();
		}
		// Help
		else if (com.getVerb().equals(Verb.HELP)) {
			help();
		}
		// Unsupported Verbs
		else {
			System.out.println("OK, you want to " + com.getVerb() + ".");
		}
	}

	// Bootup Methods
	private static void sortItems(Map<String, Object> item) {
		if (item.get("type").equals("location")) {
			Location newLocation = makeLocation(item);
			// System.out.println("adding location with id " + newLocation.getId());
			locations.put(newLocation.getId(), newLocation);
		} else {
			System.out.println("ERROR: Unknown Type Encountered at loadTime");
		}
	}
	private static void printOpening() {
		System.out.println("You've been going about a normal day when, out of the blue, you feel yourself being\n"
				         + "pulled from your current place into somewhere else. You feel time slowing down to a\n"
				         + "crawl around you, and find youself in front of a small building. It looks like some\n"
				         + "sort of shop that would be found by a gas station, minus the gas station.");
		System.out.println("Welcome to...");
		System.out.println( 
				"         __        __   __        __               ___    ___       ___ \n" + 
				" /\\     /  ` |__| |__) /  \\ |\\ | /  \\ |\\ |     /\\   |      |  |__| |__  \n" + 
				"/~~\\    \\__, |  | |  \\ \\__/ | \\| \\__/ | \\|    /~~\\  |      |  |  | |___ \n" + 
				"                                                                        \n" + 
				" __   __       ___              ___  __      __  ___  __   __   ___     \n" + 
				"/  ` /  \\ |\\ |  |   /\\  | |\\ | |__  |__)    /__`  |  /  \\ |__) |__      \n" + 
				"\\__, \\__/ | \\|  |  /~~\\ | | \\| |___ |  \\    .__/  |  \\__/ |  \\ |___     \n" + 
				"                                                                        \n"
				+ "               <Powered by the By-Tor Engine>");
	}
	
	// Command Methods
	private static void go(Direction dir) {
		Location goal = checkExit(dir);
		if (goal != null) {
			currentLocation = goal;
			System.out.print(currentLocation.getDesc());
			printItems(currentLocation);
		} else {
			System.out.println("You can't go that way.");
		}
	}
	private static void look() {
		System.out.print(currentLocation.getLongdesc());
		printItems(currentLocation);
	}
	private static void look(Direction dir) {
		Location goal = checkExit(dir);
		if (goal != null) {
			System.out.println("You see " + goal.getArticle() + " " + goal.getName() + " in that direction.");
		} else {
			System.out.println("There isn't anything in that direction.");
		}
	}
	private static void look(String item) {
		if(item.equals("map")) {
			System.out.println(" ___________\r\n" + 
					"|           |\r\n" + 
					"| restroom  |\r\n" + 
					"|__   ______|_______________________________________________\r\n" + 
					"|_        AISLE 3                      |______Soda______| | |\r\n" + 
					"| |    ________________                                   |S|\r\n" + 
					"|m|   |    cereal      |                 _______          |L|\r\n" + 
					"|a|   |    chips       |                |Coffee |         |O|\r\n" + 
					"|g|   |________________|                |Station|         |R|\r\n" + 
					"|a|                                              ____     |P|\r\n" + 
					"|z|       AISLE 2                               / r /     |_|\r\n" + 
					"|i|    ________________                        / e /        |\r\n" + 
					"|n|   |    candy       |                      / g /         |\r\n" + 
					"|e|   |   clothing     |                     | i |          |\r\n" + 
					"|s|   |________________|                     | s |          |\r\n" + 
					"| |                                          | t |          |\r\n" + 
					"|_|       AISLE 1          ___               | e |          |\r\n" + 
					"|_________________________|ATM|___   ________|_r_|__________|\r\n" + 
					"                                 /");
		}else {
		ArrayDeque<Item> itemDeque=getSmartDeque(item);
		if (itemDeque!=null)
			System.out.print(itemDeque.getFirst().getFullDesc());
		else
			System.out.println("You can't look at that.");
		}
	}
	private static void take(String item) {
		boolean found = false;
		Item thing=null;
		ArrayDeque<Item> smartThingDeque = currentLocation.getSmartItem(item);
		if(smartThingDeque!=null) 
			thing=smartThingDeque.getFirst();
		if (thing != null) {
			if (thing.getPortable()) {
				System.out.println("You take the " + thing.getName() + ".");
				player.getInventory().add(thing);
				currentLocation.remove(thing);
			} else
				System.out.println("You can't take that!");
			found = true;
		}
		if (!found)
			System.out.println("There isn't anything like that to take.");
	}
	private static void drop(String item) {
		boolean found = false;
		ArrayDeque<Item> smartItemDeque = player.getSmartItem(item);
		Item smartItem = null;
		if (smartItemDeque != null && smartItemDeque.size() > 0)
			smartItem = smartItemDeque.getFirst();
		if (smartItem != null) {
			if (smartItem.getPortable()) {
				System.out.println("You drop the " + smartItem.getName() + ".");
				currentLocation.getItems().put(smartItem.getName(), smartItem);
				player.remove(smartItem);
				if (currentLocation.getId().equals(smartItem.getGoal())) {
					System.out.println(smartItem.getGoalMessage());
				}
			}
			found = true;
		}
		if (!found)
			System.out.println("You can't drop something you don't have in your inventory!");
	}
	private static void put(String item, String container) {
//		System.out.println("put() item="+item+", cont="+container);
		Item toPut = null;
		Item holder = null;
		ArrayDeque<Item> smartItemDeque = getSmartDeque(item);
		ArrayDeque<Item> smartContDeque = getSmartDeque(container);
		if(smartItemDeque!=null) 
			toPut=smartItemDeque.getFirst();
		if(smartContDeque!=null)
			holder=smartContDeque.getFirst();
		if(toPut!=null&&holder!=null) {
			if(holder.has(item)==null) {
				if(holder.getIsContainer()) {
					if(toPut.getPortable()) {
						if(toPut.has(container)==null) {
							if(smartItemDeque.size()>1) {
								System.out.print("You have put the "+toPut.getName()+" in the "+holder.getName()+".\n");
								smartItemDeque.pop();
								smartItemDeque.getFirst().getContainer().remove(toPut);
								holder.getContainer().add(toPut);
							} else {
								if(player.getSmartItem(item)!=null) {
									System.out.print("You have put the "+toPut.getName()+" in the "+holder.getName()+".\n");
									if(!player.remove(toPut)) {
										System.out.println("ERROR: REMOVAL ERROR WITH PLAYER");
									}						
									holder.getContainer().add(toPut);
								} else if(currentLocation.getSmartItem(item)!=null) {
									System.out.print("You have put the "+toPut.getName()+" in the "+holder.getName()+".\n");
									if(!currentLocation.remove(toPut)) {
										System.out.println("ERROR: REMOVAL ERROR WITH LOCATION");
									}
									holder.getContainer().add(toPut);
								} else {
									System.out.println("ERROR: PLACE NOT FOUND");
								}
							}
						} else {
							System.out.println("Sorry, but "+toPut.getName()+" is already holding the "+holder.getName()+", so you can't put it in there.");
						}
					} else {
						System.out.println("You can't take that!");
					}
				} else {
					System.out.println("You can't put things in that.");
				}
			} else {
				System.out.println("It's already in there, so that's a bit redundant.");
			}
		} else {
			if(toPut!=null) {
				System.out.print("I don't recognize where you're trying to put that "+toPut.getName()+".");
			} else if(holder!=null) {
				System.out.print("You can't put that in the "+holder.getName()+".");
			} else {
				System.out.print("I dont recognize either item.");
			}
		}
		
	}
	private static void inventory() {
		if (player.getInventory().getMap().size() > 0) {
			System.out.print("You have these items: ");
			int spot = 0;
			for (Map.Entry<String, Item> item : player.getInventory().getMap().entrySet()) {
				if (spot > 0)
					System.out.print(", ");
				System.out.print(item.getValue().getFullName());
				spot++;
			}System.out.println();
		} else {
			System.out.println("Your inventory is empty!");
		}
	}
	private static void score() {
		System.out.println("Score=" + player.getScore());
	}
	private static void help() {
		System.out.println("" + "Explore your surroundings.  You can get points by taking certain\n"
				+ "  objects to certain other locations.\n" + "\n" + "Available commands:\n"
				+ "  go <dir>               - move in a certain direction\n"
				+ "  look                   - look at your surroundings\n"
				+ "  look <dir>             - see what location lies in a given direction\n"
				+ "  look [at] <obj>        - look at an object in your location or inventory\n"
				+ "  take <obj>             - pick up an object or remove it from a container\n"
				+ "  drop <obj>             - put down an object\n"
				+ "  put <obj1> [in] <obj2> - place one object into another, if it can hold things\n"
				+ "  inventory              - see what you are carrying\n"
				+ "  score                  - see your current score\n"
				+ "  help                   - show this help message\n"
				+ "  quit                   - quit the game\n"
				+ "\n"
				+ "Available directions:\n"
				+ "  north south east west northeast northwest southeast southwest up down in out");
	}

	// Helper Methods
	private static ArrayDeque<Item> getSmartDeque(String item) {
		ArrayDeque<Item> smartItemDeque = currentLocation.getSmartItem(item);
		if (smartItemDeque != null && smartItemDeque.size() > 0) {
			return smartItemDeque;
		} else {
			smartItemDeque = player.getSmartItem(item);
			if (smartItemDeque != null && smartItemDeque.size() > 0)
				return smartItemDeque;
		}
		return null;
	}
	
	private static Location makeLocation(Map<String, Object> location) {
		// System.out.println("\n~~~New location~~~");
		Location out = null;
		String id = (String) location.get("id") + "";
		// System.out.println("id set to:"+id);
		String name = (String) location.get("name") + "";
		// System.out.println("name set to:"+name);
		String desc = (String) location.get("desc") + "";
		// System.out.println("desc set to:"+desc);
		String longdesc = (String) location.get("longdesc") + "";
		// System.out.print("longdesc set to:"+longdesc);
		String article = (String) location.get("article");
		// System.out.println("article set to:"+article);
		Map<String, Object> tempExit = (Map<String, Object>) location.get("exits");
		Map<Direction, String> exitList = new HashMap<>();
		if (tempExit != null) {
			for (Map.Entry<String, Object> exitEntry : tempExit.entrySet()) {
				Direction temp = Direction.parse(exitEntry.getKey());
				String tempId = (String) exitEntry.getValue();
				// System.out.println("new exit in "+temp.toString()+" direction, id="+tempId);
				exitList.put(temp, tempId);
			}
		}
		List<Map<String, Object>> tempItemList = (List<Map<String, Object>>) location.get("items");
		Map<String, Item> itemList = new HashMap<>();
		if (tempItemList != null) {
			for (Map<String, Object> itemEntry : tempItemList) {
				String temp = itemEntry.get("name").toString();
				Item tempItem = makeItem(itemEntry);
				// System.out.println("new item made:"+temp+", length="+itemList.size());
				itemList.put(temp, tempItem);
			}
		}
		Map<String, ArrayList<String>> containsMap=(Map<String,ArrayList<String>>) location.get("contains");
		
		out = new Location(id, name, desc, longdesc, article, exitList, itemList, containsMap);
		return out;
	}

	@SuppressWarnings("unchecked")
	private static Item makeItem(Map<String, Object> item) {
		// System.out.println("~~~Item~~~");
		Item out;
		String name = (String) item.get("name") + "";
		// System.out.println("name: "+name);
		ArrayList<String> aliases = (ArrayList<String>) item.get("aliases");
		// System.out.println("aliases: "+aliases);
		String desc = (String) item.get("desc") + "";
		// System.out.print("desc: "+desc);

		Boolean portable = Boolean.parseBoolean(item.get("portable") + "");
		// System.out.println("portable: "+portable);
		Boolean isContainer = Boolean.parseBoolean(item.get("container") + "");
		// System.out.println("isContainer: "+isContainer);
		String goal = (String) item.get("goal") + " ";
		// System.out.println("goal: "+goal);
		String goalmessage = (String) item.get("goalmessage") + " ";
		// System.out.println("goalmessage: "+goalmessage);
		int score = 0;
		if (item.get("score") != null)
			score = Integer.parseInt(item.get("score") + "");
		// System.out.println("score: "+score+"\n");
		if (item.get("portable") == null)
			portable = true;
		if (item.get("container") == null)
			isContainer = false;
		out = new Item(name, aliases, desc, portable, isContainer, goal, goalmessage, score);
		return out;
	}

	private static Location checkExit(Direction dir) {
		String id = currentLocation.getExits().get(dir);
		if (id == null) {
			// System.out.println("There's nothing in that direction!");
			return null;
		} else {
			Location goal = locations.get(id);
			if (goal == null)
				System.out.println("ERROR: Location not found");
			return goal;
		}
	}

	private static void printItems(Location location) {
		if (location.getPortableItemNum() > 0) {
			System.out.println("You see the following items here:");
			int spot = 0;
			for (Map.Entry<String, Item> item : location.getItems().entrySet()) {

				if (item.getValue().getPortable()) {
					if (spot > 0)
						System.out.print(", ");
					System.out.print(item.getValue().getName());
					spot++;
				}
			} if(location.getItems().size()>0) System.out.println();
		}
	}
}