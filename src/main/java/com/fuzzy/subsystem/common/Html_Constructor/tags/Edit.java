package com.fuzzy.subsystem.common.Html_Constructor.tags;

import com.fuzzy.subsystem.common.Html_Constructor.parameters.EditType;

/**
 * Created by a.kiperku
 * Date: 15.08.2023
 */

public class Edit implements Build {

    private final String var;
    private final String type;
    private final int width;
    private final int height;
    private final int length;

    public Edit(String var, int width, int height, EditType type, int length){
        this.var = var;
        this.width = width;
        this.height = height;
        this.length = length;
        if (type == EditType.num){
            this.type = "number";
        }else {
            this.type = "text";
        }
    }

    public String build(){
        return "<edit var=\"" + var + "\" type=\"" + type + "\"" + " width=" + width + " height=" + height + " length=" + length + ">";
    }

}
