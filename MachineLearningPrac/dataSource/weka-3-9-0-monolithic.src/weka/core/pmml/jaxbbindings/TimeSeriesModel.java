/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"content"})
/*  14:    */ @XmlRootElement(name="TimeSeriesModel")
/*  15:    */ public class TimeSeriesModel
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="TimeSeries", namespace="http://www.dmg.org/PMML-4_1", type=TimeSeries.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="ExponentialSmoothing", namespace="http://www.dmg.org/PMML-4_1", type=ExponentialSmoothing.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="SeasonalTrendDecomposition", namespace="http://www.dmg.org/PMML-4_1", type=javax.xml.bind.JAXBElement.class), @javax.xml.bind.annotation.XmlElementRef(name="ARIMA", namespace="http://www.dmg.org/PMML-4_1", type=javax.xml.bind.JAXBElement.class), @javax.xml.bind.annotation.XmlElementRef(name="SpectralAnalysis", namespace="http://www.dmg.org/PMML-4_1", type=javax.xml.bind.JAXBElement.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected TIMESERIESALGORITHM bestFit;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected MININGFUNCTION functionName;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Boolean isScorable;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String modelName;
/*  29:    */   
/*  30:    */   public List<Object> getContent()
/*  31:    */   {
/*  32:138 */     if (this.content == null) {
/*  33:139 */       this.content = new ArrayList();
/*  34:    */     }
/*  35:141 */     return this.content;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getAlgorithmName()
/*  39:    */   {
/*  40:153 */     return this.algorithmName;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setAlgorithmName(String value)
/*  44:    */   {
/*  45:165 */     this.algorithmName = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TIMESERIESALGORITHM getBestFit()
/*  49:    */   {
/*  50:177 */     return this.bestFit;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setBestFit(TIMESERIESALGORITHM value)
/*  54:    */   {
/*  55:189 */     this.bestFit = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public MININGFUNCTION getFunctionName()
/*  59:    */   {
/*  60:201 */     return this.functionName;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setFunctionName(MININGFUNCTION value)
/*  64:    */   {
/*  65:213 */     this.functionName = value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public boolean isIsScorable()
/*  69:    */   {
/*  70:225 */     if (this.isScorable == null) {
/*  71:226 */       return true;
/*  72:    */     }
/*  73:228 */     return this.isScorable.booleanValue();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setIsScorable(Boolean value)
/*  77:    */   {
/*  78:241 */     this.isScorable = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getModelName()
/*  82:    */   {
/*  83:253 */     return this.modelName;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setModelName(String value)
/*  87:    */   {
/*  88:265 */     this.modelName = value;
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeSeriesModel
 * JD-Core Version:    0.7.0.1
 */