//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.15 at 01:08:12 PM PDT 
//


package com.gmo.generated.configuration.clientprocessorconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serverAdress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="connectionPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="uiMode" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serverAdress",
    "connectionPort",
    "uiMode"
})
@XmlRootElement(name = "ClientProcessorConfig")
public class ClientProcessorConfig {

    @XmlElement(required = true)
    protected String serverAdress;
    protected int connectionPort;
    protected boolean uiMode;

    /**
     * Gets the value of the serverAdress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerAdress() {
        return serverAdress;
    }

    /**
     * Sets the value of the serverAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerAdress(String value) {
        this.serverAdress = value;
    }

    /**
     * Gets the value of the connectionPort property.
     * 
     */
    public int getConnectionPort() {
        return connectionPort;
    }

    /**
     * Sets the value of the connectionPort property.
     * 
     */
    public void setConnectionPort(int value) {
        this.connectionPort = value;
    }

    /**
     * Gets the value of the uiMode property.
     * 
     */
    public boolean isUiMode() {
        return uiMode;
    }

    /**
     * Sets the value of the uiMode property.
     * 
     */
    public void setUiMode(boolean value) {
        this.uiMode = value;
    }

}
