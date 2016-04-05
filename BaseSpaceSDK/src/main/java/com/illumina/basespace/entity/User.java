/**
* Copyright 2013 Illumina
* 
 * Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*    http://www.apache.org/licenses/LICENSE-2.0
* 
 *  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package com.illumina.basespace.entity;

import java.net.URI;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.illumina.basespace.util.DateDeserializer;

/**
 * The User resource is provided so that an app can get basic information about the user that is using the application
 * @author bking
 *
 */
public class User extends UserCompact
{
    @JsonProperty("Email")
    private String email;

    /**
     * Current user's email address
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }
    protected void setEmail(String email)
    {
        this.email = email;
    }
    


    @JsonDeserialize(using=DateDeserializer.class)
    @JsonProperty("DateLastActive")
    private Date dateLastActive;
    /**
     * Get the date this user was last active
     * @return the last active date
     */
    public Date getDateLastActive()
    {
        return dateLastActive;
    }
    protected void setDateLastActive(Date dateLastActive)
    {
        this.dateLastActive = dateLastActive;
    }
    
    @JsonProperty("HrefRuns")
    private URI hrefRuns;
    /**
     * The runs that the user owns or has access to
     * @return the runs URI
     */
    public URI getHrefRuns()
    {
        return hrefRuns;
    }
    protected void setHrefRuns(URI hrefRuns)
    {
        this.hrefRuns = hrefRuns;
    }

    @JsonProperty("HrefProjects")
    private URI hrefProjects;
    /**
     *  The projects that the user owns or has access to
     * @return the projects URI
     */
    public URI getHrefProjects()
    {
        return hrefProjects;
    }
    protected void setHrefProjects(URI hrefProjects)
    {
        this.hrefProjects = hrefProjects;
    }
    
    @JsonDeserialize(using=DateDeserializer.class)
    @JsonProperty("DateCreated")
    private Date dateCreated;

    /**
     * Get the date this entity was created
     * @return the create date
     */
    public Date getDateCreated()
    {
        return dateCreated;
    }
    protected void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    
    
    

    
    
  
 
    
}
