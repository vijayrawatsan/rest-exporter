package com.giftdiggers.rest.controller;

import javax.naming.AuthenticationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giftdiggers.core.domain.Comment;
import com.giftdiggers.core.domain.User;
import com.giftdiggers.core.util.GiftDiggersException;
import com.giftdiggers.service.CommentService;
import com.giftdiggers.spring.helper.Authenticated;
import com.giftdiggers.web.dto.GiftDiggerResponse;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping(value = "/comment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers="name1=value1,name2=value2")
public class CommentRestController {

	protected static Logger logger = Logger.getLogger("CommentRestController");
	
	@Autowired
	private CommentService commentService;
	
	/**
	 * This will add a comment.
	 * @param comment
	 * @throws GiftDiggersException
	 */
	@ResponseBody
	//@RequestMapping(value = "/comment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping("/comment")
	public ResponseEntity<GiftDiggerResponse> addComment(Comment comment, @Authenticated User user) {
		logger.info("comment:"+comment);
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			comment.setFullName(user.getFirstName() + " " + user.getLastName());
			comment.setUserEmail(user.getEmail());
			comment.setUserThumbnailImageUrl(user.getThumbNailImageUrl());
			commentService.save(comment);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, comment), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in saving the comment. Might have added in comment collection but not in products or vice versa. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	/**
	 * This will delete a comment.
	 * @param comment
	 * @throws GiftDiggersException
	 */
	@ResponseBody
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> deleteComment(@PathVariable String id, @Authenticated User user, @RequestParameter String vijay) {
		logger.info("commentId:"+id);
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			commentService.delete(id);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, null), HttpStatus.OK);
		} catch (GiftDiggersException e) {
			logger.error("Gift Digger Exception occurred. Error message is: " + e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, ImmutableList.of(e.getMessage()), null), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) {
			logger.error("Exception in saving the comment. Might have added in comment collection but not in products or vice versa. Error message is: " + e);
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
