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
package jcifs.utils;


import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb1.filter.FalseFileFilter;
import jcifs.smb1.filter.FileFileFilter;
import jcifs.smb1.filter.Smb1FileFilter;
import jcifs.smb1.filter.Smb1FileFilters;
import jcifs.smb1.filter.SuffixFileFilter;
import jcifs.smb1.filter.TrueFileFilter;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileFilter;

public class Smb1FileUtils {

	protected static Logger LOG = LoggerFactory.getLogger(Smb1FileUtils.class);
	
	//-----------------------------------------------------------------------
    /**
     * Finds files within a given directory (and optionally its
     * subdirectories). All files found are filtered by an SmbFileFilter.
     *
     * @param files                 the collection of files found.
     * @param directory             the directory to search in.
     * @param filter                the filter to apply to files and directories.
     * @param includeSubDirectories indicates if will include the subdirectories themselves
     * @throws IOException 
     */
    private static void innerListFiles(final Collection<SmbFile> files, final SmbFile directory,
                                       final SmbFileFilter filter, final boolean includeSubDirectories) throws IOException {
    	
    	SmbFile[] found = directory.listFiles(filter);
        if (found != null) {
            for (final SmbFile file : found) {
                if (file.isDirectory()) {
                    if (includeSubDirectories) {
                        files.add(file);
                    }
                    innerListFiles(files, file, filter, includeSubDirectories);
                } else {
                    files.add(file);
                }
            }
        }
    }
    
	//-----------------------------------------------------------------------
	
    /**
     * Converts an array of file extensions to suffixes for use
     * with SmbFileFilters.
     *
     * @param extensions an array of extensions. Format: {"java", "xml"}
     * @return an array of suffixes. Format: {".java", ".xml"}
     */
    public static String[] toSuffixes(final String[] extensions) {
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }
    
    public static Collection<SmbFile> listFiles(final SmbFile[] files, final String[] extensions) throws IOException {
        //Find files
        final Collection<SmbFile> file_collections = new java.util.LinkedList<SmbFile>();
        if(files != null && files.length > 0){
        	Smb1FileFilter filter;
            if (extensions == null) {
                filter = TrueFileFilter.INSTANCE;
            } else {
                final String[] suffixes = toSuffixes(extensions);
                filter = new SuffixFileFilter(suffixes);
            }
            filter = Smb1FileFilters.and(filter, FileFileFilter.FILE);
			//????????????
			for(SmbFile SmbFile :files){
				if(filter.accept(SmbFile)){
					file_collections.add(SmbFile);
				}
			}
		}
        return file_collections;
    }
    
    public static Collection<SmbFile> listFiles(final SmbFile[] files,  final Smb1FileFilter fileFilter) throws IOException {
        //Find files
        final Collection<SmbFile> file_collections = new java.util.LinkedList<SmbFile>();
        if(files != null && files.length > 0){
        	SmbFileFilter filter;
        	if (fileFilter == null) {
                filter = TrueFileFilter.INSTANCE;
            } else {
                filter = fileFilter;
            }
			//????????????
			for(SmbFile SmbFile :files){
				if(filter.accept(SmbFile)){
					file_collections.add(SmbFile);
				}
			}
		}
        return file_collections;
    }
    
    public static Collection<SmbFile> listFiles(final SmbFile directory, final String[] extensions, final boolean recursive) throws IOException {
    	Smb1FileFilter filter;
        if (extensions == null) {
            filter = TrueFileFilter.INSTANCE;
        } else {
            final String[] suffixes = toSuffixes(extensions);
            filter = new SuffixFileFilter(suffixes);
        }
        return listFiles( directory, filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
    }
    
    public static Collection<SmbFile> listFiles( final SmbFile directory, final Smb1FileFilter fileFilter, final boolean recursive) throws IOException {
    	Smb1FileFilter filter;
        if (fileFilter == null) {
            filter = TrueFileFilter.INSTANCE;
        } else {
            filter = fileFilter;
        }
    	return listFiles(directory, filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
    }
    	
    public static Collection<SmbFile> listFiles( final SmbFile directory, final Smb1FileFilter fileFilter, final Smb1FileFilter dirFilter) throws IOException {
        
    	validateListFilesParameters(directory, fileFilter);

        final Smb1FileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        final Smb1FileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);

        //Find files
        final Collection<SmbFile> files = new java.util.LinkedList<SmbFile>();
        innerListFiles(files, directory, Smb1FileFilters.or(effFileFilter, effDirFilter), false);
        return files;
    }
    
    /**
     * Validates the given arguments.
     * <ul>
     * <li>Throws {@link IllegalArgumentException} if {@code directory} is not a directory</li>
     * <li>Throws {@link NullPointerException} if {@code fileFilter} is null</li>
     * </ul>
     *
     * @param directory  The File to test
     * @param fileFilter The SmbFileFilter to test
     * @throws SmbException 
     */
    private static void validateListFilesParameters(final SmbFile directory, final Smb1FileFilter fileFilter) throws SmbException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter 'directory' is not a directory: " + directory);
        }
        if (fileFilter == null) {
            throw new NullPointerException("Parameter 'fileFilter' is null");
        }
    }
    

    /**
     * Returns a filter that accepts files in addition to the {@link File} objects accepted by the given filter.
     *
     * @param fileFilter a base filter to add to
     * @return a filter that accepts files
     */
    private static Smb1FileFilter setUpEffectiveFileFilter(final Smb1FileFilter fileFilter) {
        return Smb1FileFilters.and(fileFilter, Smb1FileFilters.notFileFilter(Smb1FileFilters.DIRECTORIES));
    }

    /**
     * Returns a filter that accepts directories in addition to the {@link File} objects accepted by the given filter.
     *
     * @param dirFilter a base filter to add to
     * @return a filter that accepts directories
     */
    private static Smb1FileFilter setUpEffectiveDirFilter(final Smb1FileFilter dirFilter) {
        return dirFilter == null ? FalseFileFilter.INSTANCE : Smb1FileFilters.and(dirFilter, Smb1FileFilters.DIRECTORIES);
    }
    
    /**
     * Tests if the specified <code>File</code> is newer than the specified
     * time reference.
     *
     * @param file       the <code>File</code> of which the modification date must
     *                   be compared, must not be {@code null}
     * @param timeMillis the time reference measured in milliseconds since the
     *                   epoch (00:00:00 GMT, January 1, 1970)
     * @return true if the <code>File</code> exists and has been modified after
     * the given time reference.
     * @throws IllegalArgumentException if the file is {@code null}
     */
    public static boolean isFileNewer(final SmbFile file, final long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        return file.getLastModified() > timeMillis;
    }

    
}
