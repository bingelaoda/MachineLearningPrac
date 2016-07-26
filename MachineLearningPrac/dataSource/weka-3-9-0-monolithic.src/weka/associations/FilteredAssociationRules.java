/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.util.List;
/*   4:    */ import weka.filters.Filter;
/*   5:    */ 
/*   6:    */ public class FilteredAssociationRules
/*   7:    */   extends AssociationRules
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -4223408305476916955L;
/*  10:    */   protected Filter m_filter;
/*  11:    */   protected AssociationRules m_wrappedRules;
/*  12:    */   
/*  13:    */   public FilteredAssociationRules(String producer, Filter filter, AssociationRules rules)
/*  14:    */   {
/*  15: 53 */     super(null, producer);
/*  16:    */     
/*  17: 55 */     this.m_filter = filter;
/*  18: 56 */     this.m_wrappedRules = rules;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public FilteredAssociationRules(Object producer, Filter filter, AssociationRules rules)
/*  22:    */   {
/*  23: 67 */     super(null, producer);
/*  24:    */     
/*  25: 69 */     this.m_filter = filter;
/*  26: 70 */     this.m_wrappedRules = rules;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public FilteredAssociationRules(Filter filter, AssociationRules rules)
/*  30:    */   {
/*  31: 80 */     super(null);
/*  32:    */     
/*  33: 82 */     this.m_filter = filter;
/*  34: 83 */     this.m_wrappedRules = rules;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setRules(List<AssociationRule> rules)
/*  38:    */   {
/*  39: 94 */     this.m_wrappedRules.setRules(rules);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public List<AssociationRule> getRules()
/*  43:    */   {
/*  44:105 */     return this.m_wrappedRules.getRules();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public int getNumRules()
/*  48:    */   {
/*  49:116 */     return this.m_wrappedRules.getNumRules();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setWrappedRules(AssociationRules rules)
/*  53:    */   {
/*  54:125 */     this.m_wrappedRules = rules;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public AssociationRules getWrappedRules()
/*  58:    */   {
/*  59:134 */     return this.m_wrappedRules;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.FilteredAssociationRules
 * JD-Core Version:    0.7.0.1
 */