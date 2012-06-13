package com.vegaasen.mobile.wurfl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @vegaasen
 */
@XmlRootElement(name = "folder")
public class Folder {

    private String description = "";

    private List<Device> devices = new ArrayList<Device>();

    @XmlAttribute(name = "description", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "useragent", required = true)
    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device device) {
        if(device!=null) {
            this.devices.add(device);
        }
    }

    public void removeDevice(Device device) {
        if(device!=null) {
            this.devices.remove(device);
        }
    }
}
