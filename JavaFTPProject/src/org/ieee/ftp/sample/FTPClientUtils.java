package org.ieee.ftp.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Date;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

public class FTPClientUtils 
{
	private static String hostName = "pubftp.ieee.org";
	private static String username = "icxconfer";
	private static String password = "SprinG2016";
	
	public static boolean ftpConnect(FTPSClient ftpcl)  
	{
		boolean connected = false;
		System.out.println("FTPClientUtils :: Logging in FTP..");
		try 
		{
			//ftpcl = new FTPSClient(false);
			ftpcl.setAuthValue("TLS");
			// to show FTP commands in prompt
			ftpcl.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
			// disable remote host verification
			ftpcl.setRemoteVerificationEnabled(false);
			// trust in ALL
			ftpcl.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
			// send keepAlive every 30 seconds
			ftpcl.setControlKeepAliveTimeout(10l);
			// data transfer timeout
			ftpcl.setDataTimeout(30000);
			
			// connect
			//ftps.connect("pubftp.ieee.org", 3100);
			ftpcl.connect(hostName, 3100);
			ftpcl.login(username, password);
			showServerReply(ftpcl);
			ftpcl.enterLocalPassiveMode();
			ftpcl.execPROT("P");
			ftpcl.setFileType(FTP.BINARY_FILE_TYPE);
			
			boolean sendNoop = ftpcl.sendNoOp();
			System.out.println("sendNoop=" + sendNoop);
			if(sendNoop)
			{
				connected = true;
			}
		}
		catch (FTPConnectionClosedException e) 
		{          
            System.err.println("PubFTP ERROR :: FTP Server Unreachable");
        } 
		catch (SocketException e) 
		{
            System.err.println("PubFTP ERROR :: FTP Server Unreachable");
        } 
		catch (IOException e) 
		{
            System.err.println("PubFTP ERROR :: FTP Server Unreachable");
        }
		System.out.println("FTPClientUtils :: FTP Login Successful..");
		return connected;
	}
	
	private boolean checkConnectionWithOneRetry(FTPSClient ftpcl)
	{
	    try 
	    {
	        // Sends a NOOP command to the FTP server. 
	        boolean answer = ftpcl.sendNoOp();
	        if(answer)
	            return true;
	        else
	        {
	            System.out.println("Server connection failed!");
	            boolean success = ftpConnect(ftpcl);
	            if(success)
	            {
	                System.out.println("Reconnect attampt have succeeded!");
	                return true;
	            }
	            else
	            {
	                System.out.println("Reconnect attampt failed!");
	                return false;
	            }
	        }
	    }
	    catch (FTPConnectionClosedException e) 
	    {
	        System.out.println("Server connection is closed!");
	        boolean recon = ftpConnect(ftpcl);
	        if(recon)
	        {
	            System.out.println("Reconnect attampt have succeeded!");
	            return true;
	        }
	        else
	        {
	            System.out.println("Reconnect attampt have failed!");
	            return false;
	        }

	    }
	    catch (IOException e) 
	    {
	        System.out.println("Server connection failed!");
	        boolean recon = ftpConnect(ftpcl);
	        if(recon)
	        {
	            System.out.println("Reconnect attampt have succeeded!");
	            return true;
	        }
	        else
	        {
	            System.out.println("Reconnect attampt have failed!");
	            return false;
	        }   
	    }
	    catch (NullPointerException e) 
	    {
	        System.out.println("Server connection is closed!");
	        boolean recon = ftpConnect(ftpcl);
	        if(recon)
	        {
	            System.out.println("Reconnect attampt have succeeded!");
	            return true;
	        }
	        else
	        {
	            System.out.println("Reconnect attampt have failed!");
	            return false;
	        }   
	    }
	}
	
	public void sleep()
	{
        try 
        {
            Thread.sleep(10000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
	}
	
	/**
	 * disconnect to FTP server
	 * 
	 * @param ftpclient
	 *            is Object which is having details of FTP server like IP, user
	 *            name and password
	 * @throws IOException
	 *             throws Exception
	 */
	public static void ftpDisConnect(FTPSClient ftpcl) throws IOException 
	{
		System.out.println("FTPClientUtils :: FTP Logging out..");
		// close
		if(ftpcl.isConnected())
		{
			ftpcl.logout();
			ftpcl.disconnect();
		}
		else
		{
			System.out.println("FTPClientUtils :: FTP Already disconnected..");
		}
		System.out.println("FTPClientUtils :: FTP Disconnected Successfully..");
	}
	
	public static void listFiles(FTPSClient ftpcl, String destinationDirectory) 
			throws IOException 
	{
		if(destinationDirectory != null)
		{
			ftpcl.changeWorkingDirectory(destinationDirectory);
		} 
		System.out.println("--------listFiles--DIR="+ftpcl.printWorkingDirectory());
		FTPFile[] files = ftpcl.listFiles();
		if(files != null && files.length > 0)
		{
			for(FTPFile file: files)
			{
				System.out.println("--------FileName="+file.getName()+":RawListing="+file.getRawListing());
		    }
		}
		else
		{
			System.out.println("-------------No files exist to List for Folder "+destinationDirectory+"--");
		}
	}
	
	public static boolean checkFile(FTPSClient ftpcl, String filePath) throws IOException 
	{
	    InputStream inputStream = ftpcl.retrieveFileStream(filePath);
	    int returnCode = ftpcl.getReplyCode();
	    //source not found
	    if (inputStream == null || returnCode == 550) 
	    {
	        return false;
	    }
	    return true;
	}
	
	/**
     * Determines whether a directory exists or not
     * @param FTPClient
     * @param dirPath
     * @return true if exists, false otherwise
     * @throws IOException thrown if any I/O error occurred.
     */
	public static boolean checkDirectory(FTPSClient ftpcl, String dirPath) 
			throws IOException 
	{
		ftpcl.changeWorkingDirectory(dirPath);
		int returnCode = ftpcl.getReplyCode();
		//source not found
	    if (returnCode == 550) 
	    {
	        return false;
	    }
	    return true;
	}
	
	private static void showServerReply(FTPSClient ftpcl) 
	{
        String[] replies = ftpcl.getReplyStrings();
        if (replies != null && 
        	replies.length > 0) 
        {
            for (String aReply : replies) 
            {
                System.out.println("SERVER REPLY : " + aReply);
            }
        }
    }
	
	/**
	 * fileName : full qualified path
	 */
	public static void uploadFile(FTPSClient ftpcl, 
								   String remoteDir,
								   String fileName, 
			                       InputStream inputStream)
				throws IOException
	{
		// uploads file as InputStream
		if(remoteDir != null)
		{
			ftpcl.changeWorkingDirectory(remoteDir);
		}
		System.out.println("Start uploading file="+fileName);
		long startTime = new Date().getTime();
		boolean fileDone = ftpcl.storeFile(fileName, inputStream);
		if(fileDone)
		{
			System.out.println("The "+fileName+" is upload submitted successfully.");
		}
		/*if(ftpcl.completePendingCommand())
		{
			long endTime = new Date().getTime();
			System.out.println("Total Time to upload.... "+(endTime-startTime));
		}*/
			
		

		
	}
	
	
	public static void main(String[] args) 
	{
		try
		{
			FTPSClient ftpclient = new FTPSClient(false);
			FTPClientUtils.ftpConnect(ftpclient);
			FTPClientUtils.listFiles(ftpclient, null);
			/*String chkDirectoryPath = "/SriTest/folderpath";
			if(FTPClientUtils.checkDirectory(ftpclient, chkDirectoryPath))
			{
				System.out.println(chkDirectoryPath+" exists..... ");
			}
			else
			{
				boolean crFolder = ftpclient.makeDirectory(chkDirectoryPath);
				System.out.println(chkDirectoryPath+" created..... "+crFolder);
			}
			String chkFilePath = "/inside_229_test2fr-icxconfer.zip";
			if(FTPClientUtils.checkFile(ftpclient, chkFilePath))
			{
				System.out.println(chkFilePath+ " file exists..... ");
			}
			else
			{
				System.out.println(chkFilePath+ " file NOT exists..... ");
			}*/
			String uploadFileName = "BIPublisherDesktop32.exe";
			String remoteDir = "/SriTest/folderpath";
			File f = new File("C:/MyDrive/Install/BIPublisherDesktop32.exe");
			InputStream inputStream = new FileInputStream(f);
			FTPClientUtils.uploadFile(ftpclient,remoteDir,uploadFileName,inputStream);
			inputStream.close();
			FTPClientUtils.ftpDisConnect(ftpclient);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
}
