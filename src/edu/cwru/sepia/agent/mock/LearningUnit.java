package edu.cwru.sepia.agent.mock;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Tuple;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class LearningUnit {

    int unitId;
    
    public LearningUnit(int unitId)
    {
    	this.unitId = unitId;
    }
    
	public List<Tuple<TargetedAction, Double>> calcJTable(StateView s, HistoryView h, int playernum)
	{
		List<Tuple<TargetedAction, Double>> jTable = new ArrayList<Tuple<TargetedAction, Double>>(2);
		
		TargetedAction t1 = (TargetedAction) TargetedAction.createCompoundAttack(unitId, 0);
		TargetedAction t2 = (TargetedAction) TargetedAction.createCompoundAttack(unitId, 1);
		
		if(unitId == 0)
		{
			jTable.add(new Tuple<TargetedAction, Double>(t1, 5.0));
			jTable.add(new Tuple<TargetedAction, Double>(t2, 3.0));
		}
		else
		{
			jTable.add(new Tuple<TargetedAction, Double>(t1, -2.0));
			jTable.add(new Tuple<TargetedAction, Double>(t2, 8.0));
		}
		
		return jTable;
	}

}
