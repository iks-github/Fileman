/*
 * Copyright 2020 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.fileman.backend;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * JavaBean class of the MOGLiCC JavaBean Group.
 *
 * Data for favourite filter
 *
 * @author generated by MOGLiCC
 */
@ApiModel(description = "Data for favourite filter")
public class FavouriteSetting implements Serializable
{
	private static final long serialVersionUID = 1588950083313L;

	// ===============  instance fields  ===============

    @NotNull(message="Value of mandatory attribute 'id' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
	private Integer id;

    @NotNull(message="Value of mandatory attribute 'username' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
	private String username;

    @NotNull(message="Value of mandatory attribute 'filename' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
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