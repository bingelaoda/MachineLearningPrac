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
/*  14:    */ @XmlRootElement(name="SetReference")
/*  15:    */ public class SetReference
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String setId;
/*  21:    */   
/*  22:    */   public List<Extension> getExtension()
/*  23:    */   {
/*  24: 78 */     if (this.extension == null) {
/*  25: 79 */       this.extension = new ArrayList();
/*  26:    */     }
/*  27: 81 */     return this.extension;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getSetId()
/*  31:    */   {
/*  32: 93 */     return this.setId;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setSetId(String value)
/*  36:    */   {
/*  37:105 */     this.setId = value;
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SetReference
 * JD-Core Version:    0.7.0.1
 */