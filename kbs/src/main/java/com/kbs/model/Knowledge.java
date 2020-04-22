package com.kbs.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings("deprecation")
@Entity
public class Knowledge implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "Title cannot be null.")
	@NotEmpty(message = "Title cannot be empty.")
	private String title;

	@NotNull(message = "Description cannot be null.")
	@NotEmpty(message = "Description cannot be empty.")
	private String description;

//	@NotNull(message = "Platform cannot be null.")
//	@NotEmpty(message = "Platform cannot be empty.")
//	private String affectedPlatform;

	@ForeignKey(name = "platform_id")
	@ManyToOne
	private Platform platform;

	@ForeignKey(name = "commodity_id")
	@ManyToOne
	private Commodity commodity;

	@ForeignKey(name = "regioncountry_id")
	@ManyToOne
	private RegionCountry regionCountry;

	private String duplicationSteps;

	private String solution;

	private String severity;

	@NotNull(message = "Status cannot be null.")
	@NotEmpty(message = "Status cannot be empty.")
	private String status;

//	@NotNull(message = "Create date cannot be null.")
//	@NotEmpty(message = "Create date cannot be empty.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date createDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date closeDate;

	@Lob
	private byte[] attachment;

	private String attachementFileName;
	private String attachementFileType;

	public String getAttachementFileName() {
		return attachementFileName;
	}

	public void setAttachementFileName(String attachementFileName) {
		this.attachementFileName = attachementFileName;
	}

	public String getAttachementFileType() {
		return attachementFileType;
	}

	public void setAttachementFileType(String attachementFileType) {
		this.attachementFileType = attachementFileType;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public RegionCountry getRegionCountry() {
		return regionCountry;
	}

	public void setRegionCountry(RegionCountry regionCountry) {
		this.regionCountry = regionCountry;
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public void setCommodity(Commodity commodity) {
		this.commodity = commodity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDuplicationSteps() {
		return duplicationSteps;
	}

	public void setDuplicationSteps(String duplicationSteps) {
		this.duplicationSteps = duplicationSteps;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Knowledge other = (Knowledge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Knowledge [id=" + id + ", title=" + title + ", description=" + description + ", platform=" + platform
				+ ", commodity=" + commodity + ", regionCountry=" + regionCountry + ", duplicationSteps="
				+ duplicationSteps + ", solution=" + solution + ", severity=" + severity + ", status=" + status
				+ ", createDate=" + createDate + ", closeDate=" + closeDate + "]";
	}

}
