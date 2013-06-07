package com.giftdiggers.rest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.giftdiggers.core.domain.EmailVerification;
import com.giftdiggers.core.domain.User;
import com.giftdiggers.core.helper.LoginUserValidation;
import com.giftdiggers.core.helper.UpdateUserInfoValidation;
import com.giftdiggers.core.util.GiftDiggersConstants;
import com.giftdiggers.core.util.GiftDiggersException;
import com.giftdiggers.core.util.SecurityUtil;
import com.giftdiggers.service.EmailService;
import com.giftdiggers.service.EmailVerificationService;
import com.giftdiggers.service.ProductService;
import com.giftdiggers.service.UserService;
import com.giftdiggers.spring.helper.Authenticated;
import com.giftdiggers.web.dto.FriendDTO;
import com.giftdiggers.web.dto.FriendWrapper;
import com.giftdiggers.web.dto.GiftDiggerResponse;
import com.giftdiggers.web.dto.ResetPasswordDTO;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping("/user")
public class UserRestController {

	protected static Logger logger = Logger.getLogger("UserRestController");

	private List<FriendDTO> friendsList = new ArrayList<FriendDTO>();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailVerificationService emailVerificationService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ProductService productService;
	
	@ResponseBody
	@RequestMapping(value = "/dig/{productSlug}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> addDig(@PathVariable String productSlug, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			productService.incrementDigsCount(productSlug);
			userService.pushDigs(user.getEmail(), productSlug);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null),	HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@ResponseBody
	@RequestMapping(value = "/dig/{productSlug}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> deleteDig(@PathVariable String productSlug, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
				productService.decrementDigsCount(productSlug);
				userService.pullDigs(user.getEmail(), productSlug);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null),	HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	/**
	 * This method updates user's firstName and lastName as of now. Later on other fields might be added.
	 * @param user
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	@RequestMapping(value="/home/updateuserinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<GiftDiggerResponse> updateUserInfo(@Authenticated User user, @RequestBody @Validated(value=UpdateUserInfoValidation.class) User userData, 
			BindingResult bindingResult) {
		ResponseEntity<GiftDiggerResponse> response = null;
		if(bindingResult.hasErrors()) {
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, user),  HttpStatus.BAD_REQUEST);
			//to be able to restore the original state on client side---need to think more on this
        } else {
			try {
				User updatedUser = userService.updateFirstNameAndLastName(user, userData.getFirstName(), userData.getLastName());
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, updatedUser), HttpStatus.OK);
				
			} catch (Exception e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), 
						HttpStatus.INTERNAL_SERVER_ERROR);
			
			}
		}
		return response;
	}
	
	@RequestMapping(value="/home/updatepassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<GiftDiggerResponse> updatePassword(@Authenticated User user, @RequestBody @Validated(value=Default.class) ResetPasswordDTO passwordInfo, BindingResult bindingResult) {
		ResponseEntity<GiftDiggerResponse> response = null;
		if(bindingResult.hasErrors()) {
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, null),  HttpStatus.BAD_REQUEST);
			//to be able to restore the original state on client side---need to think more on this
        } else {
			try {
				boolean passwordUpdated = userService.updatePassword(user, passwordInfo.getOldPassword(), passwordInfo.getNewPassword());
				if(passwordUpdated) {
					response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null), HttpStatus.OK);
				} else {
					response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of("Wrong password"), null),  HttpStatus.BAD_REQUEST);
				}
				
			} catch (Exception e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), 
						HttpStatus.INTERNAL_SERVER_ERROR);
			
			}
		}
		return response;
	}
	
	/**
	 * Add a new USER.
	 * @param user
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> signup(@Validated(value=Default.class) User user, BindingResult bindingResult) {
		ResponseEntity<GiftDiggerResponse> response = null;
		if(bindingResult.hasErrors()){
			response = createErrorResponse(bindingResult);
        } else {
			try {
				//TODO - Take inputs from team
				// User state :=> logged in but not verified
				user = userService.signUp(user);
				EmailVerification emailVerification = emailVerificationService.save(new EmailVerification(new Date(), user.getEmail()));
				logger.info("Email verification id " +emailVerification.getId());
				//TODO - email service not implemented
				emailService.sendMailForEmailVerification(emailVerification.getId(), emailVerification.getEmail());
				HttpHeaders requestHeaders = storeUserSignatureInCookie(user.getPersistentCookie());
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, user), requestHeaders, HttpStatus.CREATED);
			} catch (GiftDiggersException e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.CONFLICT);
			} 
			catch (Exception e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
		return response;
	}
	
	/**
	 * For login we will not be using header signature instead we will use the full proof findByEmailAndPassword
	 * This will return a logged in user who may or may not be verified
     * LOGIN an existing USER.
     * @param user
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftDiggerResponse> login(@Validated(value=LoginUserValidation.class) User user, BindingResult bindingResult) {
    		ResponseEntity<GiftDiggerResponse> response = null;
    		if(bindingResult.hasErrors()) {
    			response = createErrorResponse(bindingResult);
            } else {
	            try {
	                user = userService.findByEmailAndPassword(user.getEmail(), SecurityUtil.convertToSHA256(user.getPassword()));
	                if(user != null) {
	                	user = userService.saveWithNewPersistentCookie(user);
	                	HttpHeaders requestHeaders = storeUserSignatureInCookie(user.getPersistentCookie());
	        			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, user), requestHeaders, HttpStatus.OK);
	                } else {
	                	response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of("Authentication failed"), null), HttpStatus.UNAUTHORIZED);
	                }
	            } catch (Exception e) {
	            	logger.error(e);
	    			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
	            }
            }
            return response;
    }
	
	/**
	 * Check if a user is already logged in.
	 * @param user
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> isLoggedIn(@CookieValue(value=GiftDiggersConstants.USER_SESSION_ID, required=false) String persistentCookie) {
		ResponseEntity<GiftDiggerResponse> response = null;
		if(StringUtils.isBlank(persistentCookie)){
			return new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, null), HttpStatus.OK);
		}
		try {
			User user = userService.findByPersistentCookie(persistentCookie);
			if(user != null) {
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, user), HttpStatus.OK);
			} else {
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of("Authentication failed"), null), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(ex.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/fb-login", method = RequestMethod.POST)
	public ResponseEntity<GiftDiggerResponse> fbLogin(User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		User userFB = userService.findByFacebookAccessToken(user.getFacebookAccessToken());
		if(userFB == null) {	// This control flow is executed when a new user logs in via Facebook or 
								// an existing user logs in whose facebook access token has expired.
			try {
				if(isValidFacebookUser(user)) {
					User userFromDB = userService.findByEmail(user.getEmail());
					if(userFromDB != null) {	// This is executed when the facebook access token expires
						userFromDB.setFacebookAccessToken(user.getFacebookAccessToken());
						userFB = userService.updateExistingUser(userFromDB);
					} else {	// This is executed for a FIRST TIMER user.
						user.setPassword(UUID.randomUUID().toString());
						user.setVerified(true);
						userFB = userService.saveNewUser(user);
					}
					HttpHeaders requestHeaders = storeUserSignatureInCookie(userFB.getPersistentCookie());
					response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, userFB), requestHeaders, HttpStatus.OK);
				}
				else {
					throw new GiftDiggersException("Invalid Facebook user");
				}
			} catch (GiftDiggersException e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.UNAUTHORIZED);
			} catch (Exception e) {
				logger.error(e);
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {	// This else defines the flow for an existing user.
			userFB = userService.updateExistingUser(userFB);
			HttpHeaders requestHeaders = storeUserSignatureInCookie(userFB.getPersistentCookie());
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, userFB), requestHeaders, HttpStatus.OK);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getFacebookFriends", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> getFacebookFriends(@Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		RestTemplate restTemplate = new RestTemplate();
		List<FriendWrapper> friendWrapperList = new ArrayList<FriendWrapper>();
		FriendDTO friendDTO = null;
		friendsList.clear();
		friendWrapperList.clear();
		try {
			String event = restTemplate.getForObject("https://graph.facebook.com/me?fields=friends.fields(id,birthday,name,gender)&access_token="+user.getFacebookAccessToken(), String.class);
			JSONObject result = new JSONObject(event);
			JSONObject friends = result.getJSONObject("friends");
			JSONArray friendsArray = friends.getJSONArray("data");
			for (int i = 0; i < friendsArray.length(); ++i) {
			    JSONObject record = friendsArray.getJSONObject(i);
			    String[] date = record.has("birthday")?record.getString("birthday").split("\\/"):"01-01".split("-");
		        friendDTO = new FriendDTO();
			    friendDTO.setBirthday(new LocalDate(1990, Integer.parseInt(date[0]), Integer.parseInt(date[1]) ));
			    friendDTO.setFacebookID(record.getString("id"));
			    friendDTO.setName(record.getString("name"));
			    friendDTO.setGender(record.has("gender")?record.getString("gender"):"");
			    friendWrapperList.add(new FriendWrapper(friendDTO));
			    friendsList.add(friendDTO);
			}
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, friendWrapperList), null, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error(e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	
	@ResponseBody
	@RequestMapping(value = "/getFriendsBirthday", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> getFriendsBirthday(@Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		List<FriendDTO> birthdayList = new ArrayList<FriendDTO>();
		try {
	    for (int i=0; i<friendsList.size();i++) {
	    	FriendDTO friend = friendsList.get(i);
			if( ( friend.getBirthday().getMonthOfYear() == LocalDate.now().getMonthOfYear() 
					&&  friend.getBirthday().getDayOfMonth() > LocalDate.now().getDayOfMonth())
						||  (friend.getBirthday().getMonthOfYear() > LocalDate.now().getMonthOfYear()) ) {	
					birthdayList.add(friend);
				}
	    }
		Collections.sort(birthdayList, new Comparator<FriendDTO>() {
			public int compare(final FriendDTO object1,
					final FriendDTO object2) {
				return object1.getBirthday().compareTo(
						object2.getBirthday());
			}
		});
	    response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, birthdayList), null, HttpStatus.OK);
		} catch (Exception e) {
		logger.error(e);
		response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	
	/**
	 * logs out a user by expiring persistent cookie.
	 * @param user
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> logOut() {
		HttpHeaders requestHeaders = expireUserSignatureInCookie();
		ResponseEntity<GiftDiggerResponse> response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(true, null , new User()), requestHeaders, HttpStatus.OK);
		return response;
	}
	
	@ResponseBody
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<GiftDiggerResponse> handleAuthenticationException(AuthenticationException ex) {
		return new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(false, ImmutableList.of(ex.getMessage()), null), HttpStatus.UNAUTHORIZED);
	}
	
	// using the password field to store access token because password will be null anyways in case of a FB login
	private boolean isValidFacebookUser(User user) {
		//TODO - Externalise restTemplate in appcontext
		RestTemplate restTemplate = new RestTemplate();
		User fbUser = restTemplate.getForEntity("https://graph.facebook.com/me?access_token="+user.getFacebookAccessToken(), User.class).getBody();
		logger.info("Response recieved from Facebook for access token = "+user.getFacebookAccessToken()+", Email Response= :"+fbUser.getEmail());
		return user.getEmail().equals(fbUser.getEmail());
	}
	
	private ResponseEntity<GiftDiggerResponse> createErrorResponse(BindingResult bindingResult) {
		/*List<FieldError> errors = bindingResult.getFieldErrors();
	    for (FieldError error : errors ) {
	        System.out.println (error.getObjectName() + " - " + error.getDefaultMessage());
	    }*/
		//TODO fill in apt error messages
		return new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, null),  HttpStatus.BAD_REQUEST);
	}
	
	private HttpHeaders storeUserSignatureInCookie(String persistentCookie) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Set-Cookie", GiftDiggersConstants.USER_SESSION_ID + "=" + persistentCookie);
		return requestHeaders;
	}
	
	private HttpHeaders expireUserSignatureInCookie(){
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Set-Cookie", GiftDiggersConstants.USER_SESSION_ID + "=" + ";Expires=Thu, 01 Jan 1970 00:00:00 GMT");
		return requestHeaders;
	}
}
