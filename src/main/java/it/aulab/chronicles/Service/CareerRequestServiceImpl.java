package it.aulab.chronicles.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.aulab.chronicles.Model.CareerRequest;
import it.aulab.chronicles.Model.Role;
import it.aulab.chronicles.Model.User;
import it.aulab.chronicles.Repository.CareerRequestRepository;
import it.aulab.chronicles.Repository.RoleRepository;
import it.aulab.chronicles.Repository.UserRepository;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();
        if (!allUserIds.contains(user.getId())) {
            return false;
        }

        List<Long> requests = careerRequestRepository.findUserById(user.getId());
        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    @Override
    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        emailService.sendSimpleMail("adminAulabChronicles@admin.com", "Richiesta di lavoro: " + careerRequest.getRole().getName(), "Una nuova richiesta di collaborazione da parte di " + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();

        List<Role> userRoles = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        userRoles.add(newRole);

        user.setRoles(userRoles);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleMail(user.getEmail(), "Richiesta di lavoro accettata", "La tua richiesta di lavoro per il ruolo di " + role.getName() + " è stata accettata!");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }



}
