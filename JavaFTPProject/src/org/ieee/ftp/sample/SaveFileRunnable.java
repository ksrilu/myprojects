package org.ieee.ftp.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.net.ftp.FTPSClient;

class SaveFileRunnable implements Runnable 
{
	private Thread saveFile;
	private String threadName;
	private String fileName;
	private String folderName;
	private InputStream inputStream;
	private Date startTime;
	private Date endTime;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	SaveFileRunnable(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void run() {
		try {
			System.out.println("Running " + threadName);
			FTPSClient ftpclient = new FTPSClient(false);
			FTPClientUtils.ftpConnect(ftpclient);
			FTPClientUtils.uploadFile(ftpclient, getFolderName(),
					getFileName(), getInputStream());
			inputStream.close();
			FTPClientUtils.ftpDisConnect(ftpclient);
			Thread.sleep(50);
		} catch (InterruptedException e) {
			System.out.println("Thread " + threadName + " interrupted.");
		} catch (IOException io) {
			System.out.println("Thread " + threadName
					+ " IOException occurred.");
			io.printStackTrace(System.out);
		}
		end();
	}

	public void start() {
		startTime = new Date();
		System.out.println("Starting " + threadName);
		if (saveFile == null) {
			saveFile = new Thread(this, threadName);
			saveFile.start();
		}
	}
	
	public void end() {
		
		if(saveFile != null)
		{
			System.out.println("Ending " + threadName);
			saveFile = null;
		}
		endTime = new Date();
		System.out.println("Total FileSave Time : "+(endTime.getTime()-startTime.getTime())+" MS ");
	}
}
