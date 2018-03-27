package com.alliejc.wcstinder.util;

import com.alliejc.wcstinder.trackmyswing.Dancer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acaldwell on 3/27/18.
 */

public class DancerCache {
    private static DancerCache mInstance = null;
    private List<Dancer> allDancersList = new ArrayList<>();
    private Boolean isDancerReadFromResourceFile;

    private DancerCache(){}

    public static DancerCache getInstance(){
        if(mInstance == null)
        {
            mInstance = new DancerCache();
        }
        return mInstance;
    }

    public List<Dancer> getAllDancers() {
        return allDancersList;
    }

    public void setAllDrugs(List<Dancer> drugs) {
        this.allDancersList = drugs;
    }

    public Boolean getIsDancerReadFromResourceFile() {
        return isDancerReadFromResourceFile;
    }

    public void setIsDancerReadFromResourceFile(Boolean dancerReadFromResourceFile) {
        isDancerReadFromResourceFile = dancerReadFromResourceFile;
    }
}
