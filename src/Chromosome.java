public abstract class Chromosome 
{
  /**
   * The list of cities, which are the genes of this
   * chromosome.
   */
  protected int [] cityList;

  /**
   * The cost of following the cityList order of this
   * chromosome.
   */
  protected double cost;
  
  /**
   * The cities
   */
  protected City[] cities;
      
  /**
   * @param cities: The list of cities. Order is important
   */
  Chromosome(City[] cities) 
  {
    this.cities = cities;
    cityList = new int[cities.length];
  }
  
  /**
   * Copy Constructor
   * @param other: Other Chromosome to copy
   */
  Chromosome(Chromosome other)
  {
	  this.cities = new City[other.cities.length]; // Its immutable so this is fine
	  for(int i = 0; i < cities.length; ++i)
		  this.cities[i] = other.cities[i];
	  this.cityList = new int[cities.length];
	  for(int i = 0; i < cities.length ; ++i)
	  {
		  this.cityList[i] = other.getCity(i);
	  }
	  this.cost = other.getCost();
  }

/**
   * Calculate the cost of visiting the cities in the currently configured order (cityList)
   * The method also checks that:
   *   1. All cities are visited
   *   2. Each city is visited ONLY once
   */
  public void calculateCost() {
	  cost=0;
	  for ( int i=0;i<cityList.length-1;i++ ) {
		  double dist = cities[cityList[i]].proximity(cities[cityList[i+1]]);
	      cost += dist;
	  }	
  }

  /**
   * Get the cost for this chromosome. This is the
   * amount of distance that must be traveled.
   */
  public double getCost() {
    return cost;
  }

  /**
   * @param i The city you want.
   * @return The ith city.
   */
  public int getCity(int i) {
    return cityList[i];
  }
  
  /**
   * Get the list of cities
   * @return All cities that need to be visited
   */
  City[] getCities()
  {
	  return cities;
  }

  /**
   * Set the order of cities that this chromosome
   * would visit.
   * 
   * @param list A list of cities.
   */
  void setCities(int [] list) {
    for ( int i=0;i<cityList.length;i++ ) {
      cityList[i] = list[i];
    }
  }

  /**
   * Set the index'th city in the city list.
   * 
   * @param index The city index to change
   * @param value The city number to place into the index.
   */
  void setCity(int index, int value) {
    cityList[index] = value;
  }
  
  /**
   * Abstract method to force implementation of mating in derived classes
   * @param chromosome: The chromosome with which to mate
   * @return An array of child Chromosomes resulting from the mating
   */
  public abstract Chromosome[] mate(Chromosome chromosome);
  
  /**
   * Abstract method to force implementation of mutation in derived classes
   */
  public abstract void mutate();
  
  @Override 
  public String toString()
  {
	  StringBuilder sb = new StringBuilder("[");
	  for(int i : cityList)
	  {
		  if(i != cityList[0])
			  sb.append(", ");
		  sb.append(i);
	  }
	  sb.append("]");
	  return sb.toString();
  }

  /**
   * Sort the chromosomes by their cost.
   * 
   * @param chromosomes An array of chromosomes to sort.
   * @param num How much of the chromosome list to sort.
   */
  public static void sortChromosomes(Chromosome[] chromosomes,int num) {
    Chromosome ctemp;
    boolean swapped = true;
    while ( swapped ) {
      swapped = false;
      for ( int i=0;i<num-1;i++ ) {
        if ( chromosomes[i].getCost() > chromosomes[i+1].getCost() ) {
          ctemp = chromosomes[i];
          chromosomes[i] = chromosomes[i+1];
          chromosomes[i+1] = ctemp;
          swapped = true;
        }
      }
    }
  }
  
  /**
   * Sort all chromosomes
   * 
   * @param chromosomes An array of chromosomes to sort.
   */
  public static void sortChromosomes(Chromosome[] chromosomes) {
	  sortChromosomes(chromosomes, chromosomes.length);
  }
}