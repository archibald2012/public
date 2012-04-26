/**
 * 
 */
package edu.hziee.common.tcp.endpoint;

/**
 * @author wangqi
 * 
 */
public interface IEndpointChangeListener {

  void onCreate(Endpoint endpoint);
  void onStop(Endpoint endpoint);
}
