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
import java.util.List;

import org.apache.commons.io.IOCase;

import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;

@SuppressWarnings("serial")
public class PrefixFileFilter extends AbstractFileFilter implements Serializable {

	/** The filename prefixes to search for */
    private final String[] prefixes;

    /** Whether the comparison is case sensitive. */
    private final IOCase caseSensitivity;

    /**
     * Constructs a new Prefix file filter for a single prefix.
     *
     * @param prefix  the prefix to allow, must not be null
     * @throws IllegalArgumentException if the prefix is null
     */
    public PrefixFileFilter(final String prefix) {
        this(prefix, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new Prefix file filter for a single prefix
     * specifying case-sensitivity.
     *
     * @param prefix  the prefix to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the prefix is null
     * @since 1.4
     */
    public PrefixFileFilter(final String prefix, final IOCase caseSensitivity) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        this.prefixes = new String[] {prefix};
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Prefix file filter for any of an array of prefixes.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     *
     * @param prefixes  the prefixes to allow, must not be null
     * @throws IllegalArgumentException if the prefix array is null
     */
    public PrefixFileFilter(final String[] prefixes) {
        this(prefixes, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new Prefix file filter for any of an array of prefixes
     * specifying case-sensitivity.
     *
     * @param prefixes  the prefixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the prefix is null
     * @since 1.4
     */
    public PrefixFileFilter(final String[] prefixes, final IOCase caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The array of prefixes must not be null");
        }
        this.prefixes = new String[prefixes.length];
        System.arraycopy(prefixes, 0, this.prefixes, 0, prefixes.length);
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Prefix file filter for a list of prefixes.
     *
     * @param prefixes  the prefixes to allow, must not be null
     * @throws IllegalArgumentException if the prefix list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public PrefixFileFilter(final List<String> prefixes) {
        this(prefixes, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new Prefix file filter for a list of prefixes
     * specifying case-sensitivity.
     *
     * @param prefixes  the prefixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the prefix list is null
     * @throws ClassCastException if the list does not contain Strings
     * @since 1.4
     */
    public PrefixFileFilter(final List<String> prefixes, final IOCase caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The list of prefixes must not be null");
        }
        this.prefixes = prefixes.toArray(new String[prefixes.size()]);
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Checks to see if the filename starts with the prefix.
     *
     * @param file  the File to check
     * @return true if the filename starts with one of our prefixes
     */
    @Override
    public boolean accept(final SmbFile file ) throws SmbException {
        final String name = file.getName();
        for (final String prefix : this.prefixes) {
            if (caseSensitivity.checkStartsWith(name, prefix)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks to see if the filename starts with the prefix.
     *
     * @param file  the File directory
     * @param name  the filename
     * @return true if the filename starts with one of our prefixes
     */
    @Override
    public boolean accept(final SmbFile file, final String name) throws SmbException {
        for (final String prefix : prefixes) {
            if (caseSensitivity.checkStartsWith(name, prefix)) {
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
        if (prefixes != null) {
            for (int i = 0; i < prefixes.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(prefixes[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
	
}