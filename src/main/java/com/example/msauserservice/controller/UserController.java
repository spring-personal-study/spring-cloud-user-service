package com.example.msauserservice.controller;

import com.example.msauserservice.Greeting;
import com.example.msauserservice.model.RequestUser;
import com.example.msauserservice.model.ResponseUser;
import com.example.msauserservice.model.UserDto;
import com.example.msauserservice.model.UserEntity;
import com.example.msauserservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final Greeting greeting;
    private final Environment env;

    private final UserService userService;


    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status(HttpServletRequest request) {
        //System.out.println(request.getServerPort());
        return String.format("It's Working in User Service, " +
                "port(local.server.port)=%s, " +
                "port(server.port)=%s, " +
                "token secret=%s, " +
                "token expiration time=%s",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"),
                env.getProperty("token.secret"),
                env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(requestUser, userDto);
        ResponseUser responseUser = new ResponseUser();
        BeanUtils.copyProperties(userService.createUser(userDto), responseUser);
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();

        userList.forEach(v -> {
            ResponseUser responseUser = new ResponseUser();
            BeanUtils.copyProperties(v, responseUser);
            result.add(responseUser);
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser user = new ResponseUser();
        BeanUtils.copyProperties(userDto, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


//  ??? ?????????????????? gateway-service ?????? ???????????? ?????????, WebSecurity ?????? ????????? AuthenticationFilter ????????? ???????????? ????????? ?????? ????????? ?????? ???????????? ?????????
//  ?????????????????? ??????????????? ?????????????????? ????????? ????????? ????????????. ???, ????????? ????????? ???????????? ???????????? UsernamePasswordAuthenticationToken ??? ?????? ???????????????.
//    @PostMapping("/login")
//    public ResponseEntity<Void> login() {
//        return ResponseEntity.status(HttpStatus.OK).body(null);
//    }

}
