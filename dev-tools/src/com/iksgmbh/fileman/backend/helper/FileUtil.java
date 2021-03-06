
/*
* Copyright 2018 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
* 
 * Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
 *     http://www.apache.org/licenses/LICENSE-2.0
* 
 * Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.iksgmbh.fileman.backend.helper;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utils to handle file operations in SysNat
 * 
 * TODO: Introduce bundle to support german error messages
 * 
 * @author Reik Oberrath
 */
public class FileUtil
{	
	public static final String ROOT_PATH_PLACEHOLDER = "<root.path>";
	
	@SuppressWarnings("unused")
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");

	static int MAX_SECONDS_TO_WAIT_FOR_DOWNLOAD_TO_START = 60;

	private static File downloadDir = null;
	private static File firefoxExecutable;

	public static void copyTextFileToTargetDir(String sourceDir,
	                                           String sourceFilename,
	                                           String targetDir)
	{
		sourceDir = FileUtil.findAbsoluteFilePath(sourceDir);
		targetDir = FileUtil.findAbsoluteFilePath(targetDir);
		String content = FileUtil.readTextFileToString(sourceDir + "/" + sourceFilename);
		FileUtil.writeFile(new File(targetDir, sourceFilename), content);
	}

	public static File writeFile(String fileName,
			                     String fileContent)
	{
		try {
			fileName = findAbsoluteFilePath(fileName);
			writeFile(new FileOutputStream(fileName), fileContent, fileName);
			return new File(fileName);
		} catch (Exception e) {
			String message = "Could not write file " + new File(fileName).getAbsolutePath();
			System.err.println(message);
			e.printStackTrace();
			throw new RuntimeException(message);
		}
	}

	public static void writeFile(final File file, 
			                     final List<String> fileContent)
	{
		StringBuffer sb = new StringBuffer();
		fileContent.forEach(line -> sb.append(line).append(System.getProperty("line.separator")));
		writeFile(file, sb.toString().trim());
	}

	public static void writeFile(final File file, 
			                     final String fileContent)
	{
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
			if (!file.getParentFile().exists()) {
				throw new RuntimeException("Could not create directory " + file.getParentFile().getAbsolutePath());
			}
		}

		try {
			writeFile(new FileOutputStream(file), fileContent, file.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Could not write file " + file.getAbsolutePath());
		}
	}

	public static void writeFile(final FileOutputStream fileOutputStream,
	                             final String fileContent,
	                             final String fileName)
	{
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
			try {
				writer.write(fileContent);
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Datei konnte nicht gespeichert werden: " + fileName);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
  
	/**
	 * Searches for files of a certain extension in a given directory (not recursively!).
	 * 
	 * @param aFileExtension
	 * @param directory
	 * @return list of matching files
	 */
	public static FileList findFilesIn(final String aFileExtension, 
			                           final File directory)
	{
		final String fileExtension = aFileExtension.toLowerCase();
		final FileList toReturn = new FileList();
		final File[] listFiles = directory.listFiles();
		
		for (File file : listFiles) {
			if (file.getAbsolutePath().toLowerCase().endsWith(fileExtension) && file.isFile()) {
				toReturn.add(file);
			}
		}

		return toReturn;
	}
	
	public static File findFileRecursively(final String aFileName, final File directory)
	{
		List<File> result = FileFinder.findFiles(directory, null, null, null, null, aFileName);
		
		if (result.size() == 0) {
			throw new RuntimeException("No file <b>" + aFileName + "</b> found in <b>" 
		                                       + directory.getAbsolutePath() + "</b>!");
		}
		
		if (result.size() > 1) 
		{
			Optional<File> exactMatch = result.stream().filter(file -> file.getName().equals(aFileName)).findFirst();
			if (exactMatch.isPresent()) {
				return exactMatch.get();
			}
			
			throw new RuntimeException("Ambiguous data file <b>" + aFileName + "</b>!");
		}
		
		return result.get(0);
	}	

	public static FileList findDownloadFiles(String... fileExtensions)
	{
		final File downloadDir = getDownloadDir();
		final FileList toReturn = new FileList();
		final File[] listFiles = downloadDir.listFiles();

		for (File file : listFiles) 
		{
			for (String extension : fileExtensions) 
			{
				extension = extension.toLowerCase();
				if (file.getAbsolutePath().toLowerCase().endsWith(extension) && file.isFile()) {
					toReturn.add(file);
				}
			}
		}

		return toReturn;
	}

	public static File getDownloadDir()
	{
		if (downloadDir == null) {
			downloadDir = new File(System.getProperty("user.home") + "/Downloads");
			if (!downloadDir.exists()) {
				throw new RuntimeException("Download-Verzeichnis nicht verfügbar.");
			}
		}
		return downloadDir;
	}

	/**
	 * Finds most recent download file not older than <timespanOfZhePastInMillisToConsider> milliseconds.
	 * @param timespanOfThePastInMillisToConsider to consider into the past since method call
	 * @return file found - otherwise an exception is thrown
	 */
	public static File findRecentDownloadFile(final long timespanOfThePastInMillisToConsider)
	{
		final long timeLimit = new Date().getTime() - timespanOfThePastInMillisToConsider;

		FileList currentFileList;
		List<File> recentFiles = new ArrayList<>();
		boolean downloadIsProgress = true;
		int millisAlreadyWaitedForDownloadToStart = 0;
		int millisToWaitForEachAttempt = 100;
		
		while (downloadIsProgress) 
		{
			currentFileList = FileUtil.findDownloadFiles(".pdf", ".part");
			recentFiles = extractRecentFiles(currentFileList, timeLimit);
			if ( recentFiles == null || recentFiles.isEmpty()) 
			{
				sleep(millisToWaitForEachAttempt);
				millisAlreadyWaitedForDownloadToStart += millisToWaitForEachAttempt;
				if (millisAlreadyWaitedForDownloadToStart/1000 > MAX_SECONDS_TO_WAIT_FOR_DOWNLOAD_TO_START) {
					throw new RuntimeException("Problem: Download stopped to continue.");
				}
			} else {
				downloadIsProgress = isDownloadInProgress(recentFiles);
			}
		}
		
		if (recentFiles == null || recentFiles.isEmpty()) {
			throw new RuntimeException("Problem: No recently downloaded file found.");
		}

		return getMostRecentFile(recentFiles);
	}

	private static void sleep(int millis)
	{
		try { Thread.sleep(millis);} 
		catch (InterruptedException e) {e.printStackTrace();}
	}

	private static File getMostRecentFile(List<File> recentFiles)
	{
		File toReturn = recentFiles.get(0);
		long maxValue = getLastModificationTimeAsMillis(toReturn);

		for (File file : recentFiles) {
			long millisToCompare = getLastModificationTimeAsMillis(file);
			if (millisToCompare > maxValue) {
				toReturn = file;
				maxValue = millisToCompare;
			}
		}

		return toReturn;
	}

	public static List<File> extractRecentFiles(final FileList fileList, 
                                                final long timeLimit)
	{
		if (fileList == null || fileList.size() == 0) {
			return null;
		}

		final List<File> toReturn = new ArrayList<>();
		
		for (File file : fileList.getFiles()) {
			long lastModificationTimeInMillis = getLastModificationTimeAsMillis(file);
			//System.out.println("Limit: " + TIME_FORMAT.format(new Date(timeLimit)));
			//System.out.println(file.getName() + ": " + TIME_FORMAT.format(new Date(lastModificationTimeInMillis)));
			if (lastModificationTimeInMillis > timeLimit) {
				toReturn.add(file);
			}
		}

		return toReturn;
	}

	private static boolean isDownloadInProgress(final List<File> recentFiles)
	{
		boolean inProgress = recentFiles.stream().filter(file -> file.getName().endsWith(".part")).findFirst().isPresent();
		if ( ! inProgress) {
			sleep(100); // give system time to really complete download
		}
		return inProgress;
	}

	public static long getLastModificationTimeAsMillis(File f)
	{
		try {
			Path path = Paths.get(f.getCanonicalPath());
			BasicFileAttributes attr;
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			return attr.lastModifiedTime().toMillis();
		} 
		catch (IOException e) 
		{
			System.err.println("Warning: Last modification date of '" + f.getAbsolutePath() + "' could not be determined.");
			return 0;
		}
	}

	public static void open(final File file)
	{
		if (!file.exists()) {
			throw new RuntimeException("Das angegebene Datei " + file.getAbsolutePath() + " existiert nicht mehr.");
		}
		if (!file.getName().endsWith("pdf")) {
			throw new RuntimeException(
			        "Das Öffnen der angegebene Datei " + file.getAbsolutePath() + " wird nicht unterstützt.");
		}
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Das Öffnen der Datei " + file.getAbsolutePath() + " schlug fehl.");
		}
	}

	public static void waitUntilNumberOfPdfFilesIs(int expectedFileNumber)
	{
		List<File> files;
		boolean waitAgain = true;
		boolean partFileFound = false;

		while (waitAgain) {
			files = findDownloadFiles("pdf").getFiles();
			for (File file : files) {
				if (file.getName().endsWith("part")) {
					partFileFound = true;
				}
			}

			if (partFileFound) {
				try {
					Thread.sleep(100); // wait until part file is gone
				} catch (InterruptedException e) {
					e.printStackTrace();
					waitAgain = false;
				}
			} else {
				waitAgain = files.size() != expectedFileNumber;
			}
		}
	}

	public static void appendTo(File csvfile, String line)
	{
		line += System.getProperty("line.separator");
		Path path = Paths.get(csvfile.getAbsolutePath());
		try {
			Files.write(path, line.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static List<String> readTextFile(String pathAndfilename)
	{
		if (!new File(pathAndfilename).exists()) {
			throw new RuntimeException("Cannot read non-existing file '" + pathAndfilename + "'.");
		}

		final List<String> fileContent = new ArrayList<String>();

		try (Stream<String> stream = Files.lines(Paths.get(pathAndfilename.trim()))) {
			stream.forEach(line -> fileContent.add(line));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileContent;
	}

	public static void loadPropertyFile(String filename, Properties properties)
	{
		loadPropertyFile(new File(filename), properties);
	}

	public static void loadPropertyFile(File f, Properties properties)
	{
		List<String> lines;
		try {
			lines = readTextFile(f);
		} catch (Exception e) {
			String message = "Error loading file <b>" + f.getAbsolutePath() + "</b>.";
			System.err.println(message);
			throw new RuntimeException(message);
		}

		lines.forEach(line -> toProperty(line, properties, f));
	}

	private static void toProperty(String line, Properties properties, File inputFile)
	{
		line = line.trim();
		if (line.isEmpty() || line.startsWith("#")) {
			return;
		}

		int pos = line.indexOf('=');
		if (pos == -1) {
			throw new RuntimeException("Line <b>" + line + "</b> in der Datei <b>" + inputFile.getAbsolutePath()
			        + " represents no valid property line.");
		}

		String key = line.substring(0, pos).trim();
		String value = line.substring(pos + 1).trim();
		properties.setProperty(key, value);
	}

	public static int getNumberOfFilesStartingWith(String fileNamePrefix, String searchDir)
	{
		int counter = 0;
		final String[] listFiles = new File(searchDir).list();
		for (String fileName : listFiles) {
			if (fileName.startsWith(fileNamePrefix)) {
				counter++;
			}
		}

		return counter;
	}

	public static List<String> readTextFile(final File file)
	{
		return readTextFile(file.getAbsolutePath());
	}

	public static String readTextFileToString(String pathAndFilename)
	{
		if ( ! isAbsolutePath(pathAndFilename)) {
			pathAndFilename = findAbsoluteFilePath(pathAndFilename);
		}
		return readTextFileToString(new File(pathAndFilename));
	}

	public static String readTextFileToString(final File file)
	{
		final List<String> lines = readTextFile(file.getAbsolutePath());
		final StringBuffer sb = new StringBuffer();
		lines.forEach(l -> sb.append(l).append(System.getProperty("line.separator")));
		return sb.toString().trim();
	}

	public static List<File> findFilesEndingWith(final String fileEnding, final File dirToSearch)
	{
		return FileFinder.searchFilesRecursively(dirToSearch, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(fileEnding);
			}
		});
	}

	/**
	 * @param dir to remove with all files and subdirs
	 * @return true if successful
	 */
	public static boolean deleteFolder(String dir)
	{
		dir = FileUtil.findAbsoluteFilePath(dir);
		return deleteFolder(new File(dir));
	}

	/**
	 * @param dir to remove with all files and subdirs
	 * @return true if successful
	 */
	public static boolean deleteFolder(File dir)
	{
		if (dir.exists() && dir.isDirectory()) {
			final List<File> children = Arrays.asList(dir.listFiles());
			children.stream().filter(file -> file.isFile()).forEach(file -> file.delete());
			children.stream().filter(file -> file.isDirectory()).forEach(FileUtil::deleteFolder);
			return dir.delete();
		} else {
			if (!dir.exists()) {
				System.out.println("Nothing to delete. Directory '" + dir.getAbsoluteFile() + "' does not exist.");
			} else {
				System.err.println("Directory to delete '" + dir.getAbsoluteFile() + "' is no directory!");
			}
		}
		return false;
	}

	public static void copyFolder(File sourceDir, File targetDir)
	{
		final String sourceFolderName = sourceDir.getName();
		final String targetFolderName = targetDir.getName();
		final int lengthOfRootPath = buildCanonicalPath(sourceDir).length();
		final List<File> result = FileFinder.findFiles(sourceDir, null, null, null, null, null);

		if (!sourceFolderName.equals(targetFolderName)) {
			targetDir = new File(targetDir, sourceFolderName);
		}

		for (File file : result) {
			String relativePath = buildCanonicalPath(file.getParentFile()).substring(lengthOfRootPath);
			File targetFile = new File(targetDir, relativePath + "/" + file.getName());
			targetFile.getParentFile().mkdirs();
			copyBinaryFile(file, targetFile);
		}
	}

	private static String buildCanonicalPath(File f)
	{
		try {
			return f.getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException("Error building canoical path for " + f.getAbsolutePath());
		}
	}

	public static File copyFileToTargetDir(final File sourceFile, 
                                           final String targetDir)
	{
		final File targetFile = new File(targetDir, sourceFile.getName());
		return copyBinaryFile(sourceFile, targetFile);		
	}
	
	public static File copyFileToTargetDir(final File sourceFile, 
			                               final File targetDir)
	{
		final File targetFile = new File(targetDir, sourceFile.getName());
		return copyBinaryFile(sourceFile, targetFile);		
	}

	public static File copyFileToTargetDir(final String sourceFileAsString, final String targetDirAsString)
	{
		final File targetDir = new File(targetDirAsString);
		if (!targetDir.exists()) {
			throw new RuntimeException("Target dir does not exist: " + targetDir.getAbsolutePath());
		}
		if (!targetDir.isDirectory()) {
			throw new RuntimeException("Target dir is no directory: " + targetDir.getAbsolutePath());
		}
		final File sourceFile = new File(sourceFileAsString);
		if (!sourceFile.exists()) {
			throw new RuntimeException("Source file does not exist: " + sourceFile.getAbsolutePath());
		}

		return copyFileToTargetDir(sourceFile, targetDir);
	}

	public static void copyBinaryFile(String fromFileName, String toFileName)
	{
		fromFileName = FileUtil.findAbsoluteFilePath(fromFileName);
		toFileName = FileUtil.findAbsoluteFilePath(toFileName);

		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);
		copyBinaryFile(fromFile, toFile);
	}

	/**
	 * @param fromFile
	 * @param toFileName name must represent a file not a directory!
	 * @return created or overwritten target file
	 */
	public static File copyBinaryFile(final File fromFile, final String toFileName)
	{
		final File toFileOrDirectory = new File(toFileName);
		return copyBinaryFile(fromFile, toFileOrDirectory);
	}

	public static File copyBinaryFile(String fromFileName, File toFileOrDirectory)
	{
		fromFileName = FileUtil.findAbsoluteFilePath(fromFileName);
		File fromFile = new File(fromFileName);
		return copyBinaryFile(fromFile, toFileOrDirectory);
	}

	/**
	 * Uses streams to perform copy
	 * 
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 * @return created file
	 */
	public static File copyBinaryFile(final File fromFile, File toFileOrDirectory)
	{
		if (!fromFile.exists())
			throw new RuntimeException("FileCopy: " + "no such source file: " + fromFile.getAbsolutePath());
		if (!fromFile.isFile())
			throw new RuntimeException("FileCopy: " + "can't copy directory: " + fromFile.getAbsolutePath());
		if (!fromFile.canRead()) {
			throw new RuntimeException("FileCopy: " + "source file is unreadable: " + fromFile.getAbsolutePath());
		}
		File toFile = toFileOrDirectory;
		if (toFileOrDirectory.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if ( ! toFile.getParentFile().exists() ) {
			toFile.getParentFile().mkdirs();
		}
		
		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new RuntimeException(
				        "FileCopy: " + "destination file is unwriteable: " + toFile.getAbsolutePath());
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new RuntimeException("FileCopy: " + "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new RuntimeException("FileCopy: " + "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new RuntimeException("FileCopy: " + "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return toFile;
	}

	public static File createFolder(final String toCreate)
	{
		final File folder = new File(toCreate);
		boolean ok = folder.mkdirs();
		if (!ok) {
			throw new RuntimeException("Error creating report directory '" + toCreate + "'.");
		}
		return folder;

	}

	public static void createZipFile(File directoryToZip, File targetZipFile)
	{
		List<File> filesToZip = FileFinder.findFiles(directoryToZip, null, null, null, ".zip", null);
		String rootZipDir = directoryToZip.getName();

		byte[] buffer = new byte[1024];
		try (FileOutputStream fos = new FileOutputStream(targetZipFile);
		        ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (File file : filesToZip) {
				String filename = file.getAbsolutePath();
				int pos = filename.indexOf(rootZipDir);
				String zipEntryPath = filename.substring(pos + 1 + rootZipDir.length());
				ZipEntry ze = new ZipEntry(zipEntryPath);
				zos.putNextEntry(ze);
				try (FileInputStream in = new FileInputStream(file);) {
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				}
			}
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex.getMessage());
		} catch (Exception ex) {
			throw new RuntimeException("Error zipping directory: " + directoryToZip.getAbsolutePath());
		}
	}

	public static File getFirefoxExecutable()
	{
		if (firefoxExecutable == null) {
			String pathToFirefoxExecutable = System.getProperty("absolute.path.to.browser.executable");
			if (pathToFirefoxExecutable != null) 
			{
				firefoxExecutable = new File(pathToFirefoxExecutable);
			} 
			else 
			{
				String firefoxExecutableName = System.getProperty("absolute.path.to.browser.executable");
				if (firefoxExecutableName == null) {
					throw new RuntimeException("No Firefox Executable defined!");
				}
				String firefoxDir = System.getProperty("relative.path.to.webdrivers");
				if (firefoxDir.contains(ROOT_PATH_PLACEHOLDER)) {
					firefoxDir = firefoxDir.replace(ROOT_PATH_PLACEHOLDER, System.getProperty("root.path"));
				} else {
					firefoxDir = System.getProperty("user.dir") + '/' + firefoxDir;
				}
				firefoxExecutable = new File(firefoxDir, "firefox.exe");
			}
		}

		return firefoxExecutable;
	}

	public static boolean deleteFile(String filename) {
		return deleteFile(new File(filename));		
	}

	public static boolean deleteFile(File file) {
		return file.delete();		
	}

	public static String getRootDir()
	{
		String toReturn = new File("").getAbsolutePath();
		if (toReturn.endsWith("sources"))
		{
			int pos = toReturn.lastIndexOf("sources");
			toReturn = toReturn.substring(0, pos-1);
		}
		return toReturn;
	}

	public static String findAbsoluteFilePath(String relativeFilepath)
	{
		if (isAbsolutePath(relativeFilepath)) {
			return relativeFilepath;
		}
		
		File file = new File(relativeFilepath); 
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		
		if (relativeFilepath.startsWith("sources")) 
		{
			int pos = relativeFilepath.indexOf("/");
			String filepath = ".." + relativeFilepath.substring(pos, relativeFilepath.length());

			file = new File(filepath);
			if (file.exists()) {
				return file.getAbsolutePath();
			}		
		}

		String toReturn = getRootDir();
		if ( toReturn.endsWith("IntelliJ\\_SysNat")) {
			if (relativeFilepath.startsWith("..")) {
				relativeFilepath = "sources" + relativeFilepath.substring(2);
			}
			toReturn = toReturn + "\\..\\..\\" + relativeFilepath;
		} else {
			toReturn = toReturn + "\\" + relativeFilepath;
		}

		return toReturn;
	}

	public static boolean isAbsolutePath(String path)
	{
		if (path.startsWith("..")) return false;
		if (path.startsWith("sources")) return false;
		if (path.startsWith("/")) return false;
		if (path.startsWith("\\")) return false;
		return true;
	}


}