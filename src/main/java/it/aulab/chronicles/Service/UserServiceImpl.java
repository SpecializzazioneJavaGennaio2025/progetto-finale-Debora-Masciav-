package it.aulab.chronicles.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.DTO.UserDTO;
import it.aulab.chronicles.Model.Role;
import it.aulab.chronicles.Model.User;
import it.aulab.chronicles.Repository.RoleRepository;
import it.aulab.chronicles.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(UserDTO userDTO, RedirectAttributes RedirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setUsername(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));
        userRepository.save(user);

        authenticateUserAndSetSession(user, userDTO, request);
    }

    public void authenticateUserAndSetSession(User user, UserDTO userDTO, HttpServletRequest request) {
        try{
            CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            e.printStackTrace();    
        }
    }
}
