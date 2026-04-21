package com.smartcampus.service;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DataStore {
    //Rooms marked by roomID
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
     // Sensors marked by sensorID
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
     //SensorReadings marked by sensorID
    private static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();


    // Room functions
    public static Map<String, Room> getRooms() {
        return rooms;
    }
    public static Room getRoom(String id) {
        return rooms.get(id);
    }
    public static void putRoom(Room room) {
        rooms.put(room.getId(), room);
    }
    public static boolean deleteRoom(String id) {
        return rooms.remove(id) != null;
    }

    // Sensor Functions
    public static Map<String, Sensor> getSensors() {
        return sensors;
    }
    public static Sensor getSensor(String id) {
        return sensors.get(id);
    }
    public static void putSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }
    public static boolean deleteSensor(String id) {
        return sensors.remove(id) != null;
    }

    // Sensor Reading Functions
    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public static void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }


}
