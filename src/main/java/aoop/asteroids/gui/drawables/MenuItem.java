package aoop.asteroids.gui.drawables;

public abstract class MenuItem implements IDrawable {

    private String name;
    private Menu.MenuEventCallback menuEventCallback;
    private boolean hasFocus;
    private boolean isSelected;

    public MenuItem(String name, Menu.MenuEventCallback menuEventCallback) {
        this.name = name;
        this.menuEventCallback = menuEventCallback;
        hasFocus = false;
        isSelected = false;
    }


    public String getName() {
        return name;
    }

    public void setFocus(boolean focus) {hasFocus = focus;}

    public void select() {
        isSelected = true;
        if (menuEventCallback != null) menuEventCallback.OnSelectChange(this);
    }

    public void unSelect() {
        isSelected = false;
        if (menuEventCallback != null) menuEventCallback.OnSelectChange(this);
    }

    public boolean isSelected() {return isSelected; }

    public boolean hasFocus() { return hasFocus; }


}
