package com.iksgmbh.fileman.backend;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.validation.constraints.*;
import javax.persistence.*;

import io.swagger.annotations.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Content of hosted file
 *
 * This file is autogenerated by MOGLiCC. Do not modify manually!
 */
@ApiModel(description = "Content of hosted file")
@Entity
@Table(name="FILE_CONTENT_DATA")
public class FileContentData implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1593425043106L;

	// ===============  instance fields  ===============

    @Column(name="UUID", unique=true, columnDefinition="bigint")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uuid;

    @NotNull(message="Value of mandatory attribute 'name' is not present.")
    @Size(min=3, max=128, message="Value of attribute 'name' is out of valid range (3-128)")
    @ApiModelProperty(notes = "Mandatory. Valid length ranges from 3 to 128.")
    @Column(name="NAME", columnDefinition="varchar")
	private String name;

    @NotNull(message="Value of mandatory attribute 'content' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
    @Column(name="CONTENT", columnDefinition="varchar")
	private String content;

    @NotNull(message="Value of mandatory attribute 'size' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
    @Column(name="SIZE", columnDefinition="bigint")
	private Long size;

    @NotNull(message="Value of mandatory attribute 'creator' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
    @Column(name="CREATOR", columnDefinition="varchar")
	private String creator;

    @Column(name="CREATION_DATE", columnDefinition="datetime")
    @org.springframework.data.annotation.CreatedDate
	private Date creationDate;


	// ===============  setter methods  ===============

	public void setUuid(final Long uuid)
	{
		this.uuid = uuid;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setContent(final String content)
	{
		this.content = content;
	}

	public void setSize(final Long size)
	{
		this.size = size;
	}

	public void setCreator(final String creator)
	{
		this.creator = creator;
	}

	public void setCreationDate(final Date creationDate)
	{
		this.creationDate = creationDate;
	}

	// ===============  getter methods  ===============

	public Long getUuid()
	{
		return uuid;
	}

	public String getName()
	{
		return name;
	}

	public String getContent()
	{
		return content;
	}

	public Long getSize()
	{
		return size;
	}

	public String getCreator()
	{
		return creator;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	// ===============  additional Javabean methods  ===============

	@Override
	public String toString()
	{
		return "FileContentData ["
				+ "uuid = " + uuid + ", "
				+ "name = " + name + ", "
				+ "content = " + content + ", "
				+ "size = " + size + ", "
				+ "creator = " + creator + ", "
				+ "creationDate = " + creationDate + ""
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final FileContentData other = (FileContentData) obj;

		if (uuid == null)
		{
			if (other.uuid != null)
				return false;
		} else
		{
			if (! uuid.equals(other.uuid))
				   return false;
		}
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else
		{
			if (! name.equals(other.name))
				   return false;
		}
		if (content == null)
		{
			if (other.content != null)
				return false;
		} else
		{
			if (! content.equals(other.content))
				   return false;
		}
		if (size == null)
		{
			if (other.size != null)
				return false;
		} else
		{
			if (! size.equals(other.size))
				   return false;
		}
		if (creator == null)
		{
			if (other.creator != null)
				return false;
		} else
		{
			if (! creator.equals(other.creator))
				   return false;
		}
		if (creationDate == null)
		{
			if (other.creationDate != null)
				return false;
		} else
		{
			if (! creationDate.equals(other.creationDate))
				   return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());

		return result;
	}

	@Override
	public Object clone()
	{
		final FileContentData clone;
		try {
			clone = (FileContentData) super.clone();
		} catch (Exception e) {
			throw new AssertionError("Unexpected error cloning " + this);
		}

		if (this.uuid != null) clone.uuid = new Long(this.uuid);
		if (this.name != null) clone.name = new String(name);
		if (this.content != null) clone.content = new String(content);
		if (this.size != null) clone.size = new Long(this.size);
		if (this.creator != null) clone.creator = new String(creator);
		if (this.creationDate != null) clone.creationDate = (java.util.Date)this.creationDate.clone();  // probably, here is need of manual adaptation

		return clone;
	}

	public void merge(FileContentData otherFileContentData)
	{
        if (otherFileContentData.getUuid() != null) {
            this.setUuid(otherFileContentData.getUuid());
       }
        if (otherFileContentData.getName() != null) {
            if(! otherFileContentData.getName().isEmpty()) {
           	 this.setName(otherFileContentData.getName());
            }
       }
        if (otherFileContentData.getContent() != null) {
            if(! otherFileContentData.getContent().isEmpty()) {
           	 this.setContent(otherFileContentData.getContent());
            }
       }
        if (otherFileContentData.getSize() != null) {
            this.setSize(otherFileContentData.getSize());
       }
        if (otherFileContentData.getCreator() != null) {
            if(! otherFileContentData.getCreator().isEmpty()) {
           	 this.setCreator(otherFileContentData.getCreator());
            }
       }
        if (otherFileContentData.getCreationDate() != null) {
            this.setCreationDate(otherFileContentData.getCreationDate());
       }
	}
}