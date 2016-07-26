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
/*  14:    */ @XmlRootElement(name="Interval")
/*  15:    */ public class Interval
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String closure;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double leftMargin;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double rightMargin;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 93 */     if (this.extension == null) {
/*  29: 94 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 96 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getClosure()
/*  35:    */   {
/*  36:108 */     return this.closure;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setClosure(String value)
/*  40:    */   {
/*  41:120 */     this.closure = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Double getLeftMargin()
/*  45:    */   {
/*  46:132 */     return this.leftMargin;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setLeftMargin(Double value)
/*  50:    */   {
/*  51:144 */     this.leftMargin = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Double getRightMargin()
/*  55:    */   {
/*  56:156 */     return this.rightMargin;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setRightMargin(Double value)
/*  60:    */   {
/*  61:168 */     this.rightMargin = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Interval
 * JD-Core Version:    0.7.0.1
 */