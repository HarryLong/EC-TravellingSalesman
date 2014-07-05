

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TournamentSelector {
	
	Chromosome[] winningChromosomes;
	
	/**
	 * Constructor which also starts the tournament selection mechanism
	 * @param - chromosomes: List of all chromosomes which can be selected for the tournament
	 * <b>Important: Each chromosome which wins a tournament and so is deemed selected is removed from this initial list</b>
	 * @param - nRequestedChromosomes: The number of chromosomes wanted at the end (i.e number of tournaments to run)
	 * @param - tournamentSize: The number of genotypes to include for each tournament
	 * @param - probabilityOfChosingElite: The probability of choosing the fitest genotype in each tournament
	 */
	TournamentSelector(List<Chromosome> chromosomes, int nRequestedChromosomes, int tournamentSize, double probabilityOfChosingElite)
	{	
		if(nRequestedChromosomes > chromosomes.size())
		{
			System.err.println("Can't get " + nRequestedChromosomes + " from a total population of " + chromosomes.size() + " chromosomes. Exiting");
			System.exit(1);
		}
		
		List<Integer> validIndices = new ArrayList<Integer>(chromosomes.size());
		for(int i = 0 ; i < chromosomes.size(); ++i)
			validIndices.add(i);
		
		winningChromosomes = new Chromosome[nRequestedChromosomes];
		
		for(int i = 0; i < nRequestedChromosomes; ++i)
		{
			int thisTournamentSize = Math.min(validIndices.size(), tournamentSize);
			Chromosome[] tournament = new Chromosome[thisTournamentSize];
			List<Integer> tournamentIndices = new ArrayList<Integer>();
			Map<Chromosome, Integer> chromosomeToIdxMapping = new HashMap<Chromosome, Integer>();
			
			// Create the tournament
			for(int ii = 0; ii < thisTournamentSize; ++ii)
			{
				int randomIndex = validIndices.get(Utils.random(0, validIndices.size()-1));
				Chromosome selectedC = chromosomes.get(randomIndex);
				tournament[ii] = selectedC;
				
				// Update indices
				tournamentIndices.add(randomIndex);
				validIndices.remove(Integer.valueOf(randomIndex));

				chromosomeToIdxMapping.put(selectedC, randomIndex);
			}
			
			Chromosome.sortChromosomes(tournament); // Get fittest
			
			Chromosome winner;
			if(thisTournamentSize < 2 || Utils.getTrueWithProbability(probabilityOfChosingElite))
			{
				winner = tournament[0];
			}
			else // Select at random from the rest
			{
				winner = tournament[Utils.random(1, thisTournamentSize-1)];
			}
			winningChromosomes[i] = winner;

			// Remove winning chromosome index from list of tournament indices
			tournamentIndices.remove(Integer.valueOf(chromosomeToIdxMapping.get(winner)));
			
			// Re-append all but the winning chromosome index to the list of valid indices
			validIndices.addAll(tournamentIndices);
		}
		
		// Remove all parent chromosomes from original list
		for(Chromosome c : winningChromosomes)
			chromosomes.remove(c);
	}
	
	/**
	 * Get the winners from the tournament
	 * @return: The genotypes which survived the tournament
	 */
	public Chromosome[] getWinners()
	{
		return winningChromosomes;
	}
}
