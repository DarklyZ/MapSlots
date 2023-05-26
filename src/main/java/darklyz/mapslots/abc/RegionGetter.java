package darklyz.mapslots.abc;

public interface RegionGetter {
	boolean isOpen();
	Integer mapId();
	int inX();
	int inY();
	int inSide();
	int outX();
	int outY();
	int side();
	int centerX();
	int centerY();
	int chunkSide();
}
