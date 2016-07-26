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
/*  13:    */ @XmlType(name="", propOrder={"extension", "simplePredicate", "compoundPredicate", "simpleSetPredicate", "_true", "_false", "scoreDistribution"})
/*  14:    */ @XmlRootElement(name="SimpleRule")
/*  15:    */ public class SimpleRule
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
/*  29:    */   @XmlElement(name="ScoreDistribution", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  30:    */   protected List<ScoreDistribution> scoreDistribution;
/*  31:    */   @XmlAttribute
/*  32:    */   protected Double confidence;
/*  33:    */   @XmlAttribute
/*  34:    */   protected String id;
/*  35:    */   @XmlAttribute
/*  36:    */   protected Double nbCorrect;
/*  37:    */   @XmlAttribute
/*  38:    */   protected Double recordCount;
/*  39:    */   @XmlAttribute(required=true)
/*  40:    */   protected String score;
/*  41:    */   @XmlAttribute
/*  42:    */   protected Double weight;
/*  43:    */   
/*  44:    */   public List<Extension> getExtension()
/*  45:    */   {
/*  46:113 */     if (this.extension == null) {
/*  47:114 */       this.extension = new ArrayList();
/*  48:    */     }
/*  49:116 */     return this.extension;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public SimplePredicate getSimplePredicate()
/*  53:    */   {
/*  54:128 */     return this.simplePredicate;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setSimplePredicate(SimplePredicate value)
/*  58:    */   {
/*  59:140 */     this.simplePredicate = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public CompoundPredicate getCompoundPredicate()
/*  63:    */   {
/*  64:152 */     return this.compoundPredicate;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setCompoundPredicate(CompoundPredicate value)
/*  68:    */   {
/*  69:164 */     this.compoundPredicate = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public SimpleSetPredicate getSimpleSetPredicate()
/*  73:    */   {
/*  74:176 */     return this.simpleSetPredicate;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setSimpleSetPredicate(SimpleSetPredicate value)
/*  78:    */   {
/*  79:188 */     this.simpleSetPredicate = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public True getTrue()
/*  83:    */   {
/*  84:200 */     return this._true;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setTrue(True value)
/*  88:    */   {
/*  89:212 */     this._true = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public False getFalse()
/*  93:    */   {
/*  94:224 */     return this._false;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setFalse(False value)
/*  98:    */   {
/*  99:236 */     this._false = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public List<ScoreDistribution> getScoreDistribution()
/* 103:    */   {
/* 104:262 */     if (this.scoreDistribution == null) {
/* 105:263 */       this.scoreDistribution = new ArrayList();
/* 106:    */     }
/* 107:265 */     return this.scoreDistribution;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public Double getConfidence()
/* 111:    */   {
/* 112:277 */     return this.confidence;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setConfidence(Double value)
/* 116:    */   {
/* 117:289 */     this.confidence = value;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String getId()
/* 121:    */   {
/* 122:301 */     return this.id;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setId(String value)
/* 126:    */   {
/* 127:313 */     this.id = value;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public Double getNbCorrect()
/* 131:    */   {
/* 132:325 */     return this.nbCorrect;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setNbCorrect(Double value)
/* 136:    */   {
/* 137:337 */     this.nbCorrect = value;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Double getRecordCount()
/* 141:    */   {
/* 142:349 */     return this.recordCount;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void setRecordCount(Double value)
/* 146:    */   {
/* 147:361 */     this.recordCount = value;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String getScore()
/* 151:    */   {
/* 152:373 */     return this.score;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setScore(String value)
/* 156:    */   {
/* 157:385 */     this.score = value;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Double getWeight()
/* 161:    */   {
/* 162:397 */     return this.weight;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void setWeight(Double value)
/* 166:    */   {
/* 167:409 */     this.weight = value;
/* 168:    */   }
/* 169:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SimpleRule
 * JD-Core Version:    0.7.0.1
 */