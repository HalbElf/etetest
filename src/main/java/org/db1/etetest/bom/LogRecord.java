package org.db1.etetest.bom;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LogRecord {
	@Id 
	@GeneratedValue
	private Long id; 
	@Column(columnDefinition="TEXT")
	private String requestData;
	@Column(columnDefinition="TEXT")
	private String responseData;
	@ManyToOne(targetEntity=ProxyServer.class)
	private ProxyServer proxyServer;
    private Date time;

	public LogRecord() {
	}
	
	public String getRequestData() {
		return requestData;
	}
	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
	public String getResponseData() {
		return responseData;
	}
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

    public void setTime(Date time) {
        this.time = time;
    }
    public Date getTime() {
        return time;
    }
    public void setProxyServer(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }
}
