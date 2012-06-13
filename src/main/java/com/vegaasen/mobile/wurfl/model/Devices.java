package com.vegaasen.mobile.wurfl.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author vegaasen
 */
@XmlRootElement(name = "useragentswitcher")
public class Devices {

    private List<Folder> folders;

    @XmlElement(name = "folder", defaultValue = "")
    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
