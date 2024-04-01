package com.fuzzy.subsystem.extensions.listeners;

public interface MethodCollection {
    String ReduceCurrentHp = "L2Character.ReduceCurrentHp";
    String L2ZoneObjectEnter = "L2Zone.onZoneEnter";
    String L2ZoneObjectLeave = "L2Zone.onZoneLeave";
    String AbstractAInotifyEvent = "AbstractAI.notifyEvent";
    String AbstractAIsetIntention = "AbstractAI.setIntention";
    String onStartAttack = "L2Character.doAttack";
    String onStartCast = "L2Character.doCast";
    String onStartAltCast = "L2Character.altUseSkill";
    String OnAttacked = "L2Character.onHitTimer";
    String onDecay = "L2Character.onDecay";
    String doDie = "L2Character.doDie";
    String onKill = "L2Character.doDie.KillerNotifier";
}
