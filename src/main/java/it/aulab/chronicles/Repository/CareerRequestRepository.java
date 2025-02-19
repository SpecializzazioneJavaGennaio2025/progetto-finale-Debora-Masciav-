package it.aulab.chronicles.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.aulab.chronicles.Model.CareerRequest;



public interface CareerRequestRepository extends CrudRepository<CareerRequest, Long> {

    List<CareerRequest> findByIsCheckedFalse();
    List<CareerRequest> findByIsReviewedFalse();
    
    @Query(value="SELECT user_id FROM users_roles", nativeQuery = true)
    List<Long> findAllUserIds();

    @Query(value="SELECT role_id FROM users_roles WHERE user_id = :id", nativeQuery = true)
    List<Long> findUserById(@Param("id") Long id);

    

    


}
