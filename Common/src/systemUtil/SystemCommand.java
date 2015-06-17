//*******************************************************************
//
//      Oce Engineering Creteil: PRODUCT DOCUMENT        CONFIDENTIAL
//
//       R&D-filename    :   SystemCommand.java
//
//       Title           :
//       Abstract        :
//
//
//
//
//       Keywords        :
//
//       %version:       10 %
//       %created_by:    qgmo %
//       %date_created:  Thu Dec 23 11:59:35 2010 %
//       %full_filespec: SystemCommand.java~10:java:CRCOBALT#1 %
//
//   Copyright 2004 Oce-Technologies B.V., Venlo, The Netherlands
//
//*******************************************************************
package systemUtil;

import java.awt.Desktop;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import network.network_Client.BaseClient;

public class SystemCommand {

	private ArrayList<Process> processList;
	private static boolean CONSOLE_DEBUG = false;
	private BaseClient client = null;

	public SystemCommand(BaseClient client) {
		processList = new ArrayList<Process>();
		this.client = client;
	}

	public SystemCommand() {
		processList = new ArrayList<Process>();
	}

	public ArrayList<Process> getProcessList() {
		return processList;
	}

	public void setConsoleDebug(boolean debug) {
		CONSOLE_DEBUG = debug;
	}

	public void killAllAssociatedProcesses() {
		for (int i = 0; i < processList.size(); i++) {
			processList.get(i).destroy();
		}
	}

	public void Test() {

		Runtime runtime = Runtime.getRuntime();
		Process p;
		try {

			// String commandeTest = "cmd /c find " +
			// ServerApplication.SaneBaseFolder + " -name *.tcl -print";
			String commandeTest = "cmd /c find " + "\\\\Cobuild2-sp\\Logs\\BMRef54 -name *.tcl -print";

			p = runtime.exec(commandeTest);

			Misc misc;
			if (client != null) {
				misc = this.new Misc(client);
			} else {
				misc = this.new Misc();
			}
			misc.redirect(p);
			p.waitFor();

			String output = misc.getOutput();

			System.out.println(output);

		} catch (IOException e) {
			System.out.println("IOException " + e);
		} catch (InterruptedException e) {
			System.out.println("Process for testapp was interrupted");
			System.err.println(e);
		}

	}

	public String[] listDirectories(String CheminRepertoire) {

		FilenameFilter filterDir = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				File fTest = new File(dir.getAbsolutePath() + "\\" + name);
				boolean condOnType = fTest.isDirectory();
				return condOnType;
			}
		};

		File f = new File(CheminRepertoire);
		return f.list(filterDir);

	}
	
	/**
	 * 
	 * @param path the path to the directory inside which files will be listed.
	 * @return list of files
	 */
	public File[] listFilesOnly(String path) throws SecurityException {

		FileFilter filterExt = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		
		File f = new File(path);
		return f.listFiles(filterExt);
	}

	/**
	 * 
	 * @param CheminRepertoire
	 *            path of the directory to find into
	 * @param fileExtension
	 *            file extension filter. don't give the "*.xml" but just ".xml"
	 * @return list of files with the given file extension. Warning : return
	 *         null if no file corresponding to the criteria.
	 */
	public File[] listFiles(String CheminRepertoire, final String fileExtension) {

		FilenameFilter filterExt = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(fileExtension)) {
					return true;
				} else {
					return false;
				}
			}
		};

		File f = new File(CheminRepertoire);
		return f.listFiles(filterExt);

	}

	/**
	 * This function remove all inside the given directory.
	 * 
	 * @param emplacement
	 *            path du directory
	 */
	public boolean removeAllINDirectory(String emplacement) {
		boolean allSucceeded = true;
		File path = new File(emplacement);
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						if (!removeAllINDirectory(files[i].getAbsolutePath())) {
							allSucceeded = false;
						}
					}
					if (!files[i].delete()) {
						allSucceeded = false;
					}
				}
			}
		}
		return allSucceeded;
	}

	/**
	 * 
	 * @param CheminRepertoire
	 *            chemin du dossier que l'on souhaite supprimer
	 * @return true si l'operation s'est bien passe, false sinon
	 */
	public String removeDirectory(String CheminRepertoire) {

		return execCMDcommand("rmdir /s /q " + CheminRepertoire);

	}

	public String createDirectory(String CheminRepertoire) {
		return execCMDcommand("mkdir " + CheminRepertoire);
	}

	public String removeFile(String CheminFile) {
		return execCMDcommand("rm -f " + CheminFile);
	}

	public void openFileWithDefault(String completePath) {

		if (Desktop.isDesktopSupported()) {
			if (Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
				try {
					java.awt.Desktop.getDesktop().open(new File(completePath));
				} catch (IOException ex) {
					System.err.println("IO Exception encountered -> unable to open " + completePath + ex);
				}
			} else {
				System.err.println("Desktop is not supported on this OS -> unable to open " + completePath);
			}
		} else {
			System.err.println("Desktop is not supported on this OS -> unable to open " + completePath);
		}
	}

	public String execCMDcommand(String command) {
		String completeCommand = "cmd /c " + command;

		Runtime runtime = Runtime.getRuntime();
		Process p;
		try {
			p = runtime.exec(completeCommand);
			processList.add(p);
			Misc misc;
			if (client != null) {
				misc = this.new Misc(client);
			} else {
				misc = this.new Misc();
			}
			misc.redirect(p);
			p.waitFor();
			String output = misc.getOutput();
			return output;

		} catch (IOException e) {
			System.out.println("IOException " + e);
		} catch (InterruptedException e) {
			System.out.println("Process was interrupted");
			System.err.println(e);
		}
		return "Command Line Error in SystemCommand.java";
	}

	/**
	 * 
	 * @param path
	 *            chemin du directory
	 * @return true si le dossier existe, false sinon
	 */
	public boolean isdirectoryExist(String path) {
		File f = new File(path);
		return f.exists();
	}

	/**
	 * 
	 * @param Drive
	 *            lettre du disque que l'on souhaite monter
	 * @param networkPath
	 *            chemin reseau
	 * @return true si le disque a ete monte correctement, false sinon
	 */
	public boolean monterDisqueReseau(String drive, String networkPath) {

		// Dans un premier temps, on verifie si le lecteur que l'on veut
		// monter n'existe pas
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].getAbsolutePath().equals(drive + "\\")) {
				System.out.println("Le lecteur reseau existe deja, on le supprime");
				execCMDcommand("net use " + drive + " /delete");
			}
		}

		execCMDcommand("net use " + drive + " " + networkPath);

		roots = File.listRoots();
		boolean driveMounted = false;
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].getAbsolutePath().equals(drive + "\\")) {
				driveMounted = true;
			}
		}

		return driveMounted;
	}

	/**
	 * 
	 * A class created to redirect the output (including errors) <br>
	 * of a process. <br>
	 * Used in tonga.util.Misc. <br>
	 * 
	 */
	static class Redirector extends Thread {

		InputStream iStream;
		StringBuffer out;
		BaseClient client = null;

		/**
		 * 
		 * Constructor for Redirector <br>
		 * 
		 * @param InputStream
		 *            - the input stream of the process <br>
		 * @param DataOutputStream
		 *            dos - the output stream of the output file. <br>
		 * 
		 */
		public Redirector(InputStream is) {
			iStream = is;
			out = new StringBuffer();
		}

		public Redirector(InputStream is, BaseClient client) {
			iStream = is;
			out = new StringBuffer();
			this.client = client;
		}

		/**
		 * 
		 * Method run - execution of actual writing to output stream <br>
		 * 
		 */
		public void run() {

			int ch;
			try {
				byte[] b = new byte[4096];
				while ((ch = iStream.read(b)) != -1) {

					String line = new String(b, 0, ch);

					if (line.isEmpty()) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if (CONSOLE_DEBUG == true) {
							System.out.println(line);
						}
						if (client != null) {
							client.sendString("[LOG]<##>" + line);
						}

						out.append(line);
					}
				}
			} catch (IOException e) {
				System.out.println("error occurred when trying to read process output");
				System.err.println(e);
			}
		}

		public String getString() {
			return out.toString();
		}
	}

	class Misc {

		Redirector rStdout;
		Redirector rStderr;
		BaseClient client = null;

		public Misc() {
		}

		public Misc(BaseClient client) {
			this.client = client;
		}

		// send string to log and optionally to stdout
		public void log(String str, DataOutputStream dout) {
			try {
				dout.writeBytes(str + "\n");
				System.out.println(str);
			} catch (IOException e) {
				System.out.println("IO Exception occurred in printing out " + str);
				System.err.println(e);
			}
		}

		// Redirect stdout and stderr of process
		// (redirection is needed when waitFor() is used
		// to guard against process occasionally dying unpredictably
		public void redirect(Process p) {
			InputStream isStdout = p.getInputStream();

			// Redirector extends Thread
			if (client != null) {
				rStdout = new Redirector(isStdout, client);
			} else {
				rStdout = new Redirector(isStdout);
			}
			rStdout.run();

			InputStream isStderr = p.getErrorStream();
			if (client != null) {
				rStderr = new Redirector(isStderr, client);
			} else {
				rStderr = new Redirector(isStderr);
			}
			rStderr.run();

			try {
				rStdout.join();
				rStderr.join();
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception in Misc.redirect()");
				System.out.println(e);
			}

		}

		public String getOutput() {
			return rStdout.getString() + rStderr.getString();
		}
	}
}
