/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.DistanceFunction;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class LinearNNSearch
/*  15:    */   extends NearestNeighbourSearch
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 1915484723703917241L;
/*  18:    */   protected double[] m_Distances;
/*  19: 63 */   protected boolean m_SkipIdentical = false;
/*  20:    */   
/*  21:    */   public LinearNNSearch() {}
/*  22:    */   
/*  23:    */   public LinearNNSearch(Instances insts)
/*  24:    */   {
/*  25: 80 */     super(insts);
/*  26: 81 */     this.m_DistanceFunction.setInstances(insts);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 91 */     return "Class implementing the brute force search algorithm for nearest neighbour search.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:102 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:104 */     result.add(new Option("\tSkip identical instances (distances equal to zero).\n", "S", 1, "-S"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:108 */     result.addAll(Collections.list(super.listOptions()));
/*  43:    */     
/*  44:110 */     return result.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:129 */     super.setOptions(options);
/*  51:    */     
/*  52:131 */     setSkipIdentical(Utils.getFlag('S', options));
/*  53:    */     
/*  54:133 */     Utils.checkForRemainingOptions(options);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:142 */     Vector<String> result = new Vector();
/*  60:    */     
/*  61:144 */     Collections.addAll(result, super.getOptions());
/*  62:146 */     if (getSkipIdentical()) {
/*  63:147 */       result.add("-S");
/*  64:    */     }
/*  65:149 */     return (String[])result.toArray(new String[result.size()]);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String skipIdenticalTipText()
/*  69:    */   {
/*  70:159 */     return "Whether to skip identical instances (with distance 0 to the target)";
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setSkipIdentical(boolean skip)
/*  74:    */   {
/*  75:169 */     this.m_SkipIdentical = skip;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean getSkipIdentical()
/*  79:    */   {
/*  80:178 */     return this.m_SkipIdentical;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Instance nearestNeighbour(Instance target)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:191 */     return kNearestNeighbours(target, 1).instance(0);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Instances kNearestNeighbours(Instance target, int kNN)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:206 */     boolean print = false;
/*  93:208 */     if (this.m_Stats != null) {
/*  94:209 */       this.m_Stats.searchStart();
/*  95:    */     }
/*  96:211 */     NearestNeighbourSearch.MyHeap heap = new NearestNeighbourSearch.MyHeap(this, kNN);
/*  97:212 */     int firstkNN = 0;
/*  98:213 */     for (int i = 0; i < this.m_Instances.numInstances(); i++) {
/*  99:214 */       if (target != this.m_Instances.instance(i))
/* 100:    */       {
/* 101:216 */         if (this.m_Stats != null) {
/* 102:217 */           this.m_Stats.incrPointCount();
/* 103:    */         }
/* 104:218 */         if (firstkNN < kNN)
/* 105:    */         {
/* 106:219 */           if (print) {
/* 107:220 */             System.out.println("K(a): " + (heap.size() + heap.noOfKthNearest()));
/* 108:    */           }
/* 109:221 */           double distance = this.m_DistanceFunction.distance(target, this.m_Instances.instance(i), (1.0D / 0.0D), this.m_Stats);
/* 110:222 */           if ((distance == 0.0D) && (this.m_SkipIdentical))
/* 111:    */           {
/* 112:223 */             if (i >= this.m_Instances.numInstances() - 1) {
/* 113:226 */               heap.put(i, distance);
/* 114:    */             }
/* 115:    */           }
/* 116:    */           else
/* 117:    */           {
/* 118:227 */             heap.put(i, distance);
/* 119:228 */             firstkNN++;
/* 120:    */           }
/* 121:    */         }
/* 122:    */         else
/* 123:    */         {
/* 124:231 */           NearestNeighbourSearch.MyHeapElement temp = heap.peek();
/* 125:232 */           if (print) {
/* 126:233 */             System.out.println("K(b): " + (heap.size() + heap.noOfKthNearest()));
/* 127:    */           }
/* 128:234 */           double distance = this.m_DistanceFunction.distance(target, this.m_Instances.instance(i), temp.distance, this.m_Stats);
/* 129:235 */           if ((distance != 0.0D) || (!this.m_SkipIdentical)) {
/* 130:237 */             if (distance < temp.distance) {
/* 131:238 */               heap.putBySubstitute(i, distance);
/* 132:240 */             } else if (distance == temp.distance) {
/* 133:241 */               heap.putKthNearest(i, distance);
/* 134:    */             }
/* 135:    */           }
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:247 */     Instances neighbours = new Instances(this.m_Instances, heap.size() + heap.noOfKthNearest());
/* 140:248 */     this.m_Distances = new double[heap.size() + heap.noOfKthNearest()];
/* 141:249 */     int[] indices = new int[heap.size() + heap.noOfKthNearest()];
/* 142:250 */     int i = 1;
/* 143:251 */     while (heap.noOfKthNearest() > 0)
/* 144:    */     {
/* 145:252 */       NearestNeighbourSearch.MyHeapElement h = heap.getKthNearest();
/* 146:253 */       indices[(indices.length - i)] = h.index;
/* 147:254 */       this.m_Distances[(indices.length - i)] = h.distance;
/* 148:255 */       i++;
/* 149:    */     }
/* 150:257 */     while (heap.size() > 0)
/* 151:    */     {
/* 152:258 */       NearestNeighbourSearch.MyHeapElement h = heap.get();
/* 153:259 */       indices[(indices.length - i)] = h.index;
/* 154:260 */       this.m_Distances[(indices.length - i)] = h.distance;
/* 155:261 */       i++;
/* 156:    */     }
/* 157:264 */     this.m_DistanceFunction.postProcessDistances(this.m_Distances);
/* 158:266 */     for (int k = 0; k < indices.length; k++) {
/* 159:267 */       neighbours.add(this.m_Instances.instance(indices[k]));
/* 160:    */     }
/* 161:270 */     if (this.m_Stats != null) {
/* 162:271 */       this.m_Stats.searchFinish();
/* 163:    */     }
/* 164:273 */     return neighbours;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public double[] getDistances()
/* 168:    */     throws Exception
/* 169:    */   {
/* 170:294 */     if (this.m_Distances == null) {
/* 171:295 */       throw new Exception("No distances available. Please call either kNearestNeighbours or nearestNeighbours first.");
/* 172:    */     }
/* 173:297 */     return this.m_Distances;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setInstances(Instances insts)
/* 177:    */     throws Exception
/* 178:    */   {
/* 179:309 */     this.m_Instances = insts;
/* 180:310 */     this.m_DistanceFunction.setInstances(insts);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void update(Instance ins)
/* 184:    */     throws Exception
/* 185:    */   {
/* 186:325 */     if (this.m_Instances == null) {
/* 187:326 */       throw new Exception("No instances supplied yet. Cannot update withoutsupplying a set of instances first.");
/* 188:    */     }
/* 189:328 */     this.m_DistanceFunction.update(ins);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void addInstanceInfo(Instance ins)
/* 193:    */   {
/* 194:340 */     if (this.m_Instances != null) {
/* 195:    */       try
/* 196:    */       {
/* 197:341 */         update(ins);
/* 198:    */       }
/* 199:    */       catch (Exception ex)
/* 200:    */       {
/* 201:342 */         ex.printStackTrace();
/* 202:    */       }
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getRevision()
/* 207:    */   {
/* 208:351 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 209:    */   }
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.LinearNNSearch
 * JD-Core Version:    0.7.0.1
 */