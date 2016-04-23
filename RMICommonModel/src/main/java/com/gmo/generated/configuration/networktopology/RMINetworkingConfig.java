//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.23 at 01:38:54 PM PDT 
//


package com.gmo.generated.configuration.networktopology;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RMINetworkingConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RMINetworkingConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RmiRegistryParameters" type="{http://com/gmo/generated/configuration/networkTopology}RMIRegistryParameters"/>
 *         &lt;sequence>
 *           &lt;element name="RmiInterfaces" type="{http://com/gmo/generated/configuration/networkTopology}RmiInterface" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RMINetworkingConfig", propOrder = {
    "rmiRegistryParameters",
    "rmiInterfaces"
})
public class RMINetworkingConfig {

    @XmlElement(name = "RmiRegistryParameters", required = true)
    protected RMIRegistryParameters rmiRegistryParameters;
    @XmlElement(name = "RmiInterfaces")
    protected List<RmiInterface> rmiInterfaces;

    /**
     * Gets the value of the rmiRegistryParameters property.
     * 
     * @return
     *     possible object is
     *     {@link RMIRegistryParameters }
     *     
     */
    public RMIRegistryParameters getRmiRegistryParameters() {
        return rmiRegistryParameters;
    }

    /**
     * Sets the value of the rmiRegistryParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link RMIRegistryParameters }
     *     
     */
    public void setRmiRegistryParameters(RMIRegistryParameters value) {
        this.rmiRegistryParameters = value;
    }

    /**
     * Gets the value of the rmiInterfaces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rmiInterfaces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRmiInterfaces().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RmiInterface }
     * 
     * 
     */
    public List<RmiInterface> getRmiInterfaces() {
        if (rmiInterfaces == null) {
            rmiInterfaces = new ArrayList<RmiInterface>();
        }
        return this.rmiInterfaces;
    }

}
