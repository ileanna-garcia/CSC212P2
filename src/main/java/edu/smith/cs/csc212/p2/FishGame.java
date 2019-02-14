package edu.smith.cs.csc212.p2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
//For this code I got help from practically all TAs, fellow peers like Kiara and Bethany 
//who helped me understand concepts, and I also looked at JJ's lectures and Piazza code.

/**
 * This class manages our model of gameplay: missing and found fish, etc.
 * @author jfoley
 * @param <Snail>
 *
 */
public class FishGame<Snail> {
	/**
	 * This is the world in which the fish are missing. (It's mostly a List!).
	 */
	World world;
	/**
	 * The player (a Fish.COLORS[0]-colored fish) goes seeking their friends.
	 */
	Fish player;
	/**
	 * The home location.
	 */
	FishHome home;
	/**
	 * These are the missing fish!
	 */
	List<Fish> missing;
	
	/**
	 * These are fish we've found!
	 */
	List<Fish> found;
	
	/**
	 * Number of steps!
	 */
	int stepsTaken;

	/**
	 * Score!
	 */
	int score;

	// constant rocks!
	int num_rocks = 10;
	
	//constant food
	int food = 3;
	
	//Returning home
	boolean isHome = false;
	
	//creating an instance of falling rocks
	FallingRock  fallingrock;
	
	//Fish at home list
	List <Fish> fishHome = new ArrayList<>(); 
	
	/**
	 * Create a FishGame of a particular size.
	 * @param w how wide is the grid?
	 * @param h how tall is the grid?
	 */
	public FishGame(int w, int h) {
		world = new World(w, h);
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		
		// Add a home!
		home = world.insertFishHome();
		
		// Generate some more rocks!
		//(lab) Make 5 into a constant, so it's easier to find & change.
		
		
		for (int i=0; i<num_rocks; i++) {
			world.insertRockRandomly();
			}
		
		//fish food on screen randomly implemented like rocks
		for (int i=0; i<food; i++) {
			world.insertFoodRandomly();
			}
		
		// the rock is implemented randomly on a rock
		fallingrock = new FallingRock(world);
		world.insertRandomly(fallingrock);
		
		
		// Make the snail!
		world.insertSnailRandomly();
	
		// Make the player out of the 0th fish color.
		player = new Fish(0, world);
		// Start the player at "home".
		player.setPosition(home.getX(), home.getY());
		player.markAsPlayer();
		world.register(player);
		
		// Generate fish of all the colors but the first into the "missing" List.
		for (int ft = 1; ft < Fish.COLORS.length; ft++) {
			Fish friend = world.insertFishRandomly(ft);
			missing.add(friend);
		}
	}
	
	
	/**
	 * How we tell if the game is over: if missingFishLeft() == 0.
	 * @return the size of the missing list.
	 */
	public int missingFishLeft() {
		return missing.size();
	}
	
	/**
	 * This method is how the PlayFish app tells whether we're done.
	 * @return true if the player has won (or maybe lost?).
	 */
	public boolean gameOver() {
		// (P2) We want to bring the fish home before we win!
		return isHome;
	}
	
	//when the player is at the home position, then put the following fishes into the home (aka make the fishes disappear from the world)
			//if home's x == player's x, etc..., then move the fishes from found list to the fishhome list.
	public void reachHome() {
		if (player.inSameSpot(home)){
		for (Fish f : this.found) {
			fishHome.add(f);
			this.world.remove(f);
			
	}
		for (Fish f: fishHome) {
		this.found.remove(f);
		}
		}
	
	if (missing.isEmpty() && player.inSameSpot(home)) {
		isHome = true;
	}
	}
	
	/**
	 * Update positions of everything (the user has just pressed a button).
	 */
	public void step() {
		// Keep track of how long the game has run.
		this.stepsTaken += 1;
				
		// These are all the objects in the world in the same cell as the player.
		List<WorldObject> overlap = this.player.findSameCell();
		// The player is there, too, let's skip them.
		overlap.remove(this.player);
		
		// If we find a fish, remove it from missing.
		for (WorldObject wo : overlap) {
			// It is missing if it's in our missing list.
			if (missing.contains(wo)) {
				
				//(lab): add to found instead! (So we see objectsFollow work!)
							
				missing.remove(wo);
				found.add((Fish) wo);
				// Increase score when you find a special green fish!
				
				Fish s = (Fish)wo;
				if (s.color == 5) {
					score += 20;
				}
				else  {
					score += 10;
				}
			}
			
			//if the fish touches what is the instance of a fishfood then the score increases and its removed from screen
		
			if (wo instanceof FishFood) {
				score += 20;
				world.remove(wo);
			}
		}
		
		//we go through the list of missing fish and we find any fish that isn't the player and then we go through the list of fish and if the fish is 
		// touching what is the instance of a food then the food is removed from the world.
	for (Fish f:missing) {
		List <WorldObject> fish = f.findSameCell();
		fish.remove(this.player);
		for(WorldObject wo: fish) {
			if (wo instanceof FishFood) {
				world.remove(wo);
			}
		}
		
		
	}				
		
		// Make sure missing fish *do* something.
		wanderMissingFish();
		// When fish get added to "found" they will follow the player around.
		World.objectsFollow(player, found);
		// Step any world-objects that run themselves.
		world.stepAll();
		//reaching home is implemented
		reachHome();
		
	}

	
	/**
	 * Call moveRandomly() on all of the missing fish to make them seem alive.
	 */
	private void wanderMissingFish() {
		Random rand = ThreadLocalRandom.current();
		for (Fish lost : missing) {
			// 80% of the time, lost fish move randomly.
			if (rand.nextDouble() == 0.8) {
				lost.moveRandomly();
			}
			// 30% of the time, lost fish move randomly.
			if (rand.nextDouble() == 0.3) {
				lost.moveRandomly();
			}
		}
	}

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		// TODO(P2) use this print to debug your World.canSwim changes!
		System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		
		List<WorldObject> atPoint = world.find(x, y);
		// (P2) allow the user to click and remove rocks.
		for (WorldObject o: atPoint ) {
			if (o instanceof Rock) {	
				world.remove(o);
		}
		
		}	
}
	}
