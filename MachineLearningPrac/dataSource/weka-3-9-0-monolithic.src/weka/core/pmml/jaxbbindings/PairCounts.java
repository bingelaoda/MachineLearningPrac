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
/*  13:    */ @XmlType(name="", propOrder={"extension", "targetValueCounts"})
/*  14:    */ @XmlRootElement(name="PairCounts")
/*  15:    */ public class PairCounts
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="TargetValueCounts", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected TargetValueCounts targetValueCounts;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String value;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 82 */     if (this.extension == null) {
/*  27: 83 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 85 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public TargetValueCounts getTargetValueCounts()
/*  33:    */   {
/*  34: 97 */     return this.targetValueCounts;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setTargetValueCounts(TargetValueCounts value)
/*  38:    */   {
/*  39:109 */     this.targetValueCounts = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getValue()
/*  43:    */   {
/*  44:121 */     return this.value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setValue(String value)
/*  48:    */   {
/*  49:133 */     this.value = value;
/*  50:    */   }
/*  51:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PairCounts
 * JD-Core Version:    0.7.0.1
 */