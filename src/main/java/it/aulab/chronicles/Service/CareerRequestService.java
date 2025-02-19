package it.aulab.chronicles.Service;

import it.aulab.chronicles.Model.CareerRequest;
import it.aulab.chronicles.Model.User;

public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    void careerReject(Long requestId);
    CareerRequest find(Long id);
}
