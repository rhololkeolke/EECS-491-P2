package edu.cwru.sepia.agent.mock;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.sepia.agent.ActionCombination;
import edu.cwru.sepia.agent.Factor2;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class FactorTests {

	public static void main(String[] args) {
		LearningUnit u0 = new LearningUnit(0);
		LearningUnit u1 = new LearningUnit(1);
		LearningUnit u2 = new LearningUnit(2);
		
		
		try {
			Factor2 f1 = new Factor2((StateView)null, (HistoryView)null, 0, u0, u1);
			
			f1 = f1.max(u0.unitId);
			
			List<LearningUnit> agents = new ArrayList<LearningUnit>(2);
			agents.add(u1);
			agents.add(u2);
			List<Factor2> factors = new ArrayList<Factor2>(1);
			factors.add(f1);
			
			Factor2 f2 = new Factor2((StateView)null, (HistoryView)null, 0, agents, factors);
			f2 = f2.max(u1.unitId);
			
			agents.clear();
			
			factors.clear();
			factors.add(f2);
			
			Factor2 f3 = new Factor2((StateView)null, (HistoryView)null, 0, agents, factors);
			f3 = f3.max(u2.unitId);
			
			ActionCombination bestActions = f3.getMaxes();
			System.out.println("bestActions" + bestActions.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
	}

}
