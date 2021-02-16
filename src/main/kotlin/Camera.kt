class Camera(private val fov: Double, private val aspect: Double, private val near: Double, private val far: Double) {
    var position = Vector3(0.0, 50.0, 150.0)
    var target = Vector3(0.0, 0.0, 0.0)

    fun computeTransform() = Matrix4.createLookAt(position, target, Vector3.UP) * Matrix4.createPerspective(fov, aspect, near, far)
}
