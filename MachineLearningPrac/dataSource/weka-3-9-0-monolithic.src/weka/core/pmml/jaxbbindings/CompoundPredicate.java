/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlElements;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "simplePredicateOrCompoundPredicateOrSimpleSetPredicate"})
/*  15:    */ @XmlRootElement(name="CompoundPredicate")
/*  16:    */ public class CompoundPredicate
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElements({@XmlElement(name="SimpleSetPredicate", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SimpleSetPredicate.class), @XmlElement(name="SimplePredicate", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SimplePredicate.class), @XmlElement(name="False", namespace="http://www.dmg.org/PMML-4_1", required=true, type=False.class), @XmlElement(name="True", namespace="http://www.dmg.org/PMML-4_1", required=true, type=True.class), @XmlElement(name="CompoundPredicate", namespace="http://www.dmg.org/PMML-4_1", required=true, type=CompoundPredicate.class)})
/*  21:    */   protected List<Object> simplePredicateOrCompoundPredicateOrSimpleSetPredicate;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected String booleanOperator;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27:100 */     if (this.extension == null) {
/*  28:101 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30:103 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public List<Object> getSimplePredicateOrCompoundPredicateOrSimpleSetPredicate()
/*  34:    */   {
/*  35:133 */     if (this.simplePredicateOrCompoundPredicateOrSimpleSetPredicate == null) {
/*  36:134 */       this.simplePredicateOrCompoundPredicateOrSimpleSetPredicate = new ArrayList();
/*  37:    */     }
/*  38:136 */     return this.simplePredicateOrCompoundPredicateOrSimpleSetPredicate;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getBooleanOperator()
/*  42:    */   {
/*  43:148 */     return this.booleanOperator;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setBooleanOperator(String value)
/*  47:    */   {
/*  48:160 */     this.booleanOperator = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.CompoundPredicate
 * JD-Core Version:    0.7.0.1
 */