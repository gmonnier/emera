//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.14 at 08:50:54 PM PDT 
//


package com.gmo.generated.configuration.applicationcontext;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SplitPatternsStorage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SplitPatternsStorage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="SplitPatterns" type="{http://com/gmo/generated/configuration/applicationContext}SplitPattern" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SplitPatternsStorage", propOrder = {
    "splitPatterns"
})
public class SplitPatternsStorage {

    @XmlElement(name = "SplitPatterns")
    protected List<SplitPattern> splitPatterns;

    /**
     * Gets the value of the splitPatterns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the splitPatterns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSplitPatterns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SplitPattern }
     * 
     * 
     */
    public List<SplitPattern> getSplitPatterns() {
        if (splitPatterns == null) {
            splitPatterns = new ArrayList<SplitPattern>();
        }
        return this.splitPatterns;
    }

}
