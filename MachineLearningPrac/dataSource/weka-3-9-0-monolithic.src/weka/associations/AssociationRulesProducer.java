package weka.associations;

public abstract interface AssociationRulesProducer
{
  public abstract AssociationRules getAssociationRules();
  
  public abstract String[] getRuleMetricNames();
  
  public abstract boolean canProduceRules();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AssociationRulesProducer
 * JD-Core Version:    0.7.0.1
 */