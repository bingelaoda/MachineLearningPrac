/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.OptionHandler;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public class AssociationRules
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 8889198755948056749L;
/*  12: 41 */   protected String m_producer = "Unknown";
/*  13:    */   protected List<AssociationRule> m_rules;
/*  14:    */   
/*  15:    */   public AssociationRules(List<AssociationRule> rules, String producer)
/*  16:    */   {
/*  17: 53 */     this.m_rules = rules;
/*  18: 54 */     this.m_producer = producer;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public AssociationRules(List<AssociationRule> rules, Object producer)
/*  22:    */   {
/*  23: 64 */     String producerString = producer.getClass().getName();
/*  24: 65 */     if (producerString.startsWith("weka.associations.")) {
/*  25: 66 */       producerString = producerString.substring("weka.associations.".length());
/*  26:    */     }
/*  27: 69 */     if ((producer instanceof OptionHandler))
/*  28:    */     {
/*  29: 70 */       String[] o = ((OptionHandler)producer).getOptions();
/*  30: 71 */       producerString = producerString + " " + Utils.joinOptions(o);
/*  31:    */     }
/*  32: 74 */     this.m_rules = rules;
/*  33: 75 */     this.m_producer = producerString;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public AssociationRules(List<AssociationRule> rules)
/*  37:    */   {
/*  38: 84 */     this(rules, "Unknown");
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setRules(List<AssociationRule> rules)
/*  42:    */   {
/*  43: 93 */     this.m_rules = rules;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public List<AssociationRule> getRules()
/*  47:    */   {
/*  48:102 */     return this.m_rules;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int getNumRules()
/*  52:    */   {
/*  53:111 */     return this.m_rules.size();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setProducer(String producer)
/*  57:    */   {
/*  58:123 */     this.m_producer = producer;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getProducer()
/*  62:    */   {
/*  63:132 */     return this.m_producer;
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AssociationRules
 * JD-Core Version:    0.7.0.1
 */