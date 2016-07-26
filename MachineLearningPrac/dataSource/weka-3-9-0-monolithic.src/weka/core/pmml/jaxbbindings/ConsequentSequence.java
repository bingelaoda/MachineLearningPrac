/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"extension", "sequenceReference", "time"})
/*  13:    */ @XmlRootElement(name="ConsequentSequence")
/*  14:    */ public class ConsequentSequence
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="SequenceReference", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected SequenceReference sequenceReference;
/*  20:    */   @XmlElement(name="Time", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected Time time;
/*  22:    */   
/*  23:    */   public List<Extension> getExtension()
/*  24:    */   {
/*  25: 80 */     if (this.extension == null) {
/*  26: 81 */       this.extension = new ArrayList();
/*  27:    */     }
/*  28: 83 */     return this.extension;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public SequenceReference getSequenceReference()
/*  32:    */   {
/*  33: 95 */     return this.sequenceReference;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setSequenceReference(SequenceReference value)
/*  37:    */   {
/*  38:107 */     this.sequenceReference = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Time getTime()
/*  42:    */   {
/*  43:119 */     return this.time;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setTime(Time value)
/*  47:    */   {
/*  48:131 */     this.time = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ConsequentSequence
 * JD-Core Version:    0.7.0.1
 */