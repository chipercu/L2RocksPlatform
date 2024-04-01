package com.fuzzy.subsystem.common.Html_Constructor.parameters;

public interface ButtonAction {

    default String simpleAction(String action){
        return " action=\"bypass" + action + "\"";
    }

}
