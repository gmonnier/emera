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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Projects are the fundamental unit of organization in BaseSpace. Users may create projects, place items into projects, and share projects (and their contents) with other users
 * @author bking
 */
public class Project extends ProjectCompact
{
    @JsonProperty("HrefSamples")
    private URI hrefSamples;
    /**
     * The samples inside of this project
     * @return
     */
    public URI getSamplesHref()
    {
        return hrefSamples;
    }
    protected void setSamplesHref(URI hrefSamples)
    {
        this.hrefSamples = hrefSamples;
    }
    
    @JsonProperty("HrefAppResults")
    private URI hrefAppResults;
    /**
     * The appresults in this project
     * @return
     */
    public URI getAppResultsHref()
    {
        return hrefAppResults;
    }
    protected void setAppResultsHref(URI hrefAppResults)
    {
        this.hrefAppResults = hrefAppResults;
    }
    
    @JsonProperty("HrefBaseSpaceUI")
    private String hrefBaseSpaceUI;
    public String getHrefBaseSpaceUI()
    {
        return hrefBaseSpaceUI;
    }
    public void setHrefBaseSpaceUI(String hrefBaseSpaceUI)
    {
        this.hrefBaseSpaceUI = hrefBaseSpaceUI;
    }
    

    
    @Override
    public String toString()
    {
        return "Project [hrefSamples=" + hrefSamples + ", hrefAppResults=" + hrefAppResults + ", hrefBaseSpaceUI="
                + hrefBaseSpaceUI + ", toString()=" + super.toString() + "]";
    }
    
}
