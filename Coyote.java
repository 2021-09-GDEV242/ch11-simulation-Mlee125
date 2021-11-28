import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a coyote.
 * Coyotes age, move, eat foxes, and die.
 *
 * @author Matthew Lee
 * @version 11.28.2021
 */
public class Coyote extends Animal
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a coyote can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a coyote can live.
    private static final int MAX_AGE = 125;
    // The likelihood of a coyote breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a coyote can go before it has to eat again.
    private static final int FOX_FOOD_VALUE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The coyote's food level, which is increased by eating foxes.
    private int foodLevel;

    /**
     * Create a coyote. A coyote can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    
    public Coyote(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            foodLevel = rand.nextInt(FOX_FOOD_VALUE);
        }
        else {
            setAge(0);
            foodLevel = FOX_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the coyote does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newCoyotes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newCoyotes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Make this coyote more hungry. This could result in the coyote's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for foxes adjacent to the current location.
     * Only the first live fox is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) { 
                    fox.setDead();
                    foodLevel = FOX_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this coyote is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCoyote A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newCoyotes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Coyote young = new Coyote(false, field, loc);
            newCoyotes.add(young);
        }
    }
    
    /**
     * A coyote can breed if it has reached the breeding age.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns the max age of the coyote
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Returns the breeding probability of the coyote
     */
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns the max litter size of the coyote
     */
    public int getMaxLitter()
    {
        return MAX_LITTER_SIZE;
    }
}
