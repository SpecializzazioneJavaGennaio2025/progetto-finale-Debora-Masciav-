package it.aulab.chronicles.Service;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.DTO.UserDTO;
import it.aulab.chronicles.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void saveUser(UserDTO userDTO, RedirectAttributes RedirectAttributes, HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    User find(Long id);
}
