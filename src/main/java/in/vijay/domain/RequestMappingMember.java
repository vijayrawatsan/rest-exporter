package in.vijay.domain;

import java.util.List;

public enum RequestMappingMember {

	VALUE("value") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			restObject.setMapping(restObject.getMapping() + value.replace("\"", ""));
			
		}
	}, METHOD("method") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			restObject.setRequestMethod(RequestMethod.getRequestMethod(value));
			
		}
	}, CONSUMES("consumes") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			restObject.setConsumes(MediaType.getMediaType(value));
		}
	}, PRODUCES("produces") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			restObject.setProduces(MediaType.getMediaType(value));			
		}
	}, HEADERS("headers") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			String[] nameValues = value.split(",");
			List<String> headerValueConditions = GeneralUtil.getCombinedNameValuePairs(restObject.getHeaderValueConditions(), nameValues); 
			restObject.setHeaderValueConditions(headerValueConditions);
		}

	}, PARAMS("params") {
		@Override
		public void updateRestObject(String value, RestObject restObject) {
			String[] nameValues = value.split(",");
			List<String> requestParamConditions = GeneralUtil.getCombinedNameValuePairs(restObject.getHeaderValueConditions(), nameValues); 
			restObject.setRequestParamConditions(requestParamConditions);
		}
	};
	
	private String name;

	private RequestMappingMember(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public abstract  void updateRestObject(String value, RestObject restObject);
}
