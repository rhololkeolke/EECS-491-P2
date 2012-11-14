package edu.cwru.sepia.agent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.DeathLog;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class CoordinationAgent extends Agent {
	
	Map<Integer, LearningUnit> units; // used for quick lookup
	
	int turnCount = 1;
	
	int episodeCount = 1;
	
	int numEpisodes;
	
	Map<Integer, Action> actions;
	
	double[] cumRewards = new double[5];
	
	int frozenGameCount = 5; // when less than the number of slots in cumRewards units won't be updated
	
	FileWriter fstream;
	BufferedWriter out;
	
	// the agents
	LearningUnit f1, f2, f3, a1, a2, b1, b2;
	
	public CoordinationAgent(int playernum, String[] args) throws IOException {
		super(playernum);
		units = new HashMap<Integer, LearningUnit>();
		
		fstream = new FileWriter("learningData.csv");
		out = new BufferedWriter(fstream);
		
		if(args.length < 5)
		{
			System.err.println("Must supply number of episodes and file path to coordination graph definition file");
			System.exit(1);
		}
		else
		{
			numEpisodes = Integer.parseInt(args[3]);
		}
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
			for(Integer unitId : newstate.getUnitIds(playernum))
			{
				switch(unitId)
				{
				case 6:
					f1 = new LearningUnit(unitId);
					units.put(unitId, f1);
					break;
				case 7:
					f2 = new LearningUnit(unitId);
					units.put(unitId, f2);
					break;
				case 8:
					f3 = new LearningUnit(unitId);
					units.put(unitId, f3);
					break;
				case 9:
					a1 = new LearningUnit(unitId);
					units.put(unitId, a1);
					break;
				case 10:
					a2 = new LearningUnit(unitId);
					units.put(unitId, a2);
					break;
				case 11:
					b1 = new LearningUnit(unitId);
					units.put(unitId, b1);
					break;
				case 12:
					b2 = new LearningUnit(unitId);
					units.put(unitId, b2);
					break;
				}
				LearningUnit myunit = new LearningUnit(unitId);
				units.put(unitId, myunit);
			}
		}
		
		// execute the policy

		try {
			return getAction(newstate, statehistory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			try {
				return getAction(newstate, statehistory);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			// if this isn't an event return an empty action list so the previous actions
			// continue to execute
			return new HashMap<Integer, Action>(); 
		}
		return null;
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
				System.exit(0);
			}
		}
		
	}

	@Override
	public void savePlayerData(OutputStream os) {
	}

	@Override
	public void loadPlayerData(InputStream is) {
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
	
	private Map<Integer, Action> getAction(StateView state, HistoryView history) throws Exception
	{	
		Map<Integer, Action> actionMap = new HashMap<Integer, Action>();
		
		List<Integer> aliveIds = state.getUnitIds(playernum);
		
		// Footmen coordination graph
		Factor footmen = new Factor(state, history, playernum, f1, f2, f3);
		
		// extract the actions
		ActionCombination footmenActions = footmen.selectAction();
		for(Integer unitId : footmenActions.keySet())
		{
			if(aliveIds.contains(unitId))
			{
				actionMap.put(unitId, (Action)footmenActions.get(unitId));
			}
		}
		
		// Archer coordination graph
		Factor archers = new Factor(state, history, playernum, a1, a2);
		
		// extract the actions
		ActionCombination archerActions = archers.selectAction();
		for(Integer unitId : archerActions.keySet())
		{
			if(aliveIds.contains(unitId))
			{
				actionMap.put(unitId, (Action)archerActions.get(unitId));
			}
		}
		
		// Ballista coordination graph
		Factor ballistae = new Factor(state, history, playernum, b1, b2);
		
		// extract the actions
		ActionCombination ballistaeActions = ballistae.selectAction();
		for(Integer unitId : ballistaeActions.keySet())
		{
			if(aliveIds.contains(unitId))
			{
				actionMap.put(unitId, (Action)ballistaeActions.get(unitId));
			}
		}
		
		return actionMap;
	}
}
