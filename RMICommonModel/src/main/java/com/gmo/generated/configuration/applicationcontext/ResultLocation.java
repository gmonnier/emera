//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.10 at 05:30:29 PM PDT 
//


package com.gmo.generated.configuration.applicationcontext;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultLocation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultLocation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="analysisResultsLocationRoot" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="analysisResultsLocationType" type="{http://com/gmo/generated/configuration/applicationContext}locationType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultLocation", propOrder = {
    "analysisResultsLocationRoot",
    "analysisResultsLocationType"
})
public class ResultLocation
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String analysisResultsLocationRoot;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LocationType analysisResultsLocationType;

    /**
     * Gets the value of the analysisResultsLocationRoot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalysisResultsLocationRoot() {
        return analysisResultsLocationRoot;
    }

    /**
     * Sets the value of the analysisResultsLocationRoot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalysisResultsLocationRoot(String value) {
        this.analysisResultsLocationRoot = value;
    }

    public boolean isSetAnalysisResultsLocationRoot() {
        return (this.analysisResultsLocationRoot!= null);
    }

    /**
     * Gets the value of the analysisResultsLocationType property.
     * 
     * @return
     *     possible object is
     *     {@link LocationType }
     *     
     */
    public LocationType getAnalysisResultsLocationType() {
        return analysisResultsLocationType;
    }

    /**
     * Sets the value of the analysisResultsLocationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *     
     */
    public void setAnalysisResultsLocationType(LocationType value) {
        this.analysisResultsLocationType = value;
    }

    public boolean isSetAnalysisResultsLocationType() {
        return (this.analysisResultsLocationType!= null);
    }

}