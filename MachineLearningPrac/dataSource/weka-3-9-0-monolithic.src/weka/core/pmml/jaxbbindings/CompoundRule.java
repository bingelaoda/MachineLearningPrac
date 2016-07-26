/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlElements;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "simplePredicate", "compoundPredicate", "simpleSetPredicate", "_true", "_false", "rule"})
/*  14:    */ @XmlRootElement(name="CompoundRule")
/*  15:    */ public class CompoundRule
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="SimplePredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected SimplePredicate simplePredicate;
/*  21:    */   @XmlElement(name="CompoundPredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected CompoundPredicate compoundPredicate;
/*  23:    */   @XmlElement(name="SimpleSetPredicate", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected SimpleSetPredicate simpleSetPredicate;
/*  25:    */   @XmlElement(name="True", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected True _true;
/*  27:    */   @XmlElement(name="False", namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected False _false;
/*  29:    */   @XmlElements({@XmlElement(name="CompoundRule", namespace="http://www.dmg.org/PMML-4_1", required=true, type=CompoundRule.class), @XmlElement(name="SimpleRule", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SimpleRule.class)})
/*  30:    */   protected List<Object> rule;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34: 98 */     if (this.extension == null) {
/*  35: 99 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37:101 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public SimplePredicate getSimplePredicate()
/*  41:    */   {
/*  42:113 */     return this.simplePredicate;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setSimplePredicate(SimplePredicate value)
/*  46:    */   {
/*  47:125 */     this.simplePredicate = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public CompoundPredicate getCompoundPredicate()
/*  51:    */   {
/*  52:137 */     return this.compoundPredicate;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setCompoundPredicate(CompoundPredicate value)
/*  56:    */   {
/*  57:149 */     this.compoundPredicate = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public SimpleSetPredicate getSimpleSetPredicate()
/*  61:    */   {
/*  62:161 */     return this.simpleSetPredicate;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setSimpleSetPredicate(SimpleSetPredicate value)
/*  66:    */   {
/*  67:173 */     this.simpleSetPredicate = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public True getTrue()
/*  71:    */   {
/*  72:185 */     return this._true;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setTrue(True value)
/*  76:    */   {
/*  77:197 */     this._true = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public False getFalse()
/*  81:    */   {
/*  82:209 */     return this._false;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setFalse(False value)
/*  86:    */   {
/*  87:221 */     this._false = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public List<Object> getRule()
/*  91:    */   {
/*  92:248 */     if (this.rule == null) {
/*  93:249 */       this.rule = new ArrayList();
/*  94:    */     }
/*  95:251 */     return this.rule;
/*  96:    */   }
/*  97:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.CompoundRule
 * JD-Core Version:    0.7.0.1
 */