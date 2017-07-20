package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector2;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** The face that contains this triangle */
  OBJFace face = null;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
	  // TODO#A2: fill in this function.
	  
	  // variables for the values of xd, yd, zd, are g, h, i respectively
	  double g = rayIn.direction.x;
	  double h = rayIn.direction.y;
	  double i = rayIn.direction.z;
	  
	  // calculate det(A)
	  double detA = a*(e*i-f*h) - b*(d*i - f*g) + c*(d*h - e*g);
	  
	  // get the three vectors to access point v0 (a)
	  Vector3 v0 = owner.getMesh().getPosition(face,0);
	 
	  // variables for the values of j, k, l
	  double j = v0.x - rayIn.origin.x;
	  double k = v0.y - rayIn.origin.y;
	  double l = v0.z - rayIn.origin.z;
	  
	  // calculate detTopT, then t
	  double detTopT = f*(a*k - j*b) + e*(j*c - a*l) + d*(b*l - k*c);
	  double t = (-1)*detTopT/detA;

	  if (t < rayIn.start|| t > rayIn.end) {
		  return false;
	  }
	  
	  // calculate detTopGamma, then gamma
	  double detTopGamma = i*(a*k - j*b) + h*(j*c - a*l) + g*(b*l - k*c);
	  double gamma = detTopGamma/detA;
	  if (gamma < 0 || gamma > 1){
		  return false;
	  }
	  
	  // calculate detTopBeta, then beta
	  double detTopBeta = j*(e*i - h*f) + k*(g*f - d*i) + l*(d*h - e*g);
	  double beta = detTopBeta/detA;
	  if (beta < 0 || beta > (1 - gamma)) {
		  return false;
	  }
	  
	  // put locations in
	  Vector3d location = new Vector3d();
	  rayIn.evaluate(location, t);
	  
	  // normals
	  if (face.hasNormals()) {
		  Vector3 normal0 = owner.getMesh().getNormal(face, 0);
		  Vector3 normal1 = owner.getMesh().getNormal(face, 1);
		  Vector3 normal2 = owner.getMesh().getNormal(face, 2);
		  Vector3d newNormal = new Vector3d();
		  newNormal.addMultiple((1 - beta - gamma), normal0);
		  newNormal.addMultiple(beta, normal1);
		  newNormal.addMultiple(gamma, normal2);
		  outRecord.normal.set(newNormal);
	  } else {
		  outRecord.normal.set(norm);
	  }
	  
	  // texture
	  if (face.hasUVs()) {
		  Vector2 texture0 = owner.getMesh().getUV(face, 0);
		  Vector2 texture1 = owner.getMesh().getUV(face, 1);
		  Vector2 texture2 = owner.getMesh().getUV(face, 2);
		  Vector2d newTexture = new Vector2d();
		  newTexture.addMultiple((1 - beta - gamma), texture0);
		  newTexture.addMultiple(beta, texture1);
		  newTexture.addMultiple(gamma, texture2);
		  outRecord.texCoords.set(newTexture);
	  }
	  
	  outRecord.normal.normalize();
	  outRecord.location.set(location);
	  outRecord.t = t;
	  outRecord.surface = this;
	  return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}