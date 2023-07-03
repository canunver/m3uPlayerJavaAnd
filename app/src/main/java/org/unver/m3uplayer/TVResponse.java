package org.unver.m3uplayer;

public class TVResponse {
    public int resultsSay() {
        return results.length;
    }

    public TVInfo InfoAl(int i) {
        return results[i];
    }

    public int page;
    public int total_results;
    public int total_pages;
    public TVInfo[] results;

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
