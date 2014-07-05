

import java.util.ArrayList;

public class GAChromosome extends Chromosome {

	public GAChromosome(City[] cities) {
		super(cities);
		initializeChromosome();
	}

	/**
	 * Finds a cycle of corresponding cities between the two parents
	 * Example:
	 * [1,3,5,2,7,6,4]
	 * [7,6,5,4,3,2,1]
	 * 1 --> 7 --> 3 --> 6 --> 2 --> 4 --> 1
	 * This cycle is then used in the mate method for crossover to produce children
	 */
	ArrayList<Integer> findCycle(Chromosome wife)
	{
		int pos = 0;
		ArrayList<Integer> fCycle = new ArrayList<Integer>();
		boolean cycle = false;
		boolean same = true;
		while (same) {
			if ((this.cityList[pos] == wife.cityList[pos])) {
				if (pos < this.cityList.length - 1) {
					pos += 1;
				} else {
					same = false;
				}
			}
			else
				same = false;
		}
		int posStart = pos;
		int startNum = this.cityList[posStart];
		fCycle.add(startNum);
		while (!cycle) {

			if (wife.cityList[pos] != this.cityList[posStart]) {
				fCycle.add(wife.cityList[pos]);
				boolean found = false;
				int look = 0;
				while (!found) {
					if (this.cityList[look] == wife.cityList[pos]) {
						found = true;
						pos = look;
					} else
						look += 1;
				}
			} else {
				cycle = true;
			}
		}
		return fCycle;
	}

	@Override
	public Chromosome[] mate(Chromosome wife) {
		/**
		 1. The cycle of corresponding cities between the two parents is found
		 2. The cityLists of both children are initialized to '-1' in every position
		 3. Child 1 is populated with the cities found in the Cycle. The positions of which are the same as
		 	that of Parent 1.
		 4. The rest of the positions in Child 1 that are '-1' are populated with the corresponding positions of 
		 	Parent 2.
		 5. Child 2 is the opposite of Child 1 i.e if the city in position 0 of child 1 is the same as that of parent 2, 
		 	the city in position 0 of child 2 will be the same as that of parent 1.
		 	
		 */
		ArrayList<Integer> cycle = findCycle(wife);
		Chromosome[] children = new Chromosome[2];
		children[0] = new GAChromosome(this.cities);
		children[1] = new GAChromosome(wife.cities);
		children[0].cityList = initializeCityList(cities.length);
		children[1].cityList = initializeCityList(cities.length);

		int number, j;
		boolean found;
		for (int i = 0; i < cycle.size(); i++) {
			number = cycle.get(i);
			j = 0;
			found = false;
			while (!found) {
				if (this.cityList[j] == number) {
					children[0].cityList[j] = number;
					found = true;
				} else {
					j++;
				}
			}
		}
		for (int k = 0; k < children[0].cityList.length; k++) {
			if (children[0].cityList[k] == -1) {
				children[0].cityList[k] = wife.cityList[k];
			}
		}
		for (int l = 0; l < children[1].cityList.length; l++) {
			if (children[0].cityList[l] == this.cityList[l]) {
				children[1].cityList[l] = wife.cityList[l];
			} else {
				children[1].cityList[l] = this.cityList[l];
			}
		}

		return children;
	}

	@Override
	public
	/**
	 * Each child produced in the generation has a chance of mutation.
	 * For each child, 5% of the cities have a 1% chance of swapping places with other cities.
	 */
	void mutate()
	{
		double rand, city1, city2;
		int temp;
		int numberMutate = (int) (this.cityList.length * 0.05);
		for (int i = 0; i < numberMutate ; i++) {
			rand = Math.random();
			if (rand <= 0.01) {
				city1 = (Math.random() * this.cities.length);
				city2 = (Math.random() * this.cities.length);
				int c1 = (int) city1;
				int c2 = (int) city2;
				temp = this.cityList[c1];
				this.cityList[c1] = this.cityList[c2];
				this.cityList[c2] = temp;

			}
		}
	}

	int[] initializeCityList(int numCities) {
		int[] intList = new int[numCities];
		for (int i = 0; i < intList.length; i++) {
			intList[i] = -1;
		}
		return intList;
	}

	/**
	 * Initializes the city list of the chromosome by randomly selecting a previously unselected city for the next position in the list.
	 */
	protected void initializeChromosome() {
		int[] citiesUsed = initUsedCities();
		for (int i = 0; i < cities.length; i++) {
			int r = (int) (Math.random()*cities.length);
			if (citiesUsed[r] == 1) {
				cityList[i] = r;
				citiesUsed[r] = 0;
			} else if (citiesUsed[r] == 0) {
				boolean found = false;
				int p = r, q = r;

				if (r >= 1) {
					p = r - 1;
				}
				if (r < citiesUsed.length - 1) {
					q = r + 1;
				}
				while (!found) {
					if (citiesUsed[p] == 1) {
						cityList[i] = p;
						citiesUsed[p] = 0;
						found = true;
					} else if (citiesUsed[q] == 1) {
						cityList[i] = q;
						citiesUsed[q] = 0;
						found = true;
					} else {
						if (p > 0) {
							p -= 1;
						}
						if (q < citiesUsed.length - 1) {
							q += 1;
						}
					}
				}
			}
		}
	}

	protected int[] initUsedCities() {
		int[] citiesUsed = new int[cities.length];
		for (int i = 0; i < cities.length; i++) {
			citiesUsed[i] = 1;
		}
		return citiesUsed;
	}
}