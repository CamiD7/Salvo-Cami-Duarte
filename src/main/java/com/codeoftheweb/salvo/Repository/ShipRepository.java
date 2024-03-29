package com.codeoftheweb.salvo.Repository;
import com.codeoftheweb.salvo.Classes.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship, Long> {

}
