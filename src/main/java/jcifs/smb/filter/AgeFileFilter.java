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

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import jcifs.smb.SmbFile;
import jcifs.utils.SmbFileUtils;


@SuppressWarnings("serial")
public class AgeFileFilter extends AbstractFileFilter implements Serializable {

    /** The cutoff time threshold. */
    private final long cutoff;
    /** Whether the files accepted will be older or newer. */
    private final boolean acceptOlder;

    /**
     * Constructs a new age file filter for files equal to or older than
     * a certain cutoff
     *
     * @param cutoff  the threshold age of the files
     */
    public AgeFileFilter(final long cutoff) {
        this(cutoff, true);
    }

    /**
     * Constructs a new age file filter for files on any one side
     * of a certain cutoff.
     *
     * @param cutoff  the threshold age of the files
     * @param acceptOlder  if true, older files (at or before the cutoff)
     * are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final long cutoff, final boolean acceptOlder) {
        this.acceptOlder = acceptOlder;
        this.cutoff = cutoff;
    }

    /**
     * Constructs a new age file filter for files older than (at or before)
     * a certain cutoff date.
     *
     * @param cutoffDate  the threshold age of the files
     */
    public AgeFileFilter(final Date cutoffDate) {
        this(cutoffDate, true);
    }

    /**
     * Constructs a new age file filter for files on any one side
     * of a certain cutoff date.
     *
     * @param cutoffDate  the threshold age of the files
     * @param acceptOlder  if true, older files (at or before the cutoff)
     * are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        this(cutoffDate.getTime(), acceptOlder);
    }

    /**
     * Constructs a new age file filter for files older than (at or before)
     * a certain File (whose last modification time will be used as reference).
     *
     * @param cutoffReference  the file whose last modification
     *        time is usesd as the threshold age of the files
     */
    public AgeFileFilter(final File cutoffReference) {
        this(cutoffReference, true);
    }

    /**
     * Constructs a new age file filter for files on any one side
     * of a certain File (whose last modification time will be used as
     * reference).
     *
     * @param cutoffReference  the file whose last modification
     *        time is usesd as the threshold age of the files
     * @param acceptOlder  if true, older files (at or before the cutoff)
     * are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final File cutoffReference, final boolean acceptOlder) {
        this(cutoffReference.lastModified(), acceptOlder);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks to see if the last modification of the file matches cutoff
     * favorably.
     * <p>
     * If last modification time equals cutoff and newer files are required,
     * file <b>IS NOT</b> selected.
     * If last modification time equals cutoff and older files are required,
     * file <b>IS</b> selected.
     *
     * @param file  the File to check
     * @return true if the filename matches
     */
    @Override
    public boolean accept(final SmbFile file) {
        final boolean newer = SmbFileUtils.isFileNewer(file, cutoff);
        return acceptOlder ? !newer : newer;
    }

    /**
     * Provide a String representaion of this file filter.
     *
     * @return a String representaion
     */
    @Override
    public String toString() {
        final String condition = acceptOlder ? "<=" : ">";
        return super.toString() + "(" + condition + cutoff + ")";
    }
	
}
