package com.dhanantry.scapeandrunparasites.entity.ai.misc;


public interface EntityCanSummon {
    public void addID(int var1, int var2);

    public int IDable();

    public void checkID();

    public int[] getIDList();

    public int[] getPointList();

    public int getTotalParasites();

    public int getActualParasites();

    public void setActualParasites(int var1);
}

