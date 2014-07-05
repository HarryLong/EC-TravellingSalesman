


/**
 * Implementation of a state table used for the Evolutionary Programming
 * 
 * @author harry
 *
 */

public class StateTable {
	
	private short startState;
	private Short[][] table;

	StateTable(int nStates, int nInputs) {
		table = new Short[nStates][nInputs];
	}

	/**
	 * Copy constructor
	 * @param other: The template state table to copy
	 */
	public StateTable(StateTable other) {
		this.startState = other.getStartState();
		table = new Short[other.getNumbeOfStates()][other.getNumberOfInputs()];
		for (int x = 0; x < other.getNumbeOfStates(); ++x) {
			for (int y = 1; y < other.getNumberOfInputs(); ++y) {
				setOutput(x, y, other.getOutput(x, y));
			}
		}
	}

	/**
	 * Given a state and an input, returns the corresponsing output
	 * @param currentState
	 * @param input
	 * @return: The corresponding output
	 */
	public short getOutput(int currentState, int input) {
		return table[currentState][input];
	}

	public void setOutput(int state, int input, Short output) {
		if (output != null) {
			table[state][input] = output;
		}
	}

	public short getStartState() {
		return startState;
	}

	public void setStartState(short state) {
		this.startState = state;
	}

	public int getNumbeOfStates() {
		return table.length;
	}

	public int getNumberOfInputs() {
		return table[0].length;
	}

	/**
	 * Utility method to write the state table to a file 
	 * @param filename
	 */
	public void writeToFile(String filename) {
		Utils.writeCSV(table, filename);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < table.length; ++x) {
			for (int y = 0; y < table[x].length; ++y) {
				if (y != 0)
					sb.append(" , ");
				sb.append(table[x][y]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}