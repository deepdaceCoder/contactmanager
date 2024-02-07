package com.scm.constants;

import java.util.ArrayList;
import java.util.Arrays;

public class UrlConstants {
	
	public static final String HOME_PAGE = "/";
	public static final String ABOUT = "/about";
	public static final String SIGNIN = "/signin";
	public static final String SIGNUP = "/signup";
	public static final String REGISTER = "/register";
	public static final String USER_INDEX = "/user/index";
	public static final String FORGOT_PASSWORD = "/forgot";
	public static final ArrayList<String> URL_LIST = new ArrayList<String>(
														Arrays.asList(new String[]{HOME_PAGE, ABOUT,
																SIGNIN, SIGNUP, REGISTER, FORGOT_PASSWORD}));
	
}
