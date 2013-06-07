package com.giftdiggers.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giftdiggers.core.domain.Product;
import com.giftdiggers.core.helper.AgeGroup;
import com.giftdiggers.core.helper.Interest;
import com.giftdiggers.core.helper.Occasion;
import com.giftdiggers.core.helper.Price;
import com.giftdiggers.core.helper.Relationship;
import com.giftdiggers.core.helper.SearchMapping;
import com.giftdiggers.service.ProductService;
import com.giftdiggers.web.dto.GiftDiggerResponse;

@Controller
public class ProductRestController {

	private static final Logger logger = Logger.getLogger("ProductRestController");

	@Autowired
	private ProductService productService;

	@ResponseBody
	@RequestMapping(value = "/product/{slug}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GiftDiggerResponse> getProduct(@PathVariable String slug) {
		ResponseEntity<GiftDiggerResponse> response = null;
		try {
			Product product = productService.findBySlug(slug);
			if (null != product) {
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.TRUE, null, product),	HttpStatus.OK);
			} else {
				response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, null),	HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			logger.error(e);
			response = new ResponseEntity<GiftDiggerResponse>(new GiftDiggerResponse(Boolean.FALSE, null, null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/products/popular", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> getPopularProducts() {
		//TODO - Remove this waiting code
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Product> products = productService.findPopularProducts();
		return products;
	}

	@ResponseBody
	@RequestMapping(value = "/gifts/{rel}/{ocsn}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> searchFor(@PathVariable String rel, @PathVariable String ocsn) {
		logger.info("Search Gifts for:" + rel+", " + ocsn);
		Occasion occasion = Occasion.getByName(ocsn);
		Relationship relationship = Relationship.getByName(rel);
		
		if(relationship == null || occasion == null) {
			logger.error("either is null");
			return new ArrayList<Product>();
		}
		
		SearchMapping searchMapping = new SearchMapping(relationship, occasion);
		return getProducts(searchMapping, 0, 10);
	}

	@ResponseBody
	@RequestMapping(value = "/gifts/{rel}/{ocsn}/{age}/{priceRange}/{interests}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> searchFor(@PathVariable String rel, @PathVariable String ocsn, @PathVariable String age, 
									@PathVariable String priceRange, @PathVariable String interests) {
		logger.info("Search Gifts with filters: Relation = " + rel +", Occasion = " + ocsn + ", Age = "+ age + ", Price = "+ priceRange +", Interests = " + interests);
		Occasion occasion = Occasion.getByName(ocsn);
		
		Relationship relationship = Relationship.getByName(rel);
		
		AgeGroup ageGroup = AgeGroup.getByDisplayName(age);
		
		interests = interests.substring(6); //removing 'loves:'
		List<Interest> interestList = new ArrayList<Interest>();
		for(String interest : interests.split(";")) {
			Interest interestEnum = Interest.getByName(interest);
			if(interestEnum != null)
				interestList.add(interestEnum);
		}
		
		Price price = Price.fromPriceRange(priceRange);
		
		SearchMapping searchMapping = new SearchMapping(ageGroup, relationship, occasion, interestList, price);
		return getProducts(searchMapping, 0, 10);
	}
	 
	@ResponseBody
	@RequestMapping(value = "/gifts/{first}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> searchFor(@PathVariable String first) {
		logger.info("Search Gifts for single parameter:"+first);
		Relationship relationship = Relationship.getByName(first);
		Occasion occasion = relationship == null ? Occasion.getByName(first): null;
		if(relationship == null && occasion == null) {
			logger.error("BAD request");
			return new ArrayList<Product>();
		}
		SearchMapping searchMapping = new SearchMapping(relationship, occasion);
		return getProducts(searchMapping, 0, 10);
	}
	
	@ResponseBody
	@RequestMapping(value = "/relationship", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Relationship> getRelationShips() {
		return null;
		
	}

	private List<Product> getProducts(SearchMapping searchMapping, int pageNum, int pageSize) {
		logger.info("Searching for: " + searchMapping);
		return productService.findBySearchMapping(searchMapping, pageNum, pageSize);
	}
}
