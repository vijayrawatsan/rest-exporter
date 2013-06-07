package com.giftdiggers.rest.controller;

import javax.naming.AuthenticationException;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giftdiggers.core.domain.User;
import com.giftdiggers.core.domain.Wishlist;
import com.giftdiggers.core.util.GiftDiggersException;
import com.giftdiggers.service.UserService;
import com.giftdiggers.service.WishlistService;
import com.giftdiggers.spring.helper.Authenticated;
import com.giftdiggers.web.dto.GiftDiggerResponse;
import com.google.common.collect.ImmutableList;

@Controller
public class WishlistRestController {

	private final static Logger LOGGER = Logger.getLogger(WishlistRestController.class);
	
	@Autowired
	private WishlistService wishlistService;
	
	@Autowired
	private UserService userService;
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/add/{productSlug}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> addProductToWishlist(@PathVariable String productSlug, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			wishlistService.addProduct(user.getActiveWishListId(), productSlug);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null), HttpStatus.OK);
		} 
		catch (GiftDiggersException e) {
			LOGGER.error("Gift Digger Exception occurred. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) {
			LOGGER.error("Exception in adding product:"+productSlug+" to User Wishlist. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/remove/{productSlug}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> removeProductFromWishlist(@NotNull String wishlistId, @PathVariable String productSlug, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			wishlistService.removeProduct(wishlistId, productSlug);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null), HttpStatus.OK);
		} 
		catch (GiftDiggersException e) {
			LOGGER.error("Gift Digger Exception occurred. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) {
			LOGGER.error("Exception in removing product:"+productSlug+" from Wishlist:"+wishlistId+". Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> createWishlist(@Validated(Default.class) Wishlist wishlist, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			wishlist = wishlistService.save(wishlist);
			if(wishlist.isActive()){
				user.setActiveWishListId(wishlist.getId());
				userService.update(user);
			}
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, wishlist), HttpStatus.OK);
		}
		catch (Exception e) {
			LOGGER.error("Exception in saving the wishList. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> updateWishlist(@Validated(Default.class) Wishlist wishlist, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			wishlist = wishlistService.update(wishlist);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, wishlist), HttpStatus.OK);
		}
		catch (Exception e) {
			LOGGER.error("Exception in saving the wishList. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/active/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> activateWishlist(@PathVariable String id, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			if(!wishlistService.matchesUserEmail(id, user.getEmail())){
				throw new GiftDiggersException("Activate Wishlist for User: Wishlist userEmail should match User email");
			}
			user.setActiveWishListId(id);
			userService.update(user);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null), HttpStatus.OK);
		}
		catch (GiftDiggersException e) {
			LOGGER.error("Gift Digger Exception occurred. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) {
			LOGGER.error("Exception in saving the wishList. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/wishlist/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> deleteWishlist(@PathVariable String id, @Authenticated User user) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
		}
		catch (Exception e) {
			LOGGER.error("Exception in deleting the wishlist:"+id+". Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<GiftDiggerResponse> handleAuthenticationException(AuthenticationException ex) {
		return new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(false, ImmutableList.of(ex.getMessage()), null), HttpStatus.UNAUTHORIZED);
	}
	
}
