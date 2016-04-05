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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Samples are the result of demultiplexing the output of a sequencing instrument flow cell run. A Sample in BaseSpace contains metadata about how it was produced (i.e. from the sample sheet) and a collection of Files representing all the reads in compressed FASTQ format.
 * @author bking
 *
 */
public class SampleCompact extends OwnedResource
{
    @JsonProperty("SampleId")
    protected String sampleId;
    /**
     * The Id of the Sample from the samplesheet, this is specified by the user at the flow cell level
     * @return
     */
    public String getSampleId()
    {
        return sampleId;
    }
    public void setSampleId(String sampleId)
    {
        this.sampleId = sampleId;
    }
    
    @JsonProperty("Status")
    private String status;
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    @JsonProperty("StatusSummary")
    private String statusSummary;
    public String getStatusSummary()
    {
        return statusSummary;
    }
    public void setStatusSummary(String statusSummary)
    {
        this.statusSummary = statusSummary;
    }
    
    @JsonProperty("TotalSize")
    private long totalSize;
    public long getTotalSize()
    {
        return totalSize;
    }
    public void setTotalSize(long totalSize)
    {
        this.totalSize = totalSize;
    }
    
    
    
    @Override
    public String toString()
    {
        return "SampleCompact [sampleId=" + sampleId + ", status=" + status + ", statusSummary=" + statusSummary
                + ", toString()=" + super.toString() + "]";
    }
    
    
}
