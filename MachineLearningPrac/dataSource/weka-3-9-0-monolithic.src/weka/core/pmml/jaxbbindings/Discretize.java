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
/*  13:    */ @XmlType(name="", propOrder={"extension", "discretizeBin"})
/*  14:    */ @XmlRootElement(name="Discretize")
/*  15:    */ public class Discretize
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="DiscretizeBin", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<DiscretizeBin> discretizeBin;
/*  21:    */   @XmlAttribute
/*  22:    */   protected DATATYPE dataType;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String defaultValue;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String field;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String mapMissingTo;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 91 */     if (this.extension == null) {
/*  33: 92 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 94 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public List<DiscretizeBin> getDiscretizeBin()
/*  39:    */   {
/*  40:120 */     if (this.discretizeBin == null) {
/*  41:121 */       this.discretizeBin = new ArrayList();
/*  42:    */     }
/*  43:123 */     return this.discretizeBin;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public DATATYPE getDataType()
/*  47:    */   {
/*  48:135 */     return this.dataType;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setDataType(DATATYPE value)
/*  52:    */   {
/*  53:147 */     this.dataType = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getDefaultValue()
/*  57:    */   {
/*  58:159 */     return this.defaultValue;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setDefaultValue(String value)
/*  62:    */   {
/*  63:171 */     this.defaultValue = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getField()
/*  67:    */   {
/*  68:183 */     return this.field;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setField(String value)
/*  72:    */   {
/*  73:195 */     this.field = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getMapMissingTo()
/*  77:    */   {
/*  78:207 */     return this.mapMissingTo;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setMapMissingTo(String value)
/*  82:    */   {
/*  83:219 */     this.mapMissingTo = value;
/*  84:    */   }
/*  85:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Discretize
 * JD-Core Version:    0.7.0.1
 */