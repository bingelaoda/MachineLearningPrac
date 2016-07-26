/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import weka.associations.AssociationRules;
/*  5:   */ 
/*  6:   */ public class BatchAssociationRulesEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 6332614648885439492L;
/* 10:   */   protected AssociationRules m_rules;
/* 11:   */   
/* 12:   */   public BatchAssociationRulesEvent(Object source, AssociationRules rules)
/* 13:   */   {
/* 14:49 */     super(source);
/* 15:   */     
/* 16:51 */     this.m_rules = rules;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public AssociationRules getRules()
/* 20:   */   {
/* 21:60 */     return this.m_rules;
/* 22:   */   }
/* 23:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BatchAssociationRulesEvent
 * JD-Core Version:    0.7.0.1
 */