package edu.cwru.sepia.agent.mock;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.sepia.agent.ActionCombination;
import edu.cwru.sepia.agent.Factor;
import edu.cwru.sepia.agent.LearningUnit;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class FactorTests {

	/*
	 * Tests a coordination graph between 3 agents.
	 * 
	 * The graph is a chain as follows
	 * 
	 * a0 - a1 - a2
	 */
	public static void main(String[] args) throws Exception {
		
		/*
		 * u0.calcJMap will return
		 * 
		 * | Target ID |       |
         * |-----------+-------|
		 * |        a0 | J(a0) |
		 * |-----------+-------|
		 * |         0 |     7 |
		 * |         1 |    10 |
		 */
		LearningUnit u0 = new LearningUnit(0);
		
		/*
		 * u1.calcJMap will return
		 * 
		 * | Target ID |       |
		 * |-----------+-------|
		 * | a1        | J(a1) |
		 * |-----------+-------|
		 * | 0         | 4     |
		 * | 1         | 2     |
		 */
		LearningUnit u1 = new LearningUnit(1);
		
		/*
		 * u2.calcJMap will return
		 * 
		 * | TargetID |       |
		 * |----------+-------|
		 * |       a2 | J(a2) |
		 * |----------+-------|
	 	 * |        0 |     9 |
	 	 * |        1 |     3 |
		 */
		LearningUnit u2 = new LearningUnit(2);
		
		
		Factor units = new Factor(null, null, 0, u0, u1, u2);
		
		ActionCombination ac1 = units.selectAction();
		
		ActionCombination ac2 = units.selectAction();
		
		System.out.println("");
	}

}
