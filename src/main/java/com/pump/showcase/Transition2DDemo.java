/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase;

import java.net.URL;

import com.pump.image.transition.BarsTransition2D;
import com.pump.image.transition.BlendTransition2D;
import com.pump.image.transition.BlindsTransition2D;
import com.pump.image.transition.BoxTransition2D;
import com.pump.image.transition.CheckerboardTransition2D;
import com.pump.image.transition.CircleTransition2D;
import com.pump.image.transition.CollapseTransition2D;
import com.pump.image.transition.CurtainTransition2D;
import com.pump.image.transition.DiamondsTransition2D;
import com.pump.image.transition.DocumentaryTransition2D;
import com.pump.image.transition.DotsTransition2D;
import com.pump.image.transition.DropTransition2D;
import com.pump.image.transition.FlurryTransition2D;
import com.pump.image.transition.FunkyWipeTransition2D;
import com.pump.image.transition.GooTransition2D;
import com.pump.image.transition.HalftoneTransition2D;
import com.pump.image.transition.KaleidoscopeTransition2D;
import com.pump.image.transition.LevitateTransition2D;
import com.pump.image.transition.MeshShuffleTransition2D;
import com.pump.image.transition.MicroscopeTransition2D;
import com.pump.image.transition.MirageTransition2D;
import com.pump.image.transition.MotionBlendTransition2D;
import com.pump.image.transition.PivotTransition2D;
import com.pump.image.transition.PushTransition2D;
import com.pump.image.transition.RadialWipeTransition2D;
import com.pump.image.transition.RefractiveTransition2D;
import com.pump.image.transition.RevealTransition2D;
import com.pump.image.transition.RotateTransition2D;
import com.pump.image.transition.ScaleTransition2D;
import com.pump.image.transition.ScribbleTransition2D;
import com.pump.image.transition.SlideTransition2D;
import com.pump.image.transition.SpiralTransition2D;
import com.pump.image.transition.SplitTransition2D;
import com.pump.image.transition.SquareRainTransition2D;
import com.pump.image.transition.SquaresTransition2D;
import com.pump.image.transition.StarTransition2D;
import com.pump.image.transition.StarsTransition2D;
import com.pump.image.transition.SwivelTransition2D;
import com.pump.image.transition.TossTransition2D;
import com.pump.image.transition.Transition;
import com.pump.image.transition.Transition2D;
import com.pump.image.transition.Transition2DInstruction;
import com.pump.image.transition.WaveTransition2D;
import com.pump.image.transition.WipeTransition2D;
import com.pump.image.transition.ZoomTransition2D;

/**
 * A demo of the {@link Transition2D} architecture.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/Transition2DDemo.png"
 * alt="A screenshot of the Transition2DDemo.">
 * 
 * @see com.pump.showcase.Transition3DDemo
 *
 */
public class Transition2DDemo extends TransitionDemo {
	private static final long serialVersionUID = 1L;

	static Transition[][] transitions = new Transition[][] {
			BarsTransition2D.getDemoTransitions(),
			new Transition[] { new BlendTransition2D() },
			BlindsTransition2D.getDemoTransitions(),
			BoxTransition2D.getDemoTransitions(),
			CheckerboardTransition2D.getDemoTransitions(),
			CircleTransition2D.getDemoTransitions(),
			new Transition[] { new CollapseTransition2D() },
			new Transition[] { new CurtainTransition2D() },
			DiamondsTransition2D.getDemoTransitions(),
			DocumentaryTransition2D.getDemoTransitions(),
			new Transition[] { new DotsTransition2D() },
			new Transition[] { new DropTransition2D() },
			FlurryTransition2D.getDemoTransitions(),
			FunkyWipeTransition2D.getDemoTransitions(),
			new Transition[] { new GooTransition2D() },
			HalftoneTransition2D.getDemoTransitions(),
			new Transition[] { new KaleidoscopeTransition2D() },
			new Transition[] { new LevitateTransition2D() },
			new Transition[] { new MeshShuffleTransition2D() },
			new Transition[] { new MicroscopeTransition2D() },
			new Transition[] { new MirageTransition2D() },
			new Transition[] { new MotionBlendTransition2D() },
			new Transition[] { new RefractiveTransition2D() },
			PivotTransition2D.getDemoTransitions(),
			PushTransition2D.getDemoTransitions(),
			RadialWipeTransition2D.getDemoTransitions(),
			RevealTransition2D.getDemoTransitions(),
			RotateTransition2D.getDemoTransitions(),
			ScaleTransition2D.getDemoTransitions(),
			ScribbleTransition2D.getDemoTransitions(),
			SlideTransition2D.getDemoTransitions(),
			SpiralTransition2D.getDemoTransitions(),
			SplitTransition2D.getDemoTransitions(),
			new Transition[] { new SquareRainTransition2D() },
			new Transition[] { new SquaresTransition2D() },
			StarTransition2D.getDemoTransitions(),
			StarsTransition2D.getDemoTransitions(),
			SwivelTransition2D.getDemoTransitions(),
			TossTransition2D.getDemoTransitions(),
			WaveTransition2D.getDemoTransitions(),
			WipeTransition2D.getDemoTransitions(),
			ZoomTransition2D.getDemoTransitions() };

	public Transition2DDemo() {
		super(transitions);
	}

	@Override
	public String getTitle() {
		return "Transition2D";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a set new transitions based on 2D rendering operations.";
	}

	@Override
	public URL getHelpURL() {
		return Transition2DDemo.class.getResource("transition2Ddemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "animation", "slideshow", "transition",
				"vector-graphics" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Transition2D.class, Transition.class,
				Transition2DInstruction.class };
	}
}