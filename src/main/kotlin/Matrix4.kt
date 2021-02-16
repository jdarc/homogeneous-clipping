import Vector3.Companion.cross
import java.lang.Math.fma
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class Matrix4(
    private val m00: Double, private val m01: Double, private val m02: Double, private val m03: Double,
    private val m10: Double, private val m11: Double, private val m12: Double, private val m13: Double,
    private val m20: Double, private val m21: Double, private val m22: Double, private val m23: Double,
    private val m30: Double, private val m31: Double, private val m32: Double, private val m33: Double
) {

    operator fun times(rhs: Matrix4) = Matrix4(
        fma(m00, rhs.m00, fma(m01, rhs.m10, fma(m02, rhs.m20, m03 * rhs.m30))),
        fma(m00, rhs.m01, fma(m01, rhs.m11, fma(m02, rhs.m21, m03 * rhs.m31))),
        fma(m00, rhs.m02, fma(m01, rhs.m12, fma(m02, rhs.m22, m03 * rhs.m32))),
        fma(m00, rhs.m03, fma(m01, rhs.m13, fma(m02, rhs.m23, m03 * rhs.m33))),
        fma(m10, rhs.m00, fma(m11, rhs.m10, fma(m12, rhs.m20, m13 * rhs.m30))),
        fma(m10, rhs.m01, fma(m11, rhs.m11, fma(m12, rhs.m21, m13 * rhs.m31))),
        fma(m10, rhs.m02, fma(m11, rhs.m12, fma(m12, rhs.m22, m13 * rhs.m32))),
        fma(m10, rhs.m03, fma(m11, rhs.m13, fma(m12, rhs.m23, m13 * rhs.m33))),
        fma(m20, rhs.m00, fma(m21, rhs.m10, fma(m22, rhs.m20, m23 * rhs.m30))),
        fma(m20, rhs.m01, fma(m21, rhs.m11, fma(m22, rhs.m21, m23 * rhs.m31))),
        fma(m20, rhs.m02, fma(m21, rhs.m12, fma(m22, rhs.m22, m23 * rhs.m32))),
        fma(m20, rhs.m03, fma(m21, rhs.m13, fma(m22, rhs.m23, m23 * rhs.m33))),
        fma(m30, rhs.m00, fma(m31, rhs.m10, fma(m32, rhs.m20, m33 * rhs.m30))),
        fma(m30, rhs.m01, fma(m31, rhs.m11, fma(m32, rhs.m21, m33 * rhs.m31))),
        fma(m30, rhs.m02, fma(m31, rhs.m12, fma(m32, rhs.m22, m33 * rhs.m32))),
        fma(m30, rhs.m03, fma(m31, rhs.m13, fma(m32, rhs.m23, m33 * rhs.m33)))
    )

    operator fun times(rhs: Vector3): Vector3 {
        val w = fma(m03, rhs.x, fma(m13, rhs.y, fma(m23, rhs.z, m33)))
        val x = fma(m00, rhs.x, fma(m10, rhs.y, fma(m20, rhs.z, m30))) / w
        val y = fma(m01, rhs.x, fma(m11, rhs.y, fma(m21, rhs.z, m31))) / w
        val z = fma(m02, rhs.x, fma(m12, rhs.y, fma(m22, rhs.z, m32))) / w
        return Vector3(x, y, z)
    }

    operator fun times(rhs: Vector4) = Vector4(
        fma(m00, rhs.x, fma(m10, rhs.y, fma(m20, rhs.z, m30 * rhs.w))),
        fma(m01, rhs.x, fma(m11, rhs.y, fma(m21, rhs.z, m31 * rhs.w))),
        fma(m02, rhs.x, fma(m12, rhs.y, fma(m22, rhs.z, m32 * rhs.w))),
        fma(m03, rhs.x, fma(m13, rhs.y, fma(m23, rhs.z, m33 * rhs.w)))
    )

    companion object {

        fun createRotationX(radians: Double): Matrix4 {
            val cos = cos(radians)
            val sin = sin(radians)
            return Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, cos, sin, 0.0, 0.0, -sin, cos, 0.0, 0.0, 0.0, 0.0, 1.0)
        }

        fun createRotationY(radians: Double): Matrix4 {
            val cos = cos(radians)
            val sin = sin(radians)
            return Matrix4(cos, 0.0, -sin, 0.0, 0.0, 1.0, 0.0, 0.0, sin, 0.0, cos, 0.0, 0.0, 0.0, 0.0, 1.0)
        }

        fun createRotationZ(radians: Double): Matrix4 {
            val cos = cos(radians)
            val sin = sin(radians)
            return Matrix4(cos, sin, 0.0, 0.0, -sin, cos, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        }

        fun createLookAt(eye: Vector3, at: Vector3, up: Vector3): Matrix4 {
            val az = Vector3.normalize(at - eye)
            val ax = Vector3.normalize(cross(up, az))
            val ay = Vector3.normalize(cross(az, ax))
            val tx = -Vector3.dot(ax, eye)
            val ty = -Vector3.dot(ay, eye)
            val tz = -Vector3.dot(az, eye)
            return Matrix4(ax.x, ay.x, az.x, 0.0, ax.y, ay.y, az.y, 0.0, ax.z, ay.z, az.z, 0.0, tx, ty, tz, 1.0)
        }

        fun createPerspective(fov: Double, aspectRatio: Double, near: Double, far: Double): Matrix4 {
            val b = 1.0 / tan(fov * 0.5)
            val a = b / aspectRatio
            val c = far / (far - near)
            val e = -near * c
            return Matrix4(a, 0.0, 0.0, 0.0, 0.0, b, 0.0, 0.0, 0.0, 0.0, c, 1.0, 0.0, 0.0, e, 0.0)
        }
    }
}
