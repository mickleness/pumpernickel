var allLetters = new Object();

function getGlyph(letter) {
	var keyName = "glyph-"+letter;
	var g = allLetters[keyName];
	if(g==null) {
		g = createGlyph(letter);
		allLetters[keyName] = g;
	}
	return g;
}

function createGlyph(letter) {
	var glyph = new Object();
	glyph.descent = 0.3;
	glyph.leading = 0.1;
	if( letter==' ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5052083134651184;
		glyph.pixels = 0.0;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
			} else if(percentComplete<50) {
			} else if(percentComplete<75) {
			} else {
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='!') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.21875;
		glyph.pixels = 0.8707722;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10937502*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.10789415*sx+tx, 0.2281047*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10937502*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.10641328*sx+tx, 0.4457927*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10937502*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.104932405*sx+tx, 0.66348076*sy+ty);
			} else {
				ctx.moveTo( 0.10937502*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.7760417*sy+ty);
				ctx.moveTo( 0.11458335*sx+tx, 0.890625*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.9166667*sy+ty,0.093750015*sx+tx, 0.9739583*sy+ty,0.083333336*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='"') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4114583432674408;
		glyph.pixels = 0.48443162;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10937502*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.12631623*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10937502*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.24742414*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10937502*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.25*sy+ty);
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.2942989*sx+tx, 0.12892072*sy+ty);
			} else {
				ctx.moveTo( 0.10937502*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.25*sy+ty);
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.29166672*sx+tx, 0.25*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='#') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 3.7162259;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.515625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.24306312*sx+tx, 0.8985923*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.515625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.21354163*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.59256506*sx+tx, 0.80096793*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.515625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.21354163*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.5364582*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.12500001*sx+tx, 0.32291666*sy+ty);
				ctx.lineTo( 0.85221714*sx+tx, 0.33212197*sy+ty);
			} else {
				ctx.moveTo( 0.515625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.21354163*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.5364582*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.12500001*sx+tx, 0.32291666*sy+ty);
				ctx.lineTo( 0.9479165*sx+tx, 0.33333334*sy+ty);
				ctx.moveTo( 0.05729166*sx+tx, 0.6458333*sy+ty);
				ctx.lineTo( 0.8906248*sx+tx, 0.6510417*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='$') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.84375;
		glyph.pixels = 3.43812;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.74479145*sx+tx, 0.22916667*sy+ty);
				ctx.bezierCurveTo( 0.74479145*sx+tx, 0.22916667*sy+ty,0.6874998*sx+tx, 0.041666668*sy+ty,0.47916678*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.3541668*sx+tx, 0.036458336*sy+ty,0.17708328*sx+tx, 0.104166664*sy+ty,0.17708328*sx+tx, 0.23958328*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.2920943*sy+ty,0.20673092*sx+tx, 0.3344404*sy+ty,0.25031203*sx+tx, 0.36935455*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.74479145*sx+tx, 0.22916667*sy+ty);
				ctx.bezierCurveTo( 0.74479145*sx+tx, 0.22916667*sy+ty,0.6874998*sx+tx, 0.041666668*sy+ty,0.47916678*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.3541668*sx+tx, 0.036458336*sy+ty,0.17708328*sx+tx, 0.104166664*sy+ty,0.17708328*sx+tx, 0.23958328*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.36979166*sy+ty,0.35937512*sx+tx, 0.4375001*sy+ty,0.48437512*sx+tx, 0.48437506*sy+ty);
				ctx.bezierCurveTo( 0.60937476*sx+tx, 0.5312499*sy+ty,0.72395813*sx+tx, 0.5677083*sy+ty,0.7239581*sx+tx, 0.7499999*sy+ty);
				ctx.bezierCurveTo( 0.7239581*sx+tx, 0.8370515*sy+ty,0.6304963*sx+tx, 0.90642124*sy+ty,0.5420844*sx+tx, 0.94051754*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.74479145*sx+tx, 0.22916667*sy+ty);
				ctx.bezierCurveTo( 0.74479145*sx+tx, 0.22916667*sy+ty,0.6874998*sx+tx, 0.041666668*sy+ty,0.47916678*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.3541668*sx+tx, 0.036458336*sy+ty,0.17708328*sx+tx, 0.104166664*sy+ty,0.17708328*sx+tx, 0.23958328*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.36979166*sy+ty,0.35937512*sx+tx, 0.4375001*sy+ty,0.48437512*sx+tx, 0.48437506*sy+ty);
				ctx.bezierCurveTo( 0.60937476*sx+tx, 0.5312499*sy+ty,0.72395813*sx+tx, 0.5677083*sy+ty,0.7239581*sx+tx, 0.7499999*sy+ty);
				ctx.bezierCurveTo( 0.7239581*sx+tx, 0.8749999*sy+ty,0.53124994*sx+tx, 0.96354157*sy+ty,0.43750012*sx+tx, 0.96354157*sy+ty);
				ctx.bezierCurveTo( 0.24999996*sx+tx, 0.9635417*sy+ty,0.119791694*sx+tx, 0.8229167*sy+ty,0.10416668*sx+tx, 0.7499999*sy+ty);
				ctx.moveTo( 0.43229178*sx+tx, -0.09375*sy+ty);
				ctx.lineTo( 0.4308205*sx+tx, 0.24464476*sy+ty);
			} else {
				ctx.moveTo( 0.74479145*sx+tx, 0.22916667*sy+ty);
				ctx.bezierCurveTo( 0.74479145*sx+tx, 0.22916667*sy+ty,0.6874998*sx+tx, 0.041666668*sy+ty,0.47916678*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.3541668*sx+tx, 0.036458336*sy+ty,0.17708328*sx+tx, 0.104166664*sy+ty,0.17708328*sx+tx, 0.23958328*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.36979166*sy+ty,0.35937512*sx+tx, 0.4375001*sy+ty,0.48437512*sx+tx, 0.48437506*sy+ty);
				ctx.bezierCurveTo( 0.60937476*sx+tx, 0.5312499*sy+ty,0.72395813*sx+tx, 0.5677083*sy+ty,0.7239581*sx+tx, 0.7499999*sy+ty);
				ctx.bezierCurveTo( 0.7239581*sx+tx, 0.8749999*sy+ty,0.53124994*sx+tx, 0.96354157*sy+ty,0.43750012*sx+tx, 0.96354157*sy+ty);
				ctx.bezierCurveTo( 0.24999996*sx+tx, 0.9635417*sy+ty,0.119791694*sx+tx, 0.8229167*sy+ty,0.10416668*sx+tx, 0.7499999*sy+ty);
				ctx.moveTo( 0.43229178*sx+tx, -0.09375*sy+ty);
				ctx.lineTo( 0.42708343*sx+tx, 1.1041666*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='%') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 3.0767503;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.22395828*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.5960036*sx+tx, 0.326775*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.22395828*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.77083313*sx+tx, 0.010416667*sy+ty);
				ctx.moveTo( 0.2760417*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34895834*sy+ty,0.09895834*sx+tx, 0.33333334*sy+ty,0.09375001*sx+tx, 0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.12948492*sy+ty,0.123513065*sx+tx, 0.07476524*sy+ty,0.16945687*sx+tx, 0.042131677*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.22395828*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.77083313*sx+tx, 0.010416667*sy+ty);
				ctx.moveTo( 0.2760417*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34895834*sy+ty,0.09895834*sx+tx, 0.33333334*sy+ty,0.09375001*sx+tx, 0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.09375*sy+ty,0.16666663*sx+tx, 0.015625*sy+ty,0.26041666*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.3281251*sx+tx, 0.010416667*sy+ty,0.4218751*sx+tx, 0.06770831*sy+ty,0.4218751*sx+tx, 0.17708333*sy+ty);
				ctx.bezierCurveTo( 0.4218751*sx+tx, 0.25520834*sy+ty,0.3750001*sx+tx, 0.34375*sy+ty,0.27083334*sx+tx, 0.34375*sy+ty);
				ctx.moveTo( 0.7291665*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.68004364*sx+tx, 0.9739583*sy+ty,0.60028887*sx+tx, 0.9280105*sy+ty,0.57854426*sx+tx, 0.8525301*sy+ty);
			} else {
				ctx.moveTo( 0.22395828*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.77083313*sx+tx, 0.010416667*sy+ty);
				ctx.moveTo( 0.2760417*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34895834*sy+ty,0.09895834*sx+tx, 0.33333334*sy+ty,0.09375001*sx+tx, 0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.09375*sy+ty,0.16666663*sx+tx, 0.015625*sy+ty,0.26041666*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.3281251*sx+tx, 0.010416667*sy+ty,0.4218751*sx+tx, 0.06770831*sy+ty,0.4218751*sx+tx, 0.17708333*sy+ty);
				ctx.bezierCurveTo( 0.4218751*sx+tx, 0.25520834*sy+ty,0.3750001*sx+tx, 0.34375*sy+ty,0.27083334*sx+tx, 0.34375*sy+ty);
				ctx.moveTo( 0.7291665*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6718748*sx+tx, 0.9739583*sy+ty,0.5729165*sx+tx, 0.9114583*sy+ty,0.5729165*sx+tx, 0.8125*sy+ty);
				ctx.bezierCurveTo( 0.5729165*sx+tx, 0.7135417*sy+ty,0.66666645*sx+tx, 0.6510417*sy+ty,0.75520813*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.8437498*sx+tx, 0.6510417*sy+ty,0.9010415*sx+tx, 0.7291666*sy+ty,0.9010415*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.9010415*sx+tx, 0.90625*sy+ty,0.82291645*sx+tx, 0.9791667*sy+ty,0.72916645*sx+tx, 0.9739584*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='&') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9270833134651184;
		glyph.pixels = 3.1532612;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.73958313*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.73958313*sx+tx, 0.5625*sy+ty,0.609375*sx+tx, 0.9791667*sy+ty,0.36979178*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.27434132*sx+tx, 0.96875*sy+ty,0.18929087*sx+tx, 0.9354702*sy+ty,0.14092919*sx+tx, 0.87416846*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.73958313*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.73958313*sx+tx, 0.5625*sy+ty,0.609375*sx+tx, 0.9791667*sy+ty,0.36979178*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.21874997*sx+tx, 0.96875*sy+ty,0.09375*sx+tx, 0.8854167*sy+ty,0.09895834*sx+tx, 0.7395833*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.6614583*sy+ty,0.17708331*sx+tx, 0.5416666*sy+ty,0.32291678*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.4415678*sx+tx, 0.40942448*sy+ty,0.5429802*sx+tx, 0.3190697*sy+ty,0.5766631*sx+tx, 0.24817693*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.73958313*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.73958313*sx+tx, 0.5625*sy+ty,0.609375*sx+tx, 0.9791667*sy+ty,0.36979178*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.21874997*sx+tx, 0.96875*sy+ty,0.09375*sx+tx, 0.8854167*sy+ty,0.09895834*sx+tx, 0.7395833*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.6614583*sy+ty,0.17708331*sx+tx, 0.5416666*sy+ty,0.32291678*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.4687501*sx+tx, 0.39583337*sy+ty,0.58854145*sx+tx, 0.2760417*sy+ty,0.58854145*sx+tx, 0.203125*sy+ty);
				ctx.bezierCurveTo( 0.58854145*sx+tx, 0.13020831*sy+ty,0.49479178*sx+tx, 0.020833334*sy+ty,0.41666678*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.33854178*sx+tx, 0.020833334*sy+ty,0.23437496*sx+tx, 0.08854166*sy+ty,0.23437496*sx+tx, 0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.23437497*sx+tx, 0.22753346*sy+ty,0.2585372*sx+tx, 0.28074753*sy+ty,0.2970702*sx+tx, 0.34346896*sy+ty);
			} else {
				ctx.moveTo( 0.73958313*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.73958313*sx+tx, 0.5625*sy+ty,0.609375*sx+tx, 0.9791667*sy+ty,0.36979178*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.21874997*sx+tx, 0.96875*sy+ty,0.09375*sx+tx, 0.8854167*sy+ty,0.09895834*sx+tx, 0.7395833*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.6614583*sy+ty,0.17708331*sx+tx, 0.5416666*sy+ty,0.32291678*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.4687501*sx+tx, 0.39583337*sy+ty,0.58854145*sx+tx, 0.2760417*sy+ty,0.58854145*sx+tx, 0.203125*sy+ty);
				ctx.bezierCurveTo( 0.58854145*sx+tx, 0.13020831*sy+ty,0.49479178*sx+tx, 0.020833334*sy+ty,0.41666678*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.33854178*sx+tx, 0.020833334*sy+ty,0.23437496*sx+tx, 0.08854166*sy+ty,0.23437496*sx+tx, 0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.234375*sx+tx, 0.36458334*sy+ty,0.82291645*sx+tx, 0.984375*sy+ty,0.82291645*sx+tx, 0.984375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='\'') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.1145833358168602;
		glyph.pixels = 0.24479167;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.046874996*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.046874996*sx+tx, 0.061197918*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.046874996*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.046874996*sx+tx, 0.122395836*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.046874996*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.046874996*sx+tx, 0.18359375*sy+ty);
			} else {
				ctx.moveTo( 0.046874996*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.046874996*sx+tx, 0.24479167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='(') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4947916567325592;
		glyph.pixels = 1.4006696;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.40104175*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.40104175*sx+tx, -0.15104167*sy+ty,0.31527954*sx+tx, -0.06382588*sy+ty,0.23513773*sx+tx, 0.06453044*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.40104175*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.40104175*sx+tx, -0.15104167*sy+ty,0.093750015*sx+tx, 0.16145837*sy+ty,0.09895835*sx+tx, 0.47395834*sy+ty);
				ctx.bezierCurveTo( 0.099223085*sx+tx, 0.49487215*sy+ty,0.100160606*sx+tx, 0.5153957*sy+ty,0.101701856*sx+tx, 0.5355147*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.40104175*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.40104175*sx+tx, -0.15104167*sy+ty,0.093750015*sx+tx, 0.16145837*sy+ty,0.09895835*sx+tx, 0.47395834*sy+ty);
				ctx.bezierCurveTo( 0.10169488*sx+tx, 0.6901444*sy+ty,0.1763221*sx+tx, 0.8646339*sy+ty,0.24653979*sx+tx, 0.98156244*sy+ty);
			} else {
				ctx.moveTo( 0.40104175*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.40104175*sx+tx, -0.15104167*sy+ty,0.093750015*sx+tx, 0.16145837*sy+ty,0.09895835*sx+tx, 0.47395834*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.8854167*sy+ty,0.36979175*sx+tx, 1.1458334*sy+ty,0.36979175*sx+tx, 1.1458334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter==')') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5;
		glyph.pixels = 1.4270902;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.078125*sx+tx, -0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, -0.16145833*sy+ty,0.16272618*sx+tx, -0.11099449*sy+ty,0.2423959*sx+tx, 0.0065719974*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.078125*sx+tx, -0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, -0.16145833*sy+ty,0.3750001*sx+tx, 0.015625*sy+ty,0.38020843*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.38051882*sx+tx, 0.5067902*sy+ty,0.3797934*sx+tx, 0.5335444*sy+ty,0.37815458*sx+tx, 0.55943483*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.078125*sx+tx, -0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, -0.16145833*sy+ty,0.3750001*sx+tx, 0.015625*sy+ty,0.38020843*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.3829678*sx+tx, 0.72474927*sy+ty,0.30386096*sx+tx, 0.9016228*sy+ty,0.22885822*sx+tx, 1.0136597*sy+ty);
			} else {
				ctx.moveTo( 0.078125*sx+tx, -0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, -0.16145833*sy+ty,0.3750001*sx+tx, 0.015625*sy+ty,0.38020843*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.38541678*sx+tx, 0.9427083*sy+ty,0.09895834*sx+tx, 1.1614584*sy+ty,0.09895834*sx+tx, 1.1614584*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='*') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5104166865348816;
		glyph.pixels = 1.0285251;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.25520828*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.26041666*sx+tx, 0.19791667*sy+ty);
				ctx.moveTo( 0.083333336*sx+tx, 0.09375*sy+ty);
				ctx.lineTo( 0.1368779*sx+tx, 0.12944637*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.25520828*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.26041666*sx+tx, 0.19791667*sy+ty);
				ctx.moveTo( 0.083333336*sx+tx, 0.09375*sy+ty);
				ctx.lineTo( 0.27083334*sx+tx, 0.21875*sy+ty);
				ctx.lineTo( 0.3480407*sx+tx, 0.16146713*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.25520828*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.26041666*sx+tx, 0.19791667*sy+ty);
				ctx.moveTo( 0.083333336*sx+tx, 0.09375*sy+ty);
				ctx.lineTo( 0.27083334*sx+tx, 0.21875*sy+ty);
				ctx.lineTo( 0.43229175*sx+tx, 0.098958336*sy+ty);
				ctx.moveTo( 0.10937502*sx+tx, 0.34895834*sy+ty);
				ctx.lineTo( 0.21895239*sx+tx, 0.24329437*sy+ty);
			} else {
				ctx.moveTo( 0.25520828*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.26041666*sx+tx, 0.19791667*sy+ty);
				ctx.moveTo( 0.083333336*sx+tx, 0.09375*sy+ty);
				ctx.lineTo( 0.27083334*sx+tx, 0.21875*sy+ty);
				ctx.lineTo( 0.43229175*sx+tx, 0.098958336*sy+ty);
				ctx.moveTo( 0.10937502*sx+tx, 0.34895834*sy+ty);
				ctx.lineTo( 0.25520828*sx+tx, 0.20833333*sy+ty);
				ctx.lineTo( 0.41145843*sx+tx, 0.34375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='+') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5885416865348816;
		glyph.pixels = 0.90980065;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.29166675*sx+tx, 0.41666666*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.41666666*sy+ty,0.29166675*sx+tx, 0.5125748*sy+ty,0.2924343*sx+tx, 0.6184233*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.29166675*sx+tx, 0.41666666*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.41666666*sy+ty,0.29166675*sx+tx, 0.7604167*sy+ty,0.2968751*sx+tx, 0.8645833*sy+ty);
				ctx.moveTo( 0.046874996*sx+tx, 0.6302083*sy+ty);
				ctx.lineTo( 0.07116809*sx+tx, 0.63047236*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.29166675*sx+tx, 0.41666666*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.41666666*sy+ty,0.29166675*sx+tx, 0.7604167*sy+ty,0.2968751*sx+tx, 0.8645833*sy+ty);
				ctx.moveTo( 0.046874996*sx+tx, 0.6302083*sy+ty);
				ctx.lineTo( 0.29860485*sx+tx, 0.6329445*sy+ty);
			} else {
				ctx.moveTo( 0.29166675*sx+tx, 0.41666666*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.41666666*sy+ty,0.29166675*sx+tx, 0.7604167*sy+ty,0.2968751*sx+tx, 0.8645833*sy+ty);
				ctx.moveTo( 0.046874996*sx+tx, 0.6302083*sy+ty);
				ctx.lineTo( 0.52604157*sx+tx, 0.6354167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter==',') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2552083432674408;
		glyph.pixels = 0.31585354;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.13508849*sx+tx, 0.91732305*sy+ty,0.14811386*sx+tx, 0.90754503*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.14062501*sx+tx, 0.90625*sy+ty,0.15104167*sx+tx, 0.90625*sy+ty);
				ctx.bezierCurveTo( 0.15741853*sx+tx, 0.90625*sy+ty,0.15793973*sx+tx, 0.9374802*sy+ty,0.15260524*sx+tx, 0.97006804*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.14062501*sx+tx, 0.90625*sy+ty,0.15104167*sx+tx, 0.90625*sy+ty);
				ctx.bezierCurveTo( 0.16145833*sx+tx, 0.90625*sy+ty,0.15625*sx+tx, 0.98958325*sy+ty,0.13541667*sx+tx, 1.0260416*sy+ty);
				ctx.bezierCurveTo( 0.1292457*sx+tx, 1.0368408*sy+ty,0.12124685*sx+tx, 1.0494679*sy+ty,0.1130444*sx+tx, 1.0618925*sy+ty);
			} else {
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.14062501*sx+tx, 0.90625*sy+ty,0.15104167*sx+tx, 0.90625*sy+ty);
				ctx.bezierCurveTo( 0.16145833*sx+tx, 0.90625*sy+ty,0.15625*sx+tx, 0.98958325*sy+ty,0.13541667*sx+tx, 1.0260416*sy+ty);
				ctx.bezierCurveTo( 0.11458334*sx+tx, 1.0625*sy+ty,0.072916664*sx+tx, 1.1197916*sy+ty,0.072916664*sx+tx, 1.1197916*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='-') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5833333134651184;
		glyph.pixels = 0.4270834;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.17968751*sx+tx, 0.6354167*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.28645837*sx+tx, 0.6354167*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.39322922*sx+tx, 0.6354167*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.50000006*sx+tx, 0.6354167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='.') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.171875;
		glyph.pixels = 0.05298952;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.083333336*sx+tx, 0.9479167*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.9479167*sy+ty,0.08528646*sx+tx, 0.9453125*sy+ty,0.087402344*sx+tx, 0.94246423*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.083333336*sx+tx, 0.9479167*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.9479167*sy+ty,0.091145836*sx+tx, 0.9375*sy+ty,0.09244792*sx+tx, 0.9355469*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.083333336*sx+tx, 0.9479167*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.9479167*sy+ty,0.10091146*sx+tx, 0.9244792*sy+ty,0.08772786*sx+tx, 0.94132483*sy+ty);
			} else {
				ctx.moveTo( 0.083333336*sx+tx, 0.9479167*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.9479167*sy+ty,0.114583336*sx+tx, 0.90625*sy+ty,0.0625*sx+tx, 0.9739583*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='/') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7604166865348816;
		glyph.pixels = 1.1311799;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.65104145*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.5117186*sx+tx, 0.24609375*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.65104145*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.37239572*sx+tx, 0.4921875*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.65104145*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.23307288*sx+tx, 0.73828125*sy+ty);
			} else {
				ctx.moveTo( 0.65104145*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.09375001*sx+tx, 0.984375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='0') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8229166865348816;
		glyph.pixels = 2.3344722;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.4583334*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.32030833*sx+tx, 0.036458332*sy+ty,0.13879003*sx+tx, 0.14084198*sy+ty,0.10455454*sx+tx, 0.45294622*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.4583334*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.30729175*sx+tx, 0.036458332*sy+ty,0.10416669*sx+tx, 0.16145833*sy+ty,0.09895835*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.09895835*sx+tx, 0.7448562*sy+ty,0.2028639*sx+tx, 0.95768094*sy+ty,0.3817397*sx+tx, 0.96833295*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.4583334*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.30729175*sx+tx, 0.036458332*sy+ty,0.10416669*sx+tx, 0.16145833*sy+ty,0.09895835*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.09895835*sx+tx, 0.75*sy+ty,0.2083333*sx+tx, 0.96875*sy+ty,0.3958334*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.5104167*sx+tx, 0.96875*sy+ty,0.7291665*sx+tx, 0.8229167*sy+ty,0.7239582*sx+tx, 0.5520833*sy+ty);
				ctx.bezierCurveTo( 0.7239582*sx+tx, 0.54359496*sy+ty,0.72390175*sx+tx, 0.5347904*sy+ty,0.72376883*sx+tx, 0.52571744*sy+ty);
			} else {
				ctx.moveTo( 0.4583334*sx+tx, 0.036458336*sy+ty);
				ctx.bezierCurveTo( 0.30729175*sx+tx, 0.036458332*sy+ty,0.10416669*sx+tx, 0.16145833*sy+ty,0.09895835*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.09895835*sx+tx, 0.75*sy+ty,0.2083333*sx+tx, 0.96875*sy+ty,0.3958334*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.5104167*sx+tx, 0.96875*sy+ty,0.7291665*sx+tx, 0.8229167*sy+ty,0.7239582*sx+tx, 0.5520833*sy+ty);
				ctx.bezierCurveTo( 0.7239582*sx+tx, 0.36979163*sy+ty,0.6979165*sx+tx, 0.041666668*sy+ty,0.44791675*sx+tx, 0.041666668*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='1') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.53125;
		glyph.pixels = 1.624804;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.30073178*sx+tx, 0.13047849*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.29372934*sx+tx, 0.5366191*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.2867269*sx+tx, 0.94275975*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.28645837*sx+tx, 0.9583333*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.46354178*sx+tx, 0.9635417*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='2') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 2.0600471;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.119791694*sx+tx, 0.18229167*sy+ty);
				ctx.bezierCurveTo( 0.046875004*sx+tx, 0.25*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.40625012*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.45126396*sx+tx, 0.038853303*sy+ty,0.51147455*sx+tx, 0.07403203*sy+ty,0.5573302*sx+tx, 0.122576416*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.119791694*sx+tx, 0.18229167*sy+ty);
				ctx.bezierCurveTo( 0.046875004*sx+tx, 0.25*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.40625012*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.48958346*sx+tx, 0.036458332*sy+ty,0.62499976*sx+tx, 0.16145831*sy+ty,0.62499976*sx+tx, 0.26041666*sy+ty);
				ctx.bezierCurveTo( 0.63018286*sx+tx, 0.35371298*sy+ty,0.49609905*sx+tx, 0.48311558*sy+ty,0.38700643*sx+tx, 0.551096*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.119791694*sx+tx, 0.18229167*sy+ty);
				ctx.bezierCurveTo( 0.046875004*sx+tx, 0.25*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.40625012*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.48958346*sx+tx, 0.036458332*sy+ty,0.62499976*sx+tx, 0.16145831*sy+ty,0.62499976*sx+tx, 0.26041666*sy+ty);
				ctx.bezierCurveTo( 0.6302081*sx+tx, 0.35416666*sy+ty,0.49479175*sx+tx, 0.48437494*sy+ty,0.38541678*sx+tx, 0.5520833*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.6197917*sy+ty,0.16666663*sx+tx, 0.7395834*sy+ty,0.15104166*sx+tx, 0.7760417*sy+ty);
				ctx.bezierCurveTo( 0.13577734*sx+tx, 0.81165844*sy+ty,0.13542499*sx+tx, 0.96657103*sy+ty,0.13541687*sx+tx, 0.9737037*sy+ty);
			} else {
				ctx.moveTo( 0.119791694*sx+tx, 0.18229167*sy+ty);
				ctx.bezierCurveTo( 0.046875004*sx+tx, 0.25*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.40625012*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.48958346*sx+tx, 0.036458332*sy+ty,0.62499976*sx+tx, 0.16145831*sy+ty,0.62499976*sx+tx, 0.26041666*sy+ty);
				ctx.bezierCurveTo( 0.6302081*sx+tx, 0.35416666*sy+ty,0.49479175*sx+tx, 0.48437494*sy+ty,0.38541678*sx+tx, 0.5520833*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.6197917*sy+ty,0.16666663*sx+tx, 0.7395834*sy+ty,0.15104166*sx+tx, 0.7760417*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.8125*sy+ty,0.13541667*sx+tx, 0.9739583*sy+ty,0.13541667*sx+tx, 0.9739583*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.96875*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='3') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.734375;
		glyph.pixels = 2.0829797;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.12500001*sx+tx, 0.13541667*sy+ty);
				ctx.bezierCurveTo( 0.12500001*sx+tx, 0.13541667*sy+ty,0.24999996*sx+tx, 0.046875*sy+ty,0.3593751*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.45806974*sx+tx, 0.046875*sy+ty,0.58220917*sx+tx, 0.118968956*sy+ty,0.60551214*sx+tx, 0.1942761*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.12500001*sx+tx, 0.13541667*sy+ty);
				ctx.bezierCurveTo( 0.12500001*sx+tx, 0.13541667*sy+ty,0.24999996*sx+tx, 0.046875*sy+ty,0.3593751*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.4687501*sx+tx, 0.046875*sy+ty,0.6093748*sx+tx, 0.13541666*sy+ty,0.6093748*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.36979166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.46354166*sy+ty,0.33091855*sx+tx, 0.46286997*sy+ty,0.3519073*sx+tx, 0.46445122*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.12500001*sx+tx, 0.13541667*sy+ty);
				ctx.bezierCurveTo( 0.12500001*sx+tx, 0.13541667*sy+ty,0.24999996*sx+tx, 0.046875*sy+ty,0.3593751*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.4687501*sx+tx, 0.046875*sy+ty,0.6093748*sx+tx, 0.13541666*sy+ty,0.6093748*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.36979166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.46354166*sy+ty,0.62499976*sx+tx, 0.44791675*sy+ty,0.63020813*sx+tx, 0.7447917*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.8255044*sy+ty,0.5819683*sx+tx, 0.87865144*sy+ty,0.52486247*sx+tx, 0.9128627*sy+ty);
			} else {
				ctx.moveTo( 0.12500001*sx+tx, 0.13541667*sy+ty);
				ctx.bezierCurveTo( 0.12500001*sx+tx, 0.13541667*sy+ty,0.24999996*sx+tx, 0.046875*sy+ty,0.3593751*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.4687501*sx+tx, 0.046875*sy+ty,0.6093748*sx+tx, 0.13541666*sy+ty,0.6093748*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.36979166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty,0.31770843*sx+tx, 0.46354166*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.46354166*sy+ty,0.62499976*sx+tx, 0.44791675*sy+ty,0.63020813*sx+tx, 0.7447917*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.9166667*sy+ty,0.4114584*sx+tx, 0.9635417*sy+ty,0.35416675*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.2968751*sx+tx, 0.9739583*sy+ty,0.10937502*sx+tx, 0.8854167*sy+ty,0.08854167*sx+tx, 0.7916667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='4') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.640625;
		glyph.pixels = 2.2556505;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479175*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.19966888*sx+tx, 0.53260386*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479175*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.08854167*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.44004083*sx+tx, 0.7207891*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479175*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.08854167*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.7239583*sy+ty);
				ctx.moveTo( 0.5104167*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.5038373*sx+tx, 0.4309516*sy+ty);
			} else {
				ctx.moveTo( 0.49479175*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.08854167*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.7239583*sy+ty);
				ctx.moveTo( 0.5104167*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.49479175*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='5') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7708333134651184;
		glyph.pixels = 2.3197963;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.64583313*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.20312496*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.20312496*sx+tx, 0.052083332*sy+ty,0.19725664*sx+tx, 0.09381356*sy+ty,0.18944201*sx+tx, 0.15051225*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.64583313*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.20312496*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.20312496*sx+tx, 0.052083332*sy+ty,0.15624997*sx+tx, 0.38541666*sy+ty,0.15104166*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, 0.38020834*sy+ty,0.37500012*sx+tx, 0.328125*sy+ty,0.40104178*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.41980323*sx+tx, 0.32747805*sy+ty,0.43784148*sx+tx, 0.32956335*sy+ty,0.45504668*sx+tx, 0.33397162*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.64583313*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.20312496*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.20312496*sx+tx, 0.052083332*sy+ty,0.15624997*sx+tx, 0.38541666*sy+ty,0.15104166*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, 0.38020834*sy+ty,0.37500012*sx+tx, 0.328125*sy+ty,0.40104178*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.55208313*sx+tx, 0.32291666*sy+ty,0.6562498*sx+tx, 0.49479163*sy+ty,0.6562498*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.65859115*sx+tx, 0.7215215*sy+ty,0.634619*sx+tx, 0.79809904*sy+ty,0.5914308*sx+tx, 0.85520947*sy+ty);
			} else {
				ctx.moveTo( 0.64583313*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.20312496*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.20312496*sx+tx, 0.052083332*sy+ty,0.15624997*sx+tx, 0.38541666*sy+ty,0.15104166*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, 0.38020834*sy+ty,0.37500012*sx+tx, 0.328125*sy+ty,0.40104178*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.55208313*sx+tx, 0.32291666*sy+ty,0.6562498*sx+tx, 0.49479163*sy+ty,0.6562498*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.66145813*sx+tx, 0.8333333*sy+ty,0.5364582*sx+tx, 0.9635417*sy+ty,0.3593751*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.20833328*sx+tx, 0.953125*sy+ty,0.09895834*sx+tx, 0.8020833*sy+ty,0.09895834*sx+tx, 0.8020833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='6') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7291666865348816;
		glyph.pixels = 2.306946;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479175*sx+tx, 0.03125*sy+ty);
				ctx.bezierCurveTo( 0.49479175*sx+tx, 0.03125*sy+ty,0.265625*sx+tx, 0.171875*sy+ty,0.19270828*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.1606914*sx+tx, 0.339692*sy+ty,0.13414161*sx+tx, 0.40290374*sy+ty,0.11720801*sx+tx, 0.47196645*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479175*sx+tx, 0.03125*sy+ty);
				ctx.bezierCurveTo( 0.49479175*sx+tx, 0.03125*sy+ty,0.265625*sx+tx, 0.171875*sy+ty,0.19270828*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.098958336*sx+tx, 0.43229166*sy+ty,0.05208333*sx+tx, 0.703125*sy+ty,0.15624997*sx+tx, 0.8697917*sy+ty);
				ctx.bezierCurveTo( 0.18136543*sx+tx, 0.93006873*sy+ty,0.25855854*sx+tx, 0.95159036*sy+ty,0.33818787*sx+tx, 0.9536291*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479175*sx+tx, 0.03125*sy+ty);
				ctx.bezierCurveTo( 0.49479175*sx+tx, 0.03125*sy+ty,0.265625*sx+tx, 0.171875*sy+ty,0.19270828*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.098958336*sx+tx, 0.43229166*sy+ty,0.05208333*sx+tx, 0.703125*sy+ty,0.15624997*sx+tx, 0.8697917*sy+ty);
				ctx.bezierCurveTo( 0.20833333*sx+tx, 0.9947917*sy+ty,0.4843751*sx+tx, 0.95312506*sy+ty,0.5416665*sx+tx, 0.9166667*sy+ty);
				ctx.bezierCurveTo( 0.6109792*sx+tx, 0.87334627*sy+ty,0.6766887*sx+tx, 0.7219303*sy+ty,0.6368984*sx+tx, 0.57630306*sy+ty);
			} else {
				ctx.moveTo( 0.49479175*sx+tx, 0.03125*sy+ty);
				ctx.bezierCurveTo( 0.49479175*sx+tx, 0.03125*sy+ty,0.265625*sx+tx, 0.171875*sy+ty,0.19270828*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.098958336*sx+tx, 0.43229166*sy+ty,0.05208333*sx+tx, 0.703125*sy+ty,0.15624997*sx+tx, 0.8697917*sy+ty);
				ctx.bezierCurveTo( 0.20833333*sx+tx, 0.9947917*sy+ty,0.4843751*sx+tx, 0.95312506*sy+ty,0.5416665*sx+tx, 0.9166667*sy+ty);
				ctx.bezierCurveTo( 0.6249998*sx+tx, 0.8645833*sy+ty,0.7031248*sx+tx, 0.65625*sy+ty,0.59895813*sx+tx, 0.48958334*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.40104166*sy+ty,0.42708346*sx+tx, 0.375*sy+ty,0.33333343*sx+tx, 0.390625*sy+ty);
				ctx.bezierCurveTo( 0.23958327*sx+tx, 0.40625*sy+ty,0.13020834*sx+tx, 0.46875*sy+ty,0.13020834*sx+tx, 0.46875*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='7') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7291666865348816;
		glyph.pixels = 1.6183447;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.06770833*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.47227746*sx+tx, 0.061003312*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.06770833*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.6354165*sx+tx, 0.0625*sy+ty);
				ctx.lineTo( 0.5241028*sx+tx, 0.27674907*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.06770833*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.6354165*sx+tx, 0.0625*sy+ty);
				ctx.lineTo( 0.33757222*sx+tx, 0.6357704*sy+ty);
			} else {
				ctx.moveTo( 0.06770833*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.6354165*sx+tx, 0.0625*sy+ty);
				ctx.lineTo( 0.15104164*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='8') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7135416865348816;
		glyph.pixels = 2.7986107;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.4114584*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.041666664*sy+ty,0.14583331*sx+tx, 0.16145834*sy+ty,0.14583331*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.14583331*sx+tx, 0.33854166*sy+ty,0.24479163*sx+tx, 0.41145834*sy+ty,0.36979175*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.38814715*sx+tx, 0.45442986*sy+ty,0.4056275*sx+tx, 0.46168354*sy+ty,0.42219448*sx+tx, 0.46958587*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.4114584*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.041666664*sy+ty,0.14583331*sx+tx, 0.16145834*sy+ty,0.14583331*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.14583331*sx+tx, 0.33854166*sy+ty,0.24479163*sx+tx, 0.41145834*sy+ty,0.36979175*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.5312499*sx+tx, 0.5052083*sy+ty,0.6249998*sx+tx, 0.6197917*sy+ty,0.6249998*sx+tx, 0.7291667*sy+ty);
				ctx.bezierCurveTo( 0.6249998*sx+tx, 0.8873063*sy+ty,0.55466443*sx+tx, 0.9563544*sy+ty,0.3961975*sx+tx, 0.9630058*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.4114584*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.041666664*sy+ty,0.14583331*sx+tx, 0.16145834*sy+ty,0.14583331*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.14583331*sx+tx, 0.33854166*sy+ty,0.24479163*sx+tx, 0.41145834*sy+ty,0.36979175*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.5312499*sx+tx, 0.5052083*sy+ty,0.6249998*sx+tx, 0.6197917*sy+ty,0.6249998*sx+tx, 0.7291667*sy+ty);
				ctx.bezierCurveTo( 0.6249998*sx+tx, 0.8958333*sy+ty,0.5468748*sx+tx, 0.9635417*sy+ty,0.36979175*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.21874994*sx+tx, 0.9635417*sy+ty,0.09375001*sx+tx, 0.875*sy+ty,0.09375001*sx+tx, 0.734375*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6569448*sy+ty,0.19267465*sx+tx, 0.5390454*sy+ty,0.28189483*sx+tx, 0.49348417*sy+ty);
			} else {
				ctx.moveTo( 0.4114584*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.041666664*sy+ty,0.14583331*sx+tx, 0.16145834*sy+ty,0.14583331*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.14583331*sx+tx, 0.33854166*sy+ty,0.24479163*sx+tx, 0.41145834*sy+ty,0.36979175*sx+tx, 0.44791666*sy+ty);
				ctx.bezierCurveTo( 0.5312499*sx+tx, 0.5052083*sy+ty,0.6249998*sx+tx, 0.6197917*sy+ty,0.6249998*sx+tx, 0.7291667*sy+ty);
				ctx.bezierCurveTo( 0.6249998*sx+tx, 0.8958333*sy+ty,0.5468748*sx+tx, 0.9635417*sy+ty,0.36979175*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.21874994*sx+tx, 0.9635417*sy+ty,0.09375001*sx+tx, 0.875*sy+ty,0.09375001*sx+tx, 0.734375*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6510417*sy+ty,0.2083333*sx+tx, 0.5208333*sy+ty,0.3020834*sx+tx, 0.484375*sy+ty);
				ctx.bezierCurveTo( 0.36979175*sx+tx, 0.453125*sy+ty,0.6041665*sx+tx, 0.3333333*sy+ty,0.6041665*sx+tx, 0.234375*sy+ty);
				ctx.bezierCurveTo( 0.6041665*sx+tx, 0.13541669*sy+ty,0.4895834*sx+tx, 0.036458332*sy+ty,0.4062501*sx+tx, 0.046875*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='9') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 2.3028238;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.61458313*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.5260417*sy+ty,0.42708343*sx+tx, 0.6197917*sy+ty,0.35416678*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.25176996*sx+tx, 0.6197917*sy+ty,0.1583073*sx+tx, 0.5483194*sy+ty,0.11881577*sx+tx, 0.43915263*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.61458313*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.5260417*sy+ty,0.42708343*sx+tx, 0.6197917*sy+ty,0.35416678*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.6197917*sy+ty,0.09895834*sx+tx, 0.49479166*sy+ty,0.09895834*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.1875*sy+ty,0.23437496*sx+tx, 0.036458332*sy+ty,0.39583343*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.4166767*sx+tx, 0.036458332*sy+ty,0.4359358*sx+tx, 0.03901739*sy+ty,0.45370957*sx+tx, 0.043713737*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.61458313*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.5260417*sy+ty,0.42708343*sx+tx, 0.6197917*sy+ty,0.35416678*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.6197917*sy+ty,0.09895834*sx+tx, 0.49479166*sy+ty,0.09895834*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.1875*sy+ty,0.23437496*sx+tx, 0.036458332*sy+ty,0.39583343*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.58854145*sx+tx, 0.036458332*sy+ty,0.64583313*sx+tx, 0.25520834*sy+ty,0.64583313*sx+tx, 0.359375*sy+ty);
				ctx.bezierCurveTo( 0.64710397*sx+tx, 0.434355*sy+ty,0.63162994*sx+tx, 0.50685424*sy+ty,0.6055398*sx+tx, 0.57445157*sy+ty);
			} else {
				ctx.moveTo( 0.61458313*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.61458313*sx+tx, 0.5260417*sy+ty,0.42708343*sx+tx, 0.6197917*sy+ty,0.35416678*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.6197917*sy+ty,0.09895834*sx+tx, 0.49479166*sy+ty,0.09895834*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.1875*sy+ty,0.23437496*sx+tx, 0.036458332*sy+ty,0.39583343*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.58854145*sx+tx, 0.036458332*sy+ty,0.64583313*sx+tx, 0.25520834*sy+ty,0.64583313*sx+tx, 0.359375*sy+ty);
				ctx.bezierCurveTo( 0.65104145*sx+tx, 0.6666667*sy+ty,0.3750001*sx+tx, 0.9322917*sy+ty,0.23958328*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter==':') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2395833283662796;
		glyph.pixels = 0.22573465;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020833*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.12198677*sx+tx, 0.39391094*sy+ty,0.11520721*sx+tx, 0.42203367*sy+ty,0.11062842*sx+tx, 0.4456075*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020833*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.11458334*sx+tx, 0.40625*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.8854167*sy+ty);
				ctx.bezierCurveTo( 0.088541664*sx+tx, 0.88689786*sy+ty,0.088471465*sx+tx, 0.888496*sy+ty,0.08833771*sx+tx, 0.89019674*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020833*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.11458334*sx+tx, 0.40625*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.8854167*sy+ty);
				ctx.bezierCurveTo( 0.088541664*sx+tx, 0.9017823*sy+ty,0.07997103*sx+tx, 0.93243223*sy+ty,0.071806625*sx+tx, 0.95791674*sy+ty);
			} else {
				ctx.moveTo( 0.13020833*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.11458334*sx+tx, 0.40625*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty,0.104166664*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.8854167*sy+ty);
				ctx.bezierCurveTo( 0.088541664*sx+tx, 0.9166667*sy+ty,0.057291668*sx+tx, 1.0*sy+ty,0.057291668*sx+tx, 1.0*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter==';') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2864583432674408;
		glyph.pixels = 0.3788566;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.19270833*sx+tx, 0.36979166*sy+ty);
				ctx.lineTo( 0.16275708*sx+tx, 0.4596454*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.19270833*sx+tx, 0.36979166*sy+ty);
				ctx.lineTo( 0.16145833*sx+tx, 0.46354166*sy+ty);
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.09895833*sx+tx, 0.9010417*sy+ty,0.119791664*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.121630505*sx+tx, 0.9010417*sy+ty,0.123347625*sx+tx, 0.9016909*sy+ty,0.12493943*sx+tx, 0.9028891*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.19270833*sx+tx, 0.36979166*sy+ty);
				ctx.lineTo( 0.16145833*sx+tx, 0.46354166*sy+ty);
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.09895833*sx+tx, 0.9010417*sy+ty,0.119791664*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.13985655*sx+tx, 0.9010417*sy+ty,0.14542782*sx+tx, 0.9783409*sy+ty,0.13185258*sx+tx, 1.0026551*sy+ty);
			} else {
				ctx.moveTo( 0.19270833*sx+tx, 0.36979166*sy+ty);
				ctx.lineTo( 0.16145833*sx+tx, 0.46354166*sy+ty);
				ctx.moveTo( 0.104166664*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.104166664*sx+tx, 0.9791667*sy+ty,0.09895833*sx+tx, 0.9010417*sy+ty,0.119791664*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.9010417*sy+ty,0.14583331*sx+tx, 0.9843751*sy+ty,0.13020833*sx+tx, 1.0052084*sy+ty);
				ctx.bezierCurveTo( 0.11458334*sx+tx, 1.0260416*sy+ty,0.09375*sx+tx, 1.0885416*sy+ty,0.09375*sx+tx, 1.0885416*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='<') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7291666865348816;
		glyph.pixels = 1.2488501;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.64583313*sx+tx, 0.30208334*sy+ty);
				ctx.lineTo( 0.3792398*sx+tx, 0.4645784*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.64583313*sx+tx, 0.30208334*sy+ty);
				ctx.lineTo( 0.11264646*sx+tx, 0.62707347*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.64583313*sx+tx, 0.30208334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.34997773*sx+tx, 0.7926208*sy+ty);
			} else {
				ctx.moveTo( 0.64583313*sx+tx, 0.30208334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.61458313*sx+tx, 0.9583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='=') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.640625;
		glyph.pixels = 0.9454867;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.20749699*sx+tx, 0.5194951*sy+ty,0.3237471*sx+tx, 0.51817536*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.10017309*sx+tx, 0.7447917*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.33654475*sx+tx, 0.7447917*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.57291645*sx+tx, 0.7447917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='>') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7135416865348816;
		glyph.pixels = 1.223671;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.296875*sy+ty);
				ctx.lineTo( 0.35024473*sx+tx, 0.46359664*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.296875*sy+ty);
				ctx.lineTo( 0.60673946*sx+tx, 0.6303183*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.296875*sy+ty);
				ctx.lineTo( 0.61458313*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.37102884*sx+tx, 0.8046229*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.296875*sy+ty);
				ctx.lineTo( 0.61458313*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.119791694*sx+tx, 0.9791667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='?') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.734375;
		glyph.pixels = 1.4146777;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.083333336*sx+tx, 0.1770833*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.1770833*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.39583343*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.40582386*sx+tx, 0.04210103*sy+ty,0.41552448*sx+tx, 0.042933878*sy+ty,0.42493528*sx+tx, 0.044171244*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.083333336*sx+tx, 0.1770833*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.1770833*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.39583343*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.515625*sx+tx, 0.046874996*sy+ty,0.5937498*sx+tx, 0.10937501*sy+ty,0.63020813*sx+tx, 0.2395833*sy+ty);
				ctx.bezierCurveTo( 0.6384047*sx+tx, 0.26885685*sy+ty,0.6394935*sx+tx, 0.29628763*sy+ty,0.6357235*sx+tx, 0.3217573*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.083333336*sx+tx, 0.1770833*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.1770833*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.39583343*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.515625*sx+tx, 0.046874996*sy+ty,0.5937498*sx+tx, 0.10937501*sy+ty,0.63020813*sx+tx, 0.2395833*sy+ty);
				ctx.bezierCurveTo( 0.66666645*sx+tx, 0.36979175*sy+ty,0.56249976*sx+tx, 0.46354178*sy+ty,0.515625*sx+tx, 0.5104167*sy+ty);
				ctx.bezierCurveTo( 0.5006075*sx+tx, 0.5254342*sy+ty,0.46687955*sx+tx, 0.55755836*sy+ty,0.4279713*sx+tx, 0.5942867*sy+ty);
			} else {
				ctx.moveTo( 0.083333336*sx+tx, 0.1770833*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.1770833*sy+ty,0.26041663*sx+tx, 0.026041666*sy+ty,0.39583343*sx+tx, 0.041666664*sy+ty);
				ctx.bezierCurveTo( 0.515625*sx+tx, 0.046874996*sy+ty,0.5937498*sx+tx, 0.10937501*sy+ty,0.63020813*sx+tx, 0.2395833*sy+ty);
				ctx.bezierCurveTo( 0.66666645*sx+tx, 0.36979175*sy+ty,0.56249976*sx+tx, 0.46354178*sy+ty,0.515625*sx+tx, 0.5104167*sy+ty);
				ctx.bezierCurveTo( 0.46875012*sx+tx, 0.5572915*sy+ty,0.23958328*sx+tx, 0.7708332*sy+ty,0.23958328*sx+tx, 0.7708332*sy+ty);
				ctx.moveTo( 0.21354167*sx+tx, 0.8958333*sy+ty);
				ctx.lineTo( 0.18749996*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='@') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.1614583730697632;
		glyph.pixels = 4.11232;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.6979165*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.6979165*sx+tx, 0.46875*sy+ty,0.515625*sx+tx, 0.38020834*sy+ty,0.4270834*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.359375*sx+tx, 0.5104167*sy+ty,0.30729175*sx+tx, 0.7291666*sy+ty,0.38541675*sx+tx, 0.7708333*sy+ty);
				ctx.bezierCurveTo( 0.52083325*sx+tx, 0.8802083*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.5833333*sy+ty,0.692429*sx+tx, 0.58500826*sy+ty,0.69203585*sx+tx, 0.58807814*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.6979165*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.6979165*sx+tx, 0.46875*sy+ty,0.515625*sx+tx, 0.38020834*sy+ty,0.4270834*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.359375*sx+tx, 0.5104167*sy+ty,0.30729175*sx+tx, 0.7291666*sy+ty,0.38541675*sx+tx, 0.7708333*sy+ty);
				ctx.bezierCurveTo( 0.52083325*sx+tx, 0.8802083*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.5833333*sy+ty,0.6458332*sx+tx, 0.8645833*sy+ty,0.9114582*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.9999998*sx+tx, 0.8072917*sy+ty,1.182292*sx+tx, 0.53125*sy+ty,0.9739582*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.9405539*sx+tx, 0.2670701*sy+ty,0.901665*sx+tx, 0.22952436*sy+ty,0.8586105*sx+tx, 0.20021452*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.6979165*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.6979165*sx+tx, 0.46875*sy+ty,0.515625*sx+tx, 0.38020834*sy+ty,0.4270834*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.359375*sx+tx, 0.5104167*sy+ty,0.30729175*sx+tx, 0.7291666*sy+ty,0.38541675*sx+tx, 0.7708333*sy+ty);
				ctx.bezierCurveTo( 0.52083325*sx+tx, 0.8802083*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.5833333*sy+ty,0.6458332*sx+tx, 0.8645833*sy+ty,0.9114582*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.9999998*sx+tx, 0.8072917*sy+ty,1.182292*sx+tx, 0.53125*sy+ty,0.9739582*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.84375*sx+tx, 0.13541667*sy+ty,0.63020813*sx+tx, 0.078124985*sy+ty,0.4114584*sx+tx, 0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.23970585*sx+tx, 0.22688784*sy+ty,0.09042873*sx+tx, 0.42716953*sy+ty,0.09975808*sx+tx, 0.641298*sy+ty);
			} else {
				ctx.moveTo( 0.6979165*sx+tx, 0.46875*sy+ty);
				ctx.bezierCurveTo( 0.6979165*sx+tx, 0.46875*sy+ty,0.515625*sx+tx, 0.38020834*sy+ty,0.4270834*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.359375*sx+tx, 0.5104167*sy+ty,0.30729175*sx+tx, 0.7291666*sy+ty,0.38541675*sx+tx, 0.7708333*sy+ty);
				ctx.bezierCurveTo( 0.52083325*sx+tx, 0.8802083*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty,0.6927082*sx+tx, 0.5833333*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.5833333*sy+ty,0.6458332*sx+tx, 0.8645833*sy+ty,0.9114582*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.9999998*sx+tx, 0.8072917*sy+ty,1.182292*sx+tx, 0.53125*sy+ty,0.9739582*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.84375*sx+tx, 0.13541667*sy+ty,0.63020813*sx+tx, 0.078124985*sy+ty,0.4114584*sx+tx, 0.16145833*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.24479167*sy+ty,0.010416667*sx+tx, 0.546875*sy+ty,0.14583331*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.18749996*sx+tx, 0.9322917*sy+ty,0.33333334*sx+tx, 1.078125*sy+ty,0.46354175*sx+tx, 1.0833334*sy+ty);
				ctx.bezierCurveTo( 0.6562498*sx+tx, 1.1354166*sy+ty,0.9010415*sx+tx, 0.9947917*sy+ty,0.9010415*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='A') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 2.700258;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.31791082*sx+tx, 0.37096113*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.5511904*sx+tx, 0.28318408*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.768739*sx+tx, 0.9222338*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='B') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8229166865348816;
		glyph.pixels = 3.6097527;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10912506*sx+tx, 0.09236711*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.10937502*sx+tx, 0.046875*sy+ty,0.35416675*sx+tx, 0.046875*sy+ty,0.43229175*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.43229175*sx+tx, 0.052083332*sy+ty,0.6406248*sx+tx, 0.072916664*sy+ty,0.6562498*sx+tx, 0.24479167*sy+ty);
				ctx.bezierCurveTo( 0.6562498*sx+tx, 0.34990284*sy+ty,0.5899597*sx+tx, 0.44028285*sy+ty,0.52550954*sx+tx, 0.4694795*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.10937502*sx+tx, 0.046875*sy+ty,0.35416675*sx+tx, 0.046875*sy+ty,0.43229175*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.43229175*sx+tx, 0.052083332*sy+ty,0.6406248*sx+tx, 0.072916664*sy+ty,0.6562498*sx+tx, 0.24479167*sy+ty);
				ctx.bezierCurveTo( 0.6562498*sx+tx, 0.36979166*sy+ty,0.5624998*sx+tx, 0.4739583*sy+ty,0.4895834*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.41666675*sx+tx, 0.484375*sy+ty,0.14583331*sx+tx, 0.484375*sy+ty,0.14583331*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.13020834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.5416665*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.47916666*sy+ty,0.5699366*sx+tx, 0.48805153*sy+ty,0.60484713*sx+tx, 0.50613934*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.10937502*sx+tx, 0.046875*sy+ty,0.35416675*sx+tx, 0.046875*sy+ty,0.43229175*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.43229175*sx+tx, 0.052083332*sy+ty,0.6406248*sx+tx, 0.072916664*sy+ty,0.6562498*sx+tx, 0.24479167*sy+ty);
				ctx.bezierCurveTo( 0.6562498*sx+tx, 0.36979166*sy+ty,0.5624998*sx+tx, 0.4739583*sy+ty,0.4895834*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.41666675*sx+tx, 0.484375*sy+ty,0.14583331*sx+tx, 0.484375*sy+ty,0.14583331*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.13020834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.5416665*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.47916666*sy+ty,0.72395813*sx+tx, 0.5364583*sy+ty,0.7343748*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.7447915*sx+tx, 0.7760417*sy+ty,0.6770832*sx+tx, 0.84374994*sy+ty,0.6302082*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.9427083*sy+ty,0.3958334*sx+tx, 0.9635417*sy+ty,0.10416669*sx+tx, 0.9583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='C') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8854166865348816;
		glyph.pixels = 1.898722;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.4764526*sx+tx, 0.03475518*sy+ty,0.4189012*sx+tx, 0.051430985*sy+ty,0.35899752*sx+tx, 0.0877606*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.57755893*sy+ty,0.099042036*sx+tx, 0.58218646*sy+ty,0.09920775*sx+tx, 0.5867976*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.756316*sy+ty,0.22957784*sx+tx, 0.91666496*sy+ty,0.3885333*sx+tx, 0.9549791*sy+ty);
			} else {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.7864583*sy+ty,0.2760417*sx+tx, 0.96875006*sy+ty,0.46875012*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.66145813*sx+tx, 0.9583333*sy+ty,0.81249976*sx+tx, 0.7864583*sy+ty,0.81249976*sx+tx, 0.7864583*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='D') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.890625;
		glyph.pixels = 2.7413356;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.12500003*sx+tx, 0.30945778*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.12500003*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.046875*sy+ty,0.39062512*sx+tx, 0.020833336*sy+ty,0.5260416*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.53353316*sx+tx, 0.12435173*sy+ty,0.54104507*sx+tx, 0.12923771*sy+ty,0.54854673*sx+tx, 0.13445343*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.12500003*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.046875*sy+ty,0.39062512*sx+tx, 0.020833336*sy+ty,0.5260416*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.6458331*sx+tx, 0.19270834*sy+ty,0.77083313*sx+tx, 0.34895837*sy+ty,0.77604145*sx+tx, 0.6041667*sy+ty);
				ctx.bezierCurveTo( 0.77821094*sx+tx, 0.7104703*sy+ty,0.7496559*sx+tx, 0.7851458*sy+ty,0.7103261*sx+tx, 0.83797985*sy+ty);
			} else {
				ctx.moveTo( 0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.12500003*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.046875*sy+ty,0.39062512*sx+tx, 0.020833336*sy+ty,0.5260416*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.6458331*sx+tx, 0.19270834*sy+ty,0.77083313*sx+tx, 0.34895837*sy+ty,0.77604145*sx+tx, 0.6041667*sy+ty);
				ctx.bezierCurveTo( 0.78124976*sx+tx, 0.859375*sy+ty,0.60937476*sx+tx, 0.9322917*sy+ty,0.53645825*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.46354178*sx+tx, 1.0052084*sy+ty,0.11458335*sx+tx, 0.9635417*sy+ty,0.11458335*sx+tx, 0.9635417*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='E') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 2.8438287;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11396871*sx+tx, 0.09116231*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10598092*sx+tx, 0.80207455*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.65362936*sx+tx, 0.9679025*sy+ty);
			} else {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.484375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='F') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.828125;
		glyph.pixels = 2.1614923;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.7656248*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.22526848*sx+tx, 0.03741189*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.7656248*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10188809*sx+tx, 0.4557197*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.7656248*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.10416668*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.10545986*sx+tx, 0.484375*sy+ty);
			} else {
				ctx.moveTo( 0.7656248*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.10416668*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.484375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='G') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0625;
		glyph.pixels = 2.7551143;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.57291645*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.625*sy+ty,0.83854145*sx+tx, 0.59375*sy+ty,0.94791645*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.98958313*sx+tx, 0.70312506*sy+ty,0.91145813*sx+tx, 0.65625*sy+ty,0.89583313*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.89583313*sx+tx, 0.6302083*sy+ty,0.87139696*sx+tx, 0.7593708*sy+ty,0.8110929*sx+tx, 0.8376458*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.57291645*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.625*sy+ty,0.83854145*sx+tx, 0.59375*sy+ty,0.94791645*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.98958313*sx+tx, 0.70312506*sy+ty,0.91145813*sx+tx, 0.65625*sy+ty,0.89583313*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.89583313*sx+tx, 0.6302083*sy+ty,0.8593748*sx+tx, 0.8229167*sy+ty,0.7656248*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.71354145*sx+tx, 0.921875*sy+ty,0.5104167*sx+tx, 1.0260417*sy+ty,0.3125001*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.27615452*sx+tx, 0.8786752*sy+ty,0.2356818*sx+tx, 0.8416761*sy+ty,0.19994369*sx+tx, 0.7934683*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.57291645*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.625*sy+ty,0.83854145*sx+tx, 0.59375*sy+ty,0.94791645*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.98958313*sx+tx, 0.70312506*sy+ty,0.91145813*sx+tx, 0.65625*sy+ty,0.89583313*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.89583313*sx+tx, 0.6302083*sy+ty,0.8593748*sx+tx, 0.8229167*sy+ty,0.7656248*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.71354145*sx+tx, 0.921875*sy+ty,0.5104167*sx+tx, 1.0260417*sy+ty,0.3125001*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.8177083*sy+ty,-0.015625006*sx+tx, 0.53125*sy+ty,0.19270828*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.23009439*sx+tx, 0.16801177*sy+ty,0.2811724*sx+tx, 0.12856938*sy+ty,0.33795688*sx+tx, 0.10015957*sy+ty);
			} else {
				ctx.moveTo( 0.57291645*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.625*sy+ty,0.83854145*sx+tx, 0.59375*sy+ty,0.94791645*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.98958313*sx+tx, 0.70312506*sy+ty,0.91145813*sx+tx, 0.65625*sy+ty,0.89583313*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.89583313*sx+tx, 0.6302083*sy+ty,0.8593748*sx+tx, 0.8229167*sy+ty,0.7656248*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.71354145*sx+tx, 0.921875*sy+ty,0.5104167*sx+tx, 1.0260417*sy+ty,0.3125001*sx+tx, 0.9010417*sy+ty);
				ctx.bezierCurveTo( 0.17708328*sx+tx, 0.8177083*sy+ty,-0.015625006*sx+tx, 0.53125*sy+ty,0.19270828*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.33854178*sx+tx, 0.020833334*sy+ty,0.69270813*sx+tx, -0.0052083335*sy+ty,0.7812498*sx+tx, 0.125*sy+ty);
				ctx.bezierCurveTo( 0.86458313*sx+tx, 0.234375*sy+ty,0.8906248*sx+tx, 0.30729166*sy+ty,0.8906248*sx+tx, 0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='H') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.96875;
		glyph.pixels = 2.7111824;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.6882123*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.84374976*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.8457414*sx+tx, 0.38683593*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.84374976*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.84895813*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.109375015*sx+tx, 0.546875*sy+ty);
				ctx.lineTo( 0.178455*sx+tx, 0.53665894*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.84374976*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.84895813*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.109375015*sx+tx, 0.546875*sy+ty);
				ctx.lineTo( 0.84895813*sx+tx, 0.4375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='I') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 1.85076;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.29932314*sx+tx, 0.4731002*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30177122*sx+tx, 0.93578374*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.47657058*sx+tx, 0.046360318*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='J') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.71875;
		glyph.pixels = 1.7484431;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2708333*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.052083332*sy+ty);
				ctx.moveTo( 0.53124994*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.020833334*sy+ty,0.53124994*sx+tx, 0.025118547*sy+ty,0.53124684*sx+tx, 0.033062413*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2708333*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.052083332*sy+ty);
				ctx.moveTo( 0.53124994*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.020833334*sy+ty,0.53124994*sx+tx, 0.298802*sy+ty,0.5296453*sx+tx, 0.5274018*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2708333*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.052083332*sy+ty);
				ctx.moveTo( 0.53124994*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.020833334*sy+ty,0.53124994*sx+tx, 0.6302083*sy+ty,0.5260416*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.7864583*sy+ty,0.523629*sx+tx, 0.87572634*sy+ty,0.45476297*sx+tx, 0.92946506*sy+ty);
			} else {
				ctx.moveTo( 0.2708333*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.6458331*sx+tx, 0.052083332*sy+ty);
				ctx.moveTo( 0.53124994*sx+tx, 0.020833334*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.020833334*sy+ty,0.53124994*sx+tx, 0.6302083*sy+ty,0.5260416*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.7864583*sy+ty,0.5208333*sx+tx, 0.9791667*sy+ty,0.30729178*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.15104164*sx+tx, 0.9635417*sy+ty,0.09895834*sx+tx, 0.8489583*sy+ty,0.09895834*sx+tx, 0.7239583*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='K') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.796875;
		glyph.pixels = 2.4999743;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.11651952*sx+tx, 0.6301933*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.69791645*sx+tx, 0.015625*sy+ty);
				ctx.bezierCurveTo( 0.69791645*sx+tx, 0.015625*sy+ty,0.63189113*sx+tx, 0.07116035*sy+ty,0.5448678*sx+tx, 0.1448498*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.69791645*sx+tx, 0.015625*sy+ty);
				ctx.bezierCurveTo( 0.69791645*sx+tx, 0.015625*sy+ty,0.140625*sx+tx, 0.48437503*sy+ty,0.13020834*sx+tx, 0.5052083*sy+ty);
				ctx.lineTo( 0.23905656*sx+tx, 0.5913404*sy+ty);
			} else {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.69791645*sx+tx, 0.015625*sy+ty);
				ctx.bezierCurveTo( 0.69791645*sx+tx, 0.015625*sy+ty,0.140625*sx+tx, 0.48437503*sy+ty,0.13020834*sx+tx, 0.5052083*sy+ty);
				ctx.lineTo( 0.72916645*sx+tx, 0.9791667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='L') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7135416865348816;
		glyph.pixels = 1.4864389;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.119791694*sx+tx, 0.37681806*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.119791694*sx+tx, 0.74842775*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.119791694*sx+tx, 0.9583334*sy+ty);
				ctx.lineTo( 0.28086993*sx+tx, 0.9441206*sy+ty);
			} else {
				ctx.moveTo( 0.119791694*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.119791694*sx+tx, 0.9583334*sy+ty);
				ctx.lineTo( 0.65104145*sx+tx, 0.9114584*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='M') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.265625;
		glyph.pixels = 3.8615308;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416668*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.3275405*sx+tx, 0.05039859*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416668*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.3333335*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.3333335*sx+tx, 0.026041666*sy+ty,0.5919432*sx+tx, 0.84750736*sy+ty,0.62383556*sx+tx, 0.9494021*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416668*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.3333335*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.3333335*sx+tx, 0.026041666*sy+ty,0.5989586*sx+tx, 0.8697916*sy+ty,0.625*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.94181025*sx+tx, 0.054629505*sy+ty);
			} else {
				ctx.moveTo( 0.10416668*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.3333335*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.3333335*sx+tx, 0.026041666*sy+ty,0.5989586*sx+tx, 0.8697916*sy+ty,0.625*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.942708*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 1.161459*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='N') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 3.0976725;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.100069284*sx+tx, 0.22038442*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.41552758*sx+tx, 0.47773334*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.75520813*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.75520813*sx+tx, 0.9791667*sy+ty,0.75520813*sx+tx, 0.95148647*sy+ty,0.755238*sx+tx, 0.9054647*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.75520813*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.75520813*sx+tx, 0.9791667*sy+ty,0.7552082*sx+tx, 0.114583336*sy+ty,0.7604165*sx+tx, 0.015625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='O') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.4118998;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.4340605*sx+tx, 0.041666668*sy+ty,0.15694956*sx+tx, 0.11543245*sy+ty,0.09830699*sx+tx, 0.49233425*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.41205326*sx+tx, 0.9531911*sy+ty,0.41265562*sx+tx, 0.9532532*sy+ty,0.4132654*sx+tx, 0.95331126*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.79742515*sx+tx, 0.52953875*sy+ty,0.797583*sx+tx, 0.5126678*sy+ty,0.79736435*sx+tx, 0.496259*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='P') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7916666865348816;
		glyph.pixels = 2.37591;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 1.0104167*sy+ty);
				ctx.lineTo( 0.10102399*sx+tx, 0.41644752*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 1.0104167*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.026041668*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.026041668*sy+ty,0.15321195*sx+tx, 0.026041668*sy+ty,0.2290632*sx+tx, 0.033469852*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 1.0104167*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.026041668*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.026041668*sy+ty,0.7031249*sx+tx, 0.026041668*sy+ty,0.6979165*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.6968782*sx+tx, 0.35711336*sy+ty,0.6815576*sx+tx, 0.40282884*sy+ty,0.6570715*sx+tx, 0.44075656*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 1.0104167*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.026041668*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.026041668*sy+ty,0.7031249*sx+tx, 0.026041668*sy+ty,0.6979165*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.69270813*sx+tx, 0.57812494*sy+ty,0.3281251*sx+tx, 0.6197916*sy+ty,0.24999996*sx+tx, 0.6197916*sy+ty);
				ctx.bezierCurveTo( 0.24999996*sx+tx, 0.6197916*sy+ty,0.13020834*sx+tx, 0.6197916*sy+ty,0.13020834*sx+tx, 0.62499994*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Q') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 3.090014;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5052084*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.041666668*sy+ty,0.10416669*sx+tx, 0.25*sy+ty,0.09895834*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09793025*sx+tx, 0.57120955*sy+ty,0.10461377*sx+tx, 0.61935353*sy+ty,0.11724633*sx+tx, 0.6641836*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5052084*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.041666668*sy+ty,0.10416669*sx+tx, 0.25*sy+ty,0.09895834*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7760417*sy+ty,0.28645837*sx+tx, 0.9739583*sy+ty,0.44791675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.5562908*sx+tx, 0.9739583*sy+ty,0.6553466*sx+tx, 0.9238724*sy+ty,0.73186404*sx+tx, 0.8528948*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5052084*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.041666668*sy+ty,0.10416669*sx+tx, 0.25*sy+ty,0.09895834*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7760417*sy+ty,0.28645837*sx+tx, 0.9739583*sy+ty,0.44791675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.67708313*sx+tx, 0.9739583*sy+ty,0.8645832*sx+tx, 0.75*sy+ty,0.8854165*sx+tx, 0.578125*sy+ty);
				ctx.bezierCurveTo( 0.9155709*sx+tx, 0.44410545*sy+ty,0.8832202*sx+tx, 0.26482356*sy+ty,0.77172613*sx+tx, 0.1525877*sy+ty);
			} else {
				ctx.moveTo( 0.5052084*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.29166675*sx+tx, 0.041666668*sy+ty,0.10416669*sx+tx, 0.25*sy+ty,0.09895834*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7760417*sy+ty,0.28645837*sx+tx, 0.9739583*sy+ty,0.44791675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.67708313*sx+tx, 0.9739583*sy+ty,0.8645832*sx+tx, 0.75*sy+ty,0.8854165*sx+tx, 0.578125*sy+ty);
				ctx.bezierCurveTo( 0.9322915*sx+tx, 0.36979166*sy+ty,0.8281248*sx+tx, 0.052083332*sy+ty,0.5104167*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.5208333*sx+tx, 0.041666668*sy+ty,0.5572915*sx+tx, 0.052083332*sy+ty,0.5052084*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.5312499*sx+tx, 0.734375*sy+ty);
				ctx.lineTo( 0.92708313*sx+tx, 1.0052084*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='R') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8802083134651184;
		glyph.pixels = 3.1197684;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.21484959*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.026041666*sy+ty,0.550702*sx+tx, 0.0064567793*sy+ty,0.6726864*sx+tx, 0.19827326*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.026041666*sy+ty,0.6979165*sx+tx, 0.0*sy+ty,0.7031248*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.7082783*sx+tx, 0.6009841*sy+ty,0.23411012*sx+tx, 0.5786373*sy+ty,0.13834599*sx+tx, 0.61849684*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.026041666*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.026041666*sy+ty,0.6979165*sx+tx, 0.0*sy+ty,0.7031248*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.70833313*sx+tx, 0.6041667*sy+ty,0.2239583*sx+tx, 0.578125*sy+ty,0.13541667*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.4843751*sx+tx, 0.5572917*sy+ty,0.6354165*sx+tx, 0.765625*sy+ty,0.6874998*sx+tx, 0.8854167*sy+ty);
				ctx.bezierCurveTo( 0.70833313*sx+tx, 0.9375*sy+ty,0.7447915*sx+tx, 0.9739583*sy+ty,0.7499998*sx+tx, 1.0*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='S') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8229166865348816;
		glyph.pixels = 2.3338459;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.7291667*sx+tx, 0.23958322*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.16666667*sy+ty,0.640625*sx+tx, 0.03645834*sy+ty,0.45833334*sx+tx, 0.04166667*sy+ty);
				ctx.bezierCurveTo( 0.3439843*sx+tx, 0.04493379*sy+ty,0.2501294*sx+tx, 0.078942165*sy+ty,0.19605224*sx+tx, 0.1334072*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.7291667*sx+tx, 0.23958322*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.16666667*sy+ty,0.640625*sx+tx, 0.03645834*sy+ty,0.45833334*sx+tx, 0.04166667*sy+ty);
				ctx.bezierCurveTo( 0.27604172*sx+tx, 0.046875004*sy+ty,0.14583333*sx+tx, 0.13020834*sy+ty,0.14583333*sx+tx, 0.24999988*sy+ty);
				ctx.bezierCurveTo( 0.14583333*sx+tx, 0.36979192*sy+ty,0.34375006*sx+tx, 0.42708334*sy+ty,0.46354175*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.4777459*sx+tx, 0.48449323*sy+ty,0.49248806*sx+tx, 0.49055955*sy+ty,0.50740147*sx+tx, 0.49730453*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.7291667*sx+tx, 0.23958322*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.16666667*sy+ty,0.640625*sx+tx, 0.03645834*sy+ty,0.45833334*sx+tx, 0.04166667*sy+ty);
				ctx.bezierCurveTo( 0.27604172*sx+tx, 0.046875004*sy+ty,0.14583333*sx+tx, 0.13020834*sy+ty,0.14583333*sx+tx, 0.24999988*sy+ty);
				ctx.bezierCurveTo( 0.14583333*sx+tx, 0.36979192*sy+ty,0.34375006*sx+tx, 0.42708334*sy+ty,0.46354175*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.5260417*sy+ty,0.7552084*sx+tx, 0.63020784*sy+ty,0.7135417*sx+tx, 0.74999946*sy+ty);
				ctx.bezierCurveTo( 0.68674004*sx+tx, 0.82705426*sy+ty,0.6319235*sx+tx, 0.89333415*sy+ty,0.5546368*sx+tx, 0.93081874*sy+ty);
			} else {
				ctx.moveTo( 0.7291667*sx+tx, 0.23958322*sy+ty);
				ctx.bezierCurveTo( 0.6927082*sx+tx, 0.16666667*sy+ty,0.640625*sx+tx, 0.03645834*sy+ty,0.45833334*sx+tx, 0.04166667*sy+ty);
				ctx.bezierCurveTo( 0.27604172*sx+tx, 0.046875004*sy+ty,0.14583333*sx+tx, 0.13020834*sy+ty,0.14583333*sx+tx, 0.24999988*sy+ty);
				ctx.bezierCurveTo( 0.14583333*sx+tx, 0.36979192*sy+ty,0.34375006*sx+tx, 0.42708334*sy+ty,0.46354175*sx+tx, 0.47916666*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.5260417*sy+ty,0.7552084*sx+tx, 0.63020784*sy+ty,0.7135417*sx+tx, 0.74999946*sy+ty);
				ctx.bezierCurveTo( 0.671875*sx+tx, 0.8697911*sy+ty,0.5625*sx+tx, 0.9635411*sy+ty,0.40625*sx+tx, 0.9635411*sy+ty);
				ctx.bezierCurveTo( 0.25*sx+tx, 0.9635411*sy+ty,0.10937502*sx+tx, 0.8072917*sy+ty,0.140625*sx+tx, 0.828125*sy+ty);
				ctx.bezierCurveTo( 0.21354163*sx+tx, 0.89583385*sy+ty,0.08854167*sx+tx, 0.7708328*sy+ty,0.08854167*sx+tx, 0.7708328*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='T') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7864583134651184;
		glyph.pixels = 1.7187638;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( -0.015625006*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.41406596*sx+tx, 0.041666668*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( -0.015625006*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.7343748*sx+tx, 0.041666668*sy+ty);
				ctx.moveTo( 0.35416678*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.35357872*sx+tx, 0.13542219*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( -0.015625006*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.7343748*sx+tx, 0.041666668*sy+ty);
				ctx.moveTo( 0.35416678*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.35126856*sx+tx, 0.565107*sy+ty);
			} else {
				ctx.moveTo( -0.015625006*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.7343748*sx+tx, 0.041666668*sy+ty);
				ctx.moveTo( 0.35416678*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.34895843*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='U') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.1464481;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.096460685*sx+tx, 0.4447967*sy+ty,0.13588448*sx+tx, 0.68435985*sy+ty,0.19827501*sx+tx, 0.815225*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09396302*sx+tx, 0.8843851*sy+ty,0.25665352*sx+tx, 0.9634609*sy+ty,0.43539226*sx+tx, 0.97305363*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.5448275*sx+tx, 0.97657937*sy+ty,0.6365977*sx+tx, 0.9686483*sy+ty,0.7064291*sx+tx, 0.8472799*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='V') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7916666865348816;
		glyph.pixels = 2.0678914;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.010416669*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.17311046*sx+tx, 0.50633025*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.010416669*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.33333343*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.33612025*sx+tx, 0.9822434*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.010416669*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.33333343*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.51962256*sx+tx, 0.49893415*sy+ty);
			} else {
				ctx.moveTo( 0.010416669*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.33333343*sx+tx, 0.9895833*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.015625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='W') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.3125;
		glyph.pixels = 4.0940924;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.02604167*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.32631773*sx+tx, 0.9889021*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.02604167*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.32812512*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6145831*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.6164385*sx+tx, 0.03294394*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.02604167*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.32812512*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6145831*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.87499976*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.8842759*sx+tx, 0.9688731*sy+ty);
			} else {
				ctx.moveTo( 0.02604167*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.32812512*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6145831*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.87499976*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 1.2291671*sx+tx, 0.0052083335*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='X') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9739583134651184;
		glyph.pixels = 2.5408945;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.08854167*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.48920822*sx+tx, 0.50855035*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.08854167*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.8802082*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.8802082*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.8702755*sx+tx, 0.022087583*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.08854167*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.8802082*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.8802082*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.45857528*sx+tx, 0.5058354*sy+ty);
			} else {
				ctx.moveTo( 0.08854167*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.8802082*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.8802082*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.046874996*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Y') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 1.961545;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.015625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.26605946*sx+tx, 0.43203405*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.015625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.42708343*sx+tx, 0.703125*sy+ty);
				ctx.moveTo( 0.81770813*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.72856086*sx+tx, 0.15589088*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.015625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.42708343*sx+tx, 0.703125*sy+ty);
				ctx.moveTo( 0.81770813*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.47886378*sx+tx, 0.5779454*sy+ty);
			} else {
				ctx.moveTo( 0.015625*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.42708343*sx+tx, 0.703125*sy+ty);
				ctx.moveTo( 0.81770813*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.22916663*sx+tx, 1.0*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Z') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 2.6340616;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.7522654*sx+tx, 0.052083332*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.8229164*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.8229164*sx+tx, 0.052083332*sy+ty,0.6293924*sx+tx, 0.28272164*sy+ty,0.4389387*sx+tx, 0.51062596*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.8229164*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.8229164*sx+tx, 0.052083332*sy+ty,0.062499993*sx+tx, 0.9583334*sy+ty,0.072916664*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.15400057*sx+tx, 0.953696*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.052083332*sy+ty);
				ctx.lineTo( 0.8229164*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.8229164*sx+tx, 0.052083332*sy+ty,0.062499993*sx+tx, 0.9583334*sy+ty,0.072916664*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.81249976*sx+tx, 0.9583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='[') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4166666567325592;
		glyph.pixels = 1.7397143;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.34895843*sx+tx, -0.13541667*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.110165656*sx+tx, 0.05466196*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.34895843*sx+tx, -0.13541667*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.11192649*sx+tx, 0.4895869*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.34895843*sx+tx, -0.13541667*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.11368732*sx+tx, 0.9245119*sy+ty);
			} else {
				ctx.moveTo( 0.34895843*sx+tx, -0.13541667*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 1.1458334*sy+ty);
				ctx.lineTo( 0.3281251*sx+tx, 1.1510416*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='\\') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 1.1382799;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.23437497*sx+tx, 0.24739583*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.3749999*sx+tx, 0.49479166*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.5156249*sx+tx, 0.7421875*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter==']') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4270833432674408;
		glyph.pixels = 1.6842415;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.072101936*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.49316233*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.91422284*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 1.140625*sy+ty);
				ctx.bezierCurveTo( 0.30208343*sx+tx, 1.140625*sy+ty,0.15104166*sx+tx, 1.1354166*sy+ty,0.09895834*sx+tx, 1.140625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='^') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8541666865348816;
		glyph.pixels = 1.4477808;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.083333336*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.25031778*sx+tx, 0.39241782*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.083333336*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.41730222*sx+tx, 0.07129395*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.083333336*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.42187506*sx+tx, 0.0625*sy+ty);
				ctx.bezierCurveTo( 0.42187506*sx+tx, 0.0625*sy+ty,0.5003783*sx+tx, 0.20937711*sy+ty,0.5818443*sx+tx, 0.36266336*sy+ty);
			} else {
				ctx.moveTo( 0.083333336*sx+tx, 0.7135417*sy+ty);
				ctx.lineTo( 0.42187506*sx+tx, 0.0625*sy+ty);
				ctx.bezierCurveTo( 0.42187506*sx+tx, 0.0625*sy+ty,0.74479157*sx+tx, 0.6666667*sy+ty,0.76041657*sx+tx, 0.703125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='_') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7604166865348816;
		glyph.pixels = 0.77604127;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( -0.010416653*sx+tx, 1.0416667*sy+ty);
				ctx.lineTo( 0.18359366*sx+tx, 1.0416667*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( -0.010416653*sx+tx, 1.0416667*sy+ty);
				ctx.lineTo( 0.37760398*sx+tx, 1.0416667*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( -0.010416653*sx+tx, 1.0416667*sy+ty);
				ctx.lineTo( 0.5716143*sx+tx, 1.0416666*sy+ty);
			} else {
				ctx.moveTo( -0.010416653*sx+tx, 1.0416667*sy+ty);
				ctx.lineTo( 0.76562464*sx+tx, 1.0416666*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='a') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.0325174;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.20961496*sx+tx, 0.31583285*sy+ty,0.14026642*sx+tx, 0.4195896*sy+ty,0.112146914*sx+tx, 0.49148932*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.15162939*sx+tx, 0.9051996*sy+ty,0.18895769*sx+tx, 0.922849*sy+ty,0.23003106*sx+tx, 0.9342051*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.56304884*sx+tx, 0.34844342*sy+ty,0.56383175*sx+tx, 0.39029726*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='b') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.71875;
		glyph.pixels = 2.4291942;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.10741423*sx+tx, 0.62812316*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.5416667*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5416667*sy+ty,0.20688349*sx+tx, 0.40339237*sy+ty,0.27530184*sx+tx, 0.36361688*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.5416667*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5416667*sy+ty,0.23437496*sx+tx, 0.36979166*sy+ty,0.2968751*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.33854166*sy+ty,0.5416665*sx+tx, 0.31770834*sy+ty,0.5937498*sx+tx, 0.421875*sy+ty);
				ctx.bezierCurveTo( 0.6269669*sx+tx, 0.47502232*sy+ty,0.63899916*sx+tx, 0.591724*sy+ty,0.62444234*sx+tx, 0.69631857*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.10937502*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.5416667*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5416667*sy+ty,0.23437496*sx+tx, 0.36979166*sy+ty,0.2968751*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.33854166*sy+ty,0.5416665*sx+tx, 0.31770834*sy+ty,0.5937498*sx+tx, 0.421875*sy+ty);
				ctx.bezierCurveTo( 0.6458332*sx+tx, 0.5052083*sy+ty,0.64583313*sx+tx, 0.7447917*sy+ty,0.5729165*sx+tx, 0.8489583*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.9114583*sy+ty,0.3437501*sx+tx, 0.9947917*sy+ty,0.11458336*sx+tx, 0.9270833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='c') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 1.4316267;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.36544785*sx+tx, 0.30060026*sy+ty,0.3256308*sx+tx, 0.3092526*sy+ty,0.2870021*sx+tx, 0.32876188*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27735108*sx+tx, 0.29692957*sy+ty,0.102662645*sx+tx, 0.41417065*sy+ty,0.10406275*sx+tx, 0.6840848*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27604166*sx+tx, 0.296875*sy+ty,0.09895834*sx+tx, 0.41666666*sy+ty,0.10416668*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.10416668*sx+tx, 0.8012329*sy+ty,0.20547263*sx+tx, 0.9278478*sy+ty,0.32666495*sx+tx, 0.9609778*sy+ty);
			} else {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27604166*sx+tx, 0.296875*sy+ty,0.09895834*sx+tx, 0.41666666*sy+ty,0.10416668*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.10416668*sx+tx, 0.8229167*sy+ty,0.24999993*sx+tx, 0.9791667*sy+ty,0.4010418*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.4947918*sx+tx, 0.96875*sy+ty,0.5729164*sx+tx, 0.8802083*sy+ty,0.661458*sx+tx, 0.8229167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='d') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 2.445283;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5937498*sx+tx, 0.390625*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.390625*sy+ty,0.44791675*sx+tx, 0.3125*sy+ty,0.3645834*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.24485856*sx+tx, 0.3125*sy+ty,0.13215455*sx+tx, 0.39323854*sy+ty,0.08987329*sx+tx, 0.543188*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5937498*sx+tx, 0.390625*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.390625*sy+ty,0.44791675*sx+tx, 0.3125*sy+ty,0.3645834*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.3125*sy+ty,0.083333336*sx+tx, 0.43229166*sy+ty,0.072916664*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.8229167*sy+ty,0.19791663*sx+tx, 0.9791667*sy+ty,0.30729175*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.34164426*sx+tx, 0.9791667*sy+ty,0.3765105*sx+tx, 0.9714599*sy+ty,0.4105996*sx+tx, 0.9579829*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5937498*sx+tx, 0.390625*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.390625*sy+ty,0.44791675*sx+tx, 0.3125*sy+ty,0.3645834*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.3125*sy+ty,0.083333336*sx+tx, 0.43229166*sy+ty,0.072916664*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.8229167*sy+ty,0.19791663*sx+tx, 0.9791667*sy+ty,0.30729175*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.41666675*sx+tx, 0.9791667*sy+ty,0.5312499*sx+tx, 0.9010417*sy+ty,0.6093748*sx+tx, 0.8072917*sy+ty);
				ctx.moveTo( 0.6093748*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.60340106*sx+tx, 0.38354722*sy+ty);
			} else {
				ctx.moveTo( 0.5937498*sx+tx, 0.390625*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.390625*sy+ty,0.44791675*sx+tx, 0.3125*sy+ty,0.3645834*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.21874996*sx+tx, 0.3125*sy+ty,0.083333336*sx+tx, 0.43229166*sy+ty,0.072916664*sx+tx, 0.6510417*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.8229167*sy+ty,0.19791663*sx+tx, 0.9791667*sy+ty,0.30729175*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.41666675*sx+tx, 0.9791667*sy+ty,0.5312499*sx+tx, 0.9010417*sy+ty,0.6093748*sx+tx, 0.8072917*sy+ty);
				ctx.moveTo( 0.6093748*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='e') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7083333134651184;
		glyph.pixels = 2.0929031;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.4893269*sx+tx, 0.5323722*sy+ty,0.5979283*sx+tx, 0.46273005*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.24495074*sx+tx, 0.322857*sy+ty,0.20985223*sx+tx, 0.34462476*sy+ty,0.18083331*sx+tx, 0.37065887*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.11194774*sx+tx, 0.79156196*sy+ty,0.12780146*sx+tx, 0.81782174*sy+ty,0.14955701*sx+tx, 0.84223354*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.92708343*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='f') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5104166865348816;
		glyph.pixels = 1.5545903;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.46354172*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.46354172*sx+tx, 0.046875*sy+ty,0.26562503*sx+tx, 0.046875004*sy+ty,0.23437499*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.21704304*sx+tx, 0.091278285*sy+ty,0.2037053*sx+tx, 0.120855846*sy+ty,0.19349428*sx+tx, 0.16778836*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.46354172*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.46354172*sx+tx, 0.046875*sy+ty,0.26562503*sx+tx, 0.046875004*sy+ty,0.23437499*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.17499591*sx+tx, 0.09791731*sy+ty,0.16249828*sx+tx, 0.42233616*sy+ty,0.16199988*sx+tx, 0.68034476*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.46354172*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.46354172*sx+tx, 0.046875*sy+ty,0.26562503*sx+tx, 0.046875004*sy+ty,0.23437499*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.13541666*sx+tx, 0.104166664*sy+ty,0.16666664*sx+tx, 0.9947917*sy+ty,0.16666664*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.010416666*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.03322748*sx+tx, 0.30729166*sy+ty);
			} else {
				ctx.moveTo( 0.46354172*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.46354172*sx+tx, 0.046875*sy+ty,0.26562503*sx+tx, 0.046875004*sy+ty,0.23437499*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.13541666*sx+tx, 0.104166664*sy+ty,0.16666664*sx+tx, 0.9947917*sy+ty,0.16666664*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.010416666*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.42187506*sx+tx, 0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='g') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6979166865348816;
		glyph.pixels = 2.7636192;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.59895813*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.38020834*sy+ty,0.46875006*sx+tx, 0.28645834*sy+ty,0.3593751*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.33854166*sy+ty,0.078125*sx+tx, 0.4739583*sy+ty,0.078125*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.07806525*sx+tx, 0.6591181*sy+ty,0.07803086*sx+tx, 0.66196287*sy+ty,0.0780214*sx+tx, 0.6647845*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.59895813*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.38020834*sy+ty,0.46875006*sx+tx, 0.28645834*sy+ty,0.3593751*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.33854166*sy+ty,0.078125*sx+tx, 0.4739583*sy+ty,0.078125*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.90625*sy+ty,0.26041666*sx+tx, 0.9791667*sy+ty,0.35416675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.44713598*sx+tx, 0.9739583*sy+ty,0.536968*sx+tx, 0.8641609*sy+ty,0.57253534*sx+tx, 0.81499165*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.59895813*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.38020834*sy+ty,0.46875006*sx+tx, 0.28645834*sy+ty,0.3593751*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.33854166*sy+ty,0.078125*sx+tx, 0.4739583*sy+ty,0.078125*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.90625*sy+ty,0.26041666*sx+tx, 0.9791667*sy+ty,0.35416675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.47395843*sx+tx, 0.9739583*sy+ty,0.58854145*sx+tx, 0.7916667*sy+ty,0.58854145*sx+tx, 0.7916667*sy+ty);
				ctx.moveTo( 0.5937498*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.25*sy+ty,0.5974432*sx+tx, 0.73014563*sy+ty,0.5954994*sx+tx, 0.9284261*sy+ty);
			} else {
				ctx.moveTo( 0.59895813*sx+tx, 0.38020834*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.38020834*sy+ty,0.46875006*sx+tx, 0.28645834*sy+ty,0.3593751*sx+tx, 0.3125*sy+ty);
				ctx.bezierCurveTo( 0.24999997*sx+tx, 0.33854166*sy+ty,0.078125*sx+tx, 0.4739583*sy+ty,0.078125*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.90625*sy+ty,0.26041666*sx+tx, 0.9791667*sy+ty,0.35416675*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.47395843*sx+tx, 0.9739583*sy+ty,0.58854145*sx+tx, 0.7916667*sy+ty,0.58854145*sx+tx, 0.7916667*sy+ty);
				ctx.moveTo( 0.5937498*sx+tx, 0.25*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.25*sy+ty,0.5989582*sx+tx, 0.9270834*sy+ty,0.5937498*sx+tx, 1.0052084*sy+ty);
				ctx.bezierCurveTo( 0.58854145*sx+tx, 1.0833334*sy+ty,0.5312499*sx+tx, 1.2291666*sy+ty,0.4062501*sx+tx, 1.234375*sy+ty);
				ctx.bezierCurveTo( 0.28125006*sx+tx, 1.2395834*sy+ty,0.13541667*sx+tx, 1.21875*sy+ty,0.13541667*sx+tx, 1.21875*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='h') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6979166865348816;
		glyph.pixels = 2.2299914;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.078125*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.078125*sx+tx, 0.56791455*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.078125*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.078125*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.6770833*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.6770833*sy+ty,0.09160923*sx+tx, 0.6496286*sy+ty,0.119213015*sx+tx, 0.6107606*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.078125*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.078125*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.6770833*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.6770833*sy+ty,0.2395833*sx+tx, 0.43229166*sy+ty,0.3125001*sx+tx, 0.36979166*sy+ty);
				ctx.bezierCurveTo( 0.36979178*sx+tx, 0.3177083*sy+ty,0.45833343*sx+tx, 0.28645834*sy+ty,0.5468748*sx+tx, 0.36979166*sy+ty);
				ctx.bezierCurveTo( 0.5533154*sx+tx, 0.3789158*sy+ty,0.55892634*sx+tx, 0.39274105*sy+ty,0.5638102*sx+tx, 0.41020173*sy+ty);
			} else {
				ctx.moveTo( 0.078125*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.078125*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.6770833*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 0.6770833*sy+ty,0.2395833*sx+tx, 0.43229166*sy+ty,0.3125001*sx+tx, 0.36979166*sy+ty);
				ctx.bezierCurveTo( 0.36979178*sx+tx, 0.3177083*sy+ty,0.45833343*sx+tx, 0.28645834*sy+ty,0.5468748*sx+tx, 0.36979166*sy+ty);
				ctx.bezierCurveTo( 0.6093748*sx+tx, 0.4583333*sy+ty,0.5937498*sx+tx, 0.9895833*sy+ty,0.5937498*sx+tx, 0.9895833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='i') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2447916716337204;
		glyph.pixels = 1.5151793;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12860417*sx+tx, 0.47294015*sy+ty,0.12717639*sx+tx, 0.65386677*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0067682*sy+ty,0.12505694*sx+tx, 1.0085596*sy+ty,0.12515883*sx+tx, 1.0021638*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0672139*sy+ty,0.12708068*sx+tx, 0.76719964*sy+ty,0.12861183*sx+tx, 0.53661287*sy+ty);
			} else {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.13020836*sx+tx, 0.015625*sy+ty);
				ctx.lineTo( 0.13020836*sx+tx, 0.13020833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='j') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4427083432674408;
		glyph.pixels = 1.4289256;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.3177084*sx+tx, 0.2708333*sy+ty);
				ctx.lineTo( 0.3177084*sx+tx, 0.62806475*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.3177084*sx+tx, 0.2708333*sy+ty);
				ctx.lineTo( 0.3177084*sx+tx, 0.98529613*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.3177084*sx+tx, 0.2708333*sy+ty);
				ctx.lineTo( 0.3177084*sx+tx, 1.0208334*sy+ty);
				ctx.bezierCurveTo( 0.3177084*sx+tx, 1.0885416*sy+ty,0.28125003*sx+tx, 1.1979167*sy+ty,0.24999996*sx+tx, 1.2083334*sy+ty);
				ctx.bezierCurveTo( 0.21661745*sx+tx, 1.2342975*sy+ty,0.16738585*sx+tx, 1.2417711*sy+ty,0.12864171*sx+tx, 1.2251104*sy+ty);
			} else {
				ctx.moveTo( 0.3177084*sx+tx, 0.2708333*sy+ty);
				ctx.lineTo( 0.3177084*sx+tx, 1.0208334*sy+ty);
				ctx.bezierCurveTo( 0.3177084*sx+tx, 1.0885416*sy+ty,0.28125003*sx+tx, 1.1979167*sy+ty,0.24999996*sx+tx, 1.2083334*sy+ty);
				ctx.bezierCurveTo( 0.20312496*sx+tx, 1.2447916*sy+ty,0.12500001*sx+tx, 1.2447917*sy+ty,0.08854167*sx+tx, 1.1927084*sy+ty);
				ctx.bezierCurveTo( 0.052083336*sx+tx, 1.140625*sy+ty,-0.015625006*sx+tx, 1.0208334*sy+ty,-0.015625006*sx+tx, 1.0208334*sy+ty);
				ctx.moveTo( 0.32291675*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.32291675*sx+tx, 0.010416667*sy+ty,0.31770843*sx+tx, 0.114583336*sy+ty,0.31770843*sx+tx, 0.119791664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='k') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.71875;
		glyph.pixels = 2.1896296;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.09948172*sx+tx, 0.55258566*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.5572915*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.54997665*sx+tx, 0.27781647*sy+ty,0.53543234*sx+tx, 0.29634956*sy+ty,0.51573676*sx+tx, 0.3190273*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.5572915*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.5104167*sx+tx, 0.34375*sy+ty,0.16666663*sx+tx, 0.6822917*sy+ty,0.072916664*sx+tx, 0.703125*sy+ty);
				ctx.moveTo( 0.2864584*sx+tx, 0.5364583*sy+ty);
				ctx.lineTo( 0.2905247*sx+tx, 0.54185885*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.0052083335*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.5572915*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.5104167*sx+tx, 0.34375*sy+ty,0.16666663*sx+tx, 0.6822917*sy+ty,0.072916664*sx+tx, 0.703125*sy+ty);
				ctx.moveTo( 0.2864584*sx+tx, 0.5364583*sy+ty);
				ctx.lineTo( 0.6197915*sx+tx, 0.9791667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='l') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.203125;
		glyph.pixels = 0.9895833;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.09895835*sx+tx, 0.2578125*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.09895835*sx+tx, 0.5052083*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.09895835*sx+tx, 0.7526042*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.09895835*sx+tx, 1.0*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='m') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.1302083730697632;
		glyph.pixels = 3.1240954;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6197917*sy+ty,0.09926106*sx+tx, 0.61229056*sy+ty,0.10868221*sx+tx, 0.599729*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6197917*sy+ty,0.28125*sx+tx, 0.3645833*sy+ty,0.33854175*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.4062501*sx+tx, 0.29166666*sy+ty,0.52604157*sx+tx, 0.296875*sy+ty,0.55208313*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.57392913*sx+tx, 0.36038765*sy+ty,0.5851732*sx+tx, 0.46402052*sy+ty,0.5907241*sx+tx, 0.5838913*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6197917*sy+ty,0.28125*sx+tx, 0.3645833*sy+ty,0.33854175*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.4062501*sx+tx, 0.29166666*sy+ty,0.52604157*sx+tx, 0.296875*sy+ty,0.55208313*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.6093748*sx+tx, 0.39583334*sy+ty,0.5937498*sx+tx, 1.015625*sy+ty,0.5937498*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.5781248*sx+tx, 0.484375*sy+ty);
				ctx.bezierCurveTo( 0.5781248*sx+tx, 0.484375*sy+ty,0.77083313*sx+tx, 0.31770834*sy+ty,0.8124998*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.827658*sx+tx, 0.29639903*sy+ty,0.85108787*sx+tx, 0.2900254*sy+ty,0.87677103*sx+tx, 0.28822863*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.6197917*sy+ty,0.28125*sx+tx, 0.3645833*sy+ty,0.33854175*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.4062501*sx+tx, 0.29166666*sy+ty,0.52604157*sx+tx, 0.296875*sy+ty,0.55208313*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.6093748*sx+tx, 0.39583334*sy+ty,0.5937498*sx+tx, 1.015625*sy+ty,0.5937498*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.5781248*sx+tx, 0.484375*sy+ty);
				ctx.bezierCurveTo( 0.5781248*sx+tx, 0.484375*sy+ty,0.77083313*sx+tx, 0.31770834*sy+ty,0.8124998*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.8541665*sx+tx, 0.28645834*sy+ty,0.95833313*sx+tx, 0.26562503*sy+ty,0.9999998*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 1.041667*sx+tx, 0.43229166*sy+ty,1.0260417*sx+tx, 1.0052084*sy+ty,1.0260417*sx+tx, 1.0052084*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='n') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7552083134651184;
		glyph.pixels = 2.0940912;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.7943561*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.14475031*sx+tx, 0.6175197*sy+ty,0.2193212*sx+tx, 0.49409378*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.17708328*sx+tx, 0.5104167*sy+ty,0.28645837*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.38020843*sx+tx, 0.31770834*sy+ty,0.46354195*sx+tx, 0.296875*sy+ty,0.5677083*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.5877123*sx+tx, 0.35264066*sy+ty,0.601788*sx+tx, 0.38761577*sy+ty,0.6115545*sx+tx, 0.43713894*sy+ty);
			} else {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.17708328*sx+tx, 0.5104167*sy+ty,0.28645837*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.38020843*sx+tx, 0.31770834*sy+ty,0.46354195*sx+tx, 0.296875*sy+ty,0.5677083*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.6614583*sx+tx, 0.38541666*sy+ty,0.6249998*sx+tx, 1.0*sy+ty,0.6249998*sx+tx, 1.0*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='o') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 1.8328434;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27736813*sx+tx, 0.30208334*sy+ty,0.10744485*sx+tx, 0.47049147*sy+ty,0.09000205*sx+tx, 0.6344033*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.83274436*sy+ty,0.22820449*sx+tx, 0.9781669*sy+ty,0.3528812*sx+tx, 0.9791616*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.4759399*sx+tx, 0.9791667*sy+ty,0.6520848*sx+tx, 0.84570843*sy+ty,0.65628254*sx+tx, 0.6558366*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='p') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7291666865348816;
		glyph.pixels = 2.4777036;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 0.90588427*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.2916666*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.45833334*sy+ty,0.19524959*sx+tx, 0.38515928*sy+ty,0.29178074*sx+tx, 0.33892274*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.2916666*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.45833334*sy+ty,0.31770846*sx+tx, 0.29687503*sy+ty,0.41666678*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.58333313*sx+tx, 0.30729166*sy+ty,0.63020813*sx+tx, 0.45833337*sy+ty,0.63020813*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.65503114*sy+ty,0.6235093*sx+tx, 0.6910149*sy+ty,0.6116279*sx+tx, 0.7257394*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.28645834*sy+ty);
				ctx.lineTo( 0.10416669*sx+tx, 1.2916666*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.45833334*sy+ty,0.31770846*sx+tx, 0.29687503*sy+ty,0.41666678*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.58333313*sx+tx, 0.30729166*sy+ty,0.63020813*sx+tx, 0.45833337*sy+ty,0.63020813*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.63020813*sx+tx, 0.78125*sy+ty,0.48958343*sx+tx, 0.9583333*sy+ty,0.35416678*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.21874997*sx+tx, 0.9583333*sy+ty,0.09895834*sx+tx, 0.8958333*sy+ty,0.09895834*sx+tx, 0.8958333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='q') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6927083134651184;
		glyph.pixels = 2.3640974;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5885415*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.34375*sy+ty,0.43229175*sx+tx, 0.3125*sy+ty,0.35416675*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.28339383*sx+tx, 0.34227958*sy+ty,0.1228633*sx+tx, 0.4419176*sy+ty,0.08940392*sx+tx, 0.58444774*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5885415*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.34375*sy+ty,0.43229175*sx+tx, 0.3125*sy+ty,0.35416675*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34375*sy+ty,0.08854168*sx+tx, 0.46354163*sy+ty,0.083333336*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.796875*sy+ty,0.1614583*sx+tx, 0.984375*sy+ty,0.3020834*sx+tx, 0.984375*sy+ty);
				ctx.bezierCurveTo( 0.3557818*sx+tx, 0.984375*sy+ty,0.40796125*sx+tx, 0.9669078*sy+ty,0.45195186*sx+tx, 0.9453132*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5885415*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.34375*sy+ty,0.43229175*sx+tx, 0.3125*sy+ty,0.35416675*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34375*sy+ty,0.08854168*sx+tx, 0.46354163*sy+ty,0.083333336*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.796875*sy+ty,0.1614583*sx+tx, 0.984375*sy+ty,0.3020834*sx+tx, 0.984375*sy+ty);
				ctx.bezierCurveTo( 0.4427084*sx+tx, 0.984375*sy+ty,0.5729165*sx+tx, 0.8645833*sy+ty,0.5729165*sx+tx, 0.8645833*sy+ty);
				ctx.moveTo( 0.5885415*sx+tx, 0.25520834*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.25520834*sy+ty,0.5868186*sx+tx, 0.4025185*sy+ty,0.58372307*sx+tx, 0.586077*sy+ty);
			} else {
				ctx.moveTo( 0.5885415*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.34375*sy+ty,0.43229175*sx+tx, 0.3125*sy+ty,0.35416675*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.34375*sy+ty,0.08854168*sx+tx, 0.46354163*sy+ty,0.083333336*sx+tx, 0.6302083*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.796875*sy+ty,0.1614583*sx+tx, 0.984375*sy+ty,0.3020834*sx+tx, 0.984375*sy+ty);
				ctx.bezierCurveTo( 0.4427084*sx+tx, 0.984375*sy+ty,0.5729165*sx+tx, 0.8645833*sy+ty,0.5729165*sx+tx, 0.8645833*sy+ty);
				ctx.moveTo( 0.5885415*sx+tx, 0.25520834*sy+ty);
				ctx.bezierCurveTo( 0.5885415*sx+tx, 0.25520834*sy+ty,0.5781248*sx+tx, 1.1458334*sy+ty,0.5624998*sx+tx, 1.2760416*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='r') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5989583134651184;
		glyph.pixels = 1.298929;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.60077393*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9255061*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.119791694*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.119791694*sx+tx, 0.5625*sy+ty,0.20312496*sx+tx, 0.4166667*sy+ty,0.25520828*sx+tx, 0.36458334*sy+ty);
				ctx.bezierCurveTo( 0.26091412*sx+tx, 0.35887754*sy+ty,0.2679951*sx+tx, 0.3537968*sy+ty,0.276102*sx+tx, 0.34932747*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.119791694*sx+tx, 0.5625*sy+ty);
				ctx.bezierCurveTo( 0.119791694*sx+tx, 0.5625*sy+ty,0.20312496*sx+tx, 0.4166667*sy+ty,0.25520828*sx+tx, 0.36458334*sy+ty);
				ctx.bezierCurveTo( 0.30729178*sx+tx, 0.3125*sy+ty,0.47395843*sx+tx, 0.3125*sy+ty,0.48958343*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.49479175*sx+tx, 0.34375*sy+ty,0.5052084*sx+tx, 0.45833334*sy+ty,0.5052084*sx+tx, 0.45833334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='s') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.640625;
		glyph.pixels = 1.7394919;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.53124994*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.49479166*sy+ty,0.55208313*sx+tx, 0.38541666*sy+ty,0.49479178*sx+tx, 0.33333334*sy+ty);
				ctx.bezierCurveTo( 0.43740052*sx+tx, 0.3200892*sy+ty,0.27897456*sx+tx, 0.28813496*sy+ty,0.20348029*sx+tx, 0.32311*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.53124994*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.49479166*sy+ty,0.55208313*sx+tx, 0.38541666*sy+ty,0.49479178*sx+tx, 0.33333334*sy+ty);
				ctx.bezierCurveTo( 0.42708346*sx+tx, 0.31770834*sy+ty,0.21874994*sx+tx, 0.2760417*sy+ty,0.17187496*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.421875*sy+ty,0.13020836*sx+tx, 0.5572917*sy+ty,0.32812512*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.3520497*sx+tx, 0.6273468*sy+ty,0.37407163*sx+tx, 0.63566303*sy+ty,0.39413565*sx+tx, 0.6446575*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.53124994*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.49479166*sy+ty,0.55208313*sx+tx, 0.38541666*sy+ty,0.49479178*sx+tx, 0.33333334*sy+ty);
				ctx.bezierCurveTo( 0.42708346*sx+tx, 0.31770834*sy+ty,0.21874994*sx+tx, 0.2760417*sy+ty,0.17187496*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.421875*sy+ty,0.13020836*sx+tx, 0.5572917*sy+ty,0.32812512*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.6822917*sy+ty,0.59374976*sx+tx, 0.796875*sy+ty,0.5000001*sx+tx, 0.9166667*sy+ty);
				ctx.bezierCurveTo( 0.47759235*sx+tx, 0.94529885*sy+ty,0.45042387*sx+tx, 0.9629218*sy+ty,0.42119712*sx+tx, 0.9726648*sy+ty);
			} else {
				ctx.moveTo( 0.53124994*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.53124994*sx+tx, 0.49479166*sy+ty,0.55208313*sx+tx, 0.38541666*sy+ty,0.49479178*sx+tx, 0.33333334*sy+ty);
				ctx.bezierCurveTo( 0.42708346*sx+tx, 0.31770834*sy+ty,0.21874994*sx+tx, 0.2760417*sy+ty,0.17187496*sx+tx, 0.34895834*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 0.421875*sy+ty,0.13020836*sx+tx, 0.5572917*sy+ty,0.32812512*sx+tx, 0.6197917*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.6822917*sy+ty,0.59374976*sx+tx, 0.796875*sy+ty,0.5000001*sx+tx, 0.9166667*sy+ty);
				ctx.bezierCurveTo( 0.40625012*sx+tx, 1.0364584*sy+ty,0.2291666*sx+tx, 0.9635416*sy+ty,0.16666663*sx+tx, 0.9270833*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.890625*sy+ty,0.072916664*sx+tx, 0.7916667*sy+ty,0.072916664*sx+tx, 0.7916667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='t') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5677083134651184;
		glyph.pixels = 1.4464797;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.24999996*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.25193372*sx+tx, 0.38244808*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.24999996*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.25386748*sx+tx, 0.7440628*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.24999996*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.25520828*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.020833334*sx+tx, 0.30729166*sy+ty);
				ctx.bezierCurveTo( 0.020833334*sx+tx, 0.30729166*sy+ty,0.044067726*sx+tx, 0.30671796*sy+ty,0.08043906*sx+tx, 0.30597448*sy+ty);
			} else {
				ctx.moveTo( 0.24999996*sx+tx, 0.020833334*sy+ty);
				ctx.lineTo( 0.25520828*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.020833334*sx+tx, 0.30729166*sy+ty);
				ctx.bezierCurveTo( 0.020833334*sx+tx, 0.30729166*sy+ty,0.44270843*sx+tx, 0.29687497*sy+ty,0.5052084*sx+tx, 0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='u') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 1.7867599;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.11526448*sx+tx, 0.71358263*sy+ty,0.11674729*sx+tx, 0.72960484*sy+ty,0.11904342*sx+tx, 0.7456339*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.3508884*sx+tx, 0.9635417*sy+ty,0.4282929*sx+tx, 0.935405*sy+ty,0.4873411*sx+tx, 0.9070171*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.59696335*sx+tx, 0.5481133*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='v') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7291666865348816;
		glyph.pixels = 1.5533714;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.083333336*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.22169018*sx+tx, 0.6336936*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.083333336*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.35937518*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.360069*sx+tx, 0.9930382*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.083333336*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.35937518*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.5029509*sx+tx, 0.63193583*sy+ty);
			} else {
				ctx.moveTo( 0.083333336*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.35937518*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6458329*sx+tx, 0.27083334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='w') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9739583134651184;
		glyph.pixels = 3.0257852;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.08854167*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.23958322*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.24336307*sx+tx, 0.9888213*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.08854167*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.23958322*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.48437524*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.48529807*sx+tx, 0.27995673*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.08854167*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.23958322*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.48437524*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.6562495*sx+tx, 1.0052084*sy+ty);
				ctx.lineTo( 0.6595513*sx+tx, 0.99438155*sy+ty);
			} else {
				ctx.moveTo( 0.08854167*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.23958322*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.48437524*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.6562495*sx+tx, 1.0052084*sy+ty);
				ctx.lineTo( 0.8802079*sx+tx, 0.27083334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='x') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 1.9138427;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.40690282*sx+tx, 0.6429957*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.6979164*sx+tx, 0.9791667*sy+ty);
				ctx.moveTo( 0.723958*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.70030755*sx+tx, 0.30022562*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.6979164*sx+tx, 0.9791667*sy+ty);
				ctx.moveTo( 0.723958*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.36577874*sx+tx, 0.64230037*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.6979164*sx+tx, 0.9791667*sy+ty);
				ctx.moveTo( 0.723958*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.031249998*sx+tx, 0.984375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='y') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8020833134651184;
		glyph.pixels = 1.8264782;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.06770833*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.27721846*sx+tx, 0.6817595*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.06770833*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.39583352*sx+tx, 0.9114583*sy+ty);
				ctx.moveTo( 0.69270796*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.6082224*sx+tx, 0.46043366*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.06770833*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.39583352*sx+tx, 0.9114583*sy+ty);
				ctx.moveTo( 0.69270796*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.41348615*sx+tx, 0.87344605*sy+ty);
			} else {
				ctx.moveTo( 0.06770833*sx+tx, 0.27604166*sy+ty);
				ctx.lineTo( 0.39583352*sx+tx, 0.9114583*sy+ty);
				ctx.moveTo( 0.69270796*sx+tx, 0.28125*sy+ty);
				ctx.lineTo( 0.21874991*sx+tx, 1.2864584*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='z') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7760416865348816;
		glyph.pixels = 2.0353596;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458335*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.6234233*sx+tx, 0.30729166*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458335*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.66666627*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.36320582*sx+tx, 0.6604095*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458335*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.66666627*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.09375001*sx+tx, 0.9739583*sy+ty);
				ctx.lineTo( 0.18915197*sx+tx, 0.97231346*sy+ty);
			} else {
				ctx.moveTo( 0.11458335*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.66666627*sx+tx, 0.30729166*sy+ty);
				ctx.lineTo( 0.09375001*sx+tx, 0.9739583*sy+ty);
				ctx.lineTo( 0.69791627*sx+tx, 0.9635417*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='{') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.515625;
		glyph.pixels = 1.626018;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.41666675*sx+tx, -0.14583333*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, -0.14583333*sy+ty,0.21354163*sx+tx, -0.0052083135*sy+ty,0.21874996*sx+tx, 0.16666667*sy+ty);
				ctx.bezierCurveTo( 0.21900451*sx+tx, 0.17506726*sy+ty,0.2191471*sx+tx, 0.18333098*sy+ty,0.21918562*sx+tx, 0.19145116*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.41666675*sx+tx, -0.14583333*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, -0.14583333*sy+ty,0.21354163*sx+tx, -0.0052083135*sy+ty,0.21874996*sx+tx, 0.16666667*sy+ty);
				ctx.bezierCurveTo( 0.22395828*sx+tx, 0.33854166*sy+ty,0.18229163*sx+tx, 0.453125*sy+ty,0.1614583*sx+tx, 0.453125*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.453125*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.05208333*sx+tx, 0.49479166*sy+ty,0.052414086*sx+tx, 0.49488616*sy+ty,0.05304355*sx+tx, 0.49507257*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.41666675*sx+tx, -0.14583333*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, -0.14583333*sy+ty,0.21354163*sx+tx, -0.0052083135*sy+ty,0.21874996*sx+tx, 0.16666667*sy+ty);
				ctx.bezierCurveTo( 0.22395828*sx+tx, 0.33854166*sy+ty,0.18229163*sx+tx, 0.453125*sy+ty,0.1614583*sx+tx, 0.453125*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.453125*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.05208333*sx+tx, 0.49479166*sy+ty,0.1614583*sx+tx, 0.5260417*sy+ty,0.18749996*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.21354163*sx+tx, 0.6197917*sy+ty,0.20833328*sx+tx, 0.7447916*sy+ty,0.20833328*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.20833328*sx+tx, 0.8245646*sy+ty,0.20842539*sx+tx, 0.8324339*sy+ty,0.20864421*sx+tx, 0.8410868*sy+ty);
			} else {
				ctx.moveTo( 0.41666675*sx+tx, -0.14583333*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, -0.14583333*sy+ty,0.21354163*sx+tx, -0.0052083135*sy+ty,0.21874996*sx+tx, 0.16666667*sy+ty);
				ctx.bezierCurveTo( 0.22395828*sx+tx, 0.33854166*sy+ty,0.18229163*sx+tx, 0.453125*sy+ty,0.1614583*sx+tx, 0.453125*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.453125*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty,0.05208333*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.05208333*sx+tx, 0.49479166*sy+ty,0.1614583*sx+tx, 0.5260417*sy+ty,0.18749996*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.21354163*sx+tx, 0.6197917*sy+ty,0.20833328*sx+tx, 0.7447916*sy+ty,0.20833328*sx+tx, 0.8177083*sy+ty);
				ctx.bezierCurveTo( 0.20833328*sx+tx, 0.890625*sy+ty,0.21874996*sx+tx, 1.0781249*sy+ty,0.28125003*sx+tx, 1.1041666*sy+ty);
				ctx.bezierCurveTo( 0.3437501*sx+tx, 1.1302084*sy+ty,0.38541666*sx+tx, 1.1510416*sy+ty,0.41666675*sx+tx, 1.1510416*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='|') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2708333432674408;
		glyph.pixels = 1.4687593;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13541667*sx+tx, -0.22916667*sy+ty);
				ctx.lineTo( 0.13671875*sx+tx, 0.13802083*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13541667*sx+tx, -0.22916667*sy+ty);
				ctx.lineTo( 0.13802084*sx+tx, 0.5052083*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13541667*sx+tx, -0.22916667*sy+ty);
				ctx.lineTo( 0.13932292*sx+tx, 0.8723957*sy+ty);
			} else {
				ctx.moveTo( 0.13541667*sx+tx, -0.22916667*sy+ty);
				ctx.lineTo( 0.14062501*sx+tx, 1.2395834*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='}') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.5;
		glyph.pixels = 1.6624951;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.062499993*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, -0.11458334*sy+ty,0.24999996*sx+tx, -0.072916664*sy+ty,0.265625*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.2769253*sx+tx, -0.010091043*sy+ty,0.27460465*sx+tx, 0.087104976*sy+ty,0.2724543*sx+tx, 0.15859117*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.062499993*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, -0.11458334*sy+ty,0.24999996*sx+tx, -0.072916664*sy+ty,0.265625*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.28125003*sx+tx, 0.0*sy+ty,0.27083334*sx+tx, 0.171875*sy+ty,0.27083334*sx+tx, 0.22395833*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.27604166*sy+ty,0.29166672*sx+tx, 0.44270834*sy+ty,0.3125001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.33157086*sx+tx, 0.47263643*sy+ty,0.40737802*sx+tx, 0.50439686*sy+ty,0.42006844*sx+tx, 0.50966847*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.062499993*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, -0.11458334*sy+ty,0.24999996*sx+tx, -0.072916664*sy+ty,0.265625*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.28125003*sx+tx, 0.0*sy+ty,0.27083334*sx+tx, 0.171875*sy+ty,0.27083334*sx+tx, 0.22395833*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.27604166*sy+ty,0.29166672*sx+tx, 0.44270834*sy+ty,0.3125001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.33333343*sx+tx, 0.47395834*sy+ty,0.4218751*sx+tx, 0.5104167*sy+ty,0.4218751*sx+tx, 0.5104167*sy+ty);
				ctx.bezierCurveTo( 0.4218751*sx+tx, 0.5104167*sy+ty,0.2760417*sx+tx, 0.5729167*sy+ty,0.2760417*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.6909787*sy+ty,0.27892226*sx+tx, 0.8075855*sy+ty,0.28039894*sx+tx, 0.879049*sy+ty);
			} else {
				ctx.moveTo( 0.062499993*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.19270828*sx+tx, -0.11458334*sy+ty,0.24999996*sx+tx, -0.072916664*sy+ty,0.265625*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.28125003*sx+tx, 0.0*sy+ty,0.27083334*sx+tx, 0.171875*sy+ty,0.27083334*sx+tx, 0.22395833*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.27604166*sy+ty,0.29166672*sx+tx, 0.44270834*sy+ty,0.3125001*sx+tx, 0.45833334*sy+ty);
				ctx.bezierCurveTo( 0.33333343*sx+tx, 0.47395834*sy+ty,0.4218751*sx+tx, 0.5104167*sy+ty,0.4218751*sx+tx, 0.5104167*sy+ty);
				ctx.bezierCurveTo( 0.4218751*sx+tx, 0.5104167*sy+ty,0.2760417*sx+tx, 0.5729167*sy+ty,0.2760417*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.2760417*sx+tx, 0.7083333*sy+ty,0.28125003*sx+tx, 0.8958334*sy+ty,0.28125003*sx+tx, 0.9322917*sy+ty);
				ctx.bezierCurveTo( 0.28125003*sx+tx, 0.96875*sy+ty,0.21874994*sx+tx, 1.0989584*sy+ty,0.19270828*sx+tx, 1.1145834*sy+ty);
				ctx.bezierCurveTo( 0.16666664*sx+tx, 1.1302084*sy+ty,0.083333336*sx+tx, 1.15625*sy+ty,0.05208333*sx+tx, 1.15625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='£') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9739583134651184;
		glyph.pixels = 3.1570609;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.8177083*sx+tx, 0.23958333*sy+ty);
				ctx.bezierCurveTo( 0.8177083*sx+tx, 0.23958333*sy+ty,0.7291667*sx+tx, -0.03125*sy+ty,0.5364583*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.38330206*sx+tx, -0.04496701*sy+ty,0.23709603*sx+tx, 0.054251976*sy+ty,0.20286709*sx+tx, 0.17888024*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.8177083*sx+tx, 0.23958333*sy+ty);
				ctx.bezierCurveTo( 0.8177083*sx+tx, 0.23958333*sy+ty,0.7291667*sx+tx, -0.03125*sy+ty,0.5364583*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.34895834*sx+tx, -0.046875*sy+ty,0.17187501*sx+tx, 0.10416666*sy+ty,0.19791667*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.22395833*sx+tx, 0.42708334*sy+ty,0.28125003*sx+tx, 0.59374994*sy+ty,0.28645834*sx+tx, 0.7239583*sy+ty);
				ctx.bezierCurveTo( 0.29061878*sx+tx, 0.827969*sy+ty,0.18843167*sx+tx, 0.93530315*sy+ty,0.1471436*sx+tx, 0.97428346*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.8177083*sx+tx, 0.23958333*sy+ty);
				ctx.bezierCurveTo( 0.8177083*sx+tx, 0.23958333*sy+ty,0.7291667*sx+tx, -0.03125*sy+ty,0.5364583*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.34895834*sx+tx, -0.046875*sy+ty,0.17187501*sx+tx, 0.10416666*sy+ty,0.19791667*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.22395833*sx+tx, 0.42708334*sy+ty,0.28125003*sx+tx, 0.59374994*sy+ty,0.28645834*sx+tx, 0.7239583*sy+ty);
				ctx.bezierCurveTo( 0.29166666*sx+tx, 0.8541667*sy+ty,0.13020833*sx+tx, 0.9895833*sy+ty,0.13020833*sx+tx, 0.9895833*sy+ty);
				ctx.bezierCurveTo( 0.13020833*sx+tx, 0.9895833*sy+ty,0.32812497*sx+tx, 0.8333333*sy+ty,0.40104166*sx+tx, 0.859375*sy+ty);
				ctx.bezierCurveTo( 0.47395834*sx+tx, 0.8854167*sy+ty,0.546875*sx+tx, 0.9739584*sy+ty,0.6875*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.7513426*sx+tx, 0.9815312*sy+ty,0.8001564*sx+tx, 0.94202995*sy+ty,0.8344289*sx+tx, 0.898189*sy+ty);
			} else {
				ctx.moveTo( 0.8177083*sx+tx, 0.23958333*sy+ty);
				ctx.bezierCurveTo( 0.8177083*sx+tx, 0.23958333*sy+ty,0.7291667*sx+tx, -0.03125*sy+ty,0.5364583*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.34895834*sx+tx, -0.046875*sy+ty,0.17187501*sx+tx, 0.10416666*sy+ty,0.19791667*sx+tx, 0.265625*sy+ty);
				ctx.bezierCurveTo( 0.22395833*sx+tx, 0.42708334*sy+ty,0.28125003*sx+tx, 0.59374994*sy+ty,0.28645834*sx+tx, 0.7239583*sy+ty);
				ctx.bezierCurveTo( 0.29166666*sx+tx, 0.8541667*sy+ty,0.13020833*sx+tx, 0.9895833*sy+ty,0.13020833*sx+tx, 0.9895833*sy+ty);
				ctx.bezierCurveTo( 0.13020833*sx+tx, 0.9895833*sy+ty,0.32812497*sx+tx, 0.8333333*sy+ty,0.40104166*sx+tx, 0.859375*sy+ty);
				ctx.bezierCurveTo( 0.47395834*sx+tx, 0.8854167*sy+ty,0.546875*sx+tx, 0.9739584*sy+ty,0.6875*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.828125*sx+tx, 0.984375*sy+ty,0.8958333*sx+tx, 0.7864583*sy+ty,0.8958333*sx+tx, 0.7864583*sy+ty);
				ctx.moveTo( 0.020833334*sx+tx, 0.47916666*sy+ty);
				ctx.lineTo( 0.6458333*sx+tx, 0.47916666*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='©') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8072916865348816;
		glyph.pixels = 2.5809774;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.1064044*sx+tx, 0.27588552*sy+ty,0.12979332*sx+tx, 0.20553802*sy+ty,0.17185506*sx+tx, 0.14608715*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.5988157*sx+tx, 0.014686519*sy+ty,0.677828*sx+tx, 0.11696965*sy+ty,0.70089555*sx+tx, 0.24265312*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.6354164*sx+tx, 0.015625*sy+ty,0.7083331*sx+tx, 0.16666666*sy+ty,0.7083331*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.7083331*sx+tx, 0.48958334*sy+ty,0.58854145*sx+tx, 0.6302083*sy+ty,0.42187512*sx+tx, 0.6354166*sy+ty);
				ctx.lineTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.5260416*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.21875*sy+ty,0.49001643*sx+tx, 0.19103827*sy+ty,0.4458378*sx+tx, 0.17420658*sy+ty);
			} else {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.6354164*sx+tx, 0.015625*sy+ty,0.7083331*sx+tx, 0.16666666*sy+ty,0.7083331*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.7083331*sx+tx, 0.48958334*sy+ty,0.58854145*sx+tx, 0.6302083*sy+ty,0.42187512*sx+tx, 0.6354166*sy+ty);
				ctx.lineTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.5260416*sx+tx, 0.21875*sy+ty);
				ctx.bezierCurveTo( 0.5260416*sx+tx, 0.21875*sy+ty,0.39062515*sx+tx, 0.11458334*sy+ty,0.3229168*sx+tx, 0.1875*sy+ty);
				ctx.bezierCurveTo( 0.25520828*sx+tx, 0.26041666*sy+ty,0.23958327*sx+tx, 0.359375*sy+ty,0.28125003*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.3020834*sx+tx, 0.453125*sy+ty,0.39583334*sx+tx, 0.48958334*sy+ty,0.4635418*sx+tx, 0.453125*sy+ty);
				ctx.bezierCurveTo( 0.5000001*sx+tx, 0.42708334*sy+ty,0.55208313*sx+tx, 0.375*sy+ty,0.55208313*sx+tx, 0.375*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='®') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8072916865348816;
		glyph.pixels = 2.8759317;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.107199386*sx+tx, 0.25362596*sy+ty,0.14908141*sx+tx, 0.15458328*sy+ty,0.22364339*sx+tx, 0.08768407*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.6354164*sx+tx, 0.015625*sy+ty,0.7083331*sx+tx, 0.16666666*sy+ty,0.7083331*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.7083331*sx+tx, 0.35240242*sy+ty,0.7056247*sx+tx, 0.37620884*sy+ty,0.7004558*sx+tx, 0.39915466*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.6354164*sx+tx, 0.015625*sy+ty,0.7083331*sx+tx, 0.16666666*sy+ty,0.7083331*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.7083331*sx+tx, 0.48958334*sy+ty,0.58854145*sx+tx, 0.6302083*sy+ty,0.42187512*sx+tx, 0.6354166*sy+ty);
				ctx.lineTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.28125003*sx+tx, 0.46354166*sy+ty);
				ctx.lineTo( 0.32812512*sx+tx, 0.17708333*sy+ty);
				ctx.bezierCurveTo( 0.32812512*sx+tx, 0.17708333*sy+ty,0.33424017*sx+tx, 0.17678504*sy+ty,0.34440064*sx+tx, 0.17666799*sy+ty);
			} else {
				ctx.moveTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.bezierCurveTo( 0.31770843*sx+tx, 0.6354167*sy+ty,0.09895834*sx+tx, 0.5572917*sy+ty,0.10416668*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.109375015*sx+tx, 0.19270831*sy+ty,0.22916664*sx+tx, 0.005208334*sy+ty,0.4322918*sx+tx, 0.010416667*sy+ty);
				ctx.bezierCurveTo( 0.6354164*sx+tx, 0.015625*sy+ty,0.7083331*sx+tx, 0.16666666*sy+ty,0.7083331*sx+tx, 0.328125*sy+ty);
				ctx.bezierCurveTo( 0.7083331*sx+tx, 0.48958334*sy+ty,0.58854145*sx+tx, 0.6302083*sy+ty,0.42187512*sx+tx, 0.6354166*sy+ty);
				ctx.lineTo( 0.40625012*sx+tx, 0.6354167*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.28125003*sx+tx, 0.46354166*sy+ty);
				ctx.lineTo( 0.32812512*sx+tx, 0.17708333*sy+ty);
				ctx.bezierCurveTo( 0.32812512*sx+tx, 0.17708333*sy+ty,0.5416665*sx+tx, 0.16666669*sy+ty,0.5416665*sx+tx, 0.24479167*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.32291666*sy+ty,0.3020834*sx+tx, 0.33333334*sy+ty,0.3020834*sx+tx, 0.33333334*sy+ty);
				ctx.lineTo( 0.5260416*sx+tx, 0.46354166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='°') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.453125;
		glyph.pixels = 0.81543654;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.24999963*sx+tx, 0.20833328*sy+ty);
				ctx.bezierCurveTo( 0.17304458*sx+tx, 0.21314295*sy+ty,0.10053102*sx+tx, 0.16909525*sy+ty,0.08988192*sx+tx, 0.096698225*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.24999963*sx+tx, 0.20833328*sy+ty);
				ctx.bezierCurveTo( 0.16666655*sx+tx, 0.2135416*sy+ty,0.088541694*sx+tx, 0.16145831*sy+ty,0.088541694*sx+tx, 0.07812493*sy+ty);
				ctx.bezierCurveTo( 0.08381943*sx+tx, 0.0072905635*sy+ty,0.12191315*sx+tx, -0.04213576*sy+ty,0.20282279*sx+tx, -0.050743822*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.24999963*sx+tx, 0.20833328*sy+ty);
				ctx.bezierCurveTo( 0.16666655*sx+tx, 0.2135416*sy+ty,0.088541694*sx+tx, 0.16145831*sy+ty,0.088541694*sx+tx, 0.07812493*sy+ty);
				ctx.bezierCurveTo( 0.08333339*sx+tx, -6.550302E-8*sy+ty,0.13020831*sx+tx, -0.05208338*sy+ty,0.22916637*sx+tx, -0.05208338*sy+ty);
				ctx.bezierCurveTo( 0.33146274*sx+tx, -0.05208338*sy+ty,0.36318848*sx+tx, 0.0060336045*sy+ty,0.36140403*sx+tx, 0.05926444*sy+ty);
			} else {
				ctx.moveTo( 0.24999963*sx+tx, 0.20833328*sy+ty);
				ctx.bezierCurveTo( 0.16666655*sx+tx, 0.2135416*sy+ty,0.088541694*sx+tx, 0.16145831*sy+ty,0.088541694*sx+tx, 0.07812493*sy+ty);
				ctx.bezierCurveTo( 0.08333339*sx+tx, -6.550302E-8*sy+ty,0.13020831*sx+tx, -0.05208338*sy+ty,0.22916637*sx+tx, -0.05208338*sy+ty);
				ctx.bezierCurveTo( 0.34374988*sx+tx, -0.05208338*sy+ty,0.36979172*sx+tx, 0.020833282*sy+ty,0.35937497*sx+tx, 0.07812493*sy+ty);
				ctx.bezierCurveTo( 0.3645833*sx+tx, 0.14583333*sy+ty,0.3229164*sx+tx, 0.20833328*sy+ty,0.24999963*sx+tx, 0.20833328*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='¿') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.734375;
		glyph.pixels = 1.4802034;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.6458332*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.6458332*sx+tx, 0.8385417*sy+ty,0.5468749*sx+tx, 0.9479166*sy+ty,0.3906251*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.35783488*sx+tx, 0.97942334*sy+ty,0.3238978*sx+tx, 0.977319*sy+ty,0.29097998*sx+tx, 0.9688487*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.6458332*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.6458332*sx+tx, 0.8385417*sy+ty,0.5468749*sx+tx, 0.9479166*sy+ty,0.3906251*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.23642975*sx+tx, 0.9996575*sy+ty,0.056873355*sx+tx, 0.8579726*sy+ty,0.07720516*sx+tx, 0.6740421*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.6458332*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.6458332*sx+tx, 0.8385417*sy+ty,0.5468749*sx+tx, 0.9479166*sy+ty,0.3906251*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.23437494*sx+tx, 1.0*sy+ty,0.05208333*sx+tx, 0.85416675*sy+ty,0.078125*sx+tx, 0.6666667*sy+ty);
				ctx.bezierCurveTo( 0.09364622*sx+tx, 0.554914*sy+ty,0.25533098*sx+tx, 0.4431613*sy+ty,0.38343447*sx+tx, 0.37110686*sy+ty);
			} else {
				ctx.moveTo( 0.6458332*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.6458332*sx+tx, 0.8385417*sy+ty,0.5468749*sx+tx, 0.9479166*sy+ty,0.3906251*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.23437494*sx+tx, 1.0*sy+ty,0.05208333*sx+tx, 0.85416675*sy+ty,0.078125*sx+tx, 0.6666667*sy+ty);
				ctx.bezierCurveTo( 0.10416669*sx+tx, 0.47916663*sy+ty,0.5416665*sx+tx, 0.29166666*sy+ty,0.5416665*sx+tx, 0.29166666*sy+ty);
				ctx.moveTo( 0.58333325*sx+tx, 0.140625*sy+ty);
				ctx.lineTo( 0.6406249*sx+tx, 0.03125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='À') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 2.933007;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.33902812*sx+tx, 0.31674105*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.5886936*sx+tx, 0.39334998*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.16849163*sx+tx, 0.6747276*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.36979178*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Á') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 2.93642;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.3393378*sx+tx, 0.31594598*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.58924353*sx+tx, 0.39496544*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.17098612*sx+tx, 0.6741536*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.58854145*sx+tx, -0.32291666*sy+ty);
				ctx.lineTo( 0.43229178*sx+tx, -0.14583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Â') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 3.2218652;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.3652362*sx+tx, 0.24945003*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.6352378*sx+tx, 0.5300738*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.37961888*sx+tx, 0.62614965*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.22916667*sx+tx, -0.15625*sy+ty);
				ctx.lineTo( 0.43229166*sx+tx, -0.31770834*sy+ty);
				ctx.lineTo( 0.6302083*sx+tx, -0.14583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ã') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 3.2215302;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.36520582*sx+tx, 0.24952805*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.6351839*sx+tx, 0.5299153*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.37937403*sx+tx, 0.626206*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.2552083*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.2552083*sx+tx, -0.15104167*sy+ty,0.2708334*sx+tx, -0.27604166*sy+ty,0.35416672*sx+tx, -0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.43750003*sx+tx, -0.27604166*sy+ty,0.484375*sx+tx, -0.19270833*sy+ty,0.55729157*sx+tx, -0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.63020825*sx+tx, -0.19270833*sy+ty,0.63541657*sx+tx, -0.30208334*sy+ty,0.63541657*sx+tx, -0.30208334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ä') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 2.919008;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.337758*sx+tx, 0.32000214*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.58643794*sx+tx, 0.38672394*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.15825975*sx+tx, 0.6770818*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.3177084*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.31770834*sx+tx, -0.15625*sy+ty);
				ctx.moveTo( 0.6041665*sx+tx, -0.26041666*sy+ty);
				ctx.lineTo( 0.6041665*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Å') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 3.5156944;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.39189526*sx+tx, 0.18100107*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.68258303*sx+tx, 0.6691505*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.5943791*sx+tx, 0.57673573*sy+ty);
			} else {
				ctx.moveTo( 0.072916664*sx+tx, 1.0*sy+ty);
				ctx.lineTo( 0.45833343*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.79166645*sx+tx, 0.9895833*sy+ty);
				ctx.moveTo( 0.06770833*sx+tx, 0.6979167*sy+ty);
				ctx.lineTo( 0.6562498*sx+tx, 0.5625*sy+ty);
				ctx.moveTo( 0.48437506*sx+tx, -0.0156249935*sy+ty);
				ctx.bezierCurveTo( 0.40104175*sx+tx, -0.010416669*sy+ty,0.32291672*sx+tx, -0.0625*sy+ty,0.32291672*sx+tx, -0.1458334*sy+ty);
				ctx.bezierCurveTo( 0.3177084*sx+tx, -0.22395833*sy+ty,0.3645834*sx+tx, -0.27604166*sy+ty,0.46354175*sx+tx, -0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.5781249*sx+tx, -0.27604166*sy+ty,0.60416657*sx+tx, -0.203125*sy+ty,0.5937499*sx+tx, -0.1458334*sy+ty);
				ctx.bezierCurveTo( 0.5989582*sx+tx, -0.078125*sy+ty,0.5572915*sx+tx, -0.0156249935*sy+ty,0.48437506*sx+tx, -0.0156249935*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Æ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.2864583730697632;
		glyph.pixels = 4.077852;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.05208333*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.5613969*sx+tx, 0.11166978*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.05208333*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6197915*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.7187498*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.7187498*sx+tx, 0.0052083335*sy+ty,0.71138996*sx+tx, 0.6123978*sy+ty,0.7121364*sx+tx, 0.87715775*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.05208333*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6197915*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.7187498*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.7187498*sx+tx, 0.0052083335*sy+ty,0.70833313*sx+tx, 0.8645833*sy+ty,0.7135415*sx+tx, 0.984375*sy+ty);
				ctx.moveTo( 0.7187498*sx+tx, 0.0052083326*sy+ty);
				ctx.lineTo( 1.2187504*sx+tx, 0.010416667*sy+ty);
				ctx.moveTo( 0.7187498*sx+tx, 0.46354166*sy+ty);
				ctx.lineTo( 1.0857551*sx+tx, 0.459066*sy+ty);
			} else {
				ctx.moveTo( 0.05208333*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.6197915*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.7187498*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.7187498*sx+tx, 0.0052083335*sy+ty,0.70833313*sx+tx, 0.8645833*sy+ty,0.7135415*sx+tx, 0.984375*sy+ty);
				ctx.moveTo( 0.7187498*sx+tx, 0.0052083326*sy+ty);
				ctx.lineTo( 1.2187504*sx+tx, 0.010416667*sy+ty);
				ctx.moveTo( 0.7187498*sx+tx, 0.46354166*sy+ty);
				ctx.lineTo( 1.1458336*sx+tx, 0.45833334*sy+ty);
				ctx.moveTo( 0.6979166*sx+tx, 0.94270843*sy+ty);
				ctx.bezierCurveTo( 0.6979166*sx+tx, 0.94270843*sy+ty,1.1718756*sx+tx, 0.9427082*sy+ty,1.2500004*sx+tx, 0.9427083*sy+ty);
				ctx.moveTo( 0.265625*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.6874998*sx+tx, 0.6354167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ç') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8854166865348816;
		glyph.pixels = 2.4099154;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.45416877*sx+tx, 0.03378632*sy+ty,0.34747568*sx+tx, 0.07635112*sy+ty,0.2574329*sx+tx, 0.16907556*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.67381257*sy+ty,0.13849132*sx+tx, 0.76773214*sy+ty,0.20052657*sx+tx, 0.83819383*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.7864583*sy+ty,0.2760417*sx+tx, 0.96875006*sy+ty,0.46875012*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.61688215*sx+tx, 0.9595381*sy+ty,0.7403943*sx+tx, 0.85705477*sy+ty,0.7896085*sx+tx, 0.80985737*sy+ty);
			} else {
				ctx.moveTo( 0.74999976*sx+tx, 0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.74999976*sx+tx, 0.15104167*sy+ty,0.63020813*sx+tx, 0.03125*sy+ty,0.515625*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.39583343*sx+tx, 0.03125*sy+ty,0.10416668*sx+tx, 0.19791667*sy+ty,0.09895834*sx+tx, 0.5729167*sy+ty);
				ctx.bezierCurveTo( 0.09895834*sx+tx, 0.7864583*sy+ty,0.2760417*sx+tx, 0.96875006*sy+ty,0.46875012*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.66145813*sx+tx, 0.9583333*sy+ty,0.81249976*sx+tx, 0.7864583*sy+ty,0.81249976*sx+tx, 0.7864583*sy+ty);
				ctx.moveTo( 0.44270828*sx+tx, 0.98437506*sy+ty);
				ctx.lineTo( 0.37499985*sx+tx, 1.0833331*sy+ty);
				ctx.bezierCurveTo( 0.37499985*sx+tx, 1.0833331*sy+ty,0.5208337*sx+tx, 1.1302083*sy+ty,0.510417*sx+tx, 1.1718749*sy+ty);
				ctx.bezierCurveTo( 0.5000003*sx+tx, 1.2135415*sy+ty,0.46354178*sx+tx, 1.2604164*sy+ty,0.4114582*sx+tx, 1.2552081*sy+ty);
				ctx.bezierCurveTo( 0.35937485*sx+tx, 1.2499999*sy+ty,0.3177082*sx+tx, 1.2395831*sy+ty,0.3177082*sx+tx, 1.2395831*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='È') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 3.068754;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.113336936*sx+tx, 0.14739005*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.104717374*sx+tx, 0.9145301*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.16086158*sx+tx, 0.484375*sy+ty);
			} else {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.3125001*sx+tx, -0.32291666*sy+ty);
				ctx.lineTo( 0.46354178*sx+tx, -0.15625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='É') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 3.0709145;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11333086*sx+tx, 0.14793019*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10470524*sx+tx, 0.9156104*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.16248211*sx+tx, 0.484375*sy+ty);
			} else {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.49479175*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.3593751*sx+tx, -0.14583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ê') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 3.3387492;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11257856*sx+tx, 0.21488461*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.19014685*sx+tx, 0.96422404*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.3633581*sx+tx, 0.484375*sy+ty);
			} else {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.22916664*sx+tx, -0.15104167*sy+ty);
				ctx.lineTo( 0.42187503*sx+tx, -0.3125*sy+ty);
				ctx.lineTo( 0.60416657*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ë') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8333333134651184;
		glyph.pixels = 3.067787;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11333965*sx+tx, 0.14714836*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.104722805*sx+tx, 0.91404665*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.16013631*sx+tx, 0.484375*sy+ty);
			} else {
				ctx.moveTo( 0.77083313*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.11458336*sx+tx, 0.036458332*sy+ty);
				ctx.lineTo( 0.10416668*sx+tx, 0.9635417*sy+ty);
				ctx.lineTo( 0.76041645*sx+tx, 0.96875*sy+ty);
				ctx.moveTo( 0.09895834*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.7031248*sx+tx, 0.484375*sy+ty);
				ctx.moveTo( 0.25520834*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.25520834*sx+tx, -0.15625*sy+ty);
				ctx.moveTo( 0.546875*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.546875*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ì') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 2.083509;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.29963103*sx+tx, 0.53128666*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.1302785*sx+tx, 0.042333666*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.11549809*sx+tx, 0.9593434*sy+ty,0.16822669*sx+tx, 0.9605772*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
				ctx.moveTo( 0.19270828*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.34375012*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Í') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 2.0801635;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.2996266*sx+tx, 0.5304503*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.12860587*sx+tx, 0.042314216*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.11407256*sx+tx, 0.95930487*sy+ty,0.16519774*sx+tx, 0.9605062*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
				ctx.moveTo( 0.43229198*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.28645828*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Î') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 2.3633366;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30000114*sx+tx, 0.60124254*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.27018285*sx+tx, 0.04396046*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.33285517*sx+tx, 0.9652179*sy+ty,0.450527*sx+tx, 0.96499467*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
				ctx.moveTo( 0.10416668*sx+tx, -0.15104167*sy+ty);
				ctx.lineTo( 0.29687497*sx+tx, -0.32291666*sy+ty);
				ctx.lineTo( 0.4843753*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ï') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 2.06951;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.2996125*sx+tx, 0.52778697*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.12327948*sx+tx, 0.042252284*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.10971772*sx+tx, 0.95918715*sy+ty,0.15579073*sx+tx, 0.9602837*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
				ctx.moveTo( 0.20312494*sx+tx, -0.26041666*sy+ty);
				ctx.lineTo( 0.20312494*sx+tx, -0.15104167*sy+ty);
				ctx.moveTo( 0.41666692*sx+tx, -0.26041666*sy+ty);
				ctx.lineTo( 0.41666692*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ñ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.8958333134651184;
		glyph.pixels = 3.6304917;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.099364504*sx+tx, 0.08718145*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.56494284*sx+tx, 0.6982989*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.75520813*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.75520813*sx+tx, 0.9791667*sy+ty,0.75520813*sx+tx, 0.6651951*sy+ty,0.75634795*sx+tx, 0.3940055*sy+ty);
			} else {
				ctx.moveTo( 0.10416669*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.75520813*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.75520813*sx+tx, 0.9791667*sy+ty,0.7552082*sx+tx, 0.114583336*sy+ty,0.7604165*sx+tx, 0.015625*sy+ty);
				ctx.moveTo( 0.25520825*sx+tx, -0.15625*sy+ty);
				ctx.bezierCurveTo( 0.25520825*sx+tx, -0.15625*sy+ty,0.28645825*sx+tx, -0.27604166*sy+ty,0.3541666*sx+tx, -0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.42187494*sx+tx, -0.27604166*sy+ty,0.5000001*sx+tx, -0.1875*sy+ty,0.5625001*sx+tx, -0.1875*sy+ty);
				ctx.bezierCurveTo( 0.6250001*sx+tx, -0.1875*sy+ty,0.6510418*sx+tx, -0.30729166*sy+ty,0.6510418*sx+tx, -0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ò') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.6339726;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.09069168*sx+tx, 0.5450713*sy+ty,0.08894143*sx+tx, 0.56345797*sy+ty,0.08837054*sx+tx, 0.5812031*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.4301821*sx+tx, 0.9552054*sy+ty,0.45638475*sx+tx, 0.95333856*sy+ty,0.48641512*sx+tx, 0.9464043*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.80025303*sx+tx, 0.44046116*sy+ty,0.78884083*sx+tx, 0.3515767*sy+ty,0.766369*sx+tx, 0.27951097*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.4375001*sx+tx, -0.32291666*sy+ty);
				ctx.lineTo( 0.5781248*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ó') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.6348262;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.09067448*sx+tx, 0.5451783*sy+ty,0.08892179*sx+tx, 0.5636647*sy+ty,0.08836102*sx+tx, 0.5815023*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.4302518*sx+tx, 0.9552131*sy+ty,0.4565799*sx+tx, 0.9533246*sy+ty,0.48675057*sx+tx, 0.9463266*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8002639*sx+tx, 0.4401188*sy+ty,0.7887673*sx+tx, 0.35100493*sy+ty,0.7661517*sx+tx, 0.2788159*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.6197915*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.48437506*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ô') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.9327667;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.084672995*sx+tx, 0.58252084*sy+ty,0.08711868*sx+tx, 0.6333363*sy+ty,0.097721204*sx+tx, 0.6785259*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.4545741*sx+tx, 0.9579156*sy+ty,0.5373476*sx+tx, 0.94177574*sy+ty,0.6151956*sx+tx, 0.89102626*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.80405784*sx+tx, 0.32060924*sy+ty,0.7443724*sx+tx, 0.17359506*sy+ty,0.65368235*sx+tx, 0.09900119*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.31250003*sx+tx, -0.15625*sy+ty);
				ctx.lineTo( 0.5052083*sx+tx, -0.32291666*sy+ty);
				ctx.lineTo( 0.7083333*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Õ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.954385;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.08423753*sx+tx, 0.5852304*sy+ty,0.08737986*sx+tx, 0.638199*sy+ty,0.09930311*sx+tx, 0.68499094*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.4563389*sx+tx, 0.9581117*sy+ty,0.5441902*sx+tx, 0.9404194*sy+ty,0.6247272*sx+tx, 0.88461953*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.80433315*sx+tx, 0.31193766*sy+ty,0.7396993*sx+tx, 0.16244288*sy+ty,0.64312077*sx+tx, 0.090743534*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.29166666*sx+tx, -0.14583333*sy+ty);
				ctx.bezierCurveTo( 0.29166666*sx+tx, -0.14583333*sy+ty,0.328125*sx+tx, -0.28125*sy+ty,0.390625*sx+tx, -0.28125*sy+ty);
				ctx.bezierCurveTo( 0.453125*sx+tx, -0.28125*sy+ty,0.51562494*sx+tx, -0.19270833*sy+ty,0.5989583*sx+tx, -0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.6822917*sx+tx, -0.19270833*sy+ty,0.6927083*sx+tx, -0.30208334*sy+ty,0.6927083*sx+tx, -0.30208334*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ö') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 2.6307738;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.09075611*sx+tx, 0.5446704*sy+ty,0.08901576*sx+tx, 0.5626829*sy+ty,0.088408194*sx+tx, 0.5800807*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.429921*sx+tx, 0.9551764*sy+ty,0.45565537*sx+tx, 0.9533899*sy+ty,0.48516092*sx+tx, 0.94669145*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8002123*sx+tx, 0.44174424*sy+ty,0.7891139*sx+tx, 0.3537227*sy+ty,0.76717705*sx+tx, 0.28212512*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.34895834*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.34895834*sx+tx, -0.15625*sy+ty);
				ctx.moveTo( 0.6354167*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.640625*sx+tx, -0.15625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ø') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9010416865348816;
		glyph.pixels = 3.6215122;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.070799425*sx+tx, 0.6688453*sy+ty,0.12151289*sx+tx, 0.7754411*sy+ty,0.19148451*sx+tx, 0.8464405*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.79745626*sx+tx, 0.5285594*sy+ty,0.79759955*sx+tx, 0.5107631*sy+ty,0.7973237*sx+tx, 0.49348244*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.84375*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.6564246*sx+tx, 0.2657658*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.84375*sx+tx, 0.026041666*sy+ty);
				ctx.lineTo( 0.098958336*sx+tx, 0.9791667*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ù') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.389962;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.096177325*sx+tx, 0.4946679*sy+ty,0.14536928*sx+tx, 0.7361421*sy+ty,0.22036871*sx+tx, 0.8557011*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.4717243*sx+tx, 0.9743641*sy+ty,0.48524168*sx+tx, 0.974517*sy+ty,0.49878454*sx+tx, 0.97403514*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.5770104*sx+tx, 0.9775546*sy+ty,0.70562017*sx+tx, 0.9612855*sy+ty,0.77386427*sx+tx, 0.6593886*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.moveTo( 0.3750001*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.5364582*sx+tx, -0.14583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ú') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.372576;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09619755*sx+tx, 0.49110726*sy+ty,0.14465632*sx+tx, 0.7326157*sy+ty,0.21873605*sx+tx, 0.85307246*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.4701925*sx+tx, 0.97431767*sy+ty,0.48215076*sx+tx, 0.97447866*sy+ty,0.4941381*sx+tx, 0.9741761*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.57471263*sx+tx, 0.97748494*sy+ty,0.7006438*sx+tx, 0.961908*sy+ty,0.769833*sx+tx, 0.67660457*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.moveTo( 0.5677082*sx+tx, -0.328125*sy+ty);
				ctx.lineTo( 0.4270834*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Û') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.6532755;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09587093*sx+tx, 0.548594*sy+ty,0.15683956*sx+tx, 0.7863407*sy+ty,0.24606267*sx+tx, 0.8909473*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.49492407*sx+tx, 0.97506714*sy+ty,0.53245896*sx+tx, 0.9742875*sy+ty,0.56887764*sx+tx, 0.96383*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.61180997*sx+tx, 0.97860914*sy+ty,0.7818987*sx+tx, 0.95003617*sy+ty,0.81655407*sx+tx, 0.31343445*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.moveTo( 0.28125006*sx+tx, -0.15625*sy+ty);
				ctx.lineTo( 0.47916672*sx+tx, -0.31770834*sy+ty);
				ctx.lineTo( 0.6718749*sx+tx, -0.15625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ü') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.3808231;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09618796*sx+tx, 0.49279627*sy+ty,0.14499383*sx+tx, 0.7342918*sy+ty,0.2195095*sx+tx, 0.8543242*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.47091913*sx+tx, 0.9743397*sy+ty,0.48361656*sx+tx, 0.9744977*sy+ty,0.49634185*sx+tx, 0.97411525*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.5758026*sx+tx, 0.97751796*sy+ty,0.70300347*sx+tx, 0.96161455*sy+ty,0.771762*sx+tx, 0.6685173*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.moveTo( 0.32291666*sx+tx, -0.265625*sy+ty);
				ctx.lineTo( 0.32291666*sx+tx, -0.15104167*sy+ty);
				ctx.moveTo( 0.609375*sx+tx, -0.27083334*sy+ty);
				ctx.lineTo( 0.609375*sx+tx, -0.15104167*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ß') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7864583134651184;
		glyph.pixels = 2.819488;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020833*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.1261574*sx+tx, 0.2899313*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020833*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.125*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.125*sx+tx, 0.088541664*sy+ty,0.11458337*sx+tx, -0.104166664*sy+ty,0.33333334*sx+tx, -0.104166664*sy+ty);
				ctx.bezierCurveTo( 0.47245643*sx+tx, -0.104166664*sy+ty,0.5104583*sx+tx, -0.038859222*sy+ty,0.51433086*sx+tx, 0.02476379*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020833*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.125*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.125*sx+tx, 0.088541664*sy+ty,0.11458337*sx+tx, -0.104166664*sy+ty,0.33333334*sx+tx, -0.104166664*sy+ty);
				ctx.bezierCurveTo( 0.5520833*sx+tx, -0.104166664*sy+ty,0.5208334*sx+tx, 0.057291657*sy+ty,0.5*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.47916663*sx+tx, 0.18229167*sy+ty,0.33333334*sx+tx, 0.36458334*sy+ty,0.375*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.40498638*sx+tx, 0.43623638*sy+ty,0.48892376*sx+tx, 0.506686*sy+ty,0.5666301*sx+tx, 0.5904198*sy+ty);
			} else {
				ctx.moveTo( 0.13020833*sx+tx, 0.9947917*sy+ty);
				ctx.lineTo( 0.125*sx+tx, 0.088541664*sy+ty);
				ctx.bezierCurveTo( 0.125*sx+tx, 0.088541664*sy+ty,0.11458337*sx+tx, -0.104166664*sy+ty,0.33333334*sx+tx, -0.104166664*sy+ty);
				ctx.bezierCurveTo( 0.5520833*sx+tx, -0.104166664*sy+ty,0.5208334*sx+tx, 0.057291657*sy+ty,0.5*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.47916663*sx+tx, 0.18229167*sy+ty,0.33333334*sx+tx, 0.36458334*sy+ty,0.375*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.41666666*sx+tx, 0.44791666*sy+ty,0.56250006*sx+tx, 0.5677083*sy+ty,0.6510417*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.7395833*sx+tx, 0.8177083*sy+ty,0.6666667*sx+tx, 0.921875*sy+ty,0.6197917*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.53125*sx+tx, 1.015625*sy+ty,0.3125*sx+tx, 0.9322917*sy+ty,0.3125*sx+tx, 0.9322917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='à') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.3275135;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.09873002*sx+tx, 0.53782827*sy+ty,0.09849669*sx+tx, 0.53926826*sy+ty,0.09825934*sx+tx, 0.5407754*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.18200876*sx+tx, 0.9406422*sy+ty,0.32682982*sx+tx, 0.9581434*sy+ty,0.39268783*sx+tx, 0.9475384*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.56582546*sx+tx, 0.47755447*sy+ty,0.567779*sx+tx, 0.6496622*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.2239583*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.42187506*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='á') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.317266;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.09880902*sx+tx, 0.53735423*sy+ty,0.09865756*sx+tx, 0.5382801*sy+ty,0.09850424*sx+tx, 0.5392352*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.18095344*sx+tx, 0.939411*sy+ty,0.32096767*sx+tx, 0.95741254*sy+ty,0.38856584*sx+tx, 0.9481514*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5656897*sx+tx, 0.4712418*sy+ty,0.56765646*sx+tx, 0.63903713*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.5052084*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.32291675*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='â') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.5745206;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.09682568*sx+tx, 0.5492543*sy+ty,0.094256386*sx+tx, 0.5681631*sy+ty,0.09205501*sx+tx, 0.59077114*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.43846232*sx+tx, 0.9351304*sy+ty,0.46475235*sx+tx, 0.923058*sy+ty,0.49044484*sx+tx, 0.90864015*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.56995374*sx+tx, 0.6695192*sy+ty,0.56909823*sx+tx, 0.9024394*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.17187497*sx+tx, 0.078125*sy+ty);
				ctx.lineTo( 0.37500006*sx+tx, -0.104166664*sy+ty);
				ctx.lineTo( 0.5729165*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ã') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.575245;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.0968201*sx+tx, 0.5492878*sy+ty,0.09424293*sx+tx, 0.56826216*sy+ty,0.09203772*sx+tx, 0.59094876*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.43854624*sx+tx, 0.9351184*sy+ty,0.4650187*sx+tx, 0.92293555*sy+ty,0.49083486*sx+tx, 0.9084211*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5699683*sx+tx, 0.67019445*sy+ty,0.5690957*sx+tx, 0.90311944*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.16666663*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.16666663*sx+tx, 0.078125*sy+ty,0.22916663*sx+tx, -0.041666668*sy+ty,0.2760417*sx+tx, -0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.32291675*sx+tx, -0.041666668*sy+ty,0.42708343*sx+tx, 0.046875*sy+ty,0.4843751*sx+tx, 0.046875*sy+ty);
				ctx.bezierCurveTo( 0.5416665*sx+tx, 0.046875*sy+ty,0.5781248*sx+tx, -0.078125*sy+ty,0.5781248*sx+tx, -0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ä') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.2668924;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18548393*sx+tx, 0.32208905*sy+ty,0.11272042*sx+tx, 0.4671599*sy+ty,0.09976442*sx+tx, 0.5320674*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.17576587*sx+tx, 0.9333588*sy+ty,0.2932738*sx+tx, 0.9533014*sy+ty,0.36573693*sx+tx, 0.950122*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5650635*sx+tx, 0.4421231*sy+ty,0.56701124*sx+tx, 0.5876973*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.2239583*sx+tx, -0.036458332*sy+ty);
				ctx.lineTo( 0.2239583*sx+tx, 0.078125*sy+ty);
				ctx.moveTo( 0.5104167*sx+tx, -0.036458332*sy+ty);
				ctx.lineTo( 0.5104167*sx+tx, 0.083333336*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='å') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.671875;
		glyph.pixels = 2.8741899;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.09451534*sx+tx, 0.5631163*sy+ty,0.088177264*sx+tx, 0.6163053*sy+ty,0.08721882*sx+tx, 0.6742011*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5625983*sx+tx, 0.32749394*sy+ty,0.56277126*sx+tx, 0.33594993*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.38541675*sx+tx, -0.21354167*sy+ty);
				ctx.bezierCurveTo( 0.34416765*sx+tx, -0.21648803*sy+ty,0.299585*sx+tx, -0.19276619*sy+ty,0.26958376*sx+tx, -0.15934817*sy+ty);
			} else {
				ctx.moveTo( 0.5624999*sx+tx, 0.33854166*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.33854166*sy+ty,0.46354172*sx+tx, 0.25000003*sy+ty,0.32291672*sx+tx, 0.28645834*sy+ty);
				ctx.bezierCurveTo( 0.18229164*sx+tx, 0.32291666*sy+ty,0.10937501*sx+tx, 0.4739583*sy+ty,0.09895834*sx+tx, 0.5364583*sy+ty);
				ctx.bezierCurveTo( 0.08854168*sx+tx, 0.5989583*sy+ty,0.06770832*sx+tx, 0.8072916*sy+ty,0.13020833*sx+tx, 0.8802083*sy+ty);
				ctx.bezierCurveTo( 0.19270831*sx+tx, 0.953125*sy+ty,0.39062506*sx+tx, 0.9635417*sy+ty,0.42187506*sx+tx, 0.9375*sy+ty);
				ctx.bezierCurveTo( 0.45833337*sx+tx, 0.9322917*sy+ty,0.54166657*sx+tx, 0.8802083*sy+ty,0.5624999*sx+tx, 0.859375*sy+ty);
				ctx.moveTo( 0.5624999*sx+tx, 0.32291666*sy+ty);
				ctx.bezierCurveTo( 0.5624999*sx+tx, 0.32291666*sy+ty,0.5729166*sx+tx, 0.8072916*sy+ty,0.56770825*sx+tx, 1.015625*sy+ty);
				ctx.moveTo( 0.38541675*sx+tx, -0.21354167*sy+ty);
				ctx.bezierCurveTo( 0.3125001*sx+tx, -0.21875*sy+ty,0.22916663*sx+tx, -0.140625*sy+ty,0.23437496*sx+tx, -0.072916664*sy+ty);
				ctx.bezierCurveTo( 0.23437496*sx+tx, -0.010416667*sy+ty,0.30729175*sx+tx, 0.0625*sy+ty,0.3750001*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.43229175*sx+tx, 0.052083332*sy+ty,0.5104166*sx+tx, -0.010416672*sy+ty,0.5052084*sx+tx, -0.078125*sy+ty);
				ctx.bezierCurveTo( 0.50000006*sx+tx, -0.14583333*sy+ty,0.44270843*sx+tx, -0.21354166*sy+ty,0.3906251*sx+tx, -0.20833333*sy+ty);
				ctx.lineTo( 0.33854175*sx+tx, -0.203125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='æ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.1614583730697632;
		glyph.pixels = 4.062255;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.078125*sx+tx, 0.31770828*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.31770828*sy+ty,0.25520828*sx+tx, 0.24999996*sy+ty,0.33854166*sx+tx, 0.24999996*sy+ty);
				ctx.bezierCurveTo( 0.421875*sx+tx, 0.24999996*sy+ty,0.5364583*sx+tx, 0.28645825*sy+ty,0.5364583*sx+tx, 0.5625001*sy+ty);
				ctx.lineTo( 0.5364583*sx+tx, 0.8385418*sy+ty);
				ctx.bezierCurveTo( 0.5364583*sx+tx, 0.8385418*sy+ty,0.51376593*sx+tx, 0.85389256*sy+ty,0.4800882*sx+tx, 0.8736037*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.078125*sx+tx, 0.31770828*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.31770828*sy+ty,0.25520828*sx+tx, 0.24999996*sy+ty,0.33854166*sx+tx, 0.24999996*sy+ty);
				ctx.bezierCurveTo( 0.421875*sx+tx, 0.24999996*sy+ty,0.5364583*sx+tx, 0.28645825*sy+ty,0.5364583*sx+tx, 0.5625001*sy+ty);
				ctx.lineTo( 0.5364583*sx+tx, 0.8385418*sy+ty);
				ctx.bezierCurveTo( 0.5364583*sx+tx, 0.8385418*sy+ty,0.359375*sx+tx, 0.95833343*sy+ty,0.26041666*sx+tx, 0.95833343*sy+ty);
				ctx.bezierCurveTo( 0.16145834*sx+tx, 0.95833343*sy+ty,0.083333336*sx+tx, 0.86979175*sy+ty,0.083333336*sx+tx, 0.78645843*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.7031251*sy+ty,0.171875*sx+tx, 0.5781251*sy+ty,0.50000006*sx+tx, 0.5781251*sy+ty);
				ctx.bezierCurveTo( 0.56823194*sx+tx, 0.5781251*sy+ty,0.63060826*sx+tx, 0.57857555*sy+ty,0.6866139*sx+tx, 0.5792891*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.078125*sx+tx, 0.31770828*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.31770828*sy+ty,0.25520828*sx+tx, 0.24999996*sy+ty,0.33854166*sx+tx, 0.24999996*sy+ty);
				ctx.bezierCurveTo( 0.421875*sx+tx, 0.24999996*sy+ty,0.5364583*sx+tx, 0.28645825*sy+ty,0.5364583*sx+tx, 0.5625001*sy+ty);
				ctx.lineTo( 0.5364583*sx+tx, 0.8385418*sy+ty);
				ctx.bezierCurveTo( 0.5364583*sx+tx, 0.8385418*sy+ty,0.359375*sx+tx, 0.95833343*sy+ty,0.26041666*sx+tx, 0.95833343*sy+ty);
				ctx.bezierCurveTo( 0.16145834*sx+tx, 0.95833343*sy+ty,0.083333336*sx+tx, 0.86979175*sy+ty,0.083333336*sx+tx, 0.78645843*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.7031251*sy+ty,0.171875*sx+tx, 0.5781251*sy+ty,0.50000006*sx+tx, 0.5781251*sy+ty);
				ctx.bezierCurveTo( 0.828125*sx+tx, 0.5781251*sy+ty,1.0208334*sx+tx, 0.5885418*sy+ty,1.0208334*sx+tx, 0.5885418*sy+ty);
				ctx.bezierCurveTo( 1.0208334*sx+tx, 0.5885418*sy+ty,1.0364584*sx+tx, 0.24999993*sy+ty,0.8072917*sx+tx, 0.2604166*sy+ty);
				ctx.bezierCurveTo( 0.6967665*sx+tx, 0.2654405*sy+ty,0.6347008*sx+tx, 0.29954004*sy+ty,0.60006005*sx+tx, 0.34693944*sy+ty);
			} else {
				ctx.moveTo( 0.078125*sx+tx, 0.31770828*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.31770828*sy+ty,0.25520828*sx+tx, 0.24999996*sy+ty,0.33854166*sx+tx, 0.24999996*sy+ty);
				ctx.bezierCurveTo( 0.421875*sx+tx, 0.24999996*sy+ty,0.5364583*sx+tx, 0.28645825*sy+ty,0.5364583*sx+tx, 0.5625001*sy+ty);
				ctx.lineTo( 0.5364583*sx+tx, 0.8385418*sy+ty);
				ctx.bezierCurveTo( 0.5364583*sx+tx, 0.8385418*sy+ty,0.359375*sx+tx, 0.95833343*sy+ty,0.26041666*sx+tx, 0.95833343*sy+ty);
				ctx.bezierCurveTo( 0.16145834*sx+tx, 0.95833343*sy+ty,0.083333336*sx+tx, 0.86979175*sy+ty,0.083333336*sx+tx, 0.78645843*sy+ty);
				ctx.bezierCurveTo( 0.083333336*sx+tx, 0.7031251*sy+ty,0.171875*sx+tx, 0.5781251*sy+ty,0.50000006*sx+tx, 0.5781251*sy+ty);
				ctx.bezierCurveTo( 0.828125*sx+tx, 0.5781251*sy+ty,1.0208334*sx+tx, 0.5885418*sy+ty,1.0208334*sx+tx, 0.5885418*sy+ty);
				ctx.bezierCurveTo( 1.0208334*sx+tx, 0.5885418*sy+ty,1.0364584*sx+tx, 0.24999993*sy+ty,0.8072917*sx+tx, 0.2604166*sy+ty);
				ctx.bezierCurveTo( 0.578125*sx+tx, 0.27083328*sy+ty,0.5572917*sx+tx, 0.40625*sy+ty,0.5572917*sx+tx, 0.5260418*sy+ty);
				ctx.bezierCurveTo( 0.5572917*sx+tx, 0.64583343*sy+ty,0.5260416*sx+tx, 0.9635418*sy+ty,0.8125*sx+tx, 0.9687501*sy+ty);
				ctx.bezierCurveTo( 1.015625*sx+tx, 0.9635418*sy+ty,1.0260416*sx+tx, 0.9427083*sy+ty,1.078125*sx+tx, 0.92708343*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ç') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7395833134651184;
		glyph.pixels = 1.9586103;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.33301932*sx+tx, 0.29924908*sy+ty,0.24957338*sx+tx, 0.33343118*sy+ty,0.18847312*sx+tx, 0.40966558*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27604166*sx+tx, 0.296875*sy+ty,0.09895834*sx+tx, 0.41666666*sy+ty,0.10416668*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.10416668*sx+tx, 0.7721724*sy+ty,0.15848182*sx+tx, 0.86133564*sy+ty,0.23514836*sx+tx, 0.9163957*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27604166*sx+tx, 0.296875*sy+ty,0.09895834*sx+tx, 0.41666666*sy+ty,0.10416668*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.10416668*sx+tx, 0.8229167*sy+ty,0.24999993*sx+tx, 0.9791667*sy+ty,0.4010418*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.4947918*sx+tx, 0.96875*sy+ty,0.5729164*sx+tx, 0.8802083*sy+ty,0.661458*sx+tx, 0.8229167*sy+ty);
				ctx.moveTo( 0.390625*sx+tx, 0.984375*sy+ty);
				ctx.lineTo( 0.37626693*sx+tx, 1.0188344*sy+ty);
			} else {
				ctx.moveTo( 0.598958*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.598958*sx+tx, 0.40625*sy+ty,0.5208334*sx+tx, 0.30208334*sy+ty,0.4010418*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27604166*sx+tx, 0.296875*sy+ty,0.09895834*sx+tx, 0.41666666*sy+ty,0.10416668*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.10416668*sx+tx, 0.8229167*sy+ty,0.24999993*sx+tx, 0.9791667*sy+ty,0.4010418*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.4947918*sx+tx, 0.96875*sy+ty,0.5729164*sx+tx, 0.8802083*sy+ty,0.661458*sx+tx, 0.8229167*sy+ty);
				ctx.moveTo( 0.390625*sx+tx, 0.984375*sy+ty);
				ctx.lineTo( 0.33854166*sx+tx, 1.109375*sy+ty);
				ctx.bezierCurveTo( 0.33854166*sx+tx, 1.109375*sy+ty,0.48958334*sx+tx, 1.1354167*sy+ty,0.47395834*sx+tx, 1.2083334*sy+ty);
				ctx.bezierCurveTo( 0.45833334*sx+tx, 1.28125*sy+ty,0.28125*sx+tx, 1.2864584*sy+ty,0.28125*sx+tx, 1.2864584*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='è') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7083333134651184;
		glyph.pixels = 2.3890004;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.6177938*sx+tx, 0.44072518*sy+ty,0.6150607*sx+tx, 0.4335337*sy+ty,0.61164606*sx+tx, 0.4264057*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.18671748*sx+tx, 0.34469447*sy+ty,0.12398406*sx+tx, 0.417911*sy+ty,0.09368583*sx+tx, 0.5000791*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.12938458*sx+tx, 0.8496847*sy+ty,0.23939492*sx+tx, 0.93713605*sy+ty,0.3602984*sx+tx, 0.95408005*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.92708343*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty);
				ctx.moveTo( 0.22916663*sx+tx, -0.14583333*sy+ty);
				ctx.lineTo( 0.41666675*sx+tx, 0.083333336*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='é') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7083333134651184;
		glyph.pixels = 2.385587;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.6178535*sx+tx, 0.44094008*sy+ty,0.6152234*sx+tx, 0.43396354*sy+ty,0.6119504*sx+tx, 0.42704496*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.1873888*sx+tx, 0.34444273*sy+ty,0.124830194*sx+tx, 0.41692704*sy+ty,0.094302006*sx+tx, 0.49842122*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.12918358*sx+tx, 0.84901476*sy+ty,0.2376466*sx+tx, 0.9357422*sy+ty,0.3574095*sx+tx, 0.9536615*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.92708343*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty);
				ctx.moveTo( 0.50793654*sx+tx, -0.13756613*sy+ty);
				ctx.lineTo( 0.3174604*sx+tx, 0.08465608*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ê') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7083333134651184;
		glyph.pixels = 2.6527443;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.6131809*sx+tx, 0.42411843*sy+ty,0.59851545*sx+tx, 0.4003202*sy+ty,0.57775474*sx+tx, 0.3788223*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.13484712*sx+tx, 0.36414587*sy+ty,0.068743676*sx+tx, 0.5037496*sy+ty,0.072089456*sx+tx, 0.6317587*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.5293757*sx+tx, 0.9390804*sy+ty,0.5790218*sx+tx, 0.9098867*sy+ty,0.6074761*sx+tx, 0.8859693*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.92708343*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty);
				ctx.moveTo( 0.1770833*sx+tx, 0.083333336*sy+ty);
				ctx.lineTo( 0.37500006*sx+tx, -0.119791664*sy+ty);
				ctx.lineTo( 0.5677082*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ë') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7083333134651184;
		glyph.pixels = 2.3220696;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.6189645*sx+tx, 0.4449395*sy+ty,0.6180114*sx+tx, 0.44196233*sy+ty,0.61693615*sx+tx, 0.4389897*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.19988072*sx+tx, 0.33975825*sy+ty,0.14118703*sx+tx, 0.39920938*sy+ty,0.107386895*sx+tx, 0.46807635*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.1254431*sx+tx, 0.8365465*sy+ty,0.20707776*sx+tx, 0.9098822*sy+ty,0.3046884*sx+tx, 0.94125015*sy+ty);
			} else {
				ctx.moveTo( 0.11458336*sx+tx, 0.73958343*sy+ty);
				ctx.bezierCurveTo( 0.11458336*sx+tx, 0.73958343*sy+ty,0.55729145*sx+tx, 0.49479172*sy+ty,0.61979145*sx+tx, 0.44791663*sy+ty);
				ctx.bezierCurveTo( 0.5937498*sx+tx, 0.35416663*sy+ty,0.44270834*sx+tx, 0.26041666*sy+ty,0.28645837*sx+tx, 0.30729163*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.38541666*sy+ty,0.03125*sx+tx, 0.61979175*sy+ty,0.10416668*sx+tx, 0.76562506*sy+ty);
				ctx.bezierCurveTo( 0.13541667*sx+tx, 0.86979175*sy+ty,0.29687512*sx+tx, 0.9791667*sy+ty,0.44791678*sx+tx, 0.95312506*sy+ty);
				ctx.bezierCurveTo( 0.59895813*sx+tx, 0.92708343*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty,0.6406248*sx+tx, 0.84895843*sy+ty);
				ctx.moveTo( 0.24479163*sx+tx, -0.03125*sy+ty);
				ctx.lineTo( 0.24479163*sx+tx, 0.083333336*sy+ty);
				ctx.moveTo( 0.5312499*sx+tx, -0.036458332*sy+ty);
				ctx.lineTo( 0.5312499*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ì') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2447916716337204;
		glyph.pixels = 1.7033968;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12818086*sx+tx, 0.5207734*sy+ty,0.12665588*sx+tx, 0.7209274*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0217855*sy+ty,0.12528908*sx+tx, 0.99703795*sy+ty,0.125731*sx+tx, 0.9434296*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0897398*sy+ty,0.12857628*sx+tx, 0.5445386*sy+ty,0.12980194*sx+tx, 0.3548966*sy+ty);
			} else {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.010416667*sx+tx, -0.14583333*sy+ty);
				ctx.lineTo( 0.1875*sx+tx, 0.083333336*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='í') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2447916716337204;
		glyph.pixels = 1.6887066;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12821569*sx+tx, 0.51683885*sy+ty,0.12669544*sx+tx, 0.71574277*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0206134*sy+ty,0.12526453*sx+tx, 0.9990895*sy+ty,0.12567432*sx+tx, 0.95024747*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0879817*sy+ty,0.12844506*sx+tx, 0.56450963*sy+ty,0.12973148*sx+tx, 0.36579055*sy+ty);
			} else {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( 0.27604166*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.088541664*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='î') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2447916716337204;
		glyph.pixels = 1.9353272;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12759116*sx+tx, 0.58741003*sy+ty,0.1260673*sx+tx, 0.8004232*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0402905*sy+ty,0.12582125*sx+tx, 0.93879217*sy+ty,0.12681149*sx+tx, 0.7998621*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.0625*sx+tx, 0.072916664*sy+ty);
				ctx.lineTo( -0.024194218*sx+tx, 0.039399106*sy+ty);
			} else {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.0625*sx+tx, 0.072916664*sy+ty);
				ctx.lineTo( 0.14583333*sx+tx, -0.109375*sy+ty);
				ctx.lineTo( 0.32291666*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ï') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.2447916716337204;
		glyph.pixels = 1.6297625;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12835237*sx+tx, 0.501394*sy+ty,0.12685625*sx+tx, 0.6948293*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0159105*sy+ty,0.12517695*sx+tx, 1.0053593*sy+ty,0.12546559*sx+tx, 0.9740949*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.0809273*sy+ty,0.12794323*sx+tx, 0.6402291*sy+ty,0.12940466*sx+tx, 0.41609395*sy+ty);
			} else {
				ctx.moveTo( 0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.13020836*sx+tx, 0.29166666*sy+ty,0.12500003*sx+tx, 0.8802083*sy+ty,0.12500003*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.12500003*sx+tx, 1.109375*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty,0.13020836*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.0052083335*sx+tx, -0.041666668*sy+ty);
				ctx.lineTo( -0.0052083335*sx+tx, 0.078125*sy+ty);
				ctx.moveTo( 0.28645834*sx+tx, -0.036458332*sy+ty);
				ctx.lineTo( 0.28645834*sx+tx, 0.072916664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ñ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7552083134651184;
		glyph.pixels = 2.6190152;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 0.9255872*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.17708328*sx+tx, 0.5104167*sy+ty,0.28645837*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.3421232*sx+tx, 0.35367766*sy+ty,0.39411572*sx+tx, 0.32497582*sy+ty,0.4489773*sx+tx, 0.32014444*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.17708328*sx+tx, 0.5104167*sy+ty,0.28645837*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.38020843*sx+tx, 0.31770834*sy+ty,0.46354195*sx+tx, 0.296875*sy+ty,0.5677083*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.6431699*sx+tx, 0.37728846*sy+ty,0.6342692*sx+tx, 0.7820204*sy+ty,0.62792486*sx+tx, 0.93865174*sy+ty);
			} else {
				ctx.moveTo( 0.09895834*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.09895834*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7864583*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.7864583*sy+ty,0.17708328*sx+tx, 0.5104167*sy+ty,0.28645837*sx+tx, 0.40625*sy+ty);
				ctx.bezierCurveTo( 0.38020843*sx+tx, 0.31770834*sy+ty,0.46354195*sx+tx, 0.296875*sy+ty,0.5677083*sx+tx, 0.34375*sy+ty);
				ctx.bezierCurveTo( 0.6614583*sx+tx, 0.38541666*sy+ty,0.6249998*sx+tx, 1.0*sy+ty,0.6249998*sx+tx, 1.0*sy+ty);
				ctx.moveTo( 0.203125*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.203125*sx+tx, 0.078125*sy+ty,0.24479166*sx+tx, -0.046875*sy+ty,0.3125*sx+tx, -0.046875*sy+ty);
				ctx.bezierCurveTo( 0.38020834*sx+tx, -0.046875*sy+ty,0.4270833*sx+tx, 0.036458332*sy+ty,0.5052083*sx+tx, 0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.5833333*sx+tx, 0.036458332*sy+ty,0.59375*sx+tx, -0.078125*sy+ty,0.59375*sx+tx, -0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ò') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.1249282;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.6803483*sy+ty,0.0902403*sx+tx, 0.69892365*sy+ty,0.09343013*sx+tx, 0.71702546*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.3927083*sx+tx, 0.9791667*sy+ty,0.43669644*sx+tx, 0.96579766*sy+ty,0.47895572*sx+tx, 0.9415023*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.57780176*sy+ty,0.6383142*sx+tx, 0.50040597*sy+ty,0.60140306*sx+tx, 0.4354799*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.23958333*sx+tx, -0.14583333*sy+ty);
				ctx.lineTo( 0.42708334*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ó') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.1136107;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.67922884*sy+ty,0.090044945*sx+tx, 0.696721*sy+ty,0.0928788*sx+tx, 0.7138024*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.39119953*sx+tx, 0.9791667*sy+ty,0.43326086*sx+tx, 0.9668239*sy+ty,0.47398534*sx+tx, 0.9443051*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.58037835*sy+ty,0.63975525*sx+tx, 0.5067299*sy+ty,0.60584885*sx+tx, 0.4435294*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.5052083*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.32291666*sx+tx, 0.072916664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ô') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.3783526;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.7054144*sy+ty,0.097739294*sx+tx, 0.74766725*sy+ty,0.11352093*sx+tx, 0.7862131*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.42649284*sx+tx, 0.9791667*sy+ty,0.5179994*sx+tx, 0.9320872*sy+ty,0.58126765*sx+tx, 0.85407066*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5201063*sy+ty,0.5902438*sx+tx, 0.34595782*sy+ty,0.45088908*sx+tx, 0.30909827*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.19791664*sx+tx, 0.083333336*sy+ty);
				ctx.lineTo( 0.40104172*sx+tx, -0.104166664*sy+ty);
				ctx.lineTo( 0.59895825*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='õ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.388775;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.7064453*sy+ty,0.09817576*sx+tx, 0.74964815*sy+ty,0.1146421*sx+tx, 0.7889189*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.42788225*sx+tx, 0.9791667*sy+ty,0.5215222*sx+tx, 0.93026096*sy+ty,0.58488226*sx+tx, 0.84954035*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.51773345*sy+ty,0.58761907*sx+tx, 0.3390795*sy+ty,0.4425726*sx+tx, 0.30708227*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.21354163*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.21354163*sx+tx, 0.078125*sy+ty,0.23958333*sx+tx, -0.046875*sy+ty,0.30729175*sx+tx, -0.046875*sy+ty);
				ctx.bezierCurveTo( 0.37500006*sx+tx, -0.046875*sy+ty,0.4687501*sx+tx, 0.057291668*sy+ty,0.52083325*sx+tx, 0.057291668*sy+ty);
				ctx.bezierCurveTo( 0.5729165*sx+tx, 0.057291668*sy+ty,0.6197915*sx+tx, 0.015624998*sy+ty,0.6197915*sx+tx, -0.067708336*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ö') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.04657;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.6725979*sy+ty,0.089132376*sx+tx, 0.683628*sy+ty,0.09027126*sx+tx, 0.6945162*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.3822622*sx+tx, 0.9791667*sy+ty,0.41325194*sx+tx, 0.9720625*sy+ty,0.44435638*sx+tx, 0.9588004*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5956411*sy+ty,0.64705396*sx+tx, 0.54318553*sy+ty,0.6282805*sx+tx, 0.4931863*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.265625*sx+tx, -0.036458332*sy+ty);
				ctx.lineTo( 0.265625*sx+tx, 0.072916664*sy+ty);
				ctx.moveTo( 0.5468749*sx+tx, -0.036458332*sy+ty);
				ctx.bezierCurveTo( 0.5468749*sx+tx, -0.036458332*sy+ty,0.5468751*sx+tx, 0.04166666*sy+ty,0.546875*sx+tx, 0.072916664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='÷') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 1.171875;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.088541664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.3815104*sx+tx, 0.5729167*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.088541664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.6744792*sx+tx, 0.5729167*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.088541664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.9270833*sx+tx, 0.5729167*sy+ty);
				ctx.moveTo( 0.5052083*sx+tx, 0.15625*sy+ty);
				ctx.lineTo( 0.5052083*sx+tx, 0.19661462*sy+ty);
			} else {
				ctx.moveTo( 0.088541664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.9270833*sx+tx, 0.5729167*sy+ty);
				ctx.moveTo( 0.5052083*sx+tx, 0.15625*sy+ty);
				ctx.lineTo( 0.5052083*sx+tx, 0.32291666*sy+ty);
				ctx.moveTo( 0.5052083*sx+tx, 0.828125*sy+ty);
				ctx.lineTo( 0.5052083*sx+tx, 0.9947917*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ø') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.7447916865348816;
		glyph.pixels = 2.8333316;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.7504163*sy+ty,0.12621263*sx+tx, 0.8323982*sy+ty,0.17989069*sx+tx, 0.89079493*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.6315395*sy+ty,0.65587467*sx+tx, 0.6221493*sy+ty,0.65512127*sx+tx, 0.612536*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.6875*sx+tx, 0.22395833*sy+ty);
				ctx.lineTo( 0.5049921*sx+tx, 0.45209318*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.6875*sx+tx, 0.22395833*sy+ty);
				ctx.lineTo( 0.0625*sx+tx, 1.0052084*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ù') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 2.0715084;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.11641431*sx+tx, 0.7400286*sy+ty,0.12403818*sx+tx, 0.7847152*sy+ty,0.13768126*sx+tx, 0.82518816*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.41492638*sx+tx, 0.9635417*sy+ty,0.5626181*sx+tx, 0.8666612*sy+ty,0.585493*sx+tx, 0.8510657*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.595427*sx+tx, 0.7616692*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.23958333*sx+tx, -0.140625*sy+ty);
				ctx.lineTo( 0.421875*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ú') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 2.0755289;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.116430536*sx+tx, 0.74040204*sy+ty,0.124173835*sx+tx, 0.78550786*sy+ty,0.1380456*sx+tx, 0.8262638*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.41583055*sx+tx, 0.9635417*sy+ty,0.56454146*sx+tx, 0.86539567*sy+ty,0.5859239*sx+tx, 0.8507715*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5954053*sx+tx, 0.76468444*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.5104167*sx+tx, -0.14583333*sy+ty);
				ctx.lineTo( 0.328125*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='û') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 2.3211021;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.11742217*sx+tx, 0.7632096*sy+ty,0.13418677*sx+tx, 0.83469176*sy+ty,0.16572057*sx+tx, 0.88706213*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5982548*sx+tx, 0.36859912*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.59408027*sx+tx, 0.9488597*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.17708333*sx+tx, 0.072916664*sy+ty);
				ctx.lineTo( 0.375*sx+tx, -0.109375*sy+ty);
				ctx.lineTo( 0.5677083*sx+tx, 0.072916664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ü') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 2.0003014;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.11612677*sx+tx, 0.73341525*sy+ty,0.121786505*sx+tx, 0.7707433*sy+ty,0.1316981*sx+tx, 0.8058348*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.3989124*sx+tx, 0.9635417*sy+ty,0.5286762*sx+tx, 0.8877146*sy+ty,0.5728068*sx+tx, 0.8594293*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5958111*sx+tx, 0.7082653*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.26041666*sx+tx, -0.03125*sy+ty);
				ctx.lineTo( 0.26041666*sx+tx, 0.072916664*sy+ty);
				ctx.moveTo( 0.5468749*sx+tx, -0.03125*sy+ty);
				ctx.lineTo( 0.5468749*sx+tx, 0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ĩ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.6145833134651184;
		glyph.pixels = 2.3870547;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30003253*sx+tx, 0.607172*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.2820411*sx+tx, 0.044098347*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.36019343*sx+tx, 0.96595675*sy+ty,0.46780714*sx+tx, 0.9648993*sy+ty);
			} else {
				ctx.moveTo( 0.2968751*sx+tx, 0.010416667*sy+ty);
				ctx.lineTo( 0.30208343*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.072916664*sx+tx, 0.041666668*sy+ty);
				ctx.lineTo( 0.5208333*sx+tx, 0.046875*sy+ty);
				ctx.moveTo( 0.078125*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.078125*sx+tx, 0.9583333*sy+ty,0.46354163*sx+tx, 0.96874994*sy+ty,0.5052083*sx+tx, 0.9635416*sy+ty);
				ctx.moveTo( 0.098958336*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.098958336*sx+tx, -0.15104167*sy+ty,0.12500001*sx+tx, -0.28125*sy+ty,0.19270828*sx+tx, -0.28125*sy+ty);
				ctx.bezierCurveTo( 0.26041657*sx+tx, -0.28125*sy+ty,0.32812506*sx+tx, -0.19270833*sy+ty,0.40104195*sx+tx, -0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.47395864*sx+tx, -0.19270833*sy+ty,0.48958364*sx+tx, -0.30729166*sy+ty,0.48958364*sx+tx, -0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ĩ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.3489583432674408;
		glyph.pixels = 1.9471283;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.17187499*sx+tx, 0.29166666*sy+ty,0.16922578*sx+tx, 0.59102774*sy+ty,0.16770616*sx+tx, 0.80431056*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.17187499*sx+tx, 0.29166666*sy+ty,0.16666666*sx+tx, 0.8802083*sy+ty,0.16666666*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.16666666*sx+tx, 1.0412321*sy+ty,0.1675222*sx+tx, 0.93452823*sy+ty,0.1685398*sx+tx, 0.7911897*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.17187499*sx+tx, 0.29166666*sy+ty,0.16666666*sx+tx, 0.8802083*sy+ty,0.16666666*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.16666666*sx+tx, 1.109375*sy+ty,0.17187499*sx+tx, 0.29166666*sy+ty,0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.031250007*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( -0.031250007*sx+tx, 0.078125*sy+ty,-0.02552125*sx+tx, 0.06157526*sy+ty,-0.015176358*sx+tx, 0.04049209*sy+ty);
			} else {
				ctx.moveTo( 0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.bezierCurveTo( 0.17187499*sx+tx, 0.29166666*sy+ty,0.16666666*sx+tx, 0.8802083*sy+ty,0.16666666*sx+tx, 0.9947917*sy+ty);
				ctx.bezierCurveTo( 0.16666666*sx+tx, 1.109375*sy+ty,0.17187499*sx+tx, 0.29166666*sy+ty,0.17187499*sx+tx, 0.29166666*sy+ty);
				ctx.closePath();
				ctx.moveTo( -0.031250007*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( -0.031250007*sx+tx, 0.078125*sy+ty,0.015625004*sx+tx, -0.057291668*sy+ty,0.083333336*sx+tx, -0.046875*sy+ty);
				ctx.bezierCurveTo( 0.15104164*sx+tx, -0.036458332*sy+ty,0.20312497*sx+tx, 0.052083332*sy+ty,0.27083334*sx+tx, 0.052083332*sy+ty);
				ctx.bezierCurveTo( 0.33854172*sx+tx, 0.052083332*sy+ty,0.3645834*sx+tx, -0.078125*sy+ty,0.3645834*sx+tx, -0.078125*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Œ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.3958333730697632;
		glyph.pixels = 4.776674;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.047530722*sx+tx, 0.81362826*sy+ty,0.30006605*sx+tx, 0.9543694*sy+ty,0.4069965*sx+tx, 0.9532579*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072628*sx+tx, 0.21965465*sy+ty,0.67780006*sx+tx, 0.05818322*sy+ty,0.5169598*sx+tx, 0.04179923*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 1.3177084*sx+tx, 0.072916664*sy+ty);
				ctx.lineTo( 0.8020833*sx+tx, 0.078125*sy+ty);
				ctx.lineTo( 0.8137079*sx+tx, 0.7329759*sy+ty);
			} else {
				ctx.moveTo( 0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.43229178*sx+tx, 0.041666668*sy+ty,0.140625*sx+tx, 0.11979169*sy+ty,0.09375001*sx+tx, 0.5260417*sy+ty);
				ctx.bezierCurveTo( 0.046874996*sx+tx, 0.8177083*sy+ty,0.30729178*sx+tx, 0.9583333*sy+ty,0.41145843*sx+tx, 0.953125*sy+ty);
				ctx.bezierCurveTo( 0.5052084*sx+tx, 0.9635417*sy+ty,0.78645813*sx+tx, 0.875*sy+ty,0.7968748*sx+tx, 0.546875*sy+ty);
				ctx.bezierCurveTo( 0.8072915*sx+tx, 0.21875*sy+ty,0.67708313*sx+tx, 0.057291668*sy+ty,0.515625*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.49479172*sx+tx, 0.046875*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty,0.49479172*sx+tx, 0.041666668*sy+ty);
				ctx.closePath();
				ctx.moveTo( 1.3177084*sx+tx, 0.072916664*sy+ty);
				ctx.lineTo( 0.8020833*sx+tx, 0.078125*sy+ty);
				ctx.lineTo( 0.8177083*sx+tx, 0.9583333*sy+ty);
				ctx.lineTo( 1.3489584*sx+tx, 0.953125*sy+ty);
				ctx.moveTo( 0.8072917*sx+tx, 0.46354166*sy+ty);
				ctx.lineTo( 1.2447916*sx+tx, 0.46354166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='œ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.2552083730697632;
		glyph.pixels = 3.843083;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.3655944*sx+tx, 0.9791667*sy+ty,0.37750086*sx+tx, 0.97799134*sy+ty,0.3896991*sx+tx, 0.97570443*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.6822917*sx+tx, 0.6041667*sy+ty);
				ctx.lineTo( 0.7709897*sx+tx, 0.6041667*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.6822917*sx+tx, 0.6041667*sy+ty);
				ctx.lineTo( 1.0885416*sx+tx, 0.6041667*sy+ty);
				ctx.bezierCurveTo( 1.0885416*sx+tx, 0.6041667*sy+ty,1.1197916*sx+tx, 0.27083334*sy+ty,0.9010417*sx+tx, 0.27083334*sy+ty);
				ctx.bezierCurveTo( 0.77870023*sx+tx, 0.26769638*sy+ty,0.7130402*sx+tx, 0.34013462*sy+ty,0.67902625*sx+tx, 0.41418022*sy+ty);
			} else {
				ctx.moveTo( 0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.30208334*sy+ty,0.08854167*sx+tx, 0.4895833*sy+ty,0.08854167*sx+tx, 0.6614583*sy+ty);
				ctx.bezierCurveTo( 0.08854167*sx+tx, 0.8333333*sy+ty,0.22916661*sx+tx, 0.9791667*sy+ty,0.35416678*sx+tx, 0.9791667*sy+ty);
				ctx.bezierCurveTo( 0.47916678*sx+tx, 0.9791667*sy+ty,0.6614583*sx+tx, 0.8385417*sy+ty,0.65624976*sx+tx, 0.640625*sy+ty);
				ctx.bezierCurveTo( 0.65624976*sx+tx, 0.5052083*sy+ty,0.57291645*sx+tx, 0.30208334*sy+ty,0.39583343*sx+tx, 0.30208334*sy+ty);
				ctx.moveTo( 0.6822917*sx+tx, 0.6041667*sy+ty);
				ctx.lineTo( 1.0885416*sx+tx, 0.6041667*sy+ty);
				ctx.bezierCurveTo( 1.0885416*sx+tx, 0.6041667*sy+ty,1.1197916*sx+tx, 0.27083334*sy+ty,0.9010417*sx+tx, 0.27083334*sy+ty);
				ctx.bezierCurveTo( 0.6979167*sx+tx, 0.265625*sy+ty,0.6510416*sx+tx, 0.46875006*sy+ty,0.6458333*sx+tx, 0.5416667*sy+ty);
				ctx.bezierCurveTo( 0.6458333*sx+tx, 0.71875*sy+ty,0.671875*sx+tx, 0.9479167*sy+ty,0.875*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 1.0729166*sx+tx, 0.96875*sy+ty,1.171875*sx+tx, 0.9270833*sy+ty,1.171875*sx+tx, 0.9270833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='Ũ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9114583134651184;
		glyph.pixels = 2.672975;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.095848*sx+tx, 0.5526284*sy+ty,0.15774843*sx+tx, 0.78985417*sy+ty,0.24805464*sx+tx, 0.8932553*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.49665973*sx+tx, 0.9751197*sy+ty,0.53602195*sx+tx, 0.97420925*sy+ty,0.5740524*sx+tx, 0.9622757*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6144135*sx+tx, 0.978688*sy+ty,0.787674*sx+tx, 0.9490572*sy+ty,0.8182*sx+tx, 0.28050998*sy+ty);
			} else {
				ctx.moveTo( 0.09895835*sx+tx, 0.0052083335*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.921875*sy+ty,0.27083334*sx+tx, 0.96875*sy+ty,0.4583334*sx+tx, 0.9739583*sy+ty);
				ctx.bezierCurveTo( 0.6302082*sx+tx, 0.9791667*sy+ty,0.8229165*sx+tx, 0.9427083*sy+ty,0.8229165*sx+tx, 0.057291668*sy+ty);
				ctx.lineTo( 0.8229165*sx+tx, 0.0052083335*sy+ty);
				ctx.moveTo( 0.234375*sx+tx, -0.15104167*sy+ty);
				ctx.bezierCurveTo( 0.234375*sx+tx, -0.15104167*sy+ty,0.27604166*sx+tx, -0.27604166*sy+ty,0.33854166*sx+tx, -0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.40104166*sx+tx, -0.27604166*sy+ty,0.4739583*sx+tx, -0.19270833*sy+ty,0.53125*sx+tx, -0.19270833*sy+ty);
				ctx.bezierCurveTo( 0.5885417*sx+tx, -0.19270833*sy+ty,0.6354167*sx+tx, -0.30729166*sy+ty,0.6354167*sx+tx, -0.30729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='ũ') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.703125;
		glyph.pixels = 2.3192358;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.11741463*sx+tx, 0.7630363*sy+ty,0.13409787*sx+tx, 0.8343123*sy+ty,0.16546975*sx+tx, 0.88664466*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5982615*sx+tx, 0.36766598*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.59409034*sx+tx, 0.94745994*sy+ty);
			} else {
				ctx.moveTo( 0.140625*sx+tx, 0.27604166*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 0.27604166*sy+ty,0.104166664*sx+tx, 0.5833333*sy+ty,0.11458336*sx+tx, 0.6979167*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.8177083*sy+ty,0.17187496*sx+tx, 0.9583333*sy+ty,0.2760417*sx+tx, 0.9635417*sy+ty);
				ctx.bezierCurveTo( 0.42708343*sx+tx, 0.9635417*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty,0.58854145*sx+tx, 0.8489583*sy+ty);
				ctx.moveTo( 0.59895813*sx+tx, 0.27083334*sy+ty);
				ctx.lineTo( 0.5937498*sx+tx, 0.9947917*sy+ty);
				ctx.moveTo( 0.19791664*sx+tx, 0.072916664*sy+ty);
				ctx.bezierCurveTo( 0.19791664*sx+tx, 0.072916664*sy+ty,0.21354161*sx+tx, -0.046875*sy+ty,0.30208337*sx+tx, -0.046875*sy+ty);
				ctx.bezierCurveTo( 0.39062506*sx+tx, -0.046875*sy+ty,0.4270834*sx+tx, 0.041666668*sy+ty,0.50000006*sx+tx, 0.041666668*sy+ty);
				ctx.bezierCurveTo( 0.57291657*sx+tx, 0.041666668*sy+ty,0.59895825*sx+tx, -0.072916664*sy+ty,0.59895825*sx+tx, -0.072916664*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='‚') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.3333333432674408;
		glyph.pixels = 0.5192932;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.13020833*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.059683625*sx+tx, 0.9083003*sy+ty,0.16943139*sx+tx, 0.88682836*sy+ty,0.17670764*sx+tx, 0.8854847*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.13020833*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.057291668*sx+tx, 0.90625*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty);
				ctx.lineTo( 0.22916667*sx+tx, 0.921875*sy+ty);
				ctx.bezierCurveTo( 0.22916667*sx+tx, 0.921875*sy+ty,0.2286539*sx+tx, 0.93725824*sy+ty,0.22666298*sx+tx, 0.9596582*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.13020833*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.057291668*sx+tx, 0.90625*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty);
				ctx.lineTo( 0.22916667*sx+tx, 0.921875*sy+ty);
				ctx.bezierCurveTo( 0.22916667*sx+tx, 0.921875*sy+ty,0.22424147*sx+tx, 1.069631*sy+ty,0.18565424*sx+tx, 1.1160911*sy+ty);
			} else {
				ctx.moveTo( 0.13020833*sx+tx, 0.96875*sy+ty);
				ctx.bezierCurveTo( 0.057291668*sx+tx, 0.90625*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty,0.17708333*sx+tx, 0.8854167*sy+ty);
				ctx.lineTo( 0.22916667*sx+tx, 0.921875*sy+ty);
				ctx.bezierCurveTo( 0.22916667*sx+tx, 0.921875*sy+ty,0.22395834*sx+tx, 1.0781249*sy+ty,0.18229167*sx+tx, 1.1197916*sy+ty);
				ctx.bezierCurveTo( 0.140625*sx+tx, 1.1614584*sy+ty,0.078125*sx+tx, 1.1875*sy+ty,0.078125*sx+tx, 1.1875*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='‹') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.4270833432674408;
		glyph.pixels = 0.7282412;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.34375*sx+tx, 0.39583334*sy+ty);
				ctx.lineTo( 0.20534204*sx+tx, 0.51410925*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.34375*sx+tx, 0.39583334*sy+ty);
				ctx.lineTo( 0.06693408*sx+tx, 0.63238513*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.34375*sx+tx, 0.39583334*sy+ty);
				ctx.lineTo( 0.057291668*sx+tx, 0.640625*sy+ty);
				ctx.lineTo( 0.18782112*sx+tx, 0.7485628*sy+ty);
			} else {
				ctx.moveTo( 0.34375*sx+tx, 0.39583334*sy+ty);
				ctx.lineTo( 0.057291668*sx+tx, 0.640625*sy+ty);
				ctx.lineTo( 0.328125*sx+tx, 0.8645833*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='›') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.40625;
		glyph.pixels = 0.74484664;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.041666668*sx+tx, 0.38020834*sy+ty);
				ctx.lineTo( 0.17956764*sx+tx, 0.5053407*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.041666668*sx+tx, 0.38020834*sy+ty);
				ctx.lineTo( 0.31746858*sx+tx, 0.6304731*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.041666668*sx+tx, 0.38020834*sy+ty);
				ctx.lineTo( 0.32291666*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.19022888*sx+tx, 0.755346*sy+ty);
			} else {
				ctx.moveTo( 0.041666668*sx+tx, 0.38020834*sy+ty);
				ctx.lineTo( 0.32291666*sx+tx, 0.6354167*sy+ty);
				ctx.lineTo( 0.052083332*sx+tx, 0.8802083*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='€') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 2.784873;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.7864583*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.7864583*sx+tx, 0.119791664*sy+ty,0.5677084*sx+tx, -0.026041672*sy+ty,0.41145834*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.3351306*sx+tx, 0.12901016*sy+ty,0.25507426*sx+tx, 0.22215267*sy+ty,0.20407468*sx+tx, 0.33083856*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.7864583*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.7864583*sx+tx, 0.119791664*sy+ty,0.5677084*sx+tx, -0.026041672*sy+ty,0.41145834*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.2552083*sx+tx, 0.18229167*sy+ty,0.08333331*sx+tx, 0.46354163*sy+ty,0.17708333*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.25117913*sx+tx, 0.8738314*sy+ty,0.36431628*sx+tx, 0.92481667*sy+ty,0.4573531*sx+tx, 0.9459479*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.7864583*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.7864583*sx+tx, 0.119791664*sy+ty,0.5677084*sx+tx, -0.026041672*sy+ty,0.41145834*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.2552083*sx+tx, 0.18229167*sy+ty,0.08333331*sx+tx, 0.46354163*sy+ty,0.17708333*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.921875*sy+ty,0.42708337*sx+tx, 0.9427083*sy+ty,0.5260417*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.625*sx+tx, 0.9739583*sy+ty,0.7552083*sx+tx, 0.9114583*sy+ty,0.7552083*sx+tx, 0.9114583*sy+ty);
				ctx.moveTo( 0.020833334*sx+tx, 0.38541666*sy+ty);
				ctx.lineTo( 0.38712323*sx+tx, 0.38897288*sy+ty);
			} else {
				ctx.moveTo( 0.7864583*sx+tx, 0.119791664*sy+ty);
				ctx.bezierCurveTo( 0.7864583*sx+tx, 0.119791664*sy+ty,0.5677084*sx+tx, -0.026041672*sy+ty,0.41145834*sx+tx, 0.078125*sy+ty);
				ctx.bezierCurveTo( 0.2552083*sx+tx, 0.18229167*sy+ty,0.08333331*sx+tx, 0.46354163*sy+ty,0.17708333*sx+tx, 0.6927083*sy+ty);
				ctx.bezierCurveTo( 0.27083334*sx+tx, 0.921875*sy+ty,0.42708337*sx+tx, 0.9427083*sy+ty,0.5260417*sx+tx, 0.9583333*sy+ty);
				ctx.bezierCurveTo( 0.625*sx+tx, 0.9739583*sy+ty,0.7552083*sx+tx, 0.9114583*sy+ty,0.7552083*sx+tx, 0.9114583*sy+ty);
				ctx.moveTo( 0.020833334*sx+tx, 0.38541666*sy+ty);
				ctx.lineTo( 0.5572917*sx+tx, 0.390625*sy+ty);
				ctx.moveTo( 0.03125*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.5572917*sx+tx, 0.5729166*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='∑') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.9583333134651184;
		glyph.pixels = 3.0854635;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.8749997*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.13541669*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.1555428*sx+tx, 0.024598604*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.8749997*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.13541669*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.6041664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.56160283*sx+tx, 0.6192359*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.8749997*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.13541669*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.6041664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.072916664*sx+tx, 1.1510416*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 1.1510416*sy+ty,0.07560524*sx+tx, 1.1510603*sy+ty,0.080679454*sx+tx, 1.1510943*sy+ty);
			} else {
				ctx.moveTo( 0.8749997*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.13541669*sx+tx, 0.0*sy+ty);
				ctx.lineTo( 0.6041664*sx+tx, 0.5729167*sy+ty);
				ctx.lineTo( 0.072916664*sx+tx, 1.1510416*sy+ty);
				ctx.bezierCurveTo( 0.072916664*sx+tx, 1.1510416*sy+ty,0.82291645*sx+tx, 1.15625*sy+ty,0.9114581*sx+tx, 1.1510417*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='≈') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 1.9852417;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.119791664*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.5208333*sy+ty,0.14583334*sx+tx, 0.31249997*sy+ty,0.27083334*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.33600608*sx+tx, 0.3758909*sy+ty,0.44082198*sx+tx, 0.41318926*sy+ty,0.5387753*sx+tx, 0.4439161*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.119791664*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.5208333*sy+ty,0.14583334*sx+tx, 0.31249997*sy+ty,0.27083334*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.39583334*sx+tx, 0.39583334*sy+ty,0.6666666*sx+tx, 0.49479166*sy+ty,0.7552083*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.8958333*sx+tx, 0.49479166*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty);
				ctx.moveTo( 0.109375*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.109375*sx+tx, 0.8385417*sy+ty,0.109434195*sx+tx, 0.8381717*sy+ty,0.10955789*sx+tx, 0.8374638*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.119791664*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.5208333*sy+ty,0.14583334*sx+tx, 0.31249997*sy+ty,0.27083334*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.39583334*sx+tx, 0.39583334*sy+ty,0.6666666*sx+tx, 0.49479166*sy+ty,0.7552083*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.8958333*sx+tx, 0.49479166*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty);
				ctx.moveTo( 0.109375*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.109375*sx+tx, 0.8385417*sy+ty,0.15104169*sx+tx, 0.578125*sy+ty,0.33333334*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.4171545*sx+tx, 0.69217336*sy+ty,0.4844574*sx+tx, 0.7302991*sy+ty,0.5443566*sx+tx, 0.76100653*sy+ty);
			} else {
				ctx.moveTo( 0.119791664*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.119791664*sx+tx, 0.5208333*sy+ty,0.14583334*sx+tx, 0.31249997*sy+ty,0.27083334*sx+tx, 0.35416666*sy+ty);
				ctx.bezierCurveTo( 0.39583334*sx+tx, 0.39583334*sy+ty,0.6666666*sx+tx, 0.49479166*sy+ty,0.7552083*sx+tx, 0.49479166*sy+ty);
				ctx.bezierCurveTo( 0.8958333*sx+tx, 0.49479166*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty,0.9010417*sx+tx, 0.32291666*sy+ty);
				ctx.moveTo( 0.109375*sx+tx, 0.8385417*sy+ty);
				ctx.bezierCurveTo( 0.109375*sx+tx, 0.8385417*sy+ty,0.15104169*sx+tx, 0.578125*sy+ty,0.33333334*sx+tx, 0.65625*sy+ty);
				ctx.bezierCurveTo( 0.515625*sx+tx, 0.734375*sy+ty,0.6197916*sx+tx, 0.8229167*sy+ty,0.7395833*sx+tx, 0.8229167*sy+ty);
				ctx.bezierCurveTo( 0.890625*sx+tx, 0.8333333*sy+ty,0.890625*sx+tx, 0.640625*sy+ty,0.890625*sx+tx, 0.640625*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='≠') {
		glyph.bounds = new Object();
		glyph.unitWidth = 0.640625;
		glyph.pixels = 1.5290055;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.39122266*sx+tx, 0.5173336*sy+ty,0.5156928*sx+tx, 0.5160718*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.3919325*sx+tx, 0.7447917*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.57291645*sx+tx, 0.7447917*sy+ty);
				ctx.moveTo( 0.453125*sx+tx, 0.359375*sy+ty);
				ctx.bezierCurveTo( 0.453125*sx+tx, 0.359375*sy+ty,0.4283396*sx+tx, 0.41824034*sy+ty,0.39522558*sx+tx, 0.49771428*sy+ty);
			} else {
				ctx.moveTo( 0.09375001*sx+tx, 0.5208333*sy+ty);
				ctx.bezierCurveTo( 0.09375001*sx+tx, 0.5208333*sy+ty,0.5364582*sx+tx, 0.515625*sy+ty,0.56770813*sx+tx, 0.515625*sy+ty);
				ctx.moveTo( 0.09375001*sx+tx, 0.7447917*sy+ty);
				ctx.lineTo( 0.57291645*sx+tx, 0.7447917*sy+ty);
				ctx.moveTo( 0.453125*sx+tx, 0.359375*sy+ty);
				ctx.bezierCurveTo( 0.453125*sx+tx, 0.359375*sy+ty,0.2447917*sx+tx, 0.8541667*sy+ty,0.22916667*sx+tx, 0.9114583*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='≤') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 2.3210077;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.921875*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.3834064*sx+tx, 0.42974502*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.921875*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.234375*sx+tx, 0.48958334*sy+ty);
				ctx.lineTo( 0.62070286*sx+tx, 0.65348005*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.921875*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.234375*sx+tx, 0.48958334*sy+ty);
				ctx.lineTo( 0.921875*sx+tx, 0.78125*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.34163448*sx+tx, 0.95470685*sy+ty);
			} else {
				ctx.moveTo( 0.921875*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.234375*sx+tx, 0.48958334*sy+ty);
				ctx.lineTo( 0.921875*sx+tx, 0.78125*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.953125*sy+ty);
				ctx.lineTo( 0.921875*sx+tx, 0.9583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	if( letter=='≥') {
		glyph.bounds = new Object();
		glyph.unitWidth = 1.0;
		glyph.pixels = 2.2881343;
		glyph.paint = function(ctx, destinationBounds, percentComplete) {
			var tx = destinationBounds.x;
			var ty = destinationBounds.y;
			var sx = destinationBounds.width/glyph.unitWidth;
			var sy = destinationBounds.height
			ctx.beginPath();
			if(percentComplete<25) {
				ctx.moveTo( 0.088541664*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.6190924*sx+tx, 0.42740706*sy+ty);
			} else if(percentComplete<50) {
				ctx.moveTo( 0.088541664*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.7604167*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.3743799*sx+tx, 0.6489643*sy+ty);
			} else if(percentComplete<75) {
				ctx.moveTo( 0.088541664*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.7604167*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.088541664*sx+tx, 0.7708333*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.9583333*sy+ty);
				ctx.lineTo( 0.34984127*sx+tx, 0.9583333*sy+ty);
			} else {
				ctx.moveTo( 0.088541664*sx+tx, 0.21354167*sy+ty);
				ctx.lineTo( 0.7604167*sx+tx, 0.484375*sy+ty);
				ctx.lineTo( 0.088541664*sx+tx, 0.7708333*sy+ty);
				ctx.moveTo( 0.088541664*sx+tx, 0.9583333*sy+ty);
				ctx.lineTo( 0.921875*sx+tx, 0.9583333*sy+ty);
			}
			ctx.stroke();
		}
		return glyph;
	}
	return null;
}
