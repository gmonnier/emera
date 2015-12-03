//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.02 at 11:47:34 PM PST 
//


package configuration.jaxb.applicationcontext;

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
 *         &lt;element name="AllowCharacterError" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AllowShifting" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GeneratePDFOutput" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GenerateCSVOutput" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PatternsStorage" type="{http://configuration/jaxb/applicationContext}PatternsStorage"/>
 *         &lt;element name="SplitPatternsStorage" type="{http://configuration/jaxb/applicationContext}SplitPatternsStorage"/>
 *         &lt;element name="checkForUnfoundEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="analysisResultsLocation" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "allowCharacterError",
    "allowShifting",
    "generatePDFOutput",
    "generateCSVOutput",
    "patternsStorage",
    "splitPatternsStorage",
    "checkForUnfoundEntries",
    "analysisResultsLocation"
})
@XmlRootElement(name = "ApplicationContext")
public class ApplicationContext {

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

}
