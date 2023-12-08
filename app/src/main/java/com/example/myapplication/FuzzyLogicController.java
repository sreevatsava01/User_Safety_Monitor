package com.example.myapplication;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.norm.t.Minimum;

public class FuzzyLogicController {
    private Engine engine;
    private InputVariable heartRate;
    private InputVariable respiratoryRate;
    private InputVariable stepCount;
    private OutputVariable dangerLevel;

    public FuzzyLogicController() {
        // Initialize engine, input variables, output variables, and rule block
        engine = new Engine();
        engine.setName("HealthMonitor");
        engine.setDescription("");

        // Input Variables
        heartRate = new InputVariable();
        heartRate.setName("heartRate");
        heartRate.setDescription("");
        heartRate.setEnabled(true);
        heartRate.setRange(50.000, 200.000);
        heartRate.addTerm(new Ramp("high", 100.000, 200.000));
        heartRate.addTerm(new Ramp("low", 200.000, 100.000));
        engine.addInputVariable(heartRate);

        respiratoryRate = new InputVariable();
        respiratoryRate.setName("respiratoryRate");
        respiratoryRate.setDescription("");
        respiratoryRate.setEnabled(true);
        respiratoryRate.setRange(10.000, 40.000);
        respiratoryRate.addTerm(new Ramp("high", 20.000, 40.000));
        respiratoryRate.addTerm(new Ramp("low", 40.000, 20.000));
        engine.addInputVariable(respiratoryRate);

        stepCount = new InputVariable();
        stepCount.setName("stepCount");
        stepCount.setDescription("");
        stepCount.setEnabled(true);
        stepCount.setRange(0.000, 200.000);
        stepCount.addTerm(new Ramp("low", 100.000, 0.000));
        stepCount.addTerm(new Ramp("high", 0.000, 100.000));
        engine.addInputVariable(stepCount);

        // Output Variable
        dangerLevel = new OutputVariable();
        dangerLevel.setName("dangerLevel");
        dangerLevel.setDescription("");
        dangerLevel.setEnabled(true);
        dangerLevel.setRange(0.000, 100.000);
        dangerLevel.setAggregation(new Maximum());
        dangerLevel.setDefuzzifier(new Centroid(100));
        dangerLevel.setDefaultValue(Double.NaN);
        dangerLevel.addTerm(new Ramp("low", 50.000, 0.000));
        dangerLevel.addTerm(new Ramp("high", 0.000, 50.000));
        engine.addOutputVariable(dangerLevel);

        // RuleBlock
        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setName("ruleBlock");
        ruleBlock.setDescription("");
        ruleBlock.setEnabled(true);
        ruleBlock.setConjunction(new Minimum()); // Conjunction method, e.g., Minimum
        ruleBlock.setDisjunction(new Maximum()); // Disjunction method, e.g., Maximum
        ruleBlock.setImplication(new AlgebraicProduct());
        ruleBlock.addRule(Rule.parse("if heartRate is high or respiratoryRate is high or stepCount is low then dangerLevel is high", engine));
        ruleBlock.addRule(Rule.parse("if heartRate is low and respiratoryRate is low and stepCount is high then dangerLevel is low", engine));
        engine.addRuleBlock(ruleBlock);
    }

    public double evaluateDangerLevel(double hr, double rr, double steps) {
        heartRate.setValue(hr);
        respiratoryRate.setValue(rr);
        stepCount.setValue(steps);
        engine.process();
        return dangerLevel.getValue();
    }
}
