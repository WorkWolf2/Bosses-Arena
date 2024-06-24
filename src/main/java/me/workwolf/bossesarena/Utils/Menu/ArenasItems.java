package me.workwolf.bossesarena.Utils.Menu;

public class ArenasItems {
    private String name;

    public ArenasItems(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MenuItem{" + "name='" + name + "'\'" + "'}'";
    }
}
