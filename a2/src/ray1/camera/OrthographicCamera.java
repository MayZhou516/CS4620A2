package ray1.camera;

import ray1.Ray;
import egl.math.Vector3d;

public class OrthographicCamera extends Camera {

    //TODO#A2: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.
	public Vector3d u = new Vector3d();
	public Vector3d v = new Vector3d();
	public Vector3d w = new Vector3d();
    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
	public void init() {
		// TODO#A2: Fill in this function.
		// 1) Set the 3 basis vectors in the orthonormal basis, 
		//    based on viewDir and viewUp
		// 2) Set up the helper variables if needed
		w.addMultiple(-1.0, this.getViewDir());
		w.normalize();
		Vector3d viewUp = new Vector3d(this.getViewUp());
		this.u = viewUp.cross(w);
		u.normalize();
		this.v = w.clone().cross(u);
		v.normalize();
	}

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
	public void getRay(Ray outRay, float inU, float inV) {
		// TODO#A2: Fill in this function.
		// 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
		//    instead of [0, 1]. Similarly, transform inV so that its range is
		//    [-vieHeight / 2, +viewHeight / 2]
		// 2) Set the origin field of outRay for an orthographic camera. 
		//    In an orthographic camera, the origin should depend on your transformed
		//    inU and inV and your basis vectors u and v.
		// 3) Set the direction field of outRay for an orthographic camera.

		float vw = this.getViewWidth();
		float vh = this.getViewHeight();
		float U = (inU*vw)-(vw/2);
		float V = (inV*vh)-(vh/2);
		//part 2
		//origin = viewPoint + u.setMultiple(inU, u) + v.se
		Vector3d s = new Vector3d();
		Vector3d e = new Vector3d();
		e.set(this.getViewPoint());
		s = e.clone();
		s.add(u.clone().setMultiple(U, u));
		s.add(v.clone().setMultiple(V, v));
		//part 3
		outRay.set(s,w.clone().setMultiple(-1.0,w));
		
		outRay.makeOffsetRay();
	}
}
