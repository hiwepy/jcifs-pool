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
package jcifs.smb1.filter;

import java.io.Serializable;

import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;

@SuppressWarnings("serial")
public class NotFileFilter extends AbstractFileFilter implements Serializable {

    /** The filter */
    private final Smb1FileFilter filter;

    /**
     * Constructs a new file filter that NOTs the result of another filter.
     *
     * @param filter  the filter, must not be null
     * @throws IllegalArgumentException if the filter is null
     */
    public NotFileFilter(final Smb1FileFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The filter must not be null");
        }
        this.filter = filter;
    }

    /**
     * Returns the logical NOT of the underlying filter's return value for the same File.
     *
     * @param file  the File to check
     * @return true if the filter returns false
     */
    @Override
    public boolean accept(final SmbFile file ) throws SmbException {
        return ! filter.accept(file);
    }

    /**
     * Provide a String representaion of this file filter.
     *
     * @return a String representaion
     */
    @Override
    public String toString() {
        return super.toString() + "(" + filter.toString()  + ")";
    }
}
