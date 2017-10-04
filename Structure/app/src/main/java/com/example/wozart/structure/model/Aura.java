package com.example.wozart.structure.model;

/**
 * Created by wozart on 28/09/17.
 */

public class Aura {
    private int type = 1;
    private String name = "4module";
    private int state[] = new int[]{0, 0, 0, 0};
    private int dimm[] = new int[]{100, 100, 100, 100};
    private double version = 0.0;
    private int nodes = 4;
    private String thing = null;
    private String ip;
    private String code = "Not Authorized Device";
    private int aws = 0;
    private int error = 0;
    private int led = 0;

    public Aura() {

    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int[] getStates() {
        return state;
    }

    public int[] getDims() {
        return dimm;
    }

    public double getVersion() {
        return version;
    }

    public int getNodes() {
        return nodes;
    }

    public String getIP() {
        return ip;
    }

    public String getCode() {
        return code;
    }

    public int getAWSConfiguration() {
        return aws;
    }

    public int getError() {
        return error;
    }

    public String getThing() {
        return thing;
    }

    public int getLed() {
        return led;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDummyStates(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                if (this.state[i] == 0)
                    this.state[i] = 1;
                else
                    this.state[i] = 0;
            }
        }
    }

    public void setDummyDims(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                this.dimm[i] = 100;
            }
        }
    }

    public void setStates(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                if (this.state[i] == 0)
                    this.state[i] = 1;
                else
                    this.state[i] = 0;
            }
        }
    }

    public void setDims(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                if (this.dimm[i] == 0)
                    this.dimm[i] = 1;
                else
                    this.dimm[i] = 0;
            }
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void updateStates(int[] state) {
        this.state = state;
    }

    public void updateDims(int[] dimm) {
        this.dimm = dimm;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAWSConfiguration(int aws) {
        this.aws = aws;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public void setLed(int led) {
        this.led = led;
    }
}
