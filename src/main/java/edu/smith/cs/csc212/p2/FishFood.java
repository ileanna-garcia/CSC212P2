package edu.smith.cs.csc212.p2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

//code posted on piazza
public class FishFood extends WorldObject {

	public FishFood(World world) {
		super(world);
	}

	@Override
	public void draw(Graphics2D g) {
		// Challenge 2: I don't care what it looks like.
		g.setColor(Color.orange);
		g.fill(new Ellipse2D.Double(-0.4, -0.4, 0.8, 0.8));
	}

	@Override
	public void step() {
		// Fish Food doesn't move.
	}
}