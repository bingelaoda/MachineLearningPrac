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
/*  13:    */ @XmlType(name="", propOrder={"extension", "partitionFieldStats"})
/*  14:    */ @XmlRootElement(name="Partition")
/*  15:    */ public class Partition
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="PartitionFieldStats", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<PartitionFieldStats> partitionFieldStats;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String name;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double size;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 85 */     if (this.extension == null) {
/*  29: 86 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 88 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public List<PartitionFieldStats> getPartitionFieldStats()
/*  35:    */   {
/*  36:114 */     if (this.partitionFieldStats == null) {
/*  37:115 */       this.partitionFieldStats = new ArrayList();
/*  38:    */     }
/*  39:117 */     return this.partitionFieldStats;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getName()
/*  43:    */   {
/*  44:129 */     return this.name;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setName(String value)
/*  48:    */   {
/*  49:141 */     this.name = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Double getSize()
/*  53:    */   {
/*  54:153 */     return this.size;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setSize(Double value)
/*  58:    */   {
/*  59:165 */     this.size = value;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Partition
 * JD-Core Version:    0.7.0.1
 */