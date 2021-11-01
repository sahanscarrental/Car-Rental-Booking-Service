package com.car.castel.BookingService.service.auth;


import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.service.CRUDServices;

public interface UserService extends CRUDServices<User> {

    /**
     * Update the current email if the new email is differ from current one
     * @param newEmail the new emamil address
     * @param currentEmail the current emamil address
     * @return updated email address ( new email address)
     */
    String updateUserName(String newEmail, String currentEmail);

    User getByEmail(String email);

    Boolean isUserNameExist(String  userName);

    User addUser(User user);

    User createAdminUser();

}
