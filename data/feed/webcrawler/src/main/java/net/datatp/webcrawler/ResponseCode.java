package net.datatp.webcrawler;

import java.util.HashMap;

/**
 * For more detail about the http response code http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jun 30, 2010  
 */
public class ResponseCode {
  final static public short NONE = 0 ;
  
  //Informational 1xx
  final static public short CONTINUE = 100 ;
  final static public short SWITCHING_PROTOCOLS = 101 ;
  
  //Successful 2xx
  final static public short OK = 200 ;
  final static public short CREATED = 201 ;
  final static public short ACCEPTED = 202 ;
  final static public short NON_AUTHORITATIVE_INFORMATION = 203 ;
  final static public short NO_CONTENT = 204 ;
  final static public short RESET_CONTENT = 205 ;
  final static public short PARTIAL_CONTENT = 206 ;
  
  //Redirection 3xx
  final static public short MULTIPLE_CHOICES = 300 ;
  final static public short MOVED_PERMANENTLY = 301 ;
  final static public short FOUND = 302 ;
  final static public short SEE_OTHER = 303 ;
  final static public short NOT_MODIFIED = 304 ;
  final static public short USE_PROXY = 305 ;
  final static public short UNUSED = 306 ;
  final static public short TEMPORARY_REDIRECT = 307 ;
  
  //Client error 4xx
  final static public short BAD_REQUEST = 400 ;
  final static public short UNAUTHORIZED = 401 ;
  final static public short PAYMENT_REQUIRED = 402 ;
  final static public short FORBIDDEN = 403 ;
  final static public short NOT_FOUND = 404 ;
  final static public short METHOD_NOT_ALLOWED = 405 ;
  final static public short NOT_ACCEPTABLE = 406 ;
  final static public short PROXY_AUTHENTICATION_REQUIRED = 407 ;
  final static public short REQUEST_TIMEOUT = 408 ;
  final static public short CONFLICT = 409 ;
  final static public short GONE = 410 ;
  final static public short LENGTH_REQUIRED = 411 ;
  final static public short PRECONDITION_FAILED = 412 ;
  final static public short REQUEST_ENTIY_TOO_LARGE = 413 ;
  final static public short REQUEST_URI_TOO_LONG = 414 ;
  final static public short UNSUPPORTED_MEDIA_TYPE = 415 ;
  final static public short REQUESTED_RANGE_NOT_SATISFIABLE = 416 ;
  final static public short EXPECTATION_FAILED = 417 ;
  
  //Server error 5xx
  final static public short INTERNAL_SERVER_ERROR = 500 ;
  final static public short NOT_IMPLEMENTED = 501 ;
  final static public short BAD_GATEWAY = 502 ;
  final static public short SERVICE_UNAVAILABLE = 503 ;
  final static public short GATEWAY_TIMEOUT = 504 ;
  final static public short HTTP_VERSION_NOT_SUPPORTED = 505 ;
  
  final static public short UNKNOWN_ERROR = 10000 ;
  final static public short CONNECTION_TIMEOUT = 10001 ;
  final static public short ILLEGAL_URI = 10002 ;
  
  static HashMap<Short, ResponseCode> codes = new HashMap<Short, ResponseCode>() ;
  static {
    addResponseCode(NONE, "response code none") ;
    //Informational 1xx
    addResponseCode(CONTINUE, "100 Continue, the client SHOULD continue with its request. This interim response is used to inform the client that the initial part of the request has been received and has not yet been rejected by the server") ;
    addResponseCode(SWITCHING_PROTOCOLS, "The server understands and is willing to comply with the client's request, via the Upgrade message header field, for a change in the application protocol being used on this connection") ;
    //Successful 2xx
    addResponseCode(OK, "The request has succeeded") ;
    addResponseCode(CREATED, "The request has been fulfilled and resulted in a new resource being created") ;
    addResponseCode(ACCEPTED, "The request has been accepted for processing, but the processing has not been completed") ;
    addResponseCode(NON_AUTHORITATIVE_INFORMATION, "The returned metainformation in the entity-header is not the definitive set as available from the origin server, but is gathered from a local or a third-party copy") ;
    addResponseCode(NO_CONTENT, "The server has fulfilled the request but does not need to return an entity-body, and might want to return updated metainformation") ;
    addResponseCode(RESET_CONTENT, "The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent") ;
    addResponseCode(PARTIAL_CONTENT, "The server has fulfilled the partial GET request for the resource") ;
    //Redirection 3xx
    addResponseCode(MULTIPLE_CHOICES, "The requested resource corresponds to any one of a set of representations, each with its own specific location, and agent- driven negotiation information is being provided so that the user (or user agent) can select a preferred representation and redirect its request to that location") ;
    addResponseCode(MOVED_PERMANENTLY, "The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs") ;
    addResponseCode(FOUND, "The requested resource resides temporarily under a different URI") ;
    addResponseCode(SEE_OTHER, "The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource") ;
    addResponseCode(NOT_MODIFIED, "If the client has performed a conditional GET request and access is allowed, but the document has not been modified, the server SHOULD respond with this status code") ;
    addResponseCode(USE_PROXY, "The requested resource MUST be accessed through the proxy given by the Location field") ;
    addResponseCode(UNUSED, "The 306 status code was used in a previous version of the specification, is no longer used, and the code is reserved") ;
    addResponseCode(TEMPORARY_REDIRECT, "The requested resource resides temporarily under a different URI") ;
    //Client error 4xx
    addResponseCode(BAD_REQUEST, "The request could not be understood by the server due to malformed syntax") ;
    addResponseCode(UNAUTHORIZED, "The request requires user authentication") ;
    addResponseCode(PAYMENT_REQUIRED, "This code is reserved for future use") ;
    addResponseCode(FORBIDDEN, "The server understood the request, but is refusing to fulfill it. Authorization will not help and the request SHOULD NOT be repeated") ;
    addResponseCode(NOT_FOUND, "The server has not found anything matching the Request-URI") ;
    addResponseCode(METHOD_NOT_ALLOWED, "The method specified in the Request-Line is not allowed for the resource identified by the Request-URI") ;
    addResponseCode(NOT_ACCEPTABLE, "The resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request") ;
    addResponseCode(PROXY_AUTHENTICATION_REQUIRED, "This code is similar to 401 (Unauthorized), but indicates that the client must first authenticate itself with the proxy") ;
    addResponseCode(REQUEST_TIMEOUT, "The client did not produce a request within the time that the server was prepared to wait") ;
    addResponseCode(CONFLICT, "The request could not be completed due to a conflict with the current state of the resource") ;
    addResponseCode(GONE, "The requested resource is no longer available at the server and no forwarding address is known") ;
    addResponseCode(LENGTH_REQUIRED, "The server refuses to accept the request without a defined Content- Length") ;
    addResponseCode(PRECONDITION_FAILED, "The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server") ;
    addResponseCode(REQUEST_ENTIY_TOO_LARGE, "The server is refusing to process a request because the request entity is larger than the server is willing or able to process") ;
    addResponseCode(REQUEST_URI_TOO_LONG, "The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret") ;
    addResponseCode(UNSUPPORTED_MEDIA_TYPE, "The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method") ;
    addResponseCode(REQUESTED_RANGE_NOT_SATISFIABLE, "A server SHOULD return a response with this status code if a request included a Range request-header field, and none of the range-specifier values in this field overlap the current extent of the selected resource, and the request did not include an If-Range request-header field") ;
    addResponseCode(EXPECTATION_FAILED, "The expectation given in an Expect request-header field could not be met by this server, or, if the server is a proxy, the server has unambiguous evidence that the request could not be met by the next-hop server") ;
    //Server error 5xx
    addResponseCode(INTERNAL_SERVER_ERROR, "The server encountered an unexpected condition which prevented it from fulfilling the request") ;
    addResponseCode(NOT_IMPLEMENTED, "The server does not support the functionality required to fulfill the request") ;
    addResponseCode(BAD_GATEWAY, "The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request") ;
    addResponseCode(SERVICE_UNAVAILABLE, "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server") ;
    addResponseCode(GATEWAY_TIMEOUT, "The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server specified by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) it needed to access in attempting to complete the request") ;
    addResponseCode(HTTP_VERSION_NOT_SUPPORTED, "The server does not support, or refuses to support, the HTTP protocol version that was used in the request message") ;
  }
  
  final public  short code ;
  final public  String description ;
  
  private ResponseCode(short code, String desc) {
    this.code = code ;
    this.description = desc; 
  }

  final static public ResponseCode get(short code) {
    ResponseCode rc =  codes.get(code) ;
    if(rc == null) {
      rc = new ResponseCode(code, "unknow response code") ;
      codes.put(code, rc) ;
    }
    return rc ;
  }
  
  final static public boolean isIn4XXGroup(short code) {
    return code >= 400 && code < 500 ;
  }
  
  final static public boolean isIn3XXGroup(short code) {
    return code >= 300 && code < 400 ;
  }
  
  static private void addResponseCode(short code, String desc) {
    codes.put(code, new ResponseCode(code, desc)) ;
  }
}
