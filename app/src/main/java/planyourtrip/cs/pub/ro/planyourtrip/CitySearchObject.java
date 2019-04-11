package planyourtrip.cs.pub.ro.planyourtrip;

import ir.mirrajabi.searchdialog.core.Searchable;

public class CitySearchObject implements Searchable {

    private String name;
    private int imageUrl;
    private int id;

    public CitySearchObject(String name, int imageUrl, int id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getTitle() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public CitySearchObject setName(String name) {
        this.name = name;
        return this;
    }

    public int getImageUrl() {
        return this.imageUrl;
    }

    public int getId() {
        return this.id;
    }
}
