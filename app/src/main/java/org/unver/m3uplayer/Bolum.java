package org.unver.m3uplayer;

import java.util.ArrayList;

public class Bolum {
    public ArrayList<String> idler = new ArrayList<>();
    public String bolum;
    public int seciliIdIndex = 0;

    public Bolum(String id, String bolum) {
        this.idler.add(id);
        this.bolum = bolum;
    }

    public void AddId(String id) {
        if (!idler.contains(id))
            this.idler.add(id);
    }

    public String IDBul() {
        return this.idler.get(seciliIdIndex);
    }
}
