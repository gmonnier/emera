//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.21 at 12:44:42 PM PDT 
//


package configuration.jaxb.aws;

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
 *         &lt;element name="awsAccessKeyID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="awsSecretAccessKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IAMUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IAMpwd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="aws_account_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IAM_URL_Login" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sshUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sshPrivateKeyFile" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "awsAccessKeyID",
    "awsSecretAccessKey",
    "iamUser",
    "iaMpwd",
    "awsAccountId",
    "iamurlLogin",
    "sshUser",
    "sshPrivateKeyFile"
})
@XmlRootElement(name = "AWSConfiguration")
public class AWSConfiguration {

    @XmlElement(required = true)
    protected String awsAccessKeyID;
    @XmlElement(required = true)
    protected String awsSecretAccessKey;
    @XmlElement(name = "IAMUser", required = true)
    protected String iamUser;
    @XmlElement(name = "IAMpwd", required = true)
    protected String iaMpwd;
    @XmlElement(name = "aws_account_id", required = true)
    protected String awsAccountId;
    @XmlElement(name = "IAM_URL_Login", required = true)
    protected String iamurlLogin;
    @XmlElement(required = true)
    protected String sshUser;
    @XmlElement(required = true)
    protected String sshPrivateKeyFile;

    /**
     * Gets the value of the awsAccessKeyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAwsAccessKeyID() {
        return awsAccessKeyID;
    }

    /**
     * Sets the value of the awsAccessKeyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAwsAccessKeyID(String value) {
        this.awsAccessKeyID = value;
    }

    /**
     * Gets the value of the awsSecretAccessKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    /**
     * Sets the value of the awsSecretAccessKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAwsSecretAccessKey(String value) {
        this.awsSecretAccessKey = value;
    }

    /**
     * Gets the value of the iamUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIAMUser() {
        return iamUser;
    }

    /**
     * Sets the value of the iamUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIAMUser(String value) {
        this.iamUser = value;
    }

    /**
     * Gets the value of the iaMpwd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIAMpwd() {
        return iaMpwd;
    }

    /**
     * Sets the value of the iaMpwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIAMpwd(String value) {
        this.iaMpwd = value;
    }

    /**
     * Gets the value of the awsAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAwsAccountId() {
        return awsAccountId;
    }

    /**
     * Sets the value of the awsAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAwsAccountId(String value) {
        this.awsAccountId = value;
    }

    /**
     * Gets the value of the iamurlLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIAMURLLogin() {
        return iamurlLogin;
    }

    /**
     * Sets the value of the iamurlLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIAMURLLogin(String value) {
        this.iamurlLogin = value;
    }

    /**
     * Gets the value of the sshUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSshUser() {
        return sshUser;
    }

    /**
     * Sets the value of the sshUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSshUser(String value) {
        this.sshUser = value;
    }

    /**
     * Gets the value of the sshPrivateKeyFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSshPrivateKeyFile() {
        return sshPrivateKeyFile;
    }

    /**
     * Sets the value of the sshPrivateKeyFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSshPrivateKeyFile(String value) {
        this.sshPrivateKeyFile = value;
    }

}
