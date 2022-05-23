package com.g11.questionmark.Controllers;

import com.g11.questionmark.Models.Answer;
import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Models.User;
import com.g11.questionmark.Models.Vote;
import com.g11.questionmark.Services.AnswerService;
import com.g11.questionmark.Services.QuestionService;
import com.g11.questionmark.Services.VoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class QuestionController {

    QuestionService questionService = new QuestionService();
    AnswerService answerService = new AnswerService();
    VoteService voteService = new VoteService();

    @RequestMapping("/question/{questionId}")
    public String renderQuestionPage(@PathVariable(name = "questionId") String questionId, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        Question question = questionService.getQuestionDetails(questionId);
        if (question != null) {
            Answer answer = new Answer();
            Vote vote = new Vote();
            answer.setQuestionId(question.getQuestionId());
            vote.setEntityId(question.getQuestionId());
            model.addAttribute("question", question);
            model.addAttribute("answer", answer);
            model.addAttribute("answers", answerService.getAllAnswersOfQuestion(Integer.parseInt(questionId)));
            return "question";
        }
        return "errorPage";
    }

    @RequestMapping(value = "/submitAnswer", method = RequestMethod.POST)
    public String submitAnswer(@ModelAttribute("answer") Answer answer, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        Integer userId = (Integer) httpSession.getAttribute("userId");
        if (answerService.submitAnswer(answer, userId)) {
            return "redirect:/question/"+answer.getQuestionId();
        }
        return "errorPage";
    }

    @RequestMapping(value = "/questionVote/{questionId}", method = RequestMethod.POST, params="action=up")
    public String questionUpVote(@PathVariable(name = "questionId") String questionId, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("usesr", user);
            return "login";
        }
        Integer userId = (Integer) httpSession.getAttribute("userId");
        Vote vote = new Vote();
        vote.setEntityId(Integer.parseInt(questionId));
        vote.setVote("UP");
        vote.setEntity("questions");
        if (voteService.upVote(vote, userId)) {
            return "redirect:/question/"+questionId;
        }
        return "errorPage";
    }

    @RequestMapping(value = "/questionVote/{questionId}", method = RequestMethod.POST, params="action=down")
    public String questionDownVote(@PathVariable(name = "questionId") String questionId, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        Integer userId = (Integer) httpSession.getAttribute("userId");
        Vote vote = new Vote();
        vote.setEntityId(Integer.parseInt(questionId));
        vote.setVote("DOWN");
        vote.setEntity("questions");
        if (voteService.downVote(vote, userId)) {
            return "redirect:/question/"+questionId;
        }
        return "errorPage";
    }

    @RequestMapping(value = "/answerVote/{questionId}/{answerId}", method = RequestMethod.POST, params="action=up")
    public String answerUpVote(@PathVariable(name = "questionId") String questionId, @PathVariable(name = "answerId") String answerId, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        Integer userId = (Integer) httpSession.getAttribute("userId");
        Vote vote = new Vote();
        vote.setEntityId(Integer.parseInt(answerId));
        vote.setVote("UP");
        vote.setEntity("answers");
        if (voteService.upVote(vote, userId)) {
            return "redirect:/question/"+questionId;
        }
        return "errorPage";
    }

    @RequestMapping(value = "/answerVote/{questionId}/{answerId}", method = RequestMethod.POST, params="action=down")
    public String answerDownVote(@PathVariable(name = "questionId") String questionId, @PathVariable(name = "answerId") String answerId, Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            User user = new User();
            model.addAttribute("user", user);
            return "login";
        }
        Integer userId = (Integer) httpSession.getAttribute("userId");
        Vote vote = new Vote();
        vote.setEntityId(Integer.parseInt(answerId));
        vote.setVote("DOWN");
        vote.setEntity("answers");
        if (voteService.downVote(vote, userId)) {
            return "redirect:/question/"+questionId;
        }
        return "errorPage";
    }

}
