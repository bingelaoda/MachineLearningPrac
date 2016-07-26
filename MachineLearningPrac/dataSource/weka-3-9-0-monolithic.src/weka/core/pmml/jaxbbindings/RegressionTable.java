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
/*  13:    */ @XmlType(name="", propOrder={"extension", "numericPredictor", "categoricalPredictor", "predictorTerm"})
/*  14:    */ @XmlRootElement(name="RegressionTable")
/*  15:    */ public class RegressionTable
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="NumericPredictor", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<NumericPredictor> numericPredictor;
/*  21:    */   @XmlElement(name="CategoricalPredictor", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<CategoricalPredictor> categoricalPredictor;
/*  23:    */   @XmlElement(name="PredictorTerm", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  24:    */   protected List<PredictorTerm> predictorTerm;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected double intercept;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String targetCategory;
/*  29:    */   
/*  30:    */   public RegressionTable() {}
/*  31:    */   
/*  32:    */   public RegressionTable(String targetCategory)
/*  33:    */   {
/*  34: 73 */     this.targetCategory = targetCategory;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public List<Extension> getExtension()
/*  38:    */   {
/*  39: 99 */     if (this.extension == null) {
/*  40:100 */       this.extension = new ArrayList();
/*  41:    */     }
/*  42:102 */     return this.extension;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public List<NumericPredictor> getNumericPredictors()
/*  46:    */   {
/*  47:128 */     if (this.numericPredictor == null) {
/*  48:129 */       this.numericPredictor = new ArrayList();
/*  49:    */     }
/*  50:131 */     return this.numericPredictor;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addNumericPredictor(NumericPredictor predictor)
/*  54:    */   {
/*  55:135 */     if (this.numericPredictor == null) {
/*  56:136 */       this.numericPredictor = new ArrayList();
/*  57:    */     }
/*  58:138 */     this.numericPredictor.add(predictor);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public List<CategoricalPredictor> getCategoricalPredictor()
/*  62:    */   {
/*  63:164 */     if (this.categoricalPredictor == null) {
/*  64:165 */       this.categoricalPredictor = new ArrayList();
/*  65:    */     }
/*  66:167 */     return this.categoricalPredictor;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public List<PredictorTerm> getPredictorTerm()
/*  70:    */   {
/*  71:193 */     if (this.predictorTerm == null) {
/*  72:194 */       this.predictorTerm = new ArrayList();
/*  73:    */     }
/*  74:196 */     return this.predictorTerm;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public double getIntercept()
/*  78:    */   {
/*  79:204 */     return this.intercept;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setIntercept(double value)
/*  83:    */   {
/*  84:212 */     this.intercept = value;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String getTargetCategory()
/*  88:    */   {
/*  89:224 */     return this.targetCategory;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setTargetCategory(String value)
/*  93:    */   {
/*  94:236 */     this.targetCategory = value;
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RegressionTable
 * JD-Core Version:    0.7.0.1
 */