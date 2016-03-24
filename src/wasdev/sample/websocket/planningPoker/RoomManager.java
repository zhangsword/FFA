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
import java.util.List;

public class RoomManager {
	private static List<Room> rooms = Collections.synchronizedList(new ArrayList<Room>());

	/**
	 * Finds and returns the room with the given name. If no room exists with that name creates and returns room.
	 * @param name of room requested
	 * @return Room with given name (either an existing room or a new room if it did not exist before request).
	 */
	public static Room getRoomByName(String name){
		synchronized (rooms) {
			for(Room room: rooms){
				if(room.getName().equalsIgnoreCase(name)){
					return room;
				}
			}
			//if we get here there was no pre-existing room with given name. Create room and return that.
			Room newRoom = createRoom(name);
			rooms.add(newRoom);
			return newRoom;
		}
	}

	/**
	 * Creates a Room with the provided name.
	 * @param name of room to be created.
	 * @return Room with requested name.
	 */
	public static Room createRoom(String name){
		return new Room(name);
	}

	/**
	 * Removes the room with the given name.
	 * @param name of room to be deleted.
	 */
	public static void deleteRoom(String name){
		synchronized (rooms) {
			for(Room room: rooms){
				if(room.getName().equalsIgnoreCase(name)){
					rooms.remove(room);
					return;
				}
			}
		}
	}	
}
