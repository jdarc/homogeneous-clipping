import java.lang.Math.fma

data class Vector4(val x: Double, val y: Double, val z: Double, val w: Double) {

    constructor(v: Vector3) : this(v.x, v.y, v.z, 1.0)

    operator fun div(s: Double) = Vector4(x / s, y / s, z / s, w / s)

    companion object {

        fun lerp(lhs: Vector4, rhs: Vector4, t: Double) = if (t.isInfinite()) lhs else {
            val x = fma(1.0 - t, lhs.x, t * rhs.x)
            val y = fma(1.0 - t, lhs.y, t * rhs.y)
            val z = fma(1.0 - t, lhs.z, t * rhs.z)
            val w = fma(1.0 - t, lhs.w, t * rhs.w)
            Vector4(x, y, z, w)
        }
    }
}
