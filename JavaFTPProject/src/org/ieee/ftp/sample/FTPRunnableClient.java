package org.ieee.ftp.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FTPRunnableClient 
{
	   public static void main(String args[]) throws Exception
	   {
		   
		   String uploadFileName = "BIPublisherDesktop32.exe";
		   String remoteDir = "/SriTest/folderpath";
		   File f = new File("C:/MyDrive/Install/BIPublisherDesktop32.exe");
		   InputStream inputStream = new FileInputStream(f);
		   SaveFileRunnable R1 = new SaveFileRunnable( uploadFileName);
		   R1.setFileName(uploadFileName);
		   R1.setFolderName(remoteDir);
		   R1.setInputStream(inputStream);
	       R1.start();
	       String uploadFileName1 = "BIPublisherDesktop32-1.exe";
		   String remoteDir1 = "/SriTest/folderpath";
		   File f1 = new File("C:/MyDrive/Install/BIPublisherDesktop32.exe");
		   InputStream inputStream1 = new FileInputStream(f1);
		   SaveFileRunnable R2 = new SaveFileRunnable( uploadFileName1);
		   R2.setFileName(uploadFileName1);
		   R2.setFolderName(remoteDir1);
		   R2.setInputStream(inputStream1);
	       R2.start();
	   }

}
