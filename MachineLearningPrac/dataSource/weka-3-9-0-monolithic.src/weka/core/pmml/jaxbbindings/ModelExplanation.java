/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"extension", "predictiveModelQuality", "clusteringModelQuality", "correlations"})
/*  13:    */ @XmlRootElement(name="ModelExplanation")
/*  14:    */ public class ModelExplanation
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="PredictiveModelQuality", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<PredictiveModelQuality> predictiveModelQuality;
/*  20:    */   @XmlElement(name="ClusteringModelQuality", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<ClusteringModelQuality> clusteringModelQuality;
/*  22:    */   @XmlElement(name="Correlations", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected Correlations correlations;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27: 88 */     if (this.extension == null) {
/*  28: 89 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30: 91 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public List<PredictiveModelQuality> getPredictiveModelQuality()
/*  34:    */   {
/*  35:117 */     if (this.predictiveModelQuality == null) {
/*  36:118 */       this.predictiveModelQuality = new ArrayList();
/*  37:    */     }
/*  38:120 */     return this.predictiveModelQuality;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public List<ClusteringModelQuality> getClusteringModelQuality()
/*  42:    */   {
/*  43:146 */     if (this.clusteringModelQuality == null) {
/*  44:147 */       this.clusteringModelQuality = new ArrayList();
/*  45:    */     }
/*  46:149 */     return this.clusteringModelQuality;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Correlations getCorrelations()
/*  50:    */   {
/*  51:161 */     return this.correlations;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setCorrelations(Correlations value)
/*  55:    */   {
/*  56:173 */     this.correlations = value;
/*  57:    */   }
/*  58:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ModelExplanation
 * JD-Core Version:    0.7.0.1
 */