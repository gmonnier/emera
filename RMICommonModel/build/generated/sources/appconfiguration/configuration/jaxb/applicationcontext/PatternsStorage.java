//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.28 at 06:41:22 PM PST 
//


package configuration.jaxb.applicationcontext;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PatternsStorage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PatternsStorage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DefaultPatternIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;sequence>
 *           &lt;element name="Patterns" type="{http://configuration/jaxb/applicationContext}Pattern" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PatternsStorage", propOrder = {
    "defaultPatternIndex",
    "patterns"
})
public class PatternsStorage {

    @XmlElement(name = "DefaultPatternIndex")
    protected int defaultPatternIndex;
    @XmlElement(name = "Patterns")
    protected List<Pattern> patterns;

    /**
     * Gets the value of the defaultPatternIndex property.
     * 
     */
    public int getDefaultPatternIndex() {
        return defaultPatternIndex;
    }

    /**
     * Sets the value of the defaultPatternIndex property.
     * 
     */
    public void setDefaultPatternIndex(int value) {
        this.defaultPatternIndex = value;
    }

    /**
     * Gets the value of the patterns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the patterns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPatterns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pattern }
     * 
     * 
     */
    public List<Pattern> getPatterns() {
        if (patterns == null) {
            patterns = new ArrayList<Pattern>();
        }
        return this.patterns;
    }

}
