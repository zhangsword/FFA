/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package wasdev.sample.websocket.planningPoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class Vote {
	//Map of <value, count> for votes
	private TreeMap<Integer, Integer> votes = new TreeMap<Integer, Integer>();
	//unique identifier for the vote
	private String voteID;
	
	public Vote(String name){
		voteID = name;
	}
	
	/**
	 * add a vote to the current count.
	 * @param int value of the vote cast
	 */
	public void addVote(int vote){
		if(votes.containsKey(vote)){
			votes.put(vote, votes.get(vote)+1);
		} else {
			votes.put(vote, 1);
		}
	}
	
	/**
	 * Change an existing vote to a new value
	 * @param int previous value
	 * @param int new value
	 */
	public void changeVote(int oldValue, int newValue){
		removeVote(oldValue);
		addVote(newValue);
	}
	
	/**
	 * Remove a vote value from the current vote
	 * @param int value to remove
	 */
	public void removeVote(int valueToRemove){
		if(votes.containsKey(valueToRemove)){
			votes.put(valueToRemove, votes.get(valueToRemove)-1);
			if(votes.get(valueToRemove)<=0){
				votes.remove(valueToRemove);
			}
		}
	}
	
	/**
	 * Return the treemap of all votes cast
	 * @return TreeMap of all votes of <value, count> where the value is the 
	 * vote and the count is how many people cast that value.
	 */
	public TreeMap<Integer, Integer> getVotes(){
		return votes;
	}
	
	/**
	 * Returns the ID of the vote
	 * @return String unique ID for the vote.
	 */
	public String getID(){
		return voteID;
	}
	
	/**
	 * Gets mode average vote (most common vote value). If draw or no votes returns -999.
	 * @return int of either the mode value in this vote or -999 if there is a draw or no votes. 
	 */
	public int getMode(){
		int mostFrequentKey = 0;
		int mostFrequentKeyCount = -999;
		boolean draw = false;
		for(int key:votes.keySet()){
			//if key <0 then it is a pass/don't know/cancelled vote so ignore.
			if(key>=0){
				if(votes.get(key)==mostFrequentKeyCount){
					draw = true;
				}
				if(votes.get(key)>mostFrequentKeyCount){
					mostFrequentKey = key;
					mostFrequentKeyCount = votes.get(key);
					draw = false;
				}
			}
		}
		if(draw){
			return -999;//ERROR CASE AS NO MODE POSSIBLE DUE TO A DRAW
		}
		return mostFrequentKey; 
	}
	
	/**
	 * Gets mean value of all votes cast.
	 * @return float mean vote
	 */
	public float getMean(){
		int numberOfVotes = 0;
		int totalAccumulatedSize = 0;
		for(int key:votes.keySet()){
			//if key <0 then it is a pass/don't know/cancelled vote so ignore.
			if(key>=0){
				numberOfVotes +=votes.get(key);
				totalAccumulatedSize+=(key*votes.get(key));
			}
		}
		if(numberOfVotes==0){
			return 0;
		}
		
		return totalAccumulatedSize/numberOfVotes;
	}
	
	/**
	 * Looks at the median value. Returns a string description of what the median value is, 
	 *	or what values it is split between if even number of votes.
	 *
	 * @return String sentence describing vote median
	 */
	public String getMedian(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		String returnMessage = "";
		for(int key:votes.keySet()){
			//if key <0 then it is a pass/don't know/cancelled vote so ignore.
			if(key>=0){
				for(int i=0; i<votes.get(key); i++){
					list.add(key);
				}
			}
			
		}
		Collections.sort(list);
		if(list.size()>=1 && (list.size()%2 == 0)){
			//we have even number of entries so compare if 2 entries are same/different
			int leftValue = list.get((list.size()/2)-1);
			int rightValue = list.get((list.size()/2));
			if(leftValue == rightValue){
				//if values match median value is their value.
				returnMessage = "The median value is "+leftValue;
			}
			//values did not match - no median possible so return both values and message to that effect.
			returnMessage = "There are an even number of votes, so the median value is between "+leftValue+" and "+rightValue;
		} else if(list.size()>=1) {
			//we have odd number of entries so give middle value
			returnMessage = "The median value is "+list.get(list.size()/2);
		} else {
			returnMessage = "There are no votes in the current vote. Averages cannot be calculated.";
		}
		return returnMessage;
	}
}
