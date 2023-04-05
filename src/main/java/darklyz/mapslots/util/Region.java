package darklyz.mapslots.util;

public interface Region {
    Integer getMapId();
    int getInX();
    int getInY();
    int getInSide();
    int getOutX();
    int getOutY();
    int getOutSide();
    int getCenterX();
    int getCenterY();
}
