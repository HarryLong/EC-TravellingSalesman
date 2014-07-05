

import org.junit.Assert;
import org.junit.Test;


public class TestModule {

	@Test
	public void testFSMMutation() {
		ConfigurationManager.instance().setProbabilityOfMutation(0.5d);
		
		City[] cities = Utils.generateCityList(30,100,100);
		EPChromosome chromosome  = new EPChromosome(cities);
	
		StateTable initialST = new StateTable(chromosome.getStateTable());
		
		chromosome.mutate();
		
		StateTable finalST = chromosome.getStateTable();
		
		// Ensure they have same number of states and inputs
		Assert.assertEquals(initialST.getNumbeOfStates(), finalST.getNumbeOfStates());
		Assert.assertEquals(initialST.getNumberOfInputs(), finalST.getNumberOfInputs());
		
		double mutationCount = 0;
		
		for(int x = 0; x < finalST.getNumbeOfStates(); ++x)
			for(int y = 1; y < finalST.getNumberOfInputs(); ++y)
			{
				if(finalST.getOutput(x, y) != initialST.getOutput(x, y))
					++mutationCount;
			}
		
		double percentMutated = mutationCount/(finalST.getNumbeOfStates()*(finalST.getNumberOfInputs()-1))*100;
		
		Assert.assertTrue(percentMutated > 30 && percentMutated < 70); // Be leniant with the percentage as a mutation can mutate to itself
	}
	
	@Test
	public void testFSMCrossover() {		
		City[] cities = Utils.generateCityList(30,100,100);
		
		EPChromosome[] parents  = new EPChromosome[2];
		parents[0] = new EPChromosome(cities);
		parents[1] = new EPChromosome(cities);
		StateTable parent1ST = parents[0].getStateTable();
		StateTable parent2ST = parents[1].getStateTable();

		// mate
		int seperationIndex = Utils.random(2, parents[0].getStateTable().getNumberOfInputs());;
		EPChromosome[] children = (EPChromosome[])parents[0].mate(parents[1], seperationIndex);
		
		StateTable child1ST = children[0].getStateTable();
		StateTable child2ST = children[1].getStateTable();
		
		// Number of states
		Assert.assertEquals(child1ST.getNumbeOfStates(), parent1ST.getNumbeOfStates());
		Assert.assertEquals(child1ST.getNumbeOfStates(), parent2ST.getNumbeOfStates());
		Assert.assertEquals(child1ST.getNumbeOfStates(), child2ST.getNumbeOfStates());
		
		// Number of inputs
		Assert.assertEquals(child1ST.getNumberOfInputs(), parent1ST.getNumberOfInputs());
		Assert.assertEquals(child1ST.getNumberOfInputs(), parent2ST.getNumberOfInputs());		
		Assert.assertEquals(child1ST.getNumberOfInputs(), child2ST.getNumberOfInputs());		
		
		Assert.assertTrue(child1ST.getStartState() == parent2ST.getStartState());
		Assert.assertTrue(child2ST.getStartState() == parent1ST.getStartState());
		
		for(int state = 0; state < child1ST.getNumbeOfStates(); ++state)
		{
			for(int input = 1; input < child1ST.getNumberOfInputs(); ++input)
			{
				if(input < seperationIndex)
				{
					Assert.assertTrue(child1ST.getOutput(state, input) == parent2ST.getOutput(state, input)); 
					Assert.assertTrue(child2ST.getOutput(state, input) == parent1ST.getOutput(state, input)); 
				}
				else
				{
					Assert.assertTrue(child2ST.getOutput(state, input) == parent2ST.getOutput(state, input)); 
					Assert.assertTrue(child1ST.getOutput(state, input) == parent1ST.getOutput(state, input)); 
				}
			}
		}
	}
	
	@Test
	public void testCityDistanceCalculator() {
		int cityCount = 20;
		City[] cities = Utils.generateCityList(cityCount, 100, 100);
		Integer[] visitedCitiesTracker = new Integer[cities.length];
		for(int i = 0 ; i < cities.length; ++i)
			visitedCitiesTracker[i] = 1;
		
		for(City city : cities)
		{
			double previousDistance = -1;
			for(int cityIndex = 0; cityIndex < cityCount-1; ++cityIndex)
			{
				int index = city.getNthClosestCity(cityIndex+1, visitedCitiesTracker);
				double distance = city.proximity(cities[index]);
				Assert.assertTrue(distance >= previousDistance);
				previousDistance = distance;
			}
		}
	}
	
	@Test
	public void testChromosomeSorting()
	{	
		City[] cities = Utils.generateCityList(30,100,100);
		EPChromosome[] chromosomes  = new EPChromosome[10];
		for(int i = 0; i < 10; ++i)
			chromosomes[i] = new EPChromosome(cities);
		
		Chromosome.sortChromosomes(chromosomes);
		double cost1 = chromosomes[0].getCost();
		double cost2 = chromosomes[1].getCost();
		Assert.assertTrue(cost1 <= cost2);
		for(int i = 2 ; i < chromosomes.length; ++i)
		{
			Assert.assertTrue(cost2 <= chromosomes[i].getCost());
		}
	}
}