/**
 * 
 */
package edu.hziee.common.fsm.predicate;

import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * @author ubuntu-admin
 * 
 */
public class TypeAcceptor implements Predicate {

  private Collection<Class<?>> allowTypes;

  /**
   * @return the allowTypes
   */
  public Collection<Class<?>> getAllowTypes() {
    return allowTypes;
  }

  /**
   * @param allowTypes
   *          the allowTypes to set
   */
  public void setAllowTypes(Collection<Class<?>> allowTypes) {
    this.allowTypes = allowTypes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
   */
  public boolean evaluate(Object object) {
    if (null == object) {
      return false;
    }
    return allowTypes.contains(object.getClass());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TypeAcceptor [allowTypes=" + allowTypes + "]";
  }

}
