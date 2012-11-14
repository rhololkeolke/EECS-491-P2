package edu.cwru.sepia.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.DeathLog;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class PolicyAgent extends Agent {
	
	Map<Integer, LearningUnit> units; // used for quick lookup
	
	int turnCount = 1;
	
	int episodeCount = 1;
	
	int numEpisodes = 50;
	boolean loadAgents = false;
	
	Map<Integer, Action> actions;
	
	double[] cumRewards = new double[5];
	
	int frozenGameCount = 5; // when less than the number of slots in cumRewards units won't be updated
	
	FileWriter fstream;
	BufferedWriter out;
	
	public PolicyAgent(int playernum, String[] args) throws IOException {
		super(playernum);
		units = new HashMap<Integer, LearningUnit>();
		
		fstream = new FileWriter("learningData.csv");
		out = new BufferedWriter(fstream);
		
		if(args.length < 4)
			numEpisodes = 50;
		else if(args.length == 4)
			numEpisodes = Integer.parseInt(args[3]);

	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate,
			HistoryView statehistory) {
		System.out.println("Running for " + numEpisodes);
		if(frozenGameCount < cumRewards.length)
			System.out.println("Executing frozen game " + (frozenGameCount + 1) +
					           " for episode " + episodeCount);
		else
			System.out.println("Running episode " + episodeCount);
		
		
		actions = new HashMap<Integer, Action>();
		
		// create the learning units 
		if(episodeCount == 1) // only do this for the first episode
		{
			FileInputStream fileIn;
			ObjectInputStream agentIn;
			LearningUnit myunit;
			for(Integer unitId : newstate.getUnitIds(playernum))
			{
				if(new File("agents/" + unitId + ".ser").isFile())
				{
					try {
						fileIn = new FileInputStream("agents/" + unitId + ".ser");
						agentIn = new ObjectInputStream(fileIn);
						myunit = (LearningUnit) agentIn.readObject();
						agentIn.close();
						fileIn.close();
					} catch (FileNotFoundException e) {
						myunit = new LearningUnit(unitId);
					} catch (IOException e) {
						myunit = new LearningUnit(unitId);
					} catch (ClassNotFoundException e) {
						myunit = new LearningUnit(unitId);
					}
				}
				else
				{
					myunit = new LearningUnit(unitId);
				}
				units.put(unitId, myunit);
			}
		}
		
		// execute the policy
		for(Integer unitId : units.keySet())
		{
			actions.put(unitId, units.get(unitId).getAction(newstate, statehistory, playernum));
		}
		// check if this is a learning episode or not
		return actions;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate,
			HistoryView statehistory) {
		turnCount++;
		// update the weights
		for(Integer unitId : units.keySet())
		{
			units.get(unitId).updateReward(newstate, statehistory);
			if(frozenGameCount >= cumRewards.length) // only update when not frozen
				units.get(unitId).updateWeights();
		}
		
		// if this is an event step
		// then select new actions
		if(isEvent(newstate, statehistory))
		{
			
			actions = new HashMap<Integer, Action>();
			
			for(Integer unitId : newstate.getUnitIds(playernum)) // for all of the units still alive
			{
				LearningUnit currUnit = units.get(unitId); // get the associated learning agent
				if(currUnit != null) 
				{
					actions.put(currUnit.unitId, currUnit.getAction(newstate, statehistory, playernum)); // get an action for that unit
			
				}
			}
			return actions;
		}
		else
		{
			// if this isn't an event return an empty action list so the previous actions
			// continue to execute
			return new HashMap<Integer, Action>(); 
		}
		
	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {
		
		// update the agents otherwise they will miss the reward from killing the last unit
		for(Integer unitId: units.keySet())
		{
			units.get(unitId).updateReward(newstate, statehistory);
			if(frozenGameCount >= 5)
				units.get(unitId).updateWeights();
		}
		
		if(frozenGameCount < 5) // this is a frozen game
		{
			for(Integer unitId : units.keySet()) // so gather the rewards
			{
				cumRewards[frozenGameCount] += units.get(unitId).getReward();
				units.get(unitId).resetReward(); // reset the reward for the next episode
			}
			frozenGameCount++;
		}
		else
		{
			if(episodeCount % 10 == 0) // if this is the end of a frozen game run
			{
				double total = 0;
				for(int i=0; i<cumRewards.length; i++)
				{
					total += cumRewards[i];
					cumRewards[i] = 0;
				}
				try {
					out.write(episodeCount + "," + total/cumRewards.length + "\n"); // save the data to a file
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			episodeCount++; // keep track of what episode the agent is on
			
			if(episodeCount % 10 == 0) // if the game should be frozen 
			{
				frozenGameCount = 0;
			}
			
			if(episodeCount > numEpisodes)
			{
				
				// close the data file
				try {
					out.close();
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println(episodeCount-1 + " episodes run");
				
				FileOutputStream fileOut;
				ObjectOutputStream agentOut;
				
				for(Integer unitId : units.keySet())
				{
					try {
						fileOut = new FileOutputStream("agents/" + unitId + ".ser");
						agentOut = new ObjectOutputStream(fileOut);
						agentOut.writeObject(units.get(unitId));
						agentOut.close();
						fileOut.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
		
	}

	@Override
	public void savePlayerData(OutputStream os) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadPlayerData(InputStream is) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private boolean isEvent(StateView state, HistoryView history)
	{
		int eventTimeout = 3; // max number of turns before new "event"
		
		// if a death occured then select new actions
		List<DeathLog> death = history.getDeathLogs(state.getTurnNumber()-1);
		if(death.size() > 0)
			return true;
		
		// if damage occurred
		List<DamageLog> damage = history.getDamageLogs(state.getTurnNumber()-1);
		if(damage.size() > 0)
			return true;
		
		if(turnCount % eventTimeout == 0)
			return true;
		
		return false;
	}
}
