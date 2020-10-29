package se.experis.academy.noticeboard.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import se.experis.academy.noticeboard.models.CommonResponse;
import se.experis.academy.noticeboard.models.User;
import se.experis.academy.noticeboard.models.web.LoginRequest;
import se.experis.academy.noticeboard.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        Optional<User> optionalUser = userRepository.findByUserName(loginRequest.getUserName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                System.out.println("Id Login " + request.getSession().getId());
                request.getSession().setAttribute("userId", user.getId());
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        CommonResponse cm = new CommonResponse();
        HttpSession session = request.getSession(false);

        if (session != null) {
            int loggedInUserId = (int) session.getAttribute("userId");
            Optional<User> optionalUser = userRepository.findById(loggedInUserId);
            if (optionalUser.isPresent()) {
                cm.data = optionalUser.get();
            }
        }

        return new ResponseEntity<>(cm, HttpStatus.OK);
    }

    @GetMapping("/status")
    public Boolean getStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null;
    }

    @GetMapping("/userId")
    public Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        int loggedInUserId = -1;

        if (session != null) {
            loggedInUserId = (int) session.getAttribute("userId");

        }

        return loggedInUserId;
    }
}
