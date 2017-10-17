package ph.ryetech.playpround.reflect;

/**
 * 
 */

import java.util.*;

/**
 * @author royce remulla
 */
public class Retry {

	public Map<String, Object> retryOnFailure(Object component, String methodName, int maxRetryCount,
			LinkedHashMap<Class<?>, Object> arguments) {
		final Map<String, Object> result = new HashMap<>();
		int attemptCount = 0;
		while (++attemptCount <= maxRetryCount) {
			try {
				Object returnValue = component.getClass().getDeclaredMethod(methodName, new Class<?>[0]).invoke(component, new Object[0]);						
				result.put("returnValue", returnValue);
				System.out.println("Success: " + returnValue);
				break;
			} catch (Exception e) {
				System.out.print("Failed on attempt: " + attemptCount);
				result.put("errorDetail", e.getMessage());
				
				if (attemptCount < maxRetryCount) {
					try {
						System.out.println(" Sleeping for 1 second...");
						Thread.sleep(1000);
					} catch (InterruptedException e1) {}
				} else {
					System.out.println();
				}
			}
		}

		return result;
	}

	public static int MAX_RETRY = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FailingObject failingObject = new FailingObject("Fail after 3 retries.", MAX_RETRY + 1);		
		System.out.println("\nCase: " + failingObject.getName());
		new Retry().retryOnFailure(failingObject, "method", MAX_RETRY, new LinkedHashMap<Class<?>, Object>());

		FailingObject successObject1 = new FailingObject("Success on first try.", 1);		
		System.out.println("\nCase: " + successObject1.getName());
		new Retry().retryOnFailure(successObject1, "method", MAX_RETRY, new LinkedHashMap<Class<?>, Object>());

		FailingObject successObject2 = new FailingObject("Success on second try.", 2);		
		System.out.println("\nCase: " + successObject2.getName());
		new Retry().retryOnFailure(successObject2, "method", MAX_RETRY, new LinkedHashMap<Class<?>, Object>());

		FailingObject successObject3 = new FailingObject("Success on third try.", 3);		
		System.out.println("\nCase: " + successObject3.getName());
		new Retry().retryOnFailure(successObject3, "method", MAX_RETRY, new LinkedHashMap<Class<?>, Object>());
	}

}

class FailingObject {

	private final int greenOnCount;
	private final String name;
	private int callCount = 0;

	FailingObject(String name, int greenOnCount) {
		this.greenOnCount = greenOnCount;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Object method() {
		this.callCount++;
		if (this.callCount < this.greenOnCount) {
			throw new RuntimeException("Dummy fail");
		} else {
			return "success";
		}

	}
}
