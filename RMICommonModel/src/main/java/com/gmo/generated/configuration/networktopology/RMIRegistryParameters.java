//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.15 at 01:17:33 PM PDT 
//


package com.gmo.generated.configuration.networktopology;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RMIRegistryParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RMIRegistryParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RmiRegistryAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RmiRegistryPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RMIRegistryParameters", propOrder = {
    "rmiRegistryAddress",
    "rmiRegistryPort"
})
public class RMIRegistryParameters {

    @XmlElement(name = "RmiRegistryAddress", required = true)
    protected String rmiRegistryAddress;
    @XmlElement(name = "RmiRegistryPort")
    protected int rmiRegistryPort;

    /**
     * Gets the value of the rmiRegistryAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRmiRegistryAddress() {
        return rmiRegistryAddress;
    }

    /**
     * Sets the value of the rmiRegistryAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRmiRegistryAddress(String value) {
        this.rmiRegistryAddress = value;
    }

    /**
     * Gets the value of the rmiRegistryPort property.
     * 
     */
    public int getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    /**
     * Sets the value of the rmiRegistryPort property.
     * 
     */
    public void setRmiRegistryPort(int value) {
        this.rmiRegistryPort = value;
    }

}
