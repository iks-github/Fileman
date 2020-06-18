package com.iksgmbh.fileman.backend;

import com.iksgmbh.fileman.backend.User;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;

import javax.validation.constraints.*;
import javax.persistence.*;

import io.swagger.annotations.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * JavaBean class of the MOGLiCC JavaBean Group.
 *
 * Data for favourite filter
 *
 * This file is autogenerated by MOGLiCC. Do not modify manually!
 */
@ApiModel(description = "Data for favourite filter")
@Entity
@Table(name="FAVOURITE_SETTING")
public class FavouriteSetting implements Serializable
{
	private static final long serialVersionUID = 1592489392543L;

	// ===============  instance fields  ===============

    @Column(name="ID", unique=true, columnDefinition="int")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    @NotNull(message="Value of mandatory attribute 'username' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
    @Column(name="USERNAME", columnDefinition="varchar")
	private String username;

    @NotNull(message="Value of mandatory attribute 'filename' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
    @Column(name="FILENAME", columnDefinition="varchar")
	private String filename;


	// ===============  setter methods  ===============

	public void setId(final Integer id)
	{
		this.id = id;
	}

	public void setUsername(final String username)
	{
		this.username = username;
	}

	public void setFilename(final String filename)
	{
		this.filename = filename;
	}

	// ===============  getter methods  ===============

	public Integer getId()
	{
		return id;
	}

	public String getUsername()
	{
		return username;
	}

	public String getFilename()
	{
		return filename;
	}

	// ===============  additional Javabean methods  ===============

	@Override
	public String toString()
	{
		return "FavouriteSetting ["
				+ "id = " + id + ", "
				+ "username = " + username + ", "
				+ "filename = " + filename + ""
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

		final FavouriteSetting other = (FavouriteSetting) obj;

		if (id == null)
		{
			if (other.id != null)
				return false;
		} else
		{
			if (! id.equals(other.id))
				   return false;
		}
		if (username == null)
		{
			if (other.username != null)
				return false;
		} else
		{
			if (! username.equals(other.username))
				   return false;
		}
		if (filename == null)
		{
			if (other.filename != null)
				return false;
		} else
		{
			if (! filename.equals(other.filename))
				   return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());

		return result;
	}


	public void merge(FavouriteSetting otherFavouriteSetting)
	{
        if (otherFavouriteSetting.getId() != null) {
            this.setId(otherFavouriteSetting.getId());
       }
        if (otherFavouriteSetting.getUsername() != null) {
            if(! otherFavouriteSetting.getUsername().isEmpty()) {
           	 this.setUsername(otherFavouriteSetting.getUsername());
            }
       }
        if (otherFavouriteSetting.getFilename() != null) {
            if(! otherFavouriteSetting.getFilename().isEmpty()) {
           	 this.setFilename(otherFavouriteSetting.getFilename());
            }
       }
	}
}