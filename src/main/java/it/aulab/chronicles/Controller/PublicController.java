package it.aulab.chronicles.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicController {

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {
        if (number == 403) {
            return "redirect:/?notAuthorized";
        }
        return "redirect:/";
    }
    
}
