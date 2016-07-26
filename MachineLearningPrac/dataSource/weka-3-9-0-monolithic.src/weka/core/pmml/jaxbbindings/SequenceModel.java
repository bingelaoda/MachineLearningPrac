/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"content"})
/*  15:    */ @XmlRootElement(name="SequenceModel")
/*  16:    */ public class SequenceModel
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="SequenceRule", namespace="http://www.dmg.org/PMML-4_1", type=SequenceRule.class), @javax.xml.bind.annotation.XmlElementRef(name="SetPredicate", namespace="http://www.dmg.org/PMML-4_1", type=SetPredicate.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Sequence", namespace="http://www.dmg.org/PMML-4_1", type=Sequence.class), @javax.xml.bind.annotation.XmlElementRef(name="Constraints", namespace="http://www.dmg.org/PMML-4_1", type=Constraints.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="Itemset", namespace="http://www.dmg.org/PMML-4_1", type=Itemset.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class), @javax.xml.bind.annotation.XmlElementRef(name="Item", namespace="http://www.dmg.org/PMML-4_1", type=Item.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String algorithmName;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double avgNumberOfItemsPerTransaction;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double avgNumberOfTAsPerTAGroup;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected MININGFUNCTION functionName;
/*  28:    */   @XmlAttribute
/*  29:    */   protected Boolean isScorable;
/*  30:    */   @XmlAttribute
/*  31:    */   protected BigInteger maxNumberOfItemsPerTransaction;
/*  32:    */   @XmlAttribute
/*  33:    */   protected BigInteger maxNumberOfTAsPerTAGroup;
/*  34:    */   @XmlAttribute
/*  35:    */   protected String modelName;
/*  36:    */   @XmlAttribute
/*  37:    */   protected BigInteger numberOfTransactionGroups;
/*  38:    */   @XmlAttribute
/*  39:    */   protected BigInteger numberOfTransactions;
/*  40:    */   
/*  41:    */   public List<Object> getContent()
/*  42:    */   {
/*  43:147 */     if (this.content == null) {
/*  44:148 */       this.content = new ArrayList();
/*  45:    */     }
/*  46:150 */     return this.content;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getAlgorithmName()
/*  50:    */   {
/*  51:162 */     return this.algorithmName;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setAlgorithmName(String value)
/*  55:    */   {
/*  56:174 */     this.algorithmName = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Double getAvgNumberOfItemsPerTransaction()
/*  60:    */   {
/*  61:186 */     return this.avgNumberOfItemsPerTransaction;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setAvgNumberOfItemsPerTransaction(Double value)
/*  65:    */   {
/*  66:198 */     this.avgNumberOfItemsPerTransaction = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Double getAvgNumberOfTAsPerTAGroup()
/*  70:    */   {
/*  71:210 */     return this.avgNumberOfTAsPerTAGroup;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setAvgNumberOfTAsPerTAGroup(Double value)
/*  75:    */   {
/*  76:222 */     this.avgNumberOfTAsPerTAGroup = value;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public MININGFUNCTION getFunctionName()
/*  80:    */   {
/*  81:234 */     return this.functionName;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setFunctionName(MININGFUNCTION value)
/*  85:    */   {
/*  86:246 */     this.functionName = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean isIsScorable()
/*  90:    */   {
/*  91:258 */     if (this.isScorable == null) {
/*  92:259 */       return true;
/*  93:    */     }
/*  94:261 */     return this.isScorable.booleanValue();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setIsScorable(Boolean value)
/*  98:    */   {
/*  99:274 */     this.isScorable = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public BigInteger getMaxNumberOfItemsPerTransaction()
/* 103:    */   {
/* 104:286 */     return this.maxNumberOfItemsPerTransaction;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setMaxNumberOfItemsPerTransaction(BigInteger value)
/* 108:    */   {
/* 109:298 */     this.maxNumberOfItemsPerTransaction = value;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public BigInteger getMaxNumberOfTAsPerTAGroup()
/* 113:    */   {
/* 114:310 */     return this.maxNumberOfTAsPerTAGroup;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setMaxNumberOfTAsPerTAGroup(BigInteger value)
/* 118:    */   {
/* 119:322 */     this.maxNumberOfTAsPerTAGroup = value;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String getModelName()
/* 123:    */   {
/* 124:334 */     return this.modelName;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setModelName(String value)
/* 128:    */   {
/* 129:346 */     this.modelName = value;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public BigInteger getNumberOfTransactionGroups()
/* 133:    */   {
/* 134:358 */     return this.numberOfTransactionGroups;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setNumberOfTransactionGroups(BigInteger value)
/* 138:    */   {
/* 139:370 */     this.numberOfTransactionGroups = value;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public BigInteger getNumberOfTransactions()
/* 143:    */   {
/* 144:382 */     return this.numberOfTransactions;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setNumberOfTransactions(BigInteger value)
/* 148:    */   {
/* 149:394 */     this.numberOfTransactions = value;
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SequenceModel
 * JD-Core Version:    0.7.0.1
 */