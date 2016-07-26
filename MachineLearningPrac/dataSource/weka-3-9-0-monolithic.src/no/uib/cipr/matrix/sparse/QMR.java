/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Matrix;
/*   4:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ import no.uib.cipr.matrix.Vector.Norm;
/*   7:    */ 
/*   8:    */ public class QMR
/*   9:    */   extends AbstractIterativeSolver
/*  10:    */ {
/*  11:    */   private Preconditioner M1;
/*  12:    */   private Preconditioner M2;
/*  13:    */   private Vector r;
/*  14:    */   private Vector y;
/*  15:    */   private Vector z;
/*  16:    */   private Vector v;
/*  17:    */   private Vector w;
/*  18:    */   private Vector p;
/*  19:    */   private Vector q;
/*  20:    */   private Vector d;
/*  21:    */   private Vector s;
/*  22:    */   private Vector v_tld;
/*  23:    */   private Vector w_tld;
/*  24:    */   private Vector y_tld;
/*  25:    */   private Vector z_tld;
/*  26:    */   private Vector p_tld;
/*  27:    */   
/*  28:    */   public QMR(Vector template)
/*  29:    */   {
/*  30: 66 */     this.M1 = this.M;
/*  31: 67 */     this.M2 = this.M;
/*  32: 68 */     this.r = template.copy();
/*  33: 69 */     this.y = template.copy();
/*  34: 70 */     this.z = template.copy();
/*  35: 71 */     this.v = template.copy();
/*  36: 72 */     this.w = template.copy();
/*  37: 73 */     this.p = template.copy();
/*  38: 74 */     this.q = template.copy();
/*  39: 75 */     this.d = template.copy();
/*  40: 76 */     this.s = template.copy();
/*  41: 77 */     this.v_tld = template.copy();
/*  42: 78 */     this.w_tld = template.copy();
/*  43: 79 */     this.y_tld = template.copy();
/*  44: 80 */     this.z_tld = template.copy();
/*  45: 81 */     this.p_tld = template.copy();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public QMR(Vector template, Preconditioner M1, Preconditioner M2)
/*  49:    */   {
/*  50: 99 */     this.M1 = M1;
/*  51:100 */     this.M2 = M2;
/*  52:101 */     this.r = template.copy();
/*  53:102 */     this.y = template.copy();
/*  54:103 */     this.z = template.copy();
/*  55:104 */     this.v = template.copy();
/*  56:105 */     this.w = template.copy();
/*  57:106 */     this.p = template.copy();
/*  58:107 */     this.q = template.copy();
/*  59:108 */     this.d = template.copy();
/*  60:109 */     this.s = template.copy();
/*  61:110 */     this.v_tld = template.copy();
/*  62:111 */     this.w_tld = template.copy();
/*  63:112 */     this.y_tld = template.copy();
/*  64:113 */     this.z_tld = template.copy();
/*  65:114 */     this.p_tld = template.copy();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  69:    */     throws IterativeSolverNotConvergedException
/*  70:    */   {
/*  71:119 */     checkSizes(A, b, x);
/*  72:    */     
/*  73:121 */     double rho = 0.0D;double rho_1 = 0.0D;double xi = 0.0D;double gamma = 1.0D;double gamma_1 = 0.0D;double theta = 0.0D;double theta_1 = 0.0D;double eta = -1.0D;double delta = 0.0D;double ep = 0.0D;double beta = 0.0D;
/*  74:    */     
/*  75:123 */     A.multAdd(-1.0D, x, this.r.set(b));
/*  76:    */     
/*  77:125 */     this.v_tld.set(this.r);
/*  78:126 */     this.M1.apply(this.v_tld, this.y);
/*  79:127 */     rho = this.y.norm(Vector.Norm.Two);
/*  80:    */     
/*  81:129 */     this.w_tld.set(this.r);
/*  82:130 */     this.M2.transApply(this.w_tld, this.z);
/*  83:131 */     xi = this.z.norm(Vector.Norm.Two);
/*  84:133 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  85:    */     {
/*  86:135 */       if (rho == 0.0D) {
/*  87:136 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "rho", this.iter);
/*  88:    */       }
/*  89:139 */       if (xi == 0.0D) {
/*  90:140 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "xi", this.iter);
/*  91:    */       }
/*  92:143 */       this.v.set(1.0D / rho, this.v_tld);
/*  93:144 */       this.y.scale(1.0D / rho);
/*  94:145 */       this.w.set(1.0D / xi, this.w_tld);
/*  95:146 */       this.z.scale(1.0D / xi);
/*  96:    */       
/*  97:148 */       delta = this.z.dot(this.y);
/*  98:150 */       if (delta == 0.0D) {
/*  99:151 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "delta", this.iter);
/* 100:    */       }
/* 101:154 */       this.M2.apply(this.y, this.y_tld);
/* 102:155 */       this.M1.transApply(this.z, this.z_tld);
/* 103:157 */       if (this.iter.isFirst())
/* 104:    */       {
/* 105:158 */         this.p.set(this.y_tld);
/* 106:159 */         this.q.set(this.z_tld);
/* 107:    */       }
/* 108:    */       else
/* 109:    */       {
/* 110:161 */         this.p.scale(-xi * delta / ep).add(this.y_tld);
/* 111:162 */         this.q.scale(-rho * delta / ep).add(this.z_tld);
/* 112:    */       }
/* 113:165 */       A.mult(this.p, this.p_tld);
/* 114:    */       
/* 115:167 */       ep = this.q.dot(this.p_tld);
/* 116:169 */       if (ep == 0.0D) {
/* 117:170 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "ep", this.iter);
/* 118:    */       }
/* 119:173 */       beta = ep / delta;
/* 120:175 */       if (beta == 0.0D) {
/* 121:176 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "beta", this.iter);
/* 122:    */       }
/* 123:179 */       this.v_tld.set(-beta, this.v).add(this.p_tld);
/* 124:180 */       this.M1.apply(this.v_tld, this.y);
/* 125:181 */       rho_1 = rho;
/* 126:182 */       rho = this.y.norm(Vector.Norm.Two);
/* 127:    */       
/* 128:184 */       A.transMultAdd(this.q, this.w_tld.set(-beta, this.w));
/* 129:185 */       this.M2.transApply(this.w_tld, this.z);
/* 130:186 */       xi = this.z.norm(Vector.Norm.Two);
/* 131:    */       
/* 132:188 */       gamma_1 = gamma;
/* 133:189 */       theta_1 = theta;
/* 134:190 */       theta = rho / (gamma_1 * beta);
/* 135:191 */       gamma = 1.0D / Math.sqrt(1.0D + theta * theta);
/* 136:193 */       if (gamma == 0.0D) {
/* 137:194 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "gamma", this.iter);
/* 138:    */       }
/* 139:197 */       eta = -eta * rho_1 * gamma * gamma / (beta * gamma_1 * gamma_1);
/* 140:199 */       if (this.iter.isFirst())
/* 141:    */       {
/* 142:200 */         this.d.set(eta, this.p);
/* 143:201 */         this.s.set(eta, this.p_tld);
/* 144:    */       }
/* 145:    */       else
/* 146:    */       {
/* 147:203 */         double val = theta_1 * theta_1 * gamma * gamma;
/* 148:204 */         this.d.scale(val).add(eta, this.p);
/* 149:205 */         this.s.scale(val).add(eta, this.p_tld);
/* 150:    */       }
/* 151:208 */       x.add(this.d);
/* 152:209 */       this.r.add(-1.0D, this.s);
/* 153:    */     }
/* 154:212 */     return x;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setPreconditioner(Preconditioner M)
/* 158:    */   {
/* 159:217 */     super.setPreconditioner(M);
/* 160:218 */     this.M1 = M;
/* 161:219 */     this.M2 = M;
/* 162:    */   }
/* 163:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.QMR
 * JD-Core Version:    0.7.0.1
 */