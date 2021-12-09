package org.team1.client;

import org.team1.models.ZoomLink;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for baseResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="baseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zoomLinkList" type="{http://eservice/}zoomLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseResponse", propOrder = {
        "zoomLinkList"
})
public class BaseResponse {

    @XmlElement
    protected List<ZoomLink> zoomLinkList;

    /**
     * Gets the value of the zoomLinkList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the zoomLinkList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZoomLinkList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ZoomLink }
     */
    public List<ZoomLink> getZoomLinkList() {
        if (zoomLinkList == null) {
            zoomLinkList = new ArrayList<ZoomLink>();
        }
        // System.out.println("Inside base response :"+this.zoomLinkList.get(0).getMeetingId());
        return this.zoomLinkList;
    }

}
