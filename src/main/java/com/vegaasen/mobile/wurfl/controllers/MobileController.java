package com.vegaasen.mobile.wurfl.controllers;

import com.vegaasen.mobile.wurfl.model.Device;
import com.vegaasen.mobile.wurfl.model.Devices;
import com.vegaasen.mobile.wurfl.model.Folder;
import net.sourceforge.wurfl.core.WURFLHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Simple tool to fetch the latest list of mobile devices for
 * FireFox plugins like:
 * - UserAgentSwitcher (xml)
 * - ModifyHeader (json)
 *
 * @author vegaasen
 */
@Controller()
@RequestMapping(value = "/")
@Transactional()
public class MobileController {

    private static final Logger LOGGER = Logger.getLogger(MobileController.class);
    private static final String
            GZIP_ACTIVATED = "true",
            MISSING = "missing";
    private static final String DO_NOT_MATCH = "DO_NOT_MATCH";
    private static final String
            F_TABLET = "Tablet",
            F_SMARTTV = "Smart TV",
            F_DEVICE_OS_IOS = "iPhone OS",
            F_DEVICE_OS_ANDROID = "Android",
            F_DEVICE_OS_WINPHONE = "Windows Phone OS",
            F_DEVICE_OS_NOKIA_BN = "Nokia";

    @SuppressWarnings({"unused", "all"})
    private Jaxb2Marshaller jaxMarshaller;

    /**
     * localhost:${port}/getAllDevices?gzip={false,true}
     *
     * @param request   _
     * @param response  _
     * @param gzip _
     * @return jsp-mapping
     */
    @RequestMapping(
            value = "getAllDevices",
            method = {RequestMethod.GET},
            headers = {"Accept=*/*"},
            produces = {"application/xml", "application/json"}
    )
    public
    @ResponseBody
    Devices getAllDevices(
            @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response,
            @RequestParam(value = "gzip", defaultValue = GZIP_ACTIVATED) String gzip) {
        LOGGER.debug("Request initiated");
        Devices devices = new Devices();
        if (StringUtils.isNotBlank(gzip) && gzip.equalsIgnoreCase(GZIP_ACTIVATED)) {
            Device currentDevice;
            Folder
                    tablet = new Folder(),
                    smartTv = new Folder(),
                    androidOs = new Folder(),
                    iOs = new Folder(),
                    winPhone = new Folder(),
                    nokiaOs = new Folder();
            tablet.setDescription(F_TABLET);
            smartTv.setDescription(F_SMARTTV);
            androidOs.setDescription(F_DEVICE_OS_ANDROID);
            iOs.setDescription(F_DEVICE_OS_IOS);
            winPhone.setDescription(F_DEVICE_OS_WINPHONE);
            nokiaOs.setDescription(F_DEVICE_OS_NOKIA_BN);

            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext(request));
            WURFLHolder holder = (WURFLHolder) wac.getBean("wurflHolder");
            //WURFLManager manager = holder.getWURFLManager();

            Set<net.sourceforge.wurfl.core.Device> wurflDevices = holder.getWURFLUtils().getAllDevices();
            int counter = 1;
            for (net.sourceforge.wurfl.core.Device device : wurflDevices) {
                if (!device.getUserAgent().contains(DO_NOT_MATCH)) {
                    currentDevice = new Device();
                    currentDevice.setUserAgent(device.getUserAgent());
                    currentDevice.setId(
                            String.format(
                                "%s %s OS: %s Browser: %s",
                                device.getCapability(System.getProperty("mobile.capabilities.brand_name")),
                                device.getCapability(System.getProperty("mobile.capabilities.model_name")),
                                device.getCapability(System.getProperty("mobile.capabilities.device_os_version")),
                                (StringUtils.isNotEmpty(device.getCapability(System.getProperty("mobile.capabilities.mobile_browser_version"))) ?
                                device.getCapability(System.getProperty("mobile.capabilities.mobile_browser_version"))
                                :
                                "N/A")
                            )
                    );
                    currentDevice.setBrowserCodeName("");
                    currentDevice.setBrowserName(
                            device.getCapability(System.getProperty("mobile.capabilities.mobile_browser"))
                    );
                    currentDevice.setPlatform(
                            device.getCapability(System.getProperty("mobile.capabilities.device_os"))
                    );
                    currentDevice.setVendor(
                            device.getCapability(System.getProperty("mobile.capabilities.brand_name"))
                    );
                    currentDevice.setVendorSubcategory("");
                    currentDevice.setVersion(
                            device.getCapability(System.getProperty("mobile.capabilities.mobile_browser_version"))
                    );
                    LOGGER.info(String.format("Device Number: [%s]",counter));
                    if(device.getCapability(System.getProperty("mobile.capabilities.is_tablet")).equals("true")) {
                        tablet.addDevice(currentDevice);
                    }else if(device.getCapability(System.getProperty("mobile.capabilities.device_os")).equals(F_DEVICE_OS_ANDROID)) {
                        androidOs.addDevice(currentDevice);
                    }else if(device.getCapability(System.getProperty("mobile.capabilities.device_os")).equals(F_DEVICE_OS_IOS)) {
                        iOs.addDevice(currentDevice);
                    }else if(device.getCapability(System.getProperty("mobile.capabilities.brand_name")).equals(F_DEVICE_OS_NOKIA_BN)) {
                        nokiaOs.addDevice(currentDevice);
                    }else if(device.getCapability(System.getProperty("mobile.capabilities.device_os")).equals(F_DEVICE_OS_WINPHONE)) {
                        winPhone.addDevice(currentDevice);
                    }else if(device.getCapability(System.getProperty("mobile.capabilities.is_smarttv")).equals("true")) {
                        smartTv.addDevice(currentDevice);
                    }
                    counter++;
                }
            }
            devices.setFolders(assembleFolderList(tablet, androidOs, iOs, nokiaOs, winPhone, smartTv));
            LOGGER.info(String.format(
                    "Added %s folder(s). These contained %s device(s).",
                    devices.getFolders().size(),
                    sumOfAllFolders(tablet, androidOs, iOs, nokiaOs, winPhone, smartTv)));
            if(gzip.equals("true")) {
                
            }
        }
        LOGGER.info("Returning the list");
        return devices;
    }

    private List<Folder> assembleFolderList(final Folder... folders) {
        return Arrays.asList(folders);
    }

    private int sumOfAllFolders(final Folder... folders) {
        int sum = 0;
        for(Folder f : folders) {
            if(f.getDevices()!=null && f.getDevices().size()>0){
                sum += f.getDevices().size();
            }
        }
        return sum;
    }
    
    /**
     * @param request   _
     * @param response  _
     * @param userAgent _
     * @return jsp-mapping
     */
    @RequestMapping(
            value = "getDevice",
            method = {RequestMethod.GET},
            headers = {"Accept=application/xml"},
            produces = "application/xml"
    )
    public
    @ResponseBody
    Devices getDevice(
            @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response,
            @RequestParam(value = "useragent", defaultValue = MISSING) String userAgent) {
        LOGGER.debug("Request initiated");
        Devices devices = new Devices();
        if (StringUtils.isNotBlank(userAgent) && userAgent.equalsIgnoreCase(MISSING)) {
            
        }
        LOGGER.info("Returning the list as XML");
        return devices;
    }

    private ServletContext getServletContext(HttpServletRequest request) {
        return request.getSession().getServletContext();
    }

    @Autowired
    public void setJaxMarshaller(Jaxb2Marshaller jaxMarshaller) {
        this.jaxMarshaller = jaxMarshaller;
    }
}
