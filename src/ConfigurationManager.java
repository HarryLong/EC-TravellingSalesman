

/**
 * Singleton Configuration manager class
 * 
 * @author Harry Long
 *
 */
public class ConfigurationManager {
	
	public static enum EvolutionaryAlgorithm { GENETIC_ALGORITHM, EVOLUTIONARY_PROGRAMMING };

	private static ConfigurationManager _instance;
	
	public static final String HELP = "-h";
	public static final String K = "-K";
	public static final String Q = "-Q";
	public static final String POPULATION_SIZE = "-populationSize";
	public static final String CHILDREN_PER_GENERATION = "-childrenPerGeneration";
	public static final String PROBABILITY_OF_SELECTING_FITTEST_PARENT = "-probabilityOfSelectingFittestParent";
	public static final String PROBABILITY_OF_CROSSOVER = "-probabilityOfCrossover";
	public static final String PROBABILITY_OF_MUTATION = "-probabilityOfMutation";
	public static final String CITY_COUNT = "-cityCount";
	public static final String ALGORITHM = "-algorithm";
		
	// Settings with default values
	private int k = 10; // Number of genotypes to select for tournament selection of parents 
	private int q = 1; // Number of child genotypes to include for survivor tournament selection 
	private int populationSize = 1000;
	private int childrenPerGeneration = 700;
	private double probabilityOfSelectingFittestParent = 0.6d; // When tournament selection for parent
	private double probabilityOfCrossover = 0.1d;
	private double probabilityOfMutation = 0.01d;
	private int cityCount = 25;
	private EvolutionaryAlgorithm algorithm = EvolutionaryAlgorithm.GENETIC_ALGORITHM;
	
	/**
	 * Get the singleton instance 
	 * @return
	 */
	public static ConfigurationManager instance()
	{
		if(_instance == null)
			_instance = new ConfigurationManager();
		
		return _instance;
	}
	
	private ConfigurationManager()
	{	
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("*********************************************\n");
		sb.append("Algorithm: " + (algorithm == EvolutionaryAlgorithm.EVOLUTIONARY_PROGRAMMING ? "Evolutionary Programming\n" : 
			"Genetic Algorithm\n"));
		sb.append("City count " + cityCount + "\n");
		sb.append("Population size: " + populationSize + "\n");
		sb.append("K: " + k + "\n");
		sb.append("Q: " + q + "\n");
		sb.append("Children Per Generation: " + childrenPerGeneration + "\n"); 
		sb.append("Probability of selecting fittest parent: " + probabilityOfSelectingFittestParent*100 + "%\n");
		sb.append("Probability of crossover: " + probabilityOfCrossover*100 + "%\n");
		sb.append("Probability of mutation: " + probabilityOfMutation*100 + "%\n");
		sb.append("*********************************************\n");
		
		return sb.toString();
	}
	
	/**
	 * Print help menu when used through the command line
	 */
	public void printHelp()
	{
		System.out.println("Possible Arguments: ");
		System.out.println("-h : Print this help screen");
		System.out.println("-debugMode: ON|OFF");
		System.out.println("-K: Number of genotypes to select for tournament selection of parents");
		System.out.println("-Q: Number of child genotypes to include with winning pair for survivor selection tournament");
		System.out.println("-populationSize: The population size");
		System.out.println("-childrenPerGeneration: The number of children to generate at each generation");
		System.out.println("-probabilityOfSelectingFittestParent: The probability of selecting the fittest parent out of the <K> possible parents during tournament selection (in %)");
		System.out.println("-probabilityOfCrossover: The probability of performing crossover (in %)");
		System.out.println("-probabilityOfMutation: The probability of performing mutation (in %)");
		System.out.println("-cityCount: The number of cities");
		System.out.println("-algorithm: GA|EP --> The algorithm to use (Genetic Algorithm or Evolutionary Programming)");
	}
	
	/**
	 * Parses input from the command line as configuration arguments
	 * @param args: The arguments passed through the command line
	 */
	public void parse(String[] args)
	{
		if(args.length == 1 && args[0].trim().toLowerCase().equals(HELP))
		{
			printHelp();
			return;
		}
		
		for(int i = 0 ; i < args.length; i += 2)
		{
			String nextArg = args[i+1].trim().toLowerCase();
			switch(args[i]){
			case K:
				setK(Integer.valueOf(nextArg));
				break;
			case Q:
				setQ(Integer.valueOf(nextArg));
				break;
			case POPULATION_SIZE:
				setPopulationSize(Integer.valueOf(nextArg));
				break;
			case CHILDREN_PER_GENERATION:
				setChildrenPerGeneration(Integer.valueOf(nextArg));
				break;
			case PROBABILITY_OF_SELECTING_FITTEST_PARENT:
				setProbabilityOfSelectingFittestParent(Double.valueOf(nextArg)/100d);
				break;
			case PROBABILITY_OF_MUTATION:
				setProbabilityOfMutation(Double.valueOf(nextArg)/100d);
				break;
			case CITY_COUNT:
				setCityCount(Integer.valueOf(nextArg));
				break;
			case ALGORITHM:
				if(nextArg.equals("ga"))
					setAlgorithm(EvolutionaryAlgorithm.GENETIC_ALGORITHM);
				else
					setAlgorithm(EvolutionaryAlgorithm.EVOLUTIONARY_PROGRAMMING);
				break;
			}
		}
		System.out.println(this);
	}

	/**
	 * Creates a filename of the current configuration
	 * @return
	 */
	public String getAsFilename()
	{
		return "K_" + k + "_Q_" + q + "_CPG_" + childrenPerGeneration + "_PFP_" + probabilityOfSelectingFittestParent + 
				"_PCO_" + probabilityOfCrossover + "_PM_" + probabilityOfMutation + "_PS_" + populationSize + "_CC_" + cityCount+ ".csv";
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public int getChildrenPerGeneration() {
		return childrenPerGeneration;
	}

	public void setChildrenPerGeneration(int childrenPerGeneration) {
		this.childrenPerGeneration = childrenPerGeneration;
	}

	public double getProbabilityOfSelectingFittestParent() {
		return probabilityOfSelectingFittestParent;
	}

	public void setProbabilityOfSelectingFittestParent(
			double probabilityOfSelectingFittestParent) {
		this.probabilityOfSelectingFittestParent = probabilityOfSelectingFittestParent;
	}

	public double getProbabilityOfMutation() {
		return probabilityOfMutation;
	}

	public void setProbabilityOfMutation(double probabilityOfMutation) {
		this.probabilityOfMutation = probabilityOfMutation;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getCityCount() {
		return cityCount;
	}

	public void setCityCount(int cityCount) {
		this.cityCount = cityCount;
	}

	public EvolutionaryAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(EvolutionaryAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
}
