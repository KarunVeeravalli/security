package com.clayfin.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clayfin.enums.ERole;
import com.clayfin.enums.Status;
import com.clayfin.model.Role;
import com.clayfin.model.UserLoginProfile;
import com.clayfin.repository.RoleRepository;
import com.clayfin.repository.UserLoginProfileRepository;
import com.clayfin.request.dto.LoginRequest;
import com.clayfin.request.dto.SignupRequest;
import com.clayfin.security.util.JwtUtils;
import com.clayfin.service.UserLoginProfileException;
import com.clayfin.service.UserLoginProfileService;


@Service
public class UserLoginProfileServiceImpl implements UserLoginProfileService{
	
	  @Autowired
	  AuthenticationManager authenticationManager;
	  
	  @Autowired
	  UserLoginProfileRepository userRepository;

	  @Autowired
	  RoleRepository roleRepository;

	  @Autowired
	  PasswordEncoder encoder;
	  

	  @Autowired
	  JwtUtils jwtUtils;

	@Override
	public Status register(SignupRequest signUpRequest) throws UserLoginProfileException {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
		      throw new UserLoginProfileException("Error: Username is already taken!");
		    }

		    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
		    	throw new UserLoginProfileException("Error: Email is already in use!");
		    }

		    // Create new user's account
		    UserLoginProfile user = new UserLoginProfile(signUpRequest.getUsername(), 
		               signUpRequest.getEmail(),
		               encoder.encode(signUpRequest.getPassword()));

		    Set<String> strRoles = signUpRequest.getRole();
		    Set<Role> roles = new HashSet<>();

		    if (strRoles == null) {
		      Role userRole = roleRepository.findByName(ERole.ROLE_USER);
		          if(userRole==null) {
		        	  new RuntimeException("Error: Role is not found.");
		          }
		      roles.add(userRole);
		    } else {
		      strRoles.forEach(role -> {
		        switch (role) {
		        case "admin":
		          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
		        		  if(adminRole==null) {
		                	  new RuntimeException("Error: Role is not found.");
		                  }
		          roles.add(adminRole);

		          break;
		        case "manager":
		          Role modRole = roleRepository.findByName(ERole.ROLE_MANAGER);
		        		  if(modRole==null) {
		                	  new RuntimeException("Error: Role is not found.");
		                  }
		          roles.add(modRole);

		          break;
		        default:
		          Role userRole = roleRepository.findByName(ERole.ROLE_USER);
		          if(userRole==null) {
		        	  new RuntimeException("Error: Role is not found.");
		          }
		          roles.add(userRole);
		        }
		      });
		    }

		    user.setRoles(roles);
		    System.out.println(user.getRoles());
		    userRepository.save(user);

		return Status.CREATED;
	}

	@Override
	public String login(LoginRequest loginRequest) throws UserLoginProfileException {
		 Authentication authentication = authenticationManager.authenticate(
			        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			    SecurityContextHolder.getContext().setAuthentication(authentication);
			    String jwt = jwtUtils.generateJwtToken(authentication);   
		return jwt;
	}

	@Override
	public List<String> getAllUserNames() throws UserLoginProfileException {
		List<UserLoginProfile> users = userRepository.findAll();
		List<String> names = new ArrayList<>();
		for(UserLoginProfile i : users) {
			names.add(i.getUsername());
		}
		return names;
	}

}
