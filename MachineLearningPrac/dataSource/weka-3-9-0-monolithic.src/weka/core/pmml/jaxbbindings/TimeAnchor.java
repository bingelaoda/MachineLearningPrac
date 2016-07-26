/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"timeCycle", "timeException"})
/*  15:    */ @XmlRootElement(name="TimeAnchor")
/*  16:    */ public class TimeAnchor
/*  17:    */ {
/*  18:    */   @XmlElement(name="TimeCycle", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<TimeCycle> timeCycle;
/*  20:    */   @XmlElement(name="TimeException", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<TimeException> timeException;
/*  22:    */   @XmlAttribute
/*  23:    */   protected String displayName;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger offset;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigInteger stepsize;
/*  28:    */   @XmlAttribute
/*  29:    */   protected TIMEANCHOR2 type;
/*  30:    */   
/*  31:    */   public List<TimeCycle> getTimeCycle()
/*  32:    */   {
/*  33: 92 */     if (this.timeCycle == null) {
/*  34: 93 */       this.timeCycle = new ArrayList();
/*  35:    */     }
/*  36: 95 */     return this.timeCycle;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public List<TimeException> getTimeException()
/*  40:    */   {
/*  41:121 */     if (this.timeException == null) {
/*  42:122 */       this.timeException = new ArrayList();
/*  43:    */     }
/*  44:124 */     return this.timeException;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getDisplayName()
/*  48:    */   {
/*  49:136 */     return this.displayName;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setDisplayName(String value)
/*  53:    */   {
/*  54:148 */     this.displayName = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public BigInteger getOffset()
/*  58:    */   {
/*  59:160 */     return this.offset;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setOffset(BigInteger value)
/*  63:    */   {
/*  64:172 */     this.offset = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public BigInteger getStepsize()
/*  68:    */   {
/*  69:184 */     return this.stepsize;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setStepsize(BigInteger value)
/*  73:    */   {
/*  74:196 */     this.stepsize = value;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public TIMEANCHOR2 getType()
/*  78:    */   {
/*  79:208 */     return this.type;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setType(TIMEANCHOR2 value)
/*  83:    */   {
/*  84:220 */     this.type = value;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeAnchor
 * JD-Core Version:    0.7.0.1
 */