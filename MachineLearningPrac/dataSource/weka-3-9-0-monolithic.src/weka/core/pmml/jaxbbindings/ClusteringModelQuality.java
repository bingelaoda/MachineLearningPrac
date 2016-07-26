/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   5:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   6:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   7:    */ import javax.xml.bind.annotation.XmlType;
/*   8:    */ 
/*   9:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  10:    */ @XmlType(name="")
/*  11:    */ @XmlRootElement(name="ClusteringModelQuality")
/*  12:    */ public class ClusteringModelQuality
/*  13:    */ {
/*  14:    */   @XmlAttribute(name="SSB")
/*  15:    */   protected Double ssb;
/*  16:    */   @XmlAttribute(name="SSE")
/*  17:    */   protected Double sse;
/*  18:    */   @XmlAttribute
/*  19:    */   protected String dataName;
/*  20:    */   
/*  21:    */   public Double getSSB()
/*  22:    */   {
/*  23: 60 */     return this.ssb;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void setSSB(Double value)
/*  27:    */   {
/*  28: 72 */     this.ssb = value;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Double getSSE()
/*  32:    */   {
/*  33: 84 */     return this.sse;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setSSE(Double value)
/*  37:    */   {
/*  38: 96 */     this.sse = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getDataName()
/*  42:    */   {
/*  43:108 */     return this.dataName;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setDataName(String value)
/*  47:    */   {
/*  48:120 */     this.dataName = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ClusteringModelQuality
 * JD-Core Version:    0.7.0.1
 */