//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.21 at 12:44:49 PM PDT 
//


package configuration.jaxb.storageconfig;

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
 *         &lt;element name="DataFilesRoot" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LibrariesFilesRoot" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "dataFilesRoot",
    "librariesFilesRoot"
})
@XmlRootElement(name = "StorageConfiguration")
public class StorageConfiguration {

    @XmlElement(name = "DataFilesRoot", required = true)
    protected String dataFilesRoot;
    @XmlElement(name = "LibrariesFilesRoot", required = true)
    protected String librariesFilesRoot;

    /**
     * Gets the value of the dataFilesRoot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataFilesRoot() {
        return dataFilesRoot;
    }

    /**
     * Sets the value of the dataFilesRoot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataFilesRoot(String value) {
        this.dataFilesRoot = value;
    }

    /**
     * Gets the value of the librariesFilesRoot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibrariesFilesRoot() {
        return librariesFilesRoot;
    }

    /**
     * Sets the value of the librariesFilesRoot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibrariesFilesRoot(String value) {
        this.librariesFilesRoot = value;
    }

}
