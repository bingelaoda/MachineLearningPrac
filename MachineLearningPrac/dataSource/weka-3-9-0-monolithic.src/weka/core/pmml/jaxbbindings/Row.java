/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  6:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  7:   */ import javax.xml.bind.annotation.XmlAnyElement;
/*  8:   */ import javax.xml.bind.annotation.XmlMixed;
/*  9:   */ import javax.xml.bind.annotation.XmlRootElement;
/* 10:   */ import javax.xml.bind.annotation.XmlType;
/* 11:   */ 
/* 12:   */ @XmlAccessorType(XmlAccessType.FIELD)
/* 13:   */ @XmlType(name="", propOrder={"content"})
/* 14:   */ @XmlRootElement(name="row")
/* 15:   */ public class Row
/* 16:   */ {
/* 17:   */   @XmlMixed
/* 18:   */   @XmlAnyElement
/* 19:   */   protected List<Object> content;
/* 20:   */   
/* 21:   */   public List<Object> getContent()
/* 22:   */   {
/* 23:78 */     if (this.content == null) {
/* 24:79 */       this.content = new ArrayList();
/* 25:   */     }
/* 26:81 */     return this.content;
/* 27:   */   }
/* 28:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Row
 * JD-Core Version:    0.7.0.1
 */