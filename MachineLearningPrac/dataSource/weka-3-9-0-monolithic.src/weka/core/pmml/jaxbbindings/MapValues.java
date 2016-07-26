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
/*  13:    */ @XmlType(name="", propOrder={"extension", "fieldColumnPair", "tableLocator", "inlineTable"})
/*  14:    */ @XmlRootElement(name="MapValues")
/*  15:    */ public class MapValues
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="FieldColumnPair", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<FieldColumnPair> fieldColumnPair;
/*  21:    */   @XmlElement(name="TableLocator", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected TableLocator tableLocator;
/*  23:    */   @XmlElement(name="InlineTable", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected InlineTable inlineTable;
/*  25:    */   @XmlAttribute
/*  26:    */   protected DATATYPE dataType;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String defaultValue;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String mapMissingTo;
/*  31:    */   @XmlAttribute(required=true)
/*  32:    */   protected String outputColumn;
/*  33:    */   
/*  34:    */   public List<Extension> getExtension()
/*  35:    */   {
/*  36:101 */     if (this.extension == null) {
/*  37:102 */       this.extension = new ArrayList();
/*  38:    */     }
/*  39:104 */     return this.extension;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public List<FieldColumnPair> getFieldColumnPair()
/*  43:    */   {
/*  44:130 */     if (this.fieldColumnPair == null) {
/*  45:131 */       this.fieldColumnPair = new ArrayList();
/*  46:    */     }
/*  47:133 */     return this.fieldColumnPair;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public TableLocator getTableLocator()
/*  51:    */   {
/*  52:145 */     return this.tableLocator;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setTableLocator(TableLocator value)
/*  56:    */   {
/*  57:157 */     this.tableLocator = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public InlineTable getInlineTable()
/*  61:    */   {
/*  62:169 */     return this.inlineTable;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setInlineTable(InlineTable value)
/*  66:    */   {
/*  67:181 */     this.inlineTable = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public DATATYPE getDataType()
/*  71:    */   {
/*  72:193 */     return this.dataType;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setDataType(DATATYPE value)
/*  76:    */   {
/*  77:205 */     this.dataType = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getDefaultValue()
/*  81:    */   {
/*  82:217 */     return this.defaultValue;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setDefaultValue(String value)
/*  86:    */   {
/*  87:229 */     this.defaultValue = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String getMapMissingTo()
/*  91:    */   {
/*  92:241 */     return this.mapMissingTo;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setMapMissingTo(String value)
/*  96:    */   {
/*  97:253 */     this.mapMissingTo = value;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String getOutputColumn()
/* 101:    */   {
/* 102:265 */     return this.outputColumn;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setOutputColumn(String value)
/* 106:    */   {
/* 107:277 */     this.outputColumn = value;
/* 108:    */   }
/* 109:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MapValues
 * JD-Core Version:    0.7.0.1
 */