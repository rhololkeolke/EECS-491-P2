package edu.cwru.sepia.agent;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.agent.mock.LearningUnit;

public class Factor {
	
	private List<Tuple<TargetedAction, Double>> JTable;

	// for now this method assumes that there will be at most 2 edges for every node in the graph
	// so it only needs to accept a single agent and a single factor
	public Factor(StateView s, HistoryView h, int playernum, LearningUnit max, LearningUnit agent, Factor f)
	{
		// stores the J values of all the possible actions for the agent currently being
		// maximized
		List<Tuple<TargetedAction, Double>> maxJTable = max.calcJTable(s, h, playernum);
		
		// get the J values of all the possible actions for the agent
		List<Tuple<TargetedAction, Double>> agentJTable = agent.calcJTable(s, h, playernum);

		List<Tuple<TargetedAction, Double>> intermediateTable1 = constructJointTable(maxJTable, agentJTable);
		
		List<Tuple<TargetedAction, Double>> intermediateTable2 = constructJointTable(maxJTable, f.getJTable());
		
		List<Tuple<TargetedAction, Double>> jointActionTable = constructJointTable(intermediateTable1, intermediateTable2);
		
		JTable = compressJointActionTable(maxJTable.size(), 2, jointActionTable);

	}
	
	// this method is used when there is no factor, just a single J function
	public Factor(StateView s, HistoryView h, int playernum, LearningUnit max, LearningUnit agent)
	{
		// stores the J values of all the possible actions for the agent currently being maximized
		List<Tuple<TargetedAction, Double>> maxJTable = max.calcJTable(s, h, playernum);
		
		// get the J values for all the possible actions for the agent
		List<Tuple<TargetedAction, Double>> agentJTable = agent.calcJTable(s, h, playernum);
		
		List<Tuple<TargetedAction, Double>> intermediateTable = constructJointTable(maxJTable, agentJTable);
		
		JTable = compressJointActionTable(maxJTable.size(), 1, intermediateTable);
	}
	
	/* create intermediate action pair tables for each the agent
	 * 
	 * An example of an intermediate action pair table is
	 * 
	 * +--+--+---+
     * |a1|a2|J12|
     * +==+==+===+
     * |A0|A0|4  |
     * +--+--+---+
     * |A0|A1|2  |
     * +--+--+---+
     * |A1|A0|6  |
     * +--+--+---+
     * |A1|A1|8  |
     * +--+--+---+
     * 
     * Where the first two columns select the action for each agent
     * and the third column is the summation of each agents J for the given actions.
     * 
     * It is assumed that a1 will be the agent represented by max
     * and a2 will be whichever agent in agents is currently being
     * processed
     */		
	private List<Tuple<TargetedAction, Double>> constructJointTable(List<Tuple<TargetedAction, Double>> maxTable,
				List<Tuple<TargetedAction, Double>> otherTable)
	{
		int numActions1 = maxTable.size(); // number of actions in the max table
		int numActions2 = otherTable.size(); // number of actions in the other table
		Double[] jVals = new Double[numActions1*numActions2]; // stores the sum of the J values from the max table and other table
		TargetedAction[] actions = new TargetedAction[numActions1*numActions2]; // stores the actions from the max table
		
		// construct the permutations and their associated
		for(int i=0; i<numActions1; i++)
		{
			Tuple<TargetedAction, Double> maxElement = maxTable.get(i); // convenience variable which saves some typing
			for(int j=0; j<numActions2; j++)
			{
				// get the index into the intermediate table
				int index = i*numActions1 + j;
				jVals[index] = maxElement.second + otherTable.get(j).second;
				actions[index] = maxElement.first;
			}
		}
		
		List<Tuple<TargetedAction, Double>> jointTable = new ArrayList<Tuple<TargetedAction, Double>>(jVals.length);
		
		// convert the two arrays into a list of tuples
		for(int i=0; i<jVals.length; i++)
		{
			jointTable.add(new Tuple<TargetedAction, Double>(actions[i], jVals[i]));
		}
		
		return jointTable;
	}
	
	/*
	 * Go through a table and find the maximum over the max agent
	 */
	private List<Tuple<TargetedAction, Double>> compressJointActionTable(int numActions, int dim, List<Tuple<TargetedAction, Double>> finalActionTable)
	{
		// TODO Generalize this method so it works with different table sizes
		Double[] JVals = new Double[numActions];
		TargetedAction[] actions = new TargetedAction[numActions];
		
		for(int i=0; i<(int)Math.pow(numActions, dim); i++)
		{
			// find max out of possible values of max agent's action
			JVals[i] = Double.NEGATIVE_INFINITY;
			for(int j=0; j<numActions; j++)
			{
				int index = i + j*(int)Math.pow(numActions, dim);
				if(JVals[i] < finalActionTable.get(index).second) // if found a new best number
				{
					// update everything
					JVals[i] = finalActionTable.get(index).second;
					actions[i] = finalActionTable.get(i).first;
				}
			}
		}
		
		// finally construct the final table
		List<Tuple<TargetedAction, Double>> finalJTable = new ArrayList<Tuple<TargetedAction, Double>>(JVals.length);
		for(int i=0; i<JVals.length; i++)
		{
			finalJTable.add(new Tuple<TargetedAction, Double>(actions[i], JVals[i]));
		}
		return finalJTable;
	}
	
	public List<Tuple<TargetedAction, Double>> getJTable()
	{
		return JTable;
	}
	
	
	
	public void update(Factor f)
	{
		// TODO implement this method
		// The method is supposed to take in a factor
		// and using that factor compress the internal table
	}
	
	public TargetedAction selectActions()
	{
		// TODO implement this method
		// this method is supposed to return the
		// maximum action over the actions left in the JTable
		return null;
	}
}
