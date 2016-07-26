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
/*  14:    */ @XmlRootElement(name="KohonenMap")
/*  15:    */ public class KohonenMap
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected Float coord1;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Float coord2;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Float coord3;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 84 */     if (this.extension == null) {
/*  29: 85 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 87 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Float getCoord1()
/*  35:    */   {
/*  36: 99 */     return this.coord1;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setCoord1(Float value)
/*  40:    */   {
/*  41:111 */     this.coord1 = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Float getCoord2()
/*  45:    */   {
/*  46:123 */     return this.coord2;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setCoord2(Float value)
/*  50:    */   {
/*  51:135 */     this.coord2 = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Float getCoord3()
/*  55:    */   {
/*  56:147 */     return this.coord3;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setCoord3(Float value)
/*  60:    */   {
/*  61:159 */     this.coord3 = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.KohonenMap
 * JD-Core Version:    0.7.0.1
 */