package edu.cwru.sepia.agent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JMap {
	// this class stores a mapping of ActionCombinations to the associated J values
	// also contains methods for querying the data in the map

	// Maps an action combination
	// e.g. a0=1, a1=0
	//
	// to a J value
	// e.g. (a0=1, a1=0)->5.0
	Map<ActionCombination, Double> jmap;
	
	// create an empty mapping
	public JMap()
	{
		jmap = new HashMap<ActionCombination, Double>();
	}
	
	// create a JMap from an existing mapping
	public JMap(Map<ActionCombination, Double> jmap)
	{
		this.jmap = jmap;
	}
	
	// for each agent in the ActionCombination
	// if the agent exists in this JMap then 
	// create a new ActionCombination with that agent
	// and the associated action from the original ActionCombination.
	//
	// If when queried with this new ActionCombination the JMap returns null
	// either incorrect actions were included in the ActionCombination passed in
	// or not enough agent-action mappings were specified to produce a unique
	// row in the JMap table
	public Double get(ActionCombination combination) throws Exception
	{
		ActionCombination[] keys = jmap.keySet().toArray(new ActionCombination[4]);
		
		ActionCombination queryKey = combination.prune(keys[0]);
		
		Double result;
		if((result = jmap.get(queryKey)) == null)
			throw new Exception("Did not specify enough agents to query this jmap");
		return result;
	}
	
	// given an ActionCombination create an entry in the mapping
	public void put(ActionCombination combination, Double value)
	{
		jmap.put(combination, value);
	}
	
	// return a set of all ActionCombinations in the mapping
	public Set<ActionCombination> keySet()
	{
		return jmap.keySet();
	}
	
	// create a list of all ActionCombinations in the mapping
	public List<ActionCombination> getActionCombinationList()
	{
		return new LinkedList<ActionCombination>(jmap.keySet());
	}

}
