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
/*  13:    */ @XmlType(name="", propOrder={"timeAnchor", "timeValue"})
/*  14:    */ @XmlRootElement(name="TimeSeries")
/*  15:    */ public class TimeSeries
/*  16:    */ {
/*  17:    */   @XmlElement(name="TimeAnchor", namespace="http://www.dmg.org/PMML-4_1")
/*  18:    */   protected TimeAnchor timeAnchor;
/*  19:    */   @XmlElement(name="TimeValue", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<TimeValue> timeValue;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double endTime;
/*  23:    */   @XmlAttribute
/*  24:    */   protected INTERPOLATIONMETHOD interpolationMethod;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double startTime;
/*  27:    */   @XmlAttribute
/*  28:    */   protected TIMESERIESUSAGE usage;
/*  29:    */   
/*  30:    */   public TimeAnchor getTimeAnchor()
/*  31:    */   {
/*  32: 77 */     return this.timeAnchor;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setTimeAnchor(TimeAnchor value)
/*  36:    */   {
/*  37: 89 */     this.timeAnchor = value;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List<TimeValue> getTimeValue()
/*  41:    */   {
/*  42:115 */     if (this.timeValue == null) {
/*  43:116 */       this.timeValue = new ArrayList();
/*  44:    */     }
/*  45:118 */     return this.timeValue;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Double getEndTime()
/*  49:    */   {
/*  50:130 */     return this.endTime;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setEndTime(Double value)
/*  54:    */   {
/*  55:142 */     this.endTime = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public INTERPOLATIONMETHOD getInterpolationMethod()
/*  59:    */   {
/*  60:154 */     if (this.interpolationMethod == null) {
/*  61:155 */       return INTERPOLATIONMETHOD.NONE;
/*  62:    */     }
/*  63:157 */     return this.interpolationMethod;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setInterpolationMethod(INTERPOLATIONMETHOD value)
/*  67:    */   {
/*  68:170 */     this.interpolationMethod = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Double getStartTime()
/*  72:    */   {
/*  73:182 */     return this.startTime;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setStartTime(Double value)
/*  77:    */   {
/*  78:194 */     this.startTime = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public TIMESERIESUSAGE getUsage()
/*  82:    */   {
/*  83:206 */     if (this.usage == null) {
/*  84:207 */       return TIMESERIESUSAGE.ORIGINAL;
/*  85:    */     }
/*  86:209 */     return this.usage;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setUsage(TIMESERIESUSAGE value)
/*  90:    */   {
/*  91:222 */     this.usage = value;
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TimeSeries
 * JD-Core Version:    0.7.0.1
 */