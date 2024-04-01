package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.gameserver.skills.Calculator;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;

/**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public abstract class StatsChangeListener {

    public final Stats _stat;
    protected Calculator _calculator;

    public StatsChangeListener(Stats stat) {
        _stat = stat;
    }

    public void setCalculator(Calculator calculator) {
        _calculator = calculator;
    }

    public abstract void statChanged(Double oldValue, double newValue, double baseValue, Env env);
}