/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   8:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   9:    */ import javax.xml.bind.annotation.XmlAttribute;
/*  10:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*  11:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  12:    */ import javax.xml.bind.annotation.XmlType;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"content"})
/*  16:    */ @XmlRootElement(name="AssociationModel")
/*  17:    */ public class AssociationModel
/*  18:    */ {
/*  19:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="AssociationRule", namespace="http://www.dmg.org/PMML-4_1", type=AssociationRule.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="Itemset", namespace="http://www.dmg.org/PMML-4_1", type=Itemset.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Item", namespace="http://www.dmg.org/PMML-4_1", type=Item.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  20:    */   protected List<Object> content;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String algorithmName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double avgNumberOfItemsPerTA;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected MININGFUNCTION functionName;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Boolean isScorable;
/*  29:    */   @XmlAttribute
/*  30:    */   protected BigInteger lengthLimit;
/*  31:    */   @XmlAttribute
/*  32:    */   protected BigInteger maxNumberOfItemsPerTA;
/*  33:    */   @XmlAttribute(required=true)
/*  34:    */   protected BigDecimal minimumConfidence;
/*  35:    */   @XmlAttribute(required=true)
/*  36:    */   protected BigDecimal minimumSupport;
/*  37:    */   @XmlAttribute
/*  38:    */   protected String modelName;
/*  39:    */   @XmlAttribute(required=true)
/*  40:    */   protected BigInteger numberOfItems;
/*  41:    */   @XmlAttribute(required=true)
/*  42:    */   protected BigInteger numberOfItemsets;
/*  43:    */   @XmlAttribute(required=true)
/*  44:    */   protected BigInteger numberOfRules;
/*  45:    */   @XmlAttribute(required=true)
/*  46:    */   protected BigInteger numberOfTransactions;
/*  47:    */   
/*  48:    */   public List<Object> getContent()
/*  49:    */   {
/*  50:154 */     if (this.content == null) {
/*  51:155 */       this.content = new ArrayList();
/*  52:    */     }
/*  53:157 */     return this.content;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getAlgorithmName()
/*  57:    */   {
/*  58:169 */     return this.algorithmName;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setAlgorithmName(String value)
/*  62:    */   {
/*  63:181 */     this.algorithmName = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Double getAvgNumberOfItemsPerTA()
/*  67:    */   {
/*  68:193 */     return this.avgNumberOfItemsPerTA;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setAvgNumberOfItemsPerTA(Double value)
/*  72:    */   {
/*  73:205 */     this.avgNumberOfItemsPerTA = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public MININGFUNCTION getFunctionName()
/*  77:    */   {
/*  78:217 */     return this.functionName;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setFunctionName(MININGFUNCTION value)
/*  82:    */   {
/*  83:229 */     this.functionName = value;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean isIsScorable()
/*  87:    */   {
/*  88:241 */     if (this.isScorable == null) {
/*  89:242 */       return true;
/*  90:    */     }
/*  91:244 */     return this.isScorable.booleanValue();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setIsScorable(Boolean value)
/*  95:    */   {
/*  96:257 */     this.isScorable = value;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public BigInteger getLengthLimit()
/* 100:    */   {
/* 101:269 */     return this.lengthLimit;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setLengthLimit(BigInteger value)
/* 105:    */   {
/* 106:281 */     this.lengthLimit = value;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public BigInteger getMaxNumberOfItemsPerTA()
/* 110:    */   {
/* 111:293 */     return this.maxNumberOfItemsPerTA;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setMaxNumberOfItemsPerTA(BigInteger value)
/* 115:    */   {
/* 116:305 */     this.maxNumberOfItemsPerTA = value;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public BigDecimal getMinimumConfidence()
/* 120:    */   {
/* 121:317 */     return this.minimumConfidence;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setMinimumConfidence(BigDecimal value)
/* 125:    */   {
/* 126:329 */     this.minimumConfidence = value;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public BigDecimal getMinimumSupport()
/* 130:    */   {
/* 131:341 */     return this.minimumSupport;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setMinimumSupport(BigDecimal value)
/* 135:    */   {
/* 136:353 */     this.minimumSupport = value;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String getModelName()
/* 140:    */   {
/* 141:365 */     return this.modelName;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setModelName(String value)
/* 145:    */   {
/* 146:377 */     this.modelName = value;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public BigInteger getNumberOfItems()
/* 150:    */   {
/* 151:389 */     return this.numberOfItems;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setNumberOfItems(BigInteger value)
/* 155:    */   {
/* 156:401 */     this.numberOfItems = value;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public BigInteger getNumberOfItemsets()
/* 160:    */   {
/* 161:413 */     return this.numberOfItemsets;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setNumberOfItemsets(BigInteger value)
/* 165:    */   {
/* 166:425 */     this.numberOfItemsets = value;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public BigInteger getNumberOfRules()
/* 170:    */   {
/* 171:437 */     return this.numberOfRules;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setNumberOfRules(BigInteger value)
/* 175:    */   {
/* 176:449 */     this.numberOfRules = value;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public BigInteger getNumberOfTransactions()
/* 180:    */   {
/* 181:461 */     return this.numberOfTransactions;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setNumberOfTransactions(BigInteger value)
/* 185:    */   {
/* 186:473 */     this.numberOfTransactions = value;
/* 187:    */   }
/* 188:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.AssociationModel
 * JD-Core Version:    0.7.0.1
 */