/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class DerivedFieldMetaInfo
/*  12:    */   extends FieldMetaInfo
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 875736989396755241L;
/*  16: 40 */   protected String m_displayName = null;
/*  17: 47 */   protected ArrayList<FieldMetaInfo.Value> m_values = new ArrayList();
/*  18:    */   protected Expression m_expression;
/*  19:    */   
/*  20:    */   public DerivedFieldMetaInfo(Element derivedField, ArrayList<Attribute> fieldDefs, TransformationDictionary transDict)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 54 */     super(derivedField);
/*  24:    */     
/*  25: 56 */     String displayName = derivedField.getAttribute("displayName");
/*  26: 57 */     if ((displayName != null) && (displayName.length() > 0)) {
/*  27: 58 */       this.m_displayName = displayName;
/*  28:    */     }
/*  29: 62 */     NodeList valL = derivedField.getElementsByTagName("Value");
/*  30: 63 */     if (valL.getLength() > 0) {
/*  31: 64 */       for (int i = 0; i < valL.getLength(); i++)
/*  32:    */       {
/*  33: 65 */         Node valueN = valL.item(i);
/*  34: 66 */         if (valueN.getNodeType() == 1)
/*  35:    */         {
/*  36: 67 */           FieldMetaInfo.Value v = new FieldMetaInfo.Value((Element)valueN);
/*  37: 68 */           this.m_values.add(v);
/*  38:    */         }
/*  39:    */       }
/*  40:    */     }
/*  41: 74 */     this.m_expression = Expression.getExpression(derivedField, this.m_optype, fieldDefs, transDict);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47: 85 */     this.m_expression.setFieldDefs(fieldDefs);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setFieldDefs(Instances fields)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53: 95 */     ArrayList<Attribute> tempDefs = new ArrayList();
/*  54: 96 */     for (int i = 0; i < fields.numAttributes(); i++) {
/*  55: 97 */       tempDefs.add(fields.attribute(i));
/*  56:    */     }
/*  57: 99 */     setFieldDefs(tempDefs);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Attribute getFieldAsAttribute()
/*  61:    */   {
/*  62:109 */     return this.m_expression.getOutputDef().copy(this.m_fieldName);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public double getDerivedValue(double[] incoming)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:128 */     return this.m_expression.getResult(incoming);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String toString()
/*  72:    */   {
/*  73:133 */     StringBuffer buff = new StringBuffer();
/*  74:134 */     buff.append(getFieldAsAttribute() + "\nexpression:\n");
/*  75:135 */     buff.append(this.m_expression + "\n");
/*  76:    */     
/*  77:137 */     return buff.toString();
/*  78:    */   }
/*  79:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.DerivedFieldMetaInfo
 * JD-Core Version:    0.7.0.1
 */