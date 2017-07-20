package ray1.shader;

import ray1.IntersectionRecord;
import ray1.Ray;
import ray1.Scene;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector3d;
import egl.math.Vector2;
/**
 * A Lambertian material scatters light equally in all directions. BRDF value is
 * a constant
 *
 * @author ags, zz
 */
public class Lambertian extends Shader {

	/** The color of the surface. */
	protected final Colorf diffuseColor = new Colorf(Color.White);
	public void setDiffuseColor(Colorf inDiffuseColor) { diffuseColor.set(inDiffuseColor); }
	public Colorf getDiffuseColor() {return new Colorf(diffuseColor);}

	public Lambertian() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "lambertian: " + diffuseColor;
	}

	/**
	 * Evaluate the intensity for a given intersection using the Lambert shading model.
	 * 
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record) {
		// TODO#A2: Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the Lambert shading model. Add this value
		//    to the output.
		outIntensity.setZero();
		for(ray1.Light l: scene.getLights()){
			if(!isShadowed(scene, l, record, ray)){
				Vector3d w_i = new Vector3d(0.0,0.0,0.0);
				w_i = record.location.clone().sub(l.position).negate().normalize();
				float r2 = (float) record.location.clone().sub(l.position).lenSq();
				Vector3d n = new Vector3d(record.normal.clone().normalize());
				
				//position of intersection
				//(k_L/r^2)k_d*max(n*w_i,0)
				//k_L = intensity of light source = colorOut
				//  r = distance from light source = w_i.len()
				//k_d = diffuseColor
				//  n = normal of intersection point = record.normal
				//w_i = direction of light =1 (HOW TO FIND THIS)
				Colorf colorOut = new Colorf(l.intensity.r(),l.intensity.g(),l.intensity.b());
				Colorf k_d = new Colorf();
				if (texture == null) {
					k_d.set(diffuseColor);
				} else {
					k_d.set(texture.getTexColor(new Vector2(record.texCoords)));
				}
				
				colorOut.div(r2)
					.mul(k_d)
					.mul((float) Math.max(n.clone().dot(w_i), 0));
				outIntensity.x += (float)colorOut.x;
				outIntensity.y += (float)colorOut.y;
				outIntensity.z += (float)colorOut.z;
			}
		}
	}
}
