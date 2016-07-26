/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  6:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  7:   */ import javax.xml.bind.annotation.XmlElement;
/*  8:   */ import javax.xml.bind.annotation.XmlRootElement;
/*  9:   */ import javax.xml.bind.annotation.XmlType;
/* 10:   */ 
/* 11:   */ @XmlAccessorType(XmlAccessType.FIELD)
/* 12:   */ @XmlType(name="", propOrder={"extension"})
/* 13:   */ @XmlRootElement(name="tanimoto")
/* 14:   */ public class Tanimoto
/* 15:   */ {
/* 16:   */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/* 17:   */   protected List<Extension> extension;
/* 18:   */   
/* 19:   */   public List<Extension> getExtension()
/* 20:   */   {
/* 21:74 */     if (this.extension == null) {
/* 22:75 */       this.extension = new ArrayList();
/* 23:   */     }
/* 24:77 */     return this.extension;
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Tanimoto
 * JD-Core Version:    0.7.0.1
 */