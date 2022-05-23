package com.g11.questionmark.Controllers;

import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Models.User;
import com.g11.questionmark.Services.LoginService;
import com.g11.questionmark.Services.RegisterService;
import com.g11.questionmark.Services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    LoginService loginService = new LoginService();

    @RequestMapping("/")
    public String renderHomePage(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String renderLoginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("/register")
    public String renderRegisterForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @RequestMapping(value = "/loginAction", method = RequestMethod.POST)
    public String loginAction(@ModelAttribute("user") User user, Model model, HttpServletRequest httpServletRequest) {
        Integer userId = loginService.performLogin(user);
        if (userId == 0) {
            model.addAttribute("invalidLogin", userId);
            return "login";
        } else {
            String username = loginService.getUsername(userId);
            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute("userId", userId);
            User user1 = new User();
            user1.setUserId(userId);
            user1.setUsername(username);
            model.addAttribute("user", user1);
            System.out.println("The userid is:"+userId);
            return "welcome";
        }
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public String redirect(Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        return "welcome";
    }


   /* @PostMapping("/loginAction")
    public String loginAction(@ModelAttribute("user") User user) {
        System.out.println(user);
        return "welcome";
    }*/


    @RequestMapping(value = "/registerUser")
    public String registerUser(@ModelAttribute("user") User user,Model model, @RequestParam("confirmpassword") String confirmpassword) {
        RegisterService registerService = new RegisterService();
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        if(user.getPassword().equals(confirmpassword) && user.getEmail().matches(regex)){
            registerService.performRegistration(user);
            User newuser = new User();
            model.addAttribute("user",newuser);
            return "redirect:/login";
        }
        else{
            return "register";
        }
    }
    @RequestMapping(value = "/userAction",params = "seeQuestions")
    public String seeQuestions(Model model, HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession();
        Integer userId = (Integer) httpSession.getAttribute("userId");
        System.out.println("The userid is:" +userId);
        UserService userService = new UserService();
        List<Question> questionslist=userService.performGetQuestions();
        for (Question q:questionslist) {
            System.out.println("The question is:"+q.getQuestionText());
            System.out.println("The questionId is:"+q.getQuestionId());
        }
        model.addAttribute("questionlist", questionslist);
        return "questions";
    }

    @RequestMapping(value = "/search/text")
    public String searchText(@RequestParam(value="searchText",required=false) String searchText,Model model, HttpServletRequest httpServletRequest) {
        UserService userService = new UserService();
        System.out.println("The searchtext value is:"+searchText);
        List<Question> questionslist=userService.performSearchQuestion(searchText);
        for (Question q:questionslist) {
            System.out.println("The question is:"+q.getQuestionText());
            System.out.println("The questionId is:"+q.getQuestionId());
        }

        model.addAttribute("questionlist", questionslist);
        //model.addAttribute("question",question);
        return "questions";
    }


    @RequestMapping(value = "/search/text",params = "postQuestion")
    public String postQuest(@ModelAttribute("question") Question question, Model model, HttpServletRequest httpServletRequest){

        /*User user2 = new User();
        model.addAttribute("user", user2); */
        Question question1 = new Question();
        model.addAttribute("question",question1);
        return "postQuestion";
    }

    @RequestMapping(value = "/userAction",params = "postQuestion")
    public String postQuestion(@ModelAttribute("question") Question question, Model model, HttpServletRequest httpServletRequest){

        /*User user2 = new User();
        model.addAttribute("user", user2); */
        Question question1 = new Question();
        model.addAttribute("question",question1);
        return "postQuestion";
    }

    @RequestMapping(value = "/submitQuestion")
    public String submitQuestion(@ModelAttribute("question") Question question, Model model, HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession();
        Integer userId = (Integer) httpSession.getAttribute("userId");
        System.out.println("The userid is:" +userId);
        UserService userService = new UserService();
        if(userService.performSubmitQuestion(question,userId))
        {
            System.out.println("Inside if");
            String username = loginService.getUsername(userId);
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            model.addAttribute("user", user);
            return "welcome";
        }
        System.out.println("Question posting unsuccessfull");
        return null;
    }

}

