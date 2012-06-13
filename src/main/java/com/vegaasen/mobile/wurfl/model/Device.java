package com.vegaasen.mobile.wurfl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vegaasen
 */
@XmlRootElement(name = "useragent")
public class Device {
    private String id = "";
    private String userAgent = "";
    private String browserCodeName = "";
    private String browserName = "";
    private String platform = "";
    private String vendor = "";
    private String vendorSubcategory = "";
    private String version = "";

    @XmlAttribute(name = "description", required = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "useragent", required = false)
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @XmlAttribute(name = "appcodename", required = false)
    public String getBrowserCodeName() {
        return browserCodeName;
    }

    public void setBrowserCodeName(String browserCodeName) {
        this.browserCodeName = browserCodeName;
    }

    @XmlAttribute(name = "appname", required = false)
    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    @XmlAttribute(name = "platform", required = false)
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @XmlAttribute(name = "vendor", required = false)
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @XmlAttribute(name = "vendorsub", required = false)
    public String getVendorSubcategory() {
        return vendorSubcategory;
    }

    public void setVendorSubcategory(String vendorSubcategory) {
        this.vendorSubcategory = vendorSubcategory;
    }

    @XmlAttribute(name = "appversion", required = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
