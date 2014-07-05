import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the Traveling Salesman problem
 * as a Java applet.
 */
public class TravellingSalesman extends Applet
  implements Runnable {

  /**
   * Whether to run in GUI mode or command-line mode 
   */
  private boolean runInGUI;
	
  /**
	 * 
	 */
  private static final long serialVersionUID = 8081015285484713771L;

  /**
   * The part of the population selected for mating.
   */
  protected int selectedParents;

  /**
   * The current generation
   */
  protected int generation;

  /**
   * The background worker thread.
   */
  protected Thread worker = null;

  /**
   * Is the thread started.
   */
  protected boolean started = false;

  /**
   * The list of cities.
   */
  protected City [] cities;

  /**
   * The list of chromosomes.
   */
  protected Chromosome [] chromosomes;

  /**
   * The Start button.
   */
  private Button ctrlStart;

  /**
   * The TextField that holds the number of cities.
   */
  private TextField ctrlCities;

  /**
   * The TextField for the population size.
   */
  private TextField ctrlPopulationSize;

  /**
   * Holds the buttons and other controls, forms a strip across
   * the bottom of the applet.
   */
  private Panel ctrlButtons;

  /**
   * The current status, which is displayed just above the controls.
   */
  private String status = "";
  
  /**
   * The mean cost per generation
   */
  private Double[] meanPerGeneration = new Double[1000];
  
  /**
   * The configuration for this run
   */
  private ConfigurationManager config = ConfigurationManager.instance();
  
  /**
   * Keeps track of the best ever generated genotype for all runs
   */
  private Chromosome bestOfAllRuns;
  
  /**
   * Keeps track of the run number which generated the most efficient genotype (bestOfAllRuns)
   */
  private int bestRunNumber;
  
  public TravellingSalesman()
  {
	  this(true); // Default to GUI
  }
  
  public TravellingSalesman(boolean runInGUI)
  {
	  this.runInGUI = runInGUI;
  }
  
  public void init()
  {
	// Check configuration
	switch(config.getAlgorithm()){
	case EVOLUTIONARY_PROGRAMMING:
	case GENETIC_ALGORITHM:
	default:
		// Number of parents need to be pair / Number of children per generation cannot be more than number of parents
		if(config.getChildrenPerGeneration() % 2 != 0) // Has to be pair
			config.setChildrenPerGeneration(config.getChildrenPerGeneration()+1);
			
		if(config.getChildrenPerGeneration() > config.getPopulationSize())
		{
			System.err.println("Unable to select a parent population of size " + config.getChildrenPerGeneration() + " in a population of size " + config.getPopulationSize());
			System.exit(1);
		}
		break;		
	}

	if(runInGUI)
	{
			// Initialize layout
			setLayout(new BorderLayout());
			// setup the controls
			ctrlButtons = new Panel();
			ctrlStart = new Button("Start");
			ctrlButtons.add(ctrlStart);
			ctrlButtons.add(new Label("# Cities:"));
			ctrlButtons.add(ctrlCities = new TextField(5));
			ctrlButtons.add(new Label("Population Size:"));
			ctrlButtons.add(ctrlPopulationSize = new TextField(5));
			this.add(ctrlButtons, BorderLayout.SOUTH);

			// set the default values
			ctrlPopulationSize.setText("1000");
			ctrlCities.setText("50");

			// add an action listener for the button
			ctrlStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					startThread();
				}
			});
			update();
			started = false;
	}
	else // In command-line, start straight away
		startThread();
  }

  /**
   * Start the background thread.
   */
	public void startThread() {
		int xBound = 200, yBound = 200; // Defaults
		if(runInGUI)
		{
			try {
				config.setCityCount(Integer.parseInt(ctrlCities.getText()));
			} catch (NumberFormatException e) {
			} // Default will be used

			try {
				config.setPopulationSize(Integer.parseInt(ctrlPopulationSize
						.getText()));
			} catch (NumberFormatException e) {
			} // Default will be used
			FontMetrics fm = getGraphics().getFontMetrics();
			int bottom = ctrlButtons.getBounds().y - fm.getHeight() - 2;
			xBound = getBounds().width - 10;
			yBound = bottom - 10;
		}
		
		// Initialize initial city list
		cities = Utils.generateCityList(config.getCityCount(), xBound, yBound);
		
		bestOfAllRuns = null;
		
		switch(config.getAlgorithm()){
		case EVOLUTIONARY_PROGRAMMING:
			chromosomes = new EPChromosome[config.getPopulationSize()];
			for(int i = 0 ; i < chromosomes.length ; ++i)
				chromosomes[i] = new EPChromosome(cities);
			break;
		case GENETIC_ALGORITHM:
			chromosomes = new GAChromosome[config.getPopulationSize()];
			for(int i = 0 ; i < chromosomes.length ; ++i)
				chromosomes[i] = new GAChromosome(cities);
			break;
		}

		// start up the background thread
		started = true;
		generation = 0;

		if (worker != null)
			worker = null;
		worker = new Thread(this);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
	}

  /**
   * Update the display
   */
  public void update()
  {
	  if(runInGUI) // Ignore if not running in graphics mode
	  {
		  Image img = createImage(getBounds().width, getBounds().height);
		  Graphics g = img.getGraphics();
		  FontMetrics fm = g.getFontMetrics();

		  int width = getBounds().width;
		  int bottom = ctrlButtons.getBounds().y - fm.getHeight() - 2;

		  g.setColor(Color.black);
		  g.fillRect(0, 0, width, bottom);

		  if (started && (cities != null)) {
			  g.setColor(Color.green);
			  for (int i = 0; i < config.getCityCount(); i++) {
				  int xpos = cities[i].getx();
				  int ypos = cities[i].gety();
				  g.fillOval(xpos - 5, ypos - 5, 10, 10);
			  }

			  g.setColor(Color.white);
			  for (int i = 0; i < config.getCityCount(); i++) {
				  int icity = chromosomes[0].getCity(i);
				  if (i != 0) {
					  int last = chromosomes[0].getCity(i - 1);
					  g.drawLine(cities[icity].getx(), cities[icity].gety(),
							  cities[last].getx(), cities[last].gety());
				  }
			  }
		  }

		  g.drawString(status, 0, bottom);
		  getGraphics().drawImage(img, 0, 0, this);  
	  }
  }

  /**
   * Update the status.
   *
   * @param status The status.
   */
  public void setStatus(String status)
  {
    this.status = status;
  }

  public void paint(Graphics g)
  {
	  update();
  }
  
  /**
   * Evolves the genotypes by a single generation using Evolutionary Programming
   */
  private void evolveThroughEP()
  {
	  int nParents = config.getChildrenPerGeneration(); // 2-2 Relationship
	  
	  List<Chromosome> nextGenPopulation = new ArrayList<Chromosome>();
	  
	  //*** PARENT SELECTION ***//
	  List<Chromosome> unusedPopulation = new ArrayList<Chromosome>(Arrays.asList(chromosomes)); // Population not used for mating
	  Chromosome[] parentPopulation;
	  if(nParents == chromosomes.length) // No need performing tournament selection if # parents to select == # children
	  {
		  parentPopulation = chromosomes;
		  unusedPopulation.clear();
	  }
	  else
		  parentPopulation = new TournamentSelector(unusedPopulation, nParents, config.getK(), config.getProbabilityOfSelectingFittestParent()).getWinners(); 
	  

	  //*** Mating ***//
	  Chromosome[] childPopulation = new Chromosome[nParents];
	  for(int index = 0; index < nParents-1; index +=2)
	  {
		  Chromosome[] children = parentPopulation[index].mate(parentPopulation[index+1]);
		  childPopulation[index] = children[0];
		  childPopulation[index+1] = children[1];  // Non-mutated
	  }
	  
	  //*** MUTATION ***//
	  for(Chromosome c : childPopulation) // Sort children
	  {
		  c.mutate();
		  c.calculateCost();
	  }

	  //*** SURVIVOR SELECTION ***//
	  /*
	   *  1 - Pair parents and children by fitness
	   *  2 - Randomly select a pair and select the fitest of the parent/child. Call it F
	   *  3 - Randomly select Q other children
	   *  4 - Select the fittest of Q U F 
	   *  5 - Repeat <population_size> times
	   */
	  Chromosome.sortChromosomes(parentPopulation);
	  Chromosome.sortChromosomes(childPopulation);
	  Utils.TupleCollection<Chromosome> pairedGenotypes = 
			  new Utils.TupleCollection<Chromosome>(parentPopulation, childPopulation);
	  while(nextGenPopulation.size() < config.getPopulationSize() && pairedGenotypes.remainingPairs() > 0)
	  {	
		  // Select a random pair
		  Chromosome[] pair = pairedGenotypes.getRandomPair().toArray(new Chromosome[0]);
		  
		  // Identify and select winner of pair
		  Chromosome.sortChromosomes(pair);
				
		  // Create tournament with this and x randomly selected children
		  List<Chromosome> remainingChildren = new ArrayList<Chromosome>(pairedGenotypes.getYs());
		  if(remainingChildren.contains(pair[0])) // if warrior is a child, remove it from possible tournament enemies
			  remainingChildren.remove(pair[0]);
				
		  // Tournament size has a maximum of remaining children + 1 (being the chosen chromosome warrior)
		  int tournamentSize = Math.min(config.getQ(), remainingChildren.size()) + 1;
		  Chromosome[] tournament = new Chromosome[tournamentSize];	
		  tournament[0] = pair[0]; // Add the initial warrior
		  for(int ii = 1; ii < tournamentSize; ++ii)
		  {
			  int randomIndex = Utils.random(0, remainingChildren.size()-1);
			  tournament[ii] = remainingChildren.get(randomIndex);
			  remainingChildren.remove(randomIndex);
		  }
		  
		  // Get winner from tournament
		  Chromosome.sortChromosomes(tournament);
		  
		  // Add to next generation population
		  nextGenPopulation.add(tournament[0]);

		  // Remove winner
		  pairedGenotypes.remove(tournament[0]);		  
	  }
	  
	  int i = 0;
	  while(nextGenPopulation.size() < config.getPopulationSize() && i < unusedPopulation.size())
		  nextGenPopulation.add(unusedPopulation.get(i++));
	  	  
	  chromosomes = nextGenPopulation.toArray(new Chromosome[0]);
  }
  
  /**
   * Evolves the genotypes by a single generation using Genetic Algorithms
   */
  private void evolveThroughGA()
  {
	  int nParents = 100;
	  int populationSize = config.getPopulationSize();
	  
	  // Parent Selection
	  /**
	   * Uses ranked based roulette wheel selection
	   * The fitter individuals are assigned a probability slightly higher than unfit ones
	   */
	  int[] parentsUsed = new int[chromosomes.length];
		
	  double totalCost = 0;
	  double [] relativeCost = new double[chromosomes.length];
	  for (int i=0; i< chromosomes.length; i++)
	  {
		  chromosomes[i].calculateCost();
		  totalCost += chromosomes[i].getCost(); //Calculating summed cost of generation
	  }
	  Chromosome.sortChromosomes(chromosomes, populationSize);

	  for (int i= 0; i<chromosomes.length; i++)
	  {
		  relativeCost[i] = (chromosomes[i].getCost())/totalCost; // calculating relative costs for each chromosome.
	  }

	  double sumNew = 0;
	  double[] newCosts = new double[chromosomes.length]; 
	  double [] percOfParent = new double[chromosomes.length];
	  for (int i=0; i<chromosomes.length; i++)
	  {
		  newCosts[i] = totalCost - relativeCost[i];
		  sumNew += newCosts[i];
	  }

	  double[] percentage = new double[chromosomes.length];
	  double cumPercentage = 0;
	  for (int i =0; i<chromosomes.length; i++)
	  {
		  percentage[i] = newCosts[i]/sumNew;
		  cumPercentage += percentage[i];
		  percOfParent[i] = cumPercentage;
		  parentsUsed[i] = 1; // Initializing all chromosomes to 1 (Havent been selected for mating)
	  }
	  int[] indexParents = new int[nParents];
	  Chromosome [] parentArray = new Chromosome[nParents];
	  for (int i =0; i< nParents; i++) // Choosing Parents using Roulette wheel.
	  {
		  boolean found = false;
		  int j =0;
		  while ((!found) &&(j <1000) )
		  {

			  double rand = Math.random();

			  if ((percOfParent[j] - rand) >= 0)
			  {
				  if (parentsUsed[j] == 1)
				  {
					  parentArray[i] = chromosomes[j];
					  parentsUsed[j] = 0;
					  indexParents[i] = j;
					  found = true;
				  }
				  else
				  {
					  j = 0;
					  continue;
				  }	
			  }
			  else j += 1;
		  }		  
	  }

	  // Mating of chosen parents. Each pair of parents produce a pair of children
	  Chromosome[] childPopulation = new Chromosome[nParents];
	  for (int i=0; i<nParents-1; i += 2)
	  {
		  Chromosome[] children = parentArray[i].mate(parentArray[i+1]);
		  childPopulation[i] = children[0];
		  childPopulation[i+1] = children[1];
	  }
	  for (Chromosome c : childPopulation)
	  {
		  c.mutate();
		  c.calculateCost();
	  }
	 
	  // Recombination
	  Chromosome[] allSolutions = new Chromosome[populationSize + nParents];
	  int populationIndex = 0;
	  while(populationIndex < populationSize) // Fill with parent population
	  {
		  allSolutions[populationIndex] = chromosomes[populationIndex];
		  populationIndex ++;
	  }
	  while(populationIndex < populationSize+nParents) // Fill with child population
	  {
		  allSolutions[populationIndex] = childPopulation[populationIndex-populationSize];
		  populationIndex ++;
	  }
	  Chromosome.sortChromosomes(allSolutions);
		
	  chromosomes = Arrays.copyOfRange(allSolutions, 0, populationSize); // Take best 1000 solutions to use in next generation

  }
  
  /**
   * The main loop for the background thread.
   */
  public void run() {
	  update();		

	  while (generation < 1000) {
		  // Quick summary
		  if(generation != 0 && generation%100 == 0)
		  {
			  System.out.println("*****GENERATION: " + generation + "/1000 ********");
			  System.out.println("/// Progress since start: " + getProgress(0, generation-1) + " %");
			  System.out.println("/// Progress since generation " + (generation-100) + ": " + getProgress(generation-100, generation-1) + " %");
			  System.out.println();
		  }
		  
		  switch(config.getAlgorithm()){
		  case EVOLUTIONARY_PROGRAMMING:
			  evolveThroughEP();
			  break;
		  case GENETIC_ALGORITHM:
			  evolveThroughGA();
			  break;
		  default:
			  System.err.println("Invalid Algorithm Chosen!"); // This will never happen
			  break;
		  }
		  
		  //*************STATISTIC GATHERING****************//
		  meanPerGeneration[generation] = 0d;
		  for(Chromosome c : chromosomes)
			  meanPerGeneration[generation] += c.getCost();
		  meanPerGeneration[generation] /= config.getPopulationSize();	
		  
		  Chromosome.sortChromosomes(chromosomes); // Get the absolute best for this run
		  double cost = chromosomes[0].getCost();
		  NumberFormat nf = NumberFormat.getInstance();
		  nf.setMinimumFractionDigits(2);
		  nf.setMinimumFractionDigits(2);
		  
		  // Check it its the best of all time
		  if(bestOfAllRuns == null || bestOfAllRuns.getCost() > chromosomes[0].getCost())
		  {
			  bestOfAllRuns = chromosomes[0];
			  bestRunNumber = generation;
		  }

		  setStatus("Generation " + generation + " Cost " + (int) cost);	
		  update();
		  generation++;
	  }
	  summarizeRun();
	  setStatus("Solution found after " + generation + " generations.");
	  
	  // TODO: Do you want to do this?
	  chromosomes[0] = bestOfAllRuns;
	  update();
  }
  
  /**
   * Print the entire population
   */
  public void printPopulation()
  {
	  System.out.println("******POPULATION [G: " + generation + "************");
	  for(Chromosome c : chromosomes)
		  System.out.println(c);
	  System.out.println("****************************");
  }
  
  /**
   * Summarizes the run by:
   *   - Printing various statistics gathered throughout to the console
   *   - Creating a CSV file with the mean cost at each generation in order to visualize the evolution
   */
  public void summarizeRun()
  {	  	  
	  double progress = getProgress(0, generation-1);
	  System.out.println(config);
	  System.out.println("Progress: " + progress + " %");
	  System.out.println("Minimum calculated cost from all runs: " + bestOfAllRuns.getCost() + " ( run number " + bestRunNumber + " )");
	  
	  // Write to file
//	  String filename = "/home/harry/Uni/Evolutionary Computation/Assignment 1/comparison_statistics/" + config.getAsFilename();
//	  Double[][] meansAsCSV = new Double[1][];
//	  meansAsCSV[0] = meanPerGeneration;
//	  Utils.writeCSV(meansAsCSV, filename);
  }
  
  /**
   * Get the progress from two given generations based on the mean cost
   * @param from: from generation
   * @param to: to generation
   * @return The progress (in percent)
   */
  public double getProgress(int from, int to)
  {
	  return ((meanPerGeneration[from]-meanPerGeneration[to])/meanPerGeneration[from])*100d;
  }
  
  public static void main(String[] args)
  {
	  if(args.length != 0) 
		  ConfigurationManager.instance().parse(args);
	  TravellingSalesman tsm = new TravellingSalesman(false);
	  System.out.println(ConfigurationManager.instance());
	  tsm.init();
  }
}
