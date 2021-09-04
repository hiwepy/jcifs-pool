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
import java.util.List;

import org.apache.commons.io.IOCase;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@SuppressWarnings("serial")
public class NameFileFilter  extends AbstractFileFilter implements Serializable{
	
	/** The filenames to search for */
    private final String[] names;
    /** Whether the comparison is case sensitive. */
    private final IOCase caseSensitivity;

    /**
     * Constructs a new case-sensitive name file filter for a single name.
     *
     * @param name  the name to allow, must not be null
     * @throws IllegalArgumentException if the name is null
     */
    public NameFileFilter(final String name) {
        this(name, null);
    }

    /**
     * Construct a new name file filter specifying case-sensitivity.
     *
     * @param name  the name to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name is null
     */
    public NameFileFilter(final String name, final IOCase caseSensitivity) {
        if (name == null) {
            throw new IllegalArgumentException("The wildcard must not be null");
        }
        this.names = new String[] {name};
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new case-sensitive name file filter for an array of names.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     *
     * @param names  the names to allow, must not be null
     * @throws IllegalArgumentException if the names array is null
     */
    public NameFileFilter(final String[] names) {
        this(names, null);
    }

    /**
     * Constructs a new name file filter for an array of names specifying case-sensitivity.
     *
     * @param names  the names to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the names array is null
     */
    public NameFileFilter(final String[] names, final IOCase caseSensitivity) {
        if (names == null) {
            throw new IllegalArgumentException("The array of names must not be null");
        }
        this.names = new String[names.length];
        System.arraycopy(names, 0, this.names, 0, names.length);
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new case-sensitive name file filter for a list of names.
     *
     * @param names  the names to allow, must not be null
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public NameFileFilter(final List<String> names) {
        this(names, null);
    }

    /**
     * Constructs a new name file filter for a list of names specifying case-sensitivity.
     *
     * @param names  the names to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public NameFileFilter(final List<String> names, final IOCase caseSensitivity) {
        if (names == null) {
            throw new IllegalArgumentException("The list of names must not be null");
        }
        this.names = names.toArray(new String[names.size()]);
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks to see if the filename matches.
     *
     * @param file  the File to check
     * @return true if the filename matches
     */
    @Override
    public boolean accept(final SmbFile file) throws SmbException {
        final String name = file.getName();
        for (final String name2 : this.names) {
            if (caseSensitivity.checkEquals(name, name2)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks to see if the filename matches.
     *
     * @param dir  the File directory (ignored)
     * @param name  the filename
     * @return true if the filename matches
     */
    @Override
    public boolean accept(final SmbFile dir, final String name) throws SmbException {
        for (final String name2 : names) {
            if (caseSensitivity.checkEquals(name, name2)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Provide a String representaion of this file filter.
     *
     * @return a String representaion
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(names[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
    
}