//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.23 at 01:38:54 PM PDT 
//


package com.gmo.generated.configuration.basespace;

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
 *         &lt;element name="bsUserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bsPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bsClientID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bsClientSecret" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bsAccessToken" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "bsUserName",
    "bsPassword",
    "bsClientID",
    "bsClientSecret",
    "bsAccessToken"
})
@XmlRootElement(name = "BaseSpaceConfiguration")
public class BaseSpaceConfiguration {

    @XmlElement(required = true)
    protected String bsUserName;
    @XmlElement(required = true)
    protected String bsPassword;
    @XmlElement(required = true)
    protected String bsClientID;
    @XmlElement(required = true)
    protected String bsClientSecret;
    @XmlElement(required = true)
    protected String bsAccessToken;

    /**
     * Gets the value of the bsUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsUserName() {
        return bsUserName;
    }

    /**
     * Sets the value of the bsUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsUserName(String value) {
        this.bsUserName = value;
    }

    /**
     * Gets the value of the bsPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsPassword() {
        return bsPassword;
    }

    /**
     * Sets the value of the bsPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsPassword(String value) {
        this.bsPassword = value;
    }

    /**
     * Gets the value of the bsClientID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsClientID() {
        return bsClientID;
    }

    /**
     * Sets the value of the bsClientID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsClientID(String value) {
        this.bsClientID = value;
    }

    /**
     * Gets the value of the bsClientSecret property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsClientSecret() {
        return bsClientSecret;
    }

    /**
     * Sets the value of the bsClientSecret property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsClientSecret(String value) {
        this.bsClientSecret = value;
    }

    /**
     * Gets the value of the bsAccessToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsAccessToken() {
        return bsAccessToken;
    }

    /**
     * Sets the value of the bsAccessToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsAccessToken(String value) {
        this.bsAccessToken = value;
    }

}
