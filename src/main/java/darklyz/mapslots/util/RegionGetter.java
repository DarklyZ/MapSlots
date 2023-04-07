package darklyz.mapslots.util;

public interface RegionGetter {
    Integer mapId();
    int inX();
    int inY();
    int inSide();
    int outX();
    int outY();
    int outSide();
    int centerX();
    int centerY();
    int chunkSide();
}
