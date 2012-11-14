package edu.cwru.sepia.agent.mock;

import java.io.Serializable;

import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.ActionCombination;
import edu.cwru.sepia.agent.JMap;
import edu.cwru.sepia.agent.Tuple;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class LearningUnit implements Serializable {

    int unitId;
    
    public LearningUnit(int unitId)
    {
    	this.unitId = unitId;
    }
    
	public JMap calcJMap(StateView s, HistoryView h, int playernum)
	{
		JMap jmap = new JMap();
		
		TargetedAction t0 = (TargetedAction) TargetedAction.createCompoundAttack(unitId, 0);
		TargetedAction t1 = (TargetedAction) TargetedAction.createCompoundAttack(unitId, 1);
		
		ActionCombination comb0 = new ActionCombination(new Tuple<Integer, TargetedAction>(unitId, t0));
		ActionCombination comb1 = new ActionCombination(new Tuple<Integer, TargetedAction>(unitId, t1));
		if(unitId == 0)
		{

			jmap.put(comb0, 7.0);
			jmap.put(comb1, 10.0);
		}
		else if(unitId == 1)
		{
			jmap.put(comb0, 4.0);
			jmap.put(comb1, 2.0);
		}
		else
		{
			jmap.put(comb0, 9.0);
			jmap.put(comb1, 3.0);
		}
		
		return jmap;
	}

}
