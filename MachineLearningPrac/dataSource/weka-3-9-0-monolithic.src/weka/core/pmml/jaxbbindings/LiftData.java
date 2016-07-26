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
/*  13:    */ @XmlType(name="", propOrder={"extension", "modelLiftGraph", "optimumLiftGraph", "randomLiftGraph"})
/*  14:    */ @XmlRootElement(name="LiftData")
/*  15:    */ public class LiftData
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="ModelLiftGraph", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected ModelLiftGraph modelLiftGraph;
/*  21:    */   @XmlElement(name="OptimumLiftGraph", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected OptimumLiftGraph optimumLiftGraph;
/*  23:    */   @XmlElement(name="RandomLiftGraph", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected RandomLiftGraph randomLiftGraph;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double rankingQuality;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String targetFieldDisplayValue;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String targetFieldValue;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34: 96 */     if (this.extension == null) {
/*  35: 97 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37: 99 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public ModelLiftGraph getModelLiftGraph()
/*  41:    */   {
/*  42:111 */     return this.modelLiftGraph;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setModelLiftGraph(ModelLiftGraph value)
/*  46:    */   {
/*  47:123 */     this.modelLiftGraph = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public OptimumLiftGraph getOptimumLiftGraph()
/*  51:    */   {
/*  52:135 */     return this.optimumLiftGraph;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setOptimumLiftGraph(OptimumLiftGraph value)
/*  56:    */   {
/*  57:147 */     this.optimumLiftGraph = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public RandomLiftGraph getRandomLiftGraph()
/*  61:    */   {
/*  62:159 */     return this.randomLiftGraph;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setRandomLiftGraph(RandomLiftGraph value)
/*  66:    */   {
/*  67:171 */     this.randomLiftGraph = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Double getRankingQuality()
/*  71:    */   {
/*  72:183 */     return this.rankingQuality;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setRankingQuality(Double value)
/*  76:    */   {
/*  77:195 */     this.rankingQuality = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getTargetFieldDisplayValue()
/*  81:    */   {
/*  82:207 */     return this.targetFieldDisplayValue;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setTargetFieldDisplayValue(String value)
/*  86:    */   {
/*  87:219 */     this.targetFieldDisplayValue = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String getTargetFieldValue()
/*  91:    */   {
/*  92:231 */     return this.targetFieldValue;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setTargetFieldValue(String value)
/*  96:    */   {
/*  97:243 */     this.targetFieldValue = value;
/*  98:    */   }
/*  99:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.LiftData
 * JD-Core Version:    0.7.0.1
 */