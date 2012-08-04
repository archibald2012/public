/**
 * 
 */
package edu.hziee.common.websocket.endpoint;

/**
 * @author wangqi
 * 
 */
public interface IEndpointChangeListener {

  void onCreate(Endpoint endpoint);
  void onStop(Endpoint endpoint);
}
