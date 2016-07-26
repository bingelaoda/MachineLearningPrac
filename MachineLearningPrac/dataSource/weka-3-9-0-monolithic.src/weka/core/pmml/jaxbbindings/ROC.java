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
/*  13:    */ @XmlType(name="", propOrder={"extension", "rocGraph"})
/*  14:    */ @XmlRootElement(name="ROC")
/*  15:    */ public class ROC
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="ROCGraph", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected ROCGraph rocGraph;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String negativeTargetFieldDisplayValue;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String negativeTargetFieldValue;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String positiveTargetFieldDisplayValue;
/*  27:    */   @XmlAttribute(required=true)
/*  28:    */   protected String positiveTargetFieldValue;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 91 */     if (this.extension == null) {
/*  33: 92 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 94 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public ROCGraph getROCGraph()
/*  39:    */   {
/*  40:106 */     return this.rocGraph;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setROCGraph(ROCGraph value)
/*  44:    */   {
/*  45:118 */     this.rocGraph = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getNegativeTargetFieldDisplayValue()
/*  49:    */   {
/*  50:130 */     return this.negativeTargetFieldDisplayValue;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setNegativeTargetFieldDisplayValue(String value)
/*  54:    */   {
/*  55:142 */     this.negativeTargetFieldDisplayValue = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getNegativeTargetFieldValue()
/*  59:    */   {
/*  60:154 */     return this.negativeTargetFieldValue;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setNegativeTargetFieldValue(String value)
/*  64:    */   {
/*  65:166 */     this.negativeTargetFieldValue = value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getPositiveTargetFieldDisplayValue()
/*  69:    */   {
/*  70:178 */     return this.positiveTargetFieldDisplayValue;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setPositiveTargetFieldDisplayValue(String value)
/*  74:    */   {
/*  75:190 */     this.positiveTargetFieldDisplayValue = value;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String getPositiveTargetFieldValue()
/*  79:    */   {
/*  80:202 */     return this.positiveTargetFieldValue;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setPositiveTargetFieldValue(String value)
/*  84:    */   {
/*  85:214 */     this.positiveTargetFieldValue = value;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ROC
 * JD-Core Version:    0.7.0.1
 */