package com.fuzzy.subsystems.list;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public class RemoteListItem<T extends RemoteObject> {

    private T element = null;
    private boolean selected = false;
    private boolean hidden = false;

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}