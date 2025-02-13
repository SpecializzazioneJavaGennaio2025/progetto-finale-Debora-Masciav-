package it.aulab.chronicles.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.aulab.chronicles.Model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
