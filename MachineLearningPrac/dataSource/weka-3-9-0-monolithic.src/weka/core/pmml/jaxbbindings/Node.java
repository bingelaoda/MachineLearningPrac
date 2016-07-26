/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"content"})
/*  14:    */ @XmlRootElement(name="Node")
/*  15:    */ public class Node
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="Regression", namespace="http://www.dmg.org/PMML-4_1", type=Regression.class), @javax.xml.bind.annotation.XmlElementRef(name="SimplePredicate", namespace="http://www.dmg.org/PMML-4_1", type=SimplePredicate.class), @javax.xml.bind.annotation.XmlElementRef(name="SimpleSetPredicate", namespace="http://www.dmg.org/PMML-4_1", type=SimpleSetPredicate.class), @javax.xml.bind.annotation.XmlElementRef(name="False", namespace="http://www.dmg.org/PMML-4_1", type=False.class), @javax.xml.bind.annotation.XmlElementRef(name="Node", namespace="http://www.dmg.org/PMML-4_1", type=Node.class), @javax.xml.bind.annotation.XmlElementRef(name="ScoreDistribution", namespace="http://www.dmg.org/PMML-4_1", type=ScoreDistribution.class), @javax.xml.bind.annotation.XmlElementRef(name="DecisionTree", namespace="http://www.dmg.org/PMML-4_1", type=DecisionTree.class), @javax.xml.bind.annotation.XmlElementRef(name="CompoundPredicate", namespace="http://www.dmg.org/PMML-4_1", type=CompoundPredicate.class), @javax.xml.bind.annotation.XmlElementRef(name="True", namespace="http://www.dmg.org/PMML-4_1", type=True.class), @javax.xml.bind.annotation.XmlElementRef(name="Partition", namespace="http://www.dmg.org/PMML-4_1", type=Partition.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String defaultChild;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String id;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double recordCount;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String score;
/*  27:    */   
/*  28:    */   public List<Object> getContent()
/*  29:    */   {
/*  30:129 */     if (this.content == null) {
/*  31:130 */       this.content = new ArrayList();
/*  32:    */     }
/*  33:132 */     return this.content;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getDefaultChild()
/*  37:    */   {
/*  38:144 */     return this.defaultChild;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setDefaultChild(String value)
/*  42:    */   {
/*  43:156 */     this.defaultChild = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getId()
/*  47:    */   {
/*  48:168 */     return this.id;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setId(String value)
/*  52:    */   {
/*  53:180 */     this.id = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Double getRecordCount()
/*  57:    */   {
/*  58:192 */     return this.recordCount;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setRecordCount(Double value)
/*  62:    */   {
/*  63:204 */     this.recordCount = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getScore()
/*  67:    */   {
/*  68:216 */     return this.score;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setScore(String value)
/*  72:    */   {
/*  73:228 */     this.score = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Node
 * JD-Core Version:    0.7.0.1
 */