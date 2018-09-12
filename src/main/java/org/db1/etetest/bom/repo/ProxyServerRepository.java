package org.db1.etetest.bom.repo;

import java.util.List;

import org.db1.etetest.bom.ProxyServer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProxyServerRepository  extends CrudRepository<ProxyServer, Long>  {
    List<ProxyServer> findByName(String name);
    @Modifying
    @Query("delete from ProxyServer px where px.name = ?1")
    void deleteByName(String firstName);
}
