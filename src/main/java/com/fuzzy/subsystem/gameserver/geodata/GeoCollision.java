package com.fuzzy.subsystem.gameserver.geodata;

import com.fuzzy.subsystem.gameserver.model.L2Territory;

public abstract interface GeoCollision {

    L2Territory getGeoPos();

    void setGeoPos(L2Territory value);

    byte[][] getGeoAround();

    void setGeoAround(byte[][] geo);

    // создаем или убираем преграду в геодате...
    boolean isConcrete();
}
