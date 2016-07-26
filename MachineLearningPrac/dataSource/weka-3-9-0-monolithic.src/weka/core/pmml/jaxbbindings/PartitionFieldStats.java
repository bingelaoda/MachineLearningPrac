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
/*  13:    */ @XmlType(name="", propOrder={"extension", "counts", "numericInfo", "numarray"})
/*  14:    */ @XmlRootElement(name="PartitionFieldStats")
/*  15:    */ public class PartitionFieldStats
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Counts", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Counts counts;
/*  21:    */   @XmlElement(name="NumericInfo", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected NumericInfo numericInfo;
/*  23:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  24:    */   protected List<ArrayType> numarray;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String field;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String weighted;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32:100 */     if (this.extension == null) {
/*  33:101 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35:103 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Counts getCounts()
/*  39:    */   {
/*  40:115 */     return this.counts;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setCounts(Counts value)
/*  44:    */   {
/*  45:127 */     this.counts = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public NumericInfo getNumericInfo()
/*  49:    */   {
/*  50:139 */     return this.numericInfo;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setNumericInfo(NumericInfo value)
/*  54:    */   {
/*  55:151 */     this.numericInfo = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public List<ArrayType> getNUMARRAY()
/*  59:    */   {
/*  60:177 */     if (this.numarray == null) {
/*  61:178 */       this.numarray = new ArrayList();
/*  62:    */     }
/*  63:180 */     return this.numarray;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getField()
/*  67:    */   {
/*  68:192 */     return this.field;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setField(String value)
/*  72:    */   {
/*  73:204 */     this.field = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getWeighted()
/*  77:    */   {
/*  78:216 */     if (this.weighted == null) {
/*  79:217 */       return "0";
/*  80:    */     }
/*  81:219 */     return this.weighted;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setWeighted(String value)
/*  85:    */   {
/*  86:232 */     this.weighted = value;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PartitionFieldStats
 * JD-Core Version:    0.7.0.1
 */