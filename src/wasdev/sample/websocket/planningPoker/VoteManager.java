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

public class VoteManager {
	//list of all votes.
	private static volatile ArrayList<Vote> votes = new ArrayList<Vote>();

	/**
	 * Returns vote with the given ID. If no existing vote exists with the ID creates one and returns that.
	 * 
	 * @param id of vote requested.
	 * @return Vote with corresponding ID.
	 */
	public static Vote getVoteByID(String id){
		for(Vote vote: votes){
			if(vote.getID().equalsIgnoreCase(id)){
				return vote;
			}
		}
		//if we get here there was no vote. Create vote and return that.
		Vote newVote = createVote(id);
		votes.add(newVote);
		return newVote;
	}
	
	/**
	 * Creates vote with given ID.
	 * 
	 * @param id of vote to be created.
	 * @return Vote with given ID.
	 */
	public static Vote createVote(String id){
		return new Vote(id);
	}
	
	/**
	 * Deletes the vote with the given ID.
	 * @param id of vote to be deleted.
	 */
	public static void deleteVote(String id){
		for(Vote vote: votes){
			if(vote.getID().equalsIgnoreCase(id)){
				votes.remove(vote);
			}
		}
	}
}
