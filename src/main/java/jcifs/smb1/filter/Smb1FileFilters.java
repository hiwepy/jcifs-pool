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


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileFilter;

public class Smb1FileFilters {

    /**
     * Accepts all SmbFile entries, including null.
     */
    public static final Smb1FileFilter ALL = new AbstractFileFilter() {
    	
    	@Override
        public boolean accept(SmbFile file) throws SmbException {
            return true;
        }
    	
    };

    /**
     * Accepts all non-null SmbFile entries.
     */
    public static final Smb1FileFilter NON_NULL = new AbstractFileFilter() {
        
        @Override
        public boolean accept(SmbFile file) throws SmbException {
        	return file != null;
        }
		 
    };

    /**
     * Accepts all (non-null) SmbFile directory entries.
     */
    public static final Smb1FileFilter DIRECTORIES = new AbstractFileFilter() {
    	
    	@Override
        public boolean accept(SmbFile file) throws SmbException {
			return file != null && file.isDirectory();
        }
    	
    };

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting array is a subset of the original file list that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the array of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
	 * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     *
     */
    public static SmbFile[] filter(final Smb1FileFilter filter, final SmbFile... files) throws SmbException {
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (files == null) {
            return new SmbFile[0];
        }
        final List<SmbFile> acceptedFiles = new ArrayList<SmbFile>();
        for (final SmbFile file : files) {
            if (file == null) {
                throw new IllegalArgumentException("file array contains null");
            }
            if (filter.accept(file)) {
                acceptedFiles.add(file);
            }
        }
        return acceptedFiles.toArray(new SmbFile[acceptedFiles.size()]);
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting array is a subset of the original file list that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the array of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     *
     */
    public static SmbFile[] filter(final Smb1FileFilter filter, final Iterable<SmbFile> files) throws SmbException {
        final List<SmbFile> acceptedFiles = filterList(filter, files);
        return acceptedFiles.toArray(new SmbFile[acceptedFiles.size()]);
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting list is a subset of the original files that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link List} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * List&lt;File&gt; filesAndDirectories = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
     *     FileFilterUtils.directoryFileFilter());
     * </pre>
     * @param filter the filter to apply to each files in the list.
     * @param files the collection of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     */
    public static List<SmbFile> filterList(final Smb1FileFilter filter, final Iterable<SmbFile> files) throws SmbException {
        return filter(filter, files, new ArrayList<SmbFile>());
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting list is a subset of the original files that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link List} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * List&lt;File&gt; filesAndDirectories = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
     *     FileFilterUtils.directoryFileFilter());
     * </pre>
     * @param filter the filter to apply to each files in the list.
     * @param files the collection of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     */
    public static List<SmbFile> filterList(final Smb1FileFilter filter, final SmbFile... files) throws SmbException {
        final SmbFile[] acceptedFiles = filter(filter, files);
        return Arrays.asList(acceptedFiles);
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting set is a subset of the original file list that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the collection of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     *
     */
    public static Set<SmbFile> filterSet(final Smb1FileFilter filter, final SmbFile... files) throws SmbException {
        final SmbFile[] acceptedFiles = filter(filter, files);
        return new HashSet<SmbFile>(Arrays.asList(acceptedFiles));
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects. The resulting set is a subset of the original file list that
     * matches the provided filter.
     * </p>
     *
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     *
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the collection of files to apply the filter to.
     *
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     *
     
     */
    public static Set<SmbFile> filterSet(final Smb1FileFilter filter, final Iterable<SmbFile> files) throws SmbException {
        return filter(filter, files, new HashSet<SmbFile>());
    }

    /**
     * <p>
     * Applies an {@link Smb1FileFilter} to the provided {@link File}
     * objects and appends the accepted files to the other supplied collection.
     * </p>
     *
     * <pre>
     * List&lt;File&gt; files = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(files,
     *     FileFilterUtils.sizeFileFilter(FileUtils.FIFTY_MB),
     *         new ArrayList&lt;File&gt;());
     * </pre>
     * @param filter the filter to apply to the collection of files.
     * @param files the collection of files to apply the filter to.
     * @param acceptedFiles the list of files to add accepted files to.
     *
     * @param <T> the type of the file collection.
     * @return a subset of <code>files</code> that is accepted by the
     *         file filter.
     * @throws SmbException 
     * @throws IllegalArgumentException if the filter is {@code null}
     *         or <code>files</code> contains a {@code null} value.
     */
    private static <T extends Collection<SmbFile>> T filter(final Smb1FileFilter filter,
            final Iterable<SmbFile> files, final T acceptedFiles) throws SmbException {
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (files != null) {
            for (final SmbFile file : files) {
                if (file == null) {
                    throw new IllegalArgumentException("file collection contains null");
                }
                if (filter.accept(file)) {
                    acceptedFiles.add(file);
                }
            }
        }
        return acceptedFiles;
    }

    /**
     * Returns a filter that returns true if the filename starts with the specified text.
     *
     * @param prefix  the filename prefix
     * @return a prefix checking filter
     * @see PrefixFileFilter
     */
    public static Smb1FileFilter prefixFileFilter(final String prefix) {
        return new PrefixFileFilter(prefix);
    }

    /**
     * Returns a filter that returns true if the filename starts with the specified text.
     *
     * @param prefix  the filename prefix
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @return a prefix checking filter
     * @see PrefixFileFilter
     
     */
    public static Smb1FileFilter prefixFileFilter(final String prefix, final IOCase caseSensitivity) {
        return new PrefixFileFilter(prefix, caseSensitivity);
    }

    /**
     * Returns a filter that returns true if the filename ends with the specified text.
     *
     * @param suffix  the filename suffix
     * @return a suffix checking filter
     * @see SuffixFileFilter
     */
    public static Smb1FileFilter suffixFileFilter(final String suffix) {
        return new SuffixFileFilter(suffix);
    }

    /**
     * Returns a filter that returns true if the filename ends with the specified text.
     *
     * @param suffix  the filename suffix
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @return a suffix checking filter
     * @see SuffixFileFilter
     
     */
    public static Smb1FileFilter suffixFileFilter(final String suffix, final IOCase caseSensitivity) {
        return new SuffixFileFilter(suffix, caseSensitivity);
    }

    /**
     * Returns a filter that returns true if the filename matches the specified text.
     *
     * @param name  the filename
     * @return a name checking filter
     * @see NameFileFilter
     */
    public static Smb1FileFilter nameFileFilter(final String name) {
        return new NameFileFilter(name);
    }

    /**
     * Returns a filter that returns true if the filename matches the specified text.
     *
     * @param name  the filename
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @return a name checking filter
     * @see NameFileFilter
     
     */
    public static Smb1FileFilter nameFileFilter(final String name, final IOCase caseSensitivity) {
        return new NameFileFilter(name, caseSensitivity);
    }

    /**
     * Returns a filter that checks if the file is a directory.
     *
     * @return file filter that accepts only directories and not files
     * @see DirectoryFileFilter#DIRECTORY
     */
    public static Smb1FileFilter directoryFileFilter() {
        return Smb1FileFilters.DIRECTORIES;
    }

    /**
     * Returns a filter that checks if the file is a file (and not a directory).
     *
     * @return file filter that accepts only files and not directories
     * @see FileFileFilter#FILE
     */
    public static Smb1FileFilter fileFileFilter() {
        return FileFileFilter.FILE;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a filter that ANDs the specified filters.
     *
     * @param filters the IOSmbFileFilter that will be ANDed together.
     * @return a filter that ANDs the specified filters
     *
     * @throws IllegalArgumentException if the filters are null or contain a
     *         null value.
     * @see AndFileFilter
     
     */
    public static Smb1FileFilter and(final Smb1FileFilter... filters) {
        return new AndFileFilter(toList(filters));
    }

    /**
     * Returns a filter that ORs the specified filters.
     *
     * @param filters the IOSmbFileFilter that will be ORed together.
     * @return a filter that ORs the specified filters
     *
     * @throws IllegalArgumentException if the filters are null or contain a
     *         null value.
     * @see OrFileFilter
     
     */
    public static Smb1FileFilter or(final Smb1FileFilter... filters) {
        return new OrFileFilter(toList(filters));
    }

    /**
     * Create a List of file filters.
     *
     * @param filters The file filters
     * @return The list of file filters
     * @throws IllegalArgumentException if the filters are null or contain a
     *         null value.
     
     */
    public static List<Smb1FileFilter> toList(final Smb1FileFilter... filters) {
        if (filters == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        final List<Smb1FileFilter> list = new ArrayList<Smb1FileFilter>(filters.length);
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] == null) {
                throw new IllegalArgumentException("The filter[" + i + "] is null");
            }
            list.add(filters[i]);
        }
        return list;
    }

    /**
     * Returns a filter that NOTs the specified filter.
     *
     * @param filter  the filter to invert
     * @return a filter that NOTs the specified filter
     * @see NotFileFilter
     */
    public static Smb1FileFilter notFileFilter(final Smb1FileFilter filter) {
        return new NotFileFilter(filter);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a filter that always returns true.
     *
     * @return a true filter
     * @see TrueFileFilter#TRUE
     */
    public static Smb1FileFilter trueFileFilter() {
        return TrueFileFilter.TRUE;
    }

    /**
     * Returns a filter that always returns false.
     *
     * @return a false filter
     * @see FalseFileFilter#FALSE
     */
    public static Smb1FileFilter falseFileFilter() {
        return FalseFileFilter.FALSE;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an <code>IOSmbFileFilter</code> that wraps the
     * <code>FileFilter</code> instance.
     *
     * @param filter  the filter to be wrapped
     * @return a new filter that implements SmbFileFilter
     * @see DelegateFileFilter
     */
    public static Smb1FileFilter asFileFilter(final SmbFileFilter filter) {
        return new DelegateFileFilter(filter);
    }


    //-----------------------------------------------------------------------
    /**
     * Returns a filter that returns true if the file was last modified after
     * the specified cutoff time.
     *
     * @param cutoff  the time threshold
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final long cutoff) {
        return new AgeFileFilter(cutoff);
    }

    /**
     * Returns a filter that filters files based on a cutoff time.
     *
     * @param cutoff  the time threshold
     * @param acceptOlder  if true, older files get accepted, if false, newer
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final long cutoff, final boolean acceptOlder) {
        return new AgeFileFilter(cutoff, acceptOlder);
    }

    /**
     * Returns a filter that returns true if the file was last modified after
     * the specified cutoff date.
     *
     * @param cutoffDate  the time threshold
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final Date cutoffDate) {
        return new AgeFileFilter(cutoffDate);
    }

    /**
     * Returns a filter that filters files based on a cutoff date.
     *
     * @param cutoffDate  the time threshold
     * @param acceptOlder  if true, older files get accepted, if false, newer
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        return new AgeFileFilter(cutoffDate, acceptOlder);
    }

    /**
     * Returns a filter that returns true if the file was last modified after
     * the specified reference file.
     *
     * @param cutoffReference  the file whose last modification
     *        time is usesd as the threshold age of the files
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final File cutoffReference) {
        return new AgeFileFilter(cutoffReference);
    }

    /**
     * Returns a filter that filters files based on a cutoff reference file.
     *
     * @param cutoffReference  the file whose last modification
     *        time is usesd as the threshold age of the files
     * @param acceptOlder  if true, older files get accepted, if false, newer
     * @return an appropriately configured age file filter
     * @see AgeFileFilter
     */
    public static Smb1FileFilter ageFileFilter(final File cutoffReference, final boolean acceptOlder) {
        return new AgeFileFilter(cutoffReference, acceptOlder);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a filter that returns true if the file is bigger than a certain size.
     *
     * @param threshold  the file size threshold
     * @return an appropriately configured SizeFileFilter
     * @see SizeFileFilter
     */
    public static Smb1FileFilter sizeFileFilter(final long threshold) {
        return new SizeFileFilter(threshold);
    }

    /**
     * Returns a filter that filters based on file size.
     *
     * @param threshold  the file size threshold
     * @param acceptLarger  if true, larger files get accepted, if false, smaller
     * @return an appropriately configured SizeFileFilter
     * @see SizeFileFilter
     */
    public static Smb1FileFilter sizeFileFilter(final long threshold, final boolean acceptLarger) {
        return new SizeFileFilter(threshold, acceptLarger);
    }

    /**
     * Returns a filter that accepts files whose size is &gt;= minimum size
     * and &lt;= maximum size.
     *
     * @param minSizeInclusive the minimum file size (inclusive)
     * @param maxSizeInclusive the maximum file size (inclusive)
     * @return an appropriately configured IOSmbFileFilter
     * @see SizeFileFilter
     */
    public static Smb1FileFilter sizeRangeFileFilter(final long minSizeInclusive, final long maxSizeInclusive ) {
        final Smb1FileFilter minimumFilter = new SizeFileFilter(minSizeInclusive, true);
        final Smb1FileFilter maximumFilter = new SizeFileFilter(maxSizeInclusive + 1L, false);
        return new AndFileFilter(minimumFilter, maximumFilter);
    }

    /**
     * Returns a filter that accepts files that begin with the provided magic
     * number.
     *
     * @param magicNumber the magic number (byte sequence) to match at the
     *        beginning of each file.
     *
     * @return an SmbFileFilter that accepts files beginning with the provided
     *         magic number.
     *
     * @throws IllegalArgumentException if <code>magicNumber</code> is
     *         {@code null} or the empty String.
     * @see MagicNumberFileFilter
     
     */
    public static Smb1FileFilter magicNumberFileFilter(final String magicNumber) {
        return new MagicNumberFileFilter(magicNumber);
    }

    /**
     * Returns a filter that accepts files that contains the provided magic
     * number at a specified offset within the file.
     *
     * @param magicNumber the magic number (byte sequence) to match at the
     *        provided offset in each file.
     * @param offset the offset within the files to look for the magic number.
     *
     * @return an SmbFileFilter that accepts files containing the magic number
     *         at the specified offset.
     *
     * @throws IllegalArgumentException if <code>magicNumber</code> is
     *         {@code null} or the empty String, or if offset is a
     *         negative number.
     * @see MagicNumberFileFilter
     
     */
    public static Smb1FileFilter magicNumberFileFilter(final String magicNumber, final long offset) {
        return new MagicNumberFileFilter(magicNumber, offset);
    }

    /**
     * Returns a filter that accepts files that begin with the provided magic
     * number.
     *
     * @param magicNumber the magic number (byte sequence) to match at the
     *        beginning of each file.
     *
     * @return an SmbFileFilter that accepts files beginning with the provided
     *         magic number.
     *
     * @throws IllegalArgumentException if <code>magicNumber</code> is
     *         {@code null} or is of length zero.
     * @see MagicNumberFileFilter
     
     */
    public static Smb1FileFilter magicNumberFileFilter(final byte[] magicNumber) {
        return new MagicNumberFileFilter(magicNumber);
    }

    /**
     * Returns a filter that accepts files that contains the provided magic
     * number at a specified offset within the file.
     *
     * @param magicNumber the magic number (byte sequence) to match at the
     *        provided offset in each file.
     * @param offset the offset within the files to look for the magic number.
     *
     * @return an SmbFileFilter that accepts files containing the magic number
     *         at the specified offset.
     *
     * @throws IllegalArgumentException if <code>magicNumber</code> is
     *         {@code null}, or contains no bytes, or <code>offset</code>
     *         is a negative number.
     * @see MagicNumberFileFilter
     */
    public static Smb1FileFilter magicNumberFileFilter(final byte[] magicNumber, final long offset) {
        return new MagicNumberFileFilter(magicNumber, offset);
    }

    //-----------------------------------------------------------------------
    /* Constructed on demand and then cached */
    private static final Smb1FileFilter cvsFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter("CVS")));

    /* Constructed on demand and then cached */
    private static final Smb1FileFilter svnFilter = notFileFilter( and(directoryFileFilter(), nameFileFilter(".svn")));

    /**
     * Decorates a filter to make it ignore CVS directories.
     * Passing in {@code null} will return a filter that accepts everything
     * except CVS directories.
     *
     * @param filter  the filter to decorate, null means an unrestricted filter
     * @return the decorated filter, never null
     */
    public static Smb1FileFilter makeCVSAware(final Smb1FileFilter filter) {
        if (filter == null) {
            return cvsFilter;
        } else {
            return and(filter, cvsFilter);
        }
    }

    /**
     * Decorates a filter to make it ignore SVN directories.
     * Passing in {@code null} will return a filter that accepts everything
     * except SVN directories.
     *
     * @param filter  the filter to decorate, null means an unrestricted filter
     * @return the decorated filter, never null
     */
    public static Smb1FileFilter makeSVNAware(final Smb1FileFilter filter) {
        if (filter == null) {
            return svnFilter;
        } else {
            return and(filter, svnFilter);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Decorates a filter so that it only applies to directories and not to files.
     *
     * @param filter  the filter to decorate, null means an unrestricted filter
     * @return the decorated filter, never null
     * @see Smb1FileFilters#DIRECTORIES
     */
    public static Smb1FileFilter makeDirectoryOnly(final Smb1FileFilter filter) {
        if (filter == null) {
            return Smb1FileFilters.DIRECTORIES;
        }
        return new AndFileFilter(Smb1FileFilters.DIRECTORIES, filter);
    }

    /**
     * Decorates a filter so that it only applies to files and not to directories.
     *
     * @param filter  the filter to decorate, null means an unrestricted filter
     * @return the decorated filter, never null
     * @see FileFileFilter#FILE
     */
    public static Smb1FileFilter makeFileOnly(final Smb1FileFilter filter) {
        if (filter == null) {
            return FileFileFilter.FILE;
        }
        return new AndFileFilter(FileFileFilter.FILE, filter);
    }
    
}
