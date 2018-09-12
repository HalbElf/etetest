package org.db1.etetest.bom;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ProxyServer {
	@Id 
	@GeneratedValue
	private Long id; 
	@Column(nullable = false, unique = true)
	private String name;
	
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy="proxyServer", cascade = CascadeType.ALL)
	private List<LogRecord> logRecords;
	
	public ProxyServer() {
	}
	
	public LogRecord addLogRecord(String request, String response) {
	    if (logRecords == null) {
	        logRecords = new LinkedList<>();
	    }
	    LogRecord logRecord = new LogRecord();
	    logRecord.setProxyServer(this);
	    logRecord.setRequestData(request);
	    logRecord.setResponseData(response);
	    logRecord.setTime(new Date(System.currentTimeMillis()));
	    logRecords.add(logRecord);
	    return logRecord;
	}
}
