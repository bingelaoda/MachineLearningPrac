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
/*  13:    */ @XmlType(name="", propOrder={"extension", "linearNorm"})
/*  14:    */ @XmlRootElement(name="NormContinuous")
/*  15:    */ public class NormContinuous
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="LinearNorm", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<LinearNorm> linearNorm;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String field;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double mapMissingTo;
/*  25:    */   @XmlAttribute
/*  26:    */   protected OUTLIERTREATMENTMETHOD outliers;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 88 */     if (this.extension == null) {
/*  31: 89 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 91 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<LinearNorm> getLinearNorm()
/*  37:    */   {
/*  38:117 */     if (this.linearNorm == null) {
/*  39:118 */       this.linearNorm = new ArrayList();
/*  40:    */     }
/*  41:120 */     return this.linearNorm;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getField()
/*  45:    */   {
/*  46:132 */     return this.field;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setField(String value)
/*  50:    */   {
/*  51:144 */     this.field = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Double getMapMissingTo()
/*  55:    */   {
/*  56:156 */     return this.mapMissingTo;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setMapMissingTo(Double value)
/*  60:    */   {
/*  61:168 */     this.mapMissingTo = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public OUTLIERTREATMENTMETHOD getOutliers()
/*  65:    */   {
/*  66:180 */     if (this.outliers == null) {
/*  67:181 */       return OUTLIERTREATMENTMETHOD.AS_IS;
/*  68:    */     }
/*  69:183 */     return this.outliers;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setOutliers(OUTLIERTREATMENTMETHOD value)
/*  73:    */   {
/*  74:196 */     this.outliers = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NormContinuous
 * JD-Core Version:    0.7.0.1
 */