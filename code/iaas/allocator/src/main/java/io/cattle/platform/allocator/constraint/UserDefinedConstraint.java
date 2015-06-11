package io.cattle.platform.allocator.constraint;

/**
 * Since this is a user defined constraint, the constraint must
 * supply a user-friendly failure message to provide the necessary
 * information for the user to correct the behavior
 *
 * @author sonchang
 */
public interface UserDefinedConstraint {
    String getFailureMessage();
}
