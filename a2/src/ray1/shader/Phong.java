package ray1.shader;

import ray1.IntersectionRecord;
import ray1.Ray;
import ray1.Scene;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3d;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Phong extends Shader {

	/** The color of the diffuse reflection. */
	protected final Colorf diffuseColor = new Colorf(Color.White);
	public void setDiffuseColor(Colorf diffuseColor) { this.diffuseColor.set(diffuseColor); }
	public Colorf getDiffuseColor() {return new Colorf(diffuseColor);}

	/** The color of the specular reflection. */
	protected final Colorf specularColor = new Colorf(Color.White);
	public void setSpecularColor(Colorf specularColor) { this.specularColor.set(specularColor); }
	public Colorf getSpecularColor() {return new Colorf(specularColor);}

	/** The exponent controlling the sharpness of the specular reflection. */
	protected float exponent = 1.0f;
	public void setExponent(float exponent) { this.exponent = exponent; }
	public float getExponent() {return exponent;}

	public Phong() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Phong shading model.
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
		// 4) Compute the color of the point using the Phong shading model. Add this value
		//    to the output.
		outIntensity.setZero();
		for(ray1.Light l: scene.getLights()){
			if(!isShadowed(scene, l, record, ray)){
				//direction of light, position of intersection & distance to light source
				//(k_L/r^2)(k_d*max(n*w_i,0)+k_s*max(n.dot(h),0)^p)
				//k_L = intensity of light source = colorOut
				//  r = distance from light source = w_i.len()
				//k_d = diffuseColor
				//  n = normal of intersection point = record.normal
				//k_s = specularColor
				//w_i = direction of light = (HOW TO FIND THIS)
				//w_o = direction of reflection = w_i - 2(w_i dot n)/n
				//  h = (w_i dot w_o)/(length of w_i+w_o)
				
				Vector3d w_i = new Vector3d(0.0,0.0,0.0);
				w_i = record.location.clone().sub(l.position).negate();
				w_i.normalize();
				
				float r2 = (float) record.location.clone().sub(l.position).lenSq();
				
				Vector3d n = new Vector3d(record.normal.clone().normalize());
				
				Colorf colorOut = new Colorf(l.intensity.r(),l.intensity.g(),l.intensity.b());
				Colorf k_d = new Colorf();
				if (texture == null) {
					k_d.set(diffuseColor);
				} else {
					k_d.set(texture.getTexColor(new Vector2(record.texCoords)));
				}
				
				Vector3d w_o = new Vector3d(l.position);
				w_o.sub(record.location).normalize();
				Vector3d h = new Vector3d();
				h = w_i.clone().add(w_o).normalize();
				
				//original diffuse color
				Vector3d diffColor = new Vector3d(diffuseColor.r(),diffuseColor.g(),diffuseColor.b());
				diffColor.mul(Math.max(record.normal.clone().dot(w_i), 0));
				
				//original specular color
				Colorf specColor = getSpecularColor();//new Colorf(specularColor.r(), specularColor.g(), specularColor.b());
				// specColor.mul((float)Math.pow(Math.max(record.normal.clone().dot(h), 0), this.getExponent()));
				Colorf firstHalf = new Colorf();
				firstHalf.add(k_d.mul((float)Math.max(n.clone().dot(w_i), 0)));
				Colorf secondHalf = new Colorf();
				secondHalf.add(specColor.mul((float)Math.pow(Math.max(n.clone().dot(h), 0), getExponent())));
				colorOut.div(r2).mul(firstHalf.add(secondHalf));
				outIntensity.x += (float)colorOut.x;
				outIntensity.y += (float)colorOut.y;
				outIntensity.z += (float)colorOut.z;
			}
		}
	}

}
