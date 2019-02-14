package edu.smith.cs.csc212.p2;



//this is the code that was posted on JJ's lecture of 2/13/19


public class FallingRock extends Rock {

	public FallingRock(World world) {

		super(world);
}


@Override
	public void step() {
		this.moveDown();
	
	}
}
