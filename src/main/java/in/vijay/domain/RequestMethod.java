package in.vijay.domain;

public enum RequestMethod {
    GET, POST, DELETE, HEAD, PUT;
    
    public static RequestMethod getRequestMethod(String method) {
    	for(RequestMethod requestMethod : RequestMethod.values()) {
    		if(method.contains(requestMethod.toString())) {
    			return requestMethod;
    		}
    	}
    	return null;
    }
}
