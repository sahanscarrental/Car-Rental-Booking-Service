package com.car.castel.BookingService.service.auth;

import com.car.castel.BookingService.entity.auth.ERole;
import com.car.castel.BookingService.entity.auth.Role;
import com.car.castel.BookingService.repository.auth.RoleRepository;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role create(Role entity) {
        return roleRepository.save(entity);
    }

    @Override
    public Role update(UUID uuid, Role role) {
        Optional<Role> role1 = roleRepository.findById(uuid);
        if (role1.isPresent()) {
            return roleRepository.save(role);
        }
        throw  new EntityNotFoundException(Role.class,"id",uuid.toString());
    }

    @Override
    public Role get(UUID uuid) {
        Optional<Role> role = roleRepository.findById(uuid);
        if (role.isPresent()) {
            return role.get();
        }
        throw  new EntityNotFoundException(Role.class,"id",uuid.toString());
    }

    @Override
    public void delete(UUID uuid) {
        Optional<Role> role = roleRepository.findById(uuid);
        role.ifPresent(value -> roleRepository.delete(value));
        throw  new EntityNotFoundException(Role.class,"id",uuid.toString());
    }

    @Override
    public List<Role> createAll(List<Role> list) {
        return null;
    }



    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(ERole eRole) {
        Optional<Role> role = roleRepository.findByName(eRole);
        if (role.isPresent()) {
            return role.get();
        }
        throw  new EntityNotFoundException(Role.class,"id",role.toString());
    }
}
