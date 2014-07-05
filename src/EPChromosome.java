

/**
 * Class that implements Genotype logic for Evolutionary Programming
 * 
 * @author Harry Long
 */


public class EPChromosome extends Chromosome{
	
	private StateTable stateTable;
	private double probabilityOfMutation;
	
	public EPChromosome(City[] cities) {
		super(cities);
		probabilityOfMutation = ConfigurationManager.instance().getProbabilityOfMutation();
		
		// Initialize the state table with random values
		stateTable = new StateTable(cities.length, cities.length);
		stateTable.setStartState((short) Utils.random(0, cities.length-1));
		for(int cityIndex = 0; cityIndex < cities.length; cityIndex++)
		{
			for(int visitedCities = 1; visitedCities < cities.length; ++visitedCities) // 0 visited cities is not possible as wouldn't be on the ST
			{
				stateTable.setOutput(cityIndex,visitedCities,(short) Utils.random(1,cities.length-visitedCities));
			}
		}
		// Generate city list + calculates the cost
		calculateCost();		
	}
	
	/**
	 * Copy constructor
	 * @param other: Template chromosome for the copy
	 */
	public EPChromosome(Chromosome other)
	{
		super(other);
		stateTable = new StateTable(((EPChromosome)other).getStateTable());
		probabilityOfMutation = ConfigurationManager.instance().getProbabilityOfMutation();	
	}
	
	/**
	 * Using the state table, generates the order in which the cities should be visited
	 * 
	 * It would be more readable to use a list of visited cities and pass that to the
	 * City class when getting the Nth city. This is a big performance bottleneck however
	 * as removing elements from a list is of O(n^2). In stead this "hack" is used where 
	 * a new array represents the cities in the range [0,n), where the cities index
	 * is nullified in this array upon visiting it. 
	 */
	protected void generateCityList()
	{
		int currentCity = stateTable.getStartState();
		cityList[0] = currentCity;
		Integer remainingCityTracker[] = new Integer[cities.length];
		for(int i = 0; i < cityList.length; ++i)
		{
			if(i != currentCity) // Current city must not be appended
				remainingCityTracker[i] = 1;
		}
		
		for(int i = 1; i < cities.length; ++i)
		{
			short nThClosestCity = stateTable.getOutput(currentCity, i); 
			// We move to the new city here:
			currentCity = cities[currentCity].getNthClosestCity(nThClosestCity, remainingCityTracker);
			
			// We update the cityList
			cityList[i] = currentCity;
			
			remainingCityTracker[currentCity] = null; // nullify it
		}
	}
	
	public StateTable getStateTable()
	{
		return stateTable;
	}
	
	/**
	 * Overrided cost calculation method which forces the re-generation of the city list before calculating the cost
	 */
	@Override 
	public void calculateCost() {
		generateCityList();
		super.calculateCost();
	}

	/**
	 * Given another genotype, creates 2 new genotypes through crossover
	 */
	@Override
	public Chromosome[] mate(Chromosome wife) 
	{
		return mate(wife, Utils.random(1, stateTable.getNumberOfInputs()));

	}
	
	/**
	 * Given another genotype, creates 2 new genotypes through crossover from
	 * input 0 to <seperation index>.
	 * Separate method for testing purposes
	 */
	public Chromosome[] mate(Chromosome wife, int seperationIndex) 
	{
		EPChromosome[] children = new EPChromosome[2];
		children[0] = new EPChromosome(this); // Dad 
		children[1] = new EPChromosome(wife);
		StateTable child1ST = children[0].getStateTable();
		StateTable child2ST = children[1].getStateTable();
		
		if(seperationIndex > 1)
		{
			short tmp = child2ST.getStartState();
			child2ST.setStartState(child1ST.getStartState());
			child1ST.setStartState(tmp);
			
			for(int state = 0; state < stateTable.getNumbeOfStates(); ++state)
			{
				for(int input = 1; input < seperationIndex; ++input)
				{
					tmp = child2ST.getOutput(state, input);
					child2ST.setOutput(state, input, child1ST.getOutput(state,input));
					child1ST.setOutput(state, input, tmp);
				}
			}
		}
		return children;
	}
	
	/**
	 * This will mutate each cell of the state table with a probability P specified in the configuration
	 */
	@Override
	public void mutate()
	{
		int numberOfCellsToMutate = 
				(int) (probabilityOfMutation * stateTable.getNumbeOfStates()*stateTable.getNumberOfInputs());
		if(Utils.getTrueWithProbability(probabilityOfMutation))// Possible mutation of start state
		{
			stateTable.setStartState((short) Utils.random(0, stateTable.getNumbeOfStates()-1)); 
		}
		for(int i = 0 ; i < numberOfCellsToMutate; ++i)
		{
			int randomState = Utils.random(0,cities.length-1);
			int randomInput = Utils.random(1,cities.length-2);
			stateTable.setOutput(randomState, randomInput, (short) Utils.random(1,cities.length-randomInput));
		}
	}
}