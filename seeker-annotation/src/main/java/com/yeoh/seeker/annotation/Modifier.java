package com.yeoh.seeker.annotation;

/**
 * Represents a modifier on a method element .
 *
 * @author JingYeoh
 * @since 2018-09-13
 */
public enum Modifier {

    /** The modifier {@code public} */ PUBLIC,
    /** The modifier {@code protected} */ PROTECTED,
    /** The modifier {@code private} */ PRIVATE,
    /** The modifier with the default value */ DEFAULT;

    /**
     * Returns this modifier's name in lowercase.
     */
    public String toString() {
        return name().toLowerCase(java.util.Locale.US);
    }
}
