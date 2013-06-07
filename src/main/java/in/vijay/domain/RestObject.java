package in.vijay.domain;

import java.util.List;

public class RestObject {

	private String mapping;

	private RequestMethod requestMethod;
	
	private MediaType produces;
	
	private MediaType consumes;
	
	private List<String> pathVariables;

	private List<String> requestParameters;
	
	private List<String> headerValueConditions;

	private List<String> requestParamConditions;

	private RestObject() {
		mapping = "";
	}
	
	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public MediaType getProduces() {
		return produces;
	}

	public void setProduces(MediaType produces) {
		this.produces = produces;
	}

	public MediaType getConsumes() {
		return consumes;
	}

	public void setConsumes(MediaType consumes) {
		this.consumes = consumes;
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public void setPathVariables(List<String> pathVariables) {
		this.pathVariables = pathVariables;
	}

	public List<String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(List<String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public List<String> getHeaderValueConditions() {
		return headerValueConditions;
	}

	public void setHeaderValueConditions(List<String> headerValueConditions) {
		this.headerValueConditions = headerValueConditions;
	}

	public List<String> getRequestParamConditions() {
		return requestParamConditions;
	}

	public void setRequestParamConditions(List<String> requestParamConditions) {
		this.requestParamConditions = requestParamConditions;
	}

	@Override
	public String toString() {
		return "RestObject [mapping=" + mapping + ", requestMethod="
				+ requestMethod + ", produces=" + produces + ", consumes="
				+ consumes + ", pathVariables=" + pathVariables
				+ ", requestParameters=" + requestParameters
				+ ", headerValueConditions=" + headerValueConditions
				+ ", requestParamConditions=" + requestParamConditions + "]";
	}
	
	public static RestObject getRestObject() {
		return new RestObject();
	}
	
	public static RestObject copyOf(RestObject o) {
		RestObject copy = new RestObject();
		copy.setConsumes(o.getConsumes());
		copy.setHeaderValueConditions(o.getHeaderValueConditions());
		copy.setMapping(o.getMapping());
		copy.setPathVariables(o.getPathVariables());
		copy.setProduces(o.getProduces());
		copy.setRequestMethod(o.getRequestMethod());
		copy.setRequestParamConditions(o.getRequestParamConditions());
		copy.setRequestParameters(o.getRequestParameters());
		return copy;
	}
}
