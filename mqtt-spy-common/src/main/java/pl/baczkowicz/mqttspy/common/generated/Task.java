//
// Copyright (c) 2015 Kamil Baczkowicz
//
// CSOFF: a.*
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Timestamp removed by maven-replacer-plugin to avoid detecting changes - see the project POM for details
//


package pl.baczkowicz.mqttspy.common.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jvnet.jaxb2_commons.lang.CopyTo;
import org.jvnet.jaxb2_commons.lang.Copyable;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.builder.CopyBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBCopyBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBEqualsBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBHashCodeBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBToStringBuilder;


/**
 * <p>Java class for Task complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Task">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="autoStart" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="repeat" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
*/
@SuppressWarnings("all")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Task")
@XmlSeeAlso({
    ScriptDetails.class
})
public class Task implements CopyTo, Copyable, Equals, HashCode, ToString
{

    @XmlAttribute(name = "autoStart")
    protected Boolean autoStart;
    @XmlAttribute(name = "repeat")
    protected Boolean repeat;

    /**
     * Default no-arg constructor
     * 
     */
    public Task() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public Task(final Boolean autoStart, final Boolean repeat) {
        this.autoStart = autoStart;
        this.repeat = repeat;
    }

    /**
     * Gets the value of the autoStart property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Sets the value of the autoStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoStart(Boolean value) {
        this.autoStart = value;
    }

    /**
     * Gets the value of the repeat property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRepeat() {
        return repeat;
    }

    /**
     * Sets the value of the repeat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRepeat(Boolean value) {
        this.repeat = value;
    }

    public void toString(ToStringBuilder toStringBuilder) {
        {
            Boolean theAutoStart;
            theAutoStart = this.isAutoStart();
            toStringBuilder.append("autoStart", theAutoStart);
        }
        {
            Boolean theRepeat;
            theRepeat = this.isRepeat();
            toStringBuilder.append("repeat", theRepeat);
        }
    }

    public String toString() {
        final ToStringBuilder toStringBuilder = new JAXBToStringBuilder(this);
        toString(toStringBuilder);
        return toStringBuilder.toString();
    }

    public void equals(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof Task)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final Task that = ((Task) object);
        equalsBuilder.append(this.isAutoStart(), that.isAutoStart());
        equalsBuilder.append(this.isRepeat(), that.isRepeat());
    }

    public boolean equals(Object object) {
        if (!(object instanceof Task)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EqualsBuilder equalsBuilder = new JAXBEqualsBuilder();
        equals(object, equalsBuilder);
        return equalsBuilder.isEquals();
    }

    public void hashCode(HashCodeBuilder hashCodeBuilder) {
        hashCodeBuilder.append(this.isAutoStart());
        hashCodeBuilder.append(this.isRepeat());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new JAXBHashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

    public Object copyTo(Object target, CopyBuilder copyBuilder) {
        final Task copy = ((target == null)?((Task) createCopy()):((Task) target));
        {
            Boolean sourceAutoStart;
            sourceAutoStart = this.isAutoStart();
            Boolean copyAutoStart = ((Boolean) copyBuilder.copy(sourceAutoStart));
            copy.setAutoStart(copyAutoStart);
        }
        {
            Boolean sourceRepeat;
            sourceRepeat = this.isRepeat();
            Boolean copyRepeat = ((Boolean) copyBuilder.copy(sourceRepeat));
            copy.setRepeat(copyRepeat);
        }
        return copy;
    }

    public Object copyTo(Object target) {
        final CopyBuilder copyBuilder = new JAXBCopyBuilder();
        return copyTo(target, copyBuilder);
    }

    public Object createCopy() {
        return new Task();
    }

}
