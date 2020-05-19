package lv.kaneps.oitc.math;

public class Sphere
{
	public double x, y, z, r;

	public Sphere(double x, double y, double z, double r)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}

	public Sphere(double x, double y, double z)
	{
		this(x, y, z, 1);
	}

	public Sphere()
	{
		this(0, 0, 0);
	}

	/**
	 * @param x - point's x
	 * @param y - point's y
	 * @param z - point's z
	 * @return squared distance from sphere's center to given point
	 */
	public double getDistanceTo(double x, double y, double z)
	{
		double _x = x - this.x;
		double _y = y - this.y;
		double _z = z - this.z;
		return _x*_x + _y*_y + _z*_z;
	}

	public boolean isPointInside(double x, double y, double z)
	{
		return getDistanceTo(x, y, z) < r * r;
	}
}
