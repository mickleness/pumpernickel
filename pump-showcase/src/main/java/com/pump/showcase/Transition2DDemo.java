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
import com.pump.image.transition.WaveTransition2D;
import com.pump.image.transition.WeaveTransition2D;
import com.pump.image.transition.WipeTransition2D;
import com.pump.image.transition.ZoomTransition2D;

/**
 * A demo of the {@link Transition2D} architecture.
 * 
 * @see com.pump.showcase.Transition3DDemo
 *
 */
public class Transition2DDemo extends TransitionDemo {
	private static final long serialVersionUID = 1L;

	static Transition[] transitions = new Transition[] {
			new BarsTransition2D(Transition2D.HORIZONTAL, true),
			new BarsTransition2D(Transition2D.HORIZONTAL, false),
			new BarsTransition2D(Transition2D.VERTICAL, true),
			new BarsTransition2D(Transition2D.VERTICAL, false),
			new BlendTransition2D(), new BlindsTransition2D(Transition2D.LEFT),
			new BlindsTransition2D(Transition2D.RIGHT),
			new BlindsTransition2D(Transition2D.UP),
			new BlindsTransition2D(Transition2D.DOWN),
			new BoxTransition2D(Transition2D.IN),
			new BoxTransition2D(Transition2D.OUT),
			new CheckerboardTransition2D(Transition2D.LEFT),
			new CheckerboardTransition2D(Transition2D.RIGHT),
			new CheckerboardTransition2D(Transition2D.UP),
			new CheckerboardTransition2D(Transition2D.DOWN),
			new CircleTransition2D(Transition2D.IN),
			new CircleTransition2D(Transition2D.OUT),
			new CollapseTransition2D(), new CurtainTransition2D(),
			new DiamondsTransition2D(55), new DiamondsTransition2D(90),
			new DiamondsTransition2D(120),
			new DocumentaryTransition2D(Transition2D.LEFT),
			new DocumentaryTransition2D(Transition2D.RIGHT),
			new DocumentaryTransition2D(Transition2D.UP),
			new DocumentaryTransition2D(Transition2D.DOWN),
			new DotsTransition2D(), new DropTransition2D(),
			new FlurryTransition2D(Transition2D.IN),
			new FlurryTransition2D(Transition2D.OUT),
			new FunkyWipeTransition2D(true), new FunkyWipeTransition2D(false),
			new GooTransition2D(), new HalftoneTransition2D(Transition2D.IN),
			new HalftoneTransition2D(Transition2D.OUT),
			new KaleidoscopeTransition2D(), new LevitateTransition2D(),
			new MeshShuffleTransition2D(), new MicroscopeTransition2D(),
			new MirageTransition2D(), new MotionBlendTransition2D(),
			new RefractiveTransition2D(),
			new PivotTransition2D(Transition2D.TOP_LEFT, true),
			new PivotTransition2D(Transition2D.TOP_RIGHT, true),
			new PivotTransition2D(Transition2D.BOTTOM_LEFT, true),
			new PivotTransition2D(Transition2D.BOTTOM_RIGHT, true),
			new PivotTransition2D(Transition2D.TOP_LEFT, false),
			new PivotTransition2D(Transition2D.TOP_RIGHT, false),
			new PivotTransition2D(Transition2D.BOTTOM_LEFT, false),
			new PivotTransition2D(Transition2D.BOTTOM_RIGHT, false),
			new PushTransition2D(Transition2D.LEFT),
			new PushTransition2D(Transition2D.RIGHT),
			new PushTransition2D(Transition2D.UP),
			new PushTransition2D(Transition2D.DOWN),
			new RadialWipeTransition2D(Transition2D.CLOCKWISE),
			new RadialWipeTransition2D(Transition2D.COUNTER_CLOCKWISE),
			new RevealTransition2D(Transition2D.LEFT),
			new RevealTransition2D(Transition2D.RIGHT),
			new RevealTransition2D(Transition2D.UP),
			new RevealTransition2D(Transition2D.DOWN),
			new RotateTransition2D(Transition2D.IN),
			new RotateTransition2D(Transition2D.OUT),
			new ScaleTransition2D(Transition2D.IN),
			new ScaleTransition2D(Transition2D.OUT),
			new ScribbleTransition2D(false), new ScribbleTransition2D(true),
			new SlideTransition2D(Transition2D.LEFT),
			new SlideTransition2D(Transition2D.RIGHT),
			new SlideTransition2D(Transition2D.UP),
			new SlideTransition2D(Transition2D.DOWN),
			new SpiralTransition2D(false), new SpiralTransition2D(true),
			new SplitTransition2D(Transition2D.HORIZONTAL, false),
			new SplitTransition2D(Transition2D.VERTICAL, false),
			new SplitTransition2D(Transition2D.HORIZONTAL, true),
			new SplitTransition2D(Transition2D.VERTICAL, true),
			new SquareRainTransition2D(), new SquaresTransition2D(),
			new StarTransition2D(Transition2D.IN),
			new StarTransition2D(Transition2D.OUT),
			new StarsTransition2D(Transition2D.LEFT),
			new StarsTransition2D(Transition2D.RIGHT),
			new SwivelTransition2D(Transition2D.CLOCKWISE),
			new SwivelTransition2D(Transition2D.COUNTER_CLOCKWISE),
			new TossTransition2D(Transition2D.LEFT),
			new TossTransition2D(Transition2D.RIGHT),
			new WaveTransition2D(Transition2D.UP),
			new WaveTransition2D(Transition2D.RIGHT), new WeaveTransition2D(),
			new WipeTransition2D(Transition2D.LEFT),
			new WipeTransition2D(Transition2D.RIGHT),
			new WipeTransition2D(Transition2D.UP),
			new WipeTransition2D(Transition2D.DOWN),
			new ZoomTransition2D(Transition2D.LEFT),
			new ZoomTransition2D(Transition2D.RIGHT) };

	public Transition2DDemo() {
		super(transitions, false);
	}
}