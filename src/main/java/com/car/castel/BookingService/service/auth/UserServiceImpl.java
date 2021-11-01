package com.car.castel.BookingService.service.auth;


import com.car.castel.BookingService.entity.auth.ERole;
import com.car.castel.BookingService.entity.auth.Role;
import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.entity.auth.UserStatus;
import com.car.castel.BookingService.repository.auth.RoleRepository;
import com.car.castel.BookingService.repository.auth.UserRepository;
import com.car.castel.BookingService.web.exception.BadRequestException;
import com.car.castel.BookingService.web.exception.EmailAlreadyExistException;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public
class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Lazy
    @Autowired
    PasswordEncoder encoder;

    private String adminEmail = "car.renting.banger@gmail.com";

    @Override
    public User create(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public User update(UUID uuid, User user) {
        Optional<User> user1 = userRepository.findById(uuid);
        if (user1.isPresent()){
            return userRepository.save(user);
        }else {
            throw new EntityNotFoundException(User.class,"id",uuid.toString());
        }
    }

    @Override
    public User get(UUID uuid) {
        Optional<User> user = userRepository.findById(uuid);
        if (user.isPresent()){
            return user.get();
        }else {
            throw new EntityNotFoundException(User.class,"id",uuid.toString());
        }
    }

    @Override
    public void delete(UUID uuid) {
        Optional<User> user = userRepository.findById(uuid);
        if (user.isPresent()){
            userRepository.delete(user.get());
        }else {
            throw new EntityNotFoundException(User.class,"id",uuid.toString());
        }
    }

    @Override
    public List<User> createAll(List<User> list) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }


    @Override
    public String updateUserName(String newEmail,String currentEmail) {
        if (currentEmail.equals(newEmail)){
            return newEmail;
        }
        Optional<User> user = userRepository.findByUserName(currentEmail);
        if (!user.isPresent()){
            throw new EntityNotFoundException(User.class,"email",currentEmail);
        }
        Optional<User> user2 = userRepository.findByUserName(newEmail);
        if (user2.isPresent() && !user2.get().getId().equals(user.get().getId())){
            throw new BadRequestException(User.class,"Email is already in use for email",newEmail);
        }
        if (!newEmail.equals(user.get().getUserName())){
            User user1 = user.get();
            user1.setUserName(newEmail);
            this.update(user1.getId(), user1);
            return newEmail;
        }
        return user.get().getUserName();
    }

    @Override
    public User getByEmail(String email) {
        Optional<User> byUserName = userRepository.findByUserName(email);
        return byUserName.get();
    }

    @Override
    public Boolean isUserNameExist(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    @Override
    public User addUser(User user) {
        if (this.isUserNameExist(user.getUserName())){
            throw new EmailAlreadyExistException();
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User createAdminUser() {
        User user = userRepository.findByUserName(adminEmail).orElse(null);
        if (user == null) {
            System.out.println("admin user not found, hence creating them");
            user = new User();
            user.setUserName(adminEmail);
            user.setUserStatus(UserStatus.ACTIVATED);
            Optional<Role> byName = roleRepository.findByName(ERole.ROLE_ADMIN);
            Set<Role> roles = new HashSet<Role>();
            if (byName.isPresent()) {
                roles.add(byName.get());
                user.setRoles(roles);
            } else {
                throw new EntityNotFoundException(Role.class, "id", ERole.ROLE_ADMIN.toString());
            }

            String encode = encoder.encode("1234Aa");
            user.setPassword(encode);
            return userRepository.save(user);
        } else {
            System.out.println("admin user is already created");
            return null;
        }
    }


}
