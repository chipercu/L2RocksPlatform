package com.fuzzy.subsystem.gameserver.geodata;

import com.fuzzy.subsystem.gameserver.model.L2Territory;

public class GeoBlock implements GeoCollision {

    private L2Territory shape;

    @Override
    public L2Territory getGeoPos() {
        return shape;
    }

    @Override
    public void setGeoPos(L2Territory value) {
        shape = value;
    }

    private byte[][] _geoAround;

    @Override
    public byte[][] getGeoAround() {
        return _geoAround;
    }

    @Override
    public void setGeoAround(byte[][] geo) {
        _geoAround = geo;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }
}