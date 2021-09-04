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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@SuppressWarnings("serial")
public class AndFileFilter extends AbstractFileFilter implements ConditionalFileFilter, Serializable {
	
	
	/** The list of file filters. */
	private final List<Smb2FileFilter> fileFilters;
	
	/**
	* Constructs a new instance of <code>AndFileFilter</code>.
	*
	* @since 1.1
	*/
	public AndFileFilter() {
		this.fileFilters = new ArrayList<>();
	}
	
	/**
	* Constructs a new instance of <code>AndFileFilter</code>
	* with the specified list of filters.
	*
	* @param fileFilters  a List of IOSmbFileFilter instances, copied, null ignored
	* @since 1.1
	*/
	public AndFileFilter(final List<Smb2FileFilter> fileFilters) {
		if (fileFilters == null) {
		    this.fileFilters = new ArrayList<>();
		} else {
		    this.fileFilters = new ArrayList<>(fileFilters);
		}
	}
	
	/**
	* Constructs a new file filter that ANDs the result of two other filters.
	*
	* @param filter1  the first filter, must not be null
	* @param filter2  the second filter, must not be null
	* @throws IllegalArgumentException if either filter is null
	*/
	public AndFileFilter(final Smb2FileFilter filter1, final Smb2FileFilter filter2) {
		if (filter1 == null || filter2 == null) {
		    throw new IllegalArgumentException("The filters must not be null");
		}
		this.fileFilters = new ArrayList<>(2);
		addFileFilter(filter1);
		addFileFilter(filter2);
	}
	
	/**
	* {@inheritDoc}
	*/
	public void addFileFilter(final Smb2FileFilter IOSmbFileFilter) {
		this.fileFilters.add(IOSmbFileFilter);
	}
	
	/**
	* {@inheritDoc}
	*/
	public List<Smb2FileFilter> getFileFilters() {
		return Collections.unmodifiableList(this.fileFilters);
	}
	
	/**
	* {@inheritDoc}
	*/
	public boolean removeFileFilter(final Smb2FileFilter IOSmbFileFilter) {
		return this.fileFilters.remove(IOSmbFileFilter);
	}
	
	/**
	* {@inheritDoc}
	*/
	public void setFileFilters(final List<Smb2FileFilter> fileFilters) {
		this.fileFilters.clear();
		this.fileFilters.addAll(fileFilters);
	}
	
   /**
	* {@inheritDoc}
	*/
	@Override
	public boolean accept(final SmbFile file ) throws SmbException {
		if (this.fileFilters.isEmpty()) {
		    return false;
		}
		for (final Smb2FileFilter fileFilter : fileFilters) {
		    if (!fileFilter.accept(file)) {
		        return false;
		    }
		}
		return true;
	}
	
   /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final SmbFile file, final String name) throws SmbException{
        if (this.fileFilters.isEmpty()) {
            return false;
        }
        for (final Smb2FileFilter fileFilter : fileFilters) {
            if (!fileFilter.accept(file, name)) {
                return false;
            }
        }
        return true;
    }
	
	/**
	* Provide a String representaion of this file filter.
	* @return a String representaion
	*/
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(super.toString());
		buffer.append("(");
		if (fileFilters != null) {
		    for (int i = 0; i < fileFilters.size(); i++) {
		        if (i > 0) {
		            buffer.append(",");
		        }
		        final Object filter = fileFilters.get(i);
		        buffer.append(filter == null ? "null" : filter.toString());
		    }
		}
		buffer.append(")");
		return buffer.toString();
	}

}
