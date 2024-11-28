package com.exam_jee.ds.controllers;

import com.exam_jee.ds.config.JwtService;
import com.exam_jee.ds.config.UserDetailsImpl;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.payload.request.LoginRequest;
import com.exam_jee.ds.payload.request.RegisterBanquierRequest;
import com.exam_jee.ds.payload.request.SignupRequest;
import com.exam_jee.ds.payload.response.ApiResponse;
import com.exam_jee.ds.payload.response.JwtResponse;
import com.exam_jee.ds.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

//    @PostMapping("register")
//    public ResponseEntity<?> register(@RequestBody SignupRequest request){
//        if (userService.existsByUsername(request.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new ApiResponse(false,"Error: Username is already taken!"));
//        }
//
//        if (userService.existsByEmail(request.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new ApiResponse(false,"Error: Email is already in use!"));
//        }
//        User user = User.builder()
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .passwordd(passwordEncoder.encode(request.getPassword()))
//                .build();
//        userService.save(user);
//        return ResponseEntity.ok(new ApiResponse(true,"User registered successfully!"));
//    }
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        if (!userService.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,"Email not found !"));
        }
        User user = userService.findByEmail(request.getEmail());
//        if(!user.isEmailVerified()){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ApiResponse(false,"Verify Your Email Please"));
//        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtService.createToken(new HashMap<>(),request.getEmail());
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);
//        System.out.println(userDetails.getUsername());
        return ResponseEntity.ok(JwtResponse.builder()
                        .email(request.getEmail())
                        .id(userDetails.getId())
                        .token(jwt)
                        .username(userDetails.getUsername())
                        .balance(userDetails.getBalance())
                        .status(userDetails.isEnabled())
                        .roles(userDetails.getAuthorities().stream().map(item -> item.getAuthority() ).toList())
                .build());
    }
    @PostMapping("/client")
    public ResponseEntity<?> registerClient(
            @RequestBody RegisterBanquierRequest client
    ) {
        if (userService.existsByEmail(client.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,"Error: Email is already in use!"));
        }
        try {
            userService.registerClient(client);
            return ResponseEntity.ok(new ApiResponse(true, "Client successfully registered"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostMapping("/banquier")
    public ResponseEntity<?> registerbBanquier(
            @RequestBody RegisterBanquierRequest banquier
            ) {
        if (userService.existsByEmail(banquier.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,"Error: Email is already in use!"));
        }
        try {
            userService.registerBanquier(banquier);
            return ResponseEntity.ok(new ApiResponse(true, "Banquier person successfully registered"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
