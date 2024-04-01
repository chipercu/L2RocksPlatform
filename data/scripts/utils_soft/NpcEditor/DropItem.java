package utils_soft.NpcEditor;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class DropItem {

    private int id;
    private int min;
    private int max;
    private int group;
    private int chance;
    private boolean isSpoil;

    public DropItem(int id, int min, int max, int group, int chance, boolean isSpoil) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.group = group;
        this.chance = chance;
        this.isSpoil = isSpoil;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public boolean getIsSpoil() {
        return isSpoil;
    }

    public void setIsSpoil(boolean isSpoil) {
        this.isSpoil = isSpoil;
    }
}
