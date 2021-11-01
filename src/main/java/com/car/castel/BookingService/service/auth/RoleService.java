package com.car.castel.BookingService.service.auth;


import com.car.castel.BookingService.entity.auth.ERole;
import com.car.castel.BookingService.entity.auth.Role;
import com.car.castel.BookingService.service.CRUDServices;

import java.util.List;

public interface RoleService extends CRUDServices<Role> {
    List<Role> getAll();
    Role findByName(ERole eRole);
}
