import kotlin.math.sqrt

data class Vector3(val x: Double, val y: Double, val z: Double) {

    val length = sqrt(x * x + y * y + z * z)

    operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    operator fun times(scalar: Double) = Vector3(x * scalar, y * scalar, z * scalar)

    operator fun div(scalar: Double) = Vector3(x / scalar, y / scalar, z / scalar)

    companion object {
        val UP = Vector3(0.0, 1.0, 0.0)

        fun dot(lhs: Vector3, rhs: Vector3) = lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z

        fun normalize(vec: Vector3) = vec / vec.length

        fun cross(lhs: Vector3, rhs: Vector3): Vector3 {
            val x = lhs.y * rhs.z - lhs.z * rhs.y
            val y = lhs.z * rhs.x - lhs.x * rhs.z
            val z = lhs.x * rhs.y - lhs.y * rhs.x
            return Vector3(x, y, z)
        }
    }
}
