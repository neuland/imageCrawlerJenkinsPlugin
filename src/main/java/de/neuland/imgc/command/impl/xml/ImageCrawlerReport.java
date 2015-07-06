//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.03.02 um 01:09:32 PM CET 
//

package de.neuland.imgc.command.impl.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fileCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="imageCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="failedImageCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ratio" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fileResult" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="imageResult" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="found" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                             &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "time",
    "fileCount",
    "imageCount",
    "failedImageCounter",
    "ratio",
    "fileResult"
})
@XmlRootElement(name = "imageCrawlerReport")
public class ImageCrawlerReport {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;
    protected int fileCount;
    protected int imageCount;
    protected int failedImageCounter;
    protected double ratio;
    @XmlElement(required = true)
    protected List<ImageCrawlerReport.FileResult> fileResult;

    /**
     * Ruft den Wert der time-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Legt den Wert der time-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Ruft den Wert der fileCount-Eigenschaft ab.
     * 
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * Legt den Wert der fileCount-Eigenschaft fest.
     * 
     */
    public void setFileCount(int value) {
        this.fileCount = value;
    }

    /**
     * Ruft den Wert der imageCount-Eigenschaft ab.
     * 
     */
    public int getImageCount() {
        return imageCount;
    }

    /**
     * Legt den Wert der imageCount-Eigenschaft fest.
     * 
     */
    public void setImageCount(int value) {
        this.imageCount = value;
    }

    /**
     * Ruft den Wert der failedImageCount-Eigenschaft ab.
     * 
     */
    public int getFailedImageCounter() {
        return failedImageCounter;
    }

    /**
     * Legt den Wert der failedImageCount-Eigenschaft fest.
     * 
     */
    public void setFailedImageCounter(int value) {
        this.failedImageCounter = value;
    }

    /**
     * Ruft den Wert der ratio-Eigenschaft ab.
     * 
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Legt den Wert der ratio-Eigenschaft fest.
     * 
     */
    public void setRatio(double value) {
        this.ratio = value;
    }

    /**
     * Gets the value of the fileResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImageCrawlerReport.FileResult }
     * 
     * 
     */
    public List<ImageCrawlerReport.FileResult> getFileResult() {
        if (fileResult == null) {
            fileResult = new ArrayList<ImageCrawlerReport.FileResult>();
        }
        return this.fileResult;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="imageResult" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="found" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *                   &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "fileName",
        "imageResult"
    })
    public static class FileResult {

        @XmlElement(required = true)
        protected String fileName;
        @XmlElement(required = true)
        protected List<ImageCrawlerReport.FileResult.ImageResult> imageResult;

        /**
         * Ruft den Wert der fileName-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Legt den Wert der fileName-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFileName(String value) {
            this.fileName = value;
        }

        /**
         * Gets the value of the imageResult property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imageResult property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getImageResult().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageCrawlerReport.FileResult.ImageResult }
         * 
         * 
         */
        public List<ImageCrawlerReport.FileResult.ImageResult> getImageResult() {
            if (imageResult == null) {
                imageResult = new ArrayList<ImageCrawlerReport.FileResult.ImageResult>();
            }
            return this.imageResult;
        }


        /**
         * <p>Java-Klasse für anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="found" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
         *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "found",
            "image"
        })
        public static class ImageResult {

            protected boolean found;
            @XmlElement(required = true)
            protected String image;

            /**
             * Ruft den Wert der found-Eigenschaft ab.
             * 
             */
            public boolean isFound() {
                return found;
            }

            /**
             * Legt den Wert der found-Eigenschaft fest.
             * 
             */
            public void setFound(boolean value) {
                this.found = value;
            }

            /**
             * Ruft den Wert der image-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getImage() {
                return image;
            }

            /**
             * Legt den Wert der image-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setImage(String value) {
                this.image = value;
            }

        }

    }

}
