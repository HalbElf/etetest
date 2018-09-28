package org.db1.etetest.bom;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @OneToMany(mappedBy = "proxyServer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<LogRecord> logRecords = new LinkedList<>();
	
	public ProxyServer() {
	}

    public  List<LogRecord> getLogRecords() {
        return logRecords;
    }
}
