/**
 * Various utility methods used throughout the application
 * 
 * @author Harry & Paul
 * 
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



public class Utils {
	/**
	 * 
	 * @param from : the lower range (inclusive)
	 * @param to : the upper range (inclusive)
	 * @return : A random value in the range [from,to]
	 */
	public static int random(int from, int to)
	{
		return from + (int) (Math.random() * (to - from + 1));
	}
	
	/**
	 * Outputs the content of a 2D array to a CSV file
	 * 
	 * @param data - the data to write to the CSV file
	 * @param filename - the name of the CSV file to create
	 */
	public static <T> void writeCSV(T[][] data, String filename)
	{
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		    for(int x = 0; x < data.length ; ++x)
		    {
		    	for(int y = 0; y < data[x].length; ++y)
		    	{
		    		bw.write(data[x][y] +  " , ");
		    	}
		    	bw.newLine();
		    }
		} catch (IOException ex) {
			System.err.println("Unable to write to file: " + filename);
			ex.printStackTrace();
			return;
		} finally {
			try 
			{
				bw.close();
			} catch (Exception ex) {} 
		}
	}
	
	/**
	 * Returns true with the specified probability
	 * 
	 * @param probability: As a double ranging from 0.0 (0%) to 1.0 (100%)
	 * @return
	 */
	public static boolean getTrueWithProbability(double probability)
	{
		return (Math.random() <= probability ? true : false);
	}
	
	/**
	 * Prints the content of a list in human-readable form
	 * 
	 * @param data: The list to print
	 */
	public static <T> void printList(List<T> data) {
		for (int i = 0; i < data.size(); i++) {
			System.out.print("--> " + data.get(i));
		}
		System.out.println();
	}

	/**
	 * Prints the content of an array in human-readable form
	 * 
	 * @param data: The list to print
	 */
	public static <T> void printArr(T[] data) {
		for (int i = 0; i < data.length; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
	}
	
	/**
	 * Generates a list of cities randomly located in the specified dimensions
	 * 
	 * @param cityCount: Amount of cities to generate
	 * @param xBound: The maximum horizontal point
	 * @param yBound: The maximum vertical point
	 * @return: The list of randomly generated cities in the specified dimensions
	 */
	public static City[] generateCityList(int cityCount, int xBound, int yBound)
	{
		City[] cities = new City[cityCount];
		for (int i = 0; i < cityCount; i++) {
			cities[i] = new City(
					(int) (Math.random() * xBound),
					(int) (Math.random() * yBound),
					i);
		}
		for(City c : cities)
		{
			c.calculateCityDistances(cities);
		}
		
		return cities;
	}
	
	/**
	 * Implements a tuple collection <X,Y> such that:
	 *   - Upon creation all tuples are valid (i.e they are paired)
	 *   - When removing an X, it retains the Y value in a seperate collection and invalidates the given pair
	 *   - The Collection of remaining Ys can be accessed easily and efficiently 
	 *  
	 * The main use for this class is for pairing genotypes by Parent/Child pair
	 * 
	 * @author harry
	 *
	 * @param <T>
	 */
	public static class TupleCollection<T>
	{
		private final List<T> totalXList;
		private final List<T> totalYList;
		
		private List<T> remainingYList;
		private List<T> solitaryYs;

		List<Integer> validPairIndices;
		
		int lastSelectedPairIndex; // For performance optimization
		
		public TupleCollection(T[] xs, T[] ys)
		{
			totalXList = new ArrayList<T>(Arrays.asList(xs));
			totalYList  = new ArrayList<T>(Arrays.asList(ys));
			remainingYList = new ArrayList<T>(Arrays.asList(ys));
			solitaryYs = new ArrayList<T>();
			lastSelectedPairIndex = -1;
			
			validPairIndices = new ArrayList<Integer>();
			for(int i = 0; i < totalYList.size() ; ++i)
				validPairIndices.add(i);
		}
		
		public List<T> getYs()
		{
			return remainingYList;
		}
		
		public int remainingPairs()
		{
			return validPairIndices.size();
		}
		
		public void remove(T toRemove)
		{			
			if(totalXList.get(lastSelectedPairIndex) == toRemove) // Winner is parent
			{
				validPairIndices.remove(Integer.valueOf(lastSelectedPairIndex));
				solitaryYs.add(totalYList.get(lastSelectedPairIndex));
				return;	
			}
			else // It is a child 
			{
				// Remove from list of children
				if(!remainingYList.remove(toRemove))
				{
					System.err.println("Could not find child to remove! This should not happen");
					System.exit(1);
				}
				
				// Option 1 : It is already a solitary child
				if(solitaryYs.contains(toRemove))
				{
					solitaryYs.remove(toRemove);
					return;
				}
				// Option 2 : It is still a valid pair - Find it + remove it
				Iterator<Integer> indexIt = validPairIndices.iterator();
				while(indexIt.hasNext())
				{
					Integer index = indexIt.next();
					if(totalYList.get(index) == toRemove)
					{
						indexIt.remove();
						return;
					}
				}
			}
			
			// Should NEVER come here
			System.err.println("Could not find child to remove! This should not happen");
			System.exit(1);
		}
		
		/**
		 * Utility method which prints the pairs in human-readable format
		 */
		public void printValidPairs()
		{
			for(Integer i : validPairIndices)
			{
				System.out.print(i + " --> ");
				System.out.print("[" + ((Chromosome)totalXList.get(i)).getCost() + " , " + ((Chromosome)totalYList.get(i)).getCost() + "]   ");
			}
		}
		
		/**
		 * Retrieve a random pair
		 * 
		 * @return
		 */
		public List<T> getRandomPair()
		{
			if(validPairIndices.isEmpty()) // No more valid pairs, return an empty list
				return new ArrayList<T>();
			
			List<T> ret = new ArrayList<>();
			int randomIndex = validPairIndices.get(Utils.random(0, validPairIndices.size()-1));
			ret.add(totalXList.get(randomIndex));
			ret.add(totalYList.get(randomIndex));
			lastSelectedPairIndex = randomIndex;
			return ret;
		}
	}
	
	/**
	 * Implements a simple timer with stop/start capabilities
	 * 
	 * @author harry
	 *
	 */
	public static class Timer{
		private long duration;
		
		private long startTime;
		public Timer()
		{
			duration = startTime = 0l;
		}
		
		public void start()
		{
			startTime = System.nanoTime();
		}
		
		public void stop()
		{
			duration += (System.nanoTime()-startTime);
		}
		
		public long getDuration()
		{
			return duration/1000000000;
		}
	}
}