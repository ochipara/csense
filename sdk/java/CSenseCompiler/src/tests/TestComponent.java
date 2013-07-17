package tests;

public class TestComponent<T> {
    public TestComponent() {
    }

    public static <T> TestComponent<T> newComponent(Class<T> cls) {
	return new TestComponent<T>();
    }
}
