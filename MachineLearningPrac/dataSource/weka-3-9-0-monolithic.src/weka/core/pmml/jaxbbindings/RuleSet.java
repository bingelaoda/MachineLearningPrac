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
/*  14:    */ @XmlType(name="", propOrder={"extension", "ruleSelectionMethod", "scoreDistribution", "rule"})
/*  15:    */ @XmlRootElement(name="RuleSet")
/*  16:    */ public class RuleSet
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="RuleSelectionMethod", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<RuleSelectionMethod> ruleSelectionMethod;
/*  22:    */   @XmlElement(name="ScoreDistribution", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected List<ScoreDistribution> scoreDistribution;
/*  24:    */   @XmlElements({@XmlElement(name="SimpleRule", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SimpleRule.class), @XmlElement(name="CompoundRule", namespace="http://www.dmg.org/PMML-4_1", required=true, type=CompoundRule.class)})
/*  25:    */   protected List<Object> rule;
/*  26:    */   @XmlAttribute
/*  27:    */   protected Double defaultConfidence;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String defaultScore;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Double nbCorrect;
/*  32:    */   @XmlAttribute
/*  33:    */   protected Double recordCount;
/*  34:    */   
/*  35:    */   public List<Extension> getExtension()
/*  36:    */   {
/*  37:103 */     if (this.extension == null) {
/*  38:104 */       this.extension = new ArrayList();
/*  39:    */     }
/*  40:106 */     return this.extension;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public List<RuleSelectionMethod> getRuleSelectionMethod()
/*  44:    */   {
/*  45:132 */     if (this.ruleSelectionMethod == null) {
/*  46:133 */       this.ruleSelectionMethod = new ArrayList();
/*  47:    */     }
/*  48:135 */     return this.ruleSelectionMethod;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public List<ScoreDistribution> getScoreDistribution()
/*  52:    */   {
/*  53:161 */     if (this.scoreDistribution == null) {
/*  54:162 */       this.scoreDistribution = new ArrayList();
/*  55:    */     }
/*  56:164 */     return this.scoreDistribution;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public List<Object> getRule()
/*  60:    */   {
/*  61:191 */     if (this.rule == null) {
/*  62:192 */       this.rule = new ArrayList();
/*  63:    */     }
/*  64:194 */     return this.rule;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Double getDefaultConfidence()
/*  68:    */   {
/*  69:206 */     return this.defaultConfidence;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setDefaultConfidence(Double value)
/*  73:    */   {
/*  74:218 */     this.defaultConfidence = value;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getDefaultScore()
/*  78:    */   {
/*  79:230 */     return this.defaultScore;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setDefaultScore(String value)
/*  83:    */   {
/*  84:242 */     this.defaultScore = value;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Double getNbCorrect()
/*  88:    */   {
/*  89:254 */     return this.nbCorrect;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setNbCorrect(Double value)
/*  93:    */   {
/*  94:266 */     this.nbCorrect = value;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Double getRecordCount()
/*  98:    */   {
/*  99:278 */     return this.recordCount;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setRecordCount(Double value)
/* 103:    */   {
/* 104:290 */     this.recordCount = value;
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RuleSet
 * JD-Core Version:    0.7.0.1
 */