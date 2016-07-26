/*   1:    */ package weka.experiment.xml;
/*   2:    */ 
/*   3:    */ import java.beans.PropertyDescriptor;
/*   4:    */ import java.io.BufferedInputStream;
/*   5:    */ import java.io.BufferedOutputStream;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.ObjectInputStream;
/*   9:    */ import java.io.ObjectOutputStream;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import java.util.Vector;
/*  12:    */ import org.w3c.dom.Document;
/*  13:    */ import org.w3c.dom.Element;
/*  14:    */ import weka.classifiers.Classifier;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.xml.PropertyHandler;
/*  17:    */ import weka.core.xml.XMLBasicSerialization;
/*  18:    */ import weka.core.xml.XMLDocument;
/*  19:    */ import weka.core.xml.XMLSerializationMethodHandler;
/*  20:    */ import weka.experiment.Experiment;
/*  21:    */ import weka.experiment.PropertyNode;
/*  22:    */ import weka.experiment.ResultProducer;
/*  23:    */ import weka.experiment.SplitEvaluator;
/*  24:    */ 
/*  25:    */ public class XMLExperiment
/*  26:    */   extends XMLBasicSerialization
/*  27:    */ {
/*  28:    */   public static final String NAME_CLASSFIRST = "classFirst";
/*  29:    */   public static final String NAME_PROPERTYNODE_VALUE = "value";
/*  30:    */   public static final String NAME_PROPERTYNODE_PARENTCLASS = "parentClass";
/*  31:    */   public static final String NAME_PROPERTYNODE_PROPERTY = "property";
/*  32:    */   
/*  33:    */   public XMLExperiment()
/*  34:    */     throws Exception
/*  35:    */   {}
/*  36:    */   
/*  37:    */   public void clear()
/*  38:    */     throws Exception
/*  39:    */   {
/*  40: 88 */     super.clear();
/*  41:    */     
/*  42:    */ 
/*  43: 91 */     this.m_Properties.addIgnored("__root__.options");
/*  44: 92 */     this.m_Properties.addIgnored(Experiment.class, "options");
/*  45:    */     
/*  46:    */ 
/*  47: 95 */     this.m_Properties.addAllowed(Classifier.class, "debug");
/*  48: 96 */     this.m_Properties.addAllowed(Classifier.class, "options");
/*  49:    */     
/*  50:    */ 
/*  51: 99 */     this.m_Properties.addAllowed(SplitEvaluator.class, "options");
/*  52:    */     
/*  53:    */ 
/*  54:102 */     this.m_Properties.addAllowed(ResultProducer.class, "options");
/*  55:    */     
/*  56:    */ 
/*  57:105 */     this.m_CustomMethods.register(this, PropertyNode.class, "PropertyNode");
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected void writePostProcess(Object o)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:120 */     Element node = addElement(this.m_Document.getDocument().getDocumentElement(), "classFirst", Boolean.class.getName(), false);
/*  64:    */     
/*  65:122 */     node.appendChild(node.getOwnerDocument().createTextNode(new Boolean(false).toString()));
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected Object readPostProcess(Object o)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:142 */     Experiment exp = (Experiment)o;
/*  72:    */     
/*  73:    */ 
/*  74:145 */     Vector<Element> children = XMLDocument.getChildTags(this.m_Document.getDocument().getDocumentElement());
/*  75:147 */     for (int i = 0; i < children.size(); i++)
/*  76:    */     {
/*  77:148 */       Element node = (Element)children.get(i);
/*  78:149 */       if (node.getAttribute("name").equals("classFirst"))
/*  79:    */       {
/*  80:150 */         exp.classFirst(new Boolean(XMLDocument.getContent(node)).booleanValue());
/*  81:    */         
/*  82:152 */         break;
/*  83:    */       }
/*  84:    */     }
/*  85:156 */     return o;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Element writePropertyNode(Element parent, Object o, String name)
/*  89:    */     throws Exception
/*  90:    */   {
/*  91:178 */     if (DEBUG) {
/*  92:179 */       trace(new Throwable(), name);
/*  93:    */     }
/*  94:182 */     this.m_CurrentNode = parent;
/*  95:    */     
/*  96:184 */     PropertyNode pnode = (PropertyNode)o;
/*  97:185 */     Element node = (Element)parent.appendChild(this.m_Document.getDocument().createElement("object"));
/*  98:    */     
/*  99:187 */     node.setAttribute("name", name);
/* 100:188 */     node.setAttribute("class", pnode.getClass().getName());
/* 101:189 */     node.setAttribute("primitive", "no");
/* 102:190 */     node.setAttribute("array", "no");
/* 103:192 */     if (pnode.value != null) {
/* 104:193 */       invokeWriteToXML(node, pnode.value, "value");
/* 105:    */     }
/* 106:195 */     if (pnode.parentClass != null) {
/* 107:196 */       invokeWriteToXML(node, pnode.parentClass.getName(), "parentClass");
/* 108:    */     }
/* 109:199 */     if (pnode.property != null) {
/* 110:200 */       invokeWriteToXML(node, pnode.property.getDisplayName(), "property");
/* 111:    */     }
/* 112:205 */     if ((pnode.value != null) && (pnode.property != null) && (pnode.property.getPropertyType().isPrimitive()))
/* 113:    */     {
/* 114:207 */       Vector<Element> children = XMLDocument.getChildTags(node);
/* 115:208 */       for (int i = 0; i < children.size(); i++)
/* 116:    */       {
/* 117:209 */         Element child = (Element)children.get(i);
/* 118:210 */         if (child.getAttribute("name").equals("value"))
/* 119:    */         {
/* 120:213 */           child.setAttribute("class", pnode.property.getPropertyType().getName());
/* 121:    */           
/* 122:215 */           child.setAttribute("primitive", "yes");
/* 123:    */         }
/* 124:    */       }
/* 125:    */     }
/* 126:219 */     return node;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public Object readPropertyNode(Element node)
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:241 */     if (DEBUG) {
/* 133:242 */       trace(new Throwable(), node.getAttribute("name"));
/* 134:    */     }
/* 135:245 */     this.m_CurrentNode = node;
/* 136:    */     
/* 137:247 */     Object result = null;
/* 138:    */     
/* 139:249 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 140:250 */     Object value = null;
/* 141:251 */     String parentClass = null;
/* 142:252 */     String property = null;
/* 143:254 */     for (int i = 0; i < children.size(); i++)
/* 144:    */     {
/* 145:255 */       Element child = (Element)children.get(i);
/* 146:257 */       if (child.getAttribute("name").equals("value")) {
/* 147:258 */         if (stringToBoolean(child.getAttribute("primitive"))) {
/* 148:259 */           value = getPrimitive(child);
/* 149:    */         } else {
/* 150:261 */           value = invokeReadFromXML(child);
/* 151:    */         }
/* 152:    */       }
/* 153:264 */       if (child.getAttribute("name").equals("parentClass")) {
/* 154:265 */         parentClass = XMLDocument.getContent(child);
/* 155:    */       }
/* 156:267 */       if (child.getAttribute("name").equals("property")) {
/* 157:268 */         property = XMLDocument.getContent(child);
/* 158:    */       }
/* 159:    */     }
/* 160:    */     Class<?> cls;
/* 161:    */     Class<?> cls;
/* 162:272 */     if (parentClass != null) {
/* 163:273 */       cls = Class.forName(parentClass);
/* 164:    */     } else {
/* 165:275 */       cls = null;
/* 166:    */     }
/* 167:278 */     if (cls != null) {
/* 168:279 */       result = new PropertyNode(value, new PropertyDescriptor(property, cls), cls);
/* 169:    */     } else {
/* 170:282 */       result = new PropertyNode(value);
/* 171:    */     }
/* 172:285 */     return result;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String getRevision()
/* 176:    */   {
/* 177:295 */     return RevisionUtils.extract("$Revision: 10204 $");
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static void main(String[] args)
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:310 */     if (args.length > 0) {
/* 184:312 */       if (args[0].toLowerCase().endsWith(".xml"))
/* 185:    */       {
/* 186:313 */         System.out.println(new XMLExperiment().read(args[0]).toString());
/* 187:    */       }
/* 188:    */       else
/* 189:    */       {
/* 190:318 */         FileInputStream fi = new FileInputStream(args[0]);
/* 191:319 */         ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/* 192:    */         
/* 193:321 */         Object o = oi.readObject();
/* 194:322 */         oi.close();
/* 195:    */         
/* 196:    */ 
/* 197:    */ 
/* 198:326 */         new XMLExperiment().write(new BufferedOutputStream(new FileOutputStream(args[0] + ".xml")), o);
/* 199:    */         
/* 200:    */ 
/* 201:329 */         FileOutputStream fo = new FileOutputStream(args[0] + ".exp");
/* 202:330 */         ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/* 203:    */         
/* 204:332 */         oo.writeObject(o);
/* 205:333 */         oo.close();
/* 206:    */       }
/* 207:    */     }
/* 208:    */   }
/* 209:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.xml.XMLExperiment
 * JD-Core Version:    0.7.0.1
 */