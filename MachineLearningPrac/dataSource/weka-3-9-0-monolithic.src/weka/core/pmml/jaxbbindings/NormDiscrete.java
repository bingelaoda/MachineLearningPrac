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
/*  13:    */ @XmlType(name="", propOrder={"extension"})
/*  14:    */ @XmlRootElement(name="NormDiscrete")
/*  15:    */ public class NormDiscrete
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String field;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double mapMissingTo;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String method;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String value;
/*  27:    */   
/*  28:    */   public NormDiscrete() {}
/*  29:    */   
/*  30:    */   public NormDiscrete(String field, String value)
/*  31:    */   {
/*  32: 73 */     this.field = field;
/*  33: 74 */     this.value = value;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<Extension> getExtension()
/*  37:    */   {
/*  38:100 */     if (this.extension == null) {
/*  39:101 */       this.extension = new ArrayList();
/*  40:    */     }
/*  41:103 */     return this.extension;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getField()
/*  45:    */   {
/*  46:115 */     return this.field;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setField(String value)
/*  50:    */   {
/*  51:127 */     this.field = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Double getMapMissingTo()
/*  55:    */   {
/*  56:139 */     return this.mapMissingTo;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setMapMissingTo(Double value)
/*  60:    */   {
/*  61:151 */     this.mapMissingTo = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getMethod()
/*  65:    */   {
/*  66:163 */     if (this.method == null) {
/*  67:164 */       return "indicator";
/*  68:    */     }
/*  69:166 */     return this.method;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setMethod(String value)
/*  73:    */   {
/*  74:179 */     this.method = value;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getValue()
/*  78:    */   {
/*  79:191 */     return this.value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setValue(String value)
/*  83:    */   {
/*  84:203 */     this.value = value;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NormDiscrete
 * JD-Core Version:    0.7.0.1
 */