/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Comparator;
/*   9:    */ 
/*  10:    */ public class InstanceComparator
/*  11:    */   implements Comparator<Instance>, Serializable, RevisionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -6589278678230949683L;
/*  14:    */   protected boolean m_IncludeClass;
/*  15:    */   protected Range m_Range;
/*  16:    */   
/*  17:    */   public InstanceComparator()
/*  18:    */   {
/*  19: 61 */     this(true);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public InstanceComparator(boolean includeClass)
/*  23:    */   {
/*  24: 70 */     this(includeClass, "first-last", false);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public InstanceComparator(boolean includeClass, String range, boolean invert)
/*  28:    */   {
/*  29: 83 */     this.m_Range = new Range();
/*  30:    */     
/*  31: 85 */     setIncludeClass(includeClass);
/*  32: 86 */     setRange(range);
/*  33: 87 */     setInvert(invert);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setIncludeClass(boolean includeClass)
/*  37:    */   {
/*  38: 96 */     this.m_IncludeClass = includeClass;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public boolean getIncludeClass()
/*  42:    */   {
/*  43:105 */     return this.m_IncludeClass;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setRange(String value)
/*  47:    */   {
/*  48:114 */     this.m_Range.setRanges(value);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getRange()
/*  52:    */   {
/*  53:123 */     return this.m_Range.getRanges();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setInvert(boolean value)
/*  57:    */   {
/*  58:132 */     this.m_Range.setInvert(value);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public boolean getInvert()
/*  62:    */   {
/*  63:141 */     return this.m_Range.getInvert();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int compare(Instance inst1, Instance inst2)
/*  67:    */   {
/*  68:163 */     this.m_Range.setUpper(inst1.numAttributes() - 1);
/*  69:    */     int classindex;
/*  70:    */     int classindex;
/*  71:166 */     if (inst1.classIndex() == -1) {
/*  72:167 */       classindex = inst1.numAttributes() - 1;
/*  73:    */     } else {
/*  74:169 */       classindex = inst1.classIndex();
/*  75:    */     }
/*  76:171 */     int result = 0;
/*  77:    */     label364:
/*  78:172 */     for (int i = 0; i < inst1.numAttributes(); i++) {
/*  79:174 */       if (this.m_Range.isInRange(i)) {
/*  80:178 */         if ((getIncludeClass()) || (i != classindex)) {
/*  81:183 */           if ((inst1.isMissing(i)) || (inst2.isMissing(i)))
/*  82:    */           {
/*  83:184 */             if ((!inst1.isMissing(i)) || (!inst2.isMissing(i)))
/*  84:    */             {
/*  85:188 */               if (inst1.isMissing(i))
/*  86:    */               {
/*  87:189 */                 result = -1; break;
/*  88:    */               }
/*  89:191 */               result = 1;
/*  90:192 */               break;
/*  91:    */             }
/*  92:    */           }
/*  93:    */           else
/*  94:    */           {
/*  95:    */             Instances data1;
/*  96:    */             Instances data2;
/*  97:    */             int n;
/*  98:    */             InstanceComparator comp;
/*  99:197 */             switch (inst1.attribute(i).type())
/* 100:    */             {
/* 101:    */             case 2: 
/* 102:199 */               result = inst1.stringValue(i).compareTo(inst2.stringValue(i));
/* 103:200 */               break;
/* 104:    */             case 4: 
/* 105:202 */               data1 = inst1.relationalValue(i);
/* 106:203 */               data2 = inst2.relationalValue(i);
/* 107:204 */               n = 0;
/* 108:205 */               comp = new InstanceComparator();
/* 109:    */             default: 
/* 110:206 */               while ((n < data1.numInstances()) && (n < data2.numInstances()) && (result == 0))
/* 111:    */               {
/* 112:207 */                 result = comp.compare(data1.instance(n), data2.instance(n));
/* 113:208 */                 n++; continue;
/* 114:212 */                 if (Utils.eq(inst1.value(i), inst2.value(i))) {
/* 115:    */                   break label364;
/* 116:    */                 }
/* 117:216 */                 if (inst1.value(i) < inst2.value(i)) {
/* 118:217 */                   result = -1;
/* 119:    */                 } else {
/* 120:219 */                   result = 1;
/* 121:    */                 }
/* 122:    */               }
/* 123:    */             }
/* 124:224 */             if (result != 0) {
/* 125:    */               break;
/* 126:    */             }
/* 127:    */           }
/* 128:    */         }
/* 129:    */       }
/* 130:    */     }
/* 131:228 */     return result;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String getRevision()
/* 135:    */   {
/* 136:237 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static void main(String[] args)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:248 */     if (args.length == 0) {
/* 143:249 */       return;
/* 144:    */     }
/* 145:252 */     Instances inst = new Instances(new BufferedReader(new FileReader(args[0])));
/* 146:253 */     inst.setClassIndex(inst.numAttributes() - 1);
/* 147:    */     
/* 148:    */ 
/* 149:256 */     Comparator<Instance> comp = new InstanceComparator();
/* 150:257 */     System.out.println("\nIncluding the class");
/* 151:258 */     System.out.println("comparing 1. instance with 1.: " + comp.compare(inst.instance(0), inst.instance(0)));
/* 152:259 */     System.out.println("comparing 1. instance with 2.: " + comp.compare(inst.instance(0), inst.instance(1)));
/* 153:260 */     System.out.println("comparing 2. instance with 1.: " + comp.compare(inst.instance(1), inst.instance(0)));
/* 154:    */     
/* 155:    */ 
/* 156:263 */     comp = new InstanceComparator(false);
/* 157:264 */     System.out.println("\nExcluding the class");
/* 158:265 */     System.out.println("comparing 1. instance with 1.: " + comp.compare(inst.instance(0), inst.instance(0)));
/* 159:266 */     System.out.println("comparing 1. instance with 2.: " + comp.compare(inst.instance(0), inst.instance(1)));
/* 160:267 */     System.out.println("comparing 2. instance with 1.: " + comp.compare(inst.instance(1), inst.instance(0)));
/* 161:    */     
/* 162:    */ 
/* 163:270 */     Instances tmp = new Instances(inst);
/* 164:271 */     Collections.sort(tmp, new InstanceComparator(false));
/* 165:272 */     System.out.println("\nSorted on all attributes");
/* 166:273 */     System.out.println(tmp);
/* 167:    */     
/* 168:    */ 
/* 169:276 */     tmp = new Instances(inst);
/* 170:277 */     Collections.sort(tmp, new InstanceComparator(false, "2", false));
/* 171:278 */     System.out.println("\nSorted on 2nd attribute");
/* 172:279 */     System.out.println(tmp);
/* 173:    */   }
/* 174:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.InstanceComparator
 * JD-Core Version:    0.7.0.1
 */