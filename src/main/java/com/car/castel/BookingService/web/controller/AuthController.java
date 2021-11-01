package com.car.castel.BookingService.web.controller;


import com.car.castel.BookingService.entity.VehicleDriver;
import com.car.castel.BookingService.entity.auth.ERole;
import com.car.castel.BookingService.entity.auth.Role;
import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.entity.auth.UserStatus;
import com.car.castel.BookingService.repository.auth.RoleRepository;
import com.car.castel.BookingService.security.jwt.JwtUtils;
import com.car.castel.BookingService.security.services.UserDetailsImpl;
import com.car.castel.BookingService.service.DriverService;
import com.car.castel.BookingService.service.auth.UserService;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import com.car.castel.BookingService.web.exception.InvalidCredentialException;
import com.car.castel.BookingService.web.dto.request.FoughtPassword;
import com.car.castel.BookingService.web.dto.request.LoginRequest;
import com.car.castel.BookingService.web.dto.request.RegisterRequest;
import com.car.castel.BookingService.web.dto.response.ApiResponse;
import com.car.castel.BookingService.web.dto.response.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;


    /**
     * method for creating user ROLE
     * @param roleList list of user roles
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    @PostMapping("/role")
    public ResponseEntity<ApiResponse> create(@RequestBody List<ERole> roleList) throws Exception{
        List<Role> roleList1 = roleList
                .stream()
                .map(eRole -> Role.builder().name(eRole).build())
                .collect(Collectors.toList());
        ;
        return ResponseEntity.ok(ApiResponse
                .builder()
                .status(true)
                .timestamp(new Date())
                .body(roleRepository.saveAll(roleList1))
                .build());
    }


    /**
     * method for reset the password of user
     * @param foughtPassword FoughtPassword instance
     * @return ResponseEntity
     * @throws Exception throws Exception
     * @throws EntityNotFoundException throws EntityNotFoundException
     */
    @PostMapping("/fought")
    public ResponseEntity<ApiResponse> foughtPassword(@Valid @RequestBody FoughtPassword foughtPassword)
            throws Exception, EntityNotFoundException
    {

        User user = userService.getByEmail(foughtPassword.getEmail());
        if (user == null) {
            throw new EntityNotFoundException(User.class, " email ", foughtPassword.getEmail());
        }
        user.setPassword(passwordEncoder.encode(foughtPassword.getPassword()));

        return ResponseEntity.ok(ApiResponse
                .builder()
                .status(true)
                .timestamp(new Date())
                .message("register success")
                .body(userService.update(user.getId(), user))
                .build());
    }


    /**
     * method for register a user
     * @param registerRequest RegisterRequest instance
     * @return ResponseEntity
     * @throws Exception throws Exception
     * @throws EntityNotFoundException throws EntityNotFoundException
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest)
            throws Exception, EntityNotFoundException
    {

        User user = new User();
        user.setUserName(registerRequest.getEmail());
        user.setUserStatus(UserStatus.ACTIVATED);
        user.setPassword(registerRequest.getPassword());
        Set<Role> roleList = registerRequest
                .getRoleList()
                .stream()
                .map(eRole -> {
                    Optional<Role> byName = roleRepository.findByName(eRole);
                    if (byName.isPresent()) {
                        return byName.get();
                    } else {
                        throw new EntityNotFoundException(Role.class, "id", eRole.toString());
                    }
                })
                .collect(Collectors.toSet());
        user.setRoles(roleList);

        return ResponseEntity.ok(ApiResponse
                .builder()
                .status(true)
                .timestamp(new Date())
                .message("register success")
                .body(userService.addUser(user))
                .build());
    }


    /**
     * method for login
     * @param loginRequest LoginRequest request
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
            throws Exception
    {
        try {

            log.info("getPassword --- {}", passwordEncoder.encode(loginRequest.getPassword()));
            log.info("New user login attempt with {}", loginRequest.toString());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            log.info("authentication {}",usernamePasswordAuthenticationToken.toString());
            log.info("getCredentials {}",usernamePasswordAuthenticationToken.getCredentials());
            log.info("isAuthenticated {}",usernamePasswordAuthenticationToken.isAuthenticated());


            /**
             * check the username and the password
             */
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            /**
             * generating JWT token
             */
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            VehicleDriver byEmail = null;
            try {
                byEmail = driverService.getByEmail(userDetails.getUsername());
            }catch (Exception e){
                log.error("no driver fro {}", userDetails.getUserName());
            }
            return ResponseEntity.ok(ApiResponse
                    .builder()
                    .status(true)
                    .timestamp(new Date())
                    .message("Login success")
                    .body(new JwtResponse(jwt, userDetails.getId().toString(), userDetails.getUserName(), roles, byEmail))
                    .build());
        }catch (BadCredentialsException e){
            throw new InvalidCredentialException();
        }

    }

}
