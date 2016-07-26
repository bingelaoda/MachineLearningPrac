/*    1:     */ package weka.classifiers.sklearn;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.List;
/*    5:     */ import weka.classifiers.AbstractClassifier;
/*    6:     */ import weka.classifiers.rules.ZeroR;
/*    7:     */ import weka.core.Attribute;
/*    8:     */ import weka.core.AttributeStats;
/*    9:     */ import weka.core.BatchPredictor;
/*   10:     */ import weka.core.Capabilities;
/*   11:     */ import weka.core.Capabilities.Capability;
/*   12:     */ import weka.core.CapabilitiesHandler;
/*   13:     */ import weka.core.Instance;
/*   14:     */ import weka.core.Instances;
/*   15:     */ import weka.core.OptionMetadata;
/*   16:     */ import weka.core.SelectedTag;
/*   17:     */ import weka.core.Tag;
/*   18:     */ import weka.core.Utils;
/*   19:     */ import weka.core.WekaException;
/*   20:     */ import weka.filters.Filter;
/*   21:     */ import weka.filters.unsupervised.attribute.Remove;
/*   22:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   23:     */ import weka.python.PythonSession;
/*   24:     */ 
/*   25:     */ public class ScikitLearnClassifier
/*   26:     */   extends AbstractClassifier
/*   27:     */   implements BatchPredictor, CapabilitiesHandler
/*   28:     */ {
/*   29:     */   protected static final String TRAINING_DATA_ID = "scikit_classifier_training";
/*   30:     */   protected static final String TEST_DATA_ID = "scikit_classifier_test";
/*   31:     */   protected static final String MODEL_ID = "weka_scikit_learner";
/*   32:     */   private static final long serialVersionUID = -6212485658537766441L;
/*   33:     */   
/*   34:     */   public static enum Learner
/*   35:     */   {
/*   36:  64 */     DecisionTreeClassifier("tree", true, false, true, "\tclass_weight=None, criterion='gini', max_depth=None,\n\tmax_features=None, max_leaf_nodes=None, min_samples_leaf=1,\n\tmin_samples_split=2, min_weight_fraction_leaf=0.0,\n\trandom_state=None, splitter='best'"),  DecisionTreeRegressor("tree", false, true, false, "\tcriterion='mse', max_depth=None, max_features=None,\n\tmax_leaf_nodes=None, min_samples_leaf=1, min_samples_split=2,\n\tmin_weight_fraction_leaf=0.0, random_state=None,\n\tsplitter='best'"),  GaussianNB("naive_bayes", true, false, true, ""),  MultinomialNB("naive_bayes", true, false, true, "alpha=1.0, class_prior=None, fit_prior=True"),  BernoulliNB("naive_bayes", true, false, true, "alpha=1.0, binarize=0.0, class_prior=None, fit_prior=True"),  LDA("lda", true, false, true, "\tn_components=None, priors=None, shrinkage=None, solver='svd',\n\tstore_covariance=False, tol=0.0001"),  QDA("qda", true, false, true, "\tpriors=None, reg_param=0.0"),  LogisticRegression("linear_model", true, false, true, "\tC=1.0, class_weight=None, dual=False, fit_intercept=True,\n\tintercept_scaling=1, max_iter=100, multi_class='ovr',\n\tpenalty='l2', random_state=None, solver='liblinear', tol=0.0001,\n\tverbose=0"),  LogisticRegressionCV("linear_model", true, false, true, "\tCs=10, class_weight=None, cv=None, dual=False,\n\tfit_intercept=True, intercept_scaling=1.0, max_iter=100,\n\tmulti_class='ovr', n_jobs=1, penalty='l2', refit=True,\n\tscoring=None, solver='lbfgs', tol=0.0001, verbose=0"),  LinearRegression("linear_model", false, true, false, "\tcopy_X=True, fit_intercept=True, n_jobs=1, normalize=False"),  ARDRegression("linear_model", false, true, false, "\talpha_1=1e-06, alpha_2=1e-06, compute_score=False, copy_X=True,\n\tfit_intercept=True, lambda_1=1e-06, lambda_2=1e-06, n_iter=300,\n\tnormalize=False, threshold_lambda=10000.0, tol=0.001, verbose=False"),  BayesianRidge("linear_model", false, true, false, "\talpha_1=1e-06, alpha_2=1e-06, compute_score=False, copy_X=True,\n\tfit_intercept=True, lambda_1=1e-06, lambda_2=1e-06, n_iter=300,\n\tnormalize=False, tol=0.001, verbose=False"),  ElasticNet("linear_model", false, true, false, "\talpha=1.0, copy_X=True, fit_intercept=True, l1_ratio=0.5,\n\tmax_iter=1000, normalize=False, positive=False, precompute=False,\n\trandom_state=None, selection='cyclic', tol=0.0001, warm_start=False"),  Lars("linear_model", false, true, false, "\tcopy_X=True, eps=2.2204460492503131e-16, fit_intercept=True,\n\tfit_path=True, n_nonzero_coefs=500, normalize=True, precompute='auto',\n\tverbose=False"),  LarsCV("linear_model", false, true, false, "\tcopy_X=True, cv=None, eps=2.2204460492503131e-16, fit_intercept=True,\n\tmax_iter=500, max_n_alphas=1000, n_jobs=1, normalize=True,\n\tprecompute='auto', verbose=False"),  Lasso("linear_model", false, true, false, "\talpha=1.0, copy_X=True, fit_intercept=True, max_iter=1000,\n\tnormalize=False, positive=False, precompute=False, random_state=None,\n\tselection='cyclic', tol=0.0001, warm_start=False"),  LassoCV("linear_model", false, true, false, "\talphas=None, copy_X=True, cv=None, eps=0.001, fit_intercept=True,\n\tmax_iter=1000, n_alphas=100, n_jobs=1, normalize=False, positive=False,\n\tprecompute='auto', random_state=None, selection='cyclic', tol=0.0001,\n\tverbose=False"),  LassoLars("linear_model", false, true, false, "\talpha=1.0, copy_X=True, eps=2.2204460492503131e-16,\n\tfit_intercept=True, fit_path=True, max_iter=500, normalize=True,\n\tprecompute='auto', verbose=False"),  LassoLarsCV("linear_model", false, true, false, "\tcopy_X=True, cv=None, eps=2.2204460492503131e-16,\n\tfit_intercept=True, max_iter=500, max_n_alphas=1000, n_jobs=1,\n\tnormalize=True, precompute='auto', verbose=False"),  LassoLarsIC("linear_model", false, true, false, "\tcopy_X=True, criterion='aic', eps=2.2204460492503131e-16,\n\tfit_intercept=True, max_iter=500, normalize=True, precompute='auto',\n\tverbose=False"),  OrthogonalMatchingPursuit("linear_model", false, true, false, "\tfit_intercept=True, n_nonzero_coefs=None,\n\tnormalize=True, precompute='auto', tol=None"),  OrthogonalMatchingPursuitCV("linear_model", false, true, false, "\tcopy=True, cv=None, fit_intercept=True,\n\tmax_iter=None, n_jobs=1, normalize=True, verbose=False"),  PassiveAggressiveClassifier("linear_model", true, false, false, "\tC=1.0, fit_intercept=True, loss='hinge', n_iter=5,\n\tn_jobs=1, random_state=None, shuffle=True, verbose=0,\n\twarm_start=False"),  PassiveAggressiveRegressor("linear_model", false, true, false, "\tC=1.0, class_weight=None, epsilon=0.1,\n\tfit_intercept=True, loss='epsilon_insensitive', n_iter=5,\n\trandom_state=None, shuffle=True, verbose=0, warm_start=False"),  Perceptron("linear_model", true, false, false, "\talpha=0.0001, class_weight=None, eta0=1.0, fit_intercept=True,\n\tn_iter=5, n_jobs=1, penalty=None, random_state=0, shuffle=True,\n\tverbose=0, warm_start=False"),  RANSACRegressor("linear_model", false, true, false, "\tbase_estimator=None, is_data_valid=None, is_model_valid=None,\n\tmax_trials=100, min_samples=None, random_state=None,\n\tresidual_metric=None, residual_threshold=None, stop_n_inliers=inf,\n\tstop_probability=0.99, stop_score=inf"),  Ridge("linear_model", false, true, false, "\talpha=1.0, copy_X=True, fit_intercept=True, max_iter=None,\n\tnormalize=False, solver='auto', tol=0.001"),  RidgeClassifier("linear_model", true, false, false, "\talpha=1.0, class_weight=None, copy_X=True, fit_intercept=True,\n\tmax_iter=None, normalize=False, solver='auto', tol=0.001"),  RidgeClassifierCV("linear_model", true, false, false, "alphas=array([  0.1,   1. ,  10. ]), class_weight=None,\n\tcv=None, fit_intercept=True, normalize=False, scoring=None"),  RidgeCV("linear_model", false, true, false, "alphas=array([  0.1,   1. ,  10. ]), cv=None, fit_intercept=True,\n\tgcv_mode=None, normalize=False, scoring=None, store_cv_values=False"),  SGDClassifier("linear_model", true, false, false, "\talpha=0.0001, average=False, class_weight=None, epsilon=0.1,\n\teta0=0.0, fit_intercept=True, l1_ratio=0.15,\n\tlearning_rate='optimal', loss='hinge', n_iter=5, n_jobs=1,\n\tpenalty='l2', power_t=0.5, random_state=None, shuffle=True,\n\tverbose=0, warm_start=False"),  SGDRegressor("linear_model", false, true, false, "\talpha=0.0001, average=False, epsilon=0.1, eta0=0.01,\n\tfit_intercept=True, l1_ratio=0.15, learning_rate='invscaling',\n\tloss='squared_loss', n_iter=5, penalty='l2', power_t=0.25,\n\trandom_state=None, shuffle=True, verbose=0, warm_start=False"),  TheilSenRegressor("linear_model", false, true, false, "\tcopy_X=True, fit_intercept=True, max_iter=300,\n\tmax_subpopulation=10000, n_jobs=1, n_subsamples=None,\n\trandom_state=None, tol=0.001, verbose=False"),  GaussianProcess("gaussian_process", false, true, false, "\tregr='constant', corr='squared_exponential',\n\tbeta0=None, storage_mode='full', verbose=False, theta0=0.1,\n \tthetaL=None, thetaU=None, optimizer='fmin_cobyla', random_start=1,\n \tnormalize=True, nugget=2.2204460492503131e-15, random_state=None"),  KernelRidge("kernel_ridge", false, true, false, "\talpha=1, coef0=1, degree=3, gamma=None, kernel='linear',\n\tkernel_params=None"),  KNeighborsClassifier("neighbors", true, false, true, "\talgorithm='auto', leaf_size=30, metric='minkowski',\n\tmetric_params=None, n_neighbors=5, p=2, weights='uniform'"),  RadiusNeighborsClassifier("neighbors", true, false, false, "\talgorithm='auto', leaf_size=30, metric='minkowski',\n\tmetric_params=None, outlier_label=None, p=2, radius=1.0,\n\tweights='uniform'"),  KNeighborsRegressor("neighbors", false, true, false, "algorithm='auto', leaf_size=30, metric='minkowski',\n\tmetric_params=None, n_neighbors=5, p=2, weights='uniform'"),  RadiusNeighborsRegressor("neighbors", false, true, false, ""),  SVC("svm", true, false, false, "\tC=1.0, cache_size=200, class_weight=None, coef0=0.0, degree=3, gamma=0.0,\n\tkernel='rbf', max_iter=-1, probability=False, random_state=None,\n\tshrinking=True, tol=0.001, verbose=False"),  LinearSVC("svm", true, false, false, "\tC=1.0, class_weight=None, dual=True, fit_intercept=True,\n\tintercept_scaling=1, loss='squared_hinge', max_iter=1000,\n\tmulti_class='ovr', penalty='l2', random_state=None, tol=0.0001,\n\tverbose=0"),  NuSVC("svm", true, false, false, "\tcache_size=200, coef0=0.0, degree=3, gamma=0.0, kernel='rbf',\n\tmax_iter=-1, nu=0.5, probability=False, random_state=None,\n\tshrinking=True, tol=0.001, verbose=False"),  SVR("svm", false, true, false, "\tC=1.0, cache_size=200, coef0=0.0, degree=3, epsilon=0.1, gamma=0.0,\n\tkernel='rbf', max_iter=-1, shrinking=True, tol=0.001, verbose=False"),  NuSVR("svm", false, true, false, "\tC=1.0, cache_size=200, coef0=0.0, degree=3, gamma=0.0, kernel='rbf',\n\tmax_iter=-1, nu=0.5, shrinking=True, tol=0.001, verbose=False"),  AdaBoostClassifier("ensemble", true, false, true, "\talgorithm='SAMME.R', base_estimator=None,\n\tlearning_rate=1.0, n_estimators=50, random_state=None"),  AdaBoostRegressor("ensemble", false, true, false, "\tbase_estimator=None, learning_rate=1.0, loss='linear',\n\tn_estimators=50, random_state=None"),  BaggingClassifier("ensemble", true, false, true, "\tbase_estimator=None, bootstrap=True,\n\tbootstrap_features=False, max_features=1.0, max_samples=1.0,\n\tn_estimators=10, n_jobs=1, oob_score=False, random_state=None,\n\tverbose=0"),  BaggingRegressor("ensemble", false, true, false, "\tbase_estimator=None, bootstrap=True,\n\tbootstrap_features=False, max_features=1.0, max_samples=1.0,\n\tn_estimators=10, n_jobs=1, oob_score=False, random_state=None,\n\tverbose=0"),  ExtraTreeClassifier("tree", true, false, true, "\tclass_weight=None, criterion='gini', max_depth=None,\n\tmax_features='auto', max_leaf_nodes=None, min_samples_leaf=1,\n\tmin_samples_split=2, min_weight_fraction_leaf=0.0,\n\trandom_state=None, splitter='random'"),  ExtraTreeRegressor("tree", false, true, false, "\tcriterion='mse', max_depth=None, max_features='auto',\n\tmax_leaf_nodes=None, min_samples_leaf=1, min_samples_split=2,\n\tmin_weight_fraction_leaf=0.0, random_state=None,\n\tsplitter='random'"),  GradientBoostingClassifier("ensemble", true, false, true, "\tinit=None, learning_rate=0.1, loss='deviance',\n\tmax_depth=3, max_features=None, max_leaf_nodes=None,\n\tmin_samples_leaf=1, min_samples_split=2,\n\tmin_weight_fraction_leaf=0.0, n_estimators=100,\n\trandom_state=None, subsample=1.0, verbose=0,\n\twarm_start=False"),  GradientBoostingRegressor("ensemble", false, true, false, "\talpha=0.9, init=None, learning_rate=0.1, loss='ls',\n\tmax_depth=3, max_features=None, max_leaf_nodes=None,\n\tmin_samples_leaf=1, min_samples_split=2,\n\tmin_weight_fraction_leaf=0.0, n_estimators=100,\n\trandom_state=None, subsample=1.0, verbose=0, warm_start=False"),  RandomForestClassifier("ensemble", true, false, true, "\tbootstrap=True, class_weight=None, criterion='gini',\n\tmax_depth=None, max_features='auto', max_leaf_nodes=None,\n\tmin_samples_leaf=1, min_samples_split=2,\n\tmin_weight_fraction_leaf=0.0, n_estimators=10, n_jobs=1,\n\toob_score=False, random_state=None, verbose=0,\n\twarm_start=False"),  RandomForestRegressor("ensemble", false, true, false, "\tbootstrap=True, criterion='mse', max_depth=None,\n\tmax_features='auto', max_leaf_nodes=None, min_samples_leaf=1,\n\tmin_samples_split=2, min_weight_fraction_leaf=0.0,\n\tn_estimators=10, n_jobs=1, oob_score=False, random_state=None,\n\tverbose=0, warm_start=False");
/*   37:     */     
/*   38:     */     private String m_module;
/*   39:     */     private boolean m_classification;
/*   40:     */     private boolean m_regression;
/*   41:     */     private boolean m_producesProbabilities;
/*   42:     */     private String m_defaultParameters;
/*   43:     */     
/*   44:     */     private Learner(String module, boolean classification, boolean regression, boolean producesProbabilities, String defaultParameters)
/*   45:     */     {
/*   46: 345 */       this.m_module = module;
/*   47: 346 */       this.m_producesProbabilities = producesProbabilities;
/*   48: 347 */       this.m_classification = classification;
/*   49: 348 */       this.m_regression = regression;
/*   50: 349 */       this.m_defaultParameters = defaultParameters;
/*   51:     */     }
/*   52:     */     
/*   53:     */     public String getModule()
/*   54:     */     {
/*   55: 358 */       return this.m_module;
/*   56:     */     }
/*   57:     */     
/*   58:     */     public boolean producesProbabilities(String params)
/*   59:     */     {
/*   60: 371 */       return this.m_producesProbabilities;
/*   61:     */     }
/*   62:     */     
/*   63:     */     public boolean isClassifier()
/*   64:     */     {
/*   65: 380 */       return this.m_classification;
/*   66:     */     }
/*   67:     */     
/*   68:     */     public boolean isRegressor()
/*   69:     */     {
/*   70: 389 */       return this.m_regression;
/*   71:     */     }
/*   72:     */     
/*   73:     */     public String getDefaultParameters()
/*   74:     */     {
/*   75: 398 */       return this.m_defaultParameters;
/*   76:     */     }
/*   77:     */   }
/*   78:     */   
/*   79: 403 */   public static final Tag[] TAGS_LEARNER = new Tag[Learner.values().length];
/*   80:     */   
/*   81:     */   static
/*   82:     */   {
/*   83: 406 */     for (Learner l : Learner.values()) {
/*   84: 407 */       TAGS_LEARNER[l.ordinal()] = new Tag(l.ordinal(), l.toString());
/*   85:     */     }
/*   86:     */   }
/*   87:     */   
/*   88: 412 */   protected Learner m_learner = Learner.DecisionTreeClassifier;
/*   89: 415 */   protected String m_learnerOpts = "";
/*   90:     */   protected boolean m_useSupervisedNominalToBinary;
/*   91:     */   protected Filter m_nominalToBinary;
/*   92: 424 */   protected Filter m_replaceMissing = new ReplaceMissingValues();
/*   93:     */   protected String m_pickledModel;
/*   94:     */   protected boolean m_dontFetchModelFromPython;
/*   95: 441 */   protected String m_learnerToString = "";
/*   96:     */   protected String m_modelHash;
/*   97:     */   protected boolean[] m_nominalEmptyClassIndexes;
/*   98:     */   protected ZeroR m_zeroR;
/*   99:     */   
/*  100:     */   public String globalInfo()
/*  101:     */   {
/*  102: 463 */     StringBuilder b = new StringBuilder();
/*  103: 464 */     b.append("A wrapper for classifiers implemented in the scikit-learn python library. The following learners are available:\n\n");
/*  104: 466 */     for (Learner l : Learner.values())
/*  105:     */     {
/*  106: 467 */       b.append(l.toString()).append("\n");
/*  107: 468 */       b.append("[");
/*  108: 469 */       if (l.isClassifier()) {
/*  109: 470 */         b.append(" classification ");
/*  110:     */       }
/*  111: 472 */       if (l.isRegressor()) {
/*  112: 473 */         b.append(" regression ");
/*  113:     */       }
/*  114: 475 */       b.append("]").append("\nDefault parameters:\n");
/*  115: 476 */       b.append(l.getDefaultParameters()).append("\n");
/*  116:     */     }
/*  117: 478 */     return b.toString();
/*  118:     */   }
/*  119:     */   
/*  120: 484 */   protected String m_batchPredictSize = "100";
/*  121:     */   protected boolean m_continueOnSysErr;
/*  122:     */   
/*  123:     */   public Capabilities getCapabilities()
/*  124:     */   {
/*  125: 500 */     Capabilities result = super.getCapabilities();
/*  126: 501 */     result.disableAll();
/*  127:     */     
/*  128: 503 */     boolean pythonAvailable = true;
/*  129: 504 */     if (!PythonSession.pythonAvailable()) {
/*  130:     */       try
/*  131:     */       {
/*  132: 507 */         if (!PythonSession.initSession("python", getDebug())) {
/*  133: 508 */           pythonAvailable = false;
/*  134:     */         }
/*  135:     */       }
/*  136:     */       catch (WekaException ex)
/*  137:     */       {
/*  138: 511 */         pythonAvailable = false;
/*  139:     */       }
/*  140:     */     }
/*  141: 515 */     if (pythonAvailable)
/*  142:     */     {
/*  143: 516 */       result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  144: 517 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  145: 518 */       result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  146: 519 */       result.enable(Capabilities.Capability.MISSING_VALUES);
/*  147: 520 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  148: 521 */       if (this.m_learner.isClassifier())
/*  149:     */       {
/*  150: 522 */         result.enable(Capabilities.Capability.BINARY_CLASS);
/*  151: 523 */         result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  152:     */       }
/*  153: 525 */       if (this.m_learner.isRegressor()) {
/*  154: 526 */         result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  155:     */       }
/*  156:     */     }
/*  157: 530 */     return result;
/*  158:     */   }
/*  159:     */   
/*  160:     */   @OptionMetadata(displayName="Use supervised nominal to binary conversion", description="Use supervised nominal to binary conversion of nominal attributes.", commandLineParamName="S", commandLineParamSynopsis="-S", commandLineParamIsFlag=true, displayOrder=3)
/*  161:     */   public boolean getUseSupervisedNominalToBinary()
/*  162:     */   {
/*  163: 545 */     return this.m_useSupervisedNominalToBinary;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public void setUseSupervisedNominalToBinary(boolean useSupervisedNominalToBinary)
/*  167:     */   {
/*  168: 556 */     this.m_useSupervisedNominalToBinary = useSupervisedNominalToBinary;
/*  169:     */   }
/*  170:     */   
/*  171:     */   @OptionMetadata(displayName="Scikit-learn learner", description="Scikit-learn learner to use.\nAvailable learners:\nDecisionTreeClassifier, DecisionTreeRegressor, GaussianNB, MultinomialNB,BernoulliNB, LDA, QDA, LogisticRegression, LogisticRegressionCV,\nLinearRegression, ARDRegression, BayesianRidge, ElasticNet, Lars,\nLarsCV, Lasso, LassoCV, LassoLars, LassoLarsCV, LassoLarsIC, OrthogonalMatchingPursuit,\nOrthogonalMatchingPursuitCV, PassiveAggressiveClassifier, PassiveAggressiveRegressor, Perceptron, RANSACRegressor,\nRidge, RidgeClassifier, RidgeClassifierCV, RidgeCV, SGDClassifier,\nSGDRegressor,TheilSenRegressor, GaussianProcess, KernelRidge, KNeighborsClassifier, \nRadiusNeighborsClassifier, KNeighborsRegressor, RadiusNeighborsRegressor, SVC,\nLinearSVC, NuSVC, SVR, NuSVR, AdaBoostClassifier, AdaBoostRegressor,BaggingClassifier, BaggingRegressor,\nExtraTreeClassifier, ExtraTreeRegressor,GradientBoostingClassifier, GradientBoostingRegressor,\nRandomForestClassifier, RandomForestRegressor.\n(default = DecisionTreeClassifier)", commandLineParamName="learner", commandLineParamSynopsis="-learner <learner name>", displayOrder=1)
/*  172:     */   public SelectedTag getLearner()
/*  173:     */   {
/*  174: 587 */     return new SelectedTag(this.m_learner.ordinal(), TAGS_LEARNER);
/*  175:     */   }
/*  176:     */   
/*  177:     */   public void setLearner(SelectedTag learner)
/*  178:     */   {
/*  179: 596 */     int learnerID = learner.getSelectedTag().getID();
/*  180: 597 */     for (Learner l : Learner.values()) {
/*  181: 598 */       if (l.ordinal() == learnerID)
/*  182:     */       {
/*  183: 599 */         this.m_learner = l;
/*  184: 600 */         break;
/*  185:     */       }
/*  186:     */     }
/*  187:     */   }
/*  188:     */   
/*  189:     */   @OptionMetadata(displayName="Learner parameters", description="learner parameters to use", displayOrder=2, commandLineParamName="parameters", commandLineParamSynopsis="-parameters <comma-separated list of name=value pairs>")
/*  190:     */   public String getLearnerOpts()
/*  191:     */   {
/*  192: 618 */     return this.m_learnerOpts;
/*  193:     */   }
/*  194:     */   
/*  195:     */   public void setLearnerOpts(String opts)
/*  196:     */   {
/*  197: 627 */     this.m_learnerOpts = opts;
/*  198:     */   }
/*  199:     */   
/*  200:     */   public void setBatchSize(String size)
/*  201:     */   {
/*  202: 632 */     this.m_batchPredictSize = size;
/*  203:     */   }
/*  204:     */   
/*  205:     */   @OptionMetadata(displayName="Batch size", description="The preferred number of instances to transfer into python for prediction\n(if operatingin batch prediction mode). More or fewer instances than this will be accepted.", commandLineParamName="batch", commandLineParamSynopsis="-batch <batch size>", displayOrder=4)
/*  206:     */   public String getBatchSize()
/*  207:     */   {
/*  208: 645 */     return this.m_batchPredictSize;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public boolean implementsMoreEfficientBatchPrediction()
/*  212:     */   {
/*  213: 655 */     return true;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public void setContinueOnSysErr(boolean c)
/*  217:     */   {
/*  218: 666 */     this.m_continueOnSysErr = c;
/*  219:     */   }
/*  220:     */   
/*  221:     */   @OptionMetadata(displayName="Try to continue after sys err output from script", description="Try to continue after sys err output from script.\nSome schemes report warnings to the system error stream.", displayOrder=5, commandLineParamName="continue-on-err", commandLineParamSynopsis="-continue-on-err", commandLineParamIsFlag=true)
/*  222:     */   public boolean getContinueOnSysErr()
/*  223:     */   {
/*  224: 685 */     return this.m_continueOnSysErr;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public void setDontFetchModelFromPython(boolean dontFetchModelFromPython)
/*  228:     */   {
/*  229: 698 */     this.m_dontFetchModelFromPython = dontFetchModelFromPython;
/*  230:     */   }
/*  231:     */   
/*  232:     */   @OptionMetadata(displayName="Don't retrieve model from python", description="Don't retrieve the model from python - speeds up cross-validation,\nbut prevents this classifier from being used after deserialization.\nSome models in python (e.g. large random forests) may exceed the maximum size for transfer\n(currently Integer.MAX_VALUE bytes)", displayOrder=6, commandLineParamName="dont-fetch-model", commandLineParamSynopsis="-dont-fetch-model", commandLineParamIsFlag=true)
/*  233:     */   public boolean getDontFetchModelFromPython()
/*  234:     */   {
/*  235: 720 */     return this.m_dontFetchModelFromPython;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public void buildClassifier(Instances data)
/*  239:     */     throws Exception
/*  240:     */   {
/*  241: 731 */     this.m_pickledModel = null;
/*  242: 732 */     getCapabilities().testWithFail(data);
/*  243: 733 */     this.m_zeroR = null;
/*  244: 734 */     if (!PythonSession.pythonAvailable()) {
/*  245: 736 */       if (!PythonSession.initSession("python", getDebug()))
/*  246:     */       {
/*  247: 737 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/*  248: 738 */         throw new Exception("Was unable to start python environment: " + envEvalResults);
/*  249:     */       }
/*  250:     */     }
/*  251: 743 */     if (this.m_modelHash == null) {
/*  252: 744 */       this.m_modelHash = ("" + hashCode());
/*  253:     */     }
/*  254: 747 */     data = new Instances(data);
/*  255: 748 */     data.deleteWithMissingClass();
/*  256: 749 */     if ((data.numInstances() == 0) || (data.numAttributes() == 1))
/*  257:     */     {
/*  258: 750 */       if (data.numInstances() == 0) {
/*  259: 751 */         System.err.println("No instances with non-missing class - using ZeroR model");
/*  260:     */       } else {
/*  261: 754 */         System.err.println("Only the class attribute is present in the data - using ZeroR model");
/*  262:     */       }
/*  263: 757 */       this.m_zeroR = new ZeroR();
/*  264: 758 */       this.m_zeroR.buildClassifier(data);
/*  265: 759 */       return;
/*  266:     */     }
/*  267: 762 */     if (data.classAttribute().isNominal())
/*  268:     */     {
/*  269: 764 */       AttributeStats stats = data.attributeStats(data.classIndex());
/*  270: 765 */       this.m_nominalEmptyClassIndexes = new boolean[data.classAttribute().numValues()];
/*  271: 767 */       for (int i = 0; i < stats.nominalWeights.length; i++) {
/*  272: 768 */         if (stats.nominalWeights[i] == 0.0D) {
/*  273: 769 */           this.m_nominalEmptyClassIndexes[i] = true;
/*  274:     */         }
/*  275:     */       }
/*  276:     */     }
/*  277: 774 */     this.m_replaceMissing.setInputFormat(data);
/*  278: 775 */     data = Filter.useFilter(data, this.m_replaceMissing);
/*  279: 777 */     if (getUseSupervisedNominalToBinary()) {
/*  280: 778 */       this.m_nominalToBinary = new weka.filters.supervised.attribute.NominalToBinary();
/*  281:     */     } else {
/*  282: 781 */       this.m_nominalToBinary = new weka.filters.unsupervised.attribute.NominalToBinary();
/*  283:     */     }
/*  284: 784 */     this.m_nominalToBinary.setInputFormat(data);
/*  285: 785 */     data = Filter.useFilter(data, this.m_nominalToBinary);
/*  286:     */     try
/*  287:     */     {
/*  288: 788 */       PythonSession session = PythonSession.acquireSession(this);
/*  289:     */       
/*  290: 790 */       session.instancesToPythonAsScikitLearn(data, "scikit_classifier_training", getDebug());
/*  291:     */       
/*  292:     */ 
/*  293: 793 */       StringBuilder learnScript = new StringBuilder();
/*  294: 794 */       learnScript.append("from sklearn import *").append("\n").append("import numpy as np").append("\n");
/*  295:     */       
/*  296: 796 */       learnScript.append("weka_scikit_learner" + this.m_modelHash + " = " + this.m_learner.getModule() + "." + this.m_learner.toString() + "(" + (getLearnerOpts().length() > 0 ? getLearnerOpts() : "") + ")").append("\n");
/*  297:     */       
/*  298:     */ 
/*  299:     */ 
/*  300:     */ 
/*  301: 801 */       learnScript.append("weka_scikit_learner" + this.m_modelHash + ".fit(X,np.ravel(Y))").append("\n");
/*  302:     */       
/*  303:     */ 
/*  304: 804 */       List<String> outAndErr = session.executeScript(learnScript.toString(), getDebug());
/*  305: 806 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/*  306: 807 */         if (this.m_continueOnSysErr) {
/*  307: 808 */           System.err.println((String)outAndErr.get(1));
/*  308:     */         } else {
/*  309: 810 */           throw new Exception((String)outAndErr.get(1));
/*  310:     */         }
/*  311:     */       }
/*  312: 814 */       this.m_learnerToString = session.getVariableValueFromPythonAsPlainString("weka_scikit_learner" + this.m_modelHash, getDebug());
/*  313: 818 */       if (!getDontFetchModelFromPython()) {
/*  314: 820 */         this.m_pickledModel = session.getVariableValueFromPythonAsPickledObject("weka_scikit_learner" + this.m_modelHash, getDebug());
/*  315:     */       }
/*  316:     */     }
/*  317:     */     finally
/*  318:     */     {
/*  319: 827 */       PythonSession.releaseSession(this);
/*  320:     */     }
/*  321:     */   }
/*  322:     */   
/*  323:     */   private double[][] batchScoreWithZeroR(Instances insts)
/*  324:     */     throws Exception
/*  325:     */   {
/*  326: 832 */     double[][] result = new double[insts.numInstances()][];
/*  327: 834 */     for (int i = 0; i < insts.numInstances(); i++)
/*  328:     */     {
/*  329: 835 */       Instance current = insts.instance(i);
/*  330: 836 */       result[i] = this.m_zeroR.distributionForInstance(current);
/*  331:     */     }
/*  332: 839 */     return result;
/*  333:     */   }
/*  334:     */   
/*  335:     */   public double[] distributionForInstance(Instance instance)
/*  336:     */     throws Exception
/*  337:     */   {
/*  338: 852 */     Instances temp = new Instances(instance.dataset(), 0);
/*  339: 853 */     temp.add(instance);
/*  340:     */     
/*  341: 855 */     return distributionsForInstances(temp)[0];
/*  342:     */   }
/*  343:     */   
/*  344:     */   public double[][] distributionsForInstances(Instances insts)
/*  345:     */     throws Exception
/*  346:     */   {
/*  347: 868 */     if (this.m_zeroR != null) {
/*  348: 869 */       return batchScoreWithZeroR(insts);
/*  349:     */     }
/*  350: 872 */     if (!PythonSession.pythonAvailable()) {
/*  351: 874 */       if (!PythonSession.initSession("python", getDebug()))
/*  352:     */       {
/*  353: 875 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/*  354: 876 */         throw new Exception("Was unable to start python environment: " + envEvalResults);
/*  355:     */       }
/*  356:     */     }
/*  357: 881 */     insts = Filter.useFilter(insts, this.m_replaceMissing);
/*  358: 882 */     insts = Filter.useFilter(insts, this.m_nominalToBinary);
/*  359: 883 */     Attribute classAtt = insts.classAttribute();
/*  360:     */     
/*  361: 885 */     Remove r = new Remove();
/*  362: 886 */     r.setAttributeIndices("" + (insts.classIndex() + 1));
/*  363: 887 */     r.setInputFormat(insts);
/*  364: 888 */     insts = Filter.useFilter(insts, r);
/*  365: 889 */     insts.setClassIndex(-1);
/*  366:     */     
/*  367: 891 */     double[][] results = (double[][])null;
/*  368:     */     try
/*  369:     */     {
/*  370: 893 */       PythonSession session = PythonSession.acquireSession(this);
/*  371: 894 */       session.instancesToPythonAsScikitLearn(insts, "scikit_classifier_test", getDebug());
/*  372: 895 */       StringBuilder predictScript = new StringBuilder();
/*  373: 898 */       if (!session.checkIfPythonVariableIsSet("weka_scikit_learner" + this.m_modelHash, getDebug()))
/*  374:     */       {
/*  375: 900 */         if ((this.m_pickledModel == null) || (this.m_pickledModel.length() == 0)) {
/*  376: 901 */           throw new Exception("There is no model to transfer into Python!");
/*  377:     */         }
/*  378: 903 */         session.setPythonPickledVariableValue("weka_scikit_learner" + this.m_modelHash, this.m_pickledModel, getDebug());
/*  379:     */       }
/*  380: 907 */       predictScript.append("from sklearn." + this.m_learner.getModule() + " import " + this.m_learner.toString()).append("\n");
/*  381:     */       
/*  382:     */ 
/*  383: 910 */       predictScript.append("preds = weka_scikit_learner" + this.m_modelHash + ".predict" + (this.m_learner.producesProbabilities(this.m_learnerOpts) ? "_proba" : "") + "(X)").append("\npreds = preds.tolist()\n");
/*  384:     */       
/*  385:     */ 
/*  386:     */ 
/*  387: 914 */       List<String> outAndErr = session.executeScript(predictScript.toString(), getDebug());
/*  388: 916 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/*  389: 917 */         if (this.m_continueOnSysErr) {
/*  390: 918 */           System.err.println((String)outAndErr.get(1));
/*  391:     */         } else {
/*  392: 920 */           throw new Exception((String)outAndErr.get(1));
/*  393:     */         }
/*  394:     */       }
/*  395: 924 */       List<Object> preds = (List)session.getVariableValueFromPythonAsJson("preds", getDebug());
/*  396: 927 */       if (preds == null) {
/*  397: 928 */         throw new Exception("Was unable to retrieve predictions from python");
/*  398:     */       }
/*  399: 931 */       if (preds.size() != insts.numInstances()) {
/*  400: 932 */         throw new Exception("Learner did not return as many predictions as there are test instances");
/*  401:     */       }
/*  402: 937 */       results = new double[insts.numInstances()][];
/*  403:     */       int j;
/*  404: 938 */       if ((this.m_learner.producesProbabilities(this.m_learnerOpts)) && (classAtt.isNominal()))
/*  405:     */       {
/*  406: 940 */         j = 0;
/*  407: 941 */         for (Object o : preds)
/*  408:     */         {
/*  409: 942 */           List<Number> dist = (List)o;
/*  410: 943 */           double[] newDist = new double[classAtt.numValues()];
/*  411: 944 */           int k = 0;
/*  412: 945 */           for (int i = 0; i < newDist.length; i++) {
/*  413: 946 */             if (this.m_nominalEmptyClassIndexes[i] == 0) {
/*  414: 949 */               newDist[i] = ((Number)dist.get(k++)).doubleValue();
/*  415:     */             }
/*  416:     */           }
/*  417: 951 */           Utils.normalize(newDist);
/*  418: 952 */           results[(j++)] = newDist;
/*  419:     */         }
/*  420:     */       }
/*  421:     */       else
/*  422:     */       {
/*  423:     */         int j;
/*  424: 955 */         if (classAtt.isNominal())
/*  425:     */         {
/*  426: 956 */           j = 0;
/*  427: 957 */           for (Object o : preds)
/*  428:     */           {
/*  429: 958 */             double[] dist = new double[classAtt.numValues()];
/*  430: 959 */             Number p = null;
/*  431: 960 */             if ((o instanceof List)) {
/*  432: 961 */               p = (Number)((List)o).get(0);
/*  433:     */             } else {
/*  434: 963 */               p = (Number)o;
/*  435:     */             }
/*  436: 965 */             dist[p.intValue()] = 1.0D;
/*  437: 966 */             results[(j++)] = dist;
/*  438:     */           }
/*  439:     */         }
/*  440:     */         else
/*  441:     */         {
/*  442: 969 */           j = 0;
/*  443: 970 */           for (Object o : preds)
/*  444:     */           {
/*  445: 971 */             double[] dist = new double[1];
/*  446: 972 */             Number p = null;
/*  447: 973 */             if ((o instanceof List)) {
/*  448: 974 */               p = (Number)((List)o).get(0);
/*  449:     */             } else {
/*  450: 976 */               p = (Number)o;
/*  451:     */             }
/*  452: 978 */             dist[0] = p.doubleValue();
/*  453: 979 */             results[(j++)] = dist;
/*  454:     */           }
/*  455:     */         }
/*  456:     */       }
/*  457:     */     }
/*  458:     */     finally
/*  459:     */     {
/*  460:     */       int j;
/*  461: 984 */       PythonSession.releaseSession(this);
/*  462:     */     }
/*  463: 987 */     return results;
/*  464:     */   }
/*  465:     */   
/*  466:     */   public String toString()
/*  467:     */   {
/*  468: 996 */     if (this.m_zeroR != null) {
/*  469: 997 */       return this.m_zeroR.toString();
/*  470:     */     }
/*  471: 999 */     if ((this.m_learnerToString == null) || (this.m_learnerToString.length() == 0)) {
/*  472:1000 */       return "SckitLearnClassifier: model not built yet!";
/*  473:     */     }
/*  474:1002 */     return this.m_learnerToString;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public static void main(String[] argv)
/*  478:     */   {
/*  479:1011 */     runClassifier(new ScikitLearnClassifier(), argv);
/*  480:     */   }
/*  481:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.sklearn.ScikitLearnClassifier
 * JD-Core Version:    0.7.0.1
 */