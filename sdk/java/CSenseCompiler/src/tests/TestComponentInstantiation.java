package tests;

public class TestComponentInstantiation {

    public static void main(String[] args) {
	TestComponent<Double> d = TestComponent.newComponent(Double.class);
	// TestComponent<Float> f = newComponent(Double.class);
    }
}
