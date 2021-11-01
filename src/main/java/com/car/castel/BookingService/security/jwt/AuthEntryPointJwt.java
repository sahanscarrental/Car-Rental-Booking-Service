package com.car.castel.BookingService.security.jwt;

import com.car.castel.BookingService.web.apierror.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);


	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException
	{
		final String expired = (String) request.getAttribute("expired");
		if (expired!=null){
			ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
			apiError.setMessage("Full authentication is required to access this resource");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, apiError.toString());
		}else{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Login details");
		}

		logger.error("Unauthorized error: {}", authException.getMessage());
//		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Credential");
	}

}
