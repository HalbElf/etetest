package org.db1.etetest.controllers;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.db1.etetest.aspect.Log;
import org.db1.etetest.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ServerController {
	private final static class CreateStatus {
		static enum StatusEnum{ 
			Success, Failure;
		}
		private StatusEnum status;
		@SuppressWarnings("unused")
		public StatusEnum getStatus() {
			return status;
		}
		@SuppressWarnings("unused")
		public void setStatus(StatusEnum status) {
			this.status = status;
		}
		@SuppressWarnings("unused")
		public String getText() {
			return text;
		}
		@SuppressWarnings("unused")
		public void setText(String text) {
			this.text = text;
		}
		private String text;
		static CreateStatus createStatus(StatusEnum status, @Nullable String text) {
			CreateStatus createStatus = new CreateStatus();
			createStatus.status = status;
			createStatus.text = text; 
			return createStatus;
			
		}
	}

	@Autowired
	private ServerService serverService;
	
	@RequestMapping("/")
	@Log
	public String index(Map<String, Object> model) {
		return "index";
	}

	@RequestMapping(value = "/add-server", method = RequestMethod.POST)
	@ResponseBody
	public CreateStatus addServer(@RequestBody HttpMonitorConfigDTO serverConfig) {
		try {
			serverService.addServer(serverConfig);
			return CreateStatus.createStatus(CreateStatus.StatusEnum.Success, String.format("monitor was created successfully"));
		} catch (Exception e) {
			return CreateStatus.createStatus(CreateStatus.StatusEnum.Failure, String.format("cannot create server: '%s'", e.getMessage()));
		}
	}

	@RequestMapping(value = "/get-servers", method = RequestMethod.GET)
    @ResponseBody
    public Collection<HttpMonitorConfigDTO> getServers() {
        return serverService.getServers();
    }

    @RequestMapping(value = "/delete-servers", method = RequestMethod.PUT)
    @ResponseBody
    public void deleteServer(@RequestBody HttpMonitorConfigDTO[] monitors) {
        for (HttpMonitorConfigDTO monitor : monitors) {
            serverService.stopServer(monitor.getAlias());
        }
    }

    @RequestMapping(value = "/clear-data", method = RequestMethod.DELETE)
    @ResponseBody
    public void clearDatabase() {
        serverService.clearDatabase();
    }
}
