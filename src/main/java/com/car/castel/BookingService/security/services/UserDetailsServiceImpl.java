package com.car.castel.BookingService.security.services;

import com.car.castel.BookingService.entity.auth.User;
import com.car.castel.BookingService.repository.auth.UserRepository;
import com.car.castel.BookingService.web.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		User user = userRepository
                .findByUserName(email)
				.orElseThrow(() -> new EntityNotFoundException(User.class,"User Not Found with username",email));
		return UserDetailsImpl.build(user);
	}

}
