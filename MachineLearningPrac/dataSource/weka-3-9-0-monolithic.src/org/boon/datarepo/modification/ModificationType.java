package org.boon.datarepo.modification;

public enum ModificationType
{
  BEFORE_INCREMENT,  AFTER_INCREMENT,  BEFORE_UPDATE,  BEFORE_MODIFY,  BEFORE_ADD,  AFTER_UPDATE,  AFTER_MODIFY,  AFTER_ADD,  BEFORE_MODIFY_BY_VALUE_SETTERS,  AFTER_MODIFY_BY_VALUE_SETTERS,  BEFORE_UPDATE_BY_VALUE_SETTERS,  AFTER_UPDATE_BY_VALUE_SETTERS;
  
  private ModificationType() {}
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.modification.ModificationType
 * JD-Core Version:    0.7.0.1
 */