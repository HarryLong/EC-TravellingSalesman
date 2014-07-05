
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class City {

  /**
   * The city's x position.
   */
  private int xpos;

  /**
   * The city's y position.
   */
  private int ypos;
  
  /**
   * index of this city in the main data structure. Think of it as the city name
   */
  private int name; 
  
  /**
   * The distance from this city to all other cities
   */
  private Map<Integer, Integer> cityDistances;
  
  /**
   * List of cities ranked in order of distance (closest to furthest)
   */
  private List<Integer> citiesRankedByName;
  
  /**
   * Corresponding name of the ranked cities
   */
  private List<Integer> citiesRankedByDistance;
  
  /**
   * Constructor.
   * 
   * @param x The city's x position
   * @param y The city's y position.
   */
  public City(int x, int y, int name) {
    xpos = x;
    ypos = y;
    this.name = name;
  }

  /**
   * Return's the city's x position.
   * 
   * @return The city's x position.
   */
  public int getx() {
    return xpos;
  }

  /**
   * Returns the city's y position.
   * 
   * @return The city's y position.
   */
  public int gety() {
    return ypos;
  }

  public int getName()
  {
	  return name;
  }
  
  /**
   * Returns how close the city is to another city.
   * 
   * @param cother The other city.
   * @return A distance.
   */
  public int proximity(City cother) {
    return cityDistances != null && cityDistances.containsKey(cother.getName()) ? cityDistances.get(cother.getName()) : proximity(cother.getx(),cother.gety());
  }

  /**
   * Returns how far this city is from a a specific point.
   * This method uses the pythagorean theorum to calculate
   * the distance.
   * 
   * @param x The x coordinate
   * @param y The y coordinate
   * @return The distance.
   */
  public int proximity(int x, int y) {
    int xdiff = xpos - x;
    int ydiff = ypos - y;
    return(int)Math.sqrt( xdiff*xdiff + ydiff*ydiff );
  }
  
  /**
   * Iterates through each city, memorizing the distances for future reference
   * @param otherCities: List of all cities. If current city included in input, it will be ignored
   */
  public void calculateCityDistances(City[] otherCities)
  {
	  cityDistances = new HashMap<Integer,Integer>(otherCities.length-1);
	  citiesRankedByName = new ArrayList<Integer>(otherCities.length - 1);
	  citiesRankedByDistance = new ArrayList<Integer>(otherCities.length - 1);
	  
	  for(int cityIndex = 0; cityIndex < otherCities.length; ++cityIndex)
	  {
		  if(this != otherCities[cityIndex]) // Don't do for this city
		  {
			  int index = 0;
			  int distance = proximity(otherCities[cityIndex]);
			  while(index < citiesRankedByDistance.size() && citiesRankedByDistance.get(index) < distance)
				  ++index;

			  citiesRankedByName.add(index, cityIndex);
			  citiesRankedByDistance.add(index, distance);  
		  }
	  }
	  
	  // Fill the map
	  int i = 0;
	  for(Integer cityName : citiesRankedByName)
	  {
		  cityDistances.put(cityName, citiesRankedByDistance.get(i));
		  ++i;
	  }
  }
  
  /**
   * Returns the nth closest city to itself
   * @param n: the nth closest city (Range: [1,city count - 1]
   * @param remainingCities: The current status of the cities in order (i.e [1,2,3]) - if null: visited. if 1: not-visited 
   * @return: The nth closest city, excluding those already visited
   */
  public int getNthClosestCity(int n, Integer[] remainingCities)
  {
	  int i = -1;
	  int processedCities = 0;
	  while(processedCities != n)
	  {
		  while(remainingCities[citiesRankedByName.get(++i)] == null);
		  processedCities++;
	  }
	  
	  return citiesRankedByName.get(i);
  }  
}
