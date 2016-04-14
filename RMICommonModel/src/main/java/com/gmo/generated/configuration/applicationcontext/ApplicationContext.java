//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.14 at 01:00:20 PM PDT 
//


package com.gmo.generated.configuration.applicationcontext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="webServerPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="AllowCharacterError" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AllowShifting" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GeneratePDFOutput" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GenerateCSVOutput" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PatternsStorage" type="{http://com/gmo/generated/configuration/applicationContext}PatternsStorage"/>
 *         &lt;element name="SplitPatternsStorage" type="{http://com/gmo/generated/configuration/applicationContext}SplitPatternsStorage"/>
 *         &lt;element name="checkForUnfoundEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="analysisResultsLocation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="analysisResultsLocationType" type="{http://com/gmo/generated/configuration/applicationContext}locationType"/>
 *         &lt;element name="nodeConnectionConfiguration" type="{http://com/gmo/generated/configuration/applicationContext}RMIConnectionConfig"/>
 *         &lt;element name="basespaceConnectionConfiguration" type="{http://com/gmo/generated/configuration/applicationContext}RMIConnectionConfig"/>
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
    "webServerPort",
    "allowCharacterError",
    "allowShifting",
    "generatePDFOutput",
    "generateCSVOutput",
    "patternsStorage",
    "splitPatternsStorage",
    "checkForUnfoundEntries",
    "analysisResultsLocation",
    "analysisResultsLocationType",
    "nodeConnectionConfiguration",
    "basespaceConnectionConfiguration"
})
@XmlRootElement(name = "ApplicationContext")
public class ApplicationContext {

    protected int webServerPort;
    @XmlElement(name = "AllowCharacterError")
    protected boolean allowCharacterError;
    @XmlElement(name = "AllowShifting")
    protected boolean allowShifting;
    @XmlElement(name = "GeneratePDFOutput")
    protected boolean generatePDFOutput;
    @XmlElement(name = "GenerateCSVOutput")
    protected boolean generateCSVOutput;
    @XmlElement(name = "PatternsStorage", required = true)
    protected PatternsStorage patternsStorage;
    @XmlElement(name = "SplitPatternsStorage", required = true)
    protected SplitPatternsStorage splitPatternsStorage;
    protected boolean checkForUnfoundEntries;
    @XmlElement(required = true)
    protected String analysisResultsLocation;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LocationType analysisResultsLocationType;
    @XmlElement(required = true)
    protected RMIConnectionConfig nodeConnectionConfiguration;
    @XmlElement(required = true)
    protected RMIConnectionConfig basespaceConnectionConfiguration;

    /**
     * Gets the value of the webServerPort property.
     * 
     */
    public int getWebServerPort() {
        return webServerPort;
    }

    /**
     * Sets the value of the webServerPort property.
     * 
     */
    public void setWebServerPort(int value) {
        this.webServerPort = value;
    }

    /**
     * Gets the value of the allowCharacterError property.
     * 
     */
    public boolean isAllowCharacterError() {
        return allowCharacterError;
    }

    /**
     * Sets the value of the allowCharacterError property.
     * 
     */
    public void setAllowCharacterError(boolean value) {
        this.allowCharacterError = value;
    }

    /**
     * Gets the value of the allowShifting property.
     * 
     */
    public boolean isAllowShifting() {
        return allowShifting;
    }

    /**
     * Sets the value of the allowShifting property.
     * 
     */
    public void setAllowShifting(boolean value) {
        this.allowShifting = value;
    }

    /**
     * Gets the value of the generatePDFOutput property.
     * 
     */
    public boolean isGeneratePDFOutput() {
        return generatePDFOutput;
    }

    /**
     * Sets the value of the generatePDFOutput property.
     * 
     */
    public void setGeneratePDFOutput(boolean value) {
        this.generatePDFOutput = value;
    }

    /**
     * Gets the value of the generateCSVOutput property.
     * 
     */
    public boolean isGenerateCSVOutput() {
        return generateCSVOutput;
    }

    /**
     * Sets the value of the generateCSVOutput property.
     * 
     */
    public void setGenerateCSVOutput(boolean value) {
        this.generateCSVOutput = value;
    }

    /**
     * Gets the value of the patternsStorage property.
     * 
     * @return
     *     possible object is
     *     {@link PatternsStorage }
     *     
     */
    public PatternsStorage getPatternsStorage() {
        return patternsStorage;
    }

    /**
     * Sets the value of the patternsStorage property.
     * 
     * @param value
     *     allowed object is
     *     {@link PatternsStorage }
     *     
     */
    public void setPatternsStorage(PatternsStorage value) {
        this.patternsStorage = value;
    }

    /**
     * Gets the value of the splitPatternsStorage property.
     * 
     * @return
     *     possible object is
     *     {@link SplitPatternsStorage }
     *     
     */
    public SplitPatternsStorage getSplitPatternsStorage() {
        return splitPatternsStorage;
    }

    /**
     * Sets the value of the splitPatternsStorage property.
     * 
     * @param value
     *     allowed object is
     *     {@link SplitPatternsStorage }
     *     
     */
    public void setSplitPatternsStorage(SplitPatternsStorage value) {
        this.splitPatternsStorage = value;
    }

    /**
     * Gets the value of the checkForUnfoundEntries property.
     * 
     */
    public boolean isCheckForUnfoundEntries() {
        return checkForUnfoundEntries;
    }

    /**
     * Sets the value of the checkForUnfoundEntries property.
     * 
     */
    public void setCheckForUnfoundEntries(boolean value) {
        this.checkForUnfoundEntries = value;
    }

    /**
     * Gets the value of the analysisResultsLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalysisResultsLocation() {
        return analysisResultsLocation;
    }

    /**
     * Sets the value of the analysisResultsLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalysisResultsLocation(String value) {
        this.analysisResultsLocation = value;
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

    /**
     * Gets the value of the nodeConnectionConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link RMIConnectionConfig }
     *     
     */
    public RMIConnectionConfig getNodeConnectionConfiguration() {
        return nodeConnectionConfiguration;
    }

    /**
     * Sets the value of the nodeConnectionConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link RMIConnectionConfig }
     *     
     */
    public void setNodeConnectionConfiguration(RMIConnectionConfig value) {
        this.nodeConnectionConfiguration = value;
    }

    /**
     * Gets the value of the basespaceConnectionConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link RMIConnectionConfig }
     *     
     */
    public RMIConnectionConfig getBasespaceConnectionConfiguration() {
        return basespaceConnectionConfiguration;
    }

    /**
     * Sets the value of the basespaceConnectionConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link RMIConnectionConfig }
     *     
     */
    public void setBasespaceConnectionConfiguration(RMIConnectionConfig value) {
        this.basespaceConnectionConfiguration = value;
    }

}