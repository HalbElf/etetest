package consoleHttpMon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.db1.etetest.controllers.HttpMonitorConfigDTO;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class HttpMon {
	public enum ProcessType {
		Request, Response
	}

	private static final String REMOTE_PORT_ARG_NAME = "rp";
	private static final String LOCAL_PORT_ARG_NAME = "lp";
	private static final String REMOTE_HOST_ARG_NAME = "rh";
	private static final int BLOCK_SIZE = 4096;
	public static final int EOF = -1;
	private static final Logger LOGGER = Logger.getLogger(HttpMon.class.getName());
	private int localPort;
    private int remotePort;
	private String remoteHost;
	private Thread processThread;
    private List<Thread> activeThreads;
    private String name;
    private IRequestResponseLogger logger;

	private HttpMon() {
	}

	public static HttpMon create(String name, int localPort, int remotePort, String remoteHost, @Nullable IRequestResponseLogger logger) {
		HttpMon httpMon = new HttpMon();
		httpMon.name = name;
		httpMon.localPort = localPort;
		httpMon.remotePort = remotePort;
		httpMon.remoteHost = remoteHost;
		httpMon.activeThreads = Lists.newLinkedList();
		httpMon.logger = logger;
		LOGGER.info(String.format(
				"running with the following configuration: local port %d, remote host %s, remote port %d",
				localPort, remoteHost, remotePort));
		return httpMon;
	}
	
	public static HttpMon create(String[] args) {
		int localPort = 8080, remotePort = 80;
		String remoteHost = "localhost";
		
		Options options = new Options();
		options.addOption(LOCAL_PORT_ARG_NAME, true, "proxy listener port number");
		options.getOption(LOCAL_PORT_ARG_NAME).setType(int.class);
		options.addOption(REMOTE_PORT_ARG_NAME, true, "remote listener port number");
		options.getOption(REMOTE_PORT_ARG_NAME).setType(int.class);
		options.addOption(REMOTE_HOST_ARG_NAME, true, "remote listener host name");
		options.getOption(REMOTE_HOST_ARG_NAME).setType(String.class);

		CommandLineParser parser = new DefaultParser();
		String rowOptionValue = null;
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(LOCAL_PORT_ARG_NAME)) {
				rowOptionValue = cmd.getOptionValue(LOCAL_PORT_ARG_NAME);
				if (!Strings.isNullOrEmpty(rowOptionValue)) {
					localPort = Integer.valueOf(rowOptionValue);
				}
			}
			if (cmd.hasOption(REMOTE_PORT_ARG_NAME)) {
				rowOptionValue = cmd.getOptionValue(REMOTE_PORT_ARG_NAME);
				if (!Strings.isNullOrEmpty(rowOptionValue)) {
					remotePort = Integer.valueOf(rowOptionValue);
				}
			}
			if (cmd.hasOption(REMOTE_HOST_ARG_NAME)) {
				rowOptionValue = cmd.getOptionValue(REMOTE_HOST_ARG_NAME);
				if (!Strings.isNullOrEmpty(rowOptionValue)) {
					remoteHost = rowOptionValue;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp("httpMon", options);
			System.exit(-1);
		}
		return create("noname", localPort, remotePort, remoteHost, null);
	}

	public static void main(String[] args) {
		HttpMon instance = create(args);
		instance.process();
	}

	private final static class ProxyProcess {
		Socket socketIn, socketOut;
		byte[] buffer;
		StringBuilder sb;
		private ProcessType processType;
		private RequestHandler requestHandler;

		private ProxyProcess(Socket socketIn, Socket socketOut, ProcessType processType, RequestHandler requestHandler) throws Exception {
			this.socketIn = socketIn;
			this.socketOut = socketOut;
			this.processType = processType;
			this.buffer = new byte[BLOCK_SIZE];
			this.sb = new StringBuilder();
			this.socketIn.setSoTimeout(100);
			this.requestHandler = requestHandler;
		}

		public void run() {
			try {
				OutputStream socketOutputStream = socketOut.getOutputStream();
				InputStream socketInputStream = socketIn.getInputStream();
				int n;
				while ((n = socketInputStream.read(buffer)) > 0) {
					if (requestHandler.isInterrupted()) {
						return;
					}
					sb.append(new String(buffer, 0, n));
					socketOutputStream.write(buffer, 0, n);
				}
			} catch (SocketTimeoutException ste) {
				synchronized (LOGGER) {
					LOGGER.log(Level.INFO, "logging " + (processType == ProcessType.Request ? "request from the client" : "response from the server"));
					LOGGER.log(Level.INFO, getData());
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} 
		}
		
		String getData() {
		    return sb.toString();
		}
	}
	
	private final class RequestHandler extends Thread {
		Socket serverSocket, clientSocket;
        private IRequestResponseLogger logger;
		private RequestHandler(Socket serverSocket, Socket clientSocket, IRequestResponseLogger logger) {
			this.serverSocket = serverSocket;
			this.clientSocket = clientSocket;
			this.logger = logger;
		}
		public void run() {
			try {
				ProxyProcess requestProcess = new ProxyProcess(serverSocket, clientSocket, ProcessType.Request, this);
                ProxyProcess responseProcess = new ProxyProcess(clientSocket, serverSocket, ProcessType.Response, this);
                requestProcess.run();
                responseProcess.run();
				clientSocket.close();
				serverSocket.close();
				if (logger != null) {
				    logger.log(name, requestProcess.getData(), responseProcess.getData());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void interrupt() {
		if (processThread != null && processThread.isAlive()) {
			processThread.interrupt();
			for (Thread handlerThread : activeThreads) {
				handlerThread.interrupt();
			}
		}
	}
	public void process() {
		processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try (ServerSocket listener = new ServerSocket(localPort)) {
					while (true) {
						Socket serverSocket = listener.accept();
						LOGGER.log(Level.INFO, String.format("processing new client connection from %s/%s:%d", serverSocket.getInetAddress().getHostName(), serverSocket.getInetAddress().getHostAddress(), serverSocket.getPort()));
						try (Socket clientSocket = new Socket(remoteHost, remotePort)) {
							Thread t = new RequestHandler(serverSocket, clientSocket, logger);
							activeThreads.add(t);
							t.run();
						}
					}
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "error creating server/processing the incoming request", e);
					throw new RuntimeException(e);
				}
			}
		});
		processThread.start();
	}

    public HttpMonitorConfigDTO toMonitorConfigDTO(String alias) {
        HttpMonitorConfigDTO result = new HttpMonitorConfigDTO();
        result.setAlias(alias);
        result.setPortNumber(localPort);
        result.setRemotePortNumber(remotePort);
        result.setRemoteHost(remoteHost);
        return result;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getName() {
        return name;
    }
}