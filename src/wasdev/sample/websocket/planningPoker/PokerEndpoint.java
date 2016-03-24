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

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/PokerEndpoint")
public class PokerEndpoint {
	private Session currentSession = null;
	private Room room;
	private String userName = "Anon";

	//message prefixes which denotes a special action being required.
	private final String urlCommand 			= "/URL";
	private final String setNameCommand 		= "/SETNAME";
	private final String clearHistoryCommand 	= "/CLEARHISTORY";
	private final String setRoomCommand 		= "/SETROOM";
	private final String voteValueCommand 		= "/VOTEVALUE";
	private final String newVoteCommand 		= "/NEWVOTE";
	private final String endVoteCommand 		= "/VOTESTATE";

	//boolean which when true hides which user cast which vote. Set to false to see who cast which votes.
	//currently no UI option to switch modes.
	private boolean anonymousVotes = true;

	@OnOpen
	public void onOpen(Session session, EndpointConfig ec) {
		// Store the WebSocket session for later use.
		currentSession = session;
	}

	@OnMessage
	public void receiveMessage(String message) {
		if(message.equals("")){
			return;
		} else if(message.startsWith(urlCommand)){
			if(message.length()<=5){
				//consume and do nothing as no actual URL was sent
				return;
			}
			message = message.replace(urlCommand, "");
			if(!message.startsWith("http")){
				//if no http or https add it (http by default)
				message = "http://"+message;
			}
			sendMessage(urlCommand+message);
			return;
		} else if(message.startsWith(setNameCommand)){
			changeName(message);
			return;
		}else if(message.equals(clearHistoryCommand)){
			sendMessage("Cleared server side chat & vote history.");
			room.clearMessages();
			room.clearVoteId();
			return;
		} else if(message.startsWith(setRoomCommand)){
			changeRoom(message);
			return;
		} else if(message.startsWith(voteValueCommand)){
			message = parseVote(message);
			if(message!=null){
				sendMessage(message);
			}
			return;
		} else if(message.startsWith(newVoteCommand)){
			sendMessage(getVoteStatus());
			sendMessage(newVoteCommand);
			sendMessage(userName+" has started a new vote.");
			room.clearVoteId();
			return;
		} else if(message.startsWith(endVoteCommand)){
			sendMessage(getVoteStatus());
			return;
		}
		message = ""+userName+": "+message;
		sendMessage(message);
	}

	/**
	 * Send a message to all clients in the same room as this client.
	 * @param message
	 */
	public void sendMessage(String message){
		if(room==null){
			return;
		}
		room.addMessage(message);
		for (Session session: currentSession.getOpenSessions()){
			try {
				if(session.isOpen() && session.getUserProperties().get("roomName").equals(currentSession.getUserProperties().get("roomName"))){
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * 	Send all stored room history (messages) to the client.
	 */
	public void loadRoomHistory(){
		//get message history for room
		ArrayList<String> messageHistory = room.getMessageHistory();

		//if there is a message history send it to the client
		if(messageHistory.size()>0){
			for(String line: messageHistory){
				try {
					currentSession.getBasicRemote().sendText(line);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		if(room!=null){
			sendMessage(userName+" has left the room.");
		}
		if(isRoomEmpty()){
			RoomManager.deleteRoom(room.getName());
		}
	}

	@OnError
	public void onError(Throwable t) {
		// no error processing will be done for this sample
		t.printStackTrace();
	}

	/**
	 * Changes the room that the user is in, sends chat history for current room.
	 */
	private void changeRoom(String message){
		String roomName = message.replace(setRoomCommand, "").trim();
		if(room!=null){
			String oldRoomName = room.getName();
			//if old room name and new room name same, do nothing
			if(oldRoomName.equals(roomName)){
				loadRoomHistory();//we do this as the JS has cleared message content...
				return;
			}
			sendMessage(userName+" has left the room.");
		}
		room = RoomManager.getRoomByName(roomName);
		currentSession.getUserProperties().put("roomName", room.getName());
		loadRoomHistory();
		sendMessage(userName+" has joined the room.");
	}

	/**
	 * Change the display name for the user.
	 * @param message
	 */
	private void changeName(String message){
		String oldUserName = userName.toString();
		userName = message.replace(setNameCommand, "").trim();
		//if old and new username same, do nothing
		if(oldUserName.equals(userName)){
			return;
		}
		sendMessage("User "+oldUserName+" changed name to "+userName);    	
	}

	/**
	 * Take a vote and create an appropriate message around what was changed vote wise.
	 * @param message
	 * @return message to be shared to the chat room.
	 */
	private String parseVote(String message){
		//grab this room's vote
		Vote currentVote = VoteManager.getVoteByID(room.getVoteId());
		//remove command prefix
		String decompiledString = message.replace(voteValueCommand, "").trim();

		//grab name
		int endOfName = decompiledString.indexOf("###");
		String voterName = decompiledString.substring(0, endOfName);
		if(anonymousVotes){
			voterName = "Someone";
		}
		decompiledString = decompiledString.substring(endOfName+3);

		//grab new vote
		int endOfNewVote = decompiledString.indexOf("##");
		int newVoteValue = Integer.parseInt(decompiledString.substring(0, endOfNewVote));
		decompiledString = decompiledString.substring(endOfNewVote+2);

		//grab old vote
		int oldVoteValue = Integer.parseInt(decompiledString);
		if(newVoteValue>=0){
			currentVote.changeVote(oldVoteValue, newVoteValue);
		} else {
			currentVote.removeVote(oldVoteValue);
		}
		//Instantiate to error message. If all goes well this gets replaced.
		String newMessage="Error parsing vote.";
		if(newVoteValue>=0){
			if(oldVoteValue<0){
				newMessage = voterName+" set their vote to "+newVoteValue+".";
			} else {
				newMessage = voterName+" changed their vote from "+oldVoteValue+" to "+newVoteValue+".";
			}
		} else {
			if(newVoteValue==-3){
				newMessage = voterName+" cancelled their vote of "+oldVoteValue+".";
				if(oldVoteValue<0){
					newMessage = null;
				}
			} else if(newVoteValue==-2){
				newMessage = voterName+" passed on this vote.";
			} else if(newVoteValue==-1){
				newMessage = voterName+" needs more information to be able to vote.";
			}
		}
		return newMessage;
	}

	/**
	 * Calculates the current state of the vote and returns a message describing its state.
	 * @return message describing various vote metrics (number of votes, averages etc).
	 */
	private String getVoteStatus(){
		Vote vote = VoteManager.getVoteByID(room.getVoteId());
		int modeVote = vote.getMode();
		String voteMessage = userName+" has requested the current vote status. \nThe mean across all votes is "+vote.getMean();
		if(modeVote!=-999){
			voteMessage = voteMessage+"\nThe mode vote is "+modeVote;
		} else {
			voteMessage = voteMessage+"\nThe mode is a draw.";
		}
		voteMessage+="\n"+vote.getMedian();
		TreeMap<Integer, Integer> voteMap = vote.getVotes();
		String voteResults = "\nVote breakdown: \n";
		for(Integer value:voteMap.keySet()){
			voteResults+=value+" points (x"+voteMap.get(value)+")\n";
		}
		return voteMessage+voteResults;
	}

	/**
	 * Looks for open sessions for the given room. 
	 * @return boolean for if room is empty - true = empty room, false = users still connected.
	 */
	private boolean isRoomEmpty() {
		for (Session session: currentSession.getOpenSessions()){
			if(session.isOpen() && session.getUserProperties().get("roomName").equals(currentSession.getUserProperties().get("roomName"))){
				return false;
			}
		}
		return true;		
	}
}
