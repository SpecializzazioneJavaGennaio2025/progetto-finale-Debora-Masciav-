package it.aulab.chronicles.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.Model.CareerRequest;
import it.aulab.chronicles.Model.Role;
import it.aulab.chronicles.Model.User;
import it.aulab.chronicles.Repository.RoleRepository;
import it.aulab.chronicles.Repository.UserRepository;
import it.aulab.chronicles.Service.CareerRequestService;

@Controller
@RequestMapping("/operations")
public class OperationController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRequestService careerRequestService;

    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel, @ModelAttribute("body") String body, Principal principal) {
        viewModel.addAttribute("title", "Inserisci la tua richiesta di lavoro");
        // viewModel.addAttribute("careerRequest", new CareerRequest());
        CareerRequest careerRequest = new CareerRequest();
        careerRequest.setBody(body);
        viewModel.addAttribute("careerRequest", careerRequest);

        List<Role> roles = roleRepository.findAll();
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));

        viewModel.addAttribute("roles", roles);
        // viewModel.addAttribute("user", userRepository.findByEmail(principal.getName()));

        return "career/requestForm";
    }

    @PostMapping("/career/request/store")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName());

        if (careerRequestService.isRoleAlreadyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sei già stato assegnato a questa richiesta di lavoro!");
            redirectAttributes.addFlashAttribute("body", careerRequest.getBody());
            return "redirect:/operations/career/request";
        }

        careerRequestService.save(careerRequest, user);
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta di lavoro inviata con successo!");

        return "redirect:/";
    }


    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio richiesta" );
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";
    }

    @PostMapping("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Ruolo abilitato per l'utente");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/career/request/reject/{requestId}")
    public String careerRequestReject(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        careerRequestService.careerReject(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta rifiutata");
        return "redirect:/admin/dashboard";
    }

}
