package it.aulab.chronicles.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import it.aulab.chronicles.Model.CareerRequest;
import it.aulab.chronicles.Repository.ArticleRepository;
import it.aulab.chronicles.Repository.CareerRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NotificationInterceptor implements HandlerInterceptor {
    @Autowired
    CareerRequestRepository careerRequestRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @SuppressWarnings("null")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.isUserInRole("ROLE_ADMIN")) {
            List<CareerRequest> uncheckedRequests = careerRequestRepository.findByIsCheckedFalse();
            List<CareerRequest> reviewedRequests = careerRequestRepository.findByIsReviewedFalse();
            int careerCount = (int) uncheckedRequests.stream().filter(reviewedRequests::contains).count();

            // int careerCount = careerRequestRepository.findByIsCheckedFalse().size();
            modelAndView.addObject("careerRequests", careerCount);
        }

        if (modelAndView != null && request.isUserInRole("ROLE_REVISOR")) {
            int revisedCount = articleRepository.findByIsAcceptedNull().size();
            modelAndView.addObject("articlesToBeRevised", revisedCount);
        }

    }

}
