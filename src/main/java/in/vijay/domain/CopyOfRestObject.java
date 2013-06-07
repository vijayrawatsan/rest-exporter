package in.vijay.domain;

import java.util.List;

public class CopyOfRestObject {

	private String mapping;

	private RequestMethod requestMethod;
	
	private MediaType produces;
	
	private MediaType consumes;
	
	private List<String> pathVariables;

	private List<String> requestParameters;
	
	private List<String> headerValueConditions;

	private List<String> requestParamConditions;

	public static class Builder {
		
		//Required
		private String mapping;
		
		//Optional
		private RequestMethod requestMethod;
		
		private MediaType produces;
		
		private MediaType consumes;
		
		private List<String> pathVariables;

		private List<String> requestParameters;
		
		private List<String> headerValueConditions;

		private List<String> requestParamConditions;
		
		public Builder(String mapping) {
			this.mapping = mapping;
		}
		
		public Builder requestMethod(RequestMethod requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}
		
		public Builder produces(MediaType produces) {
			this.produces = produces;
			return this;
		}
		
		public Builder consumes(MediaType consumes) {
			this.consumes= consumes;
			return this;
		}
		
		public Builder pathVariables(List<String> pathVariables) {
			this.pathVariables = pathVariables;
			return this;
		}
		
		public Builder requestParameters(List<String> requestParameters) {
			this.requestParameters = requestParameters;
			return this;
		}
		
		public Builder headerValueConditions(List<String> headerValueConditions) {
			this.headerValueConditions = headerValueConditions;
			return this;
		}
		
		public Builder requestParamConditions(List<String> requestParamConditions) {
			this.requestParamConditions = requestParamConditions;
			return this;
		}
		
		public CopyOfRestObject build() {
			return new CopyOfRestObject(this);
		}
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

	private CopyOfRestObject(Builder builder) {
		mapping = builder.mapping;
		requestMethod = builder.requestMethod;
		produces = builder.produces;
		consumes = builder.consumes;
		pathVariables = builder.pathVariables;
		requestParameters = builder.requestParameters;
		headerValueConditions = builder.headerValueConditions;
		requestParamConditions = builder.requestParamConditions;
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
	
}
