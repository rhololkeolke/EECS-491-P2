package edu.cwru.sepia.agent.mock;

import edu.cwru.sepia.agent.Factor2;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class FactorTests {

	public static void main(String[] args) {
		LearningUnit u0 = new LearningUnit(0);
		LearningUnit u1 = new LearningUnit(1);
		
		Factor2 f1 = new Factor2((StateView)null, (HistoryView)null, 0, u0, u1);
		
		Factor2 f2;
		try {
			f2 = f1.max(u0.unitId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
	}

}
