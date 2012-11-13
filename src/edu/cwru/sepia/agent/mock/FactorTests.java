package edu.cwru.sepia.agent.mock;

import edu.cwru.sepia.agent.Factor;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class FactorTests {

	public static void main(String[] args) {
		LearningUnit u0 = new LearningUnit(0);
		LearningUnit u1 = new LearningUnit(1);
		
		Factor f1 = new Factor((StateView)null, (HistoryView)null, 0, u0, u1);
	}

}
