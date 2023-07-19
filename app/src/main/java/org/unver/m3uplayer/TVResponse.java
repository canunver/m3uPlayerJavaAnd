package org.unver.m3uplayer;

public class TVResponse {
    public int resultsSay() {
        return results.length;
    }

    public TVInfo InfoAl(int i) {
        return results[i];
    }

    public TVInfo[] results;

    @SuppressWarnings("all")
    public String ToListStr() {
        if (results == null)
            return "Not Found";
        String donusDeger = "";
        for (TVInfo tv : results
        ) {
            donusDeger += tv.ToListStr();
        }
        return donusDeger;
    }


}
