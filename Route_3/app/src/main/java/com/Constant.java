package com;

import static com.wozart.route_3.data.DeviceContract.DeviceEntry.DEVICE_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.HOME_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_1;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_2;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_3;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_4;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.ROOM_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.TABLE_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.THING_NAME;

/**
 * Created for Wozart on 12/10/17.
 * Author - Aarth Tandel
 *
 * Constants for easy access
 *
 * //////////////////////////////
 * Version - 1.0.0 - Initial built
 * //////////////////////////////
 */

public class Constant {

    //User's and Device Constants
    public static String IDENTITY_ID;
    public static final String CLOSED_CONNECTION = "client_closed_connection";
    public static final String SERVER_NOT_REACHABLE = "Server Not Reachable";
    public static final String UNPAIRED = "00000000000000000";
    public static final String PAIRED = "00000000000000011";
    public static final String WRONG_PIN = "xx:xx:xx:xx:xx:xx";
    public static final String NETWORK_SSID = "Aura";
    public static final String URL = "http://192.168.10.1/";
    public static final int MAX_HOME = 5;

    //SQL - Lite Queries
    public static final String GET_ALL_DEVICES = "select * from " + TABLE_NAME;
    public static final String GET_ALL_HOME = "select " + HOME_NAME + " from " + TABLE_NAME;
    public static final String INSERT_INITIAL_DATA = "select * from " + TABLE_NAME;
    public static final String GET_ROOMS = "select distinct " + ROOM_NAME + " from " + TABLE_NAME + " where " + HOME_NAME + " = ?";
    public static final String INSERT_ROOMS = "select " + HOME_NAME + " from " + TABLE_NAME + " where " + ROOM_NAME + " = ?";
    public static final String CRUD_ROOM = HOME_NAME + " =? and " + ROOM_NAME + " =? ";
    public static final String GET_DEVICES_IN_ROOM = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + HOME_NAME + " = ? and " + ROOM_NAME + " =?";
    public static final String GET_LOADS = "select " + LOAD_1 + ", " + LOAD_2 + ", " + LOAD_3 + ", " + LOAD_4 + " from " + TABLE_NAME + " where " + DEVICE_NAME + " = ?";
    public static final String INSERT_DEVICES = "select " + DEVICE_NAME + " from " + TABLE_NAME;
    public static final String CHECK_DEVICES = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + DEVICE_NAME + " = ?";
    public static final String GET_THING_NAME = "select " + THING_NAME + " from " + TABLE_NAME;
    public static final String GET_DEVICES_FOR_THING = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + THING_NAME + " = ?";
}
