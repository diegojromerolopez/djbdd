/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asserter.examples.diabetest1assistant;

/**
 *
 * @author diegoj
 */
public class Human {
    
    /**************************************/
    /* GLUCOSE LEVELS */
    /** Low glucose (hypoglicaemia) */
    public static final int GL = 21;
    
    /** Normal blood glucose level */
    public static final int GN = 22;
    
    /** High glucose level exercise */
    public static final int GH1 = 23;
    
    /** Higher glucose level */
    public static final int GH2 = 24;
    
    /** Very high glucose level */
    public static final int GVH = 25;
    
    /**
     * Too high glucose level, if there are no correction insulin, pacient can
     * suffer from diabetic coma.
     */
    public final int GTH = 26;
    
    /** Glucose level in mg/dc */
    private int bloodGlucoseLevel;

    /**************************************/
    /* EXERCISE LEVELS */
    /** No exercise */
    public static final int EN = 31;
    
    /** Light exercise */
    public static final int EL = 32;
    
    /** Medium exercise */
    public static final int EM = 33;
    
    /** Hard exercise */
    public static final int EH = 33;
    
    /** Meal */
    private int exercise;
    
    /**************************************/
    /* MEAL TYPES */
    /** Primary meal (breakfast, lunch and dinner) */
    public static final int M = 41;
    
    /** Supplementary meal  */
    public static final int MS = 42;
    
    /** No meal  */
    public static final int MN = 43;
    
    /** Meal */
    private int meal;

    /**************************************/
    /* INSULIN CORRECTION LEVELS */
    /** InsulinNoCorrection */
    public static final int INC = 51;
    
    /** InsulinLightCorrection */
    public static final int ILC = 52;
    
    /** InsulinMediumCorrection */
    public static final int IMC = 53;
    
    /** InsulinHighCorrection */
    public static final int IHC = 53;
    
    /** Insulin */
    private int insulin;
    
    /**************************************************************************/
    /* METHODS */
    
    public void takePrimaryMeal(int currentBloodGlucose){
        this.bloodGlucoseLevel = currentBloodGlucose;
        this.exercise = Human.EN;
        this.meal = Human.M;
        this.insulin = Human.ILC;
    }
    
    public void eatTooMuch(){
        this.exercise = Human.EN;
        this.meal = Human.MS;
        this.insulin = Human.INC;
    }
    
    public void tooMuchInsulin(){
        this.bloodGlucoseLevel = GL;
        this.exercise = Human.EN;
        this.meal = Human.MN;
        this.insulin = Human.ILC;
    }
    
    /**************************************************************************/
    /* Implementation of the variables as consultor methods */    

    public boolean EH(){ return this.exercise == EH;  }
    public boolean EL(){ return this.exercise == EL;  }
    public boolean EM(){ return this.exercise == EM;  }
    
    public boolean GL(){ return this.bloodGlucoseLevel == GL;  }
    public boolean GN(){ return this.bloodGlucoseLevel == GN;  }
    public boolean GH1(){ return this.bloodGlucoseLevel == GH1;  }
    public boolean GH2(){ return this.bloodGlucoseLevel == GH2;  }
    public boolean GVH(){ return this.bloodGlucoseLevel == GVH;  }
    public boolean GTH(){ return this.bloodGlucoseLevel == GTH;  }
    
    public boolean INC(){ return this.insulin == INC;  }
    public boolean ILC(){ return this.insulin == ILC;  }
    public boolean IHC(){ return this.insulin == IHC;  }

    public boolean M(){ return this.meal == M;  }
    public boolean MN(){ return this.meal == MN;  }
    public boolean MS(){ return this.meal == MS;  }
    
}
