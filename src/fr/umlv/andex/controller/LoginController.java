package fr.umlv.andex.controller;

import fr.umlv.andex.data.User;

public class LoginController {

	public User findUserByUserAndPassword(String user, String password){
		User userO = new User();
		userO.setUserId(1);
		return userO;
	}
}
