package com.iksgmbh.fileman.backend;

import java.lang.String;

import javax.validation.constraints.*;
import javax.persistence.*;

import io.swagger.annotations.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * This file is autogenerated by MOGLiCC. Do not modify manually!
 */
public class LoginRequest
{
	// ===============  instance fields  ===============

    @NotNull(message="Value of mandatory attribute 'userId' is not present.")
    @Size(min=2, max=64, message="Value of attribute 'userId' is out of valid range (2-64)")
    @ApiModelProperty(notes = "Mandatory. Valid length ranges from 2 to 64.")
	private String userId;

    @NotNull(message="Value of mandatory attribute 'userPw' is not present.")
    @Size(max=32, message="Value of attribute 'userPw' is larger than valid maximum (32).")
    @ApiModelProperty(notes = "Mandatory. Valid maximum length is 32.")
	private String userPw;

    @NotNull(message="Value of mandatory attribute 'filemanVersion' is not present.")
    @ApiModelProperty(notes = "Mandatory.")
	private String filemanVersion;


	// ===============  setter methods  ===============

	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

	public void setUserPw(final String userPw)
	{
		this.userPw = userPw;
	}

	public void setFilemanVersion(final String filemanVersion)
	{
		this.filemanVersion = filemanVersion;
	}

	// ===============  getter methods  ===============

	public String getUserId()
	{
		return userId;
	}

	public String getUserPw()
	{
		return userPw;
	}

	public String getFilemanVersion()
	{
		return filemanVersion;
	}

	// ===============  additional Javabean methods  ===============

	@Override
	public String toString()
	{
		return "LoginRequest ["
				+ "userId = " + userId + ", "
				+ "userPw = " + userPw + ", "
				+ "filemanVersion = " + filemanVersion + ""
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

		final LoginRequest other = (LoginRequest) obj;

		if (userId == null)
		{
			if (other.userId != null)
				return false;
		} else
		{
			if (! userId.equals(other.userId))
				   return false;
		}
		if (userPw == null)
		{
			if (other.userPw != null)
				return false;
		} else
		{
			if (! userPw.equals(other.userPw))
				   return false;
		}
		if (filemanVersion == null)
		{
			if (other.filemanVersion != null)
				return false;
		} else
		{
			if (! filemanVersion.equals(other.filemanVersion))
				   return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((userPw == null) ? 0 : userPw.hashCode());
		result = prime * result + ((filemanVersion == null) ? 0 : filemanVersion.hashCode());

		return result;
	}


	public void merge(LoginRequest otherLoginRequest)
	{
        if (otherLoginRequest.getUserId() != null) {
            if(! otherLoginRequest.getUserId().isEmpty()) {
           	 this.setUserId(otherLoginRequest.getUserId());
            }
       }
        if (otherLoginRequest.getUserPw() != null) {
            if(! otherLoginRequest.getUserPw().isEmpty()) {
           	 this.setUserPw(otherLoginRequest.getUserPw());
            }
       }
        if (otherLoginRequest.getFilemanVersion() != null) {
            if(! otherLoginRequest.getFilemanVersion().isEmpty()) {
           	 this.setFilemanVersion(otherLoginRequest.getFilemanVersion());
            }
       }
	}
}