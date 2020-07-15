package cn.nihility.boot.selector;

/**
 * @author dandelion
 * @date 2020:06:27 18:04
 */
public class Zoom {

    private Cat cat1;
    private Cat cat2;

    public Zoom() {
    }

    public Zoom(Cat cat1, Cat cat2) {
        this.cat1 = cat1;
        this.cat2 = cat2;
    }

    public Cat getCat1() {
        return cat1;
    }

    public void setCat1(Cat cat1) {
        this.cat1 = cat1;
    }

    public Cat getCat2() {
        return cat2;
    }

    public void setCat2(Cat cat2) {
        this.cat2 = cat2;
    }
}
