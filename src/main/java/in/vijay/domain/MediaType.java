package in.vijay.domain;

/**
 * <p>
 * The <code>MediaType</code> enumeration contains values that can be used
 * within a request as the request's content type.
 * </p>
 * 
 * @see Request
 * @see Response
 */
public enum MediaType {
	  
	WILDCARD( "*/*" ),
	APPLICATION_XML( "application/xml" ),
	APPLICATION_ATOM_XML( "application/atom+xml" ),
	APPLICATION_XHTML_XML( "application/xhtml+xml" ),
	APPLICATION_SVG_XML( "application/svg+xml" ),
	APPLICATION_JSON( "application/json" ),
	APPLICATION_FORM_URLENCODED( "application/x-www-form-urlencoded" ),
	MULTIPART_FORM_DATA( "multipart/form-data" ),
	APPLICATION_OCTET_STREAM( "application/octet-stream" ),
	TEXT_PLAIN( "text/plain" ),
	TEXT_XML( "text/xml" ),
	TEXT_HTML( "text/html" );
	
	private final String mimeType;

	private MediaType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	/**
	 * <p>
	 * The <code>fromString()</code> method tries to convert a mime-type to the
	 * equivalent <code>MediaType</code>.
	 * </p>
	 */
	public static MediaType fromString(String type) {
		MediaType result = null;
		MediaType[] values = values();
		for (MediaType value : values) {
			if (value.getMimeType().equals(type)) {
				result = value;
			}
		}
		return result;
	}

	public static MediaType getMediaType(String method) {
		for (MediaType mediaType : MediaType.values()) {
			if (method.contains(mediaType.toString()) || method.contains(mediaType.getMimeType())) {
				return mediaType;
			}
		}
		return null;
	}
}