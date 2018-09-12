package org.db1.etetest.controllers;

import java.util.Objects;

public class HttpMonitorConfigDTO {
	private Integer portNumber; 
	private Integer remotePortNumber;
	private String remoteHost;	
	private String alias;
	
	public Integer getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
	public Integer getRemotePortNumber() {
		return remotePortNumber;
	}
	public void setRemotePortNumber(Integer remotePortNumber) {
		this.remotePortNumber = remotePortNumber;
	}
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}	
	@Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HttpMonitorConfigDTO)) {
            return false;
        }
        HttpMonitorConfigDTO tested = (HttpMonitorConfigDTO) o;
        return Objects.equals(remoteHost, tested.remoteHost) && Objects.equals(portNumber, tested.portNumber)
                && Objects.equals(remotePortNumber, tested.remotePortNumber);
    }
	@Override
    public int hashCode() {
        return Objects.hash(remoteHost, portNumber, remotePortNumber);
    }
}