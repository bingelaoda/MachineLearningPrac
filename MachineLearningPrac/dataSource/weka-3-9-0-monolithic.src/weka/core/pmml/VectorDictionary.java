/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import org.w3c.dom.Element;
/*   9:    */ import org.w3c.dom.Node;
/*  10:    */ import org.w3c.dom.NodeList;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Instances;
/*  13:    */ 
/*  14:    */ public class VectorDictionary
/*  15:    */   implements Serializable
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -5538024467333813123L;
/*  18:    */   protected int m_numberOfVectors;
/*  19: 54 */   protected List<FieldRef> m_vectorFields = new ArrayList();
/*  20: 57 */   protected Map<String, VectorInstance> m_vectorInstances = new HashMap();
/*  21:    */   
/*  22:    */   public static VectorDictionary getVectorDictionary(Element container, MiningSchema ms)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 71 */     VectorDictionary vectDict = null;
/*  26:    */     
/*  27: 73 */     NodeList vecL = container.getElementsByTagName("VectorDictionary");
/*  28: 74 */     if (vecL.getLength() > 0)
/*  29:    */     {
/*  30: 75 */       Node vecNode = vecL.item(0);
/*  31: 76 */       if (vecNode.getNodeType() == 1) {
/*  32: 77 */         vectDict = new VectorDictionary((Element)vecNode, ms);
/*  33:    */       }
/*  34:    */     }
/*  35: 81 */     return vectDict;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double[] incomingInstanceToVectorFieldVals(double[] incoming)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41: 95 */     double[] newInst = new double[this.m_vectorFields.size()];
/*  42: 97 */     for (int i = 0; i < this.m_vectorFields.size(); i++)
/*  43:    */     {
/*  44: 98 */       FieldRef fr = (FieldRef)this.m_vectorFields.get(i);
/*  45: 99 */       newInst[i] = fr.getResult(incoming);
/*  46:    */     }
/*  47:102 */     return newInst;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public VectorDictionary(Element vectNode, MiningSchema ms)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:114 */     NodeList vecFieldsL = vectNode.getElementsByTagName("VectorFields");
/*  54:115 */     if (vecFieldsL.getLength() == 0) {
/*  55:116 */       throw new Exception("[VectorDictionary] there are no VectorFields defined!!");
/*  56:    */     }
/*  57:119 */     Instances fullStructure = ms.getFieldsAsInstances();
/*  58:120 */     ArrayList<Attribute> fieldDefs = new ArrayList();
/*  59:121 */     for (int i = 0; i < fullStructure.numAttributes(); i++) {
/*  60:122 */       fieldDefs.add(fullStructure.attribute(i));
/*  61:    */     }
/*  62:126 */     Node fieldsNode = vecFieldsL.item(0);
/*  63:    */     
/*  64:128 */     NodeList fieldRefsL = ((Element)fieldsNode).getElementsByTagName("FieldRef");
/*  65:131 */     for (int i = 0; i < fieldRefsL.getLength(); i++)
/*  66:    */     {
/*  67:132 */       Element fieldR = (Element)fieldRefsL.item(i);
/*  68:133 */       String fieldName = fieldR.getAttribute("field");
/*  69:134 */       Attribute a = fullStructure.attribute(fieldName);
/*  70:136 */       if (a == null) {
/*  71:137 */         throw new Exception("[VectorDictionary] can't find field '" + fieldName + "' in the mining schema/derived fields!");
/*  72:    */       }
/*  73:141 */       FieldMetaInfo.Optype fieldOpt = a.isNumeric() ? FieldMetaInfo.Optype.CONTINUOUS : FieldMetaInfo.Optype.CATEGORICAL;
/*  74:    */       
/*  75:    */ 
/*  76:    */ 
/*  77:145 */       FieldRef fr = new FieldRef(fieldR, fieldOpt, fieldDefs);
/*  78:146 */       this.m_vectorFields.add(fr);
/*  79:    */     }
/*  80:150 */     NodeList vecInstL = vectNode.getElementsByTagName("VectorInstance");
/*  81:152 */     if (vecInstL.getLength() == 0) {
/*  82:153 */       throw new Exception("[VectorDictionary] no VectorInstances defined!");
/*  83:    */     }
/*  84:156 */     for (int i = 0; i < vecInstL.getLength(); i++)
/*  85:    */     {
/*  86:157 */       Element vecInstEl = (Element)vecInstL.item(i);
/*  87:158 */       VectorInstance temp = new VectorInstance(vecInstEl, this.m_vectorFields);
/*  88:159 */       String id = temp.getID();
/*  89:160 */       if (this.m_vectorInstances.get(id) != null) {
/*  90:161 */         throw new Exception("[VectorDictionary] : There is already a vector with ID " + id + " in the dictionary!");
/*  91:    */       }
/*  92:164 */       this.m_vectorInstances.put(id, temp);
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public VectorInstance getVector(String ID)
/*  97:    */   {
/*  98:176 */     return (VectorInstance)this.m_vectorInstances.get(ID);
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.VectorDictionary
 * JD-Core Version:    0.7.0.1
 */