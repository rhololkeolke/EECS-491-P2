package edu.cwru.sepia.agent;

import java.util.LinkedList;
import java.util.List;

import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

import edu.cwru.sepia.agent.mock.LearningUnit;

public class Factor2 {

	JMap jmap;
	
	public Factor2(StateView s, HistoryView h, int playerNum, LearningUnit...agents)
	{
		
		jmap = new JMap();
		List<JMap> agentJMaps = new LinkedList<JMap>();
				
		// get all of the JMaps
		for(LearningUnit agent : agents)
		{
			agentJMaps.add(agent.calcJMap(s, h, playerNum));
		}
		
		// generate all of the combinations
		List<ActionCombination> combinations = null;
		for(JMap agentJMap : agentJMaps)
		{
			combinations = getCombinations(agentJMap, combinations);
		}
		
		// for each combination
		for(ActionCombination combination : combinations)
		{
			// query the JMap for the values inside of combination
			Double totVal = 0.0;
			for(JMap agentJMap : agentJMaps)
			{
				try {
					totVal += agentJMap.get(combination);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			jmap.put(combination, totVal);
		}
	}
	
	
	private List<ActionCombination> getCombinations(JMap agentJMap, List<ActionCombination> workingList)
	{
		List<ActionCombination> newWorkingList = new LinkedList<ActionCombination>();
		List<ActionCombination> agentAC = agentJMap.getActionCombinationList();
		
		if(workingList == null)
			return agentAC;
		
		for(ActionCombination ac : agentAC)
		{
			for(ActionCombination wac : workingList)
				newWorkingList.add(new ActionCombination(ac, wac));
		}
		return newWorkingList;
	}
}