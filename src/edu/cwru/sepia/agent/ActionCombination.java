package edu.cwru.sepia.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cwru.sepia.action.TargetedAction;

public class ActionCombination {
	// this class maps agent IDs to an Attack Action

	// stores the mapping
	// e.g. a0->Attack(1)
	Map<Integer, TargetedAction> combination;
	
	// create an ActionCombination from a variable length list of 
	// Tuples containing an Integer (unitIds) and a TargetedAction (Attack actions)
	public ActionCombination(Tuple<Integer, TargetedAction>...mappings)
	{
		combination = new HashMap<Integer, TargetedAction>();
		
		for(Tuple<Integer, TargetedAction> mapping : mappings)
		{
			combination.put(mapping.first, mapping.second);
		}
	}
	
	// Combine an existing ActionCombination with a variable length list of tuples
	// e.g. add on the tuples to an existing ActionCombination
	public ActionCombination(ActionCombination ac, Tuple<Integer, TargetedAction>...mappings)
	{
		combination = new HashMap<Integer, TargetedAction>();
		
		for(Integer key : ac.combination.keySet())
		{
			combination.put(key, ac.combination.get(key));
		}
		
		for(Tuple<Integer, TargetedAction> mapping : mappings)
		{
			combination.put(mapping.first, mapping.second);
		}
	}
	
	// Create an ActionCombination from two existing ActionCombinations
	// i.e. merge two ActionCombination lists
	// Note: values from ac2 are preferred over values from ac1
	public ActionCombination(ActionCombination ac1, ActionCombination ac2)
	{
		combination = new HashMap<Integer, TargetedAction>();
		for(Integer key : ac1.combination.keySet())
		{
			combination.put(key, ac1.combination.get(key));
		}
		
		for(Integer key : ac2.combination.keySet())
		{
			combination.put(key, ac2.combination.get(key));
		}
	}
	
	// add a mapping to the ActionCombination
	public void put(Integer unitId, TargetedAction t)
	{
		combination.put(unitId, t);
	}
	
	// get a set of all agent IDs in the ActionCombination instance
	public Set<Integer> keySet()
	{
		return combination.keySet();
	}
	
	// used to allow ActionCombination instances as keys in a Map
	public int hashCode()
	{
		StringBuilder repr = new StringBuilder();
		for(Integer key : combination.keySet())
		{
			repr.append(key.toString());
			
			TargetedAction t = combination.get(key);
			repr.append(Integer.toString(t.getTargetId()));
		}
		return repr.toString().hashCode();
	}
	
	// Given an ActionCombination containing agent IDs
	// remove all agentID mappings from the current ActionCombination
	// that are not in this list
	public ActionCombination prune(ActionCombination keep)
	{
		ActionCombination pruned = new ActionCombination();
		
		for(Integer key : combination.keySet())
		{
			if(keep.combination.containsKey(key))
				pruned.put(key, combination.get(key));
		}
		return pruned;
	}
	
	// used to allow ActionCombination instances as keys in a Map
	public boolean equals(Object o)
	{
		if(!(o instanceof ActionCombination))
			return false;
		
		ActionCombination other = (ActionCombination)o;
		
		if(other.size() != this.size())
			return false;
		
		for(Integer key : combination.keySet())
		{
			TargetedAction t1 = other.combination.get(key);
			if(t1 == null)
				return false;
			if(!actionEqual(t1, this.combination.get(key)))
				return false;
		}
		
		return true;
		
	}
	

	public List<ActionCombination> extract(List<Integer> keys)
	{
		ActionCombination act1 = new ActionCombination();
		ActionCombination act2 = new ActionCombination();
		
		for (Integer i:combination.keySet())
		{
			if (keys.contains(i))
			{
				act1.put(i, combination.get(i));
			}
			else
			{
				act2.put(i, combination.get(i));
			}
		}
		List<ActionCombination> list = new ArrayList<ActionCombination>(2);
		list.add(act1);
		list.add(act2);
		
		return list;
	}
	
	private boolean actionEqual(TargetedAction t1, TargetedAction t2)
	{
		if(t1.getTargetId() != t2.getTargetId())
			return false;
		return true;
	}
	
	public int size()
	{
		return combination.size();
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(" ");
		for(Integer key : combination.keySet())
		{
			builder.append(key.toString() + "=");
			
			TargetedAction t = combination.get(key);
			builder.append(Integer.toString(t.getTargetId()) + ";");
		}
		builder.append(" ");
		return builder.toString();
	}
	
	public TargetedAction get(Integer key)
	{
		return combination.get(key);
	}

}
