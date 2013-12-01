package org.trackerd.platform.clouddocker.builder.tomcat;

import java.io.*;
import java.nio.charset.*;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.tar.*;
import org.trackerd.platform.clouddocker.docker.*;

public class TomcatBuilder
{
	public static String buildAndRun(TomcatBuilderConfig builderConfig) throws Exception
	{
		String tempDirPath= System.getProperty("java.io.tmpdir")+"builder-tomcat-"+System.nanoTime();
		System.out.println("Created temp dir: "+ tempDirPath);
		File tempDir= new File(tempDirPath);
		tempDir.mkdir();

		File indexFile= new File(org.trackerd.platform.clouddocker.builder.tomcat.TomcatBuilder.class.getResource("index.html").getFile());
		copy(indexFile, new File(tempDirPath+"/index.html"));

		File tomcatUsersFile= new File(org.trackerd.platform.clouddocker.builder.tomcat.TomcatBuilder.class.getResource("tomcat-users.xml").getFile());
		copyAndAdjustTomcatUsersFile(tomcatUsersFile, tempDirPath, builderConfig);

		createDockerFile(tempDirPath, builderConfig);

		File tarFile= createTarFile(tempDirPath);

		String tagName= "tomcat/"+System.currentTimeMillis();

		DockerWrapper.build(tarFile, tagName);
		DockerWrapper.run(tagName, builderConfig.port);

		cleanupTempDir(tempDir);

		return "id";
	}

	private static File createTarFile(String tempDirPath) throws Exception
	{
		File tarFile= new File(tempDirPath+"/docker.tar");
		OutputStream tarOutput= new FileOutputStream(tarFile);

		ArchiveOutputStream archiveOutput= new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, tarOutput);

		archiveFile(tempDirPath, "Dockerfile", archiveOutput);
		archiveFile(tempDirPath, "index.html", archiveOutput);
		archiveFile(tempDirPath, "tomcat-users.xml", archiveOutput);

		archiveOutput.finish(); 
        tarOutput.close();
        
		return tarFile;
	}

	private static void archiveFile(String tempDirPath, String filename, ArchiveOutputStream archiveOutput) throws IOException
	{
		File originalFile= new File(tempDirPath+'/'+filename);
		File tar_input_file= new File(filename);
        TarArchiveEntry tar_file= new TarArchiveEntry(tar_input_file);
        tar_file.setSize(originalFile.length());
        archiveOutput.putArchiveEntry(tar_file);
        copy(originalFile, archiveOutput);
        archiveOutput.closeArchiveEntry();
	}

	private static void createDockerFile(String tempDirPath, TomcatBuilderConfig builderConfig) throws IOException
	{
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File(tempDirPath+"/Dockerfile")));

		writer.write("FROM marcel/tomcat7-java6-precise-20131128\n");
		writer.write("MAINTAINER Marcel Neuhausler\n");
		writer.write("ADD index.html /var/lib/tomcat7/webapps/ROOT/\n");
		writer.write("ADD tomcat-users.xml /etc/tomcat7/\n");
		writer.write("CMD service tomcat7 start && tail -F /var/lib/tomcat7/logs/catalina.out\n");
		
		writer.flush();
		writer.close();
	}

	private static void copyAndAdjustTomcatUsersFile(File tomcatUsersFile, String tempDirPath, TomcatBuilderConfig builderConfig) throws IOException
	{
		BufferedReader reader= new BufferedReader(new InputStreamReader(new FileInputStream(tomcatUsersFile), Charset.forName("UTF-8")));
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File(tempDirPath+"/tomcat-users.xml")));
		String line= null;
		while ((line= reader.readLine()) != null)
		{
			line= line.replace("##username", builderConfig.username);
			line= line.replace("##password", builderConfig.password);
			
			writer.write(line+'\n');
		}
		
		writer.flush();
		writer.close();
		reader.close();
	}

	public static void copy(File src, File dst) throws IOException
	{
		InputStream in= new FileInputStream(src);
		OutputStream out= new FileOutputStream(dst);

		byte[] buf= new byte[1024];
		int len;
		while ((len= in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void copy(File src, ArchiveOutputStream out) throws IOException
	{
		InputStream in= new FileInputStream(src);

		byte[] buf= new byte[1024];
		int len;
		while ((len= in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		in.close();
	}


	private static void cleanupTempDir(File tempDir)
	{
		// TODO Auto-generated method stub
		
	}


}
