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
package com.illumina.basespace.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Fetch Params specific to retrieving files from BaseSpace. Supports file-extension based filtering
 * @author bking
 *
 */
public class FileParams extends QueryParams
{
    private List<String>extensions = new ArrayList<String>();
    
    public FileParams(String[]extensions)
    {
        this(extensions,128);
    }
    
    public FileParams(String[]extensions,int limit)
    {
        this(extensions,0,limit);
    }
    
    public FileParams(String[]extensions,int offset,int limit)
    {
        super(offset,limit);
        this.extensions = Arrays.asList(extensions);
    }
    
    
    @Override
    public MultivaluedMap<String, String> toMap()
    {
        MultivaluedMap<String, String> map = super.toMap();
        if (extensions != null && extensions.size() > 0)
        {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for(String extension:extensions)
            {
                if (!first)sb.append(",");
                if (!extension.startsWith("."))extension = "." + extension;
                sb.append(extension);
                first = false;
            }
            map.add("Extensions", sb.toString());
        }
        return map;
        
    }
    
}
