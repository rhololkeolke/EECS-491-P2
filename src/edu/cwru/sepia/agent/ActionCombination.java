package edu.cwru.sepia.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cwru.sepia.action.TargetedAction;

public class ActionCombination {

	Map<Integer, TargetedAction> combination;
	
	public ActionCombination(Tuple<Integer, TargetedAction>...mappings)
	{
		combination = new HashMap<Integer, TargetedAction>();
		
		for(Tuple<Integer, TargetedAction> mapping : mappings)
		{
			combination.put(mapping.first, mapping.second);
		}
	}
	
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
	
	public void put(Integer unitId, TargetedAction t)
	{
		combination.put(unitId, t);
	}
	
	public Set<Integer> keySet()
	{
		return combination.keySet();
	}
	
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

}
