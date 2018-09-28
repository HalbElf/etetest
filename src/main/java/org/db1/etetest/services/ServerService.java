package org.db1.etetest.services;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.db1.etetest.bom.LogRecord;
import org.db1.etetest.bom.ProxyServer;
import org.db1.etetest.bom.repo.ProxyServerRepository;
import org.db1.etetest.controllers.HttpMonitorConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import consoleHttpMon.HttpMon;
import consoleHttpMon.IRequestResponseLogger;

@Service
public class ServerService  implements IRequestResponseLogger {
	private Map<String, HttpMon>servers = new HashMap<>();
	@Autowired
	private ProxyServerRepository proxyServerRepository;   
	 
	public HttpMon addServer(HttpMonitorConfigDTO monitorConfig) {
	    Preconditions.checkArgument(!Strings.isNullOrEmpty(monitorConfig.getAlias()), "alias for the monitor must be specified");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(monitorConfig.getRemoteHost()), "remote host for the monitor must be specified");
        Preconditions.checkArgument(monitorConfig.getPortNumber() != null, "local port number for the monitor must be specified");
        Preconditions.checkArgument(monitorConfig.getRemotePortNumber() != null, "remote port number for the monitor must be specified");
        
		if (servers.containsKey(monitorConfig.getAlias())) {
			throw new RuntimeException(String.format("server with the name '%s' already exists", monitorConfig.getAlias()));
		}

		Optional<Entry<String, HttpMon>> existingMonitorOpt = servers.entrySet().stream().filter(new Predicate<Map.Entry<String, HttpMon>>() {
            @Override
            public boolean test(Entry<String, HttpMon> t) {
                return t.getValue().getLocalPort() == monitorConfig.getPortNumber();
            }
        }).findFirst();
		
        if (existingMonitorOpt.isPresent()) {
		    throw new RuntimeException(String.format("server listening the same local port already exists", monitorConfig.getAlias()));
		}
		
        HttpMon httpMon = HttpMon.create(monitorConfig.getAlias(), monitorConfig.getPortNumber(), monitorConfig.getRemotePortNumber(), monitorConfig.getRemoteHost(), this);
		servers.put(monitorConfig.getAlias(), httpMon);
		// create a database object for the monitor
		try {
			httpMon.process();
			return httpMon;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public Collection<HttpMonitorConfigDTO> getServers() {
        return servers.entrySet().stream()
                .map(httpMonValue -> httpMonValue.getValue().toMonitorConfigDTO(httpMonValue.getKey()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void log(String alias, String request, String response) {
        ProxyServer proxyServer = null;
        List<ProxyServer> proxyServers = proxyServerRepository.findByName(alias);
        if (!proxyServers.isEmpty()) {
            proxyServer = proxyServers.get(0);
        }
        if (proxyServer == null) {
            proxyServer = new ProxyServer();
            proxyServer.setName(alias);
        }
        LogRecord logRecord = new LogRecord();
        logRecord.setProxyServer(proxyServer);
        logRecord.setRequestData(request);
        logRecord.setResponseData(response);
        logRecord.setTime(new Date(System.currentTimeMillis()));
        proxyServer.getLogRecords().add(logRecord);
        proxyServerRepository.save(proxyServer);
    }

    // stop server, its log records still exist in the database
    public boolean stopServer(String alias) {
        HttpMon removed = servers.remove(alias);
        if (removed != null) {
            removed.interrupt();
            return true;
        }
        return false;
    }

    // stop server and delete its records from the database
    @Transactional
    public void deleteServer(String alias) {
        stopServer(alias);
        proxyServerRepository.deleteByName(alias);
    }
    
    @Transactional
    public void clearDatabase() {
        for (String alias : servers.keySet()) {
            stopServer(alias);
        }
        proxyServerRepository.deleteAll();
    }
}
