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
import java.util.UUID;

public class Room {
	private String name = "";
	private ArrayList<String> messageHistory = new ArrayList<String>();
	private String voteId;
	
	public Room(String roomName){
		name = roomName;
	}
	
	/**
	 * Sets the name for the room.
	 * @param newName new name for the room.
	 */
	public void setName(String newName){
		name = newName;
	}
	
	/**
	 * Returns the name of the room.
	 * @return String name of the room.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Removes all message history from this room.
	 */
	public void clearMessages(){
		messageHistory.clear();
	}
	
	/**
	 * Adds a message to the room message history.
	 * @param message to add to history.
	 */
	public void addMessage(String message){
		messageHistory.add(message);
	}
	
	/**
	 * Gets all messages from this room's history.
	 * @return ArrayList<String> of all messages for this room.
	 */
	public ArrayList<String> getMessageHistory(){
		return messageHistory;
	}
	
	/**
	 * Finds the UUID for the vote currently associated with this room. If none exists it creates one and returns it.
	 * @return String UUID for the voteID in this room.
	 */
	public String getVoteId(){
		if(voteId==null){
			voteId = UUID.randomUUID().toString();
		}
		return voteId;
	}
	
	/**
	 * Removes the current vote UUID from this room. 
	 */
	public void clearVoteId(){
		voteId = null;
	}
}
