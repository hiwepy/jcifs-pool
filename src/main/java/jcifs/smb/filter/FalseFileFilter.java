/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jcifs.smb.filter;

import java.io.Serializable;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


@SuppressWarnings("serial")
public class FalseFileFilter implements Smb2FileFilter , Serializable {
   
	public static final Smb2FileFilter FALSE = new FalseFileFilter();
    public static final Smb2FileFilter INSTANCE = FALSE;

    /**
     * Restrictive consructor.
     */
    protected FalseFileFilter() {
    }

    /**
     * Returns false.
     *
     * @param file  the file to check (ignored)
     * @return false
     */
    @Override
    public boolean accept(final SmbFile file) throws SmbException{
        return false;
    }
    
    /**
     * Returns false.
     *
     * @param dir  the directory to check (ignored)
     * @param name  the filename (ignored)
     * @return false
     */
	@Override
	public boolean accept(SmbFile dir, String name) throws SmbException {
		return false;
	}

}
