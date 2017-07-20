package ray1.shader;

import ray1.shader.Texture;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;

/**
 * A Texture class that repeats the texture image as necessary for UV-coordinates
 * outside the [0.0, 1.0] range.
 * 
 * @author eschweic zz335
 *
 */
public class RepeatTexture extends Texture {

	public Colorf getTexColor(Vector2 texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colorf();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) If these coordinates are outside the image boundaries, modify them to read from
		//    the correct pixel on the image to give a repeating-tile effect.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colorf, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.
		
		int u = image.getWidth(); 
		int v = image.getHeight();
		
		int inX = (int)(texCoord.x*u + 0.5f);
		int inY = (int)(texCoord.y*v + 0.5f);
		
		inX = Math.floorMod(inX, u-1);
		inY = Math.floorMod(inY, v-1);
		
		/*
		if(inX >= u){inX -= u;}
		else if(inX < 0){inX += u;}

		if(inY >= v){inY -= v;}
		else if(inY < 0){inY += v;}
		
		if(inX < 0){inX *= -1;}
		
		if(inY < 0){inY *= -1;}
		*/
		//accounts of change in Y
		inY = v -1 - inY;

		return new Colorf(Color.fromIntRGB(image.getRGB(inX, inY)));
	}

}
