package com.wozart.route_3.utilities;

import com.wozart.route_3.model.AuraSwitch;

import java.util.ArrayList;

/**
 * Created by wozart on 01/11/17.
 */

public class DeviceUtils {

    private static ArrayList<AuraSwitch> AuraFourNodeDevice = new ArrayList<>();
    public static final String TAG = "DeviceUtils";

    public void RegisterDevice(AuraSwitch deviceName, String ip) {
        Boolean flag = true;
        for(AuraSwitch x : AuraFourNodeDevice){
            if(x.getName().equals(deviceName.getName()))
                flag = false;
        }
        if(flag) {
            AuraSwitch singleDevice = new AuraSwitch();
            singleDevice.setName(deviceName.getName());
            singleDevice.setIP(ip);
            singleDevice.setType(deviceName.getType());
            singleDevice.setCode(deviceName.getCode());
            singleDevice.setOnline(1);
            singleDevice.setStates(deviceName.getStates());
            AuraFourNodeDevice.add(singleDevice);
        }
    }

    public int GetType(String deviceName) {
        int type = 0;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                type = c.getType();
            }
        }
        return type;
    }

    public void UpdatePairingData(String deviceName, String code){
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (c.getName().contains(deviceName)) {
                c.setCode(code);
            }
        }
    }

    public AuraSwitch UpdateSwitchState(String deviceName, int switchNumber) {
        AuraSwitch singleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                singleDevice.updateStates(c.getStates());
                singleDevice.updateDims(c.getDims());
                singleDevice.setDummyStates(switchNumber);
                singleDevice.setDummyDims(switchNumber);
                singleDevice.setName(c.getName());
                return singleDevice;
            }
        }
        return null;
    }

    public AuraSwitch GetInfo(String deviceName) {
        AuraSwitch SingleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                SingleDevice = c;
                return SingleDevice;
            }
        }
        return SingleDevice;
    }

    public void UpdateDevice(AuraSwitch device) {
        String name = device.getName();

        for (AuraSwitch c : AuraFourNodeDevice) {
            String deviceName = c.getName();
            if (deviceName.contains(name)) {
                c.updateStates(device.getStates());
                c.updateDims(device.getDims());
            }
        }
    }

    public AuraSwitch GetStatesDims(String deviceName) {
        AuraSwitch SingleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {

                String name = deviceName.substring(deviceName.lastIndexOf('-') + 1);
                SingleDevice.setName(name);
                SingleDevice.updateStates(c.getStates());
                SingleDevice.updateDims(c.getDims());
            }
        }
        return SingleDevice;
    }

    public String GetCode(String deviceName){
        String code = null;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                code = c.getCode();
            }
        }
        return code;
    }

    public String GetIP(String deviceName) {
        String ip = null;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                ip = c.getIP();
            }
        }
        return ip;
    }

    public ArrayList<AuraSwitch> GetDevices(){
        return AuraFourNodeDevice;
    }

}
