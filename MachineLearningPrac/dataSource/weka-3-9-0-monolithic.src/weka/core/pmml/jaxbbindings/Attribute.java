/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "simplePredicate", "compoundPredicate", "simpleSetPredicate", "_true", "_false"})
/*  14:    */ @XmlRootElement(name="Attribute")
/*  15:    */ public class Attribute
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
/*  29:    */   @XmlAttribute
/*  30:    */   protected Double partialScore;
/*  31:    */   @XmlAttribute
/*  32:    */   protected String reasonCode;
/*  33:    */   
/*  34:    */   public List<Extension> getExtension()
/*  35:    */   {
/*  36: 97 */     if (this.extension == null) {
/*  37: 98 */       this.extension = new ArrayList();
/*  38:    */     }
/*  39:100 */     return this.extension;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public SimplePredicate getSimplePredicate()
/*  43:    */   {
/*  44:112 */     return this.simplePredicate;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setSimplePredicate(SimplePredicate value)
/*  48:    */   {
/*  49:124 */     this.simplePredicate = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public CompoundPredicate getCompoundPredicate()
/*  53:    */   {
/*  54:136 */     return this.compoundPredicate;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setCompoundPredicate(CompoundPredicate value)
/*  58:    */   {
/*  59:148 */     this.compoundPredicate = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public SimpleSetPredicate getSimpleSetPredicate()
/*  63:    */   {
/*  64:160 */     return this.simpleSetPredicate;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setSimpleSetPredicate(SimpleSetPredicate value)
/*  68:    */   {
/*  69:172 */     this.simpleSetPredicate = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public True getTrue()
/*  73:    */   {
/*  74:184 */     return this._true;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setTrue(True value)
/*  78:    */   {
/*  79:196 */     this._true = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public False getFalse()
/*  83:    */   {
/*  84:208 */     return this._false;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setFalse(False value)
/*  88:    */   {
/*  89:220 */     this._false = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Double getPartialScore()
/*  93:    */   {
/*  94:232 */     return this.partialScore;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setPartialScore(Double value)
/*  98:    */   {
/*  99:244 */     this.partialScore = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getReasonCode()
/* 103:    */   {
/* 104:256 */     return this.reasonCode;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setReasonCode(String value)
/* 108:    */   {
/* 109:268 */     this.reasonCode = value;
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Attribute
 * JD-Core Version:    0.7.0.1
 */